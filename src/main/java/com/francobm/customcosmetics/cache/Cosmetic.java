package com.francobm.customcosmetics.cache;

import com.francobm.customcosmetics.CustomCosmetics;
import com.francobm.customcosmetics.cache.balloons.Balloon;
import com.francobm.customcosmetics.files.FileCreator;
import com.francobm.customcosmetics.utils.XMaterial;
import org.bukkit.Color;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.*;

public abstract class Cosmetic {
    public static Map<String, Cosmetic> cosmetics = new LinkedHashMap<>();
    private final String id;
    private final String name;
    private final ItemStack itemStack;
    private final int modelData;
    private final CosmeticType cosmeticType;
    private final boolean colored;
    private Color color;

    public Cosmetic(String id, String name, ItemStack itemStack, int modelData, boolean colored, CosmeticType cosmeticType, Color color) {
        this.id = id;
        this.name = name;
        this.itemStack = itemStack;
        this.modelData = modelData;
        this.colored = colored;
        this.cosmeticType = cosmeticType;
        this.color = color;
    }

    public static List<Cosmetic> getCosmeticsByType(CosmeticType cosmeticType){
        List<Cosmetic> cosmetics2 = new ArrayList<>();
        for(Cosmetic cosmetic : cosmetics.values()){
            if(cosmetic.getCosmeticType() != cosmeticType) continue;
            cosmetics2.add(cosmetic);
        }
        return cosmetics2;
    }

    public static int getCosmeticCount(CosmeticType cosmeticType){
        int i = 0;
        for(Cosmetic cosmetic : cosmetics.values()){
            if(cosmetic.getCosmeticType() != cosmeticType) continue;
            i++;
        }
        return i;
    }

    public static Cosmetic getCosmetic(String id){
        return cosmetics.get(id);
    }

    public static Cosmetic getCloneCosmetic(String id){
        Cosmetic cosmetic = getCosmetic(id);
        if(cosmetic == null) return null;
        Cosmetic cosmec = null;
        switch (cosmetic.getCosmeticType()){
            case HAT:
                Hat hat = (Hat) cosmetic;
                cosmec = new Hat(hat.getId(), hat.getName(), hat.getItemStack(), hat.getModelData(), hat.isColored(), hat.getCosmeticType(), hat.getColor());
                break;
            case BAG:
                Bag bag = (Bag) cosmetic;
                cosmec = new Bag(bag.getId(), bag.getName(), bag.getItemStack(), bag.getModelData(), bag.getModelDataForMe(), bag.isColored(), bag.getSpace(), bag.getCosmeticType(), bag.getColor());
                break;
            case WALKING_STICK:
                WStick wStick = (WStick) cosmetic;
                cosmec = new WStick(wStick.getId(), wStick.getName(), wStick.getItemStack(), wStick.getModelData(), wStick.isColored(), wStick.getCosmeticType(), wStick.getColor());
                break;
            case BALLOON:
                Balloon balloon = (Balloon) cosmetic;
                cosmec = new Balloon(balloon.getId(), balloon.getName(), balloon.getItemStack(), balloon.getModelData(), balloon.isColored(), balloon.getSpace(), balloon.getCosmeticType(), balloon.getColor(), balloon.isRotation(), balloon.getRotationType(), balloon.getBalloonEngine() == null ? "" : balloon.getBalloonEngine().getModelId());
                break;
        }
        return cosmec;
    }

    public static void loadCosmetics(){
        cosmetics.clear();
        FileCreator cosmeticsConf = CustomCosmetics.getInstance().getCosmetics();
        for(String key : cosmeticsConf.getConfigurationSection("cosmetics").getKeys(false)){
            String name = "";
            ItemStack itemStack = null;
            CosmeticType cosmeticType = null;
            boolean colored = false;
            String type = "";
            double space = 0;
            String model = "";
            boolean rotation = false;
            RotationType rotationType = null;
            int modelData = 0;
            int modelDataForMe = 0;
            if(cosmeticsConf.contains("cosmetics." + key + ".item")){
                List<String> lore = null;
                boolean unbreakable = false;
                boolean glow = false;
                boolean hide_attributes = false;
                if(cosmeticsConf.contains("cosmetics." + key + ".item.display")){
                    name = cosmeticsConf.getString("cosmetics." + key + ".item.display");
                }
                if(cosmeticsConf.contains("cosmetics." + key + ".item.material")){
                    String item = cosmeticsConf.getString("cosmetics." + key + ".item.material");
                    try{
                        itemStack = XMaterial.valueOf(item.toUpperCase()).parseItem();
                    }catch (IllegalArgumentException exception){
                        CustomCosmetics.getInstance().getLogger().info("Item Material '" + item + "' in Cosmetic '" + key + "' Not Found!");
                    }
                }
                if(cosmeticsConf.contains("cosmetics." + key + ".item.lore")){
                    lore = cosmeticsConf.getStringList("cosmetics." + key + ".item.lore");
                }
                if(cosmeticsConf.contains("cosmetics." + key + ".item.glow")){
                    glow = cosmeticsConf.getBoolean("cosmetics." + key + ".item.glow");
                }
                if(cosmeticsConf.contains("cosmetics." + key + ".item.hide-attributes")){
                    hide_attributes = cosmeticsConf.getBoolean("cosmetics." + key + ".item.hide-attributes");
                }
                if(cosmeticsConf.contains("cosmetics." + key + ".item.unbreakable")){
                    unbreakable = cosmeticsConf.getBoolean("cosmetics." + key + ".item.unbreakable");
                }
                if(cosmeticsConf.contains("cosmetics." + key + ".item.modeldata")){
                    modelData = cosmeticsConf.getInt("cosmetics." + key + ".item.modeldata");
                }
                if(cosmeticsConf.contains("cosmetics." + key + ".item.modeldata-for-me")){
                    modelDataForMe = cosmeticsConf.getInt("cosmetics." + key + ".item.modeldata-for-me");
                }
                if(cosmeticsConf.contains("cosmetics." + key + ".item.item-adder")){
                    if(!CustomCosmetics.getInstance().isItemsAdder()){
                        CustomCosmetics.getInstance().getLogger().info("Item Adder plugin Not Found skipping cosmetic '" + key + "'");
                        continue;
                    }
                    String id = cosmeticsConf.getString("cosmetics." + key + ".item.item-adder");
                    if(CustomCosmetics.getInstance().getItemsAdder().getCustomStack(id) == null){
                        CustomCosmetics.getInstance().getLogger().info("Item Adder '" + id + "' Not Found skipping...");
                        continue;
                    }
                    itemStack = CustomCosmetics.getInstance().getItemsAdder().getCustomItemStack(id).clone();
                    modelData = -1;
                }
                if(itemStack == null){
                    return;
                }
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(name);
                itemMeta.setLore(lore);
                if(glow){
                    itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                    itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }
                if(hide_attributes) {
                    itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                }
                itemMeta.setUnbreakable(unbreakable);
                if(modelData != -1) {
                    itemMeta.setCustomModelData(modelData);
                }
                itemStack.setItemMeta(itemMeta);
                //CustomCosmetics.getInstance().getLogger().severe(itemStack.toString());
            }
            if(cosmeticsConf.contains("cosmetics." + key + ".type")){
                type = cosmeticsConf.getString("cosmetics." + key + ".type");
                try{
                    cosmeticType = CosmeticType.valueOf(type.toUpperCase());
                }catch (IllegalArgumentException exception){
                    CustomCosmetics.getInstance().getLogger().info("Cosmetic Type: " + type + " Not Found.");
                    return;
                }
            }
            if(cosmeticsConf.contains("cosmetics." + key + ".colored")){
                colored = cosmeticsConf.getBoolean("cosmetics." + key + ".colored");
            }
            if(cosmeticsConf.contains("cosmetics." + key + ".rotation")){
                rotation = cosmeticsConf.getBoolean("cosmetics." + key + ".rotation.enabled");
                String rotType = cosmeticsConf.getString("cosmetics." + key + ".rotation.type");
                try{
                    rotationType = RotationType.valueOf(rotType.toUpperCase());
                }catch (IllegalArgumentException exception){
                    CustomCosmetics.getInstance().getLogger().info("Cosmetic Type: " + type + " Rotation Type Not Found.");
                }
            }
            if(cosmeticsConf.contains("cosmetics." + key + ".space")){
                space = cosmeticsConf.getDouble("cosmetics." + key + ".space");
            }
            if(cosmeticsConf.contains("cosmetics." + key + ".model")){
                model = cosmeticsConf.getString("cosmetics." + key + ".model");
            }
            if(cosmeticType == null){
                return;
            }
            switch (cosmeticType){
                case HAT:
                    cosmetics.put(key, new Hat(key, name, itemStack, modelData, colored, cosmeticType, null));
                    break;
                case BAG:
                    cosmetics.put(key, new Bag(key, name, itemStack, modelData, modelDataForMe, colored, space, cosmeticType, null));
                    break;
                case WALKING_STICK:
                    cosmetics.put(key, new WStick(key, name, itemStack, modelData, colored, cosmeticType, null));
                    break;
                case BALLOON:
                    cosmetics.put(key, new Balloon(key, name, itemStack, modelData, colored, space, cosmeticType, null, rotation, rotationType, model));
                    break;
            }
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getModelData() {
        return modelData;
    }

    public CosmeticType getCosmeticType() {
        return cosmeticType;
    }

    public boolean isColored() {
        return colored;
    }

    public abstract void active(Player player);

    public abstract void clear(Player player);

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isCosmetic(ItemStack itemStack){
        if(this.itemStack == null) return false;
        if(CustomCosmetics.getInstance().isItemsAdder()) {
            if (CustomCosmetics.getInstance().getItemsAdder().getCustomStack(itemStack) != null) {
                return true;
            }
        }
        return itemStack.isSimilar(getItemColor());
    }

    public ItemStack getItemColor(){
        if(itemStack == null) return null;
        ItemStack itemStack = this.itemStack.clone();
        if(itemStack.getType() == XMaterial.LEATHER_HORSE_ARMOR.parseMaterial()){
            LeatherArmorMeta itemMeta = (LeatherArmorMeta) itemStack.getItemMeta();
            if(color != null) {
                itemMeta.setColor(color);
            }
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }
        return itemStack;
    }

}
