package com.elchologamer.userlogin.listener.restriction

import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockPlaceEvent

object BlockPlacingRestriction : BaseRestriction<BlockPlaceEvent>("blockPlacing") {
    @EventHandler
    fun handle(e: BlockPlaceEvent) {
        if (shouldRestrict(e)) e.isCancelled = true
    }
}