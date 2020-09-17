package com.elchologamer.userlogin.util;

import com.elchologamer.userlogin.UserLogin;
import org.bukkit.ChatColor;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MySQL {

    private final Map<UUID, String> data = new HashMap<>();
    private String database = "userlogin_data";
    private String table = "player_data";
    private Connection connection;

    public void connect() throws SQLException, ClassNotFoundException {
        // Get database and table from config
        database = Utils.getConfig().getString("mysql.database", database);
        table = Utils.getConfig().getString("mysql.table", table);

        if (getConnection() != null && !getConnection().isClosed())
            return;

        // Check driver and set connection
        Class.forName("com.mysql.jdbc.Driver");
        checkTable();

        // Sync YAML data if possible
        data.clear();
        if (!query("SELECT * FROM `" + database + "`.`" + table + "`").next()) {
            for (String key : UserLogin.getPlugin().getPlayerData().get().getKeys(false)) {
                String playerPassword = UserLogin.getPlugin().getPlayerData().get().getString(key + ".password");
                if (playerPassword == null) continue;

                try {
                    data.put(UUID.fromString(key), playerPassword);
                } catch (IllegalArgumentException e) {
                    Utils.log(ChatColor.RED + "Invalid UUID on playerData.yml: " + key);
                }
            }
            saveData();
            return;
        }

        // Map the current data queried from the database
        ResultSet rows = query("SELECT * FROM `" + database + "`.`" + table + "`");

        while (rows.next()) {
            data.put(
                    UUID.fromString(rows.getString("UUID")),
                    rows.getString("PASSWORD")
            );
        }

        // Encrypt/decrypt required passwords
        for (UUID uuid : data.keySet()) {
            String dataPassword = data.get(uuid);

            if (Utils.getConfig().getBoolean("password.encrypt"))
                dataPassword = Utils.encrypt(dataPassword);
            else
                dataPassword = Utils.decrypt(dataPassword);

            data.put(uuid, dataPassword);
        }
        saveData();
    }

    private void checkTable() throws SQLException {
        // Get credentials from config
        String host = Utils.getConfig().getString("mysql.host", "localhost");
        String username = Utils.getConfig().getString("mysql.username", "root");
        String password = Utils.getConfig().getString("mysql.password", "");
        int port = Utils.getConfig().getInt("mysql.port", 3306);

        boolean useSSL = UserLogin.getPlugin().getConfig().getBoolean("mysql.useSSL", false);
        setConnection(DriverManager.getConnection(
                String.format("jdbc:mysql://%s:%d?useSSL=%b", host, port, useSSL),
                username, password));

        // Create database schema if it does not exists
        if (!query("SHOW DATABASES LIKE '" + database + "'").next())
            update("CREATE SCHEMA " + database);

        // Connect to database
        setConnection(DriverManager.getConnection(
                String.format("jdbc:mysql://%s:%d/%s?useSSL=%b", host, port, database, useSSL),
                username, password));

        // Create table if it does not exist
        if (!query("SHOW TABLES LIKE '" + table + "'").next()) {
            update("CREATE TABLE `" + database + "`.`" + table + "` " +
                    "( `UUID` VARCHAR(45) NOT NULL , `USERNAME` VARCHAR(45) NOT NULL , " +
                    "`PASSWORD` VARCHAR(45) NOT NULL , PRIMARY KEY (`UUID`)) ENGINE = InnoDB;");
        }
    }

    public void saveData() {
        try {
            checkTable();
            for (UUID uuid : data.keySet()) {
                // Get password and the player's username
                String password = data.get(uuid);
                String username = UserLogin.getPlugin().getServer().getOfflinePlayer(uuid).getName();

                // Checks if row with the UUID exists
                if (query("SELECT * FROM " + table + " WHERE UUID='" + uuid.toString() + "'").next()) {
                    // Update an existing row
                    update("UPDATE " + database + "." + table + " SET USERNAME='" + username + "' " +
                            "WHERE UUID='" + uuid.toString() + "'");

                    update("UPDATE " + database + "." + table + " SET PASSWORD='" + password + "' " +
                            "WHERE UUID='" + uuid.toString() + "'");
                } else {
                    insertRow(uuid, username, password);
                }
            }
        } catch (SQLException e) {
            Utils.log(UserLogin.getPlugin().getMessage(Path.SQL_SAVE_ERROR));
            e.printStackTrace();
        }
    }

    private void insertRow(UUID uuid, String name, String password) throws SQLException {
        update("INSERT INTO " + table + " (UUID, USERNAME, PASSWORD) VALUES " +
                "('" + uuid.toString() + "', '" + name + "', '" + password + "')");
    }

    public Map<UUID, String> getData() {
        return data;
    }

    public boolean isConnected() {
        try {
            return getConnection() != null && !getConnection().isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
