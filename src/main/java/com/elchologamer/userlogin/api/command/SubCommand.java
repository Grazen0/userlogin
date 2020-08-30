package com.elchologamer.userlogin.api.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class SubCommand {

    protected final String name;
    private final List<String> subs = new ArrayList<>();
    private final boolean playerOnly;

    /**
     * A command that can be executed by a CommandHandler,
     * must be registered in it.
     * @param name The name of the sub-command.
     * @param playerOnly Whether the sub-command is player-only or not.
     */
    public SubCommand(String name, boolean playerOnly) {
        this.name = name;
        this.playerOnly = playerOnly;
    }

    /**
     * Executes the command and returns its result, whether it is true or false.
     * @param sender The sender of the command.
     * @param args The arguments of the command.
     * @return The result of the command: True if successful, false otherwise.
     */
    public abstract boolean run(CommandSender sender, String[] args);

    /**
     * Gets the name of the command.
     * @return This command's name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the list of strings to be shown as sub-commands
     * in the tab-completion of the CommandHandler.
     * @return String list of sub-command names.
     */
    public @NotNull List<String> getSubs() {
        return this.subs;
    }

    /**
     * Adds a string to the sub-command list
     * of this sub-command.
     * @param sub The string to add.
     */
    public void addSub(String sub) {
        this.subs.add(sub);
    }

    /**
     * Checks if this sub-command is player-only
     * @return True if the command is player-only, false otherwise.
     */
    public boolean isPlayerOnly() {
        return this.playerOnly;
    }
}
