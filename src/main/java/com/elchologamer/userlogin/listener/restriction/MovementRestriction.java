package com.elchologamer.userlogin.listener.restriction;

import com.elchologamer.userlogin.ULPlayer;
import com.elchologamer.userlogin.api.UserLoginAPI;
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

public class MovementRestriction extends BaseRestriction<PlayerMoveEvent> {

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
    public void handle(PlayerMoveEvent event) {
        FileConfiguration config = getPlugin().getConfig();
        if (!shouldRestrict(event)) return;

        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        if (to == null || (from.getX() == to.getX() && from.getZ() == to.getZ())) return;

        // Move back
        Vector speed = player.getVelocity();
        from.setY(to.getY());
        from.setYaw(to.getYaw());
        from.setPitch(to.getPitch());

        player.teleport(from);
        player.setVelocity(speed);

        // Warn message system
        ConfigurationSection section = config.getConfigurationSection("restrictions");
        if (section == null) return;

        int frequency = section.getInt(
                "movement.warnFrequency",
                section.getInt("moveWarnFrequency",
                        section.getInt("move-warn-frequency", -1))
        );
        if (frequency <= 0) return;

        UUID uuid = player.getUniqueId();
        Integer current = warnCoolDown.get(uuid);
        if (current == null) current = 0;

        current++;
        if (current >= frequency) {
            // Send warning message and reset counter
            ULPlayer.get(player).sendMessage("messages.move_warning");
            current = 0;
        }

        warnCoolDown.put(uuid, current);
    }
}