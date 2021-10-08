package com.elchologamer.userlogin.util

import com.elchologamer.userlogin.UserLogin.Companion.plugin
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.nio.file.Files

class CustomConfig constructor(private val path: String, autoload: Boolean = true) {
    val file = File(plugin.dataFolder, path)
    var config = YamlConfiguration.loadConfiguration(file)
        private set

    init {
        if (autoload) {
            saveDefault()
            reload()
        }
    }

    fun saveDefault() {
        if (file.exists()) return

        plugin.dataFolder.mkdir()

        plugin.getResource(path).use { stream ->
            if (stream != null) {
                Files.copy(stream, file.toPath())
            } else {
                file.createNewFile()
            }
        }
    }

    fun reload() {
        config = YamlConfiguration.loadConfiguration(file)
    }

    fun save() = config.save(file)
}