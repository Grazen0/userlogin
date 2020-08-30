package com.elchologamer.userlogin.api.command;

import com.elchologamer.userlogin.util.Utils;
import com.elchologamer.userlogin.util.lists.Path;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler implements CommandExecutor, TabCompleter {

    private final List<SubCommand> subCommands = new ArrayList<>();
    private final Utils utils = new Utils();

    /**
     * Represents a main command that handles multiple sub-commands
     * @param mainCommand The main command to associate this handler to
     * @param plugin The JavaPlugin for this command
     */
    public CommandHandler(@NotNull String mainCommand, @NotNull JavaPlugin plugin) {
        PluginCommand command = plugin.getCommand(mainCommand);
        if(command == null) return;

        command.setExecutor(this);
    }

    /**
     * Executes the command, and returns its state,
     * whether it is true or false.
     * @param sender The source of the command
     * @param command The command executed
     * @param label The label of the executed command
     * @param args Arguments used in the command
     * @return True if the command was successful, false otherwise
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (args.length == 0) return false;

        // Check for sub-commands with the matching name
        for (SubCommand sub : this.subCommands) {
            if (!sub.getName().equals(args[0])) continue;
            if (sub.isPlayerOnly() && !(sender instanceof Player)) {
                utils.sendMessage(Path.PLAYER_ONLY, sender);
                return true;
            }

            // Get an array with sub-arguments
            String[] subArgs = new String[args.length - 1];
            System.arraycopy(args, 1, subArgs, 0, args.length - 1);

            // Run command
            return sub.run(sender, subArgs);
        }

        return false;
    }

    /**
     * Adds a sub-command to this handler
     * @param sub The sub-command to add
     */
    public void addCommand(SubCommand sub) {
        this.subCommands.add(sub);
    }

    /**
     * Gets the list of tab-completions to show on the command
     * @param sender The source that is typing the command
     * @param command The typed command
     * @param alias The alias of the command used
     * @param args The current arguments of the command
     * @return The list of words to tab-complete
     */
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String @NotNull [] args) {
        List<String> options = new ArrayList<>();

        if (args.length == 1) {
            // Add all commands, filtered by search
            for (SubCommand sub : this.subCommands)
                if (sub.getName().startsWith(args[0])) options.add(sub.getName());
        } else if (args.length == 2) {
            // Check which command matches main command
            for (SubCommand sub : this.subCommands) {
                if (!sub.getName().equals(args[0])) continue;

                // Add all subs for said command, filtered by search
                for (String subOption : sub.getSubs())
                    if (subOption.startsWith(args[1])) options.add(subOption);
                break;
            }
        }

        return options;
    }

}
