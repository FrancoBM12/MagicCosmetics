package com.francobm.customcosmetics.cache.nms;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.animal.EntityPufferFish;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerBalloonHandler extends PlayerBalloon {

    public PlayerBalloonHandler(Player p, Location location){
        players = new ArrayList<>();
        this.uuid = p.getUniqueId();
        playerBags.put(uuid, this);
        Player player = getPlayer();
        WorldServer world = ((CraftWorld) player.getWorld()).getHandle();

        armorStand = new EntityArmorStand(EntityTypes.c, world);
        armorStand.b(location.getX(), location.getY()-1.2, location.getZ(), location.getYaw(), location.getPitch());
        armorStand.j(true); //Invisible
        armorStand.m(true); //Invulnerable
        leashed = new EntityPufferFish(EntityTypes.at, world);
        leashed.b(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        leashed.b(((CraftPlayer)player).getHandle(), true);
        leashed.m(true);
        leashed.j(true);
        leashed.d(true); //silent true

        DataWatcher watcher = armorStand.ai();
        watcher.b(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte)0x20);
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(armorStand.ae(), watcher, true);
        ((CraftPlayer)player).getHandle().b.a(packet);
        watcher = leashed.ai();
        watcher.b(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte)0x20);
        packet = new PacketPlayOutEntityMetadata(leashed.ae(), watcher, true);
        ((CraftPlayer)player).getHandle().b.a(packet);
    }

    @Override
    public void spawnBalloon(Player player) {
        if(players.contains(player.getUniqueId())) return;
        armorStand.m(true); //invulnerable true
        armorStand.j(true); //Invisible true

        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        connection.a(new PacketPlayOutSpawnEntityLiving(armorStand));
        //client settings
        DataWatcher watcher = armorStand.ai();
        watcher.b(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte)0x20);
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(armorStand.ae(), watcher, true);
        connection.a(packet);

        leashed.b(((CraftPlayer)getPlayer()).getHandle(), true);
        leashed.m(true);
        leashed.j(true);
        leashed.d(true); //silent true

        connection.a(new PacketPlayOutSpawnEntityLiving(leashed));
        //
        watcher = leashed.ai();
        watcher.b(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte)0x20);
        packet = new PacketPlayOutEntityMetadata(leashed.ae(), watcher, true);
        connection.a(packet);
        setLeash(player);
        players.add(player.getUniqueId());
    }

    @Override
    public void remove(boolean all){
        if(all){
            for(Player player : Bukkit.getOnlinePlayers()){
                if(!players.contains(player.getUniqueId())) continue;
                PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
                connection.a(new PacketPlayOutEntityDestroy(armorStand.ae()));
                connection.a(new PacketPlayOutEntityDestroy(leashed.ae()));
                players.remove(player.getUniqueId());
            }
            playerBags.remove(uuid);
            return;
        }
        PlayerConnection connection = ((CraftPlayer)getPlayer()).getHandle().b;
        connection.a(new PacketPlayOutEntityDestroy(armorStand.ae()));
        connection.a(new PacketPlayOutEntityDestroy(leashed.ae()));
        playerBags.remove(uuid);
    }

    @Override
    public void setItemOnHelmet(org.bukkit.inventory.ItemStack itemStack, boolean all){
        if(all) {
            for (UUID uuid : players) {
                Player player = Bukkit.getPlayer(uuid);
                if(player == null) continue;
                PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
                ArrayList<Pair<EnumItemSlot, ItemStack>> list = new ArrayList<>();
                list.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(itemStack)));
                connection.a(new PacketPlayOutEntityEquipment(armorStand.ae(), list));
            }
            return;
        }
        PlayerConnection connection = ((CraftPlayer)getPlayer()).getHandle().b;
        ArrayList<Pair<EnumItemSlot, ItemStack>> list = new ArrayList<>();
        list.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(itemStack)));
        connection.a(new PacketPlayOutEntityEquipment(armorStand.ae(), list));
    }

    @Override
    public void lookEntity(float yaw, float pitch, boolean all){
        if(all) {
            for (UUID uuid : players) {
                Player player = Bukkit.getPlayer(uuid);
                if(player == null) continue;
                PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
                connection.a(new PacketPlayOutEntityHeadRotation(armorStand, (byte) (yaw * 256 / 360)));
                connection.a(new PacketPlayOutEntity.PacketPlayOutEntityLook(armorStand.ae(), (byte) (yaw * 256 / 360), (byte) (pitch * 256 / 360), true));
                connection.a(new PacketPlayOutEntityHeadRotation(leashed, (byte) (yaw * 256 / 360)));
                connection.a(new PacketPlayOutEntity.PacketPlayOutEntityLook(leashed.ae(), (byte) (yaw * 256 / 360), (byte) (pitch * 256 / 360), true));
            }
            return;
        }
        PlayerConnection connection = ((CraftPlayer) getPlayer()).getHandle().b;
        connection.a(new PacketPlayOutEntityHeadRotation(armorStand, (byte) (yaw * 256 / 360)));
        connection.a(new PacketPlayOutEntity.PacketPlayOutEntityLook(armorStand.ae(), (byte) (yaw * 256 / 360), (byte) (pitch * 256 / 360), true));
        connection.a(new PacketPlayOutEntityHeadRotation(leashed, (byte) (yaw * 256 / 360)));
        connection.a(new PacketPlayOutEntity.PacketPlayOutEntityLook(leashed.ae(), (byte) (yaw * 256 / 360), (byte) (pitch * 256 / 360), true));

    }

    @Override
    public void setLeash(Player player){
        EntityPlayer entityPlayer = ((CraftPlayer)getPlayer()).getHandle();
        PlayerConnection playerConnection = ((CraftPlayer)player).getHandle().b;
        playerConnection.a(new PacketPlayOutAttachEntity(leashed, entityPlayer));
    }

    @Override
    public void teleport(Location location, boolean all){
        EntityPlayer entityPlayer = ((CraftPlayer)getPlayer()).getHandle();
        armorStand.b(location.getX(), location.getY()-1.2, location.getZ(), location.getYaw(), location.getPitch());
        leashed.b(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        if(all){
            for (UUID uuid : players) {
                Player player = Bukkit.getPlayer(uuid);
                if(player == null) continue;
                PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
                connection.a(new PacketPlayOutEntityTeleport(armorStand));
                connection.a(new PacketPlayOutEntityTeleport(leashed));
            }
            return;
        }
        entityPlayer.b.a(new PacketPlayOutEntityTeleport(armorStand));
        entityPlayer.b.a(new PacketPlayOutEntityTeleport(leashed));
    }
}
