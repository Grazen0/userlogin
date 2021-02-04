package com.elchologamer.userlogin.listeners;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.util.Utils;
import com.elchologamer.userlogin.util.player.ULPlayer;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.net.InetSocketAddress;
import java.util.UUID;

public class OnPlayerQuit implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) throws NullPointerException {
        UserLogin plugin = UserLogin.getPlugin();

        // Check if player is already logged in
        Player player = e.getPlayer();
        ULPlayer ulPlayer = plugin.getPlayerManager().get(player);

        if (!ulPlayer.isLoggedIn()) return;

        if (Utils.getConfig().getBoolean("teleports.savePosition")) {
            // Save the player's location
            UUID uuid = player.getUniqueId();
            Location loc = player.getLocation();
            ConfigurationSection section = plugin.getLocations().get().createSection("playerLocations." + uuid);

            section.set("world", loc.getWorld().getName());
            section.set("x", loc.getX());
            section.set("y", loc.getY());
            section.set("z", loc.getZ());
            section.set("yaw", loc.getYaw());
            section.set("pitch", loc.getPitch());

            plugin.getLocations().save();
        }

        // Store IP address if enabled
        InetSocketAddress address = player.getAddress();
        if (!Utils.getConfig().getBoolean("ipRecords.enabled") || address == null) return;

        ulPlayer.setIP(address.getHostString());

        // Schedule IP deletion
        long delay = Utils.getConfig().getLong("ipRecords.delay", 10);
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(
                plugin,
                () -> ulPlayer.setIP(null),
                delay * 20
        );
    }
}
