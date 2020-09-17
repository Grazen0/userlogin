package com.elchologamer.userlogin.listeners;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.api.UserLoginAPI;
import com.elchologamer.userlogin.util.Utils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.UUID;

public class OnPlayerQuit implements Listener {

    private final UserLogin plugin;

    public OnPlayerQuit(UserLogin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) throws NullPointerException {
        // Check if player is already logged in
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        if (!UserLoginAPI.isLoggedIn(player)) return;

        // Save the player's location
        Location loc = player.getLocation();
        ConfigurationSection section = plugin.getLocations().get().createSection("playerLocations." + uuid);

        section.set("world", Objects.requireNonNull(loc.getWorld()).getName());
        section.set("x", loc.getX());
        section.set("y", loc.getY());
        section.set("z", loc.getZ());
        section.set("yaw", loc.getYaw());
        section.set("pitch", loc.getPitch());

        plugin.getLocations().save();

        // Store IP address if enabled
        InetSocketAddress address = player.getAddress();
        if (!Utils.getConfig().getBoolean("ipRecords.enabled") || address == null) return;

        Utils.playerIP.put(uuid, address.getHostString());

        // Schedule IP deletion
        UserLogin.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(UserLogin.getPlugin(),
                () -> Utils.playerIP.put(uuid, null),
                Utils.getConfig().getLong("ipRecords.delay") * 20);
    }
}
