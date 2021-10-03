package com.elchologamer.userlogin.listener.restriction;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;

public class ItemDropRestriction extends BaseRestriction<PlayerDropItemEvent> {
    public ItemDropRestriction() {
        super("itemDrop");
    }

    @EventHandler
    public void handle(PlayerDropItemEvent e) {
        if (shouldRestrict(e)) e.setCancelled(true);
    }
}
