package com.elchologamer.userlogin.api

import com.elchologamer.userlogin.UserLogin.Companion.plugin
import org.bukkit.entity.Player
import java.util.*

object UserLoginAPI {
    /**
     * Checks if a player is registered.
     *
     * @param player the player to check
     * @return true if the player is registered, false otherwise
     */
    fun isRegistered(player: Player): Boolean {
        return isRegistered(player.uniqueId)
    }

    /**
     * Checks if the given UUID is registered.
     *
     * @param uuid the UUID of the player to check
     * @return true if the player is registered, false otherwise
     */
    fun isRegistered(uuid: UUID?): Boolean {
        return plugin.db.isRegistered(uuid!!)
    }

    /**
     * Checks if a player is logged onto the server.
     *
     * @param player The player to check for
     * @return true if the player is logged in, false otherwise
     */
    @Deprecated("As of v2.7.0, use any of the available overloads instead")
    fun isLoggedIn(player: String?): Boolean {
        val p = plugin.server.getPlayer(player!!) ?: return false
        return isLoggedIn(p)
    }

    /**
     * Checks if a player is logged onto the server.
     *
     * @param player the player to check for
     * @return true if the player is logged in, false otherwise
     */
    fun isLoggedIn(player: Player): Boolean {
        return isLoggedIn(player.uniqueId)
    }

    /**
     * Checks if a player with a UUID is logged onto the server.
     *
     * @param uuid the UUID of the player to check for
     * @return true if the player is logged in, false otherwise
     */
    fun isLoggedIn(uuid: UUID?): Boolean {
        val ulPlayer = plugin.players[uuid]
        return ulPlayer?.loggedIn == true
    }
}