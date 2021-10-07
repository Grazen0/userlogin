package com.elchologamer.userlogin.util

import com.elchologamer.userlogin.UserLogin.Companion.plugin
import com.google.common.io.ByteStreams
import com.google.gson.JsonParser
import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*

object Utils {

    // TODO: Test function
    fun color(s: String): String {
        var out = s

        try {
            Class.forName("net.md_5.bungee.api.ChatColor").getMethod("of", String::class.java)

            out = out.replace(Regex("#[a-fA-F0-9]{6}")) {
                ChatColor.of(it.value).toString()
            }
        } catch (ignored: ClassNotFoundException) {
        } catch (ignored: NoSuchMethodException) {
        }
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', out)
    }

    fun debug(s: Any) {
        if (plugin.config.getBoolean("debug")) log(s)
    }

    fun log(msg: Any, vararg args: Any?) =
        plugin.server.consoleSender.sendMessage("[${plugin.name}] ${color(String.format(msg.toString(), *args))}")

    fun sendPluginMessage(player: Player, channel: String, vararg args: String) {
        val out = ByteStreams.newDataOutput()

        args.forEach { out.writeUTF(it) }
        player.sendPluginMessage(plugin, channel, out.toByteArray())
    }

    fun fetch(url: String): String? {
        return try {
            val con = URL(url).openConnection() as HttpURLConnection
            con.requestMethod = "GET"

            val reader = BufferedReader(InputStreamReader(con.inputStream))
            reader.readLines().joinToString()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun fetchPlayerUUID(name: String?): UUID? {
        return try {
            val url = "https://api.mojang.com/users/profiles/minecraft/" + URLEncoder.encode(name, "UTF-8")
            val res = fetch(url) ?: return null

            val base = JsonParser().parse(res) ?: return null
            val data = base.asJsonObject

            if (!data.has("id")) return null

            // Found this here:
            // https://stackoverflow.com/questions/18986712/creating-a-uuid-from-a-string-with-no-dashes
            val uuidString = data["id"].asString
                .replaceFirst(
                    "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)".toRegex(),
                    "$1-$2-$3-$4-$5"
                )

            UUID.fromString(uuidString)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            null
        }
    }
}