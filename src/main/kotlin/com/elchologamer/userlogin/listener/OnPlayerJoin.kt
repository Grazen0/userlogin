package com.elchologamer.userlogin.listener

import com.elchologamer.userlogin.UserLogin.Companion.plugin
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent

object OnPlayerJoin : BaseListener() {

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        e.joinMessage = null
        val ulPlayer = plugin.players.getOrCreate(e.player)
        ulPlayer.onJoin(e)
    }
}