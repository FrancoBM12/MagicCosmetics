package com.francobm.magicosmetics.cache.nms.v1_18_R1;

import net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.PufferFish;
import org.bukkit.util.Vector;

public class ArmorStandEntity extends EntityArmorStand {

    private Location location;
    private final Player player;
    private PufferFish leashed;
    protected boolean floatLoop = true;
    protected double y = 0;
    protected double height = 0;
    protected boolean heightLoop = true;
    protected double rotate = -0.008;
    protected double rot = 0;
    protected boolean rotateLoop = true;
    protected double space;


    public ArmorStandEntity(Player player, double space) {
        super(EntityTypes.c, ((CraftWorld)player.getWorld()).getHandle());
        this.space = space;
        super.j(true);
        super.m(true);
        super.t(true);
        this.location = player.getLocation();
        this.player = player;
        leashed = player.getWorld().spawn(player.getLocation(), PufferFish.class);
        this.b(leashed.getLocation().getX(), leashed.getLocation().getY(), leashed.getLocation().getZ(), leashed.getLocation().getYaw(), leashed.getLocation().getPitch());
        j(true); //Invisible
        m(true); //Invulnerable
        t(true); //Marker
        //
        leashed.setPuffState(-1);
        leashed.setLeashHolder(player);
        leashed.setCanPickupItems(false);
        leashed.setRemoveWhenFarAway(false);
        leashed.setAI(false);
        leashed.setPuffState(-2);
        leashed.setGravity(false);
        leashed.setCollidable(false);
        leashed.setInvisible(true);
        leashed.setInvulnerable(true);
        leashed.setSilent(true);
        leashed.setLeashHolder(player);
        update();
    }

    public void spawn(){
        ((CraftWorld)player.getWorld()).getHandle().b(this);
    }

    public Entity getEntity(){
        return this.getBukkitEntity();
    }

    public ArmorStand getRealEntity(){
        return (ArmorStand) this.getBukkitEntity();
    }

    public void remove(){
        this.getBukkitEntity().remove();
    }

    @Override
    public void k() {
        super.k();
        update();
        super.a(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        leashed.teleport(this.getBukkitEntity().getLocation().clone().subtract(0, 1.3, 0));
        leashed.setLeashHolder(player);
    }

    private final double SQUARED_WALKING = 12.5;
    private final double SQUARED_RUN = 12.4;
    private final double SQUARED_DISTANCE = 20;

    private final double CATCH_UP_INCREMENTS = .27; //.25
    private double CATCH_UP_INCREMENTS_DISTANCE = CATCH_UP_INCREMENTS; //.25
    public void update(){
        if(leashed == null) return;
        Location playerLoc = player.getLocation().clone().add(0, space + 1.0 + 1.3, 0);
        Location stand = leashed.getLocation();
        Vector standDir = player.getEyeLocation().clone().subtract(stand).toVector();
        if(player.getLocation().distanceSquared(stand) > SQUARED_WALKING){
            Vector lineBetween = playerLoc.clone().subtract(stand).toVector();
            if (!standDir.equals(new Vector())) {
                standDir.normalize();
            }
            Vector distVec = lineBetween.clone().normalize().multiply(CATCH_UP_INCREMENTS_DISTANCE);
            Location standTo = stand.clone().setDirection(standDir.setY(0)).add(distVec.clone());
            location = standTo.clone();
            //this.b(newLocation.getX(), newLocation.getY(), newLocation.getZ(), newLocation.getYaw(), newLocation.getPitch());
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
                ((ArmorStand)this.getBukkitEntity()).setHeadPose(((ArmorStand)this.getBukkitEntity()).getHeadPose().add(0.007, 0, rotate));
                if (rot > 0.25) {
                    rotateLoop = true;
                }
            } else {
                rot -= 0.01;
                ((ArmorStand)this.getBukkitEntity()).setHeadPose(((ArmorStand)this.getBukkitEntity()).getHeadPose().add(0, 0, rotate).subtract(0.007, 0, 0));
                if (rot < -0.25) {
                    rotateLoop = false;
                }
            }
            location = standToLoc.clone();
            //this.b(newLocation.getX(), newLocation.getY(), newLocation.getZ(), newLocation.getYaw(), newLocation.getPitch());
        }

        if(player.getLocation().distanceSquared(stand) > SQUARED_RUN){
            if(!heightLoop){
                height += 0.01;
                ((ArmorStand)this.getBukkitEntity()).setHeadPose(((ArmorStand)this.getBukkitEntity()).getHeadPose().subtract(0.022, 0, 0));
                if(height > 0.10) heightLoop = true;
            }
        }else{
            if (heightLoop) {
                height -= 0.01;
                ((ArmorStand)this.getBukkitEntity()).setHeadPose(((ArmorStand)this.getBukkitEntity()).getHeadPose().add(0.022, 0, 0));
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

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
