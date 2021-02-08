package com.elchologamer.userlogin.listeners;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.util.BaseListener;
import com.elchologamer.userlogin.util.CustomConfig;
import com.elchologamer.userlogin.util.extensions.ULPlayer;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.net.InetSocketAddress;
import java.util.UUID;

public class OnPlayerQuit extends BaseListener {

    private final UserLogin plugin = UserLogin.getPlugin();

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) throws NullPointerException {
        // Check if player is already logged in
        Player player = e.getPlayer();
        ULPlayer ulPlayer = plugin.getPlayer(player);

        if (!ulPlayer.isLoggedIn()) return;

        if (plugin.getConfig().getBoolean("teleports.savePosition")) {
            // Save the player's location
            UUID uuid = player.getUniqueId();
            Location loc = player.getLocation();

            CustomConfig locationsConfig = plugin.getLocationsManager().getConfig();
            ConfigurationSection section = locationsConfig.get()
                    .createSection("playerLocations." + uuid);

            section.set("world", loc.getWorld().getName());
            section.set("x", loc.getX());
            section.set("y", loc.getY());
            section.set("z", loc.getZ());
            section.set("yaw", loc.getYaw());
            section.set("pitch", loc.getPitch());

            locationsConfig.save();
        }

        // Store IP address if enabled
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("ipRecords");
        if (section == null) return;

        InetSocketAddress address = player.getAddress();
        if (!section.getBoolean("enabled") || address == null) return;

        ulPlayer.setIP(address.getHostString());

        // Schedule IP deletion
        long delay = section.getLong("delay", 10);
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(
                plugin,
                () -> ulPlayer.setIP(null),
                delay * 20
        );
    }
}
