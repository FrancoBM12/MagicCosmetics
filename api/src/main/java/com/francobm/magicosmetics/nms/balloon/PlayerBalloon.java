package com.francobm.magicosmetics.nms.balloon;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class PlayerBalloon {
    public static Map<UUID, PlayerBalloon> playerBalloons = new HashMap<>();
    protected UUID uuid;
    protected List<UUID> players;
    protected boolean floatLoop = true;
    protected double y = 0;
    protected double height = 0;
    protected boolean heightLoop = true;
    protected double rotate = -0.008;
    protected double rot = 0;
    protected boolean rotateLoop = true;
    protected double space;


    public static void updatePlayerBalloon(Player player){
        for(PlayerBalloon playerBag : playerBalloons.values()){
            playerBag.remove(player);
            playerBag.spawnBag(player);
        }
    }

    public static void updatePlayerBalloonWithoutMe(Player player){
        for(PlayerBalloon playerBag : playerBalloons.values()){
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

    public static void removePlayerBalloonByPlayer(Player player){
        for(PlayerBalloon playerBag : playerBalloons.values()){
            if(player.getUniqueId().equals(playerBag.uuid)) continue;
            if(!playerBag.players.contains(player.getUniqueId())) continue;
            playerBag.remove(player);
        }
    }

    public static void addPlayerBalloonByPlayer(Player player){
        for(PlayerBalloon playerBag : playerBalloons.values()) {
            if(player.getUniqueId().equals(playerBag.uuid)) continue;
            playerBag.spawnBag(player);
        }
    }

    public abstract void spawnBag(Player player);

    public abstract void spawnBag(boolean marker, boolean all);

    public abstract void remove(boolean all);

    public abstract void remove(Player player);

    public abstract void setItemOnHelmet(org.bukkit.inventory.ItemStack itemStack, boolean all);

    public abstract void lookEntity(float yaw, float pitch, boolean all);

    public abstract void update(boolean all);

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
