package com.elchologamer.userlogin.util;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.api.types.AuthType;
import com.elchologamer.userlogin.commands.AuthCommand;
import com.elchologamer.userlogin.util.extensions.ULPlayer;
import com.github.games647.fastlogin.core.hooks.AuthPlugin;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FastLoginHook implements AuthPlugin<Player> {

    private final UserLogin plugin = UserLogin.getPlugin();

    @Override
    public boolean forceLogin(Player player) {
        ULPlayer ulPlayer = plugin.getPlayer(player);
        if (ulPlayer.isLoggedIn()) return false;

        AuthCommand.login(ulPlayer, AuthType.LOGIN);
        return true;
    }

    @Override
    public boolean forceRegister(Player player, String password) {
        try {
            plugin.getDB().createPassword(player.getUniqueId(), password);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isRegistered(String playerName) {
        UUID uuid = Utils.fetchPlayerUUID(playerName);
        if (uuid == null) return false;

        return plugin.getDB().isRegistered(uuid);
    }
}
