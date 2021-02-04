package com.elchologamer.userlogin.util.manager;

import com.elchologamer.userlogin.util.ULPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerManager extends HashMap<UUID, ULPlayer> {

    @Override
    public void clear() {
        for (ULPlayer p : values()) {
            p.cancelTimeout();
            p.cancelRepeatingMessage();
            p.setIP(null);
        }

        super.clear();
    }

    public ULPlayer get(Player player) {
        UUID uuid = player.getUniqueId();
        if (containsKey(uuid)) return super.get(player);

        return put(uuid, new ULPlayer(player));
    }
}
