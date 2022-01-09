package com.elchologamer.userlogin.api.event;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.api.types.AuthType;
import com.elchologamer.userlogin.util.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class AuthenticationEvent extends Event implements Cancellable {

    private final UserLogin plugin = UserLogin.getPlugin();

    private static final HandlerList HANDLERS = new HandlerList();

    private final AuthType type;
    private final Player player;
    private boolean cancelled = false;
    private Location destination = null;
    private String targetServer = null;
    private String message;
    private String announcement = null;

    private AuthenticationEvent(Player player, AuthType type) {
        super(true);
        this.player = player;
        this.type = type;
        message = Utils.color(plugin.getLang().getMessage("messages." + type.getMessageKey()));

        if (plugin.getConfig().getBoolean("loginBroadcast")) {
            announcement = plugin.getLang().getMessage("messages.login_announcement").replace("{player}", player.getName());
        }
    }

    public AuthenticationEvent(Player player, AuthType type, String targetServer) {
        this(player, type);
        this.targetServer = targetServer;
    }

    public AuthenticationEvent(Player player, AuthType type, Location destination) {
        this(player, type);
        this.destination = destination;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public AuthType getType() {
        return type;
    }


    public Player getPlayer() {
        return player;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTargetServer() {
        return targetServer;
    }

    public void setTargetServer(String targetServer) {
        this.targetServer = targetServer;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public Location getDestination() {
        return destination;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}