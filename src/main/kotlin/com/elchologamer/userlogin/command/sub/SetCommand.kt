package com.elchologamer.userlogin.command.sub

import com.elchologamer.userlogin.UserLogin.Companion.plugin
import com.elchologamer.userlogin.command.base.SubCommand
import com.elchologamer.userlogin.util.QuickMap
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SetCommand : SubCommand("set", true, "ul.set") {
    override fun run(sender: CommandSender, args: Array<String>): Boolean {
        if (args.isEmpty()) return false

        val type = args[0].lowercase()
        if (type != "spawn" && type != "login") return false

        val ulPlayer = plugin.getPlayer((sender as Player))
        val player = ulPlayer.player

        // Save location
        val loc = player.location
        plugin.locationsManager.saveLocation(type, loc)

        // Send message
        ulPlayer.sendMessage(
            "commands.set",
            QuickMap<String, Any>("type", type)
                .set("x", loc.x.toInt())
                .set("y", loc.y.toInt())
                .set("z", loc.z.toInt())
                .set("yaw", loc.yaw.toInt())
                .set("pitch", loc.pitch.toInt())
                .set("world", loc.world!!.name)
        )
        return true
    }

    override fun tabComplete(sender: CommandSender, alias: String, args: Array<String>): List<String> {
        val options: MutableList<String> = ArrayList()

        if (args.size == 1) {
            options.add("login" )
            options.add("spawn")
        }

        return options
    }
}