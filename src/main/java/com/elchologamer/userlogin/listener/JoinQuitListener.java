package com.elchologamer.userlogin.listener;

import com.elchologamer.userlogin.ULPlayer;
import com.elchologamer.userlogin.UserLogin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {

    public JoinQuitListener() {
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (UserLogin.getPlugin().getConfig().getBoolean("disableVanillaJoinMessages")) {
            event.setJoinMessage(null);
        }
        ULPlayer.get(event.getPlayer()).onJoin(false);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        ULPlayer.get(event.getPlayer()).onQuit();
    }
}