package com.elchologamer.userlogin.util.database.sql;

import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

public class PostgreSQL extends SQLDatabase {

    public PostgreSQL() {
        super("org.postgresql.Driver");
    }

    @Override
    protected Connection getConnection() throws SQLException {
        ConfigurationSection section = getPlugin().getConfig()
                .getConfigurationSection("database.postgresql");
        assert section != null;

        database = section.getString("database", "userlogin_data");
        table = section.getString("table", "player_data");
        String host = section.getString("host", "localhost");
        int port = section.getInt("port", 5432);

        String username = section.getString("username", "root");
        String password = section.getString("password", "");

        return DriverManager.getConnection(
                "jdbc:postgresql://" + host + ":" + port + "/" + database,
                username,
                password
        );
    }

    @Override
    public void setPassword(UUID uuid, String password) throws SQLException {
        update(
                "INSERT INTO `" + escapeTable(table) + "` (uuid,password) VALUES (?,?) " +
                        "ON CONFLICT (uuid) DO UPDATE SET password=VALUES(password)",
                uuid, password
        );
    }
}
