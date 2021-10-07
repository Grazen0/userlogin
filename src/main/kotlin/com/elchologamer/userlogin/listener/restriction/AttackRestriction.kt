package com.elchologamer.userlogin.listener.restriction

import com.elchologamer.userlogin.UserLogin.Companion.plugin
import com.elchologamer.userlogin.api.UserLoginAPI
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent

object AttackRestriction : BaseRestriction<EntityDamageByEntityEvent>("damage.attack") {
    override fun shouldRestrict(e: EntityDamageByEntityEvent): Boolean {
        if (!plugin.config.getBoolean("restrictions.$configKey")) return false

        return if (e.damager.type != EntityType.PLAYER) false
        else !UserLoginAPI.isLoggedIn(e.damager as Player)
    }

    @EventHandler
    fun handle(e: EntityDamageByEntityEvent) {
        if (shouldRestrict(e)) e.isCancelled = true
    }
}