package com.elchologamer.userlogin.listeners;

import com.elchologamer.userlogin.api.UserLoginAPI;
import com.elchologamer.userlogin.util.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class OnPlayerCommand implements Listener {

    private final Utils utils = new Utils();

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        try {
            if (!UserLoginAPI.isLoggedIn(e.getPlayer()) && utils.getConfig().getBoolean("restrictions.commands") &&
                    !e.getMessage().startsWith("/login") && !e.getMessage().startsWith("/register"))
                e.setCancelled(true);
        } catch (NullPointerException ignored) {
        }
    }
}
