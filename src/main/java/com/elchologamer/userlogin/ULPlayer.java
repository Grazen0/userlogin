package com.elchologamer.userlogin;

import com.elchologamer.userlogin.api.UserLoginAPI;
import com.elchologamer.userlogin.api.event.AuthenticationEvent;
import com.elchologamer.userlogin.api.types.AuthType;
import com.elchologamer.userlogin.manager.LocationsManager;
import com.elchologamer.userlogin.util.QuickMap;
import com.elchologamer.userlogin.util.Utils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitScheduler;

import java.net.InetSocketAddress;
import java.util.Map;

public class ULPlayer {

    private final UserLogin plugin = UserLogin.getPlugin();

    private Player player;
    private boolean loggedIn = false;
    private int timeout = -1;
    private int welcomeMessage = -1;
    private String ip = null;

    public ULPlayer(Player player) {
        this.player = player;
    }

    public void onJoin(PlayerJoinEvent event) {
        loggedIn = false;
        if (event != null) {
            this.player = event.getPlayer(); // Replace with newly joined player
        }

        // Teleport to login position
        if (plugin.getConfig().getBoolean("teleports.toLogin")) {
            Location loc = plugin.getLocationsManager().getLocation(
                    "login",
                    player.getWorld().getSpawnLocation()
            );

            player.teleport(loc);
        }

        // Bypass if IP is registered
        if (ip != null && plugin.getConfig().getBoolean("ipRecords.enabled")) {
            InetSocketAddress address = player.getAddress();

            if (address != null) {
                if (address.getHostString().equals(ip)) {
                    ip = null;
                    onAuthenticate(AuthType.LOGIN);
                    return;
                }
            }
        }

        schedulePreLoginTasks();
        sendWelcomeMessage();
    }

    public void onQuit() {
        if (!loggedIn) {
            cancelPreLoginTasks();
        } else {
            loggedIn = false;

            if (plugin.getConfig().getBoolean("teleports.savePosition")) {
                plugin.getLocationsManager().savePlayerLocation(player);
            }

            // Store IP address if enabled
            if (plugin.getConfig().getBoolean("ipRecords.enabled")) {
                InetSocketAddress address = player.getAddress();

                if (address != null) {
                    ip = address.getHostString();

                    // Schedule IP deletion
                    long delay = plugin.getConfig().getLong("ipRecords.delay", 10);
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(
                            plugin,
                            () -> ip = null,
                            delay * 20
                    );
                }
            }
        }
    }

    public void onAuthenticate(AuthType type) {
        // Teleport player
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection teleports = config.getConfigurationSection("teleports");
        assert teleports != null;

        // Call event
        AuthenticationEvent event;
        boolean bungeeEnabled = config.getBoolean("bungeeCord.enabled");

        if (bungeeEnabled) {
            String targetServer = config.getString("bungeeCord.spawnServer");
            event = new AuthenticationEvent(player, type, targetServer);
        } else {
            Location target = null;
            LocationsManager manager = plugin.getLocationsManager();

            if (teleports.getBoolean("savePosition")) {
                target = manager.getPlayerLocation(player, player.getWorld().getSpawnLocation());
            } else if (teleports.getBoolean("toSpawn", true)) {
                target = manager.getLocation("spawn", player.getWorld().getSpawnLocation());
            }

            event = new AuthenticationEvent(player, type, target);
        }

        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        cancelPreLoginTasks();

        // Send login message
        String loginMessage = event.getMessage();
        if (loginMessage != null) player.sendMessage(loginMessage);

        // Join announcement
        String announcement = event.getAnnouncement();
        if (announcement != null) {
            for (Player onlinePlayer : player.getServer().getOnlinePlayers()) {
                if (UserLoginAPI.isLoggedIn(player)) {
                    onlinePlayer.sendMessage(announcement);
                }
            }
        }

        loggedIn = true;

        // Teleport to destination
        Location targetLocation = event.getDestination();
        String targetServer = event.getTargetServer();

        if (bungeeEnabled && targetServer != null) {
            Utils.sendPluginMessage(player, "BungeeCord", "Connect", targetServer);
        } else if (targetLocation != null) {
            player.teleport(targetLocation);
        }
    }

    private void sendWelcomeMessage() {
        String path = "messages.welcome." + (UserLoginAPI.isRegistered(player) ? "registered" : "unregistered");
        sendMessage(path, new QuickMap<>("player", player.getName()));
    }

    public void schedulePreLoginTasks() {
        BukkitScheduler scheduler = player.getServer().getScheduler();

        // Timeout
        if (plugin.getConfig().getBoolean("timeout.enabled", true)) {
            long timeoutDelay = plugin.getConfig().getLong("timeout.time");

            timeout = scheduler.scheduleSyncDelayedTask(
                    plugin,
                    () -> player.kickPlayer(plugin.getLang().getMessage("messages.timeout")),
                    timeoutDelay * 20
            );
        }


        // Repeating welcome message
        long interval = plugin.getConfig().getLong("repeatingWelcomeMsg", -1) * 20;
        if (interval > 0) {
            welcomeMessage = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(
                    plugin,
                    this::sendWelcomeMessage,
                    interval, interval
            );
        }
    }

    public void cancelPreLoginTasks() {
        if (timeout != -1) {
            player.getServer().getScheduler().cancelTask(timeout);
            timeout = -1;
        }

        if (welcomeMessage != -1) {
            player.getServer().getScheduler().cancelTask(welcomeMessage);
            welcomeMessage = -1;
        }
    }

    public void sendMessage(String path) {
        sendMessage(path, null);
    }

    public void sendMessage(String path, Map<String, Object> replace) {
        String message = plugin.getLang().getMessage(path);
        if (message == null || message.length() == 0) return;

        if (replace != null) {
            for (String k : replace.keySet()) {
                message = message.replace("{" + k + "}", replace.get(k).toString());
            }
        }

        player.sendMessage(Utils.color(message));
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public Player getPlayer() {
        return player;
    }
}
