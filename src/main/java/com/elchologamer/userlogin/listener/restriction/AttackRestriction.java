package com.elchologamer.userlogin.listener.restriction;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class AttackRestriction extends BaseRestriction<EntityDamageByEntityEvent> {

    public AttackRestriction() {
        super("damage.attack");
    }

    @Override
    protected Player getEventPlayer(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        return damager instanceof Player ? (Player) damager : null;
    }

    @EventHandler
    public void handle(EntityDamageByEntityEvent event) {
        if (shouldRestrict(event)) event.setCancelled(true);
    }
}