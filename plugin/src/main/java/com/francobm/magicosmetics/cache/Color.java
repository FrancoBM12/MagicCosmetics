package com.francobm.magicosmetics.cache;

import com.francobm.magicosmetics.MagicCosmetics;
import org.bukkit.DyeColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Color {
    public static Map<String, Color> colors = new HashMap<>();
    private static final Map<String, Row> rows = new HashMap<>();
    private final String id;
    private final String name;
    private final org.bukkit.Color primaryColor;
    private final String select;
    private final boolean withRow;
    private final List<org.bukkit.Color> secondaryColors;
    private final int slot;

    public Color(String id, String name, org.bukkit.Color primaryColor, String select, boolean withRow, List<org.bukkit.Color> secondaryColors, int slot) {
        this.id = id;
        this.name = name;
        this.primaryColor = primaryColor;
        this.select = select;
        this.withRow = withRow;
        this.secondaryColors = secondaryColors;
        this.slot = slot;
        MagicCosmetics.getInstance().getLogger().info("Color named: '" + id + "' registered.");
    }

    public static Row getRow(String id){
        return rows.get(id);
    }

    public static Color getColor(String id){
        return colors.get(id);
    }

    public static void loadColors(){
        colors.clear();
        rows.clear();
        MagicCosmetics plugin = MagicCosmetics.getInstance();
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
            org.bukkit.Color primaryColor = null;
            String select = "";
            boolean withRow = true;
            List<org.bukkit.Color> secondaryColors = new ArrayList<>();
            if(plugin.getMenus().contains("colors." + key + ".name")){
                name = plugin.getMenus().getString("colors." + key + ".name");
            }
            if(plugin.getMenus().contains("colors." + key + ".primary-color")){
                String color = plugin.getMenus().getString("colors." + key + ".primary-color");
                try{
                    primaryColor = DyeColor.valueOf(color).getColor();
                }catch (IllegalArgumentException exception){
                    plugin.getLogger().info("Primary Color: '" + color + "' Not Found Parsing to Hex Color...");
                    try{
                        primaryColor = hex2Rgb(color);
                    }catch (IllegalArgumentException ex){
                        plugin.getLogger().info("Primary Color Hex: " + color + " Not Found Skipping...");
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
                        plugin.getLogger().info("Secondary Color from " + key + " RGB: '" + secondary + "' Not Found.");
                    }
                }
            }
            if(plugin.getMenus().contains("colors." + key + ".slot")){
                slot = plugin.getMenus().getInt("colors." + key + ".slot");
            }

            colors.put(key, new Color(key, name, primaryColor, select, withRow, secondaryColors, slot));
        }
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
