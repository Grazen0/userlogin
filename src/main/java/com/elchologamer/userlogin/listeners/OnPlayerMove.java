package com.elchologamer.userlogin.listeners;

import com.elchologamer.userlogin.util.Utils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class OnPlayerMove implements Listener {

    private final Utils utils = new Utils();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(@NotNull PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (!utils.getConfig().getBoolean("restrictions.movement") ||
                (e.getTo() != null && (!moved(e.getFrom(), e.getTo()))) ||
                Utils.loggedIn.get(player.getUniqueId())) return;

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

    private boolean moved(@NotNull Location from, @NotNull Location to) {
        return from.getX() != to.getX() || from.getZ() != from.getZ();
    }
}
