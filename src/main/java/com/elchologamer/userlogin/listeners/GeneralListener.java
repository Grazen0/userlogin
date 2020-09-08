package com.elchologamer.userlogin.listeners;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.api.UserLoginAPI;
import com.elchologamer.userlogin.api.event.ServerReloadEvent;
import com.elchologamer.userlogin.util.Utils;
import com.elchologamer.userlogin.util.lists.Path;
import org.bukkit.ChatColor;
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
import org.jetbrains.annotations.Nullable;

public class GeneralListener implements Listener {

    @EventHandler
    public void onItemDrop(@NotNull PlayerDropItemEvent e) {
        if (Utils.getConfig().getBoolean("restrictions.itemDrop")
                && !UserLoginAPI.isLoggedIn(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onItemPickup(@NotNull EntityPickupItemEvent e) {
        if (e.getEntity().getType().equals(EntityType.PLAYER)
                && Utils.getConfig().getBoolean("restrictions.itemPickup")
                && !UserLoginAPI.isLoggedIn((Player) e.getEntity()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerCommand(@NotNull PlayerCommandPreprocessEvent e) throws NullPointerException {
        if (!UserLoginAPI.isLoggedIn(e.getPlayer()) && Utils.getConfig().getBoolean("restrictions.commands") &&
                !e.getMessage().startsWith("/login") && !e.getMessage().startsWith("/register"))
            e.setCancelled(true);
    }

    @EventHandler
    public void onServerReload(@NotNull ServerReloadEvent e) {
        CommandSender sender = e.getSource();
        reloadWarn(sender instanceof Player ? (Player) sender : null);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBroken(@NotNull BlockBreakEvent e) {
        if (Utils.getConfig().getBoolean("restrictions.blockBreak") &&
                !Utils.loggedIn.get(e.getPlayer().getUniqueId()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onChatMessage(@NotNull AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (!Utils.getConfig().getBoolean("restrictions.chat") ||
                Utils.loggedIn.get(player.getUniqueId())) return;

        e.setCancelled(true);
        Utils.sendMessage(Path.CHAT_DISABLED, player);
    }

    private void reloadWarn(@Nullable Player player) {
        String msg = ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "[UserLogin: " +
                ChatColor.RESET.toString() + ChatColor.DARK_RED.toString() +
                "A reload has been detected. We advise you to restart the server, as this command is very " +
                "unstable, and will definitely cause trouble with this plugin." +
                ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "]";

        Utils.log(msg);
        if (player != null) {
            player.sendMessage(msg);
            return;
        }

        for (Player onlinePlayer : UserLogin.getPlugin().getServer().getOnlinePlayers()) {
            if (onlinePlayer.isOp()) onlinePlayer.sendMessage(msg);
        }
    }
}
