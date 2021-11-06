package com.elchologamer.userlogin.listener.restriction;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class ItemPickupRestriction extends BaseRestriction<EntityPickupItemEvent> {

    public ItemPickupRestriction() {
        super("itemPickup");
    }

    @EventHandler
    public void handle(EntityPickupItemEvent e) {
        if (shouldRestrict(e))
            e.setCancelled(true);
    }
}