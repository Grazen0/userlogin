package com.elchologamer.userlogin.command

import com.elchologamer.userlogin.ULPlayer
import com.elchologamer.userlogin.UserLogin.Companion.plugin
import com.elchologamer.userlogin.api.types.AuthType
import com.elchologamer.userlogin.command.base.BaseCommand
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

abstract class AuthCommand(name: String, private val type: AuthType, private val minArgs: Int) :
    BaseCommand(name, true) {

    override fun run(sender: CommandSender, label: String, args: Array<String>): Boolean {
        val ulPlayer = plugin.getPlayer((sender as Player?)!!)

        // Check if player is already logged in
        if (ulPlayer.loggedIn) {
            ulPlayer.sendMessage("messages.already_logged_in")
            return true
        }

        // Check usage
        if (args.size < minArgs) return false

        // Authenticate player
        if (authenticate(ulPlayer, args)) ulPlayer.onAuthenticate(type)
        return true
    }

    protected abstract fun authenticate(ulPlayer: ULPlayer, args: Array<String>): Boolean
}