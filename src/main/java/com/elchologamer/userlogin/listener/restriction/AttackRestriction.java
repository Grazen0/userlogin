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
    public boolean shouldRestrict(EntityDamageByEntityEvent e) {
        boolean enabled = getPlugin().getConfig().getBoolean("restrictions." + getConfigKey());
        if (!enabled) return false;

        Entity damager = e.getDamager();
        if (damager.getType() != EntityType.PLAYER) return false;

        return !UserLoginAPI.isLoggedIn((Player) damager);
    }

    @EventHandler
    public void handle(EntityDamageByEntityEvent e) {
        if (shouldRestrict(e)) e.setCancelled(true);
    }
}
