package com.elcholostudios.userlogin.api.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class SubCommand {

    protected final String name;
    private final List<String> subs = new ArrayList<>();
    private final boolean playerOnly;

    public SubCommand(String name, boolean playerOnly) {
        this.name = name;
        this.playerOnly = playerOnly;
    }

    public abstract boolean run(CommandSender sender, String[] args);

    public String getName() {
        return this.name;
    }

    public @NotNull List<String> getSubs() {
        return this.subs;
    }

    public void addSub(String sub) {
        this.subs.add(sub);
    }

    public boolean isPlayerOnly() {
        return this.playerOnly;
    }
}
