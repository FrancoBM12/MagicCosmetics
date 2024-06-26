package com.francobm.magicosmetics.cache.cosmetics;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.api.Cosmetic;
import com.francobm.magicosmetics.api.CosmeticType;
import com.francobm.magicosmetics.utils.DefaultAttributes;
import com.google.common.collect.Multimap;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Hat extends Cosmetic implements CosmeticInventory {

    private boolean overlaps;
    private double offSetY;
    private ItemStack currentItemSaved = null;
    private ItemStack combinedItem = null;

    public Hat(String id, String name, ItemStack itemStack, int modelData, boolean colored, CosmeticType cosmeticType, Color color, boolean overlaps, String permission, boolean texture, boolean hideMenu, boolean useEmote, double offSetY, NamespacedKey namespacedKey) {
        super(id, name, itemStack, modelData, colored, cosmeticType, color, permission, texture, hideMenu, useEmote, namespacedKey);
        this.overlaps = overlaps;
        this.offSetY = offSetY;
    }

    @Override
    protected void updateCosmetic(Cosmetic cosmetic) {
        super.updateCosmetic(cosmetic);
        Hat hat = (Hat) cosmetic;
        overlaps = hat.overlaps;
        offSetY = hat.offSetY;
    }

    @Override
    public boolean update() {
        boolean result = super.update();
        if(result)
            active();
        return result;
    }

    @Override
    public void active() {
        if(lendEntity != null){
            lendToEntity();
            return;
        }
        if(!overlaps) {
            ItemStack itemStack = player.getInventory().getHelmet();
            if(currentItemSaved != null) {
                player.getInventory().setHelmet(currentItemSaved);
                return;
            }
            if(itemStack == null || itemStack.getType().isAir() || isCosmetic(itemStack)) {
                //Equip Helmet Without combined.
                player.getInventory().setHelmet(getItemPlaceholders(player));
                return;
            }
            currentItemSaved = itemStack;
            return;
        }
        //Equip hat combined with helmet saved in cache
        if(currentItemSaved != null) {
            combinedItem = combinedItems(currentItemSaved);
            player.getInventory().setHelmet(combinedItem);
            return;
        }
        ItemStack itemStack = player.getInventory().getHelmet();
        if(itemStack == null || itemStack.getType().isAir() || isCosmetic(itemStack)) {
            //Equip Helmet Without combined.
            player.getInventory().setHelmet(getItemPlaceholders(player));
            return;
        }
        combinedItem = combinedItems(itemStack);
        player.getInventory().setHelmet(combinedItem);
    }

    public ItemStack changeItem(ItemStack originalItem) {
        if(isCosmetic(originalItem)) return null;
        if(!overlaps){
            if(originalItem == null || originalItem.getType().isAir()) {
                if(currentItemSaved == null || currentItemSaved.getType().isAir()) {
                    player.getInventory().setHelmet(getItemPlaceholders(player));
                    return null;
                }
            }
            ItemStack helmet;
            if(player.getInventory().getHelmet() != null && player.getInventory().getHelmet().isSimilar(currentItemSaved))
                helmet = player.getInventory().getHelmet().clone();
            else
                helmet = currentItemSaved != null ? currentItemSaved.clone() : null;
            currentItemSaved = originalItem;
            player.getInventory().setHelmet(currentItemSaved);
            return helmet;
        }
        if(originalItem == null || originalItem.getType().isAir()) return null;
        ItemStack helmet = currentItemSaved != null ? MagicCosmetics.getInstance().getVersion().getItemSavedWithNBTsUpdated(combinedItem, currentItemSaved.clone()) : null;
        combinedItem = combinedItems(originalItem);
        player.getInventory().setHelmet(combinedItem);
        return helmet;
    }

    public void leftItem() {
        if(currentItemSaved == null) return;
        if(!overlaps){
            if(player.getInventory().getHelmet() == null || player.getInventory().getHelmet().getType().isAir()) return;
            if(isCosmetic(player.getInventory().getHelmet())) return;
            if(player.getInventory().getHelmet().isSimilar(currentItemSaved))
                player.setItemOnCursor(currentItemSaved.clone());
            else
                player.setItemOnCursor(player.getInventory().getHelmet().clone());
            currentItemSaved = null;
            player.getInventory().setHelmet(getItemPlaceholders(player));
            return;
        }
        ItemStack itemSavedUpdated = MagicCosmetics.getInstance().getVersion().getItemSavedWithNBTsUpdated(combinedItem, currentItemSaved.clone());
        player.setItemOnCursor(itemSavedUpdated);
        currentItemSaved = null;
        combinedItem = null;
        player.getInventory().setHelmet(getItemPlaceholders(player));
    }

    @Override
    public ItemStack leftItemAndGet() {
        if(currentItemSaved == null) return null;
        if(!overlaps) {
            if(player.getInventory().getHelmet() == null || player.getInventory().getHelmet().getType().isAir()) return null;
            if(isCosmetic(player.getInventory().getHelmet())) return null;
            ItemStack getItem;
            if(player.getInventory().getHelmet().isSimilar(currentItemSaved))
                getItem = currentItemSaved.clone();
            else
                getItem = player.getInventory().getHelmet().clone();
            currentItemSaved = null;
            player.getInventory().setHelmet(getItemPlaceholders(player));
            return getItem;
        }
        ItemStack getItem = MagicCosmetics.getInstance().getVersion().getItemSavedWithNBTsUpdated(combinedItem, currentItemSaved.clone());
        currentItemSaved = null;
        combinedItem = null;
        player.getInventory().setHelmet(getItemPlaceholders(player));
        return getItem;
    }

    private ItemStack combinedItems(ItemStack originalItem) {
        this.currentItemSaved = originalItem;
        ItemStack cosmeticItem = getItemPlaceholders(player);
        if(currentItemSaved == null) return cosmeticItem;
        ItemMeta cosmeticMeta = cosmeticItem.getItemMeta();
        ItemMeta itemSaveMeta = (currentItemSaved.hasItemMeta() ? currentItemSaved.getItemMeta() : Bukkit.getItemFactory().getItemMeta(currentItemSaved.getType()));
        if(cosmeticMeta == null || itemSaveMeta == null) return cosmeticItem;
        if(!itemSaveMeta.getItemFlags().isEmpty())
            cosmeticMeta.addItemFlags(itemSaveMeta.getItemFlags().toArray(new ItemFlag[0]));
        List<String> lore = cosmeticMeta.hasLore() ? cosmeticMeta.getLore() : new ArrayList<>();
        if(itemSaveMeta.getLore() != null && !itemSaveMeta.getLore().isEmpty()) {
            lore.add("");
            lore.addAll(itemSaveMeta.getLore());
        }
        cosmeticMeta.setLore(lore);

        Multimap<Attribute, AttributeModifier> attributes = itemSaveMeta.getAttributeModifiers() == null ? DefaultAttributes.defaultsOf(currentItemSaved) : itemSaveMeta.getAttributeModifiers();
        cosmeticMeta.setAttributeModifiers(attributes);
        cosmeticItem.setItemMeta(cosmeticMeta);
        cosmeticItem = MagicCosmetics.getInstance().getVersion().getItemWithNBTsCopy(currentItemSaved, cosmeticItem);
        return cosmeticItem;
    }

    @Override
    public void lendToEntity() {
        if(lendEntity.getEquipment() == null) return;
        if(lendEntity.getEquipment().getHelmet() != null && lendEntity.getEquipment().getHelmet().isSimilar(getItemColor(player))) return;
        lendEntity.getEquipment().setHelmet(getItemColor(player));
    }

    @Override
    public void hide(Player player) {

    }

    @Override
    public void show(Player player) {

    }

    @Override
    public void setHideCosmetic(boolean hideCosmetic) {
        super.setHideCosmetic(hideCosmetic);
        if(hideCosmetic)
            clear();
        else
            active();
    }

    @Override
    public void clear() {
        if(isHideCosmetic()){
            player.getInventory().setHelmet(null);
            return;
        }
        if(!overlaps) {
            if(currentItemSaved == null)
                player.getInventory().setHelmet(null);
            return;
        }
        if(currentItemSaved != null){
            //Clear Hat With helmet save in cache
            ItemStack itemSavedUpdated = MagicCosmetics.getInstance().getVersion().getItemSavedWithNBTsUpdated(combinedItem, currentItemSaved.clone());
            player.getInventory().setHelmet(itemSavedUpdated);
            currentItemSaved = null;
            return;
        }
        player.getInventory().setHelmet(null);
    }

    @Override
    public void clearClose() {
        if(!overlaps) {
            if(currentItemSaved == null)
                player.getInventory().setHelmet(null);
            return;
        }
        if(currentItemSaved != null){
            //Clear Hat With helmet save in cache
            player.getInventory().setHelmet(currentItemSaved.clone());
            currentItemSaved = null;
            return;
        }
        player.getInventory().setHelmet(null);
    }

    public boolean isOverlaps() {
        return overlaps;
    }

    public double getOffSetY() {
        return offSetY;
    }

    public ItemStack getCurrentItemSaved() {
        return currentItemSaved;
    }

    public void setCurrentItemSaved(ItemStack currentItemSaved) {
        this.currentItemSaved = currentItemSaved;
    }
}
