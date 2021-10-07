package com.elchologamer.userlogin.listener

import com.elchologamer.userlogin.UserLogin.Companion.plugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

object OnPlayerQuit : Listener {
    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        plugin.getPlayer(e.player).onQuit()
    }
}