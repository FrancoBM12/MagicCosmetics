package com.francobm.magicosmetics.api;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.cache.*;
import com.francobm.magicosmetics.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MagicAPI {

    public static boolean hasCosmetic(Player player, String cosmeticId){
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        PlayerData playerData = PlayerData.getPlayer(player);
        if(plugin.isPermissions()){
            return Cosmetic.getCosmetic(cosmeticId).hasPermission(player);
        }
        Cosmetic cosmetic = playerData.getCosmeticById(cosmeticId);
        return cosmetic != null;
    }

    public static boolean spray(Player player) {
        PlayerData playerData = PlayerData.getPlayer(player);
        if(playerData.getSpray() == null) return false;
        playerData.draw(SprayKeys.API);
        return true;
    }

    public static Set<Cosmetic> getCosmetics() {
        return new HashSet<>(Cosmetic.cosmetics.values());
    }

    public static Set<Cosmetic> getCosmeticsByType(CosmeticType cosmeticType) {
        return Cosmetic.getCosmeticsByType(cosmeticType);
    }

    public static Set<Cosmetic> getCosmeticsHideByType(CosmeticType cosmeticType){
        return Cosmetic.getSetCosmeticsHideByType(cosmeticType);
    }

    public static boolean tintItem(ItemStack item, String colorHex){
        return MagicCosmetics.getInstance().getCosmeticsManager().tintItem(item, colorHex);
    }

    public static boolean hasEquipCosmetic(Entity entity, String cosmeticId){
        EntityCache entityCache = EntityCache.getEntity(entity.getUniqueId());
        if(entityCache == null) return false;
        return entityCache.hasEquipped(cosmeticId);
    }

    public static boolean hasEquipCosmetic(Player player, CosmeticType cosmeticType){
        PlayerData playerData = PlayerData.getPlayer(player);
        Cosmetic cosmetic = playerData.getEquip(cosmeticType);
        return cosmetic != null;
    }

    public static void EquipCosmetic(Player player, String cosmeticId, String color, boolean force){
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        plugin.getCosmeticsManager().equipCosmetic(player, cosmeticId, color, force);
    }

    public static void EquipCosmetic(Entity entity, String cosmeticId, String colorHex){
        EntityCache entityCache = EntityCache.getEntityOrCreate(entity);
        Cosmetic cosmetic = Cosmetic.getCloneCosmetic(cosmeticId);
        if(cosmetic == null) return;
        if(colorHex != null){
            org.bukkit.Color color = Utils.hex2Rgb(colorHex);
            cosmetic.setColor(color);
        }
        entityCache.setCosmetic(cosmetic);
    }

    public static void EquipCosmetic(Entity entity, String cosmeticId, org.bukkit.Color color){
        EntityCache entityCache = EntityCache.getEntityOrCreate(entity);
        Cosmetic cosmetic = Cosmetic.getCloneCosmetic(cosmeticId);
        if(cosmetic == null) return;
        if(color != null){
            cosmetic.setColor(color);
        }
        entityCache.setCosmetic(cosmetic);
    }

    public static void UnEquipCosmetic(Player player, CosmeticType cosmeticType){
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        plugin.getCosmeticsManager().unSetCosmetic(player, cosmeticType);
    }

    public static void UnEquipCosmetic(Entity entity, CosmeticType cosmeticType){
        EntityCache entityCache = EntityCache.getEntity(entity.getUniqueId());
        if(entityCache == null) return;
        entityCache.unSetCosmetic(cosmeticType);
    }

    public static void RemoveEntityCosmetics(UUID entityUniqueId) {
        if(!EntityCache.entities.containsKey(entityUniqueId)) return;
        EntityCache entityCache = EntityCache.getEntity(entityUniqueId);
        if(entityCache == null) return;
        entityCache.clearCosmeticsInUse();
        EntityCache.removeEntity(entityUniqueId);
    }

    public static ItemStack getCosmeticItem(String id){
        Cosmetic cosmetic = Cosmetic.getCosmetic(id);
        if(cosmetic == null) return null;
        return cosmetic.getItemColor();
    }

    public static String getCosmeticId(String name, String type){
        Player player = Bukkit.getPlayerExact(name);
        if(player == null) return null;
        PlayerData playerData = PlayerData.getPlayer(player);
        try{
            CosmeticType cosmeticType = CosmeticType.valueOf(type.toUpperCase());
            Cosmetic cosmetic = playerData.getEquip(cosmeticType);
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
        PlayerData playerData = PlayerData.getPlayer(player);
        try{
            CosmeticType cosmeticType = CosmeticType.valueOf(type.toUpperCase());
            Cosmetic cosmetic = playerData.getEquip(cosmeticType);
            if(cosmetic == null) return null;
            return cosmetic.getItemColor().clone();
        }catch (IllegalArgumentException ignored){
            MagicCosmetics.getInstance().getLogger().warning("Invalid cosmetic type: " + type);
        }
        return null;
    }

    public static ItemStack getEquipped(OfflinePlayer offlinePlayer, CosmeticType cosmeticType){
        if(!offlinePlayer.hasPlayedBefore()) return null;
        PlayerData playerData = PlayerData.getPlayer(offlinePlayer);
        if(playerData == null) {
            return null;
        }
        Cosmetic cosmetic = playerData.getEquip(cosmeticType);
        if(cosmetic == null) {
            return null;
        }
        return cosmetic.getItemColor().clone();
    }

    public static int getPlayerCosmeticsAvailable(Player player, CosmeticType cosmeticType){
        PlayerData playerData = PlayerData.getPlayer(player);
        return playerData.getCosmeticCount(cosmeticType);
    }

    public static int getPlayerAllCosmeticsAvailable(Player player){
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        PlayerData playerData = PlayerData.getPlayer(player);
        if(plugin.isPermissions()){
            return playerData.getCosmeticsPerm().size();
        }
        return playerData.getCosmetics().size();
    }

    public static int getServerCosmeticsAvailable(CosmeticType cosmeticType){
        return Cosmetic.getCosmeticCount(cosmeticType);
    }

    public static int getServerAllCosmeticsAvailable(){
        return Cosmetic.cosmetics.size();
    }
}