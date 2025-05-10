package com.francobm.magicosmetics.provider;

import org.bukkit.inventory.ItemStack;

public interface ResourcePlugin {

    ItemStack getItemStackById(String id);

    String replaceFontImageWithoutColor(String id);

    String replaceFontImages(String id);

    String getProviderName();

    String getPathName();
}
