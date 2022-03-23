package com.francobm.magicosmetics.provider;

import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import org.bukkit.inventory.ItemStack;

public class ItemsAdder {

    public CustomStack getCustomStack(String id){
        return CustomStack.getInstance(id);
    }

    public CustomStack getCustomStack(ItemStack itemStack){
        return CustomStack.byItemStack(itemStack);
    }

    public ItemStack getCustomItemStack(String id){
        CustomStack customStack = CustomStack.getInstance(id);
        if(customStack == null) return null;
        return customStack.getItemStack();
    }

    public String replaceFontImages(String id){
        return FontImageWrapper.replaceFontImages(id);
    }
}
