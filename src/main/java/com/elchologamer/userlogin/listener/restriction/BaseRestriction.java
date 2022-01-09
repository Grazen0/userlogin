package com.elchologamer.userlogin.listener.restriction;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.api.UserLoginAPI;
import com.elchologamer.userlogin.api.event.RestrictionEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerEvent;

abstract class BaseRestriction<E extends Event> implements Listener {

    private final UserLogin plugin = UserLogin.getPlugin();
    protected String configKey;

    protected BaseRestriction(String configKey) {
        this.configKey = configKey;
    }

    public boolean shouldRestrict(E event) {
        if (
                !plugin.getConfig().getBoolean(
                        "restrictions." + configKey,
                        plugin.getConfig().getBoolean("restrictions." + configKey + ".enabled")
                )
        ) return false;

        RestrictionEvent<E> restrictionEvent = new RestrictionEvent<>(event);

        Player player = getEventPlayer(event);

        if (player == null || UserLoginAPI.isLoggedIn(player)) return false;

        if (!event.isAsynchronous() || restrictionEvent.isAsynchronous()) {
            plugin.getServer().getPluginManager().callEvent(restrictionEvent);
        }

        return !restrictionEvent.isCancelled();
    }

    protected Player getEventPlayer(E event) {
        if (event instanceof PlayerEvent) {
            return ((PlayerEvent) event).getPlayer();
        } else if (event instanceof EntityEvent) {
            EntityEvent ee = (EntityEvent) event;

            if (ee.getEntityType() == EntityType.PLAYER) {
                return (Player) ee.getEntity();
            }
        }

        return null;
    }

    public UserLogin getPlugin() {
        return plugin;
    }
}