package com.elchologamer.userlogin.database


import com.elchologamer.userlogin.UserLogin.Companion.plugin
import com.elchologamer.userlogin.util.CustomConfig
import java.util.*

class YamlDB : Database(false) {
    private val playerData = CustomConfig(plugin.config.getString("database.yaml.file", "playerData.yml")!!)

    override fun connect() {
        playerData.saveDefault()
        playerData.reload()
    }

    override fun getRawPassword(uuid: UUID): String {

        val key = uuid.toString()
        return if (playerData.config.isConfigurationSection(key)) {
            playerData.config.getString("$key.password") ?: throw IllegalArgumentException()
        } else {
            playerData.config.getString(key) ?: throw IllegalArgumentException()
        }
    }

    override fun createRawPassword(uuid: UUID, password: String) {
        updateRawPassword(uuid, password)
    }

    override fun updateRawPassword(uuid: UUID, password: String) {
        playerData.config.set(uuid.toString(), password)
        playerData.save()
    }

    override fun deletePassword(uuid: UUID) {
        playerData.config.set(uuid.toString(), null)
        playerData.save()
    }

    override fun disconnect() {
        playerData.save()
    }
}