package com.elchologamer.userlogin.api.event;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.api.types.AuthType;
import com.elchologamer.userlogin.util.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class AuthenticationEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean cancelled = false;

	private final Player player;
    private final AuthType type;
    private Location destination = null;
    private String targetServer = null;
    private String message;
    private String announcement;

    private AuthenticationEvent(Player player, AuthType type) {
        super(true);
		this.player = player;
        this.type = type;

        String path = "messages." + (type == AuthType.LOGIN ? "logged_in" : "registered");
        message = Utils.color(UserLogin.getPlugin().getMessage(path));

        String originalMsg = UserLogin.getPlugin().getMessage("messages.login_announcement");
        announcement = Utils.color(originalMsg.replace("{player}", player.getName()));
    }

    public AuthenticationEvent(Player player, AuthType type, String targetServer) {
        this(player, type);
        this.targetServer = targetServer;
    }

    public AuthenticationEvent(Player player, AuthType type, Location destination) {
        this(player, type);
        this.destination = destination;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
	
	public Player getPlayer() {
		return player;
	}

    public void setDestination(Location destination) {
        this.destination = destination;
    }

    public Location getDestination() {
        return destination;
    }

    public void setTargetServer(String targetServer) {
        this.targetServer = targetServer;
    }

    public String getTargetServer() {
        return targetServer;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public AuthType getType() {
        return type;
    }
}
