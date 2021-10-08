package com.elchologamer.userlogin.command

import com.elchologamer.userlogin.ULPlayer
import com.elchologamer.userlogin.UserLogin.Companion.plugin
import com.elchologamer.userlogin.api.types.AuthType
import com.elchologamer.userlogin.util.QuickMap

object RegisterCommand : AuthCommand("register", AuthType.REGISTER, 2) {
    override fun authenticate(ulPlayer: ULPlayer, args: Array<String>): Boolean {

        // Check if player is not already registered
        val uuid = ulPlayer.player.uniqueId
        if (plugin.db.isRegistered(uuid)) {
            ulPlayer.sendMessage("messages.already_registered")
            return false
        }
        val password = args[0]
        val minChars = plugin.config.getInt("password.minCharacters", 0)
        val maxChars = plugin.config.getInt("password.maxCharacters", 128)

        // Check that both passwords match
        if (password != args[1]) {
            ulPlayer.sendMessage("messages.different_passwords")
            return false
        }

        // Check password length
        if (password.length < minChars) {
            ulPlayer.sendMessage(
                "messages.short_password",
                QuickMap("chars", minChars)
            )
            return false
        }
        if (password.length > maxChars) {
            ulPlayer.sendMessage(
                "messages.long_password",
                QuickMap("chars", maxChars)
            )
            return false
        }

        // Check password regex
        val regexStr = plugin.config.getString("password.regex", "")!!
        if (regexStr != "" && !password.matches(Regex(regexStr))) {
            ulPlayer.sendMessage(
                "messages.regex_mismatch",
                QuickMap("regex", regexStr)
            )
            return false
        }

        return try {
            plugin.db.createPassword(uuid, password)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            ulPlayer.sendMessage("messages.register_failed")
            false
        }
    }
}