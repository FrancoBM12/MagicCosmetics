package com.francobm.magicosmetics.cache;

import com.francobm.magicosmetics.files.FileCreator;
import com.francobm.magicosmetics.utils.XMaterial;
import com.francobm.magicosmetics.MagicCosmetics;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Token {
    public static Map<String, Token> tokens = new HashMap<>();
    private final String id;
    private final ItemStack itemStack;
    private final String cosmetic;

    public Token(boolean log, String id, ItemStack itemStack, String cosmetic){
        this.id = id;
        this.itemStack = itemStack;
        this.cosmetic = cosmetic;
        if(log) {
            MagicCosmetics.getInstance().getLogger().info("Token named: '" + id + "' registered.");
        }
    }

    public static Token getToken(String id){
        return tokens.get(id);
    }

    public static Token getTokenByCosmetic(String cosmeticId){
        for(Token token : tokens.values()){
            if(token.getCosmetic().equalsIgnoreCase(cosmeticId)) {
                return token;
            }
        }
        return null;
    }

    public static Token getTokenByItem(ItemStack itemStack){
        for(Token token : tokens.values()) {
            if(!token.isToken(itemStack)) continue;
            return token;
        }
        return null;
    }

    public static void loadTokens(){
        tokens.clear();
        FileCreator token = MagicCosmetics.getInstance().getTokens();
        for(String key : token.getConfigurationSection("tokens").getKeys(false)){
            String display = "";
            int amount = 1;
            String material = "";
            ItemStack itemStack = null;
            List<String> lore = new ArrayList<>();
            boolean unbreakable = false;
            boolean glow = false;
            boolean hide_attributes = false;
            int modelData = 0;
            String cosmetic = "";
            if(token.contains("tokens." + key + ".item.display")){
                display = token.getString("tokens." + key + ".item.display");
            }
            if(token.contains("tokens." + key + ".item.amount")){
                amount = token.getInt("tokens." + key + ".item.amount");
            }
            if(token.contains("tokens." + key + ".item.material")){
                material = token.getString("tokens." + key + ".item.material");
                try{
                    itemStack = XMaterial.valueOf(material.toUpperCase()).parseItem();
                }catch (IllegalArgumentException exception){
                    MagicCosmetics.getInstance().getLogger().info("Item '" + key + "' material: " + material + " Not Found.");
                }
            }
            if(token.contains("tokens." + key + ".item.lore")){
                lore = token.getStringList("tokens." + key + ".item.lore");
            }
            if(token.contains("tokens." + key + ".item.unbreakable")){
                unbreakable = token.getBoolean("tokens." + key + ".item.unbreakable");
            }
            if(token.contains("tokens." + key + ".item.glow")){
                glow = token.getBoolean("tokens." + key + ".item.glow");
            }
            if(token.contains("tokens." + key + ".item.hide-attributes")){
                hide_attributes = token.getBoolean("tokens." + key + ".item.hide-attributes");
            }
            if(token.contains("tokens." + key + ".item.modeldata")){
                modelData = token.getInt("tokens." + key + ".item.modeldata");
            }
            if(token.contains("tokens." + key + ".item.item-adder")){
                if(!MagicCosmetics.getInstance().isItemsAdder()){
                    MagicCosmetics.getInstance().getLogger().info("Item Adder plugin Not Found skipping Token Item '" + key + "'");
                    continue;
                }
                String id = token.getString("tokens." + key + ".item.item-adder");
                ItemStack ia = MagicCosmetics.getInstance().getItemsAdder().getCustomItemStack(id);
                if(ia == null){
                    MagicCosmetics.getInstance().getLogger().info("Item Adder '" + id + "' Not Found skipping...");
                    continue;
                }
                itemStack = ia.clone();
                modelData = -1;
            }
            if(token.contains("tokens." + key + ".item.oraxen")){
                if(!MagicCosmetics.getInstance().isOraxen()){
                    MagicCosmetics.getInstance().getLogger().info("Oraxen plugin Not Found skipping Token Item '" + key + "'");
                    continue;
                }
                String id = token.getString("tokens." + key + ".item.oraxen");
                ItemStack oraxen = MagicCosmetics.getInstance().getOraxen().getItemStackById(id);
                if(oraxen == null){
                    MagicCosmetics.getInstance().getLogger().info("Oraxen '" + id + "' Not Found skipping...");
                    continue;
                }
                itemStack = oraxen.clone();
                modelData = -1;
            }
            if(token.contains("tokens." + key + ".cosmetic")){
                cosmetic = token.getString("tokens." + key + ".cosmetic");
            }
            if(itemStack == null) return;
            itemStack.setAmount(amount);
            ItemMeta itemMeta = itemStack.getItemMeta();
            if(itemMeta == null) return;
            itemMeta.setDisplayName(display);
            itemMeta.setLore(lore);
            if(glow){
                itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            if(hide_attributes){
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            }
            itemMeta.setUnbreakable(unbreakable);
            if(modelData != -1) {
                itemMeta.setCustomModelData(modelData);
            }
            itemStack.setItemMeta(itemMeta);
            tokens.put(key, new Token(true, key, itemStack, cosmetic));
        }
    }

    public String getId() {
        return id;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public boolean isToken(ItemStack itemStack){
        if(itemStack == null) return false;
        if(this.itemStack == null) return false;
        ItemStack token = this.itemStack;
        if(MagicCosmetics.getInstance().isItemsAdder()) {
            if (MagicCosmetics.getInstance().getItemsAdder().getCustomStack(itemStack) != null) {
                if(token.hasItemMeta() && itemStack.hasItemMeta()) {
                    if (token.getItemMeta().hasDisplayName() && itemStack.getItemMeta().hasDisplayName()) {
                        return token.getItemMeta().getDisplayName().equals(itemStack.getItemMeta().getDisplayName());
                    }
                }
                return true;
            }
        }
        return itemStack.isSimilar(token);
    }

    public String getCosmetic() {
        return cosmetic;
    }
}
