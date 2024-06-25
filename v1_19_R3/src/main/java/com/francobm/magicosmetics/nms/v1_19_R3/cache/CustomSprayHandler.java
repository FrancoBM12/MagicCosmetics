package com.francobm.magicosmetics.nms.v1_19_R3.cache;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.nms.spray.CustomSpray;
import io.netty.channel.ChannelPipeline;
import net.minecraft.core.EnumDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.decoration.EntityItemFrame;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class CustomSprayHandler extends CustomSpray {
    private final EntityItemFrame itemFrame;
    private final Location location;
    private final net.minecraft.world.item.ItemStack itemStack;
    private final EnumDirection enumDirection;
    private final MapView mapView;
    private final int rotation;

    public CustomSprayHandler(Player player, Location location, BlockFace blockFace, ItemStack itemStack, MapView mapView, int rotation) {
        players = new CopyOnWriteArrayList<>(new ArrayList<>());
        this.uuid = player.getUniqueId();
        customSprays.put(uuid, this);
        WorldServer world = ((CraftWorld)player.getWorld()).getHandle();
        this.enumDirection = getEnumDirection(blockFace);
        itemFrame = new EntityItemFrame(EntityTypes.af, world);
        this.entity = (ItemFrame) itemFrame.getBukkitEntity();
        this.location = location;
        this.itemStack = CraftItemStack.asNMSCopy(itemStack);
        this.mapView = mapView;
        this.rotation = rotation;
        itemFrame.a(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    @Override
    public void spawn(Player player) {
        if(players.contains(player.getUniqueId())) {
            if(!player.getWorld().equals(location.getWorld())) {
                remove(player);
            }
            return;
        }
        if(!player.getWorld().equals(location.getWorld())) return;
        itemFrame.j(true); //Invisible
        itemFrame.m(true); //Invulnerable
        itemFrame.setItem(itemStack, true, false);

        itemFrame.a(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        itemFrame.a(enumDirection);
        itemFrame.b(rotation);
        sendPackets(player, spawnItemFrame());
        if(mapView != null) {
            player.sendMap(mapView);
        }
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
        customSprays.remove(uuid);
    }

    @Override
    public void remove(Player player) {
        sendPackets(player, Collections.singletonList(destroyItemFrame()));
        players.remove(player.getUniqueId());
    }

    private EnumDirection getEnumDirection(BlockFace facing){
        switch (facing){
            case NORTH:
                return EnumDirection.c;
            case SOUTH:
                return EnumDirection.d;
            case WEST:
                return EnumDirection.e;
            case EAST:
                return EnumDirection.f;
            case DOWN:
                return EnumDirection.a;
            default:
                return EnumDirection.b;
        }
    }

    private List<Packet<?>> spawnItemFrame() {
        PacketPlayOutSpawnEntity spawnEntity = new PacketPlayOutSpawnEntity(itemFrame, enumDirection.d());
        PacketPlayOutEntityMetadata entityMetadata = new PacketPlayOutEntityMetadata(itemFrame.af(), itemFrame.aj().c());
        return Arrays.asList(spawnEntity, entityMetadata);
    }

    private Packet<?> destroyItemFrame() {
        return new PacketPlayOutEntityDestroy(itemFrame.af());
    }

    private void sendPackets(Player player, List<Packet<?>> packets) {
        final ChannelPipeline pipeline = getPrivateChannelPipeline(((CraftPlayer) player).getHandle().b);
        if(pipeline == null) return;
        for(Packet<?> packet : packets)
            pipeline.write(packet);
        pipeline.flush();
    }

    private ChannelPipeline getPrivateChannelPipeline(PlayerConnection playerConnection) {
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        if(plugin.getServer().getPluginManager().isPluginEnabled("Denizen")){
            String className = "com.denizenscript.denizen.nms.v1_19.impl.network.handlers.DenizenNetworkManagerImpl";
            String methodName = "getConnection";
            try {
                Class<?> clazz = Class.forName(className);
                Class<?>[] typeParameters = { EntityPlayer.class };
                Method method = clazz.getMethod(methodName, typeParameters);
                Object[] parameters = { playerConnection.b };
                NetworkManager result = (NetworkManager) method.invoke(null, parameters);
                return result.m.pipeline();
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                return null;
            }
        }
        try {
            Field privateNetworkManager = playerConnection.getClass().getDeclaredField("h");
            privateNetworkManager.setAccessible(true);
            NetworkManager networkManager = (NetworkManager) privateNetworkManager.get(playerConnection);
            return networkManager.m.pipeline();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }
}
