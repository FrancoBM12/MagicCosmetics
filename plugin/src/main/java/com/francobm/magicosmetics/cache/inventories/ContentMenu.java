package com.francobm.magicosmetics.cache.inventories;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ContentMenu {
    private Inventory inventory;
    private String title;
    private final int size;
    private final InventoryType inventoryType;
    private final Map<Integer, SlotMenu> slotMenu;
    private final int previewSlot;
    private final int resultSlot;
    private final Slots slots;

    public ContentMenu(String title, int size, InventoryType inventoryType, Map<Integer, SlotMenu> slotMenu){
        this.title = title;
        this.size = size;
        this.inventoryType = inventoryType;
        this.slotMenu = slotMenu;
        this.resultSlot = 0;
        this.previewSlot = 0;
        this.slots = new Slots();
    }

    public ContentMenu(String title, int size, InventoryType inventoryType, Map<Integer, SlotMenu> slotMenu, int previewSlot, int resultSlot){
        this.title = title;
        this.size = size;
        this.inventoryType = inventoryType;
        this.slotMenu = slotMenu;
        this.previewSlot = previewSlot;
        this.resultSlot = resultSlot;
        this.slots = new Slots();
    }

    public ContentMenu(String title, int size, InventoryType inventoryType){
        this.title = title;
        this.size = size;
        this.inventoryType = inventoryType;
        this.slotMenu = new HashMap<>();
        this.previewSlot = 0;
        this.resultSlot = 0;
        this.slots = new Slots();
    }

    public ContentMenu getClone() {
        Map<Integer, SlotMenu> slotMenus = new HashMap<>(slotMenu);
        return new ContentMenu(title, size, inventoryType, slotMenus, previewSlot, resultSlot);
    }

    public void createInventory(InventoryHolder inventoryHolder){
        this.inventory = Bukkit.createInventory(inventoryHolder, 9*size, title);
    }

    public void createInventory(InventoryHolder inventoryHolder, String title){
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

    public void resetSlotMenu(List<Integer> ignoredSlots){
        Iterator<SlotMenu> slotMenus = slotMenu.values().iterator();
        while (slotMenus.hasNext()){
            SlotMenu slotMenu = slotMenus.next();
            if(ignoredSlots.contains(slotMenu.getSlot())) continue;
            slotMenus.remove();
        }
    }

    public void addSlotMenu(SlotMenu slotMenu){
        this.slotMenu.put(slotMenu.getSlot(), slotMenu);
    }

    public int getResultSlot() {
        return resultSlot;
    }

    public int getPreviewSlot() {
        return previewSlot;
    }

    public Slots getSlots() {
        return slots;
    }
}
