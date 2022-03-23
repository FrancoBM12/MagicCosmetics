package com.francobm.magicosmetics.listeners;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.cache.inventories.Menu;
import com.francobm.magicosmetics.cache.inventories.menus.FreeColoredMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.scheduler.BukkitRunnable;

public class InventoryListener implements Listener {

    @EventHandler
    public void onDrag(InventoryDragEvent event){
        InventoryHolder holder = event.getInventory().getHolder();
        if(holder instanceof FreeColoredMenu){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onClick(InventoryClickEvent event){
        InventoryHolder holder = event.getInventory().getHolder();
        if(holder instanceof FreeColoredMenu){
            FreeColoredMenu menu = (FreeColoredMenu) holder;
            menu.handleMenu(event);
            return;
        }
        if(holder instanceof Menu){
            event.setCancelled(true);
            if(event.getCurrentItem() == null) return;
            if(event.getClickedInventory() == null) return;
            if(event.getClickedInventory().getType() == InventoryType.PLAYER) return;
            Menu menu = (Menu) holder;
            menu.handleMenu(event);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event){
        InventoryHolder holder = event.getInventory().getHolder();
        if(holder instanceof FreeColoredMenu){
            FreeColoredMenu menu = (FreeColoredMenu) holder;
            menu.returnItem();
        }
    }
}
