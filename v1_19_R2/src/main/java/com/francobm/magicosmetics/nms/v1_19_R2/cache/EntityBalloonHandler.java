package com.francobm.magicosmetics.nms.v1_19_R2.cache;

import com.francobm.magicosmetics.cache.RotationType;
import com.francobm.magicosmetics.nms.balloon.EntityBalloon;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Vector3f;
import net.minecraft.network.protocol.game.*;
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
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class EntityBalloonHandler extends EntityBalloon {
    private final EntityArmorStand armorStand;
    private final EntityLiving leashed;
    private final double distance;
    private final double SQUARED_WALKING;
    private final double SQUARED_DISTANCE;

    public EntityBalloonHandler(Entity entity, double space, double distance, boolean bigHead, boolean invisibleLeash) {
        players = new CopyOnWriteArrayList<>(new ArrayList<>());
        this.uuid = entity.getUniqueId();
        this.distance = distance;
        this.invisibleLeash = invisibleLeash;
        entitiesBalloon.put(uuid, this);
        this.entity = (LivingEntity) entity;
        WorldServer world = ((CraftWorld)entity.getWorld()).getHandle();

        Location location = entity.getLocation().clone().add(0, space, 0);
        location = location.clone().add(entity.getLocation().clone().getDirection().multiply(-1));
        armorStand = new EntityArmorStand(EntityTypes.d, world);
        armorStand.b(location.getX(), location.getY() - 1.3, location.getZ(), location.getYaw(), location.getPitch());
        armorStand.j(true); //Invisible
        armorStand.m(true); //Invulnerable
        armorStand.t(true); //Marker
        this.bigHead = bigHead;
        if(isBigHead()){
            armorStand.d(new Vector3f(armorStand.cj.b(), 0, 0));
        }
        leashed = new EntityPufferFish(EntityTypes.ax, world);
        leashed.collides = false;
        leashed.j(true); //Invisible
        leashed.m(true); //Invulnerable
        leashed.b(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        this.space = space;
        this.SQUARED_WALKING = 5.5 * space;
        this.SQUARED_DISTANCE = 10 * space;
    }

    @Override
    public void spawn(Player player) {
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

        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        entityPlayer.b.a(new PacketPlayOutSpawnEntity(armorStand));
        armorStand.al().refresh(entityPlayer);

        entityPlayer.b.a(new PacketPlayOutSpawnEntity(leashed));
        leashed.al().refresh(entityPlayer);
        if(!invisibleLeash) {
            entityPlayer.b.a(new PacketPlayOutAttachEntity(leashed, ((CraftEntity) getEntity()).getHandle()));
        }
        //client settings
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
        entitiesBalloon.remove(uuid);
    }

    @Override
    public void remove(Player player) {
        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        connection.a(new PacketPlayOutEntityDestroy(armorStand.ah()));
        connection.a(new PacketPlayOutEntityDestroy(leashed.ah()));
        players.remove(player.getUniqueId());
    }

    @Override
    public void setItem(ItemStack itemStack) {
        if(isBigHead()){
            setItemBigHead(itemStack);
            return;
        }
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) {
                players.remove(uuid);
                continue;
            }
            PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
            ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> list = new ArrayList<>();
            list.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(itemStack)));
            connection.a(new PacketPlayOutEntityEquipment(armorStand.ah(), list));
        }
    }

    public void setItemBigHead(ItemStack itemStack) {
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) {
                players.remove(uuid);
                continue;
            }
            PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
            ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> list = new ArrayList<>();
            list.add(new Pair<>(EnumItemSlot.a, CraftItemStack.asNMSCopy(itemStack)));
            connection.a(new PacketPlayOutEntityEquipment(armorStand.ah(), list));
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
            PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
            connection.a(new PacketPlayOutEntityHeadRotation(armorStand, (byte) (yaw * 256 / 360)));
            connection.a(new PacketPlayOutEntity.PacketPlayOutEntityLook(armorStand.ah(), (byte) (yaw * 256 / 360), /*(byte) (pitch * 256 / 360)*/(byte)0, true));
            connection.a(new PacketPlayOutEntityHeadRotation(leashed, (byte) (yaw * 256 / 360)));
            connection.a(new PacketPlayOutEntity.PacketPlayOutEntityLook(leashed.ah(), (byte) (yaw * 256 / 360), /*(byte) (pitch * 256 / 360)*/(byte)0, true));
        }
    }

    private final double CATCH_UP_INCREMENTS = .27; //.25
    private double CATCH_UP_INCREMENTS_DISTANCE = CATCH_UP_INCREMENTS;
    @Override
    public void update(){
        if(isBigHead()){
            updateBigHead();
            return;
        }
        LivingEntity owner = getEntity();
        if(armorStand == null) return;
        if(leashed == null) return;
        Location playerLoc = owner.getLocation().clone().add(0, space, 0);
        Location stand = leashed.getBukkitEntity().getLocation();
        Vector standDir = owner.getEyeLocation().clone().subtract(stand).toVector();
        Location distance2 = stand.clone();
        Location distance1 = owner.getLocation().clone();

        if(distance1.distanceSquared(distance2) > SQUARED_WALKING){
            Vector lineBetween = playerLoc.clone().subtract(stand).toVector();
            if (!standDir.equals(new Vector())) {
                standDir.normalize();
            }
            Vector distVec = lineBetween.clone().normalize().multiply(CATCH_UP_INCREMENTS_DISTANCE);
            Location standTo = stand.clone().setDirection(standDir.setY(0)).add(distVec.clone());
            Location newLocation = standTo.clone();
            leashed.a(newLocation.getX(), newLocation.getY(), newLocation.getZ(), newLocation.getYaw(), newLocation.getPitch());
            armorStand.a(newLocation.getX(), newLocation.getY() - 1.3, newLocation.getZ(), newLocation.getYaw(), newLocation.getPitch());
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
                if (y < (-0.11 + 0)) {
                    floatLoop = false;
                    rotate *= -1;
                }
            }

            if (!rotateLoop) {
                rot += 0.01;
                armorStand.a(new Vector3f(armorStand.u().b() - 0.5f, armorStand.u().c(), armorStand.u().d() + rotate));
                //armorStand.setHeadPose(armorStand.getHeadPose().add(0, 0, rotate).subtract(0.008, 0, 0));
                if (rot > 0.20) {
                    rotateLoop = true;
                }
            } else {
                rot -= 0.01;
                armorStand.a(new Vector3f(armorStand.u().b() + 0.5f, armorStand.u().c(), armorStand.u().d() + rotate));
                //armorStand.setHeadPose(armorStand.getHeadPose().add(0.008, 0, rotate));//.subtract(0.006, 0, 0));
                if (rot < -0.20) {
                    rotateLoop = false;
                }
            }
            Location newLocation = standToLoc.clone();
            leashed.a(newLocation.getX(), newLocation.getY(), newLocation.getZ(), newLocation.getYaw(), newLocation.getPitch());
            armorStand.a(newLocation.getX(), newLocation.getY() - 1.3, newLocation.getZ(), newLocation.getYaw(), newLocation.getPitch());
        }
        for(UUID uuid : players){
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) {
                players.remove(uuid);
                continue;
            }
            EntityPlayer p = ((CraftPlayer)player).getHandle();
            if(!invisibleLeash) {
                p.b.a(new PacketPlayOutAttachEntity(leashed, ((CraftEntity) getEntity()).getHandle()));
            }
            armorStand.al().refresh(p);
            p.b.a(new PacketPlayOutEntityTeleport(leashed));
            p.b.a(new PacketPlayOutEntityTeleport(armorStand));
        }

        if(distance1.distanceSquared(distance2) > SQUARED_WALKING){
            if(!heightLoop){
                height += 0.01;
                armorStand.a(new Vector3f(armorStand.u().b() - 0.8f, armorStand.u().c(), armorStand.u().d()));
                //((ArmorStand)armorStand.getBukkitEntity()).setHeadPose(((ArmorStand)armorStand.getBukkitEntity()).getHeadPose().subtract(0.022, 0, 0));
                if(height > 0.10) heightLoop = true;
            }
        }else{
            if (heightLoop) {
                height -= 0.01;
                armorStand.a(new Vector3f(armorStand.u().b() + 0.8f, armorStand.u().c(), armorStand.u().d()));
                //((ArmorStand)armorStand.getBukkitEntity()).setHeadPose(((ArmorStand)armorStand.getBukkitEntity()).getHeadPose().add(0.022, 0, 0));
                if (height < (-0.10 + 0)) heightLoop = false;
                return;
            }

        }
        if(distance1.distanceSquared(distance2) > SQUARED_DISTANCE){
            CATCH_UP_INCREMENTS_DISTANCE += 0.01;
        }else{
            CATCH_UP_INCREMENTS_DISTANCE = CATCH_UP_INCREMENTS;
        }
    }

    public void updateBigHead(){
        LivingEntity owner = getEntity();
        if(armorStand == null) return;
        if(leashed == null) return;
        Location playerLoc = owner.getLocation().clone().add(0, space, 0);
        Location stand = leashed.getBukkitEntity().getLocation();
        Vector standDir = owner.getEyeLocation().clone().subtract(stand).toVector();
        Location distance2 = stand.clone();
        Location distance1 = owner.getLocation().clone();

        if(distance1.distanceSquared(distance2) > SQUARED_WALKING){
            Vector lineBetween = playerLoc.clone().subtract(stand).toVector();
            if (!standDir.equals(new Vector())) {
                standDir.normalize();
            }
            Vector distVec = lineBetween.clone().normalize().multiply(CATCH_UP_INCREMENTS_DISTANCE);
            Location standTo = stand.clone().setDirection(standDir.setY(0)).add(distVec.clone());
            Location newLocation = standTo.clone();
            leashed.a(newLocation.getX(), newLocation.getY(), newLocation.getZ(), newLocation.getYaw(), newLocation.getPitch());
            armorStand.a(newLocation.getX(), newLocation.getY() - 1.3, newLocation.getZ(), newLocation.getYaw(), newLocation.getPitch());
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
                if (y < (-0.11 + 0)) {
                    floatLoop = false;
                    rotate *= -1;
                }
            }

            if (!rotateLoop) {
                rot += 0.01;
                armorStand.d(new Vector3f(armorStand.cj.b() - 0.5f, armorStand.cj.c(), armorStand.cj.d() + rotate));
                //armorStand.setHeadPose(armorStand.getHeadPose().add(0, 0, rotate).subtract(0.008, 0, 0));
                if (rot > 0.20) {
                    rotateLoop = true;
                }
            } else {
                rot -= 0.01;
                armorStand.d(new Vector3f(armorStand.cj.b() + 0.5f, armorStand.cj.c(), armorStand.cj.d() + rotate));
                //armorStand.setHeadPose(armorStand.getHeadPose().add(0.008, 0, rotate));//.subtract(0.006, 0, 0));
                if (rot < -0.20) {
                    rotateLoop = false;
                }
            }
            Location newLocation = standToLoc.clone();
            leashed.a(newLocation.getX(), newLocation.getY(), newLocation.getZ(), newLocation.getYaw(), newLocation.getPitch());
            armorStand.a(newLocation.getX(), newLocation.getY() - 1.3, newLocation.getZ(), newLocation.getYaw(), newLocation.getPitch());
        }
        for(UUID uuid : players){
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) {
                players.remove(uuid);
                continue;
            }
            EntityPlayer p = ((CraftPlayer)player).getHandle();
            if(!invisibleLeash) {
                p.b.a(new PacketPlayOutAttachEntity(leashed, ((CraftEntity) getEntity()).getHandle()));
            }
            armorStand.al().refresh(p);
            p.b.a(new PacketPlayOutEntityTeleport(leashed));
            p.b.a(new PacketPlayOutEntityTeleport(armorStand));
        }

        if(distance1.distanceSquared(distance2) > SQUARED_WALKING){
            if(!heightLoop){
                height += 0.01;
                armorStand.d(new Vector3f(armorStand.cj.b() - 0.8f, armorStand.cj.c(), armorStand.cj.d()));
                //((ArmorStand)armorStand.getBukkitEntity()).setHeadPose(((ArmorStand)armorStand.getBukkitEntity()).getHeadPose().subtract(0.022, 0, 0));
                if(height > 0.10) heightLoop = true;
            }
        }else{
            if (heightLoop) {
                height -= 0.01;
                armorStand.d(new Vector3f(armorStand.cj.b() + 0.8f, armorStand.cj.c(), armorStand.cj.d()));
                //((ArmorStand)armorStand.getBukkitEntity()).setHeadPose(((ArmorStand)armorStand.getBukkitEntity()).getHeadPose().add(0.022, 0, 0));
                if (height < (-0.10 + 0)) heightLoop = false;
                return;
            }

        }
        if(distance1.distanceSquared(distance2) > SQUARED_DISTANCE){
            CATCH_UP_INCREMENTS_DISTANCE += 0.01;
        }else{
            CATCH_UP_INCREMENTS_DISTANCE = CATCH_UP_INCREMENTS;
        }
    }

    @Override
    public void rotate(boolean rotation, RotationType rotationType, float rotate) {
        if(isBigHead()){
            rotateBigHead(rotation, rotationType, rotate);
            return;
        }
        if(!rotation) return;
        switch (rotationType){
            case RIGHT:
                armorStand.a(new Vector3f(armorStand.u().b(), armorStand.u().c() + rotate, armorStand.u().d()));
                break;
            case UP:
                armorStand.a(new Vector3f(armorStand.u().b() + rotate, armorStand.u().c(), armorStand.u().d()));
                break;
            case ALL:
                armorStand.a(new Vector3f(armorStand.u().b() + rotate, armorStand.u().c() + rotate, armorStand.u().d()));
                break;
        }
        for(UUID uuid : players){
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) {
                players.remove(uuid);
                continue;
            }
            armorStand.al().refresh(((CraftPlayer)player).getHandle());
        }
    }

    public void rotateBigHead(boolean rotation, RotationType rotationType, float rotate) {
        if(!rotation) return;
        switch (rotationType){
            case RIGHT:
                armorStand.d(new Vector3f(armorStand.cj.b(), armorStand.cj.c() + rotate, armorStand.cj.d()));
                break;
            case UP:
                armorStand.d(new Vector3f(armorStand.cj.b() + rotate, armorStand.cj.c(), armorStand.cj.d()));
                break;
            case ALL:
                armorStand.d(new Vector3f(armorStand.cj.b() + rotate, armorStand.cj.c() + rotate, armorStand.cj.d()));
                break;
        }
        for(UUID uuid : players){
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) {
                players.remove(uuid);
                continue;
            }
            armorStand.al().refresh(((CraftPlayer)player).getHandle());
        }
    }

    public double getDistance() {
        return distance;
    }
}
