package com.elchologamer.userlogin.listeners.restrictions;

import com.elchologamer.userlogin.util.Restriction;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class ItemPickupRestriction extends Restriction<EntityPickupItemEvent> {

    public ItemPickupRestriction() {
        super("itemPickup");
    }

    @Override
    public void handle(EntityPickupItemEvent e) {
        e.setCancelled(true);
    }
}
