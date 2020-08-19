package com.elcholostudios.userlogin.util.data;

import com.elcholostudios.userlogin.UserLogin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public abstract class Configuration {

    protected @Nullable String name;
    protected File file;
    protected FileConfiguration fileConfiguration;

    public Configuration(@Nullable String name) {
        this.name = name;
    }

    public void setup() {
        file = new File(UserLogin.plugin.getDataFolder(), name + ".yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Failed to create \"" + name + ".yml\" file!");
                e.printStackTrace();
            }
        }

        reload();
    }

    public abstract void registerDefaults();

    public FileConfiguration get() {
        return fileConfiguration;
    }

    public void reload() {
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
        this.registerDefaults();
    }

    public void save() {
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            System.out.println("Error while trying to save \"" + name + ".yml\"!");
            e.printStackTrace();
        }
    }
}
