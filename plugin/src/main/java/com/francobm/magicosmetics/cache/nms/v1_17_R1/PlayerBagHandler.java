package com.francobm.magicosmetics.cache.nms.v1_17_R1;

import com.francobm.magicosmetics.nms.bag.PlayerBag;
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
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayerBagHandler extends PlayerBag {
    private final EntityArmorStand armorStand;
    private final double distance;

    public PlayerBagHandler(Player p, double distance) {
        players = new CopyOnWriteArrayList<>(new ArrayList<>());
        this.uuid = p.getUniqueId();
        this.distance = distance;
        playerBags.put(uuid, this);
        Player player = getPlayer();
        WorldServer world = ((CraftWorld) player.getWorld()).getHandle();

        armorStand = new EntityArmorStand(EntityTypes.c, world);
        armorStand.setPositionRotation(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), 0);
        armorStand.setInvisible(true); //Invisible
        armorStand.setInvulnerable(true); //Invulnerable
        //armorStand.t(true); //Marker

        DataWatcher watcher = armorStand.getDataWatcher();
        watcher.set(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte)0x20);
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(armorStand.getId(), watcher, true);
        ((CraftPlayer)player).getHandle().b.sendPacket(packet);
    }

    @Override
    public void spawn(Player player) {
        if(players.contains(player.getUniqueId())) {
            if(!getPlayer().getWorld().equals(player.getWorld())) {
                remove(player);
                return;
            }
            if(getPlayer().getLocation().distance(player.getLocation()) > distance) {
                remove(player);
            }
            return;
        }
        if(!getPlayer().getWorld().equals(player.getWorld())) return;
        if(getPlayer().getLocation().distance(player.getLocation()) > distance) return;
        armorStand.setInvulnerable(true); //invulnerable true
        armorStand.setInvisible(true); //Invisible true
        armorStand.setMarker(true); //Marker
        Location location = getPlayer().getLocation();
        armorStand.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), 0);

        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        connection.sendPacket(new PacketPlayOutSpawnEntityLiving(armorStand));
        //client settings
        DataWatcher watcher = armorStand.getDataWatcher();
        watcher.set(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte)0x20);
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(armorStand.getId(), watcher, true);
        connection.sendPacket(packet);
        addPassenger(player, getPlayer(), armorStand.getBukkitEntity());
        players.add(player.getUniqueId());
    }

    @Override
    public void spawn(boolean exception) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if(exception && player.getUniqueId().equals(this.uuid)) continue;
            spawn(player);
        }
    }

    @Override
    public void remove() {
        for(UUID uuid : players){
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) {
                players.remove(uuid);
                continue;
            }
            remove(player);
        }
        playerBags.remove(uuid);
    }

    @Override
    public void remove(Player player) {
        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        connection.sendPacket(new PacketPlayOutEntityDestroy(armorStand.getId()));
        players.remove(player.getUniqueId());
    }

    @Override
    public void addPassenger(boolean exception) {
        for(UUID uuid : players){
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) {
                players.remove(uuid);
                continue;
            }
            if(exception && player.getUniqueId().equals(this.uuid)) continue;
            addPassenger(player);
        }
    }

    @Override
    public void addPassenger(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        net.minecraft.world.entity.Entity e = ((CraftEntity)getPlayer()).getHandle();

        PacketPlayOutMount packetPlayOutMount = this.createDataSerializer(packetDataSerializer -> {
            packetDataSerializer.d(e.getId());
            packetDataSerializer.a(new int[]{armorStand.getId()});
            return new PacketPlayOutMount(packetDataSerializer);
        });
        entityPlayer.b.sendPacket(packetPlayOutMount);
    }

    @Override
    public void addPassenger(Player player, Entity entity, Entity passenger) {
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        net.minecraft.world.entity.Entity e = ((CraftEntity)entity).getHandle();
        net.minecraft.world.entity.Entity pass = ((CraftEntity)passenger).getHandle();

        PacketPlayOutMount packetPlayOutMount = this.createDataSerializer(packetDataSerializer -> {
            packetDataSerializer.d(e.getId());
            packetDataSerializer.a(new int[]{pass.getId()});
            return new PacketPlayOutMount(packetDataSerializer);
        });
        entityPlayer.b.sendPacket(packetPlayOutMount);
    }

    @Override
    public void setItemOnHelmet(ItemStack itemStack, boolean all) {
        if(all) {
            for (UUID uuid : players) {
                Player player = Bukkit.getPlayer(uuid);
                if(player == null) {
                    players.remove(uuid);
                    continue;
                }
                PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
                ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> list = new ArrayList<>();
                list.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(itemStack)));
                connection.sendPacket(new PacketPlayOutEntityEquipment(armorStand.getId(), list));
            }
            return;
        }
        PlayerConnection connection = ((CraftPlayer)getPlayer()).getHandle().b;
        ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> list = new ArrayList<>();
        list.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(itemStack)));
        connection.sendPacket(new PacketPlayOutEntityEquipment(armorStand.getId(), list));
    }

    @Override
    public void lookEntity(float yaw, float pitch, boolean all) {
        if(all) {
            for (UUID uuid : players) {
                Player player = Bukkit.getPlayer(uuid);
                if(player == null) {
                    players.remove(uuid);
                    continue;
                }
                PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
                connection.sendPacket(new PacketPlayOutEntityHeadRotation(armorStand, (byte) (yaw * 256 / 360)));
                connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(armorStand.getId(), (byte) (yaw * 256 / 360), /*(byte) (pitch * 256 / 360)*/(byte)0, true));
            }
            return;
        }
        PlayerConnection connection = ((CraftPlayer) getPlayer()).getHandle().b;
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(armorStand, (byte) (yaw * 256 / 360)));
        connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(armorStand.getId(), (byte) (yaw * 256 / 360), (byte) (pitch * 256 / 360), true));
    }

    private <T> T createDataSerializer(UnsafeFunction<PacketDataSerializer, T> callback) {
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
    private interface UnsafeFunction<K, T> {
        T apply(K k) throws Exception;
    }

    @Override
    public Entity getEntity() {
        return armorStand.getBukkitEntity();
    }
}
