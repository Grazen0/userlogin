package com.elchologamer.userlogin.listeners.restrictions;

import com.elchologamer.userlogin.util.Restriction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandRestriction extends Restriction<PlayerCommandPreprocessEvent> {
    public CommandRestriction() {
        super("commands");
    }

    @EventHandler
    public void handle(PlayerCommandPreprocessEvent e) {
        if (!shouldRestrict(e)) return;

        String m = e.getMessage();
        if (!m.startsWith("/login") && !m.startsWith("/register")) {
            e.setCancelled(true);
        }
    }
}
