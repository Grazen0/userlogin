package com.elcholostudios.userlogin.events;

import com.elcholostudios.userlogin.util.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class OnCommandSent implements Listener {

    private final Utils utils = new Utils();

    @EventHandler
    public void onCommandSent(PlayerCommandPreprocessEvent e) {
        String msg = e.getMessage() + " ";
        if (utils.getConfig().getBoolean("restrictions.commands")
                && !Utils.loggedIn.get(e.getPlayer().getUniqueId()) &&
                !(msg.startsWith("/login ") || msg.startsWith("/register "))) {
            e.setCancelled(true);
            return;
        }

        if (msg.startsWith("/reload "))
            new Utils().reloadWarn(e.getPlayer());
    }
}
