package com.elchologamer.userlogin.listeners.restrictions;

import com.elchologamer.userlogin.util.Restriction;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MovementRestriction extends Restriction<PlayerMoveEvent> {

    private final Map<UUID, Integer> warnCoolDown = new HashMap<>();

    public MovementRestriction() {
        super("movement");
    }

    @Override
    public void handle(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Location from = e.getFrom();
        Location to = e.getTo();

        if (to == null || (from.getX() == to.getX() && from.getZ() == to.getZ()))
            return;

        // Move back
        Vector speed = p.getVelocity();
        p.teleport(
                new Location(from.getWorld(), from.getX(), to.getY(), from.getZ(), to.getYaw(), to.getPitch())
        );
        p.setVelocity(speed);

        // Warn message system
        ConfigurationSection sec = getPlugin().getConfig().getConfigurationSection("restrictions");
        if (sec == null) return;

        int frequency = sec.getInt(
                "moveWarnFrequency",
                sec.getInt("move-warn-frequency", -1)
        );
        if (frequency < 0) return;

        UUID uuid = p.getUniqueId();
        Integer current = warnCoolDown.get(uuid);
        if (current == null) current = 0;

        current++;
        if (current >= frequency) {
            // Send warning message and reset counter
            getPlugin().getPlayer(p).sendPathMessage("messages.move-warning");
            current = 0;
        }

        warnCoolDown.put(uuid, current);
    }
}
