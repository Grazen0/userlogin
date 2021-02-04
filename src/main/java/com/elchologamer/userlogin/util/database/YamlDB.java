package com.elchologamer.userlogin.util.database;

import com.elchologamer.userlogin.api.CustomConfig;
import com.elchologamer.userlogin.util.Utils;

import java.util.UUID;

public class YamlDB implements Database {

    private final CustomConfig playerData;

    public YamlDB() {
        String filePath = Utils.getConfig().getString("database.yaml.file", "playerData.yml");
        playerData = new CustomConfig(filePath);
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
    public void setPassword(UUID uuid, String password) {
        playerData.get().set(uuid.toString(), password);
        playerData.save();
    }

    @Override
    public void deletePassword(UUID uuid) {
        setPassword(uuid, null);
    }

    @Override
    public void disconnect() {
        playerData.save();
    }
}
