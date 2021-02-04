package com.elchologamer.userlogin.util.database.sql;

import com.elchologamer.userlogin.util.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public abstract class SQLDatabase implements Database {

    protected Connection connection;
    protected final String driver;
    protected String table;
    protected String database;

    protected SQLDatabase(String driver) {
        this.driver = driver;
    }

    @Override
    public void connect() throws SQLException, ClassNotFoundException {
        Class.forName(driver);
        connection = getConnection();

        // Create table
        update("CREATE TABLE IF NOT EXISTS `" + escapeTable(table) + "` (" +
                "uuid VARCHAR(45) NOT NULL, " +
                "password VARCHAR(45) NOT NULL, " +
                "PRIMARY KEY (uuid)" +
                ")"
        );
    }

    protected abstract Connection getConnection() throws SQLException;

    @Override
    public String getPassword(UUID uuid) {
        try {
            ResultSet set = query(
                    "SELECT password FROM `" + escapeTable(table) + "` WHERE uuid=?",
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
    public void setPassword(UUID uuid, String password) throws SQLException {
        update(
                "INSERT INTO `" + escapeTable(table) + "` (uuid,password) VALUES (?,?) " +
                        "ON DUPLICATE KEY UPDATE password=VALUES(password)",
                uuid, password
        );
    }

    @Override
    public void deletePassword(UUID uuid) throws SQLException {
        update(
                "DELETE FROM `" + escapeTable(table) + "` WHERE uuid=?",
                table, uuid
        );
    }

    @Override
    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    protected PreparedStatement prepareSQL(String sql, Object... params) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sql);

        for (int i = 0; i < params.length; i++) {
            statement.setString(i, params[i].toString());
        }

        return statement;
    }

    protected String escapeTable(String table) {
        return table.replace("`", "\\`");
    }

    protected ResultSet query(String sql, Object... params) throws SQLException {
        return prepareSQL(sql, params).executeQuery();
    }

    protected void update(String sql, Object... params) throws SQLException {
        prepareSQL(sql, params).executeUpdate();
    }
}
