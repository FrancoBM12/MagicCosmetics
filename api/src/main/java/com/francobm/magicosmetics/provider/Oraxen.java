package com.francobm.magicosmetics.provider;

import org.bukkit.inventory.ItemStack;

public interface Oraxen {

    void register();

    ItemStack getItemStackById(String id);

    String replaceFontImages(String id);
}
