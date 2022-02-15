package com.francobm.customcosmetics.utils;

import com.francobm.customcosmetics.CustomCosmetics;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

    public static String getVersion(){
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    public static void hidePlayer(Player player){
        for(Player players : Bukkit.getOnlinePlayers()){
            players.hidePlayer(CustomCosmetics.getInstance(), player);
        }
    }

    public static void showPlayer(Player player){
        for(Player players : Bukkit.getOnlinePlayers()){
            players.showPlayer(CustomCosmetics.getInstance(), player);
        }
    }

    public static Location convertStringToLocation(String string){
        String[] strings = string.split(",");
        String world = strings[0];
        double x = Double.parseDouble(strings[1]);
        double y = Double.parseDouble(strings[2]);
        double z = Double.parseDouble(strings[3]);
        if(strings.length > 4){
            float yaw = Float.parseFloat(strings[4]);
            float pitch = Float.parseFloat(strings[5]);
            return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
        }
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

    public static String convertLocationToString(Location location, boolean isBlock){
        if(location != null){
            if(isBlock) {
                return location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ();
            }
            return location.getWorld().getName()+","+location.getX()+","+location.getY()+","+location.getZ()+","+location.getYaw()+","+location.getPitch();
        }
        return "Location is Null!!";
    }

    public static boolean isLegacyVersion(){
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        return packageName.contains("1_17_") || packageName.contains("1_16_") || packageName.contains("1_15_") || packageName.contains("1_14_") || packageName.contains("1_13_") || packageName.contains("1_12_") || packageName.contains("1_11_") || packageName.contains("1_10_") || packageName.contains("1_9_");
    }

    public static void isNew(){
        CustomCosmetics.getInstance().wkasdwk = new Utilities(CustomCosmetics.getInstance().getConfig().getString("license"), "https://licences-projects.000webhostapp.com/verify.php", CustomCosmetics.getInstance()).register();
    }

    public static String ChatColor(String message){
        if(Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17") || Bukkit.getVersion().contains("1.18")){
            Matcher matcher = pattern.matcher(message);
            while(matcher.find()){
                String color = message.substring(matcher.start(), matcher.end());
                message = message.replace(color, ChatColor.of(color) + "");
                matcher = pattern.matcher(message);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    /*public static String translateHexColorCodes(String startTag, String endTag, String message)
    {
        final char COLOR_CHAR = '\u00A7';
        final Pattern hexPattern = Pattern.compile(startTag + "([A-Fa-f0-9]{6})" + endTag);
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find())
        {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }
        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }*/

}
