package com.francobm.customcosmetics.cache.inventories;

import com.francobm.customcosmetics.CustomCosmetics;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;

public class ContentMenu {
    private Inventory inventory;
    private String title;
    private final int size;
    private final InventoryType inventoryType;
    private final Map<Integer, SlotMenu> slotMenu;

    public ContentMenu(String title, int size, InventoryType inventoryType, Map<Integer, SlotMenu> slotMenu){
        this.title = title;
        this.size = size;
        this.inventoryType = inventoryType;
        this.slotMenu = slotMenu;
    }

    public ContentMenu(String title, int size, InventoryType inventoryType){
        this.title = title;
        this.size = size;
        this.inventoryType = inventoryType;
        this.slotMenu = new HashMap<>();
    }

    public void createInventory(InventoryHolder inventoryHolder){
        this.inventory = Bukkit.createInventory(inventoryHolder, 9*size, title);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public String getTitle() {
        return title;
    }

    public int getSize() {
        return size;
    }

    public InventoryType getInventoryType() {
        return inventoryType;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<Integer, SlotMenu> getSlotMenu() {
        return slotMenu;
    }

    public SlotMenu getSlotMenuBySlot(int slot){
        return slotMenu.get(slot);
    }

    public void removeSlotMenu(int slot){
        this.slotMenu.remove(slot);
    }
    public void addSlotMenu(SlotMenu slotMenu){
        this.slotMenu.put(slotMenu.getSlot(), slotMenu);
    }
}
