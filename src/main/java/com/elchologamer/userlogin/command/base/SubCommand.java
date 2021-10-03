package com.elchologamer.userlogin.command.base;

import org.bukkit.command.CommandSender;

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
    public final boolean run(CommandSender sender, String label, String[] args) {
        if (permission != null && !sender.hasPermission(permission)) {
            String message = this.getPermissionMessage();
            if (message != null) sender.sendMessage(message);
            return true;

        }

        return run(sender, args);
    }

    public abstract boolean run(CommandSender sender, String[] args);

    public String getPermission() {
        return permission;
    }
}
