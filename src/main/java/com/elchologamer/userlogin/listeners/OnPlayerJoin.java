package com.elchologamer.userlogin.listeners;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.commands.Login;
import com.elchologamer.userlogin.util.Utils;
import com.elchologamer.userlogin.util.lists.Path;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.UUID;

public class OnPlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent e) {
        e.setJoinMessage(null);
        Player player = e.getPlayer();
        Utils.loggedIn.put(player.getUniqueId(), false);

        // Teleport to login position if enabled
        if (Utils.getConfig().getBoolean("teleports.toLogin")) {
            Location loc = Utils.getLocation(com.elchologamer.userlogin.util.lists.Location.LOGIN);
            if (loc != null)
                player.teleport(loc);
        }

        // IP record system
        InetSocketAddress address = player.getAddress();
        if (Utils.isRegistered(player) && Utils.getConfig().getBoolean("ipRecords.enabled") && address != null) {
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
        Utils.cancelTimeout(player);
        if (Utils.getConfig().getBoolean("timeout.enabled")) {
            int id = UserLogin.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(
                    UserLogin.getPlugin(),
                    () -> player.kickPlayer(UserLogin.messagesFile.get().getString(Path.TIMEOUT)),
                    Utils.getConfig().getInt("timeout.time") * 20);

            Utils.timeouts.put(player.getUniqueId(), id);
        }

        // Send respective welcome message
        if (Utils.isRegistered(player)) {
            Utils.sendMessage(Path.WELCOME_LOGIN, player);
        } else {
            Utils.sendMessage(Path.WELCOME_REGISTER, player);
        }

        ConfigurationSection section = Utils.getConfig().getConfigurationSection("joinTitle");
        if (section.getBoolean("enabled", false))
            player.sendTitle(
                    Utils.color(UserLogin.messagesFile.get().getString(Path.JOIN_TITLE)),
                    Utils.color(UserLogin.messagesFile.get().getString(Path.JOIN_SUBTITLE)),
                    section.getInt("fadeIn"),
                    section.getInt("duration"),
                    section.getInt("fadeOut"));
    }
}
