package com.francobm.magicosmetics.cache;

import com.francobm.magicosmetics.cache.balloons.Balloon;
import com.francobm.magicosmetics.cache.balloons.BalloonEngine;
import com.francobm.magicosmetics.files.FileCreator;
import com.francobm.magicosmetics.utils.XMaterial;
import com.francobm.magicosmetics.MagicCosmetics;
import org.bukkit.Color;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

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
        for(String id : cosmetics.keySet()){
            if(id.isEmpty()) continue;
            Cosmetic cosmetic = Cosmetic.getCloneCosmetic(id);
            if(cosmetic == null) continue;
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
                cosmec = new Hat(hat.getId(), hat.getName(), hat.getItemStack(), hat.getModelData(), hat.isColored(), hat.getCosmeticType(), hat.getColor(), hat.isOverlaps());
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
                cosmec = new Balloon(balloon.getId(), balloon.getName(), balloon.getItemStack(), balloon.getModelData(), balloon.isColored(), balloon.getSpace(), balloon.getCosmeticType(), balloon.getColor(), balloon.isRotation(), balloon.getRotationType(), balloon.getBalloonEngine());
                break;
        }
        return cosmec;
    }

    public static void loadCosmetics(){
        cosmetics.clear();
        FileCreator cosmeticsConf = MagicCosmetics.getInstance().getCosmetics();
        for(String key : cosmeticsConf.getConfigurationSection("cosmetics").getKeys(false)){
            String name = "";
            ItemStack itemStack = null;
            CosmeticType cosmeticType = null;
            boolean colored = false;
            Color color = null;
            String type = "";
            double space = 0;
            boolean overlaps = false;
            BalloonEngine balloonEngine = null;
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
                        MagicCosmetics.getInstance().getLogger().info("Item Material '" + item + "' in Cosmetic '" + key + "' Not Found!");
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
                if(cosmeticsConf.contains("cosmetics." + key + ".item.color")){
                    String hex = cosmeticsConf.getStringWF("cosmetics." + key + ".item.color");
                    MagicCosmetics.getInstance().getLogger().info("Hex: " + hex);
                    if(hex != null){
                        color = com.francobm.magicosmetics.cache.Color.hex2Rgb(hex);
                    }
                }
                if(cosmeticsConf.contains("cosmetics." + key + ".item.modeldata-for-me")){
                    modelDataForMe = cosmeticsConf.getInt("cosmetics." + key + ".item.modeldata-for-me");
                }
                if(cosmeticsConf.contains("cosmetics." + key + ".item.item-adder")){
                    if(!MagicCosmetics.getInstance().isItemsAdder()){
                        MagicCosmetics.getInstance().getLogger().info("Item Adder plugin Not Found skipping cosmetic '" + key + "'");
                        continue;
                    }
                    String id = cosmeticsConf.getString("cosmetics." + key + ".item.item-adder");
                    ItemStack ia = MagicCosmetics.getInstance().getItemsAdder().getCustomItemStack(id);
                    if(ia == null){
                        MagicCosmetics.getInstance().getLogger().info("IA Item: '" + id + "' Not Found skipping...");
                        continue;
                    }
                    itemStack = ia.clone();
                    modelData = -1;
                }
                if(cosmeticsConf.contains("cosmetics." + key + ".item.oraxen")){
                    if(!MagicCosmetics.getInstance().isOraxen()){
                        MagicCosmetics.getInstance().getLogger().info("Oraxen plugin Not Found skipping cosmetic '" + key + "'");
                        continue;
                    }
                    String id = cosmeticsConf.getString("cosmetics." + key + ".item.oraxen");
                    ItemStack oraxen = MagicCosmetics.getInstance().getOraxen().getItemStackById(id);
                    if(oraxen == null){
                        MagicCosmetics.getInstance().getLogger().info("Oraxen item:  '" + id + "' Not Found skipping...");
                        continue;
                    }
                    itemStack = oraxen.clone();
                    modelData = -1;
                }
                if(itemStack == null){
                    continue;
                }
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(name);
                itemMeta.setLore(lore);
                if(glow){
                    itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                    itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }
                if(hide_attributes) {
                    itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE, ItemFlag.HIDE_UNBREAKABLE);
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
                    MagicCosmetics.getInstance().getLogger().info("Cosmetic Type: " + type + " Not Found.");
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
                    MagicCosmetics.getInstance().getLogger().info("Cosmetic Type: " + type + " Rotation Type Not Found.");
                }
            }
            if(cosmeticsConf.contains("cosmetics." + key + ".space")){
                space = cosmeticsConf.getDouble("cosmetics." + key + ".space");
            }
            if(cosmeticsConf.contains("cosmetics." + key + ".overlaps")){
                overlaps = cosmeticsConf.getBoolean("cosmetics." + key + ".overlaps");
            }
            if(cosmeticsConf.contains("cosmetics." + key + ".meg.model")){
                if(!MagicCosmetics.getInstance().isModelEngine()){
                    MagicCosmetics.getInstance().getLogger().info("Model Engine plugin Not Found skipping cosmetic '" + key + "'");
                    continue;
                }
                String modelId = cosmeticsConf.getString("cosmetics." + key + ".meg.model");
                List<String> colorableParts = cosmeticsConf.getStringListWF("cosmetics." + key + ".meg.colorable-parts");
                balloonEngine = new BalloonEngine(modelId, colorableParts);
            }
            if(cosmeticType == null){
                return;
            }
            switch (cosmeticType){
                case HAT:
                    cosmetics.put(key, new Hat(key, name, itemStack, modelData, colored, cosmeticType, color, overlaps));
                    break;
                case BAG:
                    cosmetics.put(key, new Bag(key, name, itemStack, modelData, modelDataForMe, colored, space, cosmeticType, color));
                    break;
                case WALKING_STICK:
                    cosmetics.put(key, new WStick(key, name, itemStack, modelData, colored, cosmeticType, color));
                    break;
                case BALLOON:
                    cosmetics.put(key, new Balloon(key, name, itemStack, modelData, colored, space, cosmeticType, color, rotation, rotationType, balloonEngine));
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
        if(!isColored() && getColor() != null){
            return getItemColor();
        }
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
        if(itemStack == null) return false;
        if(this.itemStack == null) return false;
        ItemStack cosmetic = this.itemStack;
        if(MagicCosmetics.getInstance().isItemsAdder()) {
            if (MagicCosmetics.getInstance().getItemsAdder().getCustomStack(itemStack) != null) {
                if(cosmetic.hasItemMeta() && itemStack.hasItemMeta()) {
                    if (cosmetic.getItemMeta().hasDisplayName() && itemStack.getItemMeta().hasDisplayName()) {
                        return cosmetic.getItemMeta().getDisplayName().equals(itemStack.getItemMeta().getDisplayName());
                    }
                }
                return true;
            }
        }
        if(MagicCosmetics.getInstance().isOraxen()){
            if(MagicCosmetics.getInstance().getOraxen().getItemStackByItem(itemStack) != null){
                if(cosmetic.hasItemMeta() && itemStack.hasItemMeta()) {
                    if (cosmetic.getItemMeta().hasDisplayName() && itemStack.getItemMeta().hasDisplayName()) {
                        return cosmetic.getItemMeta().getDisplayName().equals(itemStack.getItemMeta().getDisplayName());
                    }
                }
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

    public ItemStack getItemColor(Player player) {
        ItemStack itemStack = getItemColor();
        if(itemStack.getType() != XMaterial.PLAYER_HEAD.parseMaterial()) return itemStack;
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setOwningPlayer(player);
        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }
}
