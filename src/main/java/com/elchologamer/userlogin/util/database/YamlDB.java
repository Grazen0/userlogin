package com.elchologamer.userlogin.util.database;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.util.CustomConfig;
import org.bukkit.configuration.file.FileConfiguration;

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
    public String getRawPassword(UUID uuid) {
        FileConfiguration config = playerData.get();
        return config.getString(uuid.toString(), config.getString(uuid + ".password"));
    }

    @Override
    public void createRawPassword(UUID uuid, String password) {
        updateRawPassword(uuid, password);
    }

    @Override
    public void updateRawPassword(UUID uuid, String password) {
        playerData.get().set(uuid.toString(), password);
        playerData.save();
    }

    @Override
    public void deletePassword(UUID uuid) {
        updateRawPassword(uuid, null);
    }

    @Override
    public void disconnect() {
        playerData.save();
    }
}
