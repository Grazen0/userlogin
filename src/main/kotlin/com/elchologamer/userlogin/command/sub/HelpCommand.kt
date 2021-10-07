package com.elchologamer.userlogin.command.sub

import com.elchologamer.userlogin.UserLogin.Companion.plugin
import com.elchologamer.userlogin.command.base.SubCommand
import com.elchologamer.userlogin.util.Utils
import org.bukkit.command.CommandSender

class HelpCommand : SubCommand("help", "ul.help") {
    override fun run(sender: CommandSender, args: Array<String>): Boolean {
        val message = plugin.lang.entries.getStringList("messages.help").joinToString(
            "\n",
            transform = { Utils.color(it) }
        )

        sender.sendMessage(message)
        return true
    }
}