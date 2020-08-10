package com.elcholostudios.userlogin.events;

import com.elcholostudios.userlogin.UserLogin;
import com.elcholostudios.userlogin.util.Utils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.Objects;

public class OnPlayerMove implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (e.getTo() != null && (!UserLogin.plugin.getConfig().getBoolean("restrictions.movement") ||
                !moved(e.getFrom(), e.getTo()) ||
                Utils.loggedIn.get(player.getUniqueId()))) return;

        double x = e.getFrom().getX();
        double y = Objects.requireNonNull(e.getTo()).getY();
        double z = e.getFrom().getZ();
        float yaw = e.getTo().getYaw();
        float pitch = e.getTo().getPitch();
        World world = e.getFrom().getWorld();

        Vector speed = player.getVelocity();
        player.teleport(new Location(world, x, y, z, yaw, pitch));
        player.setVelocity(speed);
    }

    private boolean moved(Location from, Location to) {
        return from.getX() != to.getX() || from.getZ() != from.getZ();
    }
}
