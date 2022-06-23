package com.francobm.magicosmetics.nms.bag;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PlayerBag {
    public static Map<UUID, PlayerBag> playerBags = new ConcurrentHashMap<>();
    protected UUID uuid;
    protected List<UUID> players;


    public static void updatePlayerBag(Player player){
        for(PlayerBag playerBag : playerBags.values()){
            playerBag.remove(player);
            playerBag.spawn(player);
        }
    }

    /*
    public static void refreshPlayerBag(Player player){
        updatePlayerBag(player);
        //removePlayerBagByPlayer(player);
        //addPlayerBagByPlayer(player);
    }
     */

    public abstract void spawn(Player player);

    public abstract void spawn(boolean exception);

    public abstract void remove();

    public abstract void remove(Player player);

    public abstract void addPassenger(Player player, Entity entity, Entity passenger);

    public abstract void addPassenger(boolean exception);

    public abstract void addPassenger(Player player);

    public abstract void setItemOnHelmet(org.bukkit.inventory.ItemStack itemStack, boolean all);

    public abstract void lookEntity(float yaw, float pitch, boolean all);

    public abstract Entity getEntity();

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
