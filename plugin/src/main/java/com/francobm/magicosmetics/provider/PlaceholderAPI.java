package com.francobm.magicosmetics.provider;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.api.MagicAPI;
import com.francobm.magicosmetics.cache.Cosmetic;
import com.francobm.magicosmetics.cache.CosmeticType;
import com.francobm.magicosmetics.cache.PlayerCache;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPI extends PlaceholderExpansion {
    private final MagicCosmetics plugin = MagicCosmetics.getInstance();

    public String setPlaceholders(Player player, String message){
        return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, message);
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
    public String onRequest(OfflinePlayer player, @NotNull String identifier){

        if(player == null || !player.isOnline() || player.getPlayer() == null){
            return null;
        }
        // %example_placeholder1%
        // %magicosmetics_equipped_count%
        PlayerCache playerCache = PlayerCache.getPlayer(player.getPlayer());
        // %magicosmetics_get_<id>%

        if(identifier.equals("get_zone")){
            if(playerCache.getZone() == null){
                return "";
            }
            return playerCache.getZone().getId();
        }

        if(identifier.startsWith("get_")){
            String id = identifier.split("_")[1];
            if(id == null || id.isEmpty()){
                return null;
            }
            id = id.replace("%", "");
            return String.valueOf(playerCache.getCosmeticById(id) != null);
        }

        if(identifier.startsWith("using_")){
            String id = identifier.split("_")[1];
            if(id == null || id.isEmpty()){
                return null;
            }
            id = id.replace("%", "");
            try{
                CosmeticType cosmeticType = CosmeticType.valueOf(id.toUpperCase());
                return String.valueOf(playerCache.getEquip(cosmeticType) != null);
            }catch (IllegalArgumentException ignored){
            }
            return String.valueOf(playerCache.getEquip(id) != null);
        }
        
        if(identifier.startsWith("player_available_")){
            String id = identifier.split("_")[2];
            if(id == null || id.isEmpty()){
                return null;
            }
            id = id.replace("%", "");
            if(id.equalsIgnoreCase("all")){
                return String.valueOf(playerCache.getCosmetics().size());
            }
            try{
                CosmeticType cosmeticType = CosmeticType.valueOf(id.toUpperCase());
                return String.valueOf(playerCache.getCosmeticCount(cosmeticType));
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

        if(identifier.equals("equipped_count")){
            return String.valueOf(playerCache.getEquippedCount());
        }

        if(identifier.equals("in_zone")){
            return String.valueOf(playerCache.isZone());
        }

        // We return null if an invalid placeholder (f.e. %example_placeholder3%)
        // was provided
        return null;
    }
}
