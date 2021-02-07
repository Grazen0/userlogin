package me.elcholostudios.userlogin.events;

import me.elcholostudios.userlogin.Essentials;
import me.elcholostudios.userlogin.UserLogin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class OnCommandReceived implements Listener {

    final Essentials es = new Essentials();

    @EventHandler
    public void onCommandSent(@NotNull PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        String command = e.getMessage().split(" ")[0];
        boolean b = Objects.requireNonNull(UserLogin.plugin.getConfig().getString("restrictions.loginDisabledCommands")).
                contains(command.replace("/", ""));
        if(b){
            e.setCancelled(true);
            es.sendMessage(player, "command-errors.command-disabled", null, null);
        }
    }
}
