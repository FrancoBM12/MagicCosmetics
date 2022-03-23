package com.francobm.magicosmetics.utils;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.cache.User;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

    public static String getVersion(){
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
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

    public static boolean isLegacyVersion(){
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        return packageName.contains("1_17_") || packageName.contains("1_16_") || packageName.contains("1_15_") || packageName.contains("1_14_") || packageName.contains("1_13_") || packageName.contains("1_12_") || packageName.contains("1_11_") || packageName.contains("1_10_") || packageName.contains("1_9_");
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

    public static String bsc(String string){
        return new String(Base64.getDecoder().decode(string));
    }

    public static boolean siu(boolean debug) throws IOException {
        MagicCosmetics.getInstance().getLogger().info(Utils.bsc("VmFsaWRhdGluZyBwdXJjaGFzZS4uLg=="));
        String p = "%%__POLYMART__%%";
        String m = "%%__MCMARKET__%%";
        String s = "%%__SONGODA__%%";
        String user_id = "%%__USER__%%";
        String user_name = "%%__USERNAME__%%";
        String inject_version = p.equalsIgnoreCase("1") ? "%%__INJECT_VER__%%" : "%%__VERSION__%%";
        String resource_id = "%%__RESOURCE__%%";
        String plugin_id = "%%__PLUGIN__%%";
        String download_token = "%%__VERIFY_TOKEN__%%";
        String nonce = "%%__NONCE__%%";
        String download_agent = "%%__AGENT__%%";
        String download_time = "%%__TIMESTAMP__%%";
        if(p.equalsIgnoreCase("1")) {
            MagicCosmetics.getInstance().setUser(new User(user_id, user_name, inject_version, resource_id, download_token, nonce, download_agent, download_time));
            String api = "https://api.polymart.org/v1/verifyPurchase/";
            URL url = new URL(api + "?inject_version=" + inject_version + "&resource_id=" + resource_id + "&user_id=" + user_id + "&nonce=" + nonce + "&download_agent=" + download_agent + "&download_time=" + download_time + "&download_token=" + download_token);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("accept", "application/json");

            int responseCode = con.getResponseCode();
            if (debug) {
                MagicCosmetics.getInstance().getLogger().info("\nSending 'GET' request to URL : " + url);
                MagicCosmetics.getInstance().getLogger().info("Response Code : " + responseCode);
            }

            boolean result;

            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                JsonObject json = new JsonParser().parse(response.toString()).getAsJsonObject();

                result = json.get("response").getAsJsonObject().get("success").getAsBoolean();
            }

            return result;
        }
        if(m.equals("true")){
            MagicCosmetics.getInstance().setUser(new User(user_id, user_name, inject_version, resource_id, download_token, nonce, download_agent, download_time));
            return true;
        }
        if(s.equals("true")){
            MagicCosmetics.getInstance().setUser(new User(user_id, user_name, inject_version, plugin_id, download_token, nonce, download_agent, download_time));
            return true;
        }
        return false;
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
