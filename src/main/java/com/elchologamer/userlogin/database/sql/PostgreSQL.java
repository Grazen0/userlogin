package com.elchologamer.userlogin.database.sql;

import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PostgreSQL extends SQLDatabase {

    public PostgreSQL() {
        super("postgresql", "org.postgresql.Driver");
    }

    @Override
    public void connect() throws SQLException, ClassNotFoundException {
        super.connect();

        // Update password max length
        int maxChars = Math.max(
                getPlugin().getConfig().getInt("password.maxCharacters", 128),
                1
        );

        update(
                "ALTER TABLE " + getTable() + " " +
                        "ALTER COLUMN password " +
                        "TYPE VARCHAR(" + maxChars + ")"
        );
    }

    @Override
    protected Connection getConnection() throws SQLException {
        ConfigurationSection section = getPlugin().getConfig()
                .getConfigurationSection("database.postgresql");
        assert section != null;

        String host = section.getString("host", "localhost");
        int port = section.getInt("port", 5432);
        boolean ssl = section.getBoolean("ssl");

        String username = section.getString("username", "root");
        String password = section.getString("password", null);

        Properties props = new Properties();

        props.setProperty("user", username);
        if (password != null) props.setProperty("password", password);
        props.setProperty("ssl", Boolean.toString(ssl));

        String url = "jdbc:postgresql://" + host + ":" + port + "/" + getDatabase();
        return DriverManager.getConnection(url, props);
    }
}
