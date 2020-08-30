package com.elchologamer.userlogin.util.data;

import com.elchologamer.userlogin.util.Utils;
import com.elchologamer.userlogin.util.lists.Path;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class MySQL {

    public final Map<UUID, String> data = new HashMap<>();
    private final Utils utils = new Utils();
    public boolean isConnected = false;
    private @Nullable String database = "userlogin_data";
    private @Nullable String table = "player_data";
    private Connection connection;

    public void connect() throws SQLException, ClassNotFoundException {
        // Get credentials from config
        String host = utils.getConfig().getString("mysql.host");
        String username = utils.getConfig().getString("mysql.username");
        String password = utils.getConfig().getString("mysql.password");
        int port = utils.getConfig().getInt("mysql.port");

        // Get database and table from config
        this.database = utils.getConfig().getString("mysql.database");
        this.table = utils.getConfig().getString("mysql.table");

        if (database == null) database = "userlogin_data";
        if (table == null) table = "player_data";

        synchronized (this) {
            if (getConnection() != null && !getConnection().isClosed())
                return;

            // Get driver and set connection
            this.isConnected = false;
            Class.forName("com.mysql.jdbc.Driver");
            setConnection(DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/",
                    username, password));

            // Create database schema
            if (!query("SHOW DATABASES LIKE '" + database + "'").next())
                update("CREATE SCHEMA " + database);

            // Connect to database
            setConnection(DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + database,
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

            boolean encrypt = utils.getConfig().getBoolean("password.encrypt");
            for (UUID uuid : data.keySet()) {
                String dataPassword = data.get(uuid);
                if (dataPassword == null) continue;
                if (encrypt && !Objects.requireNonNull(password).startsWith("§"))
                    dataPassword = utils.encrypt(dataPassword);
                else {
                    assert password != null;
                    if (password.startsWith("§"))
                        dataPassword = utils.decrypt(password);
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
                String username = Bukkit.getServer().getOfflinePlayer(uuid).getName();

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
            utils.consoleLog(utils.color(Objects.requireNonNull(
                    utils.getConfig().getString(Path.SQL_SAVE_ERROR))));

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
