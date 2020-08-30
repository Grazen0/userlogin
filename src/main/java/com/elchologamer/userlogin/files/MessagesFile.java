package com.elchologamer.userlogin.files;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.api.util.Configuration;
import com.elchologamer.userlogin.util.Utils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MessagesFile extends Configuration {

    private final Utils utils = new Utils();

    public MessagesFile() {
        super(null);
    }

    @Override
    public void setup() {
        // Create lang folder
        File folder = new File(UserLogin.plugin.getDataFolder(), "lang\\");
        folder.mkdir();

        // Load lang file specified in config
        name = utils.getConfig().getString("lang");
        if (name == null) name = "en_US";
        file = new File(UserLogin.plugin.getDataFolder(), "lang\\" + name + ".yml");

        reload();
    }

    @Override
    public void registerDefaults() {
    }

    @Override
    public void reload() {
        // Create lang file if necessary
        if (!file.exists()) {
            System.out.println("Lang file \"" + name + "\" not found! Creating default values...");
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Error while trying to create lang file");
                e.printStackTrace();
            }
        }

        // Load configuration and register default values
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }
}
