package com.elchologamer.userlogin.api;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.ULPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UserLoginAPI {

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
     * @param player The player to check for
     * @return true if the player is logged in, false otherwise
     * @deprecated As of v2.7.0, use any of the available overloads instead
     */
    @Deprecated
    public static boolean isLoggedIn(String player) {
        Player p = plugin.getServer().getPlayer(player);
        if (p == null) return false;

        return isLoggedIn(p);
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
        ULPlayer p = plugin.getPlayerManager().get(uuid);
        return p != null && p.isLoggedIn();
    }
}
