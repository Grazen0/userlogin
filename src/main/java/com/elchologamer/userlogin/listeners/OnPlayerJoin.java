package com.elchologamer.userlogin.listeners;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.api.UserLoginAPI;
import com.elchologamer.userlogin.commands.LoginCommand;
import com.elchologamer.userlogin.util.Path;
import com.elchologamer.userlogin.util.Utils;
import com.elchologamer.userlogin.util.reflect.Title;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.net.InetSocketAddress;
import java.util.UUID;

public class OnPlayerJoin implements Listener {

    private final UserLogin plugin;

    public OnPlayerJoin(UserLogin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        Player player = e.getPlayer();
        Utils.changeLoggedIn(player, false);

        // Teleport to login position if enabled
        if (plugin.getConfig().getBoolean("teleports.toLogin")) {
            Location loc = Utils.getLocation("login");
            if (loc != null)
                player.teleport(loc);
        }

        // IP record system
        InetSocketAddress address = player.getAddress();
        if (UserLoginAPI.isRegistered(player) && plugin.getConfig().getBoolean("ipRecords.enabled") && address != null) {
            // Check if stored address equals to player's address
            UUID uuid = player.getUniqueId();
            String recordedHost = Utils.playerIP.get(uuid);
            if (recordedHost != null && recordedHost.equals(address.getHostString())) {
                Utils.playerIP.put(uuid, null);
                new LoginCommand(plugin).login(player);
                return;
            }
        }

        // Set a new timeout
        Utils.cancelTimeout(player);
        Utils.setTimeout(player);

        UUID uuid = player.getUniqueId();

        // Send respective welcome message
        String path = UserLoginAPI.isRegistered(player) ? Path.WELCOME_LOGIN : Path.WELCOME_REGISTER;
        Utils.sendMessage(path, player);

        // Schedule repeating welcome message if enabled
        long interval = plugin.getConfig().getLong("repeatingWelcomeMsg", -1) * 20;
        if (interval >= 0) {
            Utils.repeatingMsg.put(uuid,
                    UserLogin.getPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(
                            UserLogin.getPlugin(),
                            () -> Utils.sendMessage(path, player),
                            interval, interval));
        }

        // Join title
        ConfigurationSection section = Utils.getConfig().getConfigurationSection("joinTitle");
        if (section == null || !section.getBoolean("enabled")) return;

        Title.send(
                player,
                plugin.getMessage(Path.JOIN_TITLE, ""),
                plugin.getMessage(Path.JOIN_SUBTITLE, ""),
                section.getInt("fadeIn", 10),
                section.getInt("duration", 70),
                section.getInt("fadeOut", 20));
    }
}
