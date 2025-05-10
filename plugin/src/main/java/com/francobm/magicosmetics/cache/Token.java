package com.francobm.magicosmetics.cache;

import com.francobm.magicosmetics.api.Cosmetic;
import com.francobm.magicosmetics.api.TokenType;
import com.francobm.magicosmetics.files.FileCreator;
import com.francobm.magicosmetics.utils.Utils;
import com.francobm.magicosmetics.utils.XMaterial;
import com.francobm.magicosmetics.MagicCosmetics;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
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
    private String tokenNBT;
    private final ItemStack itemStack;
    private final String cosmetic;
    private final TokenType tokenType;
    private final boolean exchangeable;

    public Token(String id, ItemStack itemStack, String cosmetic, TokenType tokenType, boolean exchangeable) {
        this.id = id;
        this.itemStack = itemStack;
        this.cosmetic = cosmetic;
        this.exchangeable = exchangeable;
        this.tokenType = tokenType;
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
            if(!token.isNewToken(itemStack)) continue;
            return token;
        }
        return null;
    }

    public static Token getOldTokenByItem(ItemStack itemStack) {
        for(Token token : tokens.values()) {
            if(!token.isOldToken(itemStack)) continue;
            return token;
        }
        return null;
    }

    public static boolean removeToken(Player player, ItemStack itemStack){
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        Token token = Token.getTokenByItem(itemStack);
        if(token == null) return false;
        PlayerData playerData = PlayerData.getPlayer(player);
        if(itemStack.getAmount() < token.getItemStack().getAmount()){
            Utils.sendMessage(player, plugin.prefix + plugin.getMessages().getString("insufficient-tokens"));
            return false;
        }
        if(plugin.isPermissions()){
            Cosmetic cosmetic =  Cosmetic.getCosmetic(token.getCosmetic());
            if(!cosmetic.hasPermission(player)) return true;
            Utils.sendMessage(player, plugin.prefix + plugin.getMessages().getString("already-token"));
            return false;
        }
        if(playerData.getCosmeticById(token.getCosmetic()) != null){
            Utils.sendMessage(player, plugin.prefix + plugin.getMessages().getString("already-token"));
            return false;
        }
        return true;
    }

    public static void loadTokens(){
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        tokens.clear();
        FileCreator token = plugin.getTokens();
        int tokens_count = 0;
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
            TokenType type = null;
            boolean exchangeable = true;
            if(token.contains("tokens." + key + ".item.display")){
                display = token.getString("tokens." + key + ".item.display");
                if(plugin.isResourcePlugin())
                    display = plugin.getResourcePlugin().replaceFontImages(display);
            }
            if(token.contains("tokens." + key + ".item.amount")){
                amount = token.getInt("tokens." + key + ".item.amount");
            }
            if(token.contains("tokens." + key + ".item.material")){
                material = token.getString("tokens." + key + ".item.material");
                try{
                    itemStack = XMaterial.valueOf(material.toUpperCase()).parseItem();
                }catch (IllegalArgumentException exception){
                    plugin.getLogger().warning("Item '" + key + "' material: " + material + " Not Found.");
                }
            }
            if(token.contains("tokens." + key + ".item.lore")){
                lore = token.getStringList("tokens." + key + ".item.lore");
                if(plugin.isResourcePlugin()){
                    List<String> lore2 = new ArrayList<>();
                    for(String l : lore) {
                        lore2.add(plugin.getResourcePlugin().replaceFontImages(l));
                    }
                    lore.clear();
                    lore.addAll(lore2);
                }
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
            if(plugin.isResourcePlugin()) {
                String pathName = plugin.getResourcePlugin().getPathName();
                if(token.contains("tokens." + key + ".item." + pathName)) {
                    String id = token.getString("tokens." + key + ".item." + pathName);
                    ItemStack resourceItem = plugin.getResourcePlugin().getItemStackById(id);
                    if(resourceItem == null){
                        plugin.getLogger().warning("Resource (" + plugin.getResourcePlugin().getProviderName() + ") Item: '" + id + "' Not Found skipping...");
                        continue;
                    }
                    itemStack = resourceItem.clone();
                    modelData = -1;
                }else {
                    plugin.getLogger().warning("Resource (" + plugin.getResourcePlugin().getProviderName() + ") plugin Not Found, skipping Token Item '" + key + "'");
                    continue;
                }
            }
            if(token.contains("tokens." + key + ".cosmetic")){
                cosmetic = token.getString("tokens." + key + ".cosmetic");
            }
            if(token.contains("tokens." + key + ".type")) {
                String tokenType = token.getString("tokens." + key + ".type");
                try{
                    type = TokenType.valueOf(tokenType.toUpperCase());
                }catch (IllegalArgumentException exception){
                    plugin.getLogger().warning("The token type you entered does not exist!");
                }
            }
            if(token.contains("tokens." + key + ".exchangeable")){
                exchangeable = token.getBoolean("tokens." + key + ".exchangeable");
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
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE, ItemFlag.HIDE_UNBREAKABLE);
            }
            itemMeta.setUnbreakable(unbreakable);
            if(modelData != -1) {
                itemMeta.setCustomModelData(modelData);
            }
            if(Utils.isNewerThan1206()) {
                itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("foo",0,AttributeModifier.Operation.MULTIPLY_SCALAR_1)); // This is necessary as of 1.20.6
            }
            itemStack.setItemMeta(itemMeta);
            itemStack = plugin.getVersion().setNBTCosmetic(itemStack, "key:"+key);
            Token tk = new Token(key, itemStack, cosmetic, type, exchangeable);
            tk.tokenNBT = "key:" + key;
            tokens.put(key, tk);
            tokens_count++;
        }
        plugin.getLogger().info("Registered tokens: " + tokens_count);
    }

    public String getId() {
        return id;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public boolean isNewToken(ItemStack itemStack){
        if(itemStack == null) return false;
        /*if(MagicCosmetics.getInstance().isItemsAdder()) {
            if (MagicCosmetics.getInstance().getItemsAdder().getCustomStack(itemStack) != null) {
                if(token.hasItemMeta() && itemStack.hasItemMeta()) {
                    if (token.getItemMeta().hasDisplayName() && itemStack.getItemMeta().hasDisplayName()) {
                        return token.getItemMeta().getDisplayName().equals(itemStack.getItemMeta().getDisplayName());
                    }
                }
                return true;
            }
        }*/
        String key = MagicCosmetics.getInstance().getVersion().isNBTCosmetic(itemStack);
        return key != null && !key.isEmpty() && key.equalsIgnoreCase(tokenNBT);
    }

    public boolean isOldToken(ItemStack itemStack){
        if(itemStack == null) return false;
        /*if(MagicCosmetics.getInstance().isItemsAdder()) {
            if (MagicCosmetics.getInstance().getItemsAdder().getCustomStack(itemStack) != null) {
                if(token.hasItemMeta() && itemStack.hasItemMeta()) {
                    if (token.getItemMeta().hasDisplayName() && itemStack.getItemMeta().hasDisplayName()) {
                        return token.getItemMeta().getDisplayName().equals(itemStack.getItemMeta().getDisplayName());
                    }
                }
                return true;
            }
        }*/
        String key = MagicCosmetics.getInstance().getVersion().isNBTCosmetic(itemStack);
        return key != null && !key.isEmpty() && key.equalsIgnoreCase(id);
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public String getCosmetic() {
        return cosmetic;
    }

    public boolean isExchangeable() {
        return exchangeable;
    }
}
