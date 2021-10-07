package com.elchologamer.userlogin

import com.elchologamer.userlogin.UserLogin.Companion.plugin
import com.elchologamer.userlogin.api.UserLoginAPI
import com.elchologamer.userlogin.api.event.AuthenticationEvent
import com.elchologamer.userlogin.api.types.AuthType
import com.elchologamer.userlogin.util.QuickMap
import com.elchologamer.userlogin.util.Utils
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent

class ULPlayer(var player: Player) {
    var loggedIn = false
        private set

    private var timeout = -1
    private var welcomeMessage = -1
    private var ip: String? = null

    fun onJoin(event: PlayerJoinEvent?) {
        loggedIn = false
        player = event?.player ?: player

        // Teleport to login position
        if (plugin.config.getBoolean("teleports.toLogin")) {
            player.teleport(plugin.locationsManager.getLocation("login", player.world.spawnLocation))
        }

        // Bypass if IP is registered
        if (ip != null && plugin.config.getBoolean("ipRecords.enabled")) {
            if (player.address?.hostString == ip) {
                ip = null
                onAuthenticate(AuthType.LOGIN)
                return
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
                plugin.locationsManager.savePlayerLocation(player)
            }

            // Store IP address if enabled
            if (plugin.config.getBoolean("ipRecords.enabled")) {
                ip = player.address?.hostString

                // Schedule IP deletion
                plugin.server.scheduler.scheduleSyncDelayedTask(
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
                target = plugin.locationsManager.getPlayerLocation(player, player.world.spawnLocation)
            } else if (teleports.getBoolean("toSpawn", true)) {
                target = plugin.locationsManager.getLocation("spawn", player.world.spawnLocation)
            }

            event = AuthenticationEvent(player, type, target)
        }

        plugin.server.pluginManager.callEvent(event)

        if (event.isCancelled) return

        cancelPreLoginTasks()

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
                { player.kickPlayer(plugin.lang.getMessage("messages.timeout")) },
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
        var message = plugin.lang.getMessage(path)
        if (message == null || message.isBlank()) return

        replace?.let {
            for (k in replace.keys) {
                message = message!!.replace("{$k}", it[k].toString())
            }
        }

        player.sendMessage(Utils.color(message!!))
    }
}