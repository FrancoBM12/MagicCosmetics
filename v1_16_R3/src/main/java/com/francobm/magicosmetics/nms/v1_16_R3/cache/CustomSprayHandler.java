package com.francobm.magicosmetics.nms.v1_16_R3.cache;

import com.francobm.magicosmetics.nms.spray.CustomSpray;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class CustomSprayHandler extends CustomSpray {
    private final EntityItemFrame itemFrame;
    private final Location location;
    private final net.minecraft.server.v1_16_R3.ItemStack itemStack;
    private final EnumDirection enumDirection;
    private final MapView mapView;
    private final int rotation;

    public CustomSprayHandler(Player player, Location location, BlockFace blockFace, ItemStack itemStack, MapView mapView, int rotation) {
        players = new CopyOnWriteArrayList<>(new ArrayList<>());
        this.uuid = player.getUniqueId();
        customSprays.put(uuid, this);
        WorldServer world = ((CraftWorld)player.getWorld()).getHandle();
        this.enumDirection = getEnumDirection(blockFace);
        itemFrame = new EntityItemFrame(EntityTypes.ITEM_FRAME, world);
        this.entity = (ItemFrame) itemFrame.getBukkitEntity();
        this.location = location;
        this.itemStack = CraftItemStack.asNMSCopy(itemStack);
        this.mapView = mapView;
        this.rotation = rotation;
        itemFrame.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
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
        itemFrame.setInvisible(true); //Invisible
        itemFrame.setInvulnerable(true); //Invulnerable
        itemFrame.setItem(itemStack, true, false);

        itemFrame.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        itemFrame.setDirection(enumDirection);
        itemFrame.setRotation(rotation);
        PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutSpawnEntity(itemFrame, enumDirection.c()));
        connection.sendPacket(new PacketPlayOutEntityMetadata(itemFrame.getId(), itemFrame.getDataWatcher(), true));
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
            if(!players.contains(player.getUniqueId())) continue;
            remove(player);
        }
        customSprays.remove(uuid);
    }

    @Override
    public void remove(Player player) {
        PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutEntityDestroy(itemFrame.getId()));
        players.remove(player.getUniqueId());
    }

    private EnumDirection getEnumDirection(BlockFace facing){
        switch (facing){
            case NORTH:
            case NORTH_EAST:
            case NORTH_WEST:
            case NORTH_NORTH_EAST:
            case NORTH_NORTH_WEST:
                return EnumDirection.NORTH;
            case SOUTH:
            case SOUTH_EAST:
            case SOUTH_WEST:
            case SOUTH_SOUTH_EAST:
            case SOUTH_SOUTH_WEST:
                return EnumDirection.SOUTH;
            case WEST:
            case WEST_NORTH_WEST:
            case WEST_SOUTH_WEST:
                return EnumDirection.WEST;
            case EAST:
            case EAST_NORTH_EAST:
            case EAST_SOUTH_EAST:
                return EnumDirection.EAST;
            case DOWN:
                return EnumDirection.DOWN;
            default:
                return EnumDirection.UP;
        }
    }
}
