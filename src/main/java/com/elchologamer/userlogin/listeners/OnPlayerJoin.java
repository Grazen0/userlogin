package com.elchologamer.userlogin.listeners;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.commands.Login;
import com.elchologamer.userlogin.util.Utils;
import com.elchologamer.userlogin.util.lists.Path;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.UUID;

public class OnPlayerJoin implements Listener {

    private final Utils utils = new Utils();

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent e) {
        e.setJoinMessage(null);
        Player player = e.getPlayer();
        Utils.loggedIn.put(player.getUniqueId(), false);

        // Teleport to login position if enabled
        if (utils.getConfig().getBoolean("teleports.toLogin")) {
            Location loc = utils.getLocation(com.elchologamer.userlogin.util.lists.Location.LOGIN);
            if (loc != null)
                player.teleport(loc);
        }

        // IP record system
        InetSocketAddress address = player.getAddress();
        if (utils.isRegistered(player) &&  utils.getConfig().getBoolean("ipRecords.enabled") && address != null) {
            // Check if stored address equals to player's address
            UUID uuid = player.getUniqueId();
            String recordedHost = Utils.playerIP.get(uuid);
            if (recordedHost != null && recordedHost.equals(address.getHostString())) {
                Utils.playerIP.put(uuid, null);
                new Login().login(player);
                return;
            }
        }

        // Set a new timeout
        utils.cancelTimeout(player);
        if (utils.getConfig().getBoolean("timeout.enabled")) {
            int id = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(
                    UserLogin.plugin,
                    () -> player.kickPlayer(UserLogin.messagesFile.get().getString(Path.TIMEOUT)),
                    utils.getConfig().getInt("timeout.time") * 20);

            Utils.timeouts.put(player.getUniqueId(), id);
        }

        // Send respective welcome message
        if (utils.isRegistered(player))
            utils.sendMessage(Path.WELCOME_LOGIN, player);
        else
            utils.sendMessage(Path.WELCOME_REGISTER, player);
    }
}
