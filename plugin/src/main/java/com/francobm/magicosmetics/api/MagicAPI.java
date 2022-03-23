package com.francobm.magicosmetics.api;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.cache.Cosmetic;
import com.francobm.magicosmetics.cache.CosmeticType;
import com.francobm.magicosmetics.cache.PlayerCache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MagicAPI {

    public static boolean hasCosmetic(Player player, String cosmeticId){
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        Cosmetic cosmetic = playerCache.getCosmeticById(cosmeticId);
        return cosmetic != null;
    }

    public static boolean hasEquipCosmetic(Player player, CosmeticType cosmeticType){
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        Cosmetic cosmetic = playerCache.getEquip(cosmeticType);
        return cosmetic != null;
    }

    public static void EquipCosmetic(Player player, String cosmeticId){
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        plugin.getCosmeticsManager().useCosmetic(player, cosmeticId);
    }

    public static void UnEquipCosmetic(Player player, CosmeticType cosmeticType){
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        plugin.getCosmeticsManager().unSetCosmetic(player, cosmeticType);
    }

    public static ItemStack getCosmeticItem(String id){
        Cosmetic cosmetic = Cosmetic.getCosmetic(id);
        if(cosmetic == null) return null;
        return cosmetic.getItemColor();
    }

    public static ItemStack getEquipped(String name, String type){
        Player player = Bukkit.getPlayerExact(name);
        if(player == null) return null;
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        try{
            CosmeticType cosmeticType = CosmeticType.valueOf(type.toUpperCase());
            Cosmetic cosmetic = playerCache.getEquip(cosmeticType);
            if(cosmetic == null) return null;
            return cosmetic.getItemColor();
        }catch (IllegalArgumentException ignored){

        }
        return null;
    }

    public static int getPlayerCosmeticsAvailable(Player player, CosmeticType cosmeticType){
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        return playerCache.getCosmeticCount(cosmeticType);
    }

    public static int getPlayerAllCosmeticsAvailable(Player player){
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        return playerCache.getCosmetics().size();
    }

    public static int getServerCosmeticsAvailable(CosmeticType cosmeticType){
        return Cosmetic.getCosmeticCount(cosmeticType);
    }

    public static int getServerAllCosmeticsAvailable(){
        return Cosmetic.cosmetics.size();
    }
}