package com.elchologamer.userlogin.manager

import com.elchologamer.userlogin.UserLogin.Companion.plugin
import com.elchologamer.userlogin.util.Utils
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

object LangManager {
    private val langConfigs: MutableMap<String, FileConfiguration> = HashMap()

    fun createDefault() {
        // Create lang folder
        val folder = File(plugin.dataFolder, "lang")
        folder.mkdirs()

        val langs = arrayOf("en_US", "es_ES")

        for (lang in langs) {
            try {
                val filename = "$lang.yml"
                val file = File(folder, filename)

                // Load lang from plugin resource
                val stream = plugin.getResource("lang/$filename") ?: continue
                val reader = InputStreamReader(stream)

                val resourceConfig = YamlConfiguration.loadConfiguration(reader)
                val config = YamlConfiguration.loadConfiguration(file)

                // Add missing keys
                for (key in resourceConfig.getKeys(true)) {
                    val value = resourceConfig[key]!!
                    config.addDefault(key, value)

                    // Backwards compatibility
                    val oldKey = key.replace("_", "-")
                    if (oldKey != key) {
                        config[key] = config[oldKey]
                        config[oldKey] = null
                    }
                }

                // Save defaults
                config.options().copyDefaults(true)
                config.save(file)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun reload() {
        createDefault()
        val langFolder = File(plugin.dataFolder, "lang")

        langConfigs.clear()

        val files = langFolder.listFiles() ?: return
        for (file in files) {
            val name = file.name
            if (!name.lowercase().endsWith(".yml")) continue

            val lang = name.substring(0, name.length - 4)
            langConfigs[lang] = YamlConfiguration.loadConfiguration(file)
        }
    }

    val entries: FileConfiguration
        get() {
            val lang = langConfigs[plugin.config.getString("lang", "en_US")!!]
            return lang ?: langConfigs.values.iterator().next()
        }

    fun getMessage(path: String, def: String): String {
        return getMessage(path) ?: def
    }

    fun getMessage(path: String): String? {
        val message = entries.getString(path)
        return if (message == null) null else Utils.color(message)
    }
}