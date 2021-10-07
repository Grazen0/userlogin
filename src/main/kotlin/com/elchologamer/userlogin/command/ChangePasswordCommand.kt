package com.elchologamer.userlogin.command

import com.elchologamer.userlogin.ULPlayer
import com.elchologamer.userlogin.UserLogin.Companion.plugin
import com.elchologamer.userlogin.command.base.BaseCommand
import com.elchologamer.userlogin.util.QuickMap
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object ChangePasswordCommand : BaseCommand("changepassword", true) {
    override fun run(sender: CommandSender, label: String, args: Array<String>): Boolean {
        val player = sender as Player
        val ulPlayer: ULPlayer = plugin.getPlayer(player)
        if (!ulPlayer.loggedIn) {
            ulPlayer.sendMessage("messages.must_log_in")
            return true
        }
        if (args.size < 3) return false

        // Check first password
        if (!plugin.db.comparePasswords(player.uniqueId, args[0])) {
            ulPlayer.sendMessage("messages.incorrect_password")
            return true
        }

        // Check that passwords match
        val newPassword = args[1]
        if (newPassword == args[2]) {
            ulPlayer.sendMessage("messages.different_passwords")
            return true
        }

        val minChars = plugin.config.getInt("password.minCharacters", 0)
        val maxChars = plugin.config.getInt("password.maxCharacters", 128)

        // Check password length
        if (newPassword.length < minChars) {
            ulPlayer.sendMessage(
                "messages.short_password",
                QuickMap("chars", minChars)
            )
            return true
        }

        if (newPassword.length > maxChars) {
            ulPlayer.sendMessage(
                "messages.long_password",
                QuickMap("chars", maxChars)
            )
            return true
        }

        // Check password regex
        val regexStr = plugin.config.getString("password.regex", "")!!
        if (regexStr != "" && !newPassword.matches(Regex(regexStr))) {
            ulPlayer.sendMessage("messages.regex_mismatch", QuickMap("regex", regexStr))
            return true
        }

        try {
            plugin.db.updatePassword(player.uniqueId, newPassword)
            ulPlayer.sendMessage("messages.password_changed")
        } catch (e: Exception) {
            ulPlayer.sendMessage("messages.password_change_error")
            e.printStackTrace()
        }
        return true
    }
}