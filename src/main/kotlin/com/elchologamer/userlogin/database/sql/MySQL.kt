package com.elchologamer.userlogin.database.sql

import com.elchologamer.userlogin.UserLogin.Companion.plugin
import org.bukkit.configuration.ConfigurationSection
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*

class MySQL : SQLDatabase("mysql", "com.mysql.jdbc.Driver") {
    @Throws(SQLException::class, ClassNotFoundException::class)
    override fun connect() {
        super.connect()

        // Remove username column if exists
        if (query("SHOW COLUMNS FROM $table LIKE 'username'").next()) {
            update("ALTER TABLE $table DROP COLUMN username")
        }

        // Update password max length
        val maxChars = plugin.config.getInt("password.maxCharacters", 128).coerceAtLeast(1)
        update("ALTER TABLE $table MODIFY password VARCHAR($maxChars)")
    }

    override fun getConnection(): Connection {
        val section: ConfigurationSection = plugin.config.getConfigurationSection("database.mysql")
            ?: throw IllegalStateException("MySQL settings missing in config.yml")

        val host = section.getString("host", "localhost")
        val port = section.getInt("port", 3306)
        val ssl = section.getBoolean("ssl", section.getBoolean("useSSL"))
        val username = section.getString("username", "root")
        val password = section.getString("password", null)

        val props = Properties()

        props.setProperty("user", username)
        password?.let { props.setProperty("password", it) }
        props.setProperty("useSSL", ssl.toString())
        props.setProperty("autoReconnect", "true")

        return DriverManager.getConnection("jdbc:mysql://$host:$port/$database", props)
    }
}