package com.elchologamer.userlogin.listener.restriction;

import com.elchologamer.userlogin.ULPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatRestriction extends BaseRestriction<AsyncPlayerChatEvent> {

    public ChatRestriction() {
        super("chat");
    }

    @EventHandler
    public void handle(AsyncPlayerChatEvent e) {
        if (!shouldRestrict(e)) return;

        e.setCancelled(true);
        ULPlayer.get(e.getPlayer()).sendMessage("messages.chat_disabled");
    }
}