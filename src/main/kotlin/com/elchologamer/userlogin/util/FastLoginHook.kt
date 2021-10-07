package com.elchologamer.userlogin.util

import com.elchologamer.userlogin.UserLogin.Companion.plugin
import com.elchologamer.userlogin.api.types.AuthType
import com.github.games647.fastlogin.bukkit.FastLoginBukkit
import com.github.games647.fastlogin.core.hooks.AuthPlugin
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

object FastLoginHook : AuthPlugin<Player> {
    fun register() {
        val fastLogin = JavaPlugin.getPlugin(FastLoginBukkit::class.java)
        fastLogin.core.authPluginHook = this
    }

    override fun forceLogin(player: Player): Boolean {
        val ulPlayer = plugin.getPlayer(player)
        if (ulPlayer.loggedIn) return false

        plugin.server.scheduler.runTask(
            plugin,
            Runnable { ulPlayer.onAuthenticate(AuthType.LOGIN) }
        )
        return true
    }

    override fun forceRegister(player: Player, password: String): Boolean {
        return try {
            plugin.db.createPassword(player.uniqueId, password)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun isRegistered(playerName: String): Boolean {
        val uuid = Utils.fetchPlayerUUID(playerName) ?: return false
        return plugin.db.isRegistered(uuid)
    }
}