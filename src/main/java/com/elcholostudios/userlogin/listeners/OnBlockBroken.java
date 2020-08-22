package com.elcholostudios.userlogin.listeners;

import com.elcholostudios.userlogin.util.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class OnBlockBroken implements Listener {

    private final Utils utils = new Utils();

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBroken(BlockBreakEvent e) {
        if(utils.getConfig().getBoolean("restrictions.blockBreak") &&
                !Utils.loggedIn.get(e.getPlayer().getUniqueId()))
            e.setCancelled(true);
    }
}
