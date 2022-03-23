package com.francobm.magicosmetics.cache;

import com.francobm.magicosmetics.utils.XMaterial;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WStick extends Cosmetic{

    public WStick(String id, String name, ItemStack itemStack, int modelData, boolean colored, CosmeticType cosmeticType, Color color) {
        super(id, name, itemStack, modelData, colored, cosmeticType, color);
    }

    @Override
    public void active(Player player) {
        ItemStack itemStack = player.getInventory().getItemInOffHand();

        if(itemStack.getType() != XMaterial.AIR.parseMaterial()){
            return;
        }
        player.getInventory().setItemInOffHand(getItemColor(player));
    }

    @Override
    public void clear(Player player) {
        ItemStack itemStack = player.getInventory().getItemInOffHand();
        if(itemStack.getType() != getItemColor().getType()){
            return;
        }
        player.getInventory().setItemInOffHand(null);
    }
}
