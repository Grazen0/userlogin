package com.elchologamer.userlogin.listener.restriction;

import com.elchologamer.userlogin.ULPlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandRestriction extends BaseRestriction<PlayerCommandPreprocessEvent> {

    public CommandRestriction() {
        super("commands");
    }

    @EventHandler
    public void handle(PlayerCommandPreprocessEvent e) {
        if (!shouldRestrict(e)) return;

        String command = e.getMessage().replaceAll("^/", "").split("\\s+")[0];

        List<String> allowedCommands = new ArrayList<>(Arrays.asList("login", "register"));

        ConfigurationSection section = getPlugin().getConfig().getConfigurationSection("commandAliases");
        if (section != null) {
            allowedCommands.addAll(section.getStringList("login"));
            allowedCommands.addAll(section.getStringList("register"));
        }

        for (String allowedCommand : allowedCommands) {
            if (allowedCommand.equalsIgnoreCase(command)) return;
        }

        e.setCancelled(true);
        ULPlayer.get(e.getPlayer()).sendMessage("messages.commands_disabled");
    }
}