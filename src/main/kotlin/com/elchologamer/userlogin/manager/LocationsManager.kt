package com.elchologamer.userlogin.manager

import com.elchologamer.userlogin.UserLogin.Companion.plugin
import com.elchologamer.userlogin.util.CustomConfig
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

class LocationsManager {
    val locations = CustomConfig("locations.yml")

    fun savePlayerLocation(player: Player) {
        saveLocation("playerLocations." + player.uniqueId, player.location)
    }

    fun saveLocation(key: String, location: Location) {
        val section = locations.config.createSection(key)

        section["world"] = location.world!!.name
        section["x"] = location.x
        section["y"] = location.y
        section["z"] = location.z
        section["yaw"] = location.yaw
        section["pitch"] = location.pitch

        locations.save()
    }

    fun getPlayerLocation(player: Player, def: Location): Location {
        return getPlayerLocation(player) ?: def
    }

    fun getPlayerLocation(player: Player): Location? {
        return getLocation("playerLocations." + player.uniqueId)
    }

    fun getLocation(key: String, def: Location): Location = getLocation(key) ?: def

    fun getLocation(key: String): Location? {
        val section: ConfigurationSection = locations.config.getConfigurationSection(key) ?: return null

        // Get world
        val worldName = section.getString("world") ?: ""
        val world = plugin.server.getWorld(worldName) ?: plugin.server.worlds[0]

        val spawn = world.spawnLocation

        // Return location
        return Location(
            world,
            section.getDouble("x", spawn.x),
            section.getDouble("y", spawn.y),
            section.getDouble("z", spawn.z),
            section.getDouble("yaw", spawn.yaw.toDouble()).toFloat(),
            section.getDouble("pitch", spawn.pitch.toDouble()).toFloat()
        )
    }

}