package com.francobm.customcosmetics.cache.nms;

import com.francobm.customcosmetics.cache.nms.v1_17_R1.PlayerBalloonHandler;
import com.francobm.customcosmetics.utils.Utils;
import com.mojang.datafixers.util.Pair;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.ambient.EntityBat;
import net.minecraft.world.entity.animal.EntityPufferFish;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public abstract class PlayerBalloon {
    public static Map<UUID, PlayerBalloon> playerBags = new HashMap<>();
    protected EntityArmorStand armorStand;
    protected EntityPufferFish leashed;
    protected UUID uuid;
    protected List<UUID> players;

    public static void refreshPlayerBalloon(Player player){
        removePlayerBalloonByPlayer(player);
        addPlayerBalloonByPlayer(player);
    }

    public static void removePlayerBalloonByPlayer(Player player){
        for(PlayerBalloon playerBalloon : playerBags.values()){
            if(player.getUniqueId().equals(playerBalloon.uuid)) continue;
            if(!playerBalloon.players.contains(player.getUniqueId())) continue;
            playerBalloon.players.remove(player.getUniqueId());
        }
    }

    public static void addPlayerBalloonByPlayer(Player player){
        for(PlayerBalloon playerBalloon : playerBags.values()) {
            if(player.getUniqueId().equals(playerBalloon.uuid)) continue;
            playerBalloon.spawnBalloon(player);
        }
    }

    public static PlayerBalloon createBalloon(Player player, Location location){
        switch (Utils.getVersion()){
            case "v1_17_R1":
                return new PlayerBalloonHandler(player, location);
            case "v1_18_R1":
                return new com.francobm.customcosmetics.cache.nms.PlayerBalloonHandler(player, location);
        }
        return null;
    }

    public abstract void spawnBalloon(Player player);

    public void spawnBalloon(boolean all){
        if(all) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                spawnBalloon(player);
            }
            return;
        }

        spawnBalloon(getPlayer());
    }

    public abstract void remove(boolean all);

    public abstract void setItemOnHelmet(org.bukkit.inventory.ItemStack itemStack, boolean all);

    public abstract void lookEntity(float yaw, float pitch, boolean all);

    public abstract void setLeash(Player player);

    public abstract void teleport(Location location, boolean all);

    public Player getPlayer(){
        return Bukkit.getPlayer(uuid);
    }

    public EntityArmorStand getArmorStand() {
        return armorStand;
    }

    public UUID getUuid() {
        return uuid;
    }
}
