package com.elchologamer.userlogin.listener.restriction;

import com.elchologamer.userlogin.api.UserLoginAPI;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class AttackRestriction extends BaseRestriction<EntityDamageByEntityEvent> {

    public AttackRestriction() {
        super("damage.attack");
    }

    @Override
    public boolean shouldRestrict(EntityDamageByEntityEvent event) {
        if (!getPlugin().getConfig().getBoolean("restrictions." + configKey)) return false;

        Entity damager = event.getDamager();
        if (damager.getType() != EntityType.PLAYER) return false;

        return !UserLoginAPI.isLoggedIn((Player) damager);
    }

    @EventHandler
    public void handle(EntityDamageByEntityEvent event) {
        if (shouldRestrict(event)) event.setCancelled(true);
    }
}