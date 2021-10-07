package com.elchologamer.userlogin.listener

import com.elchologamer.userlogin.UserLogin.Companion.plugin
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerQuitEvent

object OnPlayerQuit : BaseListener() {
    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        plugin.getPlayer(e.player).onQuit()
    }
}