package com.elchologamer.userlogin.api;

import com.elchologamer.userlogin.ULPlayer;
import com.elchologamer.userlogin.UserLogin;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * The class which contains several useful methods within
 * the UserLogin API. Use the INSTANCE property
 * to access its methods.
 */
public abstract class UserLoginAPI {

    private static final UserLogin plugin = UserLogin.getPlugin();

    /**
     * Checks if a player is registered.
     *
     * @param player the player to check
     * @return true if the player is registered, false otherwise
     */
    public static boolean isRegistered(Player player) {
        return isRegistered(player.getUniqueId());
    }

    /**
     * Checks if the given UUID is registered.
     *
     * @param uuid the UUID of the player to check
     * @return true if the player is registered, false otherwise
     */
    public static boolean isRegistered(UUID uuid) {
        return plugin.getDB().isRegistered(uuid);
    }

    /**
     * Checks if a player is logged onto the server.
     *
     * @param playerName The player to check for
     * @return true if the player is logged in, false otherwise
     * @deprecated As of v2.7.0, use any of the available overloads instead
     */
    public static boolean isLoggedIn(String playerName) {
        Player player = plugin.getServer().getPlayer(playerName);
        return player != null && isLoggedIn(player);
    }

    /**
     * Checks if a player is logged onto the server.
     *
     * @param player the player to check for
     * @return true if the player is logged in, false otherwise
     */
    public static boolean isLoggedIn(Player player) {
        return isLoggedIn(player.getUniqueId());
    }

    /**
     * Checks if a player with a UUID is logged onto the server.
     *
     * @param uuid the UUID of the player to check for
     * @return true if the player is logged in, false otherwise
     */
    public static boolean isLoggedIn(UUID uuid) {
        return ULPlayer.get(uuid).isLoggedIn();
    }
}