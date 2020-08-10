package com.elcholostudios.userlogin.commands;

import com.elcholostudios.userlogin.util.SubCommand;
import com.elcholostudios.userlogin.util.Utils;
import com.elcholostudios.userlogin.util.lists.Path;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler implements CommandExecutor, TabCompleter {

    private final ArrayList<SubCommand> subCommands = new ArrayList<>();
    private final Utils utils = new Utils();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) return false;

        // Check for sub-commands with the matching name
        for (SubCommand sub : this.subCommands) {
            if (!sub.getName().equals(args[0])) continue;
            if (sub.isPlayerOnly() && !(sender instanceof Player)) {
                utils.sendMessage(Path.PLAYER_ONLY, sender);
                return true;
            }

            // Get arguments and run command
            String[] subArgs = new String[args.length - 1];
            System.arraycopy(args, 1, subArgs, 0, args.length - 1);

            return sub.run(sender, subArgs);
        }

        return false;
    }

    public void addCommand(SubCommand sub) {
        this.subCommands.add(sub);
    }

    public void trim() {
        this.subCommands.trimToSize();
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> options = new ArrayList<>();
        if (args.length == 1) {
            for (SubCommand sub : this.subCommands)
                if (sub.getName().startsWith(args[0])) options.add(sub.getName());
        } else if (args.length == 2) {
            for (SubCommand sub : this.subCommands) {
                if (!sub.getName().equals(args[0])) continue;
                for (String subOption : sub.getSubs())
                    if (subOption.startsWith(args[1])) options.add(subOption);
            }
        }

        return options;
    }

}
