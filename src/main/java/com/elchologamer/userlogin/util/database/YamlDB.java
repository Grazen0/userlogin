package com.elchologamer.userlogin.util.database;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.util.CustomConfig;

import java.util.UUID;

public class YamlDB extends Database {

    private final CustomConfig playerData;

    public YamlDB() {
        String filePath = getPlugin().getConfig()
                .getString("database.yaml.file", "playerData.yml");
        playerData = new CustomConfig(UserLogin.getPlugin(), filePath);
    }

    @Override
    public void connect() {
        playerData.saveDefault();
        playerData.reload();
    }

    @Override
    public String getPassword(UUID uuid) {
        return playerData.get().getString(uuid.toString());
    }

    @Override
    public void createPassword(UUID uuid, String password) {
        updatePassword(uuid, password);
    }

    @Override
    public void updatePassword(UUID uuid, String password) {
        playerData.get().set(uuid.toString(), password);
        playerData.save();
    }

    @Override
    public void deletePassword(UUID uuid) {
        updatePassword(uuid, null);
    }

    @Override
    public void disconnect() {
        playerData.save();
    }
}
