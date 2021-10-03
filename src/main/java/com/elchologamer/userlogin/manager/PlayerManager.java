package com.elchologamer.userlogin.manager;

import com.elchologamer.userlogin.ULPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerManager extends HashMap<UUID, ULPlayer> {

    public ULPlayer getOrCreate(Player player) {
        UUID uuid = player.getUniqueId();

        if (containsKey(uuid)) {
            return get(uuid);
        } else {
            ULPlayer newPlayer = new ULPlayer(player);
            put(uuid, newPlayer);

            return newPlayer;
        }
    }
}
