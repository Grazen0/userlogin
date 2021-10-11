package com.elchologamer.userlogin.listener.restriction;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakingRestriction extends BaseRestriction<BlockBreakEvent> {

    public BlockBreakingRestriction() {
        super("blockBreaking");
    }

    @EventHandler
    public void handle(BlockBreakEvent e) {
        if (shouldRestrict(e)) e.setCancelled(true);
    }
}