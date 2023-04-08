package com.francobm.magicosmetics.cache;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.utils.Utils;
import com.francobm.magicosmetics.utils.XMaterial;
import org.bukkit.DyeColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Color {
    public static Map<String, Color> colors = new HashMap<>();
    private static final Map<String, Row> rows = new HashMap<>();
    private final String id;
    private final String name;
    private final String permission;
    private final org.bukkit.Color primaryColor;
    private final ItemStack primaryItem;
    private final String select;
    private final boolean withRow;
    private final List<org.bukkit.Color> secondaryColors;
    private final int slot;

    public Color(String id, String name, String permission, org.bukkit.Color primaryColor, ItemStack primaryItem, String select, boolean withRow, List<org.bukkit.Color> secondaryColors, int slot) {
        this.id = id;
        this.name = name;
        this.permission = permission;
        this.primaryColor = primaryColor;
        this.primaryItem = primaryItem;
        this.select = select;
        this.withRow = withRow;
        this.secondaryColors = secondaryColors;
        this.slot = slot;
    }

    public static Row getRow(String id){
        return rows.get(id);
    }

    public static Color getColor(String id){
        return colors.get(id);
    }

    public String getPermission() {
        return permission;
    }

    public static void loadColors(){
        colors.clear();
        rows.clear();
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        int colors_count = 0;
        if(!plugin.getMenus().contains("colors")) return;
        if(plugin.getMenus().contains("colors.rows")) {
            for (String key : plugin.getMenus().getConfigurationSection("colors.rows").getKeys(false)) {
                if(!plugin.getMenus().contains("colors.rows." + key)) continue;
                String character = plugin.getMenus().getString("colors.rows." + key + ".character");
                String selected = plugin.getMenus().getString("colors.rows." + key + ".selected");
                if(plugin.isItemsAdder()){
                    character = plugin.getItemsAdder().replaceFontImages(character);
                    selected = plugin.getItemsAdder().replaceFontImages(selected);
                }
                if(plugin.isOraxen()){
                    character = plugin.getOraxen().replaceFontImages(character);
                    selected = plugin.getOraxen().replaceFontImages(selected);
                }
                rows.put(key, new Row(key, character, selected));
            }
        }
        for(String key : plugin.getMenus().getConfigurationSection("colors").getKeys(false)){
            if(!plugin.getMenus().contains("colors." + key + ".name")) continue;
            int slot = 0;
            String name = "";
            String permission = "";
            org.bukkit.Color primaryColor = null;
            ItemStack primaryItem = null;
            String select = "";
            boolean withRow = true;
            List<org.bukkit.Color> secondaryColors = new ArrayList<>();
            if(plugin.getMenus().contains("colors." + key + ".name")){
                name = plugin.getMenus().getString("colors." + key + ".name");
            }
            if(plugin.getMenus().contains("colors." + key + ".permission")){
                permission = plugin.getMenus().getString("colors." + key + ".permission");
            }
            if(plugin.getMenus().contains("colors." + key + ".primary-item")) {
                String displayName = "";
                List<String> lore = null;
                boolean unbreakable = false;
                boolean glow = false;
                boolean hide_attributes = false;
                int modelData = -1;
                String texture = "";
                if(plugin.getMenus().contains("colors." + key + ".primary-item.texture")){
                    texture = plugin.getMenus().getString("colors." + key + ".primary-item.texture");
                }
                if(plugin.getMenus().contains("colors." + key + ".primary-item.display")){
                    displayName = plugin.getMenus().getString("colors." + key + ".primary-item.display");
                    if(plugin.isItemsAdder())
                        displayName = plugin.getItemsAdder().replaceFontImages(displayName);
                    if(plugin.isOraxen())
                        displayName = plugin.getOraxen().replaceFontImages(displayName);
                }
                if(plugin.getMenus().contains("colors." + key + ".primary-item.material")){
                    String item = plugin.getMenus().getString("colors." + key + ".primary-item.material");
                    try{
                        primaryItem = XMaterial.valueOf(item.toUpperCase()).parseItem();
                    }catch (IllegalArgumentException exception){
                        plugin.getLogger().warning("Primary Item Material '" + item + "' in Color '" + key + "' Not Found!");
                    }
                }
                if(plugin.getMenus().contains("colors." + key + ".primary-item.lore")){
                    lore = plugin.getMenus().getStringList("colors." + key + ".primary-item.lore");
                    if(plugin.isItemsAdder()){
                        List<String> lore2 = new ArrayList<>();
                        for(String l : lore) {
                            lore2.add(plugin.getItemsAdder().replaceFontImages(l));
                        }
                        lore.clear();
                        lore.addAll(lore2);
                    }
                    if(plugin.isOraxen()){
                        List<String> lore2 = new ArrayList<>();
                        for(String l : lore) {
                            lore2.add(plugin.getOraxen().replaceFontImages(l));
                        }
                        lore.clear();
                        lore.addAll(lore2);
                    }
                }
                if(plugin.getMenus().contains("colors." + key + ".primary-item.glow")){
                    glow = plugin.getMenus().getBoolean("colors." + key + ".primary-item.glow");
                }
                if(plugin.getMenus().contains("colors." + key + ".primary-item.hide-attributes")){
                    hide_attributes = plugin.getMenus().getBoolean("colors." + key + ".primary-item.hide-attributes");
                }
                if(plugin.getMenus().contains("colors." + key + ".primary-item.unbreakable")){
                    unbreakable = plugin.getMenus().getBoolean("colors." + key + ".primary-item.unbreakable");
                }
                if(plugin.getMenus().contains("colors." + key + ".primary-item.modeldata")){
                    modelData = plugin.getMenus().getInt("colors." + key + ".primary-item.modeldata");
                }
                if(plugin.getMenus().contains("colors." + key + ".primary-item.item-adder")){
                    if(!plugin.isItemsAdder()){
                        plugin.getLogger().warning("Item Adder plugin Not Found skipping color '" + key + "'");
                        continue;
                    }
                    String id = plugin.getMenus().getString("colors." + key + ".primary-item.item-adder");
                    ItemStack ia = plugin.getItemsAdder().getCustomItemStack(id);
                    if(ia == null){
                        plugin.getLogger().warning("IA Item: '" + id + "' Not Found skipping...");
                        continue;
                    }
                    primaryItem = ia.clone();
                    modelData = -1;
                }
                if(plugin.getMenus().contains("colors." + key + ".primary-item.oraxen")){
                    if(!plugin.isOraxen()){
                        plugin.getLogger().warning("Oraxen plugin Not Found skipping color '" + key + "'");
                        continue;
                    }
                    String id = plugin.getMenus().getString("colors." + key + ".primary-item.oraxen");
                    ItemStack oraxen = plugin.getOraxen().getItemStackById(id);
                    if(oraxen == null){
                        plugin.getLogger().warning("Oraxen item:  '" + id + "' Not Found skipping...");
                        continue;
                    }
                    primaryItem = oraxen.clone();
                    modelData = -1;
                }
                ItemMeta itemMeta = primaryItem.getItemMeta();
                itemMeta.setDisplayName(displayName);
                itemMeta.setLore(lore);
                if(glow){
                    primaryItem.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                    itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }
                if(hide_attributes) {
                    itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE, ItemFlag.HIDE_UNBREAKABLE);
                }
                itemMeta.setUnbreakable(unbreakable);
                if(modelData != -1) {
                    itemMeta.setCustomModelData(modelData);
                }
                primaryItem.setItemMeta(itemMeta);
                if(primaryItem.getType() == XMaterial.PLAYER_HEAD.parseMaterial() && texture != null) {
                    primaryItem = Utils.getCustomHead(primaryItem, texture);
                }
            }
            if(plugin.getMenus().contains("colors." + key + ".primary-color")){
                String color = plugin.getMenus().getString("colors." + key + ".primary-color");
                try{
                    primaryColor = DyeColor.valueOf(color).getColor();
                }catch (IllegalArgumentException exception){
                    plugin.getLogger().warning("Primary Color: '" + color + "' Not Found Parsing to Hex Color...");
                    try{
                        primaryColor = hex2Rgb(color);
                    }catch (IllegalArgumentException ex){
                        plugin.getLogger().warning("Primary Color Hex: " + color + " Not Found Skipping...");
                        continue;
                    }
                }
            }
            if(plugin.getMenus().contains("colors." + key + ".select")){
                select = plugin.getMenus().getString("colors." + key + ".select");
                if(plugin.isItemsAdder()){
                    select = plugin.getItemsAdder().replaceFontImages(select);
                }
                if(plugin.isOraxen()){
                    select = plugin.getOraxen().replaceFontImages(select);
                }
            }
            if(plugin.getMenus().contains("colors." + key + ".with-row")){
                withRow = plugin.getMenus().getBoolean("colors." + key + ".with-row");
            }
            if(plugin.getMenus().contains("colors." + key + ".secondary-colors")){
                for(String secondary : plugin.getMenus().getStringListWF("colors." + key + ".secondary-colors")){
                    try{
                        secondaryColors.add(hex2Rgb(secondary));
                    }catch (IllegalArgumentException exception){
                        plugin.getLogger().warning("Secondary Color from " + key + " RGB: '" + secondary + "' Not Found.");
                    }
                }
            }
            if(plugin.getMenus().contains("colors." + key + ".slot")){
                slot = plugin.getMenus().getInt("colors." + key + ".slot");
            }

            colors.put(key, new Color(key, name, permission, primaryColor, primaryItem, select, withRow, secondaryColors, slot));
            colors_count++;
        }
        MagicCosmetics.getInstance().getLogger().info("Registered colors: " + colors_count);
    }

    public ItemStack getPrimaryItem() {
        return primaryItem;
    }

    public boolean hasPermission(Player player) {
        if(permission.isEmpty()) return true;
        return player.hasPermission(permission);
    }

    public boolean isPrimaryItem() {
        return primaryItem != null;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public org.bukkit.Color getPrimaryColor() {
        return primaryColor;
    }

    public List<org.bukkit.Color> getSecondaryColors() {
        return secondaryColors;
    }

    public int getSlot() {
        return slot;
    }

    public static org.bukkit.Color hex2Rgb(String colorStr) {
        return org.bukkit.Color.fromRGB(
                Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
                Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
                Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
    }

    public String getSelectWithRow() {
        Row row = getRow(String.valueOf(slot % 9));
        return row == null ? this.select : row.getCharacter() + this.select;
    }

    public String getSelect() {
        if(withRow){
            return getSelectWithRow();
        }
        return select;
    }

    public Row getRow(){
        return getRow(String.valueOf(slot % 9));
    }

    public boolean isWithRow() {
        return withRow;
    }
}
