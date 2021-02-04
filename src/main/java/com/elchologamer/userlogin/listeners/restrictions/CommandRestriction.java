package com.elchologamer.userlogin.listeners.restrictions;

import com.elchologamer.userlogin.util.Restriction;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandRestriction extends Restriction<PlayerCommandPreprocessEvent> {
    public CommandRestriction() {
        super("commands");
    }

    @Override
    public void handle(PlayerCommandPreprocessEvent e) {
        String m = e.getMessage();
        if (!m.startsWith("/login") && !m.startsWith("/register")) {
            e.setCancelled(true);
        }
    }
}
