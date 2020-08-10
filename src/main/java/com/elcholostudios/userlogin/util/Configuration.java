package com.elcholostudios.userlogin.util;

import com.elcholostudios.userlogin.UserLogin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Configuration {
    
    private final String name;
    private File file;
    private FileConfiguration fileConfiguration;
    
    public Configuration(String name) {
        this.name = name;
    }

    public void setup() {
        file = new File(UserLogin.plugin.getDataFolder(), name + ".yml");

        if(!file.exists()){
            try{
                file.createNewFile();
            }catch (IOException e){
                System.out.println("Failed to create \"" + name + ".yml\" file!");
                e.printStackTrace();
            }
        }

        reload();
    }

    public FileConfiguration get() {
        return fileConfiguration;
    }

    public void reload() {
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try{
            fileConfiguration.save(file);
        }catch (IOException e){
            System.out.println("Error while trying to save \"" + name + ".yml\"!");
            e.printStackTrace();
        }
    }
}
