package com.francobm.magicosmetics.cache.cosmetics;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.api.Cosmetic;
import com.francobm.magicosmetics.api.CosmeticType;
import com.francobm.magicosmetics.utils.DefaultAttributes;
import com.francobm.magicosmetics.utils.Utils;
import com.google.common.collect.Multimap;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class WStick extends Cosmetic implements CosmeticInventory {

    private boolean overlaps;
    private ItemStack currentItemSaved = null;
    private ItemStack combinedItem = null;
    private boolean hasDropped;

    public WStick(String id, String name, ItemStack itemStack, int modelData, boolean colored, CosmeticType cosmeticType, Color color, String permission, boolean texture, boolean overlaps, boolean hideMenu, boolean useEmote, NamespacedKey namespacedKey) {
        super(id, name, itemStack, modelData, colored, cosmeticType, color, permission, texture, hideMenu, useEmote, namespacedKey);
        this.overlaps = overlaps;
    }

    @Override
    protected void updateCosmetic(Cosmetic cosmetic) {
        super.updateCosmetic(cosmetic);
        WStick wStick = (WStick) cosmetic;
        overlaps = wStick.overlaps;
    }

    @Override
    public boolean updateProperties() {
        boolean result = super.updateProperties();
        if(result)
            update();
        return result;
    }

    @Override
    public void update() {
        if(lendEntity != null){
            lendToEntity();
            return;
        }
        if(isHideCosmetic()) {
            return;
        }
        if(!overlaps) {
            ItemStack itemStack = player.getInventory().getItemInOffHand();
            if(currentItemSaved != null) {
                player.getInventory().setItemInOffHand(currentItemSaved);
                return;
            }
            if(itemStack.getType().isAir() || isCosmetic(itemStack)) {
                //Equip Helmet Without combined.
                player.getInventory().setItemInOffHand(getItemPlaceholders(player));
                return;
            }
            currentItemSaved = itemStack;
            return;
        }
        //Equip offhand combined with offhand item saved in cache
        if(currentItemSaved != null) {
            combinedItem = combinedItems(currentItemSaved);
            player.getInventory().setItemInOffHand(combinedItem);
            return;
        }
        ItemStack itemStack = player.getInventory().getItemInOffHand();
        if(itemStack.getType().isAir() || isCosmetic(itemStack)) {
            //Equip Helmet Without combined.
            player.getInventory().setItemInOffHand(getItemPlaceholders(player));
            return;
        }
        //Equip helmet combined with hat.
        ItemStack offHand = player.getInventory().getItemInOffHand();
        combinedItem = combinedItems(offHand);
        player.getInventory().setItemInOffHand(combinedItem);
    }

    public ItemStack changeItem(ItemStack originalItem) {
        if(isCosmetic(originalItem)) return null;
        if(!overlaps){
            if((originalItem == null || originalItem.getType().isAir())) {
                if(currentItemSaved == null || currentItemSaved.getType().isAir()){
                    player.getInventory().setItemInOffHand(getItemPlaceholders(player));
                    return null;
                }
            }
            ItemStack offhand;
            if(player.getInventory().getItemInOffHand().isSimilar(currentItemSaved))
                offhand = player.getInventory().getItemInOffHand().clone();
            else
                offhand = currentItemSaved != null ? currentItemSaved.clone() : null;
            currentItemSaved = originalItem;
            player.getInventory().setItemInOffHand(currentItemSaved);
            return offhand;
        }
        ItemStack offhand = currentItemSaved != null ? MagicCosmetics.getInstance().getVersion().getItemSavedWithNBTsUpdated(combinedItem, currentItemSaved.clone()) : null;
        if(originalItem.equals(offhand)) return null;
        combinedItem = combinedItems(originalItem);
        player.getInventory().setItemInOffHand(combinedItem);
        return offhand;
    }

    public void leftItem() {
        if(currentItemSaved == null) return;
        if(!overlaps){
            if(player.getInventory().getItemInOffHand().getType().isAir()) return;
            if(isCosmetic(player.getInventory().getItemInOffHand())) return;
            if(player.getInventory().getItemInOffHand().equals(currentItemSaved))
                player.setItemOnCursor(currentItemSaved.clone());
            else
                player.setItemOnCursor(player.getInventory().getItemInOffHand().clone());
            currentItemSaved = null;
            player.getInventory().setItemInOffHand(getItemPlaceholders(player));
            return;
        }
        ItemStack itemSavedUpdated = MagicCosmetics.getInstance().getVersion().getItemSavedWithNBTsUpdated(combinedItem, currentItemSaved.clone());
        player.setItemOnCursor(itemSavedUpdated);
        currentItemSaved = null;
        player.getInventory().setItemInOffHand(getItemPlaceholders(player));
    }

    @Override
    public ItemStack leftItemAndGet() {
        if(currentItemSaved == null) return null;
        if(!overlaps) {
            if(player.getInventory().getItemInOffHand().getType().isAir()) return null;
            if(isCosmetic(player.getInventory().getItemInOffHand())) return null;
            ItemStack getItem;
            if(player.getInventory().getItemInOffHand().equals(currentItemSaved))
                getItem = currentItemSaved.clone();
            else
                getItem = player.getInventory().getItemInOffHand().clone();
            currentItemSaved = null;
            player.getInventory().setItemInOffHand(getItemPlaceholders(player));
            return getItem;
        }
        ItemStack getItem = MagicCosmetics.getInstance().getVersion().getItemSavedWithNBTsUpdated(combinedItem, currentItemSaved.clone());;
        currentItemSaved = null;
        player.getInventory().setItemInOffHand(getItemPlaceholders(player));
        return getItem;
    }

    @Override
    public void dropItem(boolean all) {
        if(currentItemSaved == null) return;
        if(!overlaps) {
            if(player.getInventory().getItemInOffHand().getType().isAir()) return;
            if(isCosmetic(player.getInventory().getItemInOffHand())) return;
            int amount = currentItemSaved.getAmount();
            if (!all) {
                currentItemSaved.setAmount(amount - 1);
            }else {
                currentItemSaved = null;
            }
            return;
        }
        ItemStack getItem = currentItemSaved.clone();
        int amount = getItem.getAmount();
        if (!all) {
            getItem.setAmount(1);
            currentItemSaved.setAmount(amount - 1);
        }else {
            getItem.setAmount(amount);
            currentItemSaved = null;
        }
        Location location = player.getEyeLocation();
        location.setY(location.getY() - 0.30000001192092896);
        Item itemEntity = player.getWorld().dropItem(location, getItem);
        itemEntity.setThrower(player.getUniqueId());
        itemEntity.setVelocity(Utils.getItemDropVelocity(player));
        itemEntity.setPickupDelay(40);
    }

    private ItemStack combinedItems(ItemStack originalItem) {
        this.currentItemSaved = originalItem;
        ItemStack cosmeticItem = getItemPlaceholders(player);
        if(currentItemSaved == null) return cosmeticItem;
        ItemMeta cosmeticMeta = cosmeticItem.getItemMeta();
        ItemMeta itemSaveMeta = currentItemSaved.hasItemMeta() ? currentItemSaved.getItemMeta() : Bukkit.getItemFactory().getItemMeta(currentItemSaved.getType());
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
        if(!lendEntity.getEquipment().getItemInOffHand().getType().isAir() && lendEntity.getEquipment().getItemInOffHand().isSimilar(getItemColor(player))) return;
        lendEntity.getEquipment().setItemInOffHand(getItemColor(player));
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
        update();
    }

    @Override
    public void remove() {
        if(!overlaps) {
            if(currentItemSaved == null)
                player.getInventory().setItemInOffHand(null);
            return;
        }
        if(currentItemSaved != null){
            //Clear offhand With offhand item save in cache
            player.getInventory().setItemInOffHand(currentItemSaved.clone());
            currentItemSaved = null;
            return;
        }
        player.getInventory().setItemInOffHand(null);
    }

    @Override
    public void forceRemove() {
        currentItemSaved = null;
    }

    @Override
    public void clearClose() {
        if(!overlaps) {
            if(currentItemSaved == null)
                player.getInventory().setItemInOffHand(null);
            return;
        }
        if(currentItemSaved != null){
            //Clear offhand With offhand item save in cache
            player.getInventory().setItemInOffHand(currentItemSaved.clone());
            currentItemSaved = null;
            return;
        }
        player.getInventory().setItemInOffHand(null);
    }

    public boolean isOverlaps() {
        return overlaps;
    }

    @Override
    public ItemStack getCurrentItemSaved() {
        return currentItemSaved;
    }

    public void setCurrentItemSaved(ItemStack currentItemSaved) {
        this.currentItemSaved = currentItemSaved;
    }

    public boolean isHasDropped() {
        return hasDropped;
    }

    public void setHasDropped(boolean hasDropped) {
        this.hasDropped = hasDropped;
    }

    @Override
    public ItemStack getEquipment() {
        return player.getInventory().getItemInOffHand();
    }

    @Override
    public void spawn(Player player) {
        // Nothing to do
    }

    @Override
    public void despawn(Player player) {
        // Nothing to do
    }
}
