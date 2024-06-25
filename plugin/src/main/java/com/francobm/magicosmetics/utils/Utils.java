package com.francobm.magicosmetics.utils;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.cache.Sound;
import com.francobm.magicosmetics.cache.cosmetics.Spray;
import com.francobm.magicosmetics.cache.renderer.ImageRenderer;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapView;
import org.bukkit.profile.PlayerProfile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

    public static String getVersion(){
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    public static boolean existPluginClass(String className) {
        try {
            Class.forName(className);
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    public static String getTime(int time){
        int hours = time / 3600;
        int i = time - hours * 3600;
        int minutes = i / 60;
        int seconds = i - minutes * 60;
        String secondsMsg;
        String minutesMsg;
        String hoursMsg;
        if(seconds < 10) {
            if(seconds == 1) {
                secondsMsg = "0" + seconds + " second";
            } else {
                secondsMsg = "0" + seconds + " seconds";
            }
        } else {
            secondsMsg = seconds + " seconds";
        }
        if(minutes < 10) {
            if(minutes == 1) {
                minutesMsg = "0" + minutes + " minute";
            } else {
                minutesMsg = "0" + minutes + " minutes";
            }
        } else {
            minutesMsg = minutes + " minutes";
        }
        if(hours < 10) {
            if(hours == 1) {
                hoursMsg = "0" + hours + " hour";
            } else {
                hoursMsg = "0" + hours + " hours";
            }
        } else {
            hoursMsg = hours + " hours";
        }

        if(hours != 0)
        {
            return hoursMsg + " " + minutesMsg + " " + secondsMsg;
        }else if(minutes != 0) {
            return minutesMsg + " " + secondsMsg;
        }
        return secondsMsg;
    }

    public static void sendSound(Player player, Sound sound) {
        if(player == null) return;
        if(sound == null) return;

        if(sound.isCustom()) {
            player.playSound(player.getLocation(), sound.getSoundCustom(), sound.getYaw(), sound.getPitch());
            return;
        }
        player.playSound(player.getLocation(), sound.getSoundBukkit(), sound.getYaw(), sound.getPitch());
    }

    public static void sendAllSound(Location location, Sound sound) {
        if(location.getWorld() == null) return;
        if(sound == null) return;

        if(sound.isCustom()) {
            location.getWorld().playSound(location, sound.getSoundCustom(), sound.getYaw(), sound.getPitch());
            return;
        }
        location.getWorld().playSound(location, sound.getSoundBukkit(), sound.getYaw(), sound.getPitch());
    }

    public static ItemStack getMapImage(Player player, BufferedImage image, Spray spray){
        Color color = spray.getColor();
        if(!spray.isPaint()) {
            if (color != null) {
                Graphics2D g = image.createGraphics();
                g.setPaint(new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue(), 120));
                g.fillRect(0, 0, image.getWidth(), image.getHeight());
                spray.setPaint(true);
            }
        }
        MapView mapView = Bukkit.createMap(player.getWorld());
        mapView.getRenderers().clear();
        ImageRenderer imageRenderer = new ImageRenderer();
        if(!imageRenderer.load(image)) return null;
        mapView.addRenderer(imageRenderer);
        ItemStack map = XMaterial.FILLED_MAP.parseItem();
        if(map == null) return null;
        MapMeta meta = (MapMeta) map.getItemMeta();
        if(meta == null) return null;
        meta.setMapView(mapView);
        map.setItemMeta(meta);
        return map;
    }

    public static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPreMultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(bi.getRaster().createCompatibleWritableRaster());
        return new BufferedImage(cm, raster, isAlphaPreMultiplied, null);
    }

    public static BufferedImage getImage(String url){
        BufferedImage image;
        try {
            if(url.startsWith("http")) {
                image = ImageIO.read(new URL(url));
            }else{
                File file = new File(MagicCosmetics.getInstance().getDataFolder(), "sprays/" + url);
                if(!file.exists()) {
                    return null;
                }
                image = ImageIO.read(file);
            }
            image = MapPalette.resizeImage(image);
        } catch (IOException e) {
            return null;
        }
        return image;
    }

    public static boolean isDyeable(ItemStack itemStack){
        if(itemStack == null) return false;
        ItemMeta itemMeta = itemStack.getItemMeta();
        return (itemMeta instanceof LeatherArmorMeta || itemMeta instanceof PotionMeta || itemMeta instanceof MapMeta || itemMeta instanceof FireworkEffectMeta);
    }

    public static void hidePlayer(Player player){
        for(Player players : Bukkit.getOnlinePlayers()){
            players.hidePlayer(MagicCosmetics.getInstance(), player);
        }
    }

    public static void showPlayer(Player player){
        for(Player players : Bukkit.getOnlinePlayers()){
            players.showPlayer(MagicCosmetics.getInstance(), player);
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

    public static String ChatColor(String message){
        if(Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17") || Bukkit.getVersion().contains("1.18") || Bukkit.getVersion().contains("1.19") || Bukkit.getVersion().contains("1.20")){
            Matcher matcher = pattern.matcher(message);
            while(matcher.find()){
                String color = message.substring(matcher.start(), matcher.end());
                message = message.replace(color, ChatColor.of(color) + "");
                matcher = pattern.matcher(message);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static int getRotation(float yaw, boolean allowDiagonals) {
        if(allowDiagonals) return MathUtils.floor(((Location.normalizeYaw(yaw) + 180) * 8 / 360) + 0.5F) % 8;
        return MathUtils.floor(((Location.normalizeYaw(yaw) + 180) * 4 / 360) + 0.5F) % 4;
    }

    public static String bsc(String string){
        return new String(Base64.getDecoder().decode(string));
    }

    public static org.bukkit.Color hex2Rgb(String colorStr) {
        return org.bukkit.Color.fromRGB(
                Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
                Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
                Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
    }

    public static YamlConfiguration getPaperConfig(Server server) {
        try {
            return (YamlConfiguration) Server.Spigot.class.getMethod("getPaperConfig").invoke(server.spigot());
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static boolean isPaper() {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            return true;
        } catch (ClassNotFoundException ignored) {}
        try {
            Class.forName("io.papermc.paper.configuration.Configuration");
            return true;
        } catch (ClassNotFoundException ignored) {}
        return false;
    }

    private static boolean isMajorTo181(String version) {
        String[] partes = version.split("\\.");
        int major = Integer.parseInt(partes[1]);
        int minor = Integer.parseInt(partes[2]);
        int patch = Integer.parseInt(partes[3]);
        return major > 1 || (major == 1 && (minor > 18 || (minor == 18 && patch > 1)));
    }

}
