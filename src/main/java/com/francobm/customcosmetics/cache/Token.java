package com.francobm.customcosmetics.cache;

import com.francobm.customcosmetics.CustomCosmetics;
import com.francobm.customcosmetics.cache.items.Items;
import com.francobm.customcosmetics.files.FileCreator;
import com.francobm.customcosmetics.utils.XMaterial;
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
            CustomCosmetics.getInstance().getLogger().info("Token named: '" + id + "' registered.");
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
        for(Token token : tokens.values()){
            if(!token.getItemStack().isSimilar(itemStack)) continue;
            return token;
        }
        return null;
    }

    public static void loadTokens(){
        tokens.clear();
        FileCreator token = CustomCosmetics.getInstance().getTokens();
        for(String key : token.getConfigurationSection("tokens").getKeys(false)){
            String display = "";
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
            if(token.contains("tokens." + key + ".item.material")){
                material = token.getString("tokens." + key + ".item.material");
                try{
                    itemStack = XMaterial.valueOf(material.toUpperCase()).parseItem();
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
                    if(token.contains("tokens." + key + ".cosmetic")){
                        cosmetic = token.getString("tokens." + key + ".cosmetic");
                    }
                    ItemMeta itemMeta = itemStack.getItemMeta();
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
                    itemMeta.setCustomModelData(modelData);
                    itemStack.setItemMeta(itemMeta);
                }catch (IllegalArgumentException exception){
                    CustomCosmetics.getInstance().getLogger().info("Item '" + key + "' material: " + material + " Not Found.");
                }
            }
            if(itemStack == null) return;
            tokens.put(key, new Token(true, key, itemStack, cosmetic));
        }
    }

    public String getId() {
        return id;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getCosmetic() {
        return cosmetic;
    }
}
