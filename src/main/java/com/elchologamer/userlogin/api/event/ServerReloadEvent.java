package com.elchologamer.userlogin.api.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class ServerReloadEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private final CommandSender source;
    private boolean cancelled;

    /**
     * Event called when a server reload occurs.
     *
     * @param source The sender of the reload command.
     */
    public ServerReloadEvent(CommandSender source) {
        this.source = source;
        this.cancelled = false;
    }

    /**
     * Gets the handlers for this event.
     *
     * @return This event's handler list.
     */
    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    /**
     * Gets the sender/source of the command.
     *
     * @return Source of the reload command.
     */
    public CommandSender getSource() {
        return this.source;
    }

    /**
     * Gets the handlers for this event.
     *
     * @return This event's handler list.
     */
    @Override
    public @NotNull HandlerList getHandlers() {
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
}
