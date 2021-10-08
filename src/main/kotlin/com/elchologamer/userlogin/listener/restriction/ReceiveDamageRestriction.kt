package com.elchologamer.userlogin.listener.restriction

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent

object ReceiveDamageRestriction : BaseRestriction<EntityDamageEvent>("damage.receive") {
    @EventHandler
    fun handle(e: EntityDamageEvent) {
        if (shouldRestrict(e)) e.isCancelled = true
    }
}