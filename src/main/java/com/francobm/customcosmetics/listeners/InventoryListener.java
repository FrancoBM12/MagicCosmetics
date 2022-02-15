package com.francobm.customcosmetics.listeners;

import com.francobm.customcosmetics.cache.inventories.Menu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;

public class InventoryListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event){
        InventoryHolder holder = event.getInventory().getHolder();
        if(holder instanceof Menu){
            event.setCancelled(true);
            if(event.getCurrentItem() == null) return;
            if(event.getClickedInventory() == null) return;
            if(event.getClickedInventory().getType() == InventoryType.PLAYER) return;
            Menu menu = (Menu) holder;
            menu.handleMenu(event);
        }
    }
}
