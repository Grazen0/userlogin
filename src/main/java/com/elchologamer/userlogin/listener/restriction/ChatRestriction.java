package com.elchologamer.userlogin.listener.restriction;

import com.elchologamer.userlogin.UserLogin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatRestriction extends BaseRestriction<AsyncPlayerChatEvent> {

    private final UserLogin plugin = UserLogin.getPlugin();

    public ChatRestriction() {
        super("chat");
    }

    @EventHandler
    public void handle(AsyncPlayerChatEvent e) {
        if (!shouldRestrict(e)) return;

        e.setCancelled(true);
        plugin.getPlayer(e.getPlayer()).sendMessage("messages.chat_disabled");
    }
}
