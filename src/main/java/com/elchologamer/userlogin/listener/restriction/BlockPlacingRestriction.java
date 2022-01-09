package com.elchologamer.userlogin.listener.restriction;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlacingRestriction extends BaseRestriction<BlockPlaceEvent> {

    public BlockPlacingRestriction() {
        super("blockPlacing");
    }

    @Override
    protected Player getEventPlayer(BlockPlaceEvent event) {
        return event.getPlayer();
    }

    @EventHandler
    public void handle(BlockPlaceEvent e) {
        if (shouldRestrict(e)) e.setCancelled(true);
    }
}