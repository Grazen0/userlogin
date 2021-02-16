package com.elchologamer.userlogin.util;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.api.UserLoginAPI;
import com.elchologamer.userlogin.api.event.RestrictionEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerEvent;

public abstract class Restriction<E extends Event> extends BaseListener {

    private final UserLogin plugin = UserLogin.getPlugin();
    protected final String configKey;

    protected Restriction(String configKey) {
        this.configKey = configKey;
    }

    public boolean shouldRestrict(E e) {
        boolean enabled = plugin.getConfig().getBoolean("restrictions." + configKey);
        if (!enabled) return false;

        RestrictionEvent<E> event = new RestrictionEvent<>(e);

        if (e instanceof PlayerEvent) {
            Player p = ((PlayerEvent) e).getPlayer();
            event.setCancelled(UserLoginAPI.isLoggedIn(p));
        } else if (e instanceof EntityEvent) {
            Entity entity = ((EntityEvent) e).getEntity();
            event.setCancelled(
                    entity.getType().equals(EntityType.PLAYER)
                            && UserLoginAPI.isLoggedIn((Player) entity)
            );
        }

        if (!e.isAsynchronous() || event.isAsynchronous()) {
            plugin.getServer().getPluginManager().callEvent(event);
        }
        return !event.isCancelled();
    }

    public UserLogin getPlugin() {
        return plugin;
    }

    public String getConfigKey() {
        return configKey;
    }
}
