package com.francobm.magicosmetics.cache.nms.v1_19_R2;

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
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayerBagHandler extends PlayerBag {
    private final EntityArmorStand armorStand;
    private final double distance;

    public PlayerBagHandler(Player p, double distance, int height, ItemStack backPackItem, ItemStack backPackItemForMe){
        players = new CopyOnWriteArrayList<>(new ArrayList<>());
        this.uuid = p.getUniqueId();
        this.distance = distance;
        this.height = height;
        this.ids = new ArrayList<>();
        this.backPackItem = backPackItem;
        this.backPackItemForMe = backPackItemForMe;
        playerBags.put(uuid, this);
        Player player = getPlayer();
        WorldServer world = ((CraftWorld) player.getWorld()).getHandle();

        armorStand = new EntityArmorStand(EntityTypes.d, world);
        armorStand.b(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), 0);
        armorStand.j(true); //Invisible
        armorStand.m(true); //Invulnerable
        armorStand.t(true); //Marker
        armorStand.al().refresh(((CraftPlayer)player).getHandle());
    }

    @Override
    public void spawn(Player player) {
        Player owner = getPlayer();
        if(owner == null) return;
        if(players.contains(player.getUniqueId())) {
            if(!owner.getWorld().equals(player.getWorld())) {
                remove(player);
                return;
            }
            if(owner.getLocation().distanceSquared(player.getLocation()) > distance) {
                remove(player);
            }
            return;
        }
        if(!owner.getWorld().equals(player.getWorld())) return;
        if(owner.getLocation().distanceSquared(player.getLocation()) > distance) return;
        Location location = owner.getLocation();
        armorStand.b(location.getX(), location.getY(), location.getZ(), location.getYaw(), 0);

        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        entityPlayer.b.a(new PacketPlayOutSpawnEntity(armorStand));
        //client settings
        armorStand.al().refresh(entityPlayer);
        addPassenger(player, owner.getEntityId(), armorStand.ah());
        setItemOnHelmet(player, backPackItem);
        players.add(player.getUniqueId());
    }

    @Override
    public void spawnSelf(Player player) {
        Player owner = getPlayer();
        if(owner == null) return;
        if(players.contains(player.getUniqueId())) {
            if(!owner.getWorld().equals(player.getWorld())) {
                remove(player);
                return;
            }
            if(owner.getLocation().distance(player.getLocation()) > distance) {
                remove(player);
            }
            return;
        }
        if(!owner.getWorld().equals(player.getWorld())) return;
        if(owner.getLocation().distance(player.getLocation()) > distance) return;
        armorStand.j(true); //Invisible true
        armorStand.t(true); //Marker
        Location location = owner.getLocation();
        armorStand.b(location.getX(), location.getY(), location.getZ(), location.getYaw(), 0);

        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        entityPlayer.b.a(new PacketPlayOutSpawnEntity(armorStand));
        armorStand.al().refresh(entityPlayer);
        if(height > 0){
            for(int i = 0; i < height; i++) {
                EntityAreaEffectCloud entityAreaEffectCloud = new EntityAreaEffectCloud(EntityTypes.c, ((CraftWorld)player.getWorld()).getHandle());
                entityAreaEffectCloud.a(0f);
                entityAreaEffectCloud.j(true);
                entityAreaEffectCloud.b(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
                entityPlayer.b.a(new PacketPlayOutSpawnEntity(entityAreaEffectCloud));
                entityAreaEffectCloud.al().refresh(entityPlayer);
                ids.add(entityAreaEffectCloud.ah());
            }
            for(int i = 0; i < height; i++) {
                if(i == 0){
                    addPassenger(player, player.getEntityId(), ids.get(i));
                    continue;
                }
                addPassenger(player, ids.get(i - 1), ids.get(i));
            }
            addPassenger(player, ids.get(ids.size() - 1), armorStand.ah());
        }else{
            addPassenger(player, owner.getEntityId(), armorStand.ah());
        }
        setItemOnHelmet(player, backPackItemForMe == null ? backPackItem : backPackItemForMe);
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
        connection.a(new PacketPlayOutEntityDestroy(armorStand.ah()));
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
        Player owner = getPlayer();
        if(owner == null) return;
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        net.minecraft.world.entity.Entity e = ((CraftEntity)owner).getHandle();

        PacketPlayOutMount packetPlayOutMount = this.createDataSerializer(packetDataSerializer -> {
            packetDataSerializer.d(e.ah());
            packetDataSerializer.a(new int[]{armorStand.ah()});
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
        Player owner = getPlayer();
        if(owner == null) return;
        ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> list = new ArrayList<>();
        list.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(itemStack)));
        if(all) {
            for (UUID uuid : players) {
                if(this.uuid.equals(uuid)) continue;
                Player player = Bukkit.getPlayer(uuid);
                if(player == null) {
                    players.remove(uuid);
                    continue;
                }
                PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
                connection.a(new PacketPlayOutEntityEquipment(armorStand.ah(), list));
            }
            return;
        }
        PlayerConnection connection = ((CraftPlayer)owner).getHandle().b;
        connection.a(new PacketPlayOutEntityEquipment(armorStand.ah(), list));
    }

    @Override
    public void setItemOnHelmet(Player player, ItemStack itemStack) {
        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> list = new ArrayList<>();
        list.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(itemStack)));
        connection.a(new PacketPlayOutEntityEquipment(armorStand.ah(), list));
    }

    @Override
    public void lookEntity(float yaw, float pitch, boolean all) {
        Player owner = getPlayer();
        if(owner == null) return;
        if(all) {
            for (UUID uuid : players) {
                Player player = Bukkit.getPlayer(uuid);
                if(player == null) {
                    players.remove(uuid);
                    continue;
                }
                PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
                connection.a(new PacketPlayOutEntityHeadRotation(armorStand, (byte) (yaw * 256 / 360)));
                connection.a(new PacketPlayOutEntity.PacketPlayOutEntityLook(armorStand.ah(), (byte) (yaw * 256 / 360), /*(byte) (pitch * 256 / 360)*/(byte)0, true));
            }
            return;
        }
        PlayerConnection connection = ((CraftPlayer) owner).getHandle().b;
        connection.a(new PacketPlayOutEntityHeadRotation(armorStand, (byte) (yaw * 256 / 360)));
        connection.a(new PacketPlayOutEntity.PacketPlayOutEntityLook(armorStand.ah(), (byte) (yaw * 256 / 360), /*(byte) (pitch * 256 / 360)*/(byte)0, true));
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
