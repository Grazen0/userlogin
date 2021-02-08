package com.elchologamer.userlogin.util.managers;

import com.elchologamer.userlogin.UserLogin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class LangManager {

    private final UserLogin plugin = UserLogin.getPlugin();
    private final Map<String, FileConfiguration> langs = new HashMap<>();

    public void createDefault() {
        // Create lang folder
        File folder = new File(plugin.getDataFolder(), "lang");
        folder.mkdirs();

        String[] langs = {"en_US", "es_ES"};

        for (String lang : langs) {
            String filename = lang + ".yml";
            File file = new File(folder, filename);

            InputStream stream = plugin.getResource("lang/" + filename);
            if (stream == null) continue;

            InputStreamReader reader = new InputStreamReader(stream);
            FileConfiguration resourceConfig = YamlConfiguration.loadConfiguration(reader);

            // Add missing keys to existing files
            if (file.exists()) {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);

                for (String key : resourceConfig.getKeys(true)) {
                    if (config.get(key) == null) {
                        config.set(key, resourceConfig.get(key));
                    }
                }

                try {
                    config.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            FileConfiguration config = YamlConfiguration.loadConfiguration(file);


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
        createDefault();
        File langFolder = new File(plugin.getDataFolder(), "lang");

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
        String langName = plugin.getConfig().getString("lang", "en_US");
        FileConfiguration lang = langs.get(langName);

        return lang == null ? langs.values().iterator().next() : lang;
    }

    public Map<String, FileConfiguration> getLangs() {
        return langs;
    }
}
