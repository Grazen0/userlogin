package com.elchologamer.userlogin.util.command;

import com.elchologamer.userlogin.UserLogin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler extends BaseCommand {

    private final UserLogin plugin = UserLogin.getPlugin();
    private final List<SubCommand> subCommands = new ArrayList<>();

    public CommandHandler(String name) {
        super(name);
    }


    @Override
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) return false;

        for (SubCommand subCommand : subCommands) {
            if (!subCommand.getName().equals(args[0])) continue;

            // Check that player has permission
            if (!sender.hasPermission(subCommand.getPermission())) {
                sender.sendMessage(plugin.getMessage("commands.errors.no_permission"));
                return true;
            }

            return subCommand.onCommand(sender, command, label, getSubArgs(args));
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> options = new ArrayList<>();

        if (args.length == 1) {
            for (SubCommand subCommand : subCommands) {
                options.add(subCommand.getName());
            }
        } else {
            for (SubCommand subCommand : subCommands) {
                if (!subCommand.getName().equals(args[0])) continue;

                options = subCommand.onTabComplete(sender, command, label, getSubArgs(args));
                break;
            }
        }

        // Filter out list
        if (options != null) options.removeIf(s -> !s.startsWith(args[args.length - 1]));

        return options;
    }

    private String[] getSubArgs(String[] args) {
        String[] subArgs = new String[args.length - 1];
        System.arraycopy(args, 1, subArgs, 0, subArgs.length);
        return subArgs;
    }

    public void add(SubCommand subCommand) {
        subCommands.add(subCommand);
    }
}
