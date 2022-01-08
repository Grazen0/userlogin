package com.elchologamer.userlogin.listener.restriction;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickRestriction extends BaseRestriction<InventoryClickEvent> {
    public InventoryClickRestriction() {
        super("inventoryClick");
    }

    @EventHandler
    public void handle(InventoryClickEvent event) {
        if (shouldRestrict(event)) event.setCancelled(true);
    }

    @Override
    protected Player getEventPlayer(InventoryClickEvent event) {
        HumanEntity entity = event.getWhoClicked();

        if (!(entity instanceof Player)) return null;
        return (Player) entity;
    }
}
