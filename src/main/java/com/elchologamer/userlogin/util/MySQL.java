package com.elchologamer.userlogin.util;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.util.lists.Path;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MySQL {

    public final Map<UUID, String> data = new HashMap<>();
    public boolean isConnected = false;
    private @Nullable String database = "userlogin_data";
    private @Nullable String table = "player_data";
    private Connection connection;

    public void connect() throws SQLException, ClassNotFoundException {
        // Get credentials from config
        String host = Utils.getConfig().getString("mysql.host");
        String username = Utils.getConfig().getString("mysql.username");
        String password = Utils.getConfig().getString("mysql.password");
        int port = Utils.getConfig().getInt("mysql.port");

        // Get database and table from config
        database = Utils.getConfig().getString("mysql.database");
        table = Utils.getConfig().getString("mysql.table");

        if (database == null) database = "userlogin_data";
        if (table == null) table = "player_data";

        synchronized (this) {
            if (getConnection() != null && !getConnection().isClosed())
                return;

            // Check driver and set connection
            isConnected = false;
            Class.forName("com.mysql.jdbc.Driver");

            boolean useSSL = UserLogin.getPlugin().getConfig().getBoolean("mysql.useSSL", false);
            setConnection(DriverManager.getConnection(
                    String.format("jdbc:mysql://%s:%d?useSSL=%b", host, port, useSSL),
                    username, password));

            // Create database schema
            if (!query("SHOW DATABASES LIKE '" + database + "'").next())
                update("CREATE SCHEMA " + database);

            // Connect to database
            setConnection(DriverManager.getConnection(
                    String.format("jdbc:mysql://%s:%d/%s?useSSL=%b", host, port, database, useSSL),
                    username, password));

            // Create table if it does not exist
            if (!query("SHOW TABLES LIKE '" + table + "'").next())
                update("CREATE TABLE `" + database + "`.`" + table + "` " +
                        "( `UUID` VARCHAR(45) NOT NULL , `USERNAME` VARCHAR(45) NOT NULL , " +
                        "`PASSWORD` VARCHAR(45) NOT NULL , PRIMARY KEY (`UUID`)) ENGINE = InnoDB;");

            // Map the current data queried from the database
            data.clear();
            ResultSet rows = query("SELECT * FROM `" + database + "`.`" + table + "`");

            while (rows.next()) {
                data.put(
                        UUID.fromString(rows.getString("UUID")),
                        rows.getString("PASSWORD")
                );
            }

            boolean encrypt = Utils.getConfig().getBoolean("password.encrypt");
            for (UUID uuid : data.keySet()) {
                String dataPassword = data.get(uuid);
                if (dataPassword == null) continue;
                if (encrypt && !dataPassword.startsWith("§"))
                    dataPassword = Utils.encrypt(dataPassword);
                else {
                    if (dataPassword.startsWith("§"))
                        dataPassword = Utils.decrypt(password);
                }

                data.put(uuid, dataPassword);
            }
            saveData();

            this.isConnected = true;
        }
    }

    public synchronized void saveData() {
        try {
            for (UUID uuid : data.keySet()) {
                // Get password and the associated player's username
                String password = data.get(uuid);
                String username = UserLogin.getPlugin().getServer().getOfflinePlayer(uuid).getName();

                // Checks if row with the UUID exists
                if (query("SELECT * FROM " + table + " WHERE UUID='" + uuid.toString() + "'").next()) {
                    // Update an existing row
                    update("UPDATE " + database + "." + table + " SET USERNAME='" + username + "' " +
                            "WHERE UUID='" + uuid.toString() + "'");

                    update("UPDATE " + database + "." + table + " SET PASSWORD='" + password + "' " +
                            "WHERE UUID='" + uuid.toString() + "'");
                } else
                    update("INSERT INTO " + table + " (UUID, USERNAME, PASSWORD) VALUES " +
                            "('" + uuid.toString() + "', '" + username + "', '" + password + "')");
            }
        } catch (SQLException e) {
            Utils.log(Utils.color(UserLogin.messagesFile.get().getString(Path.SQL_SAVE_ERROR)));
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    private synchronized ResultSet query(String sql) throws SQLException {
        PreparedStatement statement = getConnection().prepareStatement(sql);
        return statement.executeQuery();
    }

    private synchronized void update(String sql) throws SQLException {
        PreparedStatement statement = getConnection().prepareStatement(sql);
        statement.executeUpdate();
    }
}
