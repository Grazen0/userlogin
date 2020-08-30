package com.elchologamer.userlogin.listeners;

import com.elchologamer.userlogin.api.UserLoginAPI;
import com.elchologamer.userlogin.api.event.ServerReloadEvent;
import com.elchologamer.userlogin.util.Utils;
import com.elchologamer.userlogin.util.lists.Path;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.jetbrains.annotations.NotNull;

public class GeneralListener implements Listener {

    private final Utils utils = new Utils();

    @EventHandler
    public void onItemDrop(@NotNull PlayerDropItemEvent e) {
        if (utils.getConfig().getBoolean("restrictions.itemDrop")
                && !UserLoginAPI.isLoggedIn(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onItemPickup(@NotNull EntityPickupItemEvent e) {
        if (e.getEntity().getType().equals(EntityType.PLAYER)
                && utils.getConfig().getBoolean("restrictions.itemPickup")
                && !UserLoginAPI.isLoggedIn((Player) e.getEntity()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerCommand(@NotNull PlayerCommandPreprocessEvent e) throws NullPointerException {
        if (!UserLoginAPI.isLoggedIn(e.getPlayer()) && utils.getConfig().getBoolean("restrictions.commands") &&
                !e.getMessage().startsWith("/login") && !e.getMessage().startsWith("/register"))
            e.setCancelled(true);
    }

    @EventHandler
    public void onServerReload(@NotNull ServerReloadEvent e) {
        CommandSender sender = e.getSource();
        this.utils.reloadWarn(sender instanceof Player ? (Player) sender : null);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBroken(@NotNull BlockBreakEvent e) {
        if (utils.getConfig().getBoolean("restrictions.blockBreak") &&
                !Utils.loggedIn.get(e.getPlayer().getUniqueId()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onChatMessage(@NotNull AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (!utils.getConfig().getBoolean("restrictions.chat") ||
                Utils.loggedIn.get(player.getUniqueId())) return;

        e.setCancelled(true);
        new Utils().sendMessage(Path.CHAT_DISABLED, player);
    }
}
