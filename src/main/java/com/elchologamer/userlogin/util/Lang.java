package com.elchologamer.userlogin.util;

import com.elchologamer.userlogin.UserLogin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Lang {

    private final Map<String, FileConfiguration> langs = new HashMap<>();

    public void createDefault() {
        // Create lang folder
        UserLogin plugin = UserLogin.getPlugin();
        File folder = new File(plugin.getDataFolder(), "lang");
        if (!folder.mkdir()) return;

        String[] langs = {"en_US", "es_ES"};

        for (String lang : langs) {
            String filename = lang + ".yml";
            File file = new File(folder, filename);
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            InputStreamReader reader = new InputStreamReader(plugin.getResource("lang/" + filename));
            FileConfiguration resourceConfig = YamlConfiguration.loadConfiguration(reader);

            // Add default values from resource
            for (String key : resourceConfig.getKeys(true)) {
                config.addDefault(key, resourceConfig.get(key));
            }

            config.options().copyDefaults(true);

            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void load() {
        File langFolder = new File(UserLogin.getPlugin().getDataFolder(), "lang");
        langFolder.mkdir();

        langs.clear();
        File[] files = langFolder.listFiles();
        if (files == null) return;

        for (File file : files) {
            String name = file.getName();
            if (!name.toLowerCase().endsWith(".yml")) continue;

            String lang = name.substring(0, name.length() - 4);
            langs.put(lang, YamlConfiguration.loadConfiguration(file));
        }
    }

    public FileConfiguration getMessages() {
        String langName = Utils.getConfig().getString("lang", "en_US");
        return langs.get(langName);
    }

    public Map<String, FileConfiguration> getLangs() {
        return langs;
    }
}
