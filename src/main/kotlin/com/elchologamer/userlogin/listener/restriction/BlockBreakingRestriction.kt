package com.elchologamer.userlogin.listener.restriction

import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent

object BlockBreakingRestriction : BaseRestriction<BlockBreakEvent>("blockBreaking") {
    @EventHandler
    fun handle(e: BlockBreakEvent) {
        if (shouldRestrict(e)) e.isCancelled = true
    }
}