package com.elchologamer.userlogin.manager

import com.elchologamer.userlogin.ULPlayer
import org.bukkit.entity.Player
import java.util.*

class PlayerManager : HashMap<UUID, ULPlayer>() {
    fun getOrCreate(player: Player): ULPlayer {
        var ulPlayer: ULPlayer? = get(player.uniqueId)

        if (ulPlayer == null) {
            ulPlayer = ULPlayer(player)
            put(player.uniqueId, ulPlayer)
        }

        return ulPlayer
    }
}