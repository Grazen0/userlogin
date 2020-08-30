package com.elchologamer.userlogin.listeners;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.api.event.ServerReloadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.jetbrains.annotations.NotNull;

public class ReloadListener implements Listener {

    @EventHandler
    public void onConsoleCommand(@NotNull ServerCommandEvent e) {
        if ((e.getCommand() + " ").startsWith("reload ")) {
            ServerReloadEvent event = new ServerReloadEvent(e.getSender());
            UserLogin.plugin.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled())
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerCommand(@NotNull PlayerCommandPreprocessEvent e) {
        if ((e.getMessage() + " ").startsWith("/reload ")) {
            ServerReloadEvent event = new ServerReloadEvent(e.getPlayer());
            UserLogin.plugin.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled())
                e.setCancelled(true);
        }
    }
}
