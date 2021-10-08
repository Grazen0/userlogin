package com.elchologamer.userlogin.listener.restriction

import com.elchologamer.userlogin.ULPlayer
import com.elchologamer.userlogin.UserLogin.Companion.plugin
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import java.util.*

object MovementRestriction : BaseRestriction<PlayerMoveEvent>("movement") {
    private val warnCoolDown = HashMap<UUID, Int>()

    override fun shouldRestrict(event: PlayerMoveEvent): Boolean =
        (event.to?.let { !(event.from sameBlockHorizontally it) } ?: false) && super.shouldRestrict(event)

    @EventHandler
    fun handle(e: PlayerMoveEvent) {
        if (!shouldRestrict(e)) return

        // Move back
        val velocityBeforeTeleport = e.player.velocity

        e.from.y = e.to!!.y
        e.from.yaw = e.to!!.yaw
        e.from.pitch = e.to!!.pitch

        e.player.teleport(e.from)
        e.player.velocity = velocityBeforeTeleport

        // Warn message
        val section = plugin.config.getConfigurationSection("restrictions") ?: return

        val frequency = section.getInt("$configKey.warnFrequency", section.getInt("moveWarnFrequency"))
        if (frequency <= 0) return

        val uuid = e.player.uniqueId
        var current = warnCoolDown[uuid] ?: 0

        if (++current >= frequency) {
            // Send warning message and reset counter
            ULPlayer[e.player].sendMessage("messages.move_warning")
            current = 0
        }

        warnCoolDown[uuid] = current
    }

    private infix fun Location.sameBlockHorizontally(other: Location) = x == other.x && z == other.z
}