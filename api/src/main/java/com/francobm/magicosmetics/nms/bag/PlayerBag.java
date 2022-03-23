package com.francobm.magicosmetics.nms.bag;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public abstract class PlayerBag {
    public static Map<UUID, PlayerBag> playerBags = new HashMap<>();
    protected UUID uuid;
    protected List<UUID> players;


    public static void updatePlayerBag(Player player){
        for(PlayerBag playerBag : playerBags.values()){
            playerBag.remove(player);
            playerBag.spawnBag(player);
        }
    }

    public static void updatePlayerBagWithoutMe(Player player){
        for(PlayerBag playerBag : playerBags.values()){
            if(player.getUniqueId().equals(playerBag.uuid)) continue;
            playerBag.remove(player);
            playerBag.spawnBag(player);
        }
    }

    /*
    public static void refreshPlayerBag(Player player){
        updatePlayerBag(player);
        //removePlayerBagByPlayer(player);
        //addPlayerBagByPlayer(player);
    }
     */

    public static void removePlayerBagByPlayer(Player player){
        for(PlayerBag playerBag : playerBags.values()){
            if(player.getUniqueId().equals(playerBag.uuid)) continue;
            if(!playerBag.players.contains(player.getUniqueId())) continue;
            playerBag.remove(player);
        }
    }

    public static void addPlayerBagByPlayer(Player player){
        for(PlayerBag playerBag : playerBags.values()) {
            if(player.getUniqueId().equals(playerBag.uuid)) continue;
            playerBag.spawnBag(player);
        }
    }

    public abstract void spawnBag(Player player);

    public abstract void spawnBag(boolean marker, boolean all);

    public abstract void remove(boolean all);

    public abstract void remove(Player player);

    public abstract void addPassenger(Player player, Entity entity, Entity passenger);

    public abstract void addPassenger(boolean all);

    public abstract void addPassenger(Player player);

    public abstract void setItemOnHelmet(org.bukkit.inventory.ItemStack itemStack, boolean all);

    public abstract void lookEntity(float yaw, float pitch, boolean all);


    public Player getPlayer(){
        return Bukkit.getPlayer(uuid);
    }

    public UUID getUuid() {
        return uuid;
    }

    public List<UUID> getPlayers() {
        return players;
    }
}
