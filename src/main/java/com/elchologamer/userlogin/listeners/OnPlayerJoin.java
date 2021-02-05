package com.elchologamer.userlogin.listeners;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.api.UserLoginAPI;
import com.elchologamer.userlogin.commands.AuthCommand;
import com.elchologamer.userlogin.util.Path;
import com.elchologamer.userlogin.util.extensions.QuickMap;
import com.elchologamer.userlogin.util.extensions.ULPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.net.InetSocketAddress;

public class OnPlayerJoin implements Listener {

    private final UserLogin plugin;

    public OnPlayerJoin() {
        plugin = UserLogin.getPlugin();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);

        Player p = e.getPlayer();
        ULPlayer ulPlayer = plugin.getPlayer(p);

        ulPlayer.setLoggedIn(false);

        // Teleport to login position if enabled
        if (plugin.getConfig().getBoolean("teleports.toLogin")) {
            Location loc = plugin.getLocationsManager().getLocation(
                    "login",
                    p.getWorld().getSpawnLocation()
            );

            p.teleport(loc);
        }

        // Bypass if IP is registered
        if (UserLoginAPI.isRegistered(p) && plugin.getConfig().getBoolean("ipRecords.enabled")) {
            InetSocketAddress address = p.getAddress();

            if (address != null) {
                // Check if stored address equals to player's address
                String storedIP = ulPlayer.getIP();

                if (address.getHostString().equals(storedIP)) {
                    ulPlayer.setIP(null);
                    AuthCommand.login(ulPlayer, plugin);
                    return;
                }
            }
        }

        ulPlayer.activateTimeout();

        // Send respective welcome message
        String path = UserLoginAPI.isRegistered(p) ? Path.WELCOME_LOGIN : Path.WELCOME_REGISTER;
        ulPlayer.sendPathMessage(path, new QuickMap<>("player", p.getName()));

        ulPlayer.activateRepeatingMessage(path);
    }
}
