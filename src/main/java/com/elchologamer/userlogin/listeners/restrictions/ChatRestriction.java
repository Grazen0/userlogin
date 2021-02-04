package com.elchologamer.userlogin.listeners.restrictions;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.util.Path;
import com.elchologamer.userlogin.util.Restriction;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatRestriction extends Restriction<AsyncPlayerChatEvent> {

    private final UserLogin plugin;

    public ChatRestriction() {
        super("chat");
        plugin = UserLogin.getPlugin();
    }

    public void handle(AsyncPlayerChatEvent e) {
        e.setCancelled(true);

        plugin.getPlayer(e.getPlayer()).sendPathMessage(Path.CHAT_DISABLED);
    }
}
