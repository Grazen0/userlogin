package com.elchologamer.userlogin.command

import com.elchologamer.userlogin.ULPlayer
import com.elchologamer.userlogin.UserLogin.Companion.plugin
import com.elchologamer.userlogin.api.types.AuthType

object LoginCommand : AuthCommand("login", AuthType.LOGIN, 1) {
    override fun authenticate(ulPlayer: ULPlayer, args: Array<String>): Boolean {
        val uuid = ulPlayer.player.uniqueId

        // Check if player is registered
        if (!plugin.db.isRegistered(uuid)) {
            ulPlayer.sendMessage("messages.not_registered")
            return false
        }

        // Authenticate passwords
        if (!plugin.db.comparePasswords(uuid, args[0])) {
            ulPlayer.onLoginAttempt()
            ulPlayer.sendMessage("messages.incorrect_password")
            return false
        }

        return true
    }
}