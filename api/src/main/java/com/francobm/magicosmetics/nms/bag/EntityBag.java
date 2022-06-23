package com.francobm.magicosmetics.nms.bag;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class EntityBag {
    public static Map<UUID, EntityBag> entityBags = new ConcurrentHashMap<>();
    protected UUID uuid;
    protected Entity entity;
    protected List<UUID> players;


    public static void updateEntityBag(Player player){
        for(EntityBag entityBag : entityBags.values()){
            entityBag.remove(player);
            entityBag.spawnBag(player);
        }
    }

    /*
    public static void refreshPlayerBag(Player player){
        updatePlayerBag(player);
        //removePlayerBagByPlayer(player);
        //addPlayerBagByPlayer(player);
    }
     */

    public static void removeEntityBag(Player player){
        for(EntityBag entityBag : entityBags.values()){
            if(!entityBag.players.contains(player.getUniqueId())) continue;
            entityBag.remove(player);
        }
    }

    public abstract void spawnBag(Player player);

    public abstract void spawnBag();

    public abstract void remove();

    public abstract void remove(Player player);

    public abstract void addPassenger();

    public abstract void addPassenger(Entity entity, Entity passenger);

    public abstract void addPassenger(Player player, Entity entity, Entity passenger);

    public abstract void setItemOnHelmet(org.bukkit.inventory.ItemStack itemStack);

    public abstract void lookEntity();

    public UUID getUuid() {
        return uuid;
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public Entity getEntity() {
        return entity;
    }
}
