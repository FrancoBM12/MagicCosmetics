package com.francobm.magicosmetics.nms.v1_18_R1.cache;

import com.francobm.magicosmetics.nms.IRangeManager;
import com.francobm.magicosmetics.nms.bag.PlayerBag;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelPipeline;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.EntityAreaEffectCloud;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayerBagHandler extends PlayerBag {
    private final EntityArmorStand armorStand;
    private final double distance;
    private final EntityPlayer entityPlayer;

    public PlayerBagHandler(Player p, IRangeManager rangeManager, double distance, float height, ItemStack backPackItem, ItemStack backPackItemForMe){
        hideViewers = new CopyOnWriteArrayList<>(new ArrayList<>());
        this.uuid = p.getUniqueId();
        this.distance = distance;
        this.height = height;
        this.ids = new ArrayList<>();
        this.backPackItem = backPackItem;
        this.backPackItemForMe = backPackItemForMe;
        this.rangeManager = rangeManager;
        Player player = getPlayer();
        entityPlayer = ((CraftPlayer) player).getHandle();
        WorldServer world = ((CraftWorld) player.getWorld()).getHandle();

        armorStand = new EntityArmorStand(EntityTypes.c, world);
        armorStand.b(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), 0);
        armorStand.j(true); //Invisible
        armorStand.m(true); //Invulnerable
        armorStand.t(true); //Marker

        armorStand.k(entityPlayer);
        net.minecraft.world.entity.Entity entity = entityPlayer;
        List<net.minecraft.world.entity.Entity> orderedPassengers = new ArrayList<>();
        orderedPassengers.add(armorStand);
        orderedPassengers.addAll(entity.au.stream().filter((entity1) -> entity1 != armorStand).collect(ImmutableList.toImmutableList()));
        entity.au = ImmutableList.copyOf(orderedPassengers);
    }

    @Override
    public void spawn(Player player) {
        if(hideViewers.contains(player.getUniqueId())) return;
        Player owner = getPlayer();
        if(owner == null) return;
        Location location = owner.getLocation();
        armorStand.b(location.getX(), location.getY(), location.getZ(), location.getYaw(), 0);

        sendPackets(player, getBackPackSpawn(backPackItem));
    }

    @Override
    public void spawnSelf(Player player) {
        Player owner = getPlayer();
        if(owner == null) return;
        Location location = owner.getLocation();
        armorStand.j(true); //Invisible true
        armorStand.t(true); //Marker
        armorStand.b(location.getX(), location.getY(), location.getZ(), location.getYaw(), 0);

        sendPackets(player, getBackPackSpawn(backPackItemForMe == null ? backPackItem : backPackItemForMe));
        if(height > 0){
            for(int i = 0; i < height; i++) {
                EntityAreaEffectCloud entityAreaEffectCloud = new EntityAreaEffectCloud(EntityTypes.b, ((CraftWorld)player.getWorld()).getHandle());
                entityAreaEffectCloud.a(0f);
                entityAreaEffectCloud.j(true);
                entityAreaEffectCloud.b(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
                sendPackets(player, getCloudsSpawn(entityAreaEffectCloud));
                ids.add(entityAreaEffectCloud.ae());
            }
            for(int i = 0; i < height; i++) {
                if(i == 0){
                    addPassenger(player, lendEntityId == -1 ? player.getEntityId() : lendEntityId, ids.get(i));
                    continue;
                }
                addPassenger(player, ids.get(i - 1), ids.get(i));
            }
            addPassenger(player, ids.get(ids.size() - 1), armorStand.ae());
        }else {
            addPassenger(player, lendEntityId == -1 ? owner.getEntityId() : lendEntityId, armorStand.ae());
        }
        setItemOnHelmet(player, backPackItemForMe == null ? backPackItem : backPackItemForMe);
    }

    @Override
    public void spawn(boolean exception) {
        for (Player player : getPlayersInRange()) {
            if(exception && player.getUniqueId().equals(uuid)) continue;
            spawn(player);
        }
    }

    @Override
    public void remove() {
        for (Player player : getPlayersInRange()) {
            remove(player);
        }
        net.minecraft.world.entity.Entity entity = entityPlayer;
        if (entity.au.size() == 1 && entity.au.get(0) == entity) {
            entity.au = ImmutableList.of();
        } else {
            entity.au = entity.au.stream().filter((entity1) -> entity1 != armorStand).collect(ImmutableList.toImmutableList());
        }
    }

    @Override
    public void remove(Player player) {
        if(player.getUniqueId().equals(uuid)) {
            sendPackets(player, getBackPackDismount(true));
            ids.clear();
            return;
        }
        sendPackets(player, getBackPackDismount(false));
    }

    @Override
    public void addPassenger(boolean exception) {
        List<Packet<?>> backPack = getBackPackMountPacket(lendEntityId == -1 ? getPlayer().getEntityId() : lendEntityId, armorStand.ae());
        for(Player player : getPlayersInRange()){
            if(exception && player.getUniqueId().equals(this.uuid)) continue;
            sendPackets(player, backPack);
        }
    }

    @Override
    public void addPassenger(Player player, int entity, int passenger) {
        sendPackets(player, getBackPackMountPacket(entity, passenger));
    }

    @Override
    public void setItemOnHelmet(Player player, ItemStack itemStack) {
        sendPackets(player, getBackPackHelmetPacket(itemStack));
    }

    @Override
    public void lookEntity(float yaw, float pitch, boolean all) {
        Player owner = getPlayer();
        if(owner == null) return;
        if(all) {
            for (Player player : getPlayersInRange()) {
                sendPackets(player, getBackPackRotationPackets(yaw));
            }
            return;
        }
        sendPackets(owner, getBackPackRotationPackets(yaw));
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

    public double getDistance() {
        return distance;
    }

    @Override
    public Entity getEntity() {
        return armorStand.getBukkitEntity();
    }

    private List<Packet<?>> getBackPackSpawn(ItemStack backpackItem) {
        ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> list = new ArrayList<>();
        list.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(backpackItem)));
        PacketPlayOutSpawnEntity spawnEntity = new PacketPlayOutSpawnEntity(armorStand);
        PacketPlayOutEntityMetadata entityMetadata = new PacketPlayOutEntityMetadata(armorStand.ae(), armorStand.ai(), true);
        PacketPlayOutMount mountEntity = new PacketPlayOutMount(entityPlayer);
        PacketPlayOutEntityEquipment equip = new PacketPlayOutEntityEquipment(armorStand.ae(), list);
        return Arrays.asList(spawnEntity, entityMetadata, equip, mountEntity);
    }

    private List<Packet<?>> getCloudsSpawn(EntityAreaEffectCloud entityAreaEffectCloud) {
        PacketPlayOutSpawnEntity spawnEntity = new PacketPlayOutSpawnEntity(entityAreaEffectCloud);
        PacketPlayOutEntityMetadata entityMetadata = new PacketPlayOutEntityMetadata(entityAreaEffectCloud.ae(), entityAreaEffectCloud.ai(), true);
        return Arrays.asList(spawnEntity, entityMetadata);
    }

    private List<Packet<?>> getBackPackDismount(boolean removeClouds) {
        List<Packet<?>> packets = new ArrayList<>();
        if(!removeClouds) {
            PacketPlayOutEntityDestroy backPackDestroy = new PacketPlayOutEntityDestroy(armorStand.ae());
            return Collections.singletonList(backPackDestroy);
        }
        for (Integer id : ids) {
            packets.add(new PacketPlayOutEntityDestroy(id));
        }
        packets.add(new PacketPlayOutEntityDestroy(armorStand.ae()));
        return packets;
    }

    private List<Packet<?>> getBackPackMountPacket(int entity, int passenger) {
        PacketPlayOutMount packetPlayOutMount = this.createDataSerializer(packetDataSerializer -> {
            packetDataSerializer.d(entity);
            packetDataSerializer.a(new int[]{passenger});
            return new PacketPlayOutMount(packetDataSerializer);
        });
        return Collections.singletonList(packetPlayOutMount);
    }

    private List<Packet<?>> getBackPackHelmetPacket(ItemStack itemStack) {
        ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> list = new ArrayList<>();
        list.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(itemStack)));
        return Collections.singletonList(new PacketPlayOutEntityEquipment(armorStand.ae(), list));
    }

    private List<Packet<?>> getBackPackHelmetPacket(ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> pairs) {
        return Collections.singletonList(new PacketPlayOutEntityEquipment(armorStand.ae(), pairs));
    }

    private List<Packet<?>> getBackPackRotationPackets(float yaw) {
        PacketPlayOutEntityHeadRotation packetPlayOutEntityHeadRotation = new PacketPlayOutEntityHeadRotation(armorStand, (byte) (yaw * 256 / 360));
        PacketPlayOutEntity.PacketPlayOutEntityLook packetPlayOutEntityLook = new PacketPlayOutEntity.PacketPlayOutEntityLook(armorStand.ae(), (byte) (yaw * 256 / 360), /*(byte) (pitch * 256 / 360)*/(byte)0, true);
        return Arrays.asList(packetPlayOutEntityHeadRotation, packetPlayOutEntityLook);
    }

    private void sendPackets(Player player, List<Packet<?>> packets) {
        final ChannelPipeline pipeline = getPrivateChannelPipeline(((CraftPlayer) player).getHandle().b);
        if(pipeline == null) return;
        for(Packet<?> packet : packets)
            pipeline.write(packet);
        pipeline.flush();
    }

    private ChannelPipeline getPrivateChannelPipeline(PlayerConnection playerConnection) {
        return playerConnection.a.k.pipeline();
    }
}
