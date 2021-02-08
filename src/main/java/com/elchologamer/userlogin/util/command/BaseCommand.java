package com.elchologamer.userlogin.util.command;

import com.elchologamer.userlogin.UserLogin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseCommand implements CommandExecutor, TabCompleter {

    private final UserLogin plugin = UserLogin.getPlugin();
    private final String name;
    private final boolean playerOnly;

    public BaseCommand(String name) {
        this(name, false);
    }

    public BaseCommand(String name, boolean playerOnly) {
        this.name = name;
        this.playerOnly = playerOnly;
    }

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (playerOnly && !(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("commands.errors.player_only"));
            return true;
        }

        return execute(sender, command, label, args);
    }

    public void register() {
        PluginCommand command = plugin.getCommand(name);
        if (command == null) return;

        String usage = plugin.getMessage("commands.usages." + name);
        if (usage != null) command.setUsage(usage);

        String description = plugin.getMessage("commands.descriptions." + name);
        if (description != null) command.setDescription(description);

        command.setExecutor(this);
    }

    public abstract boolean execute(CommandSender sender, Command command, String label, String[] args);

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }

    public UserLogin getPlugin() {
        return plugin;
    }

    public String getName() {
        return name;
    }

    public boolean isPlayerOnly() {
        return playerOnly;
    }
}
