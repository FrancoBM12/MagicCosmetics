package com.francobm.magicosmetics.provider;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.api.Cosmetic;
import com.francobm.magicosmetics.api.CosmeticType;
import com.francobm.magicosmetics.cache.PlayerData;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class PlaceholderAPI extends PlaceholderExpansion {
    private final MagicCosmetics plugin = MagicCosmetics.getInstance();

    public PlaceholderAPI(){
        register();
    }

    public List<String> setPlaceholders(Player player, List<String> message){
        return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, message);
    }

    public String setPlaceholders(Player player, String message){
        return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, message);
    }

    @Override
    public boolean persist() {
        return true;
    }

    /**
     * This method should always return true unless we
     * have a dependency we need to make sure is on the server
     * for our placeholders to work!
     *
     * @return always true since we do not have any dependencies.
     */
    @Override
    public boolean canRegister(){
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     *
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor(){
        return "FrancoBM";
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>The identifier has to be lowercase and can't contain _ or %
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier(){
        return "magicosmetics";
    }

    /**
     * This is the version of this expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }

    /**
     * This is the method called when a placeholder with our identifier
     * is found and needs a value.
     * <br>We specify the value identifier in this method.
     * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
     *
     * @param  player
     *         A {@link org.bukkit.OfflinePlayer OfflinePlayer}.
     * @param  identifier
     *         A String containing the identifier/value.
     *
     * @return Possibly-null String of the requested identifier.
     */
    @Override
    public String onRequest(OfflinePlayer player, String identifier){

        if(player == null || !player.isOnline() || player.getPlayer() == null){
            return null;
        }
        // %example_placeholder1%
        // %magicosmetics_equipped_count%
        PlayerData playerData = PlayerData.getPlayer(player.getPlayer());
        // %magicosmetics_get_<id>%

        if(identifier.equals("get_zone")){
            if(playerData.getZone() == null){
                return "";
            }
            return playerData.getZone().getId();
        }

        if(identifier.startsWith("get_")){
            String id = identifier.split("_")[1];
            if(id == null || id.isEmpty()){
                return null;
            }
            id = id.replace("%", "");
            if(plugin.isPermissions()){
                return String.valueOf(Cosmetic.getCosmetic(id).hasPermission(player.getPlayer()));
            }
            return String.valueOf(playerData.getCosmeticById(id) != null);
        }

        if(identifier.equals("equipped_count")){
            return String.valueOf(playerData.getEquippedCount());
        }

        if(identifier.startsWith("equipped_")){
            String id = identifier.split("_")[1];
            if(id == null || id.isEmpty()){
                return null;
            }
            try{
                CosmeticType cosmeticType = CosmeticType.valueOf(id.toUpperCase());
                Cosmetic cosmetic = playerData.getEquip(cosmeticType);
                // equipped_TYPE_material/model/hex/r/g/b/id
                if(identifier.split("_").length > 2) {
                    if(cosmetic == null) return null;
                    String subId = identifier.split("_")[2];
                    if(subId == null || subId.isEmpty()){
                        return null;
                    }
                    Color color = cosmetic.getColor();
                    switch (subId.toLowerCase()) {
                        case "id":
                            return cosmetic.getId();
                        case "material":
                            return cosmetic.getItemStack().getType().name();
                        case "modeldata":
                            return String.valueOf(cosmetic.getItemStack().getItemMeta().getCustomModelData());
                        case "hex":
                            if(color == null) return null;
                            return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
                        case "r":
                            if(color == null) return null;
                            return String.valueOf(color.getRed());
                        case "g":
                            if(color == null) return null;
                            return String.valueOf(color.getGreen());
                        case "b":
                            if(color == null) return null;
                            return String.valueOf(color.getBlue());
                    }
                    return null;
                }
                return String.valueOf(cosmetic != null);
            }catch (IllegalArgumentException ignored){
            }
            return String.valueOf(playerData.getEquip(id) != null);
        }

        if(identifier.startsWith("using_")){
            String id = identifier.split("_")[1];
            if(id == null || id.isEmpty()){
                return null;
            }
            id = id.replace("%", "");
            try{
                CosmeticType cosmeticType = CosmeticType.valueOf(id.toUpperCase());
                return String.valueOf(playerData.getEquip(cosmeticType) != null);
            }catch (IllegalArgumentException ignored){
            }
            return String.valueOf(playerData.getEquip(id) != null);
        }
        
        if(identifier.startsWith("player_available_")){
            String id = identifier.split("_")[2];
            if(id == null || id.isEmpty()){
                return null;
            }
            id = id.replace("%", "");
            if(id.equalsIgnoreCase("all")){
                if(plugin.isPermissions()){
                    return String.valueOf(playerData.getCosmeticsPerm().size());
                }
                return String.valueOf(playerData.getCosmetics().size());
            }
            try{
                CosmeticType cosmeticType = CosmeticType.valueOf(id.toUpperCase());
                return String.valueOf(playerData.getCosmeticCount(cosmeticType));
            }catch (IllegalArgumentException ignored){
            }
            return null;
        }

        if(identifier.startsWith("available_")){
            String id = identifier.split("_")[1];
            if(id == null || id.isEmpty()){
                return null;
            }
            id = id.replace("%", "");
            if(id.equalsIgnoreCase("all")){
                return String.valueOf(Cosmetic.cosmetics.size());
            }
            try{
                CosmeticType cosmeticType = CosmeticType.valueOf(id.toUpperCase());
                return String.valueOf(Cosmetic.getCosmeticCount(cosmeticType));
            }catch (IllegalArgumentException ignored){
            }
            return null;
        }

        if(identifier.equals("in_zone")){
            return String.valueOf(playerData.isZone());
        }

        // We return null if an invalid placeholder (f.e. %example_placeholder3%)
        // was provided
        return null;
    }
}
