package com.elchologamer.userlogin

import com.elchologamer.userlogin.UserLogin.Companion.plugin
import com.elchologamer.userlogin.api.UserLoginAPI
import com.elchologamer.userlogin.api.event.AuthenticationEvent
import com.elchologamer.userlogin.api.types.AuthType
import com.elchologamer.userlogin.manager.LangManager
import com.elchologamer.userlogin.manager.LocationsManager
import com.elchologamer.userlogin.util.QuickMap
import com.elchologamer.userlogin.util.Utils
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

class ULPlayer private constructor(val uuid: UUID) {
    var loggedIn = false
        private set

    private var timeout = -1
    private var welcomeMessage = -1
    private var ipForgor = -1
    private var ip: String? = null

    val player
        get() = plugin.server.getPlayer(uuid) ?: throw IllegalArgumentException("Player with UUID $uuid not found")

    companion object {
        val players = HashMap<UUID, ULPlayer>()

        operator fun get(uuid: UUID) = players[uuid] ?: ULPlayer(uuid)
        operator fun get(player: Player) = get(player.uniqueId)
    }

    init {
        players[player.uniqueId] = this
    }

    fun onJoin() {
        loggedIn = false

        // Teleport to login position
        if (plugin.config.getBoolean("teleports.toLogin")) {
            player.teleport(LocationsManager.getLocation("login", player.world.spawnLocation))
        }

        // Bypass if IP is registered
        if (plugin.config.getBoolean("ipRecords.enabled")) {
            ip?.let {
                if (player.address?.hostString == it) {
                    onAuthenticate(AuthType.LOGIN)
                    return
                }
            }

            if (ipForgor != -1) {
                plugin.server.scheduler.cancelTask(ipForgor)
                ipForgor = -1
            }
        }

        schedulePreLoginTasks()
        sendWelcomeMessage()
    }

    fun onQuit() {
        if (!loggedIn) {
            cancelPreLoginTasks()
        } else {
            loggedIn = false

            if (plugin.config.getBoolean("teleports.savePosition")) {
                LocationsManager.savePlayerLocation(player)
            }

            // Store IP address if enabled
            if (plugin.config.getBoolean("ipRecords.enabled")) {
                // Schedule IP deletion
                ipForgor = plugin.server.scheduler.scheduleSyncDelayedTask(
                    plugin,
                    { ip = null },
                    plugin.config.getLong("ipRecords.delay", 10) * 20
                )
            }
        }
    }

    fun onAuthenticate(type: AuthType) {
        // Teleport player
        val teleports = plugin.config.getConfigurationSection("teleports")!!

        // Call event
        val event: AuthenticationEvent

        val bungeeEnabled = plugin.config.getBoolean("bungeeCord.enabled")
        if (bungeeEnabled) {
            val targetServer = plugin.config.getString("bungeeCord.spawnServer")
            event = AuthenticationEvent(player, type, targetServer)
        } else {
            var target: Location? = null

            if (teleports.getBoolean("savePosition")) {
                target = LocationsManager.getPlayerLocation(player, player.world.spawnLocation)
            } else if (teleports.getBoolean("toSpawn", true)) {
                target = LocationsManager.getLocation("spawn", player.world.spawnLocation)
            }

            event = AuthenticationEvent(player, type, target)
        }

        plugin.server.pluginManager.callEvent(event)

        if (event.isCancelled) return

        cancelPreLoginTasks()

        // Save IP address
        if (plugin.config.getBoolean("ipRecords.enabled")) {
            ip = player.address?.hostString
        }

        // Send login message
        event.message?.let { player.sendMessage(it) }

        // Join announcement
        event.announcement?.let {
            for (onlinePlayer in player.server.onlinePlayers) {
                if (UserLoginAPI.isLoggedIn(player)) {
                    onlinePlayer.sendMessage(it)
                }
            }
        }

        loggedIn = true

        // Teleport to destination
        if (bungeeEnabled && event.targetServer != null) {
            Utils.sendPluginMessage(player, "BungeeCord", "Connect", event.targetServer!!)
        } else if (event.destination != null) {
            player.teleport(event.destination!!)
        }
    }

    private fun sendWelcomeMessage() {
        val path = "messages.welcome." + if (UserLoginAPI.isRegistered(player)) "registered" else "unregistered"
        sendMessage(path, QuickMap<String, Any>("player", player.name))
    }

    fun schedulePreLoginTasks() {
        // Timeout
        if (plugin.config.getBoolean("timeout.enabled", true)) {
            val timeoutDelay = plugin.config.getLong("timeout.time")
            timeout = player.server.scheduler.scheduleSyncDelayedTask(
                plugin,
                { player.kickPlayer(LangManager.getMessage("messages.timeout")) },
                timeoutDelay * 20
            )
        }


        // Repeating welcome message
        val interval = plugin.config.getLong("repeatingWelcomeMsg", -1) * 20
        if (interval > 0) {
            welcomeMessage = player.server.scheduler.scheduleSyncRepeatingTask(
                plugin,
                { sendWelcomeMessage() },
                interval, interval
            )
        }
    }

    fun cancelPreLoginTasks() {
        if (timeout != -1) {
            player.server.scheduler.cancelTask(timeout)
            timeout = -1
        }
        if (welcomeMessage != -1) {
            player.server.scheduler.cancelTask(welcomeMessage)
            welcomeMessage = -1
        }
    }

    fun sendMessage(path: String, replace: Map<String, Any>? = null) {
        var message = LangManager.getMessage(path)
        if (message == null || message.isBlank()) return

        replace?.let {
            for (k in replace.keys) {
                message = message!!.replace("{$k}", it[k].toString())
            }
        }

        player.sendMessage(Utils.color(message!!))
    }
}