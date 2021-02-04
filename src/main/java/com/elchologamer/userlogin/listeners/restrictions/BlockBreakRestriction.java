package com.elchologamer.userlogin.listeners.restrictions;

import com.elchologamer.userlogin.api.UserLoginAPI;
import com.elchologamer.userlogin.util.Restriction;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakRestriction extends Restriction<BlockBreakEvent> {

    public BlockBreakRestriction() {
        super("blobkBreak");
    }

    @Override
    public void handle(BlockBreakEvent e) {
        if (UserLoginAPI.isLoggedIn(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
}
