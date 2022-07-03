package com.francobm.magicosmetics.provider;

import dev.lone.itemsadder.api.CustomEntity;
import dev.lone.itemsadder.api.CustomPlayer;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemsAdder {

    public boolean existModel(String namespaceId) {
        return CustomEntity.isInRegistry(namespaceId);
    }

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

    public String replaceFontImageWithoutColor(String id){
        return ChatColor.stripColor(FontImageWrapper.replaceFontImages(id));
    }

    public String replaceFontImages(String id){
        return FontImageWrapper.replaceFontImages(id);
    }

    public void stopEmote(Player player){
        CustomPlayer.stopEmote(player);
    }

    public boolean hasEmote(Player player){
        try {
            return CustomPlayer.byAlreadySpawned(player) != null;
        } catch (Exception ignored) {
        }
        return false;
    }
}
