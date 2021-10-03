package com.elchologamer.userlogin.database;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.database.sql.MySQL;
import com.elchologamer.userlogin.database.sql.PostgreSQL;
import com.elchologamer.userlogin.database.sql.SQLite;
import com.elchologamer.userlogin.util.Utils;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Base64;
import java.util.UUID;

public abstract class Database {

    private static final UserLogin plugin = UserLogin.getPlugin();

    private final boolean logConnected;
    private final BCrypt.Hasher hasher = BCrypt.withDefaults();
    private final BCrypt.Verifyer verifier = BCrypt.verifyer();

    public Database() {
        this(true);
    }

    public Database(boolean logConnected) {
        this.logConnected = logConnected;
    }

    public static Database select() {
        String type = plugin.getConfig().getString("database.type", "yaml");

        switch (type.toLowerCase()) {
            case "mysql":
                return new MySQL();
            case "postgresql":
            case "postgres":
                return new PostgreSQL();
            case "sqlite":
                return new SQLite();
            case "mongodb":
            case "mongo":
                return new MongoDB();
            default:
                Utils.log("&eInvalid database type selected, defaulting to \"yaml\"");
            case "yaml":
            case "yml":
                return new YamlDB();
        }
    }

    public boolean isRegistered(UUID uuid) {
        return getRawPassword(uuid) != null;
    }

    public boolean comparePasswords(UUID uuid, String otherPassword) {
        String rawPassword = getRawPassword(uuid);

        if (rawPassword.startsWith("§")) {
            // Password is encrypted the old way (Base64)
            String decrypted = rawPassword;

            try {
                // Decrypt base64 password
                byte[] bytes = Base64.getDecoder().decode(rawPassword.replaceAll("^§", ""));
                ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                ObjectInputStream is = new ObjectInputStream(in);
                decrypted = (String) is.readObject();

                updatePassword(uuid, decrypted); // Hash password the new way
            } catch (Exception e) {
                e.printStackTrace();
            }

            return decrypted.equals(otherPassword);
        }

        if (!rawPassword.matches("\\$2a\\$[0-9]+\\$[A-Za-z0-9/+._-]{53}")) {
            // Password is not encrypted at all
            try {
                updatePassword(uuid, rawPassword);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return rawPassword.equals(otherPassword);
        }

        BCrypt.Result result = verifier.verify(otherPassword.toCharArray(), rawPassword);
        return result.verified;
    }

    private String hash(String s) {
        int extra = Math.max(plugin.getConfig().getInt("password.extraSalt"), 0);
        return hasher.hashToString(10 + extra, s.toCharArray());
    }

    public void createPassword(UUID uuid, String password) throws Exception {
        createRawPassword(uuid, hash(password));
    }

    public void updatePassword(UUID uuid, String password) throws Exception {
        updateRawPassword(uuid, hash(password));
    }

    public abstract void connect() throws Exception;

    public abstract void disconnect() throws Exception;

    protected abstract String getRawPassword(UUID uuid);

    protected abstract void createRawPassword(UUID uuid, String password) throws Exception;

    protected abstract void updateRawPassword(UUID uuid, String password) throws Exception;

    public abstract void deletePassword(UUID uuid) throws Exception;

    public UserLogin getPlugin() {
        return plugin;
    }

    public boolean logConnected() {
        return logConnected;
    }
}
