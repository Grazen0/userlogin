package com.elchologamer.userlogin.util.command;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.util.Path;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseCommand implements CommandExecutor, TabCompleter {

    private final UserLogin plugin;
    private final String name;
    private final boolean playerOnly;

    public BaseCommand(String name) {
        this(name, false);
    }

    public BaseCommand(String name, boolean playerOnly) {
        this.name = name;
        this.playerOnly = playerOnly;
        plugin = UserLogin.getPlugin();
    }

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (playerOnly && !(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage(Path.PLAYER_ONLY));
            return true;
        }

        return execute(sender, command, label, args);
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
