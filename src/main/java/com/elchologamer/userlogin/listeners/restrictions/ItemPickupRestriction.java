package com.elchologamer.userlogin.listeners.restrictions;

import com.elchologamer.userlogin.util.Restriction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class ItemPickupRestriction extends Restriction<EntityPickupItemEvent> {

    public ItemPickupRestriction() {
        super("itemPickup");
    }

    @EventHandler
    public void handle(EntityPickupItemEvent e) {
        if (shouldRestrict(e)) e.setCancelled(true);
    }
}
