package com.elchologamer.userlogin.database.sql;

import com.elchologamer.userlogin.database.Database;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public abstract class SQLDatabase extends Database {

    private final String name;
    private Connection connection;
    private final String driver;
    private final String table;
    private final String database;

    protected SQLDatabase(String name, String driver) {
        this.name = name;
        this.driver = driver;

        ConfigurationSection section = getPlugin().getConfig()
                .getConfigurationSection("database." + name);
        assert section != null;

        table = section.getString("table", "player_data").replace("`", "");
        database = section.getString("database");
    }

    @Override
    public void connect() throws SQLException, ClassNotFoundException {
        Class.forName(driver);
        connection = getConnection();

        int maxChars = Math.max(
                getPlugin().getConfig().getInt("password.maxCharacters", 128),
                1
        );


        // Create table
        update("CREATE TABLE IF NOT EXISTS " + table + " (" +
                "uuid VARCHAR(45) NOT NULL," +
                "password VARCHAR(" + maxChars + ") NOT NULL," +
                "PRIMARY KEY (uuid)" +
                ")"
        );
    }

    protected abstract Connection getConnection() throws SQLException;

    @Override
    public String getRawPassword(UUID uuid) {
        try {
            ResultSet set = query(
                    "SELECT password FROM " + table + " WHERE uuid=?",
                    uuid
            );

            if (!set.next()) return null;
            return set.getString("password");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void createRawPassword(UUID uuid, String password) throws SQLException {
        update(
                "INSERT INTO " + table + " (uuid,password) VALUES (?,?)",
                uuid, password
        );
    }

    @Override
    public void updateRawPassword(UUID uuid, String password) throws SQLException {
        update(
                "UPDATE " + table + " SET password=? WHERE uuid=?",
                password, uuid
        );
    }

    @Override
    public void deletePassword(UUID uuid) throws SQLException {
        update("DELETE FROM " + table + " WHERE uuid=?", uuid);
    }

    @Override
    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    protected PreparedStatement prepareSQL(String sql, Object... params) throws SQLException {
        if (connection == null)
            throw new SQLException("Tried to query database, but connection isn't available");

        PreparedStatement statement = connection.prepareStatement(sql);

        for (int i = 0; i < params.length; i++) {
            statement.setString(i + 1, params[i].toString());
        }

        return statement;
    }

    protected ResultSet query(String sql, Object... params) throws SQLException {
        return prepareSQL(sql, params).executeQuery();
    }

    protected void update(String sql, Object... params) throws SQLException {
        prepareSQL(sql, params).executeUpdate();
    }

    public String getName() {
        return name;
    }

    public String getTable() {
        return table;
    }

    public String getDatabase() {
        return database;
    }
}
