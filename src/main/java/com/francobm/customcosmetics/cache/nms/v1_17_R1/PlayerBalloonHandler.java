package com.francobm.customcosmetics.cache.nms.v1_17_R1;

import com.francobm.customcosmetics.cache.nms.PlayerBalloon;
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
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class PlayerBalloonHandler extends PlayerBalloon {

    public PlayerBalloonHandler(Player p, Location location){
        players = new ArrayList<>();
        this.uuid = p.getUniqueId();
        playerBags.put(uuid, this);
        Player player = getPlayer();
        WorldServer world = ((CraftWorld) player.getWorld()).getHandle();

        armorStand = new EntityArmorStand(EntityTypes.c, world);
        armorStand.setPositionRotation(location.getX(), location.getY()-1.2, location.getZ(), location.getYaw(), location.getPitch());
        armorStand.setInvisible(true); //Invisible
        armorStand.setInvulnerable(true); //Invulnerable
        leashed = new EntityPufferFish(EntityTypes.at, world);
        leashed.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        leashed.setLeashHolder(((CraftPlayer)player).getHandle(), true);
        leashed.setInvulnerable(true);
        leashed.setInvisible(true);
        leashed.setSilent(true); //silent true

        DataWatcher watcher = armorStand.getDataWatcher();
        watcher.set(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte)0x20);
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(armorStand.getId(), watcher, true);
        ((CraftPlayer)player).getHandle().b.sendPacket(packet);
        watcher = leashed.getDataWatcher();
        watcher.set(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte)0x20);
        packet = new PacketPlayOutEntityMetadata(leashed.getId(), watcher, true);
        ((CraftPlayer)player).getHandle().b.sendPacket(packet);
    }

    @Override
    public void spawnBalloon(Player player) {
        if(players.contains(player.getUniqueId())) return;
        armorStand.setInvulnerable(true); //invulnerable true
        armorStand.setInvisible(true); //Invisible true

        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        connection.sendPacket(new PacketPlayOutSpawnEntityLiving(armorStand));
        //client settings
        DataWatcher watcher = armorStand.getDataWatcher();
        watcher.set(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte)0x20);
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(armorStand.getId(), watcher, true);
        connection.sendPacket(packet);

        leashed.setLeashHolder(((CraftPlayer)getPlayer()).getHandle(), true);
        leashed.setInvulnerable(true);
        leashed.setInvisible(true);
        leashed.setSilent(true); //silent true

        connection.sendPacket(new PacketPlayOutSpawnEntityLiving(leashed));
        //
        watcher = leashed.getDataWatcher();
        watcher.set(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte)0x20);
        packet = new PacketPlayOutEntityMetadata(leashed.getId(), watcher, true);
        connection.sendPacket(packet);
        setLeash(player);
        players.add(player.getUniqueId());
    }

    @Override
    public void remove(boolean all){
        if(all){
            for(Player player : Bukkit.getOnlinePlayers()){
                if(!players.contains(player.getUniqueId())) continue;
                PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
                connection.sendPacket(new PacketPlayOutEntityDestroy(armorStand.getId()));
                connection.sendPacket(new PacketPlayOutEntityDestroy(leashed.getId()));
                players.remove(player.getUniqueId());
            }
            playerBags.remove(uuid);
            return;
        }
        PlayerConnection connection = ((CraftPlayer)getPlayer()).getHandle().b;
        connection.sendPacket(new PacketPlayOutEntityDestroy(armorStand.getId()));
        connection.sendPacket(new PacketPlayOutEntityDestroy(leashed.getId()));
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
                connection.sendPacket(new PacketPlayOutEntityEquipment(armorStand.getId(), list));
            }
            return;
        }
        PlayerConnection connection = ((CraftPlayer)getPlayer()).getHandle().b;
        ArrayList<Pair<EnumItemSlot, ItemStack>> list = new ArrayList<>();
        list.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(itemStack)));
        connection.sendPacket(new PacketPlayOutEntityEquipment(armorStand.getId(), list));
    }

    @Override
    public void lookEntity(float yaw, float pitch, boolean all){
        if(all) {
            for (UUID uuid : players) {
                Player player = Bukkit.getPlayer(uuid);
                if(player == null) continue;
                PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
                connection.sendPacket(new PacketPlayOutEntityHeadRotation(armorStand, (byte) (yaw * 256 / 360)));
                connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(armorStand.getId(), (byte) (yaw * 256 / 360), (byte) (pitch * 256 / 360), true));
                connection.sendPacket(new PacketPlayOutEntityHeadRotation(leashed, (byte) (yaw * 256 / 360)));
                connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(leashed.getId(), (byte) (yaw * 256 / 360), (byte) (pitch * 256 / 360), true));
            }
            return;
        }
        PlayerConnection connection = ((CraftPlayer) getPlayer()).getHandle().b;
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(armorStand, (byte) (yaw * 256 / 360)));
        connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(armorStand.getId(), (byte) (yaw * 256 / 360), (byte) (pitch * 256 / 360), true));
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(leashed, (byte) (yaw * 256 / 360)));
        connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(leashed.getId(), (byte) (yaw * 256 / 360), (byte) (pitch * 256 / 360), true));

    }

    @Override
    public void setLeash(Player player){
        EntityPlayer entityPlayer = ((CraftPlayer)getPlayer()).getHandle();
        PlayerConnection playerConnection = ((CraftPlayer)player).getHandle().b;
        playerConnection.sendPacket(new PacketPlayOutAttachEntity(leashed, entityPlayer));
    }

    @Override
    public void teleport(Location location, boolean all){
        EntityPlayer entityPlayer = ((CraftPlayer)getPlayer()).getHandle();
        armorStand.setPositionRotation(location.getX(), location.getY()-1.2, location.getZ(), location.getYaw(), location.getPitch());
        leashed.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        if(all){
            for (UUID uuid : players) {
                Player player = Bukkit.getPlayer(uuid);
                if(player == null) continue;
                PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
                connection.sendPacket(new PacketPlayOutEntityTeleport(armorStand));
                connection.sendPacket(new PacketPlayOutEntityTeleport(leashed));
            }
            return;
        }
        entityPlayer.b.sendPacket(new PacketPlayOutEntityTeleport(armorStand));
        entityPlayer.b.sendPacket(new PacketPlayOutEntityTeleport(leashed));
    }
}
