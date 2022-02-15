package com.francobm.customcosmetics.provider;

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
        return customStack.getItemStack();
    }

    public FontImageWrapper getFontImageWrapper(String id){
        return new FontImageWrapper(id);
    }

    public String getFontImageWrapperString(String id){
        FontImageWrapper fontImageWrapper = getFontImageWrapper(id);
        return fontImageWrapper.getString();
    }

    public boolean isFontImageWrapper(String id){
        return getFontImageWrapper(id).exists();
    }
}
