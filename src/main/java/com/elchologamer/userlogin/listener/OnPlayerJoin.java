package com.elchologamer.userlogin.listener;

import com.elchologamer.userlogin.ULPlayer;
import com.elchologamer.userlogin.UserLogin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnPlayerJoin extends BaseListener {

    private final UserLogin plugin = UserLogin.getPlugin();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);

        ULPlayer ulPlayer = plugin.getPlayers().getOrCreate(e.getPlayer());
        ulPlayer.onJoin(e);
    }
}
