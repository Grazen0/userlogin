package com.elchologamer.userlogin.listeners;

import com.elchologamer.userlogin.api.UserLoginAPI;
import com.elchologamer.userlogin.util.Utils;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class OnItemPickup implements Listener {

    private final Utils utils = new Utils();

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent e) {
        if (e.getEntity().getType().equals(EntityType.PLAYER)
                && utils.getConfig().getBoolean("restrictions.itemPickup")
                && !UserLoginAPI.isLoggedIn((Player) e.getEntity()))
            e.setCancelled(true);
    }
}
