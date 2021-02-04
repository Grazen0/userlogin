package com.elchologamer.userlogin.util.player;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.util.Path;
import com.elchologamer.userlogin.util.Utils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public class ULPlayer {

    private final Player player;
    private boolean loggedIn = false;
    private Integer timeout = null;
    private Integer welcomeMessage = null;
    private String ip = null;

    public ULPlayer(Player player) {
        this.player = player;
    }

    public void activateTimeout() {
        BukkitScheduler scheduler = player.getServer().getScheduler();
        if (timeout != null) scheduler.cancelTask(timeout);

        if (!Utils.getConfig().getBoolean("timeout.enabled")) return;

        UserLogin plugin = UserLogin.getPlugin();
        timeout = scheduler.scheduleSyncDelayedTask(
                plugin,
                () -> player.kickPlayer(plugin.getMessage(Path.TIMEOUT)),
                plugin.getConfig().getLong("timeout.time") * 20
        );
    }

    public void cancelTimeout() {
        if (timeout != null)
            player.getServer().getScheduler().cancelTask(timeout);
    }

    public void activateRepeatingMessage(String messagePath) {
        UserLogin plugin = UserLogin.getPlugin();
        long interval = plugin.getConfig().getLong("repeatingWelcomeMsg", -1) * 20;
        if (interval <= 0) return;

        welcomeMessage = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(
                plugin,
                () -> Utils.sendMessage(messagePath, player),
                interval, interval
        );
    }

    public void cancelRepeatingMessage() {
        if (welcomeMessage != null)
            player.getServer().getScheduler().cancelTask(welcomeMessage);
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
}
