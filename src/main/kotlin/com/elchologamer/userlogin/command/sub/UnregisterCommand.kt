package com.elchologamer.userlogin.command.sub

import com.elchologamer.userlogin.UserLogin.Companion.plugin
import com.elchologamer.userlogin.command.base.SubCommand
import com.elchologamer.userlogin.util.Utils.fetchPlayerUUID
import org.bukkit.command.CommandSender

class UnregisterCommand : SubCommand("unregister", "ul.unregister") {
    override fun run(sender: CommandSender, args: Array<String>): Boolean {
        if (args.isNotEmpty()) {
            sender.server.scheduler.runTaskAsynchronously(plugin, Runnable { asyncRun(sender, args) })
            return true
        }

        return false
    }

    private fun asyncRun(sender: CommandSender, args: Array<String>) {
        // Try getting player directly from server
        val victim = sender.server.getPlayer(args[0])
        val uuid = victim?.uniqueId ?: fetchPlayerUUID(args[0])

        if (uuid == null || !plugin.db.isRegistered(uuid)) {
            sender.sendMessage(plugin.lang.getMessage("commands.errors.player_not_found"))
            return
        }

        try {
            plugin.db.deletePassword(uuid)

            val message = plugin.lang.getMessage("commands.player_unregistered")
            sender.sendMessage(message?.replace("{player}", victim?.name ?: args[0]))
        } catch (e: Exception) {
            e.printStackTrace()
            sender.sendMessage(plugin.lang.getMessage("commands.errors.unregister_failed"))
        }
    }
}