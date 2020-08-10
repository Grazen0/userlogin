package com.elcholostudios.userlogin.events;

import com.elcholostudios.userlogin.UserLogin;
import com.elcholostudios.userlogin.util.Utils;
import com.elcholostudios.userlogin.util.lists.Path;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class OnChatMessage implements Listener {

    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (!UserLogin.plugin.getConfig().getBoolean("restrictions.chat") ||
                Utils.loggedIn.get(player.getUniqueId())) return;

        e.setCancelled(true);
        new Utils().sendMessage(Path.CHAT_DISABLED, player);
    }
}
