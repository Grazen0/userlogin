package com.elchologamer.userlogin.api.command;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.util.Path;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class SubCommandHandler implements CommandExecutor, TabCompleter {

    private final UserLogin plugin;

    public SubCommandHandler() {
        plugin = UserLogin.getPlugin();
    }

    protected final List<SubCommand> subCommands = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) return false;

        for (SubCommand subCommand : subCommands) {
            if (!subCommand.getName().equals(args[0])) continue;

            if (!sender.hasPermission(subCommand.getPermission())) {
                sender.sendMessage(plugin.getMessage(Path.NO_PERMISSION));
                return true;
            }

            return subCommand.onCommand(sender, command, label, getSubArgs(args));
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> options = new ArrayList<>();

        if (args.length == 1) {
            for (SubCommand subCommand : subCommands) {
                options.add(subCommand.getName());
            }
        } else {
            for (SubCommand subCommand : subCommands) {
                if (subCommand.getName().equals(args[0])) {
                    options = subCommand.onTabComplete(sender, command, alias, getSubArgs(args));
                    break;
                }
            }
        }

        if (options != null) options.removeIf(s -> !args[args.length - 1].startsWith(s));

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

    public List<SubCommand> getSubCommands() {
        return subCommands;
    }
}
