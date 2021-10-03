package com.elchologamer.userlogin;

import com.elchologamer.userlogin.util.Utils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Map;

public class ULPlayer {

    private final UserLogin plugin = UserLogin.getPlugin();
    private Player player;
    private boolean loggedIn = false;
    private Integer timeout = null;
    private Integer welcomeMessage = null;
    private String ip = null;

    public ULPlayer(Player player) {
        this.player = player;
    }

    public void activateTimeout() {
        if (loggedIn) return;

        BukkitScheduler scheduler = player.getServer().getScheduler();
        if (timeout != null) scheduler.cancelTask(timeout);

        if (!plugin.getConfig().getBoolean("timeout.enabled")) return;
        cancelTimeout();

        timeout = scheduler.scheduleSyncDelayedTask(
                plugin,
                () -> player.kickPlayer(plugin.getLang().getMessage("messages.timeout")),
                plugin.getConfig().getLong("timeout.time") * 20
        );
    }

    public void cancelTimeout() {
        if (timeout != null)
            player.getServer().getScheduler().cancelTask(timeout);
    }

    public void activateRepeatingMessage(String messagePath) {
        if (loggedIn) return;

        long interval = plugin.getConfig().getLong("repeatingWelcomeMsg", -1) * 20;
        if (interval <= 0) return;

        cancelRepeatingMessage();

        welcomeMessage = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(
                plugin,
                () -> sendMessage(messagePath),
                interval, interval
        );
    }

    public void cancelRepeatingMessage() {
        if (welcomeMessage != null)
            player.getServer().getScheduler().cancelTask(welcomeMessage);
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

    public void changeServer(String target) {
        Utils.sendPluginMessage(player, "BungeeCord", "Connect", target);
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public void setIP(String ip) {
        this.ip = ip;
    }

    public String getIP() {
        return ip;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
