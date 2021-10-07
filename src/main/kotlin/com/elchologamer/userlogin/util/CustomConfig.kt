package com.elchologamer.userlogin.util

import com.elchologamer.userlogin.UserLogin.Companion.plugin
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException
import java.nio.file.Files

class CustomConfig constructor(private val path: String) {
    val file = File(plugin.dataFolder, path)
    var config = YamlConfiguration.loadConfiguration(file)
        private set

    init {
        saveDefault()
        reload()
    }

    fun saveDefault() {
        if (file.exists()) return

        plugin.dataFolder.mkdir()

        try {
            plugin.getResource(path).use { stream ->
                if (stream != null) {
                    Files.copy(stream, file.toPath())
                } else {
                    file.createNewFile()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun reload() {
        try {
            config = YamlConfiguration.loadConfiguration(file)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    fun save() {
        try {
            config.save(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}