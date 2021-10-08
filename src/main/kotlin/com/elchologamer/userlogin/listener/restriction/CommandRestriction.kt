package com.elchologamer.userlogin.listener.restriction

import com.elchologamer.userlogin.ULPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerCommandPreprocessEvent

object CommandRestriction : BaseRestriction<PlayerCommandPreprocessEvent>("commands") {
    @EventHandler
    fun handle(e: PlayerCommandPreprocessEvent) {
        if (!shouldRestrict(e)) return

        val command = e.message.replace(Regex("^/"), "").lowercase()

        if (!command.startsWith("login") && !command.startsWith("register")) {
            e.isCancelled = true
            ULPlayer[e.player].sendMessage("messages.commands_disabled")
        }
    }
}