package com.elchologamer.userlogin.command.sub

import com.elchologamer.userlogin.ULPlayer
import com.elchologamer.userlogin.UserLogin.Companion.plugin
import com.elchologamer.userlogin.command.base.SubCommand
import org.bukkit.command.CommandSender

class ReloadCommand : SubCommand("reload", "ul.reload") {
    override fun run(sender: CommandSender, args: Array<String>): Boolean {
        plugin.reloadPlugin()

        for (player in plugin.server.onlinePlayers) {
            val ulPlayer = ULPlayer[player]
            if (!ulPlayer.loggedIn) {
                ulPlayer.schedulePreLoginTasks()
            }
        }

        sender.sendMessage(plugin.lang.getMessage("commands.reload"))
        return true
    }
}