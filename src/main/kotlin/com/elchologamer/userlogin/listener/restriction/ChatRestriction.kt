package com.elchologamer.userlogin.listener.restriction

import com.elchologamer.userlogin.ULPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.player.AsyncPlayerChatEvent

object ChatRestriction : BaseRestriction<AsyncPlayerChatEvent>("chat") {
    @EventHandler
    fun handle(e: AsyncPlayerChatEvent) {
        if (!shouldRestrict(e)) return
        e.isCancelled = true
        ULPlayer[e.player].sendMessage("messages.chat_disabled")
    }
}