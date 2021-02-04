package com.elchologamer.userlogin.util.database.sql;

import com.elchologamer.userlogin.util.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLite extends SQLDatabase {

    public SQLite() {
        super("org.sqlite.JDBC");
    }

    @Override
    protected Connection getConnection() throws SQLException {
        String url = Utils.getConfig().getString(
                "database.sqlite.database",
                "C:/sqlite/db/userlogin.db"
        );
        return DriverManager.getConnection("jdbc:sqlite:" + url);
    }
}
