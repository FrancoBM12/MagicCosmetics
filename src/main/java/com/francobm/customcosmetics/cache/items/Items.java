package com.francobm.customcosmetics.cache.items;

import com.francobm.customcosmetics.CustomCosmetics;
import com.francobm.customcosmetics.cache.Color;
import com.francobm.customcosmetics.cache.Cosmetic;
import com.francobm.customcosmetics.cache.CosmeticType;
import com.francobm.customcosmetics.cache.PlayerCache;
import com.francobm.customcosmetics.files.FileCreator;
import com.francobm.customcosmetics.utils.XMaterial;
import org.bukkit.DyeColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.*;

public class Items {
    public static Map<String, Items> items = new HashMap<>();
    private final String id;
    private final List<String> loreAvailable;
    private final List<String> loreUnavailable;
    private final ItemStack itemStack;

    public Items(boolean log, String id, ItemStack itemStack, List<String> loreAvailable, List<String> loreUnavailable){
        this.id = id;
        this.itemStack = itemStack;
        this.loreAvailable = loreAvailable;
        this.loreUnavailable = loreUnavailable;
        if(log) {
            CustomCosmetics.getInstance().getLogger().info("Item named: '" + id + "' registered.");
        }
    }

    public Items(ItemStack itemStack){
        this.id = new Random().toString();
        this.itemStack = itemStack;
        this.loreAvailable = new ArrayList<>();
        this.loreUnavailable = new ArrayList<>();
    }

    public Items(String id, ItemStack itemStack){
        this.id = id;
        this.itemStack = itemStack;
        this.loreAvailable = new ArrayList<>();
        this.loreUnavailable = new ArrayList<>();
    }

    public static Items getItem(String id){
        return items.get(id);
    }

    public static void loadItems(){
        items.clear();
        FileCreator menu = CustomCosmetics.getInstance().getMenus();
        for(String key : menu.getConfigurationSection("items").getKeys(false)){
            String display = "";
            String material = "";
            ItemStack itemStack = null;
            List<String> loreAvailable = null;
            List<String> loreUnavailable = null;
            int amount = 0;
            boolean glow = false;
            int modelData = 0;
            if(menu.contains("items." + key + ".item.display")){
                display = menu.getString("items." + key + ".item.display");
            }
            if(menu.contains("items." + key + ".item.material")){
                material = menu.getString("items." + key + ".item.material");
                try{
                    itemStack = XMaterial.valueOf(material.toUpperCase()).parseItem();
                }catch (IllegalArgumentException exception){
                    CustomCosmetics.getInstance().getLogger().info("Item '" + key + "' material: " + material + " Not Found.");
                }
            }
            List<String> lore = null;
            if(menu.contains("items." + key + ".item.lore")){
                lore = new ArrayList<>();
                for(String l : menu.getStringList("items." + key + ".item.lore")){
                    lore.add(l
                            .replace("%hats_count%", String.valueOf(Cosmetic.getCosmeticCount(CosmeticType.HAT)))
                            .replace("%bags_count%", String.valueOf(Cosmetic.getCosmeticCount(CosmeticType.BAG)))
                            .replace("%wsticks_count%", String.valueOf(Cosmetic.getCosmeticCount(CosmeticType.WALKING_STICK)))
                            .replace("%balloons_count%", String.valueOf(Cosmetic.getCosmeticCount(CosmeticType.BALLOON))));
                }
            }
            if(menu.contains("items." + key + ".item.lore-available")){
                loreAvailable = menu.getStringList("items." + key + ".item.lore-available");
            }
            if(menu.contains("items." + key + ".item.lore-unavailable")){
                loreUnavailable = menu.getStringList("items." + key + ".item.lore-unavailable");
            }
            if(menu.contains("items." + key + ".item.amount")){
                amount = menu.getInt("items." + key + ".item.amount");
            }
            if(menu.contains("items." + key + ".item.glow")){
                glow = menu.getBoolean("items." + key + ".item.glow");
            }
            if(menu.contains("items." + key + ".item.modeldata")){
                modelData = menu.getInt("items." + key + ".item.modeldata");
            }
            if(menu.contains("items." + key + ".item.item-adder")){
                if(!CustomCosmetics.getInstance().isItemsAdder()){
                    CustomCosmetics.getInstance().getLogger().info("Item Adder plugin Not Found skipping Menu Item '" + key + "'");
                    continue;
                }
                String id = menu.getString("items." + key + ".item.item-adder");
                if(CustomCosmetics.getInstance().getItemsAdder().getCustomStack(id) == null){
                    CustomCosmetics.getInstance().getLogger().info("Item Adder '" + id + "' Not Found skipping...");
                    continue;
                }
                itemStack = CustomCosmetics.getInstance().getItemsAdder().getCustomItemStack(id).clone();
                modelData = -1;
            }
            itemStack.setAmount(amount);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(display);
            itemMeta.setLore(lore);
            if(glow){
                itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            if(modelData != -1) {
                itemMeta.setCustomModelData(modelData);
            }
            itemStack.setItemMeta(itemMeta);
            if(itemStack == null) return;
            items.put(key, new Items(true, key, itemStack, loreAvailable, loreUnavailable));
        }
    }

    public Items addVariable(String variable, String string){
        if(itemStack == null) return this;
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(itemMeta.getDisplayName().replace(variable, string));
        List<String> lore = new ArrayList<>();
        for(String l : itemMeta.getLore()){
            lore.add(l.replace(variable, string));
        }
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public Items addVariable(String variable, CosmeticType cosmeticType){
        if(itemStack == null) return this;
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>();
        FileCreator messages = CustomCosmetics.getInstance().getMessages();
        switch (cosmeticType){
            case HAT:
                itemMeta.setDisplayName(itemMeta.getDisplayName().replace(variable, messages.getString("types.hat")));
                for(String l : itemMeta.getLore()){
                    lore.add(l.replace(variable, messages.getString("types.hat")));
                }
                break;
            case BAG:
                itemMeta.setDisplayName(itemMeta.getDisplayName().replace(variable, messages.getString("types.bag")));
                for(String l : itemMeta.getLore()){
                    lore.add(l.replace(variable, messages.getString("types.bag")));
                }
                break;
            case WALKING_STICK:
                itemMeta.setDisplayName(itemMeta.getDisplayName().replace(variable, messages.getString("types.wstick")));
                for(String l : itemMeta.getLore()){
                    lore.add(l.replace(variable, messages.getString("types.wstick")));
                }
                break;
            case BALLOON:
                itemMeta.setDisplayName(itemMeta.getDisplayName().replace(variable, messages.getString("types.balloon")));
                for(String l : itemMeta.getLore()){
                    lore.add(l.replace(variable, messages.getString("types.balloon")));
                }
                break;
        }
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public Items addVariable(String variable, int number){
        if(itemStack == null) return this;
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(itemMeta.getDisplayName().replace(variable, String.valueOf(number)));
        List<String> lore = new ArrayList<>();
        for(String l : itemMeta.getLore()){
            lore.add(l.replace(variable, String.valueOf(number)));
        }
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemStack addVariableItem(String variable, int number){
        if(itemStack == null) return null;
        ItemStack itemStack = this.itemStack.clone();
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(itemMeta.getDisplayName().replace(variable, String.valueOf(number)));
        List<String> lore = new ArrayList<>();
        for(String l : itemMeta.getLore()){
            lore.add(l.replace(variable, String.valueOf(number)));
        }
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public boolean isHead(){
        if(itemStack == null) return false;
        return itemStack.getType() == XMaterial.PLAYER_HEAD.parseMaterial();
    }

    public ItemStack copyItem(ItemStack head){
        if(this.itemStack == null) return null;
        ItemStack itemStack = head.clone();
        if(!isHead()) return itemStack;
        itemStack.setAmount(this.itemStack.getAmount());
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(this.itemStack.getItemMeta().getDisplayName());
        itemMeta.setLore(this.itemStack.getItemMeta().getLore());
        if(this.itemStack.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS)){
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        itemMeta.addItemFlags(ItemFlag.HIDE_DYE);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack copyItem(PlayerCache playerCache, Cosmetic cosmetic, ItemStack head){
        if(this.itemStack == null) return null;
        ItemStack itemStack = head.clone();
        if(!isHead()) return itemStack;
        itemStack.setAmount(this.itemStack.getAmount());
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(this.itemStack.getItemMeta().getDisplayName());
        if(playerCache.getCosmeticById(cosmetic.getId()) == null) {
            if(itemMeta.hasLore()){
                List<String> lore = itemMeta.getLore();
                lore.addAll(loreUnavailable);
                itemMeta.setLore(lore);
            }else {
                itemMeta.setLore(loreUnavailable);
            }
        }else{
            if(itemMeta.hasLore()){
                List<String> lore = itemMeta.getLore();
                lore.addAll(loreAvailable);
                itemMeta.setLore(lore);
            }else {
                itemMeta.setLore(loreAvailable);
            }
        }
        if(this.itemStack.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS)){
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        if(this.itemStack.getItemMeta().hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)){
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }
        itemMeta.addItemFlags(ItemFlag.HIDE_DYE);
        itemMeta.setUnbreakable(this.itemStack.getItemMeta().isUnbreakable());
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack colorItem(Color color){
        if(this.itemStack == null) return null;
        ItemStack itemStack = this.itemStack.clone();
        itemStack.setAmount(this.itemStack.getAmount());
        if(itemStack.getType() == XMaterial.LEATHER_HORSE_ARMOR.parseMaterial()){
            LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
            meta.setColor(color.getPrimaryColor());
            meta.setDisplayName(this.itemStack.getItemMeta().getDisplayName());
            meta.setLore(this.itemStack.getItemMeta().getLore());
            if(this.itemStack.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS)){
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            meta.addItemFlags(ItemFlag.HIDE_DYE);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }

    public ItemStack colorItem(org.bukkit.Color color){
        if(this.itemStack == null) return null;
        ItemStack itemStack = this.itemStack.clone();
        itemStack.setAmount(this.itemStack.getAmount());
        if(itemStack.getType() == XMaterial.LEATHER_HORSE_ARMOR.parseMaterial()){
            LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
            meta.setColor(color);
            meta.setDisplayName(this.itemStack.getItemMeta().getDisplayName());
            meta.setLore(this.itemStack.getItemMeta().getLore());
            if(this.itemStack.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS)){
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            meta.addItemFlags(ItemFlag.HIDE_DYE);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }

    public String getId() {
        return id;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
