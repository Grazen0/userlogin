package me.elcholostudios.userlogin.events;

import me.elcholostudios.userlogin.Essentials;
import me.elcholostudios.userlogin.files.PlayerDataFile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class OnPlayerQuit implements Listener {

    final Essentials es = new Essentials();

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent e) {
        Player player = e.getPlayer();
        String uuid = player.getUniqueId().toString();
        if(es.getConfig().getBoolean("restrictions.disableOpWhenQuit")){
            player.setOp(false);
        }

        PlayerDataFile.get().set(uuid+".isLoggedIn", false);
        PlayerDataFile.save();
    }
}
