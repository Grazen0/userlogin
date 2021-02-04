package com.elchologamer.userlogin.util.database.sql;

import com.elchologamer.userlogin.util.Utils;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MariaDB extends SQLDatabase {

    public MariaDB() {
        super("org.mariadb.jdbc.Driver");
    }

    @Override
    protected Connection getConnection() throws SQLException {
        ConfigurationSection section = Utils.getConfig().getConfigurationSection("database.mariadb");
        assert section != null;

        database = section.getString("database", "userlogin_data");
        table = section.getString("table", "player_data");
        String host = section.getString("host", "localhost");
        int port = section.getInt("port", 3306);

        String username = section.getString("username", "root");
        String password = section.getString("password", "");

        // Create connection
        return DriverManager.getConnection(
                "jdbc:mariadb://" + host + ":" + port + "/" + database,
                username,
                password
        );
    }
}
