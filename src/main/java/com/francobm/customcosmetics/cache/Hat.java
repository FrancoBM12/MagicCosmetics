package com.francobm.customcosmetics.cache;

import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Hat extends Cosmetic{

    public Hat(String id, String name, ItemStack itemStack, int modelData, boolean colored, CosmeticType cosmeticType, Color color) {
        super(id, name, itemStack, modelData, colored, cosmeticType, color);
    }

    @Override
    public void active(Player player) {
        ItemStack itemStack = player.getInventory().getHelmet();
        if(itemStack != null){
            return;
        }
        player.getInventory().setHelmet(getItemColor());

    }

    @Override
    public void clear(Player player) {
        ItemStack itemStack = player.getInventory().getHelmet();
        if(itemStack == null){
            return;
        }
        if(itemStack.getType() != getItemColor().getType()){
            return;
        }
        player.getInventory().setHelmet(null);
    }
}
