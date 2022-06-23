package com.francobm.magicosmetics.cache;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.api.CosmeticType;
import com.francobm.magicosmetics.nms.NPC.ItemSlot;
import com.francobm.magicosmetics.utils.XMaterial;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Hat extends Cosmetic{

    private final boolean overlaps;

    public Hat(String id, String name, ItemStack itemStack, int modelData, boolean colored, CosmeticType cosmeticType, Color color, boolean overlaps, String permission, boolean texture, boolean hideMenu) {
        super(id, name, itemStack, modelData, colored, cosmeticType, color, permission, texture, hideMenu);
        this.overlaps = overlaps;
    }

    @Override
    public void active(Player player) {
        ItemStack itemStack = player.getInventory().getHelmet();
        if(overlaps){
            /*if(itemStack == null || itemStack.getType() == XMaterial.AIR.parseMaterial()){
                player.getInventory().setHelmet(getItemColor(player));
            }*/
            MagicCosmetics.getInstance().getVersion().equip(player, ItemSlot.HELMET, getItemColor(player));
            return;
        }
        if(itemStack != null){
            return;
        }
        player.getInventory().setHelmet(getItemColor(player));

    }

    @Override
    public void clear(Player player) {
        ItemStack itemStack = player.getInventory().getHelmet();
        if(overlaps){
            MagicCosmetics.getInstance().getVersion().equip(player, ItemSlot.HELMET, itemStack);
            /*MagicCosmetics.getInstance().getVersion().equip(player, ItemSlot.HELMET, XMaterial.AIR.parseItem());
            if(itemStack != null && itemStack.getType() == getItemColor().getType()){
                player.getInventory().setHelmet(null);
                return;
            }*/
            return;
        }
        if(itemStack == null){
            return;
        }
        if(itemStack.getType() != getItemColor().getType()){
            return;
        }
        player.getInventory().setHelmet(null);
    }

    public boolean isOverlaps() {
        return overlaps;
    }
}
