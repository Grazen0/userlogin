package com.elchologamer.userlogin.api;

import com.elchologamer.userlogin.UserLogin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class CustomConfig {

    protected String path;
    protected File file;
    protected FileConfiguration config;

    public CustomConfig(String path) {
        this(path, true);
    }

    public CustomConfig(String path, boolean autoLoad) {
        this.path = path;

        if (autoLoad) {
            saveDefault();
            reload();
        }
    }

    public void saveDefault() {
        checkFile();
        if (file.exists()) return;

        try (InputStream in = UserLogin.getPlugin().getResource(path)) {
            if (in != null) {
                Files.copy(in, file.toPath());
            } else {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkFile() {
        if (file == null)
            file = new File(UserLogin.getPlugin().getDataFolder(), path);
    }

    public void reload() {
        checkFile();

        try {
            config = YamlConfiguration.loadConfiguration(file);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration get() {
        if (config == null)
            reload();
        return config;
    }

    public void save() {
        checkFile();

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getFile() {
        return file;
    }

    public String getPath() {
        return path;
    }
}
