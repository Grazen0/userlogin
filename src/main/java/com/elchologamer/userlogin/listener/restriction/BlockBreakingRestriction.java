package com.elchologamer.userlogin.listener.restriction;

import com.elchologamer.userlogin.api.UserLoginAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakingRestriction extends BaseRestriction<BlockBreakEvent> {

    public BlockBreakingRestriction() {
        super("blockBreaking");
    }

    @EventHandler
    public void handle(BlockBreakEvent e) {
        if (UserLoginAPI.isLoggedIn(e.getPlayer())) return;

        boolean oldBool = getPlugin().getConfig().getBoolean("restrictions.blockBreak");

        if (oldBool || shouldRestrict(e)) e.setCancelled(true);
    }
}
