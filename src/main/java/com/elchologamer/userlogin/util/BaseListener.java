package com.elchologamer.userlogin.util;

import com.elchologamer.userlogin.UserLogin;
import org.bukkit.event.Listener;

public abstract class BaseListener implements Listener {

    private final UserLogin plugin = UserLogin.getPlugin();

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public UserLogin getPlugin() {
        return plugin;
    }
}
