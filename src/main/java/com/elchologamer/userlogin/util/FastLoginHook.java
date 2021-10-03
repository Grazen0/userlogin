package com.elchologamer.userlogin.util;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.api.types.AuthType;
import com.elchologamer.userlogin.command.AuthCommand;
import com.elchologamer.userlogin.ULPlayer;
import com.github.games647.fastlogin.bukkit.FastLoginBukkit;
import com.github.games647.fastlogin.core.hooks.AuthPlugin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class FastLoginHook implements AuthPlugin<Player> {

    private final UserLogin plugin = UserLogin.getPlugin();

    public void register() {
        FastLoginBukkit fastLogin = JavaPlugin.getPlugin(FastLoginBukkit.class);
        fastLogin.getCore().setAuthPluginHook(new FastLoginHook());
    }

    @Override
    public boolean forceLogin(Player player) {
        ULPlayer ulPlayer = plugin.getPlayer(player);
        if (ulPlayer.isLoggedIn()) return false;

        plugin.getServer().getScheduler().runTask(
                plugin,
                () -> AuthCommand.login(ulPlayer, AuthType.LOGIN)
        );
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
