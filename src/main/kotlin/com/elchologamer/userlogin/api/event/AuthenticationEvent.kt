package com.elchologamer.userlogin.api.event

import com.elchologamer.userlogin.UserLogin.Companion.plugin
import com.elchologamer.userlogin.api.types.AuthType
import com.elchologamer.userlogin.util.Utils
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

// TODO: Test
class AuthenticationEvent(player: Player, val type: AuthType) : PlayerEvent(player), Cancellable {
    private var cancelled = false
    var destination: Location? = null
    var targetServer: String? = null
    var message: String? = Utils.color(plugin.lang.getMessage("messages.${type.messageKey}")!!)
    var announcement = if (plugin.config.getBoolean("loginBroadcast")) {
        plugin.lang.getMessage("messages.login_announcement")?.replace("{player}", player.name)
    } else {
        null
    }

    companion object {
        val HANDLERS = HandlerList()
    }

    constructor(player: Player, type: AuthType, targetServer: String?) : this(player, type) {
        this.targetServer = targetServer
    }

    constructor(player: Player, type: AuthType, destination: Location?) : this(player, type) {
        this.destination = destination
    }

    override fun getHandlers() = HANDLERS

    override fun isCancelled() = cancelled

    override fun setCancelled(cancelled: Boolean) {
        this.cancelled = cancelled
    }
}