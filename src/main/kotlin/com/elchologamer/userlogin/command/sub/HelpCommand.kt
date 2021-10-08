package com.elchologamer.userlogin.command.sub

import com.elchologamer.userlogin.command.base.SubCommand
import com.elchologamer.userlogin.manager.LangManager
import com.elchologamer.userlogin.util.Utils
import org.bukkit.command.CommandSender

class HelpCommand : SubCommand("help", "ul.help") {
    override fun run(sender: CommandSender, args: Array<String>): Boolean {
        val message = LangManager.entries.getStringList("messages.help").joinToString(
            "\n",
            transform = { Utils.color(it) }
        )

        sender.sendMessage(message)
        return true
    }
}