package com.elcholostudios.userlogin;

import com.elcholostudios.userlogin.util.Utils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class UserLoginAPI {

    /**
     * Checks if a player is logged onto the server
     * @param player The player to check for
     * @return True if the player is logged in, false otherwise
     */
    public static boolean isLoggedIn(@NotNull String player) {
        Player p = UserLogin.plugin.getServer().getPlayer(player);
        if(p == null) return false;
        return isLoggedIn(p);
    }

    /**
     * Checks if a player is logged onto the server
     * @param player The player to check for
     * @return True if the player is logged in, false otherwise
     */
    public static boolean isLoggedIn(@NotNull Player player) {
        return isLoggedIn(player.getUniqueId());
    }

    /**
     * Checks if a player with a UUID is logged onto the server
     * @param uuid The UUID of the player to check for
     * @return True if the player is logged in, false otherwise
     */
    public static boolean isLoggedIn(UUID uuid) {
        return Utils.loggedIn.get(uuid);
    }
}
