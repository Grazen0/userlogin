package com.elchologamer.userlogin.database.sql;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLite extends SQLDatabase {

    public SQLite() {
        super("sqlite", "org.sqlite.JDBC");
    }

    @Override
    protected Connection getConnection() throws SQLException {
        String path = getPlugin().getConfig().getString(
                "database.sqlite.database",
                "C:/sqlite/db/userlogin.db"
        );

        File folder = new File(path).getParentFile();
        folder.mkdirs();

        return DriverManager.getConnection("jdbc:sqlite:" + path);
    }
}
