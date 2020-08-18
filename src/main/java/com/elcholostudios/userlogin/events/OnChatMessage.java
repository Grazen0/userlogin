package com.elcholostudios.userlogin.events;

import com.elcholostudios.userlogin.util.Utils;
import com.elcholostudios.userlogin.util.lists.Path;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

public class OnChatMessage implements Listener {

    private final Utils utils = new Utils();

    @EventHandler
    public void onChatMessage(@NotNull AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (!utils.getConfig().getBoolean("restrictions.chat") ||
                Utils.loggedIn.get(player.getUniqueId())) return;

        e.setCancelled(true);
        new Utils().sendMessage(Path.CHAT_DISABLED, player);
    }
}
