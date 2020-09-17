package com.elchologamer.userlogin.api.event;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.api.enums.DestinationType;
import com.elchologamer.userlogin.api.enums.LoginType;
import com.elchologamer.userlogin.util.Path;
import com.elchologamer.userlogin.util.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

@SuppressWarnings("unused")
public class PlayerLoginEvent extends PlayerEvent implements Cancellable {

    protected static final HandlerList HANDLERS_LIST = new HandlerList();
    protected final LoginType loginType;
    protected String message;
    protected String announcement;
    protected boolean cancelled;
    protected org.bukkit.Location destinationLoc;
    protected String destinationServer;
    protected DestinationType destinationType;

    /**
     * Event called whenever a player successfully
     * logs in or gets registered onto the server.
     *
     * @param player    The logged-in player.
     * @param loginType The type of login, can be either LOGIN or REGISTER.
     */
    public PlayerLoginEvent(Player player, LoginType loginType) {
        super(player);
        this.loginType = loginType;
        cancelled = false;
        message = Utils.color(UserLogin.getPlugin().getMessage(loginType == LoginType.LOGIN ? Path.LOGGED_IN : Path.REGISTERED));

        announcement = UserLogin.getPlugin().getMessage(Path.LOGIN_ANNOUNCEMENT)
                .replace("{player}", player.getName());

        destinationLoc = Utils.normalMode() ? Utils.getLocation("spawn") :
                Utils.getLocation("playerLocations." + player.getUniqueId().toString());
        if (Utils.normalMode() && !Utils.getConfig().getBoolean("teleports.toSpawn"))
            this.destinationLoc = null;

        destinationType = !Utils.getConfig().getBoolean("bungeeCord.enabled") ?
                DestinationType.NORMAL : DestinationType.BUNGEECORD;

        destinationServer = Utils.getConfig().getString("bungeeCord.spawnServer");
    }

    /**
     * Gets the handlers for this event.
     *
     * @return This event's handler list.
     */
    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    /**
     * Gets the handlers for this event.
     *
     * @return This event's handler list.
     */
    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    /**
     * Checks if the event is currently canceled.
     *
     * @return True if the event is cancelled, false otherwise.
     */
    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    /**
     * Sets the cancelled state of the event.
     *
     * @param cancel Value to assign to the cancelled state.
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    /**
     * Gets the message that will be sent to the player.
     *
     * @return Current login/register message.
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Sets the message that will be sent to the player.
     *
     * @param message New message.
     */
    @SuppressWarnings("unused")
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the announcement message that will be sent to other players.
     *
     * @return Current announcement message.
     */
    public String getAnnouncement() {
        return this.announcement;
    }

    /**
     * Sets the announcement message that will be sent to other players.
     *
     * @param announcement New announcement message.
     */
    @SuppressWarnings("unused")
    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    /**
     * Gets the teleport destination for the player.
     *
     * @return Current destination location.
     */
    public org.bukkit.Location getDestinationLoc() {
        return this.destinationLoc;
    }

    /**
     * Sets the teleport destination for the player.
     *
     * @param destinationLoc New destination.
     */
    public void setDestinationLoc(org.bukkit.Location destinationLoc) {
        this.destinationLoc = destinationLoc;
    }

    /**
     * Gets the destination server's name for the player. (BungeeCord)
     *
     * @return Current destination server.
     */
    public String getDestinationServer() {
        return this.destinationServer;
    }

    /**
     * Sets the destination server for the player. (BungeeCord)
     *
     * @param destinationServer New destination server.
     */
    @SuppressWarnings("unused")
    public void setDestinationServer(String destinationServer) {
        this.destinationServer = destinationServer;
    }

    /**
     * Gets the destination type for the player, can be either NORMAL or BUNGEECORD.
     *
     * @return Current destination type.
     */
    public DestinationType getDestinationType() {
        return this.destinationType;
    }

    /**
     * Sets the destination type for the player's teleport.
     *
     * @param type New destination type.
     */
    @SuppressWarnings("unused")
    public void setDestinationType(DestinationType type) {
        this.destinationType = type;
    }

    /**
     * Gets the login type of the player, can be either LOGIN or REGISTER.
     *
     * @return Login type of the player.
     */
    @SuppressWarnings("unused")
    public LoginType getLoginType() {
        return this.loginType;
    }
}
