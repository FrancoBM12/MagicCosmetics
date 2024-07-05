package com.francobm.magicosmetics.cache.cosmetics;

import org.bukkit.inventory.ItemStack;

public interface CosmeticInventory {
    ItemStack changeItem(ItemStack originalItem);
    void leftItem();

    ItemStack leftItemAndGet();

    ItemStack getCurrentItemSaved();

    void setCurrentItemSaved(ItemStack currentItemSaved);

    boolean isOverlaps();

    ItemStack dropItem(boolean all);

    ItemStack getEquipment();
}
