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

                    if (!oldKey.equals(key)) {
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
