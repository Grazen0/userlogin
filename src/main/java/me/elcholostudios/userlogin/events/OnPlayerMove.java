package me.elcholostudios.userlogin.events;

import me.elcholostudios.userlogin.UserLogin;
import me.elcholostudios.userlogin.files.PlayerDataFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class OnPlayerMove implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(@NotNull PlayerMoveEvent e){
        Player player = e.getPlayer();
        String uuid = player.getUniqueId().toString();

        if(!PlayerDataFile.get().getBoolean(uuid+".isLoggedIn") &&
        UserLogin.plugin.getConfig().getBoolean("restrictions.disableMovement")){
            if(isMovement(e.getFrom(), Objects.requireNonNull(e.getTo()))) {
                Vector speed = player.getVelocity();

                String world = UserLogin.plugin.getConfig().getString("loginSpawn.world");
                if(Objects.requireNonNull(world).equals("default")){
                    world = Bukkit.getServer().getWorlds().get(0).getName();
                }
                double x = UserLogin.plugin.getConfig().getDouble("loginSpawn.x");
                double y = e.getTo().getY();
                double z = UserLogin.plugin.getConfig().getDouble("loginSpawn.z");
                float yaw = e.getTo().getYaw();
                float pitch = e.getTo().getPitch();

                Location loc = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
                player.teleport(loc);
                player.setVelocity(speed);
            }
        }
    }

    boolean isMovement(@NotNull Location from, @NotNull Location to) {
        boolean b = false;
        if(from.getX() != to.getX()){
            b = true;
        }

        if(from.getZ() != to.getZ()){
            b = true;
        }
        return b;
    }
}
