package com.elchologamer.userlogin.util;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.api.UserLoginAPI;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerEvent;

public abstract class Restriction<T extends Event> extends BaseListener {

    private final UserLogin plugin = UserLogin.getPlugin();
    protected final String configKey;

    protected Restriction(String configKey) {
        this.configKey = configKey;
    }

    public boolean shouldRestrict(T e) {
        boolean enabled = plugin.getConfig().getBoolean("restrictions." + configKey);
        if (!enabled) return false;

        if (e instanceof PlayerEvent) {
            Player p = ((PlayerEvent) e).getPlayer();
            return !UserLoginAPI.isLoggedIn(p);
        }

        if (e instanceof EntityEvent) {
            Entity entity = ((EntityEvent) e).getEntity();
            return !entity.getType().equals(EntityType.PLAYER)
                    || !UserLoginAPI.isLoggedIn((Player) entity);
        }

        return true;
    }

    public UserLogin getPlugin() {
        return plugin;
    }
}
