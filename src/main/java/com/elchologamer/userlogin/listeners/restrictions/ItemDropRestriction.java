package com.elchologamer.userlogin.listeners.restrictions;

import com.elchologamer.userlogin.util.Restriction;
import org.bukkit.event.player.PlayerDropItemEvent;

public class ItemDropRestriction extends Restriction<PlayerDropItemEvent> {
    public ItemDropRestriction() {
        super("itemDrop");
    }

    @Override
    public void handle(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }
}
