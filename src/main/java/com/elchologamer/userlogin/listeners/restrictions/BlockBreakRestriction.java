package com.elchologamer.userlogin.listeners.restrictions;

import com.elchologamer.userlogin.util.Restriction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakRestriction extends Restriction<BlockBreakEvent> {

    public BlockBreakRestriction() {
        super("blockBreak");
    }

    @EventHandler
    public void handle(BlockBreakEvent e) {
        if (!shouldRestrict(e)) e.setCancelled(true);
    }
}
