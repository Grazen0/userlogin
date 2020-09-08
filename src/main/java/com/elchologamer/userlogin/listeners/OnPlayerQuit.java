package com.elchologamer.userlogin.listeners;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.util.Utils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.UUID;

public class OnPlayerQuit implements Listener {

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent e) throws NullPointerException {
        // Check if player is already logged in
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        if (!Utils.loggedIn.get(uuid)) return;

        // Save the player's location
        Location loc = player.getLocation();
        ConfigurationSection section = UserLogin.locationsFile.get().createSection("playerLocations." + uuid);

        section.set("world", Objects.requireNonNull(loc.getWorld()).getName());
        section.set("x", loc.getX());
        section.set("y", loc.getY());
        section.set("z", loc.getZ());
        section.set("yaw", loc.getYaw());
        section.set("pitch", loc.getPitch());

        UserLogin.locationsFile.save();

        // Store IP address if enabled
        InetSocketAddress address = player.getAddress();
        if (!Utils.getConfig().getBoolean("ipRecords.enabled") || address != null) return;
        Utils.playerIP.put(uuid, address.getHostString());

        // Schedule IP deletion
        UserLogin.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(UserLogin.getPlugin(),
                () -> Utils.playerIP.put(uuid, null),
                Utils.getConfig().getLong("ipRecords.delay") * 20);
    }
}
