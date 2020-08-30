package com.elchologamer.userlogin.listeners;

import com.elchologamer.userlogin.api.UserLoginAPI;
import com.elchologamer.userlogin.util.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class OnItemDrop implements Listener {

    private final Utils utils = new Utils();

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        if (utils.getConfig().getBoolean("restrictions.itemDrop")
                && !UserLoginAPI.isLoggedIn(e.getPlayer()))
            e.setCancelled(true);
    }
}
