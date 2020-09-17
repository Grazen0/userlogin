package com.elchologamer.userlogin.api;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.util.Utils;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UserLoginAPI {

    public static boolean isRegistered(Player player) {
        UUID uuid = player.getUniqueId();
        return (!Utils.sqlMode() && UserLogin.getPlugin().getPlayerData().get().getKeys(true).contains(uuid.toString() + ".password"))
                || (Utils.sqlMode() && UserLogin.getPlugin().getSQL().getData().containsKey(uuid));
    }

    /**
     * Checks if a player is logged onto the server
     *
     * @param player The player to check for
     * @return True if the player is logged in, false otherwise
     */
    public static boolean isLoggedIn(String player) {
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
        Boolean b = Utils.loggedIn.get(uuid);
        return b != null && b;
    }
}
