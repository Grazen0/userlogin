package com.elcholostudios.userlogin.listeners;

import com.elcholostudios.userlogin.api.event.ServerReloadEvent;
import com.elcholostudios.userlogin.util.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class OnServerReload implements Listener {

    private final Utils utils = new Utils();

    @EventHandler
    public void onServerReload(@NotNull ServerReloadEvent e) {
        CommandSender sender = e.getSource();
        this.utils.reloadWarn(sender instanceof Player ? (Player) sender : null);
    }
}
