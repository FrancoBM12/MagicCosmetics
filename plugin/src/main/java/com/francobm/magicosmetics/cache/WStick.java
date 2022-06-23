package com.francobm.magicosmetics.cache;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.api.CosmeticType;
import com.francobm.magicosmetics.nms.NPC.ItemSlot;
import com.francobm.magicosmetics.utils.XMaterial;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WStick extends Cosmetic{

    private final boolean overlaps;

    public WStick(String id, String name, ItemStack itemStack, int modelData, boolean colored, CosmeticType cosmeticType, Color color, String permission, boolean texture, boolean overlaps, boolean hideMenu) {
        super(id, name, itemStack, modelData, colored, cosmeticType, color, permission, texture, hideMenu);
        this.overlaps = overlaps;
    }

    @Override
    public void active(Player player) {
        ItemStack itemStack = player.getInventory().getItemInOffHand();
        if(overlaps){
            /*if(itemStack == null || itemStack.getType() == XMaterial.AIR.parseMaterial()){
                player.getInventory().setHelmet(getItemColor(player));
            }*/
            MagicCosmetics.getInstance().getVersion().equip(player, ItemSlot.OFF_HAND, getItemColor(player));
            return;
        }
        if(itemStack.getType() != XMaterial.AIR.parseMaterial()){
            return;
        }
        player.getInventory().setItemInOffHand(getItemColor(player));
    }

    @Override
    public void clear(Player player) {
        ItemStack itemStack = player.getInventory().getItemInOffHand();
        if(overlaps){
            MagicCosmetics.getInstance().getVersion().equip(player, ItemSlot.OFF_HAND, itemStack);
            return;
        }
        if(itemStack.getType() != getItemColor().getType()){
            return;
        }
        player.getInventory().setItemInOffHand(null);
    }

    public boolean isOverlaps() {
        return overlaps;
    }
}
