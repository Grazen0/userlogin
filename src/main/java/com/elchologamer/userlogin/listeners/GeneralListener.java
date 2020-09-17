package com.elchologamer.userlogin.listeners;

import com.elchologamer.pluginapi.event.ServerReloadEvent;
import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.api.UserLoginAPI;
import com.elchologamer.userlogin.util.Path;
import com.elchologamer.userlogin.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class GeneralListener implements Listener {

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        if (Utils.getConfig().getBoolean("restrictions.itemDrop")
                && !UserLoginAPI.isLoggedIn(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) throws NullPointerException {
        if (!UserLoginAPI.isLoggedIn(e.getPlayer()) && Utils.getConfig().getBoolean("restrictions.commands") &&
                !e.getMessage().startsWith("/login") && !e.getMessage().startsWith("/register"))
            e.setCancelled(true);
    }

    @EventHandler
    public void onServerReload(ServerReloadEvent e) {
        CommandSender sender = e.getSource();
        reloadWarn(sender instanceof Player ? (Player) sender : null);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBroken(BlockBreakEvent e) {
        if (Utils.getConfig().getBoolean("restrictions.blockBreak") &&
                !UserLoginAPI.isLoggedIn(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (!Utils.getConfig().getBoolean("restrictions.chat") ||
                UserLoginAPI.isLoggedIn(player)) return;

        e.setCancelled(true);
        Utils.sendMessage(Path.CHAT_DISABLED, player);
    }

    private void reloadWarn(Player player) {
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
