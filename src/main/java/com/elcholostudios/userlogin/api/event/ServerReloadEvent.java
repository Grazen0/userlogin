package com.elcholostudios.userlogin.api.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ServerReloadEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private final CommandSender source;
    private boolean isCancelled;

    public ServerReloadEvent(CommandSender source) {
        this.source = source;
        this.isCancelled = false;
    }

    public CommandSender getSource() {
        return this.source;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
