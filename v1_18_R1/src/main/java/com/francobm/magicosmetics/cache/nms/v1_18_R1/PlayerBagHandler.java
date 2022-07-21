package com.francobm.magicosmetics.cache.nms.v1_18_R1;

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

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayerBagHandler extends PlayerBag {
    private final EntityArmorStand armorStand;
    private final double distance;

    public PlayerBagHandler(Player p, double distance, int height){
        players = new CopyOnWriteArrayList<>(new ArrayList<>());
        this.uuid = p.getUniqueId();
        this.distance = distance;
        this.height = height;
        this.ids = new ArrayList<>();
        playerBags.put(uuid, this);
        Player player = getPlayer();
        WorldServer world = ((CraftWorld) player.getWorld()).getHandle();

        armorStand = new EntityArmorStand(EntityTypes.c, world);
        armorStand.b(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), 0);
        armorStand.j(true); //Invisible
        armorStand.m(true); //Invulnerable
        //armorStand.t(true); //Marker

        DataWatcher watcher = armorStand.ai();
        watcher.b(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte)0x20);
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(armorStand.ae(), watcher, true);
        ((CraftPlayer)player).getHandle().b.a(packet);
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
        armorStand.m(true); //invulnerable true
        armorStand.j(true); //Invisible true
        armorStand.t(true); //Marker
        Location location = getPlayer().getLocation();
        armorStand.b(location.getX(), location.getY(), location.getZ(), location.getYaw(), 0);

        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        connection.a(new PacketPlayOutSpawnEntityLiving(armorStand));
        //client settings
        DataWatcher watcher = armorStand.ai();
        watcher.b(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte)0x20);
        connection.a(new PacketPlayOutEntityMetadata(armorStand.ae(), watcher, true));
        addPassenger(player, getPlayer().getEntityId(), armorStand.ae());
        players.add(player.getUniqueId());
    }

    @Override
    public void spawnSelf(Player player) {
        if(height == 0) {
            spawn(player);
            return;
        }
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
        armorStand.j(true); //Invisible true
        armorStand.t(true); //Marker
        Location location = getPlayer().getLocation();
        armorStand.b(location.getX(), location.getY(), location.getZ(), location.getYaw(), 0);

        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        connection.a(new PacketPlayOutSpawnEntityLiving(armorStand));
        connection.a(new PacketPlayOutEntityMetadata(armorStand.ae(), armorStand.ai(), true));
        for(int i = 0; i < height; i++) {
            EntityAreaEffectCloud entityAreaEffectCloud = new EntityAreaEffectCloud(EntityTypes.b, ((CraftWorld)player.getWorld()).getHandle());
            entityAreaEffectCloud.a(0f);
            entityAreaEffectCloud.j(true);
            entityAreaEffectCloud.b(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            connection.a(new PacketPlayOutSpawnEntity(entityAreaEffectCloud));
            connection.a(new PacketPlayOutEntityMetadata(entityAreaEffectCloud.ae(), entityAreaEffectCloud.ai(), true));
            ids.add(entityAreaEffectCloud.ae());
        }
        for(int i = 0; i < height; i++) {
            if(i == 0){
                addPassenger(player, player.getEntityId(), ids.get(i));
                continue;
            }
            addPassenger(player, ids.get(i - 1), ids.get(i));
        }
        addPassenger(player, ids.get(ids.size() - 1), armorStand.ae());
        players.add(player.getUniqueId());
    }

    @Override
    public void spawn(boolean exception) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if(exception && player.getUniqueId().equals(uuid)) continue;
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
        if(player.getUniqueId().equals(uuid)) {
            for (Integer id : ids) {
                connection.a(new PacketPlayOutEntityDestroy(id));
            }
            ids.clear();
        }
        connection.a(new PacketPlayOutEntityDestroy(armorStand.ae()));
        players.remove(player.getUniqueId());
    }

    @Override
    public void addPassenger(boolean exception) {
        for(UUID uuid : players){
            if(exception && uuid.equals(this.uuid)) continue;
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) {
                players.remove(uuid);
                continue;
            }
            addPassenger(player);
        }
    }

    @Override
    public void addPassenger(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        net.minecraft.world.entity.Entity e = ((CraftEntity)getPlayer()).getHandle();

        PacketPlayOutMount packetPlayOutMount = this.createDataSerializer(packetDataSerializer -> {
            packetDataSerializer.d(e.ae());
            packetDataSerializer.a(new int[]{armorStand.ae()});
            return new PacketPlayOutMount(packetDataSerializer);
        });
        entityPlayer.b.a(packetPlayOutMount);
    }

    @Override
    public void addPassenger(Player player, int entity, int passenger) {
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();

        PacketPlayOutMount packetPlayOutMount = this.createDataSerializer(packetDataSerializer -> {
            packetDataSerializer.d(entity);
            packetDataSerializer.a(new int[]{passenger});
            return new PacketPlayOutMount(packetDataSerializer);
        });
        entityPlayer.b.a(packetPlayOutMount);
    }

    @Override
    public void setItemOnHelmet(ItemStack itemStack, boolean all) {
        if(all) {
            for (UUID uuid : players) {
                if(getPlayer().getUniqueId().equals(uuid)) continue;
                Player player = Bukkit.getPlayer(uuid);
                if(player == null) {
                    players.remove(uuid);
                    continue;
                }
                PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
                ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> list = new ArrayList<>();
                list.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(itemStack)));
                connection.a(new PacketPlayOutEntityEquipment(armorStand.ae(), list));
            }
            return;
        }
        PlayerConnection connection = ((CraftPlayer)getPlayer()).getHandle().b;
        ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> list = new ArrayList<>();
        list.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(itemStack)));
        connection.a(new PacketPlayOutEntityEquipment(armorStand.ae(), list));
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
                connection.a(new PacketPlayOutEntityHeadRotation(armorStand, (byte) (yaw * 256 / 360)));
                connection.a(new PacketPlayOutEntity.PacketPlayOutEntityLook(armorStand.ae(), (byte) (yaw * 256 / 360), /*(byte) (pitch * 256 / 360)*/(byte)0, true));
            }
            return;
        }
        PlayerConnection connection = ((CraftPlayer) getPlayer()).getHandle().b;
        connection.a(new PacketPlayOutEntityHeadRotation(armorStand, (byte) (yaw * 256 / 360)));
        connection.a(new PacketPlayOutEntity.PacketPlayOutEntityLook(armorStand.ae(), (byte) (yaw * 256 / 360), /*(byte) (pitch * 256 / 360)*/(byte)0, true));
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
}
