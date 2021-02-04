package com.elchologamer.userlogin.util.database;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.util.Utils;
import com.elchologamer.userlogin.util.database.sql.MariaDB;
import com.elchologamer.userlogin.util.database.sql.MySQL;
import com.elchologamer.userlogin.util.database.sql.PostgreSQL;
import com.elchologamer.userlogin.util.database.sql.SQLite;

import java.util.UUID;

public interface Database {

    static Database select(UserLogin plugin) {
        String type = plugin.getConfig().getString("database.type", "yaml");

        switch (type.toLowerCase()) {
            case "mysql":
                return new MySQL();
            case "postgresql":
                return new PostgreSQL();
            case "mariadb":
                return new MariaDB();
            case "sqlite":
                return new SQLite();
            case "mongodb":
                return new MongoDB();
            default:
                Utils.log("&eInvalid database type selected, defaulting to \"yaml\"");
            case "yaml":
                return new YamlDB(plugin);
        }
    }

    void connect() throws Exception;

    void disconnect() throws Exception;

    String getPassword(UUID uuid);

    void setPassword(UUID uuid, String password) throws Exception;

    void deletePassword(UUID uuid) throws Exception;
}
