package com.elchologamer.userlogin.api.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RestrictionEvent<E extends Event> extends Event implements Cancellable {

    private final static HandlerList HANDLERS = new HandlerList();

    private final E restrictedEvent;
    private boolean cancelled = false;

    public RestrictionEvent(E restrictedEvent) {
        this.restrictedEvent = restrictedEvent;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
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