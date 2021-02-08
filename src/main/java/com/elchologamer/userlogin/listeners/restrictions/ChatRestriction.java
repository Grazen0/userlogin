package com.elchologamer.userlogin.listeners.restrictions;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.util.Restriction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatRestriction extends Restriction<AsyncPlayerChatEvent> {

    private final UserLogin plugin = UserLogin.getPlugin();

    public ChatRestriction() {
        super("chat");
    }

    @EventHandler
    public void handle(AsyncPlayerChatEvent e) {
        if (!shouldRestrict(e)) return;

        e.setCancelled(true);
        plugin.getPlayer(e.getPlayer()).sendPathMessage("messages.chat-disabled");
    }
}
