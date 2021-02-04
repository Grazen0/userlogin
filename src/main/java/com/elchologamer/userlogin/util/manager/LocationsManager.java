package com.elchologamer.userlogin.util.manager;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.api.CustomConfig;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LocationsManager {

    private final UserLogin plugin;
    private final CustomConfig locationsConfig;

    public LocationsManager() {
        plugin = UserLogin.getPlugin();
        locationsConfig = new CustomConfig(plugin, "locations.yml");
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

        // Return actual location
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
