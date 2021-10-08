package com.elchologamer.userlogin

import com.elchologamer.userlogin.command.ChangePasswordCommand
import com.elchologamer.userlogin.command.LoginCommand
import com.elchologamer.userlogin.command.RegisterCommand
import com.elchologamer.userlogin.command.base.CommandHandler
import com.elchologamer.userlogin.command.sub.HelpCommand
import com.elchologamer.userlogin.command.sub.ReloadCommand
import com.elchologamer.userlogin.command.sub.SetCommand
import com.elchologamer.userlogin.command.sub.UnregisterCommand
import com.elchologamer.userlogin.database.Database
import com.elchologamer.userlogin.listener.JoinQuitListener
import com.elchologamer.userlogin.listener.restriction.*
import com.elchologamer.userlogin.manager.LangManager
import com.elchologamer.userlogin.manager.LocationsManager
import com.elchologamer.userlogin.util.FastLoginHook
import com.elchologamer.userlogin.util.LogFilter
import com.elchologamer.userlogin.util.Metrics
import com.elchologamer.userlogin.util.Metrics.SimplePie
import com.elchologamer.userlogin.util.Utils
import org.bukkit.plugin.java.JavaPlugin

class UserLogin : JavaPlugin() {
    private var _db: Database? = null
    val db
        get() = _db ?: throw IllegalArgumentException("Database is not initialized")

    companion object {
        private var _plugin: UserLogin? = null
        val plugin
            get() = _plugin ?: throw IllegalArgumentException("Plugin is not initialized")

        private const val pluginID = 80669
        private const val bStatsID = 8586
    }

    override fun onEnable() {
        _plugin = this

        Utils.debug("RUNNING IN DEBUG MODE")

        server.messenger.registerOutgoingPluginChannel(this, "BungeeCord")

        reloadPlugin()

        // Register FastLogin hook
        if (server.pluginManager.isPluginEnabled("FastLogin")) {
            FastLoginHook.register()
            Utils.log("FastLogin hook registered")
        }

        try {
            LogFilter.register()
        } catch (e: NoClassDefFoundError) {
            Utils.log("&eFailed to register logging filter")
        }

        // Register event listeners
        arrayListOf(
            JoinQuitListener,
            ChatRestriction,
            MovementRestriction,
            BlockBreakingRestriction,
            BlockPlacingRestriction,
            CommandRestriction,
            ItemDropRestriction,
            MovementRestriction,
            AttackRestriction,
            ReceiveDamageRestriction
        ).forEach { server.pluginManager.registerEvents(it, this) }

        // Register Item Pickup restriction if class exists
        try {
            Class.forName("org.bukkit.event.entity.EntityPickupItemEvent")
            server.pluginManager.registerEvents(ItemPickupRestriction, this)
        } catch (ignored: ClassNotFoundException) {
        }

        val mainCommand = CommandHandler("userlogin")
        mainCommand.add(HelpCommand())
        mainCommand.add(SetCommand())
        mainCommand.add(ReloadCommand())
        mainCommand.add(UnregisterCommand())

        // Register commands
        mainCommand.register()
        LoginCommand.register()
        RegisterCommand.register()
        ChangePasswordCommand.register()

        // bStats setup
        if (!config.getBoolean("debug")) {
            val metrics = Metrics(this, bStatsID)

            metrics.addCustomChart(SimplePie("storage_type") {
                config.getString("database.type", "yaml")!!.lowercase()
            })
            metrics.addCustomChart(SimplePie("lang") { config.getString("lang", "en_US") })
        }

        // Check for updates
        if (config.getBoolean("checkUpdates", true)) {
            server.scheduler.runTaskAsynchronously(this, Runnable { checkUpdates() })
        }

        Utils.log("$name v${description.version} enabled")
    }

    fun reloadPlugin() {
        // Load configurations
        saveDefaultConfig()
        reloadConfig()

        LocationsManager.locations.saveDefault()
        LangManager.reload()

        // Cancel all plugin tasks
        server.scheduler.cancelTasks(this)

        // Start database
        _db?.disconnect()
        _db = Database.select()
        server.scheduler.runTaskAsynchronously(this, Runnable { connectDatabase() })
    }

    private fun connectDatabase() {
        try {
            db.connect()

            if (db.logConnected) {
                Utils.log(LangManager.getMessage("other.database_connected", "&eDatabase connected"))
            }
        } catch (e: Exception) {
            val log = if (e.message != null && e is ClassNotFoundException)
                LangManager.getMessage("other.driver_missing")?.replace("{driver}", e.message!!) else
                LangManager.getMessage("other.database_error")

            log?.let { Utils.log(it) }
            e.printStackTrace()
        }
    }

    private fun checkUpdates() {
        val latest = Utils.fetch("https://api.spigotmc.org/legacy/update.php?resource=$pluginID")

        if (latest == null) {
            Utils.log("&cUnable to fetch latest version")
        } else if (!latest.equals(description.version, true)) {
            Utils.log("&eA new UserLogin version is available! (v$latest)")
        } else {
            Utils.log("&aRunning latest version! (v${description.version})")
        }
    }

    override fun onDisable() = db.disconnect()
}