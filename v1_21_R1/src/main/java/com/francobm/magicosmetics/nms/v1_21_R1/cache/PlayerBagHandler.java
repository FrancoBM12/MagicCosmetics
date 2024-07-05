package com.francobm.magicosmetics.nms.v1_21_R1.cache;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.nms.bag.PlayerBag;
import com.mojang.datafixers.util.Pair;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelPipeline;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.world.entity.EntityAreaEffectCloud;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_21_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R1.util.CraftLocation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayerBagHandler extends PlayerBag {
    private final EntityArmorStand armorStand;
    private final double distance;

    public PlayerBagHandler(Player p, double distance, float height, ItemStack backPackItem, ItemStack backPackItemForMe){
        viewers = new CopyOnWriteArrayList<>(new ArrayList<>());
        hideViewers = new CopyOnWriteArrayList<>(new ArrayList<>());
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
        armorStand.k(true); //Invisible
        armorStand.n(true); //Invulnerable
        armorStand.v(true); //Marker
    }

    @Override
    public void spawn(Player player) {
        if(hideViewers.contains(player.getUniqueId())) return;
        Player owner = getPlayer();
        if(owner == null) return;
        if(viewers.contains(player.getUniqueId())) {
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

        sendPackets(player, getBackPackSpawn());
        addPassenger(player, lendEntityId == -1 ? owner.getEntityId() : lendEntityId, armorStand.an());
        setItemOnHelmet(player, backPackItem);
        viewers.add(player.getUniqueId());
    }

    @Override
    public void spawnSelf(Player player) {
        Player owner = getPlayer();
        if(owner == null) return;
        if(viewers.contains(player.getUniqueId())) {
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
        armorStand.k(true); //Invisible true
        armorStand.v(true); //Marker
        Location location = owner.getLocation();
        armorStand.b(location.getX(), location.getY(), location.getZ(), location.getYaw(), 0);

        sendPackets(player, getBackPackSpawn());
        if(height > 0){
            for(int i = 0; i < height; i++) {
                EntityAreaEffectCloud entityAreaEffectCloud = new EntityAreaEffectCloud(EntityTypes.b, ((CraftWorld)player.getWorld()).getHandle());
                entityAreaEffectCloud.a(0f);
                entityAreaEffectCloud.k(true);
                entityAreaEffectCloud.b(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
                sendPackets(player, getCloudsSpawn(entityAreaEffectCloud));
                ids.add(entityAreaEffectCloud.an());
            }
            for(int i = 0; i < height; i++) {
                if(i == 0){
                    addPassenger(player, lendEntityId == -1 ? player.getEntityId() : lendEntityId, ids.get(i));
                    continue;
                }
                addPassenger(player, ids.get(i - 1), ids.get(i));
            }
            addPassenger(player, ids.get(ids.size() - 1), armorStand.an());
        }else{
            addPassenger(player, lendEntityId == -1 ? owner.getEntityId() : lendEntityId, armorStand.an());
        }
        setItemOnHelmet(player, backPackItemForMe == null ? backPackItem : backPackItemForMe);
        viewers.add(player.getUniqueId());
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
        for(UUID uuid : viewers){
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) {
                viewers.remove(uuid);
                continue;
            }
            remove(player);
        }
        playerBags.remove(uuid);
    }

    @Override
    public void remove(Player player) {
        if(player.getUniqueId().equals(uuid)) {
            sendPackets(player, getBackPackDismount(true));
            ids.clear();
            viewers.remove(player.getUniqueId());
            return;
        }
        sendPackets(player, getBackPackDismount(false));
        viewers.remove(player.getUniqueId());
    }

    @Override
    public void addPassenger(boolean exception) {
        List<Packet<?>> backPack = getBackPackMountPacket(lendEntityId == -1 ? getPlayer().getEntityId() : lendEntityId, armorStand.an());
        for(UUID uuid : viewers){
            if(exception && uuid.equals(this.uuid)) continue;
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) {
                viewers.remove(uuid);
                continue;
            }
            sendPackets(player, backPack);
        }
    }

    @Override
    public void addPassenger(Player player, int entity, int passenger) {
        sendPackets(player, getBackPackMountPacket(entity, passenger));
    }

    @Override
    public void setItemOnHelmet(ItemStack itemStack, boolean all) {
        Player owner = getPlayer();
        if(owner == null) return;
        ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> pairs = new ArrayList<>();
        pairs.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(itemStack)));
        if(all) {
            for (UUID uuid : viewers) {
                if(this.uuid.equals(uuid)) continue;
                Player player = Bukkit.getPlayer(uuid);
                if(player == null) {
                    viewers.remove(uuid);
                    continue;
                }
                sendPackets(player, getBackPackHelmetPacket(pairs));
            }
            return;
        }
        sendPackets(owner, getBackPackHelmetPacket(pairs));
    }

    @Override
    public void setItemOnHelmet(Player player, ItemStack itemStack) {
        sendPackets(player, getBackPackHelmetPacket(itemStack));
    }

    private List<Packet<?>> getBackPackSpawn() {
        PacketPlayOutSpawnEntity spawnEntity = new PacketPlayOutSpawnEntity(armorStand, 0, CraftLocation.toBlockPosition(armorStand.getBukkitEntity().getLocation()));
        PacketPlayOutEntityMetadata entityMetadata = new PacketPlayOutEntityMetadata(armorStand.an(), armorStand.ar().c());
        return Arrays.asList(spawnEntity, entityMetadata);
    }

    private List<Packet<?>> getCloudsSpawn(EntityAreaEffectCloud entityAreaEffectCloud) {
        PacketPlayOutSpawnEntity spawnEntity = new PacketPlayOutSpawnEntity(entityAreaEffectCloud, 0, CraftLocation.toBlockPosition(entityAreaEffectCloud.getBukkitEntity().getLocation()));
        PacketPlayOutEntityMetadata entityMetadata = new PacketPlayOutEntityMetadata(entityAreaEffectCloud.an(), entityAreaEffectCloud.ar().c());
        return Arrays.asList(spawnEntity, entityMetadata);
    }

    private List<Packet<?>> getBackPackDismount(boolean removeClouds) {
        List<Packet<?>> packets = new ArrayList<>();
        if(!removeClouds) {
            PacketPlayOutEntityDestroy backPackDestroy = new PacketPlayOutEntityDestroy(armorStand.an());
            return Collections.singletonList(backPackDestroy);
        }
        for (Integer id : ids) {
            packets.add(new PacketPlayOutEntityDestroy(id));
        }
        packets.add(new PacketPlayOutEntityDestroy(armorStand.an()));
        return packets;
    }

    private List<Packet<?>> getBackPackMountPacket(int entity, int passenger) {
        PacketPlayOutMount packetPlayOutMount = this.createDataSerializer(packetDataSerializer -> {
            packetDataSerializer.c(entity);
            packetDataSerializer.a(new int[]{passenger});
            return PacketPlayOutMount.a.decode(packetDataSerializer);
        });
        return Collections.singletonList(packetPlayOutMount);
    }

    private List<Packet<?>> getBackPackHelmetPacket(ItemStack itemStack) {
        ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> list = new ArrayList<>();
        list.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(itemStack)));
        return Collections.singletonList(new PacketPlayOutEntityEquipment(armorStand.an(), list));
    }

    private List<Packet<?>> getBackPackHelmetPacket(ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> pairs) {
        return Collections.singletonList(new PacketPlayOutEntityEquipment(armorStand.an(), pairs));
    }

    @Override
    public void lookEntity(float yaw, float pitch, boolean all) {
        Player owner = getPlayer();
        if(owner == null) return;
        if(all) {
            for (UUID uuid : viewers) {
                Player player = Bukkit.getPlayer(uuid);
                if(player == null) {
                    viewers.remove(uuid);
                    continue;
                }
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

    private List<Packet<?>> getBackPackRotationPackets(float yaw) {
        PacketPlayOutEntityHeadRotation packetPlayOutEntityHeadRotation = new PacketPlayOutEntityHeadRotation(armorStand, (byte) (yaw * 256 / 360));
        PacketPlayOutEntity.PacketPlayOutEntityLook packetPlayOutEntityLook = new PacketPlayOutEntity.PacketPlayOutEntityLook(armorStand.an(), (byte) (yaw * 256 / 360), /*(byte) (pitch * 256 / 360)*/(byte)0, true);
        return Arrays.asList(packetPlayOutEntityHeadRotation, packetPlayOutEntityLook);
    }

    private void sendPackets(Player player, List<Packet<?>> packets) {
        final ChannelPipeline pipeline = getPrivateChannelPipeline(((CraftPlayer) player).getHandle().c);
        if(pipeline == null) return;
        for(Packet<?> packet : packets)
            pipeline.write(packet);
        pipeline.flush();
    }

    private ChannelPipeline getPrivateChannelPipeline(PlayerConnection playerConnection) {
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        if(plugin.getServer().getPluginManager().isPluginEnabled("Denizen")){
            String className = "com.denizenscript.denizen.nms.v1_20.impl.network.handlers.DenizenNetworkManagerImpl";
            String methodName = "getConnection";
            try {
                Class<?> clazz = Class.forName(className);
                Class<?>[] typeParameters = { EntityPlayer.class };
                Method method = clazz.getMethod(methodName, typeParameters);
                Object[] parameters = { playerConnection.f };
                NetworkManager result = (NetworkManager) method.invoke(null, parameters);
                return result.n.pipeline();
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            Field privateNetworkManager = ServerCommonPacketListenerImpl.class.getDeclaredField("e");
            privateNetworkManager.setAccessible(true);
            NetworkManager networkManager = (NetworkManager) privateNetworkManager.get(playerConnection);
            return networkManager.n.pipeline();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Bukkit.getLogger().severe("Error: Channel Pipeline not found");
            return null;
        }
    }
}
