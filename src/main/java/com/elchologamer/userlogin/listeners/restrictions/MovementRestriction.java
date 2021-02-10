package com.elchologamer.userlogin.listeners.restrictions;

import com.elchologamer.userlogin.api.UserLoginAPI;
import com.elchologamer.userlogin.util.Restriction;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MovementRestriction extends Restriction<PlayerMoveEvent> {

    private final Map<UUID, Integer> warnCoolDown = new HashMap<>();

    public MovementRestriction() {
        super("movement.enabled");
    }

    @Override
    public boolean shouldRestrict(PlayerMoveEvent e) {
        if (UserLoginAPI.isLoggedIn(e.getPlayer())) return false;

        ConfigurationSection section = getPlugin().getConfig().getConfigurationSection("restrictions");
        assert section != null;

        return section.getBoolean("movement.enabled", section.getBoolean("movement"));
    }

    @EventHandler
    public void handle(PlayerMoveEvent e) {
        FileConfiguration config = getPlugin().getConfig();
        if (!shouldRestrict(e)) return;

        Player p = e.getPlayer();
        Location from = e.getFrom();
        Location to = e.getTo();

        if (to == null || (from.getX() == to.getX() && from.getZ() == to.getZ())) return;

        // Move back
        Vector speed = p.getVelocity();
        from.setY(to.getY());
        from.setYaw(to.getYaw());
        from.setPitch(to.getPitch());

        p.teleport(from);
        p.setVelocity(speed);

        // Warn message system
        ConfigurationSection section = config.getConfigurationSection("restrictions");
        if (section == null) return;

        int frequency = section.getInt(
                "movement.warnFrequency",
                section.getInt("moveWarnFrequency",
                        section.getInt("move-warn-frequency", -1))
        );
        if (frequency <= 0) return;

        UUID uuid = p.getUniqueId();
        Integer current = warnCoolDown.get(uuid);
        if (current == null) current = 0;

        current++;
        if (current >= frequency) {
            // Send warning message and reset counter
            getPlugin().getPlayer(p).sendPathMessage("messages.move_warning");
            current = 0;
        }

        warnCoolDown.put(uuid, current);
    }
}
