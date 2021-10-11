package com.elchologamer.userlogin.listener.restriction;

import com.elchologamer.userlogin.ULPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandRestriction extends BaseRestriction<PlayerCommandPreprocessEvent> {

    public CommandRestriction() {
        super("commands");
    }

    @EventHandler
    public void handle(PlayerCommandPreprocessEvent e) {
        if (!shouldRestrict(e)) return;

        String m = e.getMessage().replaceAll("^/", "").toLowerCase();
        if (!m.startsWith("login") && !m.startsWith("register")) {
            e.setCancelled(true);
            ULPlayer.get(e.getPlayer()).sendMessage("messages.commands_disabled");
        }
    }
}