package com.francobm.magicosmetics.nms.v1_20_R4.cache;

import com.francobm.magicosmetics.nms.bag.EntityBag;
import com.mojang.datafixers.util.Pair;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R4.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class EntityBagHandler extends EntityBag {
    private final EntityArmorStand armorStand;
    private final double distance;

    public EntityBagHandler(Entity entity, double distance) {
        players = new CopyOnWriteArrayList<>(new ArrayList<>());
        this.uuid = entity.getUniqueId();
        this.distance = distance;
        this.entity = entity;
        entityBags.put(uuid, this);
        WorldServer world = ((CraftWorld) entity.getWorld()).getHandle();

        armorStand = new EntityArmorStand(EntityTypes.d, world);
        armorStand.b(entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ(), entity.getLocation().getYaw(), 0);
        armorStand.k(true); //Invisible
        armorStand.n(true); //Invulnerable
        armorStand.u(true); //Marker

    }

    @Override
    public void spawnBag(Player player) {
        if(players.contains(player.getUniqueId())) {
            if(!getEntity().getWorld().equals(player.getWorld())) {
                remove(player);
                return;
            }
            if(getEntity().getLocation().distanceSquared(player.getLocation()) > distance) {
                remove(player);
            }
            return;
        }
        if(!getEntity().getWorld().equals(player.getWorld())) return;
        if(getEntity().getLocation().distanceSquared(player.getLocation()) > distance) return;
        armorStand.n(true); //invulnerable true
        armorStand.k(true); //Invisible true
        armorStand.u(true); //Marker
        Location location = getEntity().getLocation();
        armorStand.b(location.getX(), location.getY(), location.getZ(), location.getYaw(), 0);

        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        entityPlayer.c.b(new PacketPlayOutSpawnEntity(armorStand));
        //client settings
        entityPlayer.c.b(new PacketPlayOutEntityMetadata(armorStand.al(), armorStand.ap().c()));
        addPassenger(player, getEntity(), armorStand.getBukkitEntity());
        players.add(player.getUniqueId());
    }

    @Override
    public void spawnBag() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            spawnBag(player);
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
        entityBags.remove(uuid);
    }

    @Override
    public void addPassenger() {
        for(UUID uuid : players){
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) {
                players.remove(uuid);
                continue;
            }
            EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
            net.minecraft.world.entity.Entity e = ((CraftEntity)entity).getHandle();
            PacketPlayOutMount packetPlayOutMount = this.createDataSerializer(packetDataSerializer -> {
                packetDataSerializer.c(e.al());
                packetDataSerializer.a(new int[]{armorStand.al()});
                return PacketPlayOutMount.a.decode(packetDataSerializer);
            });
            entityPlayer.c.b(packetPlayOutMount);
        }
    }

    @Override
    public void addPassenger(Entity entity, Entity passenger) {
        for(UUID uuid : players){
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) {
                players.remove(uuid);
                continue;
            }
            EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
            net.minecraft.world.entity.Entity e = ((CraftEntity)entity).getHandle();
            net.minecraft.world.entity.Entity pass = ((CraftEntity)passenger).getHandle();

            PacketPlayOutMount packetPlayOutMount = this.createDataSerializer(packetDataSerializer -> {
                packetDataSerializer.d(e.al());
                packetDataSerializer.a(new int[]{pass.al()});
                return PacketPlayOutMount.a.decode(packetDataSerializer);
            });
            entityPlayer.c.b(packetPlayOutMount);
        }
    }

    @Override
    public void addPassenger(Player player, Entity entity, Entity passenger) {
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        net.minecraft.world.entity.Entity e = ((CraftEntity)entity).getHandle();
        net.minecraft.world.entity.Entity pass = ((CraftEntity)passenger).getHandle();

        PacketPlayOutMount packetPlayOutMount = this.createDataSerializer(packetDataSerializer -> {
            packetDataSerializer.d(e.al());
            packetDataSerializer.a(new int[]{pass.al()});
            return PacketPlayOutMount.a.decode(packetDataSerializer);
        });
        entityPlayer.c.b(packetPlayOutMount);
    }

    @Override
    public void remove(Player player) {
        PlayerConnection connection = ((CraftPlayer)player).getHandle().c;
        connection.b(new PacketPlayOutEntityDestroy(armorStand.al()));
        players.remove(player.getUniqueId());
    }

    @Override
    public void setItemOnHelmet(ItemStack itemStack) {
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) {
                players.remove(uuid);
                continue;
            }
            PlayerConnection connection = ((CraftPlayer)player).getHandle().c;
            ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> list = new ArrayList<>();
            list.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(itemStack)));
            connection.b(new PacketPlayOutEntityEquipment(armorStand.al(), list));
        }
    }

    @Override
    public void lookEntity() {
        float yaw = getEntity().getLocation().getYaw();
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) {
                players.remove(uuid);
                continue;
            }
            PlayerConnection connection = ((CraftPlayer) player).getHandle().c;
            connection.b(new PacketPlayOutEntityHeadRotation(armorStand, (byte) (yaw * 256 / 360)));
            connection.b(new PacketPlayOutEntity.PacketPlayOutEntityLook(armorStand.al(), (byte) (yaw * 256 / 360), /*(byte) (pitch * 256 / 360)*/(byte)0, true));
        }
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
}
