package com.francobm.magicosmetics.utils;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class OffsetModel {
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;

    public OffsetModel(Location location, float yaw, float pitch){
        this(location.getX(), location.getY(), location.getZ(), yaw, pitch);
    }

    public OffsetModel(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public Vector getBukkitVector(){
        return new Vector(x, y, z);
    }
}
