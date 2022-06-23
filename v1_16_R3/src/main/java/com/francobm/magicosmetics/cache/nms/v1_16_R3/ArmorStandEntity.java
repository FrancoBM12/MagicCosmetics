package com.francobm.magicosmetics.cache.nms.v1_16_R3;

import net.minecraft.server.v1_16_R3.EntityArmorStand;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.Vector3f;
import net.minecraft.server.v1_16_R3.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Player;

public class ArmorStandEntity extends EntityArmorStand {

    private Location location;
    private final Player player;
    private boolean floatLoop;
    private double y;

    public ArmorStandEntity(Player player) {
        super(EntityTypes.ARMOR_STAND, ((CraftWorld)player.getWorld()).getHandle());
        super.setInvulnerable(true);
        super.setInvisible(true);
        super.setMarker(true);
        setHeadPose(new Vector3f(0,0,0));
        this.location = player.getLocation();
        this.player = player;
    }

    @Override
    public void tick() {
        update();
        super.tick();
        this.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public void update(){
        location = player.getLocation();
        location.add(0, y, 0);
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
