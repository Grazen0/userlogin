package com.elchologamer.userlogin.command.base;

import org.bukkit.command.CommandSender;

public abstract class AsyncCommand extends BaseCommand {

    public AsyncCommand(String name) {
        super(name);
    }

    public AsyncCommand(String name, boolean playerOnly) {
        super(name, playerOnly);
    }

    @Override
    public final boolean run(CommandSender sender, String label, String[] args) {
        getPlugin().getServer().getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            boolean success = asyncRun(sender, label, args);
            if (!success) sender.sendMessage(getUsage());
        });
        return true;
    }

    public abstract boolean asyncRun(CommandSender sender, String label, String[] args);
}
