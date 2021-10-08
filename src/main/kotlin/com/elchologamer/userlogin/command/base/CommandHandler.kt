package com.elchologamer.userlogin.command.base

import com.elchologamer.userlogin.manager.LangManager
import org.bukkit.command.CommandSender

class CommandHandler(name: String) : BaseCommand(name) {
    private val subCommands: MutableList<SubCommand> = ArrayList()

    override fun run(sender: CommandSender, label: String, args: Array<String>): Boolean {
        if (args.isEmpty()) return false

        for (subCommand in subCommands) {
            if (subCommand.name != args[0]) continue

            // Check that player has permission
            if (subCommand.permission != null && !sender.hasPermission(subCommand.permission!!)) {
                sender.sendMessage(LangManager.getMessage("commands.errors.no_permission"))
                return true
            }
            return subCommand.run(sender, label, getSubArgs(args))
        }
        return false
    }

    override fun tabComplete(sender: CommandSender, label: String, args: Array<String>): List<String> {
        val options = if (args.size == 1) {
            subCommands.map { it.name }
        } else {
            subCommands.find { it.name == args[0] }?.tabComplete(sender, label, getSubArgs(args))
        } ?: ArrayList()

        // Filter out list
        return options.filter { it.startsWith(args[args.size - 1]) }
    }

    private fun getSubArgs(args: Array<String>): Array<String> {
        val size = (args.size - 1).coerceAtLeast(0)
        return args.copyOfRange(1.coerceAtMost(size), size)
    }

    fun add(subCommand: SubCommand) {
        subCommands.add(subCommand)
    }
}