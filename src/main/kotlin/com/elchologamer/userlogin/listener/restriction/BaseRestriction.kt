package com.elchologamer.userlogin.listener.restriction

import com.elchologamer.userlogin.UserLogin.Companion.plugin
import com.elchologamer.userlogin.api.UserLoginAPI
import com.elchologamer.userlogin.api.event.RestrictionEvent
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityEvent
import org.bukkit.event.player.PlayerEvent

abstract class BaseRestriction<E : Event> protected constructor(val configKey: String) : Listener {

    open fun shouldRestrict(event: E): Boolean {
        if (
            !plugin.config.getBoolean(
                "restrictions.$configKey",
                plugin.config.getBoolean("restrictions.$configKey.enabled")
            )
        ) return false

        val restrictionEvent = RestrictionEvent(event)

        val player: Player? = if (event is PlayerEvent) {
            event.player
        } else if (event is EntityEvent && event.entityType == EntityType.PLAYER) {
            event.entity as Player
        } else {
            null
        }

        if (player == null || UserLoginAPI.isLoggedIn(player)) return false

        if (!event.isAsynchronous || restrictionEvent.isAsynchronous) {
            plugin.server.pluginManager.callEvent(restrictionEvent)
        }

        return !restrictionEvent.isCancelled
    }
}