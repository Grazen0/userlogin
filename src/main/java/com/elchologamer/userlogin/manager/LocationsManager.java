package com.elchologamer.userlogin.manager;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.util.CustomConfig;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class LocationsManager {

    private final UserLogin plugin = UserLogin.getPlugin();
    private final CustomConfig locations = new CustomConfig("locations.yml", false);

    public void savePlayerLocation(Player player) {
        saveLocation("playerLocations." + player.getUniqueId(), player.getLocation());
    }

    public void saveLocation(String key, Location location) {
        ConfigurationSection section = locations.get().createSection(key);

        section.set("world", location.getWorld().getName());
        section.set("x", location.getX());
        section.set("y", location.getY());
        section.set("z", location.getZ());
        section.set("yaw", location.getYaw());
        section.set("pitch", location.getPitch());

        locations.save();
    }

    public Location getPlayerLocation(Player player, Location def) {
        Location loc = getPlayerLocation(player);
        return loc != null ? loc : def;
    }

    public Location getPlayerLocation(Player player) {
        return getLocation("playerLocations." + player.getUniqueId());
    }

    public Location getLocation(String key, Location def) {
        Location loc = getLocation(key);
        return loc != null ? loc : def;
    }

    public Location getLocation(String key) {
        ConfigurationSection section = locations.get().getConfigurationSection(key);
        if (section == null) return null;

        // Get world
        World world = plugin.getServer().getWorld(section.getString("world", ""));
        if (world == null) world = plugin.getServer().getWorlds().get(0);

        Location spawn = world.getSpawnLocation();

        return new Location(
                world,
                section.getDouble("x", spawn.getX()),
                section.getDouble("y", spawn.getY()),
                section.getDouble("z", spawn.getZ()),
                (float) section.getDouble("yaw", spawn.getYaw()),
                (float) section.getDouble("pitch", spawn.getPitch())
        );
    }

    public void reload() {
        locations.reload();
    }

}