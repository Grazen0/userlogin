package com.elchologamer.userlogin.util.database.sql;

import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MySQL extends SQLDatabase {

    public MySQL() {
        super("mysql", "com.mysql.jdbc.Driver");
    }

    @Override
    public void connect() throws SQLException, ClassNotFoundException {
        super.connect();

        // Remove username column if exists
        if (query("SHOW COLUMNS FROM " + getTable() + " LIKE 'username'").next()) {
            update("ALTER TABLE " + getTable() + " DROP COLUMN username");
        }

        // Update password max length
        int maxChars = Math.max(
                getPlugin().getConfig().getInt("password.maxCharacters", 128),
                1
        );
        update("ALTER TABLE " + getTable() + " MODIFY password VARCHAR(" + maxChars + ")");
    }

    @Override
    protected Connection getConnection() throws SQLException {
        ConfigurationSection section = getPlugin().getConfig()
                .getConfigurationSection("database.mysql");
        assert section != null;

        String host = section.getString("host", "localhost");
        int port = section.getInt("port", 3306);
        boolean ssl = section.getBoolean("ssl", section.getBoolean("useSSL"));

        String username = section.getString("username", "root");
        String password = section.getString("password", null);

        Properties props = new Properties();

        props.setProperty("user", username);
        if (password != null) props.setProperty("password", password);

        props.setProperty("useSSL", Boolean.toString(ssl));
        props.setProperty("autoReconnect", "true");

        String url = "jdbc:mysql://" + host + ":" + port + "/" + getDatabase();
        return DriverManager.getConnection(url, props);
    }
}
