package com.elchologamer.userlogin.util.command;

import com.elchologamer.userlogin.UserLogin;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseCommand extends Command {

    private static CommandMap commandMap = null;

    private final UserLogin plugin = UserLogin.getPlugin();
    private final boolean playerOnly;

    public BaseCommand(String name) {
        this(name, false);
    }

    public BaseCommand(String name, boolean playerOnly) {
        super(name, "", "", new ArrayList<>());
        this.playerOnly = playerOnly;
        setPermission("ul." + name);
    }

    @Override
    public final boolean execute(CommandSender sender, String label, String[] args) {
        // Check if command is disabled
        if (!plugin.getConfig().getBoolean("enabledCommands." + getName(), true)) {
            sender.sendMessage(plugin.getMessage("commands.disabled"));
            return true;
        }

        if (playerOnly && !(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("commands.errors.player_only"));
            return true;
        }

        boolean success = run(sender, label, args);
        if (!success) sender.sendMessage(getUsage());

        return true;
    }

    public void register() {
        String usage = plugin.getMessage("commands.usages." + getName());
        if (usage != null) setUsage(usage);

        String description = plugin.getMessage("commands.descriptions." + getName());
        if (description != null) setDescription(description);

        List<String> aliases = plugin.getConfig().getStringList("commandAliases." + getName());
        setAliases(aliases);

        getCommandMap().register(plugin.getName().toLowerCase(), this);
    }

    private static CommandMap getCommandMap() {
        if (commandMap == null) {
            try {
                Server server = UserLogin.getPlugin().getServer();
                Field mapField = server.getClass().getDeclaredField("commandMap");
                mapField.setAccessible(true);

                commandMap = (CommandMap) mapField.get(server);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return commandMap;
    }

    public abstract boolean run(CommandSender sender, String label, String[] args);

    @Override
    public List<String> tabComplete(CommandSender sender, String label, String[] args) {
        return new ArrayList<>();
    }

    public UserLogin getPlugin() {
        return plugin;
    }

    public boolean isPlayerOnly() {
        return playerOnly;
    }
}
