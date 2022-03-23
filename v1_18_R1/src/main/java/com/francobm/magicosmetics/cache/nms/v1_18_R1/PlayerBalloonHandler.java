package com.francobm.magicosmetics.cache.nms.v1_18_R1;

import com.francobm.magicosmetics.nms.balloon.PlayerBalloon;
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
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.animal.EntityPufferFish;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.UUID;

public class PlayerBalloonHandler extends PlayerBalloon {
    private final EntityArmorStand armorStand;
    private final EntityLiving leashed;

    public PlayerBalloonHandler(Player p, double space) {
        players = new ArrayList<>();
        this.uuid = p.getUniqueId();
        playerBalloons.put(uuid, this);
        Player player = getPlayer();
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        WorldServer world = ((CraftWorld)player.getWorld()).getHandle();

        armorStand = new EntityArmorStand(EntityTypes.c, world);
        armorStand.b(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), 0);

        leashed = new EntityPufferFish(EntityTypes.at, world);
        leashed.b(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), 0);
        this.space = space;
    }

    @Override
    public void spawnBag(Player player) {
        if(players.contains(player.getUniqueId())) return;
        armorStand.j(true); //Invisible
        armorStand.m(true); //Invulnerable
        armorStand.t(true); //Marker
        Location location = getPlayer().getLocation();
        armorStand.b(location.getX(), location.getY(), location.getZ(), location.getYaw(), 0);
        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        connection.a(new PacketPlayOutSpawnEntityLiving(armorStand));

        DataWatcher watcher = armorStand.ai();
        watcher.b(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte)0x20);
        connection.a(new PacketPlayOutEntityMetadata(armorStand.ae(), watcher, true));
        //connection.a(new PacketPlayOutEntityMetadata(armorStand.ae(), armorStand.ai(), true));
        //client settings
        leashed.j(true); //Invisible
        leashed.m(true); //Invulnerable
        ((EntityPufferFish)leashed).b(((CraftPlayer)getPlayer()).getHandle(), true); //leashed
        leashed.b(location.getX(), location.getY(), location.getZ(), location.getYaw(), 0);
        connection.a(new PacketPlayOutSpawnEntityLiving(leashed));

        watcher = leashed.ai();
        watcher.b(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte)0x20);
        connection.a(new PacketPlayOutEntityMetadata(leashed.ae(), watcher, true));
        //connection.a(new PacketPlayOutEntityMetadata(leashed.ae(), leashed.ai(), true));
        //client settings
        players.add(player.getUniqueId());
    }

    public void update(){
        for(UUID uuid : players){
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) continue;
            if(!players.contains(uuid)){
                spawnBag(player);
                continue;
            }
        }
    }

    @Override
    public void spawnBag(boolean marker, boolean all) {
        if(all) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                spawnBag(player);
            }
            return;
        }
        PlayerConnection connection = ((CraftPlayer)getPlayer()).getHandle().b;
        armorStand.j(true); //Invisible
        armorStand.m(true); //Invulnerable
        armorStand.t(marker); //Marker
        connection.a(new PacketPlayOutSpawnEntityLiving(armorStand));

        DataWatcher watcher = armorStand.ai();
        watcher.b(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte)0x20);
        connection.a(new PacketPlayOutEntityMetadata(armorStand.ae(), watcher, true));
        //connection.a(new PacketPlayOutEntityMetadata(armorStand.ae(), armorStand.ai(), true));
        //client settings

        connection.a(new PacketPlayOutSpawnEntityLiving(leashed));
        leashed.j(true); //Invisible
        leashed.m(true); //Invulnerable
        ((EntityPufferFish)leashed).b(((CraftPlayer)getPlayer()).getHandle(), true); //leashed

        watcher = leashed.ai();
        watcher.b(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte)0x20);
        connection.a(new PacketPlayOutEntityMetadata(leashed.ae(), watcher, true));
    }

    @Override
    public void remove(boolean all) {
        if(all){
            for(Player player : Bukkit.getOnlinePlayers()){
                if(!players.contains(player.getUniqueId())) continue;
                remove(player);
            }
            playerBalloons.remove(uuid);
            return;
        }
        remove(getPlayer());
        playerBalloons.remove(uuid);
    }

    @Override
    public void remove(Player player) {
        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        connection.a(new PacketPlayOutEntityDestroy(armorStand.ae()));
        connection.a(new PacketPlayOutEntityDestroy(leashed.ae()));
        players.remove(player.getUniqueId());
    }

    @Override
    public void setItemOnHelmet(ItemStack itemStack, boolean all) {
        if(all) {
            for (UUID uuid : players) {
                Player player = Bukkit.getPlayer(uuid);
                if(player == null) continue;
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
                if(player == null) continue;
                PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
                connection.a(new PacketPlayOutEntityHeadRotation(armorStand, (byte) (yaw * 256 / 360)));
                connection.a(new PacketPlayOutEntity.PacketPlayOutEntityLook(armorStand.ae(), (byte) (yaw * 256 / 360), /*(byte) (pitch * 256 / 360)*/(byte)0, true));
                connection.a(new PacketPlayOutEntityHeadRotation(leashed, (byte) (yaw * 256 / 360)));
                connection.a(new PacketPlayOutEntity.PacketPlayOutEntityLook(leashed.ae(), (byte) (yaw * 256 / 360), /*(byte) (pitch * 256 / 360)*/(byte)0, true));
            }
            return;
        }
        PlayerConnection connection = ((CraftPlayer) getPlayer()).getHandle().b;
        connection.a(new PacketPlayOutEntityHeadRotation(armorStand, (byte) (yaw * 256 / 360)));
        connection.a(new PacketPlayOutEntity.PacketPlayOutEntityLook(armorStand.ae(), (byte) (yaw * 256 / 360), (byte) (pitch * 256 / 360), true));
        connection.a(new PacketPlayOutEntityHeadRotation(leashed, (byte) (yaw * 256 / 360)));
        connection.a(new PacketPlayOutEntity.PacketPlayOutEntityLook(leashed.ae(), (byte) (yaw * 256 / 360), /*(byte) (pitch * 256 / 360)*/(byte)0, true));
    }

    public void lookEntity(Player player, float yaw, float pitch) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
        connection.a(new PacketPlayOutEntityHeadRotation(armorStand, (byte) (yaw * 256 / 360)));
        connection.a(new PacketPlayOutEntity.PacketPlayOutEntityLook(armorStand.ae(), (byte) (yaw * 256 / 360), (byte) (pitch * 256 / 360), true));
        connection.a(new PacketPlayOutEntityHeadRotation(leashed, (byte) (yaw * 256 / 360)));
        connection.a(new PacketPlayOutEntity.PacketPlayOutEntityLook(leashed.ae(), (byte) (yaw * 256 / 360), /*(byte) (pitch * 256 / 360)*/(byte)0, true));
    }

    public void update(boolean all){
        if(all){
            for(UUID uuid : players){
                Player player = Bukkit.getPlayer(uuid);
                if(player == null) continue;
                update(player);
            }
            return;
        }
        update(getPlayer());
    }
    private final double SQUARED_WALKING = 12.5;
    private final double SQUARED_RUN = 12.4;
    private final double SQUARED_DISTANCE = 20;

    private final double CATCH_UP_INCREMENTS = .27; //.25
    private double CATCH_UP_INCREMENTS_DISTANCE = CATCH_UP_INCREMENTS; //.25
    public void update(Player player) {
        if(!players.contains(player.getUniqueId())){
            spawnBag(player);
            return;
        }
        EntityPlayer p = ((CraftPlayer)player).getHandle();
        /*if (armorStand == null) return;
        if (leashed == null) return;
        //Location location = armorStand.getLocation();//getLocalCoord(1, 0, 0, armorStand.getLocation());
        Location pLoc = getPlayer().getLocation().getBlock().getLocation().clone();
        Location as = armorStand.getBukkitEntity().getLocation().getBlock().getLocation().clone();
        pLoc.setY(0);
        as.setY(0);
        ////location.add(player.getLocation().clone().add(0, 1, 0));
        //location.setX(player.getLocation().getX());
        //location.setZ(player.getLocation().getZ());
        //location.setDirection(player.getLocation().getDirection());
        Location bLocation = armorStand.getBukkitEntity().getLocation();
        Location lLocation = leashed.getBukkitEntity().getLocation();
        if (!pLoc.equals(as)) {
            lookEntity(player, getPlayer().getLocation().getYaw(), getPlayer().getLocation().getPitch());
            lLocation.setY(getPlayer().getLocation().getY() + space + 1.0 + 1.3);
            bLocation.setY(getPlayer().getLocation().getY() + space + 1.0 + 1.3);
            if (!heightLoop) {
                height += 0.01;
                ((ArmorStand)armorStand.getBukkitEntity()).setHeadPose(((ArmorStand)armorStand.getBukkitEntity()).getHeadPose().subtract(0.01, 0, 0));

                if (height > 0.10) heightLoop = true;
            }
        } else {
            if (heightLoop) {
                height -= 0.01;
                ((ArmorStand)armorStand.getBukkitEntity()).setHeadPose(((ArmorStand)armorStand.getBukkitEntity()).getHeadPose().add(0.01, 0, 0));
                if (height < (-0.10 + 0)) heightLoop = false;
            }
            lLocation.subtract(0, 1.2, 0);
            if (!floatLoop) {
                y += 0.01;
                armorStand.b(bLocation.getX(), bLocation.getY() + 0.01, bLocation.getZ(), bLocation.getYaw(), bLocation.getPitch());
                leashed.b(lLocation.getX(), lLocation.getY() + 0.01, lLocation.getZ(), lLocation.getYaw(), lLocation.getPitch());
                if (y > 0.10) floatLoop = true;
            } else {
                y -= 0.01;
                armorStand.b(bLocation.getX(), bLocation.getY() - 0.01, bLocation.getZ(), bLocation.getYaw(), bLocation.getPitch());
                leashed.b(lLocation.getX(), lLocation.getY() - 0.01, lLocation.getZ(), lLocation.getYaw(), lLocation.getPitch());
                if (y < (-0.10 + 0)) floatLoop = false;
            }
        }
        p.b.a(new PacketPlayOutEntityTeleport(leashed));
        p.b.a(new PacketPlayOutEntityTeleport(armorStand));
        */
        if(armorStand == null) return;
        if(leashed == null) return;
        Location playerLoc = player.getLocation().clone().add(0, space + 1.0 + 1.3, 0);
        Location stand = leashed.getBukkitEntity().getLocation();
        Vector standDir = player.getEyeLocation().clone().subtract(stand).toVector();
        if(player.getLocation().distanceSquared(stand) > SQUARED_WALKING){
            Vector lineBetween = playerLoc.clone().subtract(stand).toVector();
            if (!standDir.equals(new Vector())) {
                standDir.normalize();
            }
            Vector distVec = lineBetween.clone().normalize().multiply(CATCH_UP_INCREMENTS_DISTANCE);
            Location standTo = stand.clone().setDirection(standDir.setY(0)).add(distVec.clone());
            Location newLocation = standTo.clone();
            leashed.b(newLocation.getX(), newLocation.getY(), newLocation.getZ(), newLocation.getYaw(), newLocation.getPitch());
            armorStand.b(newLocation.getX(), newLocation.getY() - 1.3, newLocation.getZ(), newLocation.getYaw(), newLocation.getPitch());
        }else {
            if (!standDir.equals(new Vector())) {
                standDir.normalize();
            }
            Location standToLoc = stand.clone().setDirection(standDir.setY(0));
            if (!floatLoop) {
                y += 0.01;
                standToLoc.add(0, 0.01, 0);
                //standToLoc.setYaw(standToLoc.getYaw() - 3F);
                if (y > 0.10) {
                    floatLoop = true;
                }
            } else {
                y -= 0.01;
                standToLoc.subtract(0, 0.01, 0);
                //standToLoc.setYaw(standToLoc.getYaw() + 3F);
                if (y < (-0.10 + 0)) {
                    floatLoop = false;
                    rotate *= -1;
                }
            }

            if (!rotateLoop) {
                rot += 0.01;
                ((ArmorStand)armorStand.getBukkitEntity()).setHeadPose(((ArmorStand)armorStand.getBukkitEntity()).getHeadPose().add(0.007, 0, rotate));
                if (rot > 0.25) {
                    rotateLoop = true;
                }
            } else {
                rot -= 0.01;
                ((ArmorStand)armorStand.getBukkitEntity()).setHeadPose(((ArmorStand)armorStand.getBukkitEntity()).getHeadPose().add(0, 0, rotate).subtract(0.007, 0, 0));
                if (rot < -0.25) {
                    rotateLoop = false;
                }
            }
            Location newLocation = standToLoc.clone();
            leashed.b(newLocation.getX(), newLocation.getY(), newLocation.getZ(), newLocation.getYaw(), newLocation.getPitch());
            armorStand.b(newLocation.getX(), newLocation.getY() - 1.3, newLocation.getZ(), newLocation.getYaw(), newLocation.getPitch());
        }
        p.b.a(new PacketPlayOutEntityTeleport(leashed));
        p.b.a(new PacketPlayOutEntityTeleport(armorStand));

        if(player.getLocation().distanceSquared(stand) > SQUARED_RUN){
            if(!heightLoop){
                height += 0.01;
                ((ArmorStand)armorStand.getBukkitEntity()).setHeadPose(((ArmorStand)armorStand.getBukkitEntity()).getHeadPose().subtract(0.022, 0, 0));
                if(height > 0.10) heightLoop = true;
            }
        }else{
            if (heightLoop) {
                height -= 0.01;
                ((ArmorStand)armorStand.getBukkitEntity()).setHeadPose(((ArmorStand)armorStand.getBukkitEntity()).getHeadPose().add(0.022, 0, 0));
                if (height < (-0.10 + 0)) heightLoop = false;
                return;
            }

        }
        if(player.getLocation().distanceSquared(stand) > SQUARED_DISTANCE){
            CATCH_UP_INCREMENTS_DISTANCE += 0.01;
        }else{
            CATCH_UP_INCREMENTS_DISTANCE = CATCH_UP_INCREMENTS;
        }
    }
}
