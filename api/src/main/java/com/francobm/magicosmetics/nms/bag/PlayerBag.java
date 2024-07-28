package com.francobm.magicosmetics.nms.bag;

import com.francobm.magicosmetics.cache.PlayerData;
import com.francobm.magicosmetics.nms.IRangeManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PlayerBag {
    protected ItemStack backPackItem;
    protected ItemStack backPackItemForMe;
    protected int lendEntityId = -1;
    protected UUID uuid;
    protected IRangeManager rangeManager;
    protected float height;
    protected List<Integer> ids;
    protected List<UUID> hideViewers;

    public abstract void spawn(Player player);

    public abstract void spawnSelf(Player player);

    public abstract void spawn(boolean exception);

    public abstract void remove();

    public abstract void remove(Player player);

    public abstract void addPassenger(Player player, int entity, int passenger);

    public abstract void addPassenger(boolean exception);

    public abstract void setItemOnHelmet(Player player, ItemStack itemStack);

    public abstract void lookEntity(float yaw, float pitch, boolean all);

    public abstract Entity getEntity();

    public void setLendEntityId(int id){
        lendEntityId = id;
    }

    public Player getPlayer(){
        return Bukkit.getPlayer(uuid);
    }

    public UUID getUuid() {
        return uuid;
    }

    public List<UUID> getHideViewers() {
        return hideViewers;
    }

    public void addHideViewer(Player player) {
        if(hideViewers.contains(player.getUniqueId())) return;
        hideViewers.add(player.getUniqueId());
        remove(player);
    }

    public void removeHideViewer(Player player) {
        hideViewers.remove(player.getUniqueId());
    }

    protected Set<Player> getPlayersInRange() {
        Set<Player> set = rangeManager.getPlayerInRange();
        set.add(getPlayer());
        return set;
    }
}
