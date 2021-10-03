package com.elchologamer.userlogin.manager;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.util.CustomConfig;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LocationsManager {

    private final UserLogin plugin = UserLogin.getPlugin();
    private final CustomConfig locationsConfig;

    public LocationsManager() {
        locationsConfig = new CustomConfig("locations.yml");
    }

    public void saveLocation(String key, Location location) {
        ConfigurationSection section = locationsConfig.get().createSection(key);

        section.set("world", location.getWorld().getName());
        section.set("x", location.getX());
        section.set("y", location.getY());
        section.set("z", location.getZ());
        section.set("yaw", location.getYaw());
        section.set("pitch", location.getPitch());

        locationsConfig.save();
    }

    public void savePlayerLocation(Player player) {
        saveLocation("playerLocations." + player.getUniqueId(), player.getLocation());
    }

    public Location getPlayerLocation(Player player) {
        return getPlayerLocation(player.getUniqueId());
    }

    public Location getPlayerLocation(UUID uuid) {
        return getLocation("playerLocations." + uuid, getLocation("spawn"));
    }

    public Location getLocation(String key) {
        return getLocation(key, null);
    }

    public Location getLocation(String key, Location def) {
        ConfigurationSection section = locationsConfig.get().getConfigurationSection(key);
        if (section == null) return def;

        // Get world
        String worldName = section.getString("world", "");
        World world = plugin.getServer().getWorld(worldName);

        if (world == null) world = plugin.getServer().getWorlds().get(0);

        Location spawn = world.getSpawnLocation();

        // Return location
        return new Location(
                world,
                section.getDouble("x", spawn.getX()),
                section.getDouble("y", spawn.getY()),
                section.getDouble("z", spawn.getZ()),
                (float) section.getDouble("yaw", spawn.getYaw()),
                (float) section.getDouble("pitch", spawn.getPitch()));
    }

    public CustomConfig getConfig() {
        return locationsConfig;
    }
}
