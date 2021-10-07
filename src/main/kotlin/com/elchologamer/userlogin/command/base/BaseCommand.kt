package com.elchologamer.userlogin.command.base

import com.elchologamer.userlogin.UserLogin.Companion.plugin
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

abstract class BaseCommand constructor(name: String, val playerOnly: Boolean = false) :
    Command(name, "", "", ArrayList()) {

    companion object {
        private var commandMap: CommandMap? = null
            get() {
                if (field == null) {
                    try {
                        val mapField = plugin.server.javaClass.getDeclaredField("commandMap")
                        mapField.isAccessible = true
                        field = mapField[plugin.server] as CommandMap
                    } catch (e: NoSuchFieldException) {
                        e.printStackTrace()
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                    }
                }

                return field
            }
    }

    init {
        permission = "ul.$name"
    }

    override fun execute(sender: CommandSender, label: String, args: Array<String>): Boolean {
        // Check if command is disabled
        if (!plugin.config.getBoolean("enabledCommands.$name", true)) {
            sender.sendMessage(plugin.lang.getMessage("commands.disabled"))
            return true
        }

        if (playerOnly && sender !is Player) {
            sender.sendMessage(plugin.lang.getMessage("commands.errors.player_only"))
            return true
        }

        val success = run(sender, label, args)
        if (!success) sender.sendMessage(usage)

        return true
    }

    fun register() {
        val usage = plugin.lang.getMessage("commands.usages.$name")
        usage?.let { setUsage(it) }

        val description = plugin.lang.getMessage("commands.descriptions.$name")
        description?.let { setDescription(it) }

        val aliases = plugin.config.getStringList("commandAliases.$name")
        setAliases(aliases)

        commandMap!!.register(plugin.name.lowercase(), this)
    }

    abstract fun run(sender: CommandSender, label: String, args: Array<String>): Boolean

    override fun tabComplete(sender: CommandSender, label: String, args: Array<String>): List<String> = ArrayList()
}