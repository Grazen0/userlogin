package com.elchologamer.userlogin.database

import at.favre.lib.crypto.bcrypt.BCrypt
import com.elchologamer.userlogin.UserLogin.Companion.plugin
import com.elchologamer.userlogin.database.sql.MySQL
import com.elchologamer.userlogin.database.sql.PostgreSQL
import com.elchologamer.userlogin.database.sql.SQLite
import com.elchologamer.userlogin.util.Utils
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream
import java.util.*

abstract class Database constructor(val logConnected: Boolean = true) {
    private val hasher = BCrypt.withDefaults()
    private val verifier = BCrypt.verifyer()

    companion object {
        fun select(): Database {
            val type = plugin.config.getString("database.type", "yaml")!!

            return when (type.lowercase()) {
                "mysql" -> MySQL()
                "postgresql", "postgres" -> PostgreSQL()
                "sqlite" -> SQLite()
                "mongodb", "mongo" -> MongoDB()
                "yaml", "yml" -> YamlDB()
                else -> {
                    Utils.log("&eInvalid database type selected, defaulting to \"yaml\"")
                    YamlDB()
                }
            }
        }
    }

    fun isRegistered(uuid: UUID) = getRawPassword(uuid) != null

    fun comparePasswords(uuid: UUID, otherPassword: String): Boolean {
        val rawPassword = getRawPassword(uuid)
            ?: throw IllegalArgumentException("UUID $uuid does not have an associated password")

        if (rawPassword.startsWith("§")) {
            // Password is encrypted the old way (Base64)
            var decrypted = rawPassword

            try {
                // Decrypt base64 password
                val bytes = Base64.getDecoder().decode(rawPassword.replace("^§".toRegex(), ""))
                val arrayInput = ByteArrayInputStream(bytes)
                val objInput = ObjectInputStream(arrayInput)
                decrypted = objInput.readObject() as String
                updatePassword(uuid, decrypted) // Hash password the new way
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return decrypted == otherPassword
        }
        if (!rawPassword.matches(Regex("\\$2a\\$[0-9]+\\$[A-Za-z0-9/+._-]{53}"))) {
            // Password is not encrypted at all
            try {
                updatePassword(uuid, rawPassword)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return rawPassword == otherPassword
        }

        val result = verifier.verify(otherPassword.toCharArray(), rawPassword)
        return result.verified
    }

    private fun hash(s: String): String {
        val extra = plugin.config.getInt("password.extraSalt").coerceAtLeast(0)
        return hasher.hashToString(10 + extra, s.toCharArray())
    }

    fun createPassword(uuid: UUID, password: String) {
        createRawPassword(uuid, hash(password))
    }

    fun updatePassword(uuid: UUID, password: String) {
        updateRawPassword(uuid, hash(password))
    }

    abstract fun connect()

    abstract fun disconnect()

    protected abstract fun getRawPassword(uuid: UUID): String?

    protected abstract fun createRawPassword(uuid: UUID, password: String)

    protected abstract fun updateRawPassword(uuid: UUID, password: String)

    abstract fun deletePassword(uuid: UUID)
}