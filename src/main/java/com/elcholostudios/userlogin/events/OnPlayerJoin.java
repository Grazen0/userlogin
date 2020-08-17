package com.elcholostudios.userlogin.events;

import com.elcholostudios.userlogin.UserLogin;
import com.elcholostudios.userlogin.util.Utils;
import com.elcholostudios.userlogin.util.lists.Location;
import com.elcholostudios.userlogin.util.lists.Path;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnPlayerJoin implements Listener {

    private final Utils utils = new Utils();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        Player player = e.getPlayer();
        Utils.loggedIn.put(player.getUniqueId(), false);

        // Set a new timeout
        utils.cancelTimeout(player);
        if (utils.getConfig().getBoolean("timeout.enabled")) {
            int seconds = utils.getConfig().getInt("timeout.time");
            int id = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(
                    UserLogin.plugin, () -> kickPlayer(player), seconds * 20);
            Utils.timeouts.put(player.getUniqueId(), id);
        }

        // Send respective welcome message
        if (utils.isRegistered(player))
            utils.sendMessage(Path.WELCOME_LOGIN, player);
        else
            utils.sendMessage(Path.WELCOME_REGISTER, player);

        // Teleport to login position if enabled
        if (utils.getConfig().getBoolean("teleports.toLogin"))
            player.teleport(utils.getLocation(Location.LOGIN));
    }

    private void kickPlayer(Player player) {
        player.kickPlayer(UserLogin.messagesFile.get().getString(Path.TIMEOUT));
    }
}
