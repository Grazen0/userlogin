package com.elchologamer.userlogin.api.event

import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class RestrictionEvent<E : Event>(val restrictedEvent: E) : Event(), Cancellable {
    private var cancelled = false

    companion object {
        val HANDLERS = HandlerList()
    }

    override fun isCancelled() = cancelled

    override fun setCancelled(cancelled: Boolean) {
        this.cancelled = cancelled
    }

    override fun getHandlers() = HANDLERS
}