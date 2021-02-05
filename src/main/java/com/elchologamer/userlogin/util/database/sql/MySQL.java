package com.elchologamer.userlogin.util.database.sql;

import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL extends SQLDatabase {

    public MySQL() {
        super("com.jdbc.mysql.Driver");
    }

    @Override
    protected Connection getConnection() throws SQLException {
        ConfigurationSection section = getPlugin().getConfig().getConfigurationSection("database.mysql");
        assert section != null;

        database = section.getString("database", "userlogin_data");
        table = section.getString("table", "player_data");
        String host = section.getString("host", "localhost");
        int port = section.getInt("port", 3306);
        boolean useSSL = section.getBoolean("useSSL", false);

        String username = section.getString("username", "root");
        String password = section.getString("password", "");


        // Create connection
        Connection con = DriverManager.getConnection(
                "jdbc:mysql://" + host + ":" + port + "/?useSSL=" + useSSL,
                username,
                password
        );

        // Connect to database
        update("CREATE DATABASE IF NOT EXISTS `" + escapeTable(database) + "`");
        query("USE `" + escapeTable(database) + "`");

        return con;
    }
}
