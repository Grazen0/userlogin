package com.elchologamer.userlogin.manager;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.util.Utils;
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
    private final Map<String, FileConfiguration> langConfigs = new HashMap<>();

    public void createDefault() {
        // Create lang folder
        File folder = new File(plugin.getDataFolder(), "lang");
        folder.mkdirs();

        String[] langs = {"en_US", "es_ES"};

        for (String lang : langs) {
            try {
                String filename = lang + ".yml";
                File file = new File(folder, filename);

                // Load lang from plugin resource
                InputStream stream = plugin.getResource("lang/" + filename);
                if (stream == null) continue;

                InputStreamReader reader = new InputStreamReader(stream);
                FileConfiguration resourceConfig = YamlConfiguration.loadConfiguration(reader);

                FileConfiguration config = YamlConfiguration.loadConfiguration(file);

                // Add missing keys
                for (String key : resourceConfig.getKeys(true)) {
                    Object value = resourceConfig.get(key);
                    config.addDefault(key, value);

                    // Backwards compatibility
                    String oldKey = key.replace("_", "-");

                    if (!oldKey.equals(key) && config.contains(oldKey, true)) {
                        config.set(key, config.get(oldKey));
                        config.set(oldKey, null);
                    }
                }

                // Save defaults
                config.options().copyDefaults(true);
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void reload() {
        createDefault();
        File langFolder = new File(plugin.getDataFolder(), "lang");

        langConfigs.clear();
        File[] files = langFolder.listFiles();
        if (files == null) return;

        // Load lang files
        for (File file : files) {
            String name = file.getName();
            if (!name.toLowerCase().endsWith(".yml")) continue;

            String lang = name.substring(0, name.length() - 4);
            langConfigs.put(lang, YamlConfiguration.loadConfiguration(file));
        }
    }

    public FileConfiguration getEntries() {
        String langName = plugin.getConfig().getString("lang", "en_US");
        FileConfiguration lang = langConfigs.get(langName);

        return lang == null ? langConfigs.values().iterator().next() : lang;
    }

    public String getMessage(String path, String def) {
        String message = getMessage(path);
        return message != null ? message : def;
    }

    public String getMessage(String path) {
        String message = getEntries().getString(path);
        return message != null ? Utils.color(message) : null;
    }
}