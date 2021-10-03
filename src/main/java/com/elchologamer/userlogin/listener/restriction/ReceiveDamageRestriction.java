package com.elchologamer.userlogin.listener.restriction;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class ReceiveDamageRestriction extends BaseRestriction<EntityDamageEvent> {

    public ReceiveDamageRestriction() {
        super("damage.receive");
    }

    @Override
    public boolean shouldRestrict(EntityDamageEvent e) {
        return super.shouldRestrict(e) && e.getEntityType() == EntityType.PLAYER;
    }

    @EventHandler
    public void handle(EntityDamageEvent e) {
        if (shouldRestrict(e)) e.setCancelled(true);
    }
}
