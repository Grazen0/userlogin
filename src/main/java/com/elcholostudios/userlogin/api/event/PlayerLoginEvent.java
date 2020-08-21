package com.elcholostudios.userlogin.api.event;

import com.elcholostudios.userlogin.UserLogin;
import com.elcholostudios.userlogin.api.event.enums.DestinationType;
import com.elcholostudios.userlogin.api.event.enums.LoginType;
import com.elcholostudios.userlogin.util.Utils;
import com.elcholostudios.userlogin.util.lists.Location;
import com.elcholostudios.userlogin.util.lists.Path;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PlayerLoginEvent extends PlayerEvent implements Cancellable {

    protected static final HandlerList HANDLERS_LIST = new HandlerList();
    protected final Utils utils = new Utils();
    protected String message;
    protected String announcement;
    protected boolean cancelled;
    protected org.bukkit.Location destinationWorld;
    protected String destinationServer;
    protected DestinationType destinationType;
    protected final LoginType loginType;

    public PlayerLoginEvent(@NotNull Player player, LoginType loginType) {
        super(player);
        this.loginType = loginType;
        this.cancelled = false;
        this.message = utils.color(Objects.requireNonNull(UserLogin.messagesFile.get().
                getString(this.loginType == LoginType.LOGIN ? Path.LOGGED_IN : Path.REGISTERED)));

        this.announcement = utils.color(Objects.requireNonNull(UserLogin.messagesFile.get().getString(
                Path.LOGIN_ANNOUNCEMENT)).replace("{player}", this.player.getName()));

        this.destinationWorld = utils.normalMode() ? utils.getLocation(Location.SPAWN) :
                utils.getLocation("playerLocations." + player.getUniqueId().toString());
        if (utils.normalMode() && !utils.getConfig().getBoolean("teleports.toSpawn"))
            this.destinationWorld = null;

        this.destinationType = !utils.getConfig().getBoolean("bungeeCord.enabled") ?
                DestinationType.NORMAL : DestinationType.BUNGEECORD;

        this.destinationServer = utils.getConfig().getString("bungeeCord.spawnServer");
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAnnouncement() {
        return this.announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public @Nullable org.bukkit.Location getDestinationWorld() {
        return this.destinationWorld;
    }

    public void setDestinationWorld(@Nullable org.bukkit.Location destinationWorld) {
        this.destinationWorld = destinationWorld;
    }

    public String getDestinationServer() {
        return this.destinationServer;
    }

    public void setDestinationServer(@NotNull String destinationServer) {
        this.destinationServer = destinationServer;
    }

    public DestinationType getDestinationType() {
        return this.destinationType;
    }

    public void setDestinationType(DestinationType type) {
        this.destinationType = type;
    }

    public LoginType getLoginType() {
        return this.loginType;
    }
}
