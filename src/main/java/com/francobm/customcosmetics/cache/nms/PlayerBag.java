package com.francobm.customcosmetics.cache.nms;

import com.francobm.customcosmetics.cache.nms.v1_17_R1.PlayerBagHandler;
import com.francobm.customcosmetics.utils.Utils;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public abstract class PlayerBag {
    public static Map<UUID, PlayerBag> playerBags = new HashMap<>();
    protected EntityArmorStand armorStand;
    protected UUID uuid;
    protected List<UUID> players;

    public static void refreshPlayerBag(Player player){
        removePlayerBagByPlayer(player);
        addPlayerBagByPlayer(player);
    }

    public static void removePlayerBagByPlayer(Player player){
        for(PlayerBag playerBag : playerBags.values()){
            if(player.getUniqueId().equals(playerBag.uuid)) continue;
            if(!playerBag.players.contains(player.getUniqueId())) continue;
            playerBag.players.remove(player.getUniqueId());
        }
    }

    public static void addPlayerBagByPlayer(Player player){
        for(PlayerBag playerBag : playerBags.values()) {
            if(player.getUniqueId().equals(playerBag.uuid)) continue;
            playerBag.spawnBag(player);
        }
    }

    public static PlayerBag createBag(Player player){
        switch (Utils.getVersion()){
            case "v1_17_R1":
                return new PlayerBagHandler(player);
            case "v1_18_R1":
                return new com.francobm.customcosmetics.cache.nms.PlayerBagHandler(player);
        }
        return null;
    }

    public abstract void spawnBag(Player player);

    public abstract void spawnBag(boolean marker, boolean all);

    public abstract void remove(boolean all);

    public abstract void addPassenger(Player player, Entity entity, net.minecraft.world.entity.Entity passenger);

    public abstract void setItemOnHelmet(org.bukkit.inventory.ItemStack itemStack, boolean all);

    public abstract void lookEntity(float yaw, float pitch, boolean all);


    public Player getPlayer(){
        return Bukkit.getPlayer(uuid);
    }

    public EntityArmorStand getArmorStand() {
        return armorStand;
    }

    protected <T> T createDataSerializer(UnsafeFunction<PacketDataSerializer, T> callback) {
        PacketDataSerializer data = new PacketDataSerializer(Unpooled.buffer());
        T result = null;
        try {
            result = callback.apply(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            data.release();
        }
        return result;
    }

    @FunctionalInterface
    protected interface UnsafeFunction<K, T> {
        T apply(K k) throws Exception;
    }

    public UUID getUuid() {
        return uuid;
    }
}
