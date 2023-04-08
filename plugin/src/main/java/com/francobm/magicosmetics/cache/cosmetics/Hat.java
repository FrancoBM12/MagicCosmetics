package com.francobm.magicosmetics.cache.cosmetics;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.api.Cosmetic;
import com.francobm.magicosmetics.api.CosmeticType;
import com.francobm.magicosmetics.nms.NPC.ItemSlot;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Hat extends Cosmetic {

    private final boolean overlaps;
    private boolean clear;

    public Hat(String id, String name, ItemStack itemStack, int modelData, boolean colored, CosmeticType cosmeticType, Color color, boolean overlaps, String permission, boolean texture, boolean hideMenu, boolean useEmote) {
        super(id, name, itemStack, modelData, colored, cosmeticType, color, permission, texture, hideMenu, useEmote);
        this.overlaps = overlaps;
    }

    @Override
    public void active(Player player) {
        if(isHideCosmetic()) {
            clear(player);
            return;
        }
        ItemStack itemStack = player.getInventory().getHelmet();
        if(overlaps){
            /*if(itemStack == null || itemStack.getType() == XMaterial.AIR.parseMaterial()){
                player.getInventory().setHelmet(getItemColor(player));
            }*/
            MagicCosmetics.getInstance().getVersion().equip(player, ItemSlot.HELMET, getItemPlaceholders(player));
            clear = false;
            return;
        }
        if(itemStack != null){
            return;
        }
        player.getInventory().setHelmet(getItemPlaceholders(player));
        clear = false;
    }

    @Override
    public void clear(Player player) {
        if(clear) return;
        ItemStack itemStack = player.getInventory().getHelmet();
        if(overlaps){
            MagicCosmetics.getInstance().getVersion().equip(player, ItemSlot.HELMET, itemStack);
            /*MagicCosmetics.getInstance().getVersion().equip(player, ItemSlot.HELMET, XMaterial.AIR.parseItem());
            if(itemStack != null && itemStack.getType() == getItemColor().getType()){
                player.getInventory().setHelmet(null);
                return;
            }*/
            clear = true;
            return;
        }
        if(itemStack == null){
            return;
        }
        if(itemStack.getType() != getItemColor().getType()){
            return;
        }
        player.getInventory().setHelmet(null);
        clear = true;
    }

    public boolean isOverlaps() {
        return overlaps;
    }
}
