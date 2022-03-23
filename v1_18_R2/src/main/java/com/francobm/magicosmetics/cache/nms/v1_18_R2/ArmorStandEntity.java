package com.francobm.magicosmetics.cache.nms.v1_18_R2;

import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.entity.Player;

public class ArmorStandEntity extends EntityArmorStand {

    private Location location;
    private final Player player;
    private boolean floatLoop;
    private double y;
    private double height = 0;
    private boolean heightLoop;


    public ArmorStandEntity(Player player) {
        super(EntityTypes.c, ((CraftWorld)player.getWorld()).getHandle());
        super.j(true);
        super.m(true);
        super.t(true);
        this.location = player.getLocation();
        this.player = player;
    }

    public void spawn(){
        ((CraftWorld)player.getWorld()).getHandle().b(this);
    }

    @Override
    public void k() {
        update();
        super.k();
        super.a(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public void update(){
        location = player.getLocation();
        Location p = player.getLocation().getBlock().getLocation().clone();
        Location as = this.getBukkitEntity().getLocation().getBlock().getLocation().clone();
        p.setY(0);
        as.setY(0);
        if(!p.equals(as)){
            if(!heightLoop){
                height += 0.01;

                if(height > 0.10) heightLoop = true;
            }
        }
        if (!floatLoop) {
            y += 0.01;
            location.add(0, 0.01, 0);
            //location.add(0, 0.01, 0);
            //location.setYaw(location.getYaw() + 7.5f);
            //leashedLoc.setYaw(leashedLoc.getYaw() - 3f);
            //armorStand.teleport(location);
            if (y > 0.10) floatLoop = true;
        } else {
            y -= 0.01;
            location.subtract(0, 0.01, 0);
            //leashedLoc.setYaw(leashedLoc.getYaw() + 3f);
            //location.subtract(0, 0.01, 0);
            //location.setYaw(location.getYaw() - 7.5f);
            //armorStand.teleport(location);
            if (y < (-0.10 + 0)) floatLoop = false;
        }
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
