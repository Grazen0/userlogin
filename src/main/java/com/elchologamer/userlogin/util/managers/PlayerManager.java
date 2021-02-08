package com.elchologamer.userlogin.util.managers;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.util.extensions.ULPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.UUID;

public class PlayerManager extends HashMap<UUID, ULPlayer> implements Listener {

    public PlayerManager() {
        UserLogin plugin = UserLogin.getPlugin();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void clear() {
        for (ULPlayer p : values()) {
            p.cancelTimeout();
            p.cancelRepeatingMessage();
            p.setIP(null);
        }

        super.clear();
    }

    public ULPlayer get(Player player) {
        UUID uuid = player.getUniqueId();
        if (containsKey(uuid)) return super.get(uuid);

        ULPlayer newPlayer = new ULPlayer(player);
        put(uuid, newPlayer);

        return newPlayer;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();

        if (containsKey(uuid)) get(uuid).setPlayer(p);
    }
}
