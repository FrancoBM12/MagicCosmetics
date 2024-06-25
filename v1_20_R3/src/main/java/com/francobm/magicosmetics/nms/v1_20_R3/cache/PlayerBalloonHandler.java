package com.francobm.magicosmetics.nms.v1_20_R3.cache;

import com.francobm.magicosmetics.cache.RotationType;
import com.francobm.magicosmetics.nms.balloon.PlayerBalloon;
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
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayerBalloonHandler extends PlayerBalloon {
    private final EntityArmorStand armorStand;
    private final EntityLiving leashed;
    private final double distance;
    private final double SQUARED_WALKING;
    private final double SQUARED_DISTANCE;

    public PlayerBalloonHandler(Player p, double space, double distance, boolean bigHead, boolean invisibleLeash) {
        viewers = new CopyOnWriteArrayList<>(new ArrayList<>());
        hideViewers = new CopyOnWriteArrayList<>(new ArrayList<>());
        this.uuid = p.getUniqueId();
        this.distance = distance;
        this.invisibleLeash = invisibleLeash;
        playerBalloons.put(uuid, this);
        Player player = getPlayer();
        WorldServer world = ((CraftWorld)player.getWorld()).getHandle();

        Location location = player.getLocation().clone().add(0, space, 0);
        location = location.clone().add(player.getLocation().clone().getDirection().multiply(-1));
        armorStand = new EntityArmorStand(EntityTypes.d, world);
        armorStand.b(location.getX(), location.getY() - 1.3, location.getZ(), location.getYaw(), location.getPitch());
        armorStand.j(true); //Invisible
        armorStand.m(true); //Invulnerable
        armorStand.u(true); //Marker
        this.bigHead = bigHead;
        if(isBigHead()){
            armorStand.d(new Vector3f(armorStand.C().b(), 0, 0));
        }
        leashed = new EntityPufferFish(EntityTypes.aC, world);
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

        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        entityPlayer.c.b(new PacketPlayOutSpawnEntity(armorStand));
        armorStand.an().refresh(entityPlayer);
        //connection.b(new PacketPlayOutEntityMetadatb(armorStand.ae(), armorStand.ai(), true));
        //client settings
        entityPlayer.c.b(new PacketPlayOutSpawnEntity(leashed));
        leashed.an().refresh(entityPlayer);
        if(!invisibleLeash) {
            entityPlayer.c.b(new PacketPlayOutAttachEntity(leashed, lendEntity == null ? ((CraftPlayer) owner).getHandle() : ((CraftLivingEntity)lendEntity).getHandle()));
        }
        //client settings
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
        playerBalloons.remove(uuid);
    }

    @Override
    public void remove(Player player) {
        PlayerConnection connection = ((CraftPlayer)player).getHandle().c;
        connection.b(new PacketPlayOutEntityDestroy(armorStand.aj()));
        connection.b(new PacketPlayOutEntityDestroy(leashed.aj()));
        connection.b(new PacketPlayOutEntityDestroy(leashed.aj()));
        viewers.remove(player.getUniqueId());
    }

    @Override
    public void setItem(ItemStack itemStack) {
        if(isBigHead()) {
            setItemBigHead(itemStack);
            return;
        }
        ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> list = new ArrayList<>();
        list.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(itemStack)));
        for (UUID uuid : viewers) {
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) {
                viewers.remove(uuid);
                continue;
            }
            PlayerConnection connection = ((CraftPlayer)player).getHandle().c;
            connection.b(new PacketPlayOutEntityEquipment(armorStand.aj(), list));
        }
    }

    public void setItemBigHead(ItemStack itemStack) {
        ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> list = new ArrayList<>();
        list.add(new Pair<>(EnumItemSlot.a, CraftItemStack.asNMSCopy(itemStack)));
        for (UUID uuid : viewers) {
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) {
                viewers.remove(uuid);
                continue;
            }
            PlayerConnection connection = ((CraftPlayer)player).getHandle().c;
            connection.b(new PacketPlayOutEntityEquipment(armorStand.aj(), list));
        }
    }

    @Override
    public void lookEntity(float yaw, float pitch) {
        for (UUID uuid : viewers) {
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) {
                viewers.remove(uuid);
                continue;
            }
            PlayerConnection connection = ((CraftPlayer) player).getHandle().c;
            connection.b(new PacketPlayOutEntityHeadRotation(armorStand, (byte) (yaw * 256 / 360)));
            connection.b(new PacketPlayOutEntity.PacketPlayOutEntityLook(armorStand.aj(), (byte) (yaw * 256 / 360), /*(byte) (pitch * 256 / 360)*/(byte)0, true));
            connection.b(new PacketPlayOutEntityHeadRotation(leashed, (byte) (yaw * 256 / 360)));
            connection.b(new PacketPlayOutEntity.PacketPlayOutEntityLook(leashed.aj(), (byte) (yaw * 256 / 360), /*(byte) (pitch * 256 / 360)*/(byte)0, true));
        }
    }

    protected void teleport(Location location) {
        leashed.b(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        armorStand.b(location.getX(), location.getY() - 1.3, location.getZ(), location.getYaw(), location.getPitch());
    }

    protected void instantUpdate() { //implement this method to others versions
        Player owner = getPlayer();
        if(owner == null) return;
        if(armorStand == null) return;
        if(leashed == null) return;
        if(!owner.getWorld().equals(leashed.getBukkitEntity().getWorld())) {
            spawn(false);
            return;
        }
        Location playerLoc = owner.getLocation().clone().add(0, space, 0);
        Location stand = leashed.getBukkitEntity().getLocation().clone();
        Vector standDir = owner.getEyeLocation().clone().subtract(stand).toVector();
        if (!standDir.equals(new Vector())) {
            standDir.normalize();
        }
        Location standToLoc = playerLoc.setDirection(standDir.setY(2));
        //Location standToLoc = owner.getLocation().clone().setDirection(standDir.setY(0));
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
        teleport(standToLoc);
        if (!rotateLoop) {
            rot += 0.02;
            armorStand.a(new Vector3f(armorStand.B().b() - 0.5f, armorStand.B().c(), armorStand.B().d() + rotate));
            //armorStand.setHeadPose(armorStand.getHeadPose().add(0, 0, rotate).subtract(0.008, 0, 0));
            if (rot > 0.20) {
                rotateLoop = true;
            }
        } else {
            rot -= 0.02;
            armorStand.a(new Vector3f(armorStand.B().b() + 0.5f, armorStand.B().c(), armorStand.B().d() + rotate));
            //armorStand.setHeadPose(armorStand.getHeadPose().add(0.008, 0, rotate));//.subtract(0.006, 0, 0));
            if (rot < -0.20) {
                rotateLoop = false;
            }
        }
        if (heightLoop) {
            height -= 0.01;
            armorStand.a(new Vector3f(armorStand.B().b() + 0.8f, armorStand.B().c(), armorStand.B().d()));
            //((ArmorStand)armorStand.getBukkitEntity()).setHeadPose(((ArmorStand)armorStand.getBukkitEntity()).getHeadPose().add(0.022, 0, 0));
            if (height < (-0.10 + 0)) heightLoop = false;
            return;
        }
        for(UUID uuid : viewers){
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) {
                viewers.remove(uuid);
                continue;
            }
            EntityPlayer p = ((CraftPlayer)player).getHandle();
            if(!invisibleLeash) {
                p.c.b(new PacketPlayOutAttachEntity(leashed, lendEntity == null ? ((CraftPlayer) owner).getHandle() : ((CraftLivingEntity)lendEntity).getHandle()));
            }
            armorStand.an().refresh(p);
            p.c.b(new PacketPlayOutEntityTeleport(leashed));
            p.c.b(new PacketPlayOutEntityTeleport(armorStand));
        }
    }

    private final double CATCH_UP_INCREMENTS = .27; //.25
    private double CATCH_UP_INCREMENTS_DISTANCE = CATCH_UP_INCREMENTS;
    @Override
    public void update(boolean instantFollow){
        if(isBigHead()) {
            updateBigHead();
            return;
        }
        if(instantFollow){
            instantUpdate();
            return;
        }
        Player owner = getPlayer();
        if(owner == null) return;
        if(armorStand == null) return;
        if(leashed == null) return;
        if(!owner.getWorld().equals(leashed.getBukkitEntity().getWorld())) {
            spawn(false);
            return;
        }
        Location playerLoc = owner.getLocation().clone().add(0, space, 0);
        Location stand = leashed.getBukkitEntity().getLocation().clone();
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
            teleport(newLocation);
        }else {
            Vector lineBetween = playerLoc.clone().subtract(stand).toVector();
            if (!standDir.equals(new Vector())) {
                standDir.normalize();
            }
            Vector distVec = lineBetween.clone().normalize().multiply(CATCH_UP_INCREMENTS_DISTANCE);
            double distY = distVec.getY();
            if(owner.isSneaking()){
                distY -= 0.13;
            }
            Location standToLoc = stand.clone().setDirection(standDir.setY(0)).add(0, distY, 0);
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
                armorStand.a(new Vector3f(armorStand.B().b() - 0.5f, armorStand.B().c(), armorStand.B().d() + rotate));
                //armorStand.setHeadPose(armorStand.getHeadPose().add(0, 0, rotate).subtract(0.008, 0, 0));
                if (rot > 0.20) {
                    rotateLoop = true;
                }
            } else {
                rot -= 0.01;
                armorStand.a(new Vector3f(armorStand.B().b() + 0.5f, armorStand.B().c(), armorStand.B().d() + rotate));
                //armorStand.setHeadPose(armorStand.getHeadPose().add(0.008, 0, rotate));//.subtract(0.006, 0, 0));
                if (rot < -0.20) {
                    rotateLoop = false;
                }
            }
            Location newLocation = standToLoc.clone();
            teleport(newLocation);
        }
        for(UUID uuid : viewers){
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) {
                viewers.remove(uuid);
                continue;
            }
            EntityPlayer p = ((CraftPlayer)player).getHandle();
            if(!invisibleLeash) {
                p.c.b(new PacketPlayOutAttachEntity(leashed, lendEntity == null ? ((CraftPlayer) owner).getHandle() : ((CraftLivingEntity)lendEntity).getHandle()));
            }
            armorStand.an().refresh(p);
            p.c.b(new PacketPlayOutEntityTeleport(leashed));
            p.c.b(new PacketPlayOutEntityTeleport(armorStand));
        }

        if(distance1.distanceSquared(distance2) > SQUARED_WALKING){
            if(!heightLoop){
                height += 0.01;
                armorStand.a(new Vector3f(armorStand.B().b() - 0.8f, armorStand.B().c(), armorStand.B().d()));
                //((ArmorStand)armorStand.getBukkitEntity()).setHeadPose(((ArmorStand)armorStand.getBukkitEntity()).getHeadPose().subtract(0.022, 0, 0));
                if(height > 0.10) heightLoop = true;
            }
        }else{
            if (heightLoop) {
                height -= 0.01;
                armorStand.a(new Vector3f(armorStand.B().b() + 0.8f, armorStand.B().c(), armorStand.B().d()));
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
        Player owner = getPlayer();
        if(owner == null) return;
        if(armorStand == null) return;
        if(leashed == null) return;
        if(!owner.getWorld().equals(leashed.getBukkitEntity().getWorld())) {
            spawn(false);
            return;
        }
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
            teleport(newLocation);
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
                armorStand.d(new Vector3f(armorStand.C().b() - 0.5f, armorStand.C().c(), armorStand.C().d() + rotate));
                //armorStand.setHeadPose(armorStand.getHeadPose().add(0, 0, rotate).subtract(0.008, 0, 0));
                if (rot > 0.20) {
                    rotateLoop = true;
                }
            } else {
                rot -= 0.01;
                armorStand.d(new Vector3f(armorStand.C().b() + 0.5f, armorStand.C().c(), armorStand.C().d() + rotate));
                //armorStand.setHeadPose(armorStand.getHeadPose().add(0.008, 0, rotate));//.subtract(0.006, 0, 0));
                if (rot < -0.20) {
                    rotateLoop = false;
                }
            }
            Location newLocation = standToLoc.clone();
            teleport(newLocation);
        }
        for(UUID uuid : viewers){
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) {
                viewers.remove(uuid);
                continue;
            }
            EntityPlayer p = ((CraftPlayer)player).getHandle();
            if(!invisibleLeash) {
                p.c.b(new PacketPlayOutAttachEntity(leashed, lendEntity == null ? ((CraftPlayer) owner).getHandle() : ((CraftLivingEntity)lendEntity).getHandle()));
            }
            armorStand.an().refresh(p);
            p.c.b(new PacketPlayOutEntityTeleport(leashed));
            p.c.b(new PacketPlayOutEntityTeleport(armorStand));
        }

        if(distance1.distanceSquared(distance2) > SQUARED_WALKING){
            if(!heightLoop){
                height += 0.01;
                armorStand.d(new Vector3f(armorStand.C().b() - 0.8f, armorStand.C().c(), armorStand.C().d()));
                //((ArmorStand)armorStand.getBukkitEntity()).setHeadPose(((ArmorStand)armorStand.getBukkitEntity()).getHeadPose().subtract(0.022, 0, 0));
                if(height > 0.10) heightLoop = true;
            }
        }else{
            if (heightLoop) {
                height -= 0.01;
                armorStand.d(new Vector3f(armorStand.C().b() + 0.8f, armorStand.C().c(), armorStand.C().d()));
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
        switch (rotationType) {
            case RIGHT:
                armorStand.a(new Vector3f(armorStand.B().b(), armorStand.B().c() + rotate, armorStand.B().d()));
                break;
            case UP:
                armorStand.a(new Vector3f(armorStand.B().b() + rotate, armorStand.B().c(), armorStand.B().d()));
                break;
            case ALL:
                armorStand.a(new Vector3f(armorStand.B().b() + rotate, armorStand.B().c() + rotate, armorStand.B().d()));
                break;
        }
        for(UUID uuid : viewers){
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) {
                viewers.remove(uuid);
                continue;
            }
            armorStand.an().refresh(((CraftPlayer)player).getHandle());
        }
    }

    public void rotateBigHead(boolean rotation, RotationType rotationType, float rotate) {
        if(!rotation) return;
        switch (rotationType){
            case RIGHT:
                armorStand.d(new Vector3f(armorStand.C().b(), armorStand.C().c() + rotate, armorStand.C().d()));
                break;
            case UP:
                armorStand.d(new Vector3f(armorStand.C().b() + rotate, armorStand.C().c(), armorStand.C().d()));
                break;
            case ALL:
                armorStand.d(new Vector3f(armorStand.C().b() + rotate, armorStand.C().c() + rotate, armorStand.C().d()));
                break;
        }
        for(UUID uuid : viewers){
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) {
                viewers.remove(uuid);
                continue;
            }
            armorStand.an().refresh(((CraftPlayer)player).getHandle());
        }
    }

    public double getDistance() {
        return distance;
    }
}
