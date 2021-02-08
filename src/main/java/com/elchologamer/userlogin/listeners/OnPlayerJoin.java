package com.elchologamer.userlogin.listeners;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.api.UserLoginAPI;
import com.elchologamer.userlogin.api.types.AuthType;
import com.elchologamer.userlogin.commands.AuthCommand;
import com.elchologamer.userlogin.util.BaseListener;
import com.elchologamer.userlogin.util.extensions.QuickMap;
import com.elchologamer.userlogin.util.extensions.ULPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.net.InetSocketAddress;

public class OnPlayerJoin extends BaseListener {

    private final UserLogin plugin = UserLogin.getPlugin();

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
                    AuthCommand.login(ulPlayer, AuthType.LOGIN);
                    return;
                }
            }
        }

        ulPlayer.activateTimeout();

        // Send respective welcome message
        String path = "messages.welcome." + (UserLoginAPI.isRegistered(p) ? "registered" : "unregistered");
        ulPlayer.sendPathMessage(path, new QuickMap<>("player", p.getName()));

        ulPlayer.activateRepeatingMessage(path);
    }
}
