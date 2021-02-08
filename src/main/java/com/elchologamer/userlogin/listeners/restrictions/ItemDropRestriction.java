package com.elchologamer.userlogin.listeners.restrictions;

import com.elchologamer.userlogin.util.Restriction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;

public class ItemDropRestriction extends Restriction<PlayerDropItemEvent> {
    public ItemDropRestriction() {
        super("itemDrop");
    }

    @EventHandler
    public void handle(PlayerDropItemEvent e) {
        if (shouldRestrict(e)) e.setCancelled(true);
    }
}
