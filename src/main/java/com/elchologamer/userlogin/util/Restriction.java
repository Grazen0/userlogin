package com.elchologamer.userlogin.util;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.api.UserLoginAPI;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerEvent;

public abstract class Restriction<T extends Event> implements Listener {

    private final UserLogin plugin;
    protected final String configKey;

    protected Restriction(String configKey) {
        this.configKey = configKey;
        plugin = UserLogin.getPlugin();
    }

    @EventHandler
    public void onEvent(T e) {
        boolean enabled = plugin.getConfig().getBoolean("restrictions." + configKey);

        if (e instanceof PlayerEvent) {
            Player p = ((PlayerEvent) e).getPlayer();
            if (!UserLoginAPI.isLoggedIn(p)) return;
        } else if (e instanceof EntityEvent) {
            Entity entity = ((EntityEvent) e).getEntity();
            if (
                    !entity.getType().equals(EntityType.PLAYER)
                            || !UserLoginAPI.isLoggedIn((Player) entity)
            ) return;
        }

        if (enabled) handle(e);
    }

    public String getConfigKey() {
        return configKey;
    }

    public abstract void handle(T event);

    public UserLogin getPlugin() {
        return plugin;
    }
}
