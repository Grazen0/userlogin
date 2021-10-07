package com.elchologamer.userlogin.command.sub

import com.elchologamer.userlogin.UserLogin.Companion.plugin
import com.elchologamer.userlogin.command.base.SubCommand
import org.bukkit.command.CommandSender

class ReloadCommand : SubCommand("reload", "ul.reload") {
    override fun run(sender: CommandSender, args: Array<String>): Boolean {
        plugin.reloadPlugin()

        for (ulPlayer in plugin.players.values) {
            if (ulPlayer.player.isOnline && !ulPlayer.loggedIn) {
                ulPlayer.onJoin(null)
            }
        }

        sender.sendMessage(plugin.lang.getMessage("commands.reload"))
        return true
    }
}