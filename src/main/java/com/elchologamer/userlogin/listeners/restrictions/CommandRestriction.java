package com.elchologamer.userlogin.listeners.restrictions;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.util.Restriction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandRestriction extends Restriction<PlayerCommandPreprocessEvent> {

    private final UserLogin plugin = UserLogin.getPlugin();

    public CommandRestriction() {
        super("commands");
    }

    @EventHandler
    public void handle(PlayerCommandPreprocessEvent e) {
        if (!shouldRestrict(e)) return;

        String m = e.getMessage().replaceAll("^/", "");
        if (!m.startsWith("login") && !m.startsWith("register")) {
            e.setCancelled(true);
            plugin.getPlayer(e.getPlayer()).sendPathMessage("messages.commands_disabled");
        }
    }
}
