package com.francobm.magicosmetics.nms.balloon;

import com.francobm.magicosmetics.cache.RotationType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class EntityBalloon {
    public static Map<UUID, EntityBalloon> entitiesBalloon = new ConcurrentHashMap<>();
    protected UUID uuid;
    protected LivingEntity entity;
    protected List<UUID> players;
    protected boolean floatLoop = true;
    protected double y = 0;
    protected double height = 0;
    protected boolean heightLoop = true;
    protected float rotate = -0.4f;
    protected double rot = 0;
    protected boolean rotateLoop = true;
    protected double space;
    protected boolean bigHead;
    protected boolean invisibleLeash;


    public static void updateEntityBalloon(Player player){
        for(EntityBalloon entityBalloon : entitiesBalloon.values()){
            entityBalloon.remove(player);
            entityBalloon.spawn(player);
        }
    }

    public static void removeEntityBalloon(Player player){
        for(EntityBalloon entityBalloon : entitiesBalloon.values()){
            if(!entityBalloon.players.contains(player.getUniqueId())) continue;
            entityBalloon.remove(player);
        }
    }

    public abstract void spawn(Player player);

    public abstract void spawn(boolean exception);

    public abstract void remove();

    public abstract void remove(Player player);

    public abstract void setItem(org.bukkit.inventory.ItemStack itemStack);

    public abstract void lookEntity();

    public abstract void update();

    public abstract void update(Player player);

    public abstract void rotate(boolean rotation, RotationType rotationType, float rotate);

    public LivingEntity getEntity() {
        return entity;
    }

    public UUID getUuid() {
        return uuid;
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public boolean isBigHead() {
        return bigHead;
    }
}
