package com.elchologamer.userlogin.listener.restriction

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityPickupItemEvent

object ItemPickupRestriction : BaseRestriction<EntityPickupItemEvent>("itemPickup") {
    @EventHandler
    fun handle(e: EntityPickupItemEvent) {
        if (shouldRestrict(e)) e.isCancelled = true
    }
}