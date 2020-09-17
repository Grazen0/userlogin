package com.elchologamer.userlogin.listeners;

import com.elchologamer.userlogin.api.UserLoginAPI;
import com.elchologamer.userlogin.util.Path;
import com.elchologamer.userlogin.util.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OnPlayerMove implements Listener {

    private final Map<UUID, Integer> warnCoolDown = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (UserLoginAPI.isLoggedIn(player)
                || !Utils.getConfig().getBoolean("restrictions.movement")
                || (e.getTo() != null && (!moved(e.getFrom(), e.getTo()))))
            return;

        Vector speed = player.getVelocity();
        player.teleport(new Location(e.getFrom().getWorld(), e.getFrom().getX(), e.getTo().getY(), e.getFrom().getZ(),
                e.getTo().getYaw(), e.getTo().getPitch()));
        player.setVelocity(speed);

        // Warn message system
        int frequency = Utils.getConfig().getInt("restrictions.move-warn-frequency", -1);
        if (frequency < 0) return;

        UUID uuid = player.getUniqueId();
        Integer current = warnCoolDown.get(uuid);
        if (current == null) {
            warnCoolDown.put(uuid, 1);
            return;
        }
        current++;
        if (current >= frequency) {
            Utils.sendMessage(Path.MOVE_WARNING, player);
            current = 0;
        }
        warnCoolDown.put(uuid, current);
    }

    private boolean moved(Location from, Location to) {
        return from.getX() != to.getX() || from.getZ() != from.getZ();
    }
}
