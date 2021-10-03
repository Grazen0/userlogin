package com.elchologamer.userlogin.listener;

import com.elchologamer.userlogin.UserLogin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnPlayerQuit extends BaseListener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        UserLogin.getPlugin().getPlayer(e.getPlayer()).onQuit();
    }
}
