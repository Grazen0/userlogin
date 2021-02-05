package com.elchologamer.userlogin.api;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.util.extensions.ULPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UserLoginAPI {

    private final static UserLogin plugin = UserLogin.getPlugin();

    public static boolean isRegistered(Player player) {
        UUID uuid = player.getUniqueId();
        return plugin.getDB().getPassword(uuid) != null;
    }

    /**
     * Checks if a player is logged onto the server
     *
     * @param player The player to check for
     * @return True if the player is logged in, false otherwise
     * @deprecated As of v2.7.0, use any of the available overloads instead
     */
    @Deprecated
    public static boolean isLoggedIn(String player) {
        Player p = plugin.getServer().getPlayer(player);
        if (p == null) return false;

        return isLoggedIn(p);
    }

    /**
     * Checks if a player is logged onto the server
     *
     * @param player The player to check for
     * @return True if the player is logged in, false otherwise
     */
    public static boolean isLoggedIn(Player player) {
        return isLoggedIn(player.getUniqueId());
    }

    /**
     * Checks if a player with a UUID is logged onto the server
     *
     * @param uuid The UUID of the player to check for
     * @return True if the player is logged in, false otherwise
     */
    public static boolean isLoggedIn(UUID uuid) {
        ULPlayer p = plugin.getPlayerManager().get(uuid);
        return p != null && p.isLoggedIn();
    }
}
