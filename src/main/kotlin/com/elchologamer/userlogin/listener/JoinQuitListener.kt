package com.elchologamer.userlogin.listener

import com.elchologamer.userlogin.ULPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object JoinQuitListener : Listener {

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        e.joinMessage = null
        ULPlayer[e.player].onJoin()
    }

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        ULPlayer[e.player].onQuit()
    }
}