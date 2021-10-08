package com.elchologamer.userlogin.listener.restriction

import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerDropItemEvent

object ItemDropRestriction : BaseRestriction<PlayerDropItemEvent>("itemDrop") {
    @EventHandler
    fun handle(e: PlayerDropItemEvent) {
        if (shouldRestrict(e)) e.isCancelled = true
    }
}