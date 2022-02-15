package com.francobm.customcosmetics.cache;

import com.francobm.customcosmetics.CustomCosmetics;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Color {
    public static Map<String, Color> colors = new HashMap<>();
    private final String id;
    private final org.bukkit.Color primaryColor;
    private final List<org.bukkit.Color> secondaryColors;

    public Color(String id, org.bukkit.Color primaryColor, List<org.bukkit.Color> secondaryColors) {
        this.id = id;
        this.primaryColor = primaryColor;
        this.secondaryColors = secondaryColors;
        CustomCosmetics.getInstance().getLogger().info("Color named: '" + id + "' registered.");
    }

    public static Color getColor(String id){
        return colors.get(id);
    }

    public static void loadColors(){
        colors.clear();
        CustomCosmetics plugin = CustomCosmetics.getInstance();
        if(!plugin.getMenus().contains("colors")) return;
        for(String key : plugin.getMenus().getConfigurationSection("colors").getKeys(false)){

            org.bukkit.Color primaryColor = null;
            List<org.bukkit.Color> secondaryColors = new ArrayList<>();
            if(plugin.getMenus().contains("colors." + key + ".primary-color")){
                String color = plugin.getMenus().getString("colors." + key + ".primary-color");
                try{
                    primaryColor = DyeColor.valueOf(color).getColor();
                }catch (IllegalArgumentException exception){
                    plugin.getLogger().info("Primary Color: '" + color + "' Not Found.");
                }
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

            colors.put(key, new Color(key, primaryColor, secondaryColors));
        }
    }

    public String getId() {
        return id;
    }

    public org.bukkit.Color getPrimaryColor() {
        return primaryColor;
    }

    public List<org.bukkit.Color> getSecondaryColors() {
        return secondaryColors;
    }

    public static org.bukkit.Color hex2Rgb(String colorStr) {
        return org.bukkit.Color.fromRGB(
                Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
                Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
                Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
    }
}
