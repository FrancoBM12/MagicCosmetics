package com.francobm.magicosmetics.nms.v1_20_R1.cache;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.nms.IRangeManager;
import com.francobm.magicosmetics.nms.bag.PlayerBag;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Transformation;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelPipeline;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityAreaEffectCloud;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.item.ItemDisplayContext;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayerBagDisplayHandler extends PlayerBag {
    //private final EntityArmorStand armorStand;
    private final Display.ItemDisplay itemDisplay;
    private final double distance;

    public PlayerBagDisplayHandler(Player p, IRangeManager rangeManager, double distance, float height, ItemStack backPackItem, ItemStack backPackItemForMe){
        hideViewers = new CopyOnWriteArrayList<>(new ArrayList<>());
        this.uuid = p.getUniqueId();
        this.distance = distance;
        //Bukkit.getLogger().info("Height: " + height);
        this.height = height;
        this.ids = new ArrayList<>();
        this.backPackItem = backPackItem;
        this.backPackItemForMe = backPackItemForMe;
        this.rangeManager = rangeManager;
        Player player = getPlayer();
        WorldServer world = ((CraftWorld) player.getWorld()).getHandle();
        itemDisplay = new Display.ItemDisplay(EntityTypes.ae, world);
        itemDisplay.a(CraftItemStack.asNMSCopy(backPackItem));
        itemDisplay.a(ItemDisplayContext.f);
        itemDisplay.a(new Transformation(new Vector3f(0f, height, 0f), new Quaternionf(), new Vector3f(.6f, .6f, .6f), new Quaternionf()));

        itemDisplay.b(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), 0);
        /*armorStand = new EntityArmorStand(EntityTypes.d, world);
        armorStand.b(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), 0);
        armorStand.j(true); //Invisible
        armorStand.m(true); //Invulnerable
        armorStand.u(true); //Marker*/
    }

    @Override
    public void spawn(Player player) {
        Player owner = getPlayer();
        if(owner == null) return;
        Location location = owner.getLocation();
        itemDisplay.b(location.getX(), location.getY(), location.getZ(), location.getYaw(), 0);
        //armorStand.b(location.getX(), location.getY(), location.getZ(), location.getYaw(), 0);

        sendPackets(player, getBackPackSpawn());
        addPassenger(player, lendEntityId == -1 ? owner.getEntityId() : lendEntityId, itemDisplay.af());
        setItemOnHelmet(player, backPackItem);
    }

    @Override
    public void spawnSelf(Player player) {
        Player owner = getPlayer();
        if(owner == null) return;
        Location location = owner.getLocation();
        /*armorStand.j(true); //Invisible true
        armorStand.u(true); //Marker
        armorStand.b(location.getX(), location.getY(), location.getZ(), location.getYaw(), 0);*/
        itemDisplay.b(location.getX(), location.getY(), location.getZ(), location.getYaw(), 0);

        sendPackets(player, getBackPackSpawn());
        if(height > 0){
            for(int i = 0; i < height; i++) {
                EntityAreaEffectCloud entityAreaEffectCloud = new EntityAreaEffectCloud(EntityTypes.c, ((CraftWorld)player.getWorld()).getHandle());
                entityAreaEffectCloud.a(0f);
                entityAreaEffectCloud.j(true);
                entityAreaEffectCloud.b(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
                sendPackets(player, getCloudsSpawn(entityAreaEffectCloud));
                ids.add(entityAreaEffectCloud.af());
            }
            for(int i = 0; i < height; i++) {
                if(i == 0){
                    addPassenger(player, lendEntityId == -1 ? player.getEntityId() : lendEntityId, ids.get(i));
                    continue;
                }
                addPassenger(player, ids.get(i - 1), ids.get(i));
            }
            addPassenger(player, ids.get(ids.size() - 1), itemDisplay.af());
        }else{
            addPassenger(player, lendEntityId == -1 ? owner.getEntityId() : lendEntityId, itemDisplay.af());
        }
        setItemOnHelmet(player, backPackItemForMe == null ? backPackItem : backPackItemForMe);
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
        List<Packet<?>> backPack = getBackPackMountPacket(lendEntityId == -1 ? getPlayer().getEntityId() : lendEntityId, itemDisplay.af());

    }

    @Override
    public void addPassenger(Player player, int entity, int passenger) {
        sendPackets(player, getBackPackMountPacket(entity, passenger));
    }

    @Override
    public void setItemOnHelmet(Player player, ItemStack itemStack) {
        sendPackets(player, getBackPackHelmetPacket(itemStack));
    }

    private List<Packet<?>> getBackPackSpawn() {
        PacketPlayOutSpawnEntity spawnEntity = new PacketPlayOutSpawnEntity(itemDisplay);
        PacketPlayOutEntityMetadata entityMetadata = new PacketPlayOutEntityMetadata(itemDisplay.af(), itemDisplay.aj().c());
        return Arrays.asList(spawnEntity, entityMetadata);
    }

    private List<Packet<?>> getCloudsSpawn(EntityAreaEffectCloud entityAreaEffectCloud) {
        PacketPlayOutSpawnEntity spawnEntity = new PacketPlayOutSpawnEntity(entityAreaEffectCloud);
        PacketPlayOutEntityMetadata entityMetadata = new PacketPlayOutEntityMetadata(entityAreaEffectCloud.af(), entityAreaEffectCloud.aj().c());
        return Arrays.asList(spawnEntity, entityMetadata);
    }

    private List<Packet<?>> getBackPackDismount(boolean removeClouds) {
        List<Packet<?>> packets = new ArrayList<>();
        if(!removeClouds) {
            PacketPlayOutEntityDestroy backPackDestroy = new PacketPlayOutEntityDestroy(itemDisplay.af());
            return Collections.singletonList(backPackDestroy);
        }
        for (Integer id : ids) {
            packets.add(new PacketPlayOutEntityDestroy(id));
        }
        packets.add(new PacketPlayOutEntityDestroy(itemDisplay.af()));
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
        return Collections.singletonList(new PacketPlayOutEntityEquipment(itemDisplay.af(), list));
    }

    private List<Packet<?>> getBackPackHelmetPacket(ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> pairs) {
        return Collections.singletonList(new PacketPlayOutEntityEquipment(itemDisplay.af(), pairs));
    }

    @Override
    public void lookEntity(float yaw, float pitch, boolean all) {
        Player owner = getPlayer();
        if(owner == null) return;

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
        return itemDisplay.getBukkitEntity();
    }

    private List<Packet<?>> getBackPackRotationPackets(float yaw) {
        PacketPlayOutEntityHeadRotation packetPlayOutEntityHeadRotation = new PacketPlayOutEntityHeadRotation(itemDisplay, (byte) (yaw * 256 / 360));
        PacketPlayOutEntity.PacketPlayOutEntityLook packetPlayOutEntityLook = new PacketPlayOutEntity.PacketPlayOutEntityLook(itemDisplay.af(), (byte) (yaw * 256 / 360), /*(byte) (pitch * 256 / 360)*/(byte)0, true);
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
                Object[] parameters = { playerConnection.b };
                NetworkManager result = (NetworkManager) method.invoke(null, parameters);
                return result.m.pipeline();
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            Field privateNetworkManager = playerConnection.getClass().getDeclaredField("h");
            privateNetworkManager.setAccessible(true);
            NetworkManager networkManager = (NetworkManager) privateNetworkManager.get(playerConnection);
            return networkManager.m.pipeline();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Bukkit.getLogger().severe("Error: Channel Pipeline not found");
            return null;
        }
    }
}
