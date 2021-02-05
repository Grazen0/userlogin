package com.elchologamer.userlogin.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class CustomConfig {

    private final JavaPlugin plugin;
    private final String path;
    private final File file;
    private FileConfiguration config;

    public CustomConfig(JavaPlugin plugin, String path) {
        this(plugin, path, true);
    }

    public CustomConfig(JavaPlugin plugin, String path, boolean autoLoad) {
        this.plugin = plugin;
        this.path = path;
        this.file = new File(plugin.getDataFolder(), path);

        if (autoLoad) {
            saveDefault();
            reload();
        }
    }

    public void saveDefault() {
        if (file.exists()) return;

        try (InputStream in = plugin.getResource(path)) {
            if (in != null) {
                Files.copy(in, file.toPath());
            } else {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
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
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public File getFile() {
        return file;
    }

    public String getPath() {
        return path;
    }
}
