package me.elcholostudios.userlogin.events;

import me.elcholostudios.userlogin.Essentials;
import me.elcholostudios.userlogin.UserLogin;
import me.elcholostudios.userlogin.files.PlayerDataFile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

public class OnPlayerChat implements Listener {

    final Essentials es = new Essentials();

    @EventHandler
    public void onPlayerChat(@NotNull AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String uuid = player.getUniqueId().toString();

        boolean block = UserLogin.plugin.getConfig().getBoolean("restrictions.disableChatBeforeLogin");
        if(!PlayerDataFile.get().getBoolean(uuid+".isLoggedIn") && block) {
            e.setCancelled(true);
            es.sendMessage(player, "display-messages.log-in-first", null, null);
        }
    }
}
