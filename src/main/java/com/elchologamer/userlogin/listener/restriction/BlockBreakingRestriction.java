package com.elchologamer.userlogin.listener.restriction;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakingRestriction extends BaseRestriction<BlockBreakEvent> {

    public BlockBreakingRestriction() {
        super("blockBreaking");
    }

    @Override
    protected Player getEventPlayer(BlockBreakEvent event) {
        return event.getPlayer();
    }

    @EventHandler
    public void handle(BlockBreakEvent e) {
        if (shouldRestrict(e)) e.setCancelled(true);
    }
}