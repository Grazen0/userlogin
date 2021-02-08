package com.elchologamer.userlogin.util.database.sql;

import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MariaDB extends SQLDatabase {

    public MariaDB() {
        super("mariadb", "org.mariadb.jdbc.Driver");
    }

    @Override
    protected Connection getConnection() throws SQLException {
        ConfigurationSection section = getPlugin().getConfig()
                .getConfigurationSection("database.mariadb");
        assert section != null;

        String host = section.getString("host", "localhost");
        int port = section.getInt("port", 3306);
        boolean ssl = section.getBoolean("ssl");

        String username = section.getString("username", "root");
        String password = section.getString("password", null);

        Properties props = new Properties();

        props.setProperty("user", username);
        if (password != null) props.setProperty("password", password);
        props.setProperty("useSSL", Boolean.toString(ssl));

        String url = "jdbc:mariadb://" + host + ":" + port + "/" + getDatabase();
        return DriverManager.getConnection(url, props);
    }
}
