package com.elchologamer.userlogin.util.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public abstract class SubCommand extends BaseCommand {

    private final String permission;

    public SubCommand(String name) {
        this(name, null);
    }

    public SubCommand(String name, boolean playerOnly) {
        this(name, playerOnly, null);
    }

    public SubCommand(String name, String permission) {
        this(name, false, permission);
    }

    public SubCommand(String name, boolean playerOnly, String permission) {
        super(name, playerOnly);
        this.permission = permission;
    }

    @Override
    public final boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (permission != null && !sender.hasPermission(permission)) {
            String message = command.getPermissionMessage();
            if (message != null) sender.sendMessage(message);
            return true;

        }

        return execute(sender, command, args);
    }

    public abstract boolean execute(CommandSender sender, Command command, String[] args);

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }

    public String getPermission() {
        return permission;
    }
}
