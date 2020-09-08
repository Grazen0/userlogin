package com.elchologamer.userlogin.util.files;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.api.util.Configuration;
import com.elchologamer.userlogin.util.Utils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MessagesFile extends Configuration {

    public MessagesFile() {
        super(null);
    }

    @Override
    public void setup() {
        // Create lang folder
        File folder = new File(UserLogin.getPlugin().getDataFolder(), "lang\\");
        folder.mkdir();

        // Load lang file specified in config
        name = Utils.getConfig().getString("lang");
        if (name == null) name = "en_US";
        file = new File(UserLogin.getPlugin().getDataFolder(), "lang\\" + name + ".yml");

        reload();
    }

    @Override
    public void registerDefaults() {
    }

    @Override
    public void reload() {
        if (file == null) return;

        // Create file if necessary
        try {
            file.createNewFile();
        } catch (IOException e) {
            System.out.println("Error while trying to create lang file \"" + name + "\"");
            e.printStackTrace();
        }

        // Load configuration and register default values
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }
}
