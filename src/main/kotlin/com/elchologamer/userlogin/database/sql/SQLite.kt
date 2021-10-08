package com.elchologamer.userlogin.database.sql

import com.elchologamer.userlogin.UserLogin.Companion.plugin
import java.io.File
import java.sql.Connection
import java.sql.DriverManager

class SQLite : SQLDatabase("sqlite", "org.sqlite.JDBC") {
    override fun getConnection(): Connection {
        val path = plugin.config.getString("database.sqlite.database", "C:/sqlite/db/userlogin.db")!!
        File(path).parentFile.mkdirs()

        return DriverManager.getConnection("jdbc:sqlite:$path")
    }
}