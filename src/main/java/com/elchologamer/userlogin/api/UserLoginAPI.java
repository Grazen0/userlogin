package com.elchologamer.userlogin.api;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.util.Utils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@SuppressWarnings("unused")
public class UserLoginAPI {

    /**
     * Checks if a player is logged onto the server
     *
     * @param player The player to check for
     * @return True if the player is logged in, false otherwise
     */
    public static boolean isLoggedIn(@NotNull String player) {
        Player p = UserLogin.getPlugin().getServer().getPlayer(player);
        if (p == null) return false;
        return isLoggedIn(p);
    }

    /**
     * Checks if a player is logged onto the server
     *
     * @param player The player to check for
     * @return True if the player is logged in, false otherwise
     */
    public static boolean isLoggedIn(@NotNull Player player) {
        return isLoggedIn(player.getUniqueId());
    }

    /**
     * Checks if a player with a UUID is logged onto the server
     *
     * @param uuid The UUID of the player to check for
     * @return True if the player is logged in, false otherwise
     */
    public static boolean isLoggedIn(UUID uuid) {
        return Utils.loggedIn.get(uuid);
    }
}
