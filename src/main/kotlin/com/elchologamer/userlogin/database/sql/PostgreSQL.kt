package com.elchologamer.userlogin.database.sql

import com.elchologamer.userlogin.UserLogin.Companion.plugin
import org.bukkit.configuration.ConfigurationSection
import java.sql.Connection
import java.sql.DriverManager
import java.util.*

class PostgreSQL : SQLDatabase("postgresql", "org.postgresql.Driver") {
    override fun connect() {
        super.connect()

        // Update password max length
        val maxChars = plugin.config.getInt("password.maxCharacters", 128).coerceAtLeast(1)

        update(
            "ALTER TABLE $table " +
                    "ALTER COLUMN password " +
                    "TYPE VARCHAR($maxChars)"
        )
    }

    override fun getConnection(): Connection {
        val section: ConfigurationSection = plugin.config.getConfigurationSection("database.postgresql")
            ?: throw IllegalStateException("PostgreSQL settings missing in config.yml")

        val host = section.getString("host", "localhost")
        val port = section.getInt("port", 5432)
        val ssl = section.getBoolean("ssl")
        val username = section.getString("username", "root")
        val password = section.getString("password", null)

        val props = Properties()

        props.setProperty("user", username)
        password?.let { props.setProperty("password", it) }
        props.setProperty("ssl", ssl.toString())

        return DriverManager.getConnection("jdbc:postgresql://$host:$port/$database", props)
    }
}