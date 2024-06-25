package com.francobm.magicosmetics.listeners;

import com.francobm.magicosmetics.cache.inventories.Menu;
import com.francobm.magicosmetics.cache.inventories.menus.FreeColoredMenu;
import com.francobm.magicosmetics.cache.inventories.menus.TokenMenu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;

public class InventoryListener implements Listener {

    @EventHandler
    public void onDrag(InventoryDragEvent event){
        InventoryHolder holder = event.getInventory().getHolder();
        if(holder instanceof FreeColoredMenu){
            event.setCancelled(true);
        }
        if(holder instanceof TokenMenu){
            TokenMenu menu = (TokenMenu) holder;
            if(!menu.isDrag()) return;
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
        if(holder instanceof TokenMenu){
            TokenMenu menu = (TokenMenu) holder;
            if(menu.isDrag()){
                menu.handleMenu(event);
                return;
            }
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
        if(holder instanceof TokenMenu){
            TokenMenu menu = (TokenMenu) holder;
            menu.returnItem();
        }
    }
}
