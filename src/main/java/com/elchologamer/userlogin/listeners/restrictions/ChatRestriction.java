package com.elchologamer.userlogin.listeners.restrictions;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.util.Path;
import com.elchologamer.userlogin.util.Restriction;
import com.elchologamer.userlogin.util.Utils;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatRestriction extends Restriction<AsyncPlayerChatEvent> {

    public ChatRestriction(UserLogin plugin) {
        super("chat");
    }

    public void handle(AsyncPlayerChatEvent e) {
        e.setCancelled(true);
        Utils.sendMessage(Path.CHAT_DISABLED, e.getPlayer());
    }
}
