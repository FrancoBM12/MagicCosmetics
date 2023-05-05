package com.francobm.magicosmetics.api;

import com.francobm.magicosmetics.cache.*;
import com.francobm.magicosmetics.cache.cosmetics.Bag;
import com.francobm.magicosmetics.cache.cosmetics.Hat;
import com.francobm.magicosmetics.cache.cosmetics.Spray;
import com.francobm.magicosmetics.cache.cosmetics.WStick;
import com.francobm.magicosmetics.cache.cosmetics.balloons.Balloon;
import com.francobm.magicosmetics.cache.cosmetics.balloons.BalloonEngine;
import com.francobm.magicosmetics.cache.cosmetics.balloons.BalloonIA;
import com.francobm.magicosmetics.files.FileCosmetics;
import com.francobm.magicosmetics.files.FileCreator;
import com.francobm.magicosmetics.utils.Utils;
import com.francobm.magicosmetics.utils.XMaterial;
import com.francobm.magicosmetics.MagicCosmetics;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;

import java.awt.image.BufferedImage;
import java.util.*;

public abstract class Cosmetic {
    public static Map<String, Cosmetic> cosmetics = new LinkedHashMap<>();
    private final String id;
    private final String name;
    private final ItemStack itemStack;
    private final int modelData;
    private final CosmeticType cosmeticType;
    private final boolean colored;
    private boolean defaultColor;
    private Color color;
    private final String permission;
    private final boolean texture;
    private final boolean hideMenu;
    private boolean hideCosmetic;
    private final boolean useEmote;
    private boolean colorBlocked;

    public Cosmetic(String id, String name, ItemStack itemStack, int modelData, boolean colored, CosmeticType cosmeticType, Color color, String permission, boolean texture, boolean hideMenu, boolean useEmote) {
        this.id = id;
        this.name = name;
        this.itemStack = itemStack;
        this.modelData = modelData;
        this.colored = colored;
        this.cosmeticType = cosmeticType;
        this.color = color;
        this.permission = permission;
        this.texture = texture;
        this.hideMenu = hideMenu;
        this.hideCosmetic = false;
        this.useEmote = useEmote;
    }

    public Cosmetic(String id, String name, String permission) {
        this.id = id;
        this.name = name;
        this.permission = permission;
        this.itemStack = null;
        this.modelData = 0;
        this.colored = false;
        this.cosmeticType = null;
        this.color = null;
        this.texture = false;
        this.hideMenu = false;
        this.hideCosmetic = false;
        this.useEmote = false;
    }

    public static List<Cosmetic> getCosmeticsUnHideByType(CosmeticType cosmeticType){
        List<Cosmetic> cosmetics2 = new ArrayList<>();
        for(String id : cosmetics.keySet()){
            if(id.isEmpty()) continue;
            Cosmetic cosmetic = Cosmetic.getCloneCosmetic(id);
            if(cosmetic == null) continue;
            if(cosmetic.isHideMenu()) continue;
            if(cosmetic.getCosmeticType() != cosmeticType) continue;
            cosmetics2.add(cosmetic);
        }
        return cosmetics2;
    }

    public static Set<Cosmetic> getSetCosmeticsHideByType(CosmeticType cosmeticType) {
        Set<Cosmetic> cosmetics2 = new HashSet<>();
        for(String id : cosmetics.keySet()){
            if(id.isEmpty()) continue;
            Cosmetic cosmetic = Cosmetic.getCloneCosmetic(id);
            if(cosmetic == null) continue;
            if(cosmetic.isHideMenu()) continue;
            if(cosmetic.getCosmeticType() != cosmeticType) continue;
            cosmetics2.add(cosmetic);
        }
        return cosmetics2;
    }

    public static Set<Cosmetic> getCosmeticsByType(CosmeticType cosmeticType){
        Set<Cosmetic> cosmetics2 = new HashSet<>();
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
                cosmec = new Hat(hat.getId(), hat.getName(), hat.getItemStack().clone(), hat.getModelData(), hat.isColored(), hat.getCosmeticType(), hat.getColor(), hat.isOverlaps(), hat.getPermission(), hat.isTexture(), hat.isHideMenu(), hat.isUseEmote());
                break;
            case BAG:
                Bag bag = (Bag) cosmetic;
                cosmec = new Bag(bag.getId(), bag.getName(), bag.getItemStack().clone(), bag.getModelData(), bag.getBagForMe(), bag.isColored(), bag.getSpace(), bag.getCosmeticType(), bag.getColor(), bag.getDistance(), bag.getPermission(), bag.isTexture(), bag.isHideMenu(), bag.getHeight(), bag.isUseEmote());
                break;
            case WALKING_STICK:
                WStick wStick = (WStick) cosmetic;
                cosmec = new WStick(wStick.getId(), wStick.getName(), wStick.getItemStack().clone(), wStick.getModelData(), wStick.isColored(), wStick.getCosmeticType(), wStick.getColor(), wStick.getPermission(), wStick.isTexture(), wStick.isOverlaps(), wStick.isHideMenu(), wStick.isUseEmote());
                break;
            case BALLOON:
                Balloon balloon = (Balloon) cosmetic;
                cosmec = new Balloon(balloon.getId(), balloon.getName(), balloon.getItemStack().clone(), balloon.getModelData(), balloon.isColored(), balloon.getSpace(), balloon.getCosmeticType(), balloon.getColor(), balloon.isRotation(), balloon.getRotationType(), balloon.getBalloonEngine() != null ? balloon.getBalloonEngine().getClone() : null, balloon.getBalloonIA() != null ? balloon.getBalloonIA().getClone() : null, balloon.getDistance(), balloon.getPermission(), balloon.isTexture(), balloon.isBigHead(), balloon.isHideMenu(), balloon.isInvisibleLeash(), balloon.isUseEmote(), balloon.isInstantFollow());
                break;
            case SPRAY:
                Spray spray = (Spray) cosmetic;
                cosmec = new Spray(spray.getId(), spray.getName(), spray.getItemStack().clone(), spray.getModelData(), spray.isColored(), spray.getCosmeticType(), spray.getColor(), spray.getPermission(), spray.isTexture(), spray.getImage(), spray.isItemImage(), spray.isHideMenu(), spray.isUseEmote());
                break;
        }
        return cosmec;
    }

    public static void loadCosmetics(){
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        cosmetics.clear();
        FileCosmetics cosmeticsFiles = plugin.getCosmetics();
        int cosmetics_count = 0;
        for(FileCreator cosmeticsConf : cosmeticsFiles.getFiles().values()){
            plugin.getLogger().info("Loading cosmetics from file: " + cosmeticsConf.getFileName());
            if(!cosmeticsConf.contains("cosmetics")) continue;
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
                BalloonIA balloonIA = null;
                boolean rotation = false;
                RotationType rotationType = null;
                int modelData = 0;
                ItemStack bagForMe = null;
                int height = 0;
                double distance = 800;
                String permission = "";
                BufferedImage image = null;
                boolean itemImage = false;
                boolean isTexture = false;
                boolean bigHead = false;
                boolean hideMenu = false;
                boolean useEmote = false;
                boolean invisibleLeash = false;
                boolean instantFollow = false;
                if(cosmeticsConf.contains("cosmetics." + key + ".permission")){
                    permission = cosmeticsConf.getString("cosmetics." + key + ".permission");
                }
                if(cosmeticsConf.contains("cosmetics." + key + ".url")){
                    String url = cosmeticsConf.getString("cosmetics." + key + ".url");
                    image = Utils.getImage(url);
                    if(image == null){
                        plugin.getLogger().warning("Could not load Spray image from url: " + url);
                        continue;
                    }
                }
                if(cosmeticsConf.contains("cosmetics." + key + ".item")){
                    List<String> lore = null;
                    boolean unbreakable = false;
                    boolean glow = false;
                    boolean hide_attributes = false;
                    String texture = "";
                    if(cosmeticsConf.contains("cosmetics." + key + ".item.texture")){
                        texture = cosmeticsConf.getString("cosmetics." + key + ".item.texture");
                        isTexture = true;
                    }
                    if(cosmeticsConf.contains("cosmetics." + key + ".item.display")){
                        name = cosmeticsConf.getString("cosmetics." + key + ".item.display");
                        if(plugin.isItemsAdder()){
                            name = plugin.getItemsAdder().replaceFontImages(name);
                        }
                    }
                    if(cosmeticsConf.contains("cosmetics." + key + ".item.material")){
                        String item = cosmeticsConf.getString("cosmetics." + key + ".item.material");
                        try{
                            itemStack = XMaterial.valueOf(item.toUpperCase()).parseItem();
                        }catch (IllegalArgumentException exception){
                            plugin.getLogger().warning("Item Material '" + item + "' in Cosmetic '" + key + "' Not Found!");
                        }
                    }
                    if(cosmeticsConf.contains("cosmetics." + key + ".item.lore")){
                        lore = cosmeticsConf.getStringList("cosmetics." + key + ".item.lore");
                        if(plugin.isItemsAdder()){
                            List<String> lore2 = new ArrayList<>();
                            for(String l : lore) {
                                lore2.add(plugin.getItemsAdder().replaceFontImages(l));
                            }
                            lore.clear();
                            lore.addAll(lore2);
                        }
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
                        if(hex != null){
                            color = com.francobm.magicosmetics.cache.Color.hex2Rgb(hex);
                        }
                    }
                    if(cosmeticsConf.contains("cosmetics." + key + ".height")) {
                        height = cosmeticsConf.getInt("cosmetics." + key + ".height");
                    }
                    if(cosmeticsConf.contains("cosmetics." + key + ".item.item-adder")){
                        if(!plugin.isItemsAdder()){
                            plugin.getLogger().warning("Item Adder plugin Not Found skipping cosmetic '" + key + "'");
                            continue;
                        }
                        String id = cosmeticsConf.getString("cosmetics." + key + ".item.item-adder");
                        ItemStack ia = plugin.getItemsAdder().getCustomItemStack(id);
                        if(ia == null){
                            plugin.getLogger().warning("IA Item: '" + id + "' Not Found skipping...");
                            continue;
                        }
                        itemStack = ia.clone();
                        modelData = -1;
                    }
                    if(cosmeticsConf.contains("cosmetics." + key + ".item.oraxen")){
                        if(!plugin.isOraxen()){
                            plugin.getLogger().warning("Oraxen plugin Not Found skipping cosmetic '" + key + "'");
                            continue;
                        }
                        String id = cosmeticsConf.getString("cosmetics." + key + ".item.oraxen");
                        ItemStack oraxen = plugin.getOraxen().getItemStackById(id);
                        if(oraxen == null){
                            plugin.getLogger().warning("Oraxen item:  '" + id + "' Not Found skipping...");
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
                    if(itemStack.getType() == XMaterial.PLAYER_HEAD.parseMaterial()) {
                        itemStack = Utils.getCustomHead(itemStack, texture);
                    }
                    if(cosmeticsConf.contains("cosmetics." + key + ".item.for-me")){
                        int datamodel = cosmeticsConf.getInt("cosmetics." + key + ".item.for-me");
                        if(datamodel != 0) {
                            bagForMe = itemStack.clone();
                            ItemMeta itemMeta1 = bagForMe.getItemMeta();
                            itemMeta1.setCustomModelData(datamodel);
                            bagForMe.setItemMeta(itemMeta1);
                        }else{
                            String id = cosmeticsConf.getString("cosmetics." + key + ".item.for-me");
                            if(id.startsWith("item-adder")){
                                if(!plugin.isItemsAdder()){
                                    plugin.getLogger().warning("ItemsAdder plugin not found, skipping cosmetic");
                                    continue;
                                }
                                String ia = id.split(";")[1];
                                ItemStack item_ia = plugin.getItemsAdder().getCustomItemStack(ia);
                                if(item_ia == null){
                                    plugin.getLogger().warning("IA Item: '" + ia + "' Not Found skipping...");
                                    continue;
                                }
                                bagForMe = itemStack.clone();
                                bagForMe.setType(item_ia.getType());
                                ItemMeta itemMeta1 = bagForMe.getItemMeta();
                                itemMeta1.setCustomModelData(item_ia.getItemMeta().getCustomModelData());
                                bagForMe.setItemMeta(itemMeta1);
                            }else if(id.startsWith("oraxen")) {
                                if(!plugin.isOraxen()){
                                    plugin.getLogger().warning("Oraxen plugin not found, skipping cosmetic");
                                    continue;
                                }
                                String oraxen = id.split(";")[1];
                                ItemStack item_orax = plugin.getOraxen().getItemStackById(oraxen);
                                if(item_orax == null){
                                    plugin.getLogger().warning("Oraxen Item: '" + oraxen + "' Not Found skipping...");
                                    continue;
                                }
                                bagForMe = itemStack.clone();
                                bagForMe.setType(item_orax.getType());
                                ItemMeta itemMeta1 = bagForMe.getItemMeta();
                                itemMeta1.setCustomModelData(item_orax.getItemMeta().getCustomModelData());
                                bagForMe.setItemMeta(itemMeta1);
                            }
                        }

                    }
                }
                if(cosmeticsConf.contains("cosmetics." + key + ".item-image")){
                    itemImage = cosmeticsConf.getBoolean("cosmetics." + key + ".item-image");
                }
                if(cosmeticsConf.contains("cosmetics." + key + ".type")){
                    type = cosmeticsConf.getString("cosmetics." + key + ".type");
                    try{
                        cosmeticType = CosmeticType.valueOf(type.toUpperCase());
                    }catch (IllegalArgumentException exception){
                        plugin.getLogger().warning("Cosmetic Type: " + type + " Not Found.");
                        return;
                    }
                }
                if(cosmeticsConf.contains("cosmetics." + key + ".colored")){
                    colored = cosmeticsConf.getBoolean("cosmetics." + key + ".colored");
                }
                if(cosmeticsConf.contains("cosmetics." + key + ".hide-menu")){
                    hideMenu = cosmeticsConf.getBoolean("cosmetics." + key + ".hide-menu");
                }
                if(cosmeticsConf.contains("cosmetics." + key + ".use-emote")){
                    useEmote = cosmeticsConf.getBoolean("cosmetics." + key + ".use-emote");
                }
                if(cosmeticsConf.contains("cosmetics." + key + ".big-head")){
                    bigHead = cosmeticsConf.getBoolean("cosmetics." + key + ".big-head");
                }
                if(cosmeticsConf.contains("cosmetics." + key + ".rotation")){
                    rotation = cosmeticsConf.getBoolean("cosmetics." + key + ".rotation.enabled");
                    String rotType = cosmeticsConf.getString("cosmetics." + key + ".rotation.type");
                    try{
                        rotationType = RotationType.valueOf(rotType.toUpperCase());
                    }catch (IllegalArgumentException exception){
                        plugin.getLogger().warning("Cosmetic Type: " + type + " Rotation Type Not Found.");
                    }
                }
                if(cosmeticsConf.contains("cosmetics." + key + ".space")){
                    space = cosmeticsConf.getDouble("cosmetics." + key + ".space");
                }
                if(cosmeticsConf.contains("cosmetics." + key + ".overlaps")){
                    overlaps = cosmeticsConf.getBoolean("cosmetics." + key + ".overlaps");
                }
                if(cosmeticsConf.contains("cosmetics." + key + ".distance")){
                    distance = cosmeticsConf.getDouble("cosmetics." + key + ".distance");
                }
                if(cosmeticsConf.contains("cosmetics." + key + ".invisible-leash")) {
                    invisibleLeash = cosmeticsConf.getBoolean("cosmetics." + key + ".invisible-leash");
                }
                if(cosmeticsConf.contains("cosmetics." + key + ".instant-follow")) {
                    instantFollow = cosmeticsConf.getBoolean("cosmetics." + key + ".instant-follow");
                }
                if(cosmeticsConf.contains("cosmetics." + key + ".meg.model")){
                    if(!plugin.isModelEngine()){
                        plugin.getLogger().warning("Model Engine plugin Not Found skipping cosmetic '" + key + "'");
                        continue;
                    }
                    String modelId = cosmeticsConf.getString("cosmetics." + key + ".meg.model");
                    List<String> colorableParts = cosmeticsConf.getStringListWF("cosmetics." + key + ".meg.colorable-parts");
                    String walk_animation = cosmeticsConf.getString("cosmetics." + key + ".meg.animations.walk");
                    String idle_animation = cosmeticsConf.getString("cosmetics." + key + ".meg.animations.idle");
                    balloonEngine = new BalloonEngine(modelId, colorableParts, walk_animation, idle_animation, distance);
                }
                if(cosmeticsConf.contains("cosmetics." + key + ".ia.model")){
                    if(!plugin.isItemsAdder()){
                        plugin.getLogger().warning("ItemsAdder plugin Not Found skipping cosmetic '" + key + "'");
                        continue;
                    }
                    String modelId = cosmeticsConf.getString("cosmetics." + key + ".ia.model");
                    if(!plugin.getItemsAdder().existModel(modelId)) {
                        plugin.getLogger().warning("ItemsAdder model Not Found skipping cosmetic '" + key + "'");
                        continue;
                    }
                    List<String> colorableParts = cosmeticsConf.getStringListWF("cosmetics." + key + ".ia.colorable-parts");
                    String walk_animation = cosmeticsConf.getString("cosmetics." + key + ".ia.animations.walk");
                    String idle_animation = cosmeticsConf.getString("cosmetics." + key + ".ia.animations.idle");
                    balloonIA = new BalloonIA(modelId, colorableParts, walk_animation, idle_animation);
                }
                if(cosmeticType == null){
                    return;
                }
                itemStack = plugin.getVersion().setNBTCosmetic(itemStack, key);
                switch (cosmeticType){
                    case HAT:
                        Hat hat = new Hat(key, name, itemStack, modelData, colored, cosmeticType, color, overlaps, permission, isTexture, hideMenu, useEmote);
                        if(color != null) {
                            hat.setDefaultColor(true);
                        }
                        cosmetics.put(key, hat);
                        break;
                    case BAG:
                        Bag bag = new Bag(key, name, itemStack, modelData, bagForMe, colored, space, cosmeticType, color, distance, permission, isTexture, hideMenu, height, useEmote);
                        if(color != null) {
                            bag.setDefaultColor(true);
                        }
                        cosmetics.put(key, bag);
                        break;
                    case WALKING_STICK:
                        WStick wStick = new WStick(key, name, itemStack, modelData, colored, cosmeticType, color, permission, isTexture, overlaps, hideMenu, useEmote);
                        if(color != null) {
                            wStick.setDefaultColor(true);
                        }
                        cosmetics.put(key, wStick);
                        break;
                    case BALLOON:
                        Balloon balloon = new Balloon(key, name, itemStack, modelData, colored, space, cosmeticType, color, rotation, rotationType, balloonEngine, balloonIA, distance, permission, isTexture, bigHead, hideMenu, invisibleLeash, useEmote, instantFollow);
                        if(color != null) {
                            balloon.setDefaultColor(true);
                        }
                        cosmetics.put(key, balloon);
                        break;
                    case SPRAY:
                        Spray spray = new Spray(key, name, itemStack, modelData, colored, cosmeticType, color, permission, isTexture, image, itemImage, hideMenu, useEmote);
                        if(color != null) {
                            spray.setDefaultColor(true);
                        }
                        cosmetics.put(key, spray);
                        break;
                }
                cosmetics_count++;
            }
            if(plugin.getConfig().contains("order-cosmetics")){
                int order = plugin.getConfig().getInt("order-cosmetics");
                switch (order){
                    case 0:
                        break;
                    case 1:
                        cosmetics = new TreeMap<>(cosmetics);
                        break;
                    case 2:
                        List<Cosmetic> list = new ArrayList<>(cosmetics.values());
                        Collections.shuffle(list);
                        cosmetics = new LinkedHashMap<>();
                        list.forEach(cosmetic -> cosmetics.put(cosmetic.getId(), cosmetic));
                        break;
                }
            }
            plugin.getLogger().info("Registered cosmetics: " + cosmetics_count);
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ItemStack getItemStack() {
        if(isDefaultColor()){
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
        /*if(this.itemStack == null) return false;
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
        if(cosmetic.hasItemMeta() && itemStack.hasItemMeta()) {
            if (cosmetic.getItemMeta().hasDisplayName() && itemStack.getItemMeta().hasDisplayName()) {
                return cosmetic.getItemMeta().getDisplayName().equals(itemStack.getItemMeta().getDisplayName());
            }
        }*/
        String key = MagicCosmetics.getInstance().getVersion().isNBTCosmetic(itemStack);
        return key != null && !key.isEmpty() && key.equalsIgnoreCase(id);
    }

    public ItemStack getItemPlaceholders(Player player) {
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        if(!plugin.isPlaceholders()) return getItemColor(player);
        ItemStack itemStack = getItemColor(player);
        ItemStack itemClone = itemStack.clone();
        if(!plugin.isPlaceholderAPI()) return itemStack;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return getItemColor(player);
        if(itemMeta.hasDisplayName()) {
            itemMeta.setDisplayName(plugin.getPlaceholderAPI().setPlaceholders(player, itemClone.getItemMeta().getDisplayName()));
        }
        if(itemMeta.hasLore()) {
            itemMeta.setLore(plugin.getPlaceholderAPI().setPlaceholders(player, itemClone.getItemMeta().getLore()));
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack getItemColor(){
        if(itemStack == null) return null;
        ItemStack itemStack = this.itemStack.clone();
        if(itemStack.getItemMeta() instanceof LeatherArmorMeta){
            LeatherArmorMeta itemMeta = (LeatherArmorMeta) itemStack.getItemMeta();
            if(color != null) {
                itemMeta.setColor(color);
            }
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }
        if(itemStack.getItemMeta() instanceof PotionMeta){
            PotionMeta itemMeta = (PotionMeta) itemStack.getItemMeta();
            if(color != null) {
                itemMeta.setColor(color);
            }
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }
        if(itemStack.getItemMeta() instanceof MapMeta){
            MapMeta itemMeta = (MapMeta) itemStack.getItemMeta();
            if(color != null) {
                itemMeta.setColor(color);
            }
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }
        if(itemStack.getItemMeta() instanceof FireworkEffectMeta) {
            FireworkEffectMeta itemMeta = (FireworkEffectMeta) itemStack.getItemMeta();
            if(color != null)
                itemMeta.setEffect(FireworkEffect.builder().withColor(color).build());
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }

    public ItemStack getItemColor(Player player) {
        if(isTexture()) return getItemColor();
        ItemStack itemStack = getItemColor();
        if(itemStack.getType() != XMaterial.PLAYER_HEAD.parseMaterial()) return itemStack;
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setOwningPlayer(player);
        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }

    public String getPermission() {
        return permission;
    }

    public boolean hasPermission(Player player){
        if(permission == null || permission.isEmpty()) return false;
        return player.hasPermission(permission);
    }

    public boolean isTexture() {
        return texture;
    }

    public void setDefaultColor(boolean defaultColor) {
        this.defaultColor = defaultColor;
    }

    public boolean isDefaultColor() {
        return defaultColor;
    }

    public boolean isHideMenu() {
        return hideMenu;
    }

    public boolean isHideCosmetic() {
        return hideCosmetic;
    }

    public void setHideCosmetic(boolean hideCosmetic) {
        this.hideCosmetic = hideCosmetic;
    }

    public boolean isUseEmote() {
        return useEmote;
    }

    public boolean isColorBlocked() {
        return colorBlocked;
    }

    public void setColorBlocked(boolean colorBlocked) {
        this.colorBlocked = colorBlocked;
    }
}
