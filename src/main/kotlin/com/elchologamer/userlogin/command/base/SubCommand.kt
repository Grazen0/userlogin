package com.elchologamer.userlogin.command.base

import org.bukkit.command.CommandSender

abstract class SubCommand constructor(
    name: String,
    playerOnly: Boolean,
    private val permission: String? = null
) : BaseCommand(
    name, playerOnly
) {
    constructor(name: String, permission: String? = null) : this(name, false, permission)

    override fun run(sender: CommandSender, label: String, args: Array<String>): Boolean {
        if (permission?.let { !sender.hasPermission(it) } == true) {
            permissionMessage?.let { sender.sendMessage(it) }
            return true
        }
        return run(sender, args)
    }

    abstract fun run(sender: CommandSender, args: Array<String>): Boolean

    override fun getPermission(): String? {
        return permission
    }
}