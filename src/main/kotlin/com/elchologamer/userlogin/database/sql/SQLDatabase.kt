package com.elchologamer.userlogin.database.sql

import com.elchologamer.userlogin.UserLogin.Companion.plugin
import com.elchologamer.userlogin.database.Database
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*

abstract class SQLDatabase protected constructor(val name: String, private val driver: String) : Database() {
    private var connection: Connection? = null
    val table: String
    val database: String?

    init {
        val section = plugin.config.getConfigurationSection("database.$name")!!
        table = section.getString("table", "player_data")!!.replace("`", "")
        database = section.getString("database")
    }

    override fun connect() {
        Class.forName(driver)
        connection = getConnection()

        val maxChars = plugin.config.getInt("password.maxCharacters", 128).coerceAtLeast(1)


        // Create table
        update(
            "CREATE TABLE IF NOT EXISTS $table (" +
                    "uuid VARCHAR(45) NOT NULL," +
                    "password VARCHAR($maxChars) NOT NULL," +
                    "PRIMARY KEY (uuid)" +
                    ")"
        )
    }

    protected abstract fun getConnection(): Connection

    public override fun getRawPassword(uuid: UUID): String {
        val set = query("SELECT password FROM $table WHERE uuid=?", uuid)
        return set.getString("password")
    }

    public override fun createRawPassword(uuid: UUID, password: String) =
        update("INSERT INTO $table (uuid,password) VALUES (?,?)", uuid, password)

    public override fun updateRawPassword(uuid: UUID, password: String) {
        update("UPDATE $table SET password=? WHERE uuid=?", password, uuid)
    }

    override fun deletePassword(uuid: UUID) =
        update("DELETE FROM $table WHERE uuid=?", uuid)

    override fun disconnect() {
        if (connection?.isClosed == true) connection!!.close()
    }

    protected fun prepareSQL(sql: String?, vararg params: Any): PreparedStatement {
        connection ?: throw SQLException("Tried to query database, but connection isn't available")
        val statement = connection!!.prepareStatement(sql)

        for (i in params.indices) {
            statement.setString(i + 1, params[i].toString())
        }

        return statement
    }

    protected fun query(sql: String?, vararg params: Any): ResultSet {
        return prepareSQL(sql, *params).executeQuery()
    }

    protected fun update(sql: String?, vararg params: Any) {
        prepareSQL(sql, *params).executeUpdate()
    }
}