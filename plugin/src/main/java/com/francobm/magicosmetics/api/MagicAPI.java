package com.francobm.magicosmetics.api;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.cache.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MagicAPI {

    public static boolean hasCosmetic(Player player, String cosmeticId){
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        if(plugin.isPermissions()){
            return Cosmetic.getCosmetic(cosmeticId).hasPermission(player);
        }
        Cosmetic cosmetic = playerCache.getCosmeticById(cosmeticId);
        return cosmetic != null;
    }

    public static boolean spray(Player player) {
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        if(playerCache.getSpray() == null) return false;
        playerCache.draw(SprayKeys.API);
        return true;
    }

    public static boolean tintItem(ItemStack item, String colorHex){
        return MagicCosmetics.getInstance().getCosmeticsManager().tintItem(item, colorHex);
    }

    public static boolean hasEquipCosmetic(Entity entity, String cosmeticId){
        EntityCache entityCache = EntityCache.getOrCreateEntity(entity.getUniqueId());
        return entityCache.hasEquipped(cosmeticId);
    }

    public static boolean hasEquipCosmetic(Player player, CosmeticType cosmeticType){
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        Cosmetic cosmetic = playerCache.getEquip(cosmeticType);
        return cosmetic != null;
    }

    public static void EquipCosmetic(Player player, String cosmeticId, String color, boolean force){
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        plugin.getCosmeticsManager().equipCosmetic(player, cosmeticId, color, force);
    }

    public static void EquipCosmetic(Entity entity, String cosmeticId, String colorHex){
        EntityCache entityCache = EntityCache.getOrCreateEntity(entity.getUniqueId());
        Cosmetic cosmetic = Cosmetic.getCloneCosmetic(cosmeticId);
        if(cosmetic == null) return;
        if(colorHex != null){
            org.bukkit.Color color = Color.hex2Rgb(colorHex);
            cosmetic.setColor(color);
        }
        entityCache.setCosmetic(cosmetic);
    }

    public static void UnEquipCosmetic(Player player, CosmeticType cosmeticType){
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        plugin.getCosmeticsManager().unSetCosmetic(player, cosmeticType);
    }

    public static void UnEquipCosmetic(Entity entity, CosmeticType cosmeticType){
        EntityCache entityCache = EntityCache.getOrCreateEntity(entity.getUniqueId());
        entityCache.unSetCosmetic(cosmeticType);
    }

    public static ItemStack getCosmeticItem(String id){
        Cosmetic cosmetic = Cosmetic.getCosmetic(id);
        if(cosmetic == null) return null;
        return cosmetic.getItemColor();
    }

    public static String getCosmeticId(String name, String type){
        Player player = Bukkit.getPlayerExact(name);
        if(player == null) return null;
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        try{
            CosmeticType cosmeticType = CosmeticType.valueOf(type.toUpperCase());
            Cosmetic cosmetic = playerCache.getEquip(cosmeticType);
            if(cosmetic == null) return null;
            return cosmetic.getId();
        }catch (IllegalArgumentException ignored){
            MagicCosmetics.getInstance().getLogger().warning("Invalid cosmetic type: " + type);
        }
        return null;
    }

    public static ItemStack getEquipped(String name, String type){
        Player player = Bukkit.getPlayerExact(name);
        if(player == null) return null;
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        try{
            CosmeticType cosmeticType = CosmeticType.valueOf(type.toUpperCase());
            Cosmetic cosmetic = playerCache.getEquip(cosmeticType);
            if(cosmetic == null) return null;
            return cosmetic.getItemColor().clone();
        }catch (IllegalArgumentException ignored){
            MagicCosmetics.getInstance().getLogger().warning("Invalid cosmetic type: " + type);
        }
        return null;
    }

    public static ItemStack getEquipped(OfflinePlayer offlinePlayer, CosmeticType cosmeticType){
        if(!offlinePlayer.hasPlayedBefore()) return null;
        PlayerCache playerCache = PlayerCache.getPlayer(offlinePlayer);
        if(playerCache == null) {
            return null;
        }
        Cosmetic cosmetic = playerCache.getEquip(cosmeticType);
        if(cosmetic == null) {
            return null;
        }
        return cosmetic.getItemColor().clone();
    }

    public static int getPlayerCosmeticsAvailable(Player player, CosmeticType cosmeticType){
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        return playerCache.getCosmeticCount(cosmeticType);
    }

    public static int getPlayerAllCosmeticsAvailable(Player player){
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        if(plugin.isPermissions()){
            return playerCache.getCosmeticsPerm().size();
        }
        return playerCache.getCosmetics().size();
    }

    public static int getServerCosmeticsAvailable(CosmeticType cosmeticType){
        return Cosmetic.getCosmeticCount(cosmeticType);
    }

    public static int getServerAllCosmeticsAvailable(){
        return Cosmetic.cosmetics.size();
    }
}