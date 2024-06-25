package com.francobm.magicosmetics.utils;

import org.bukkit.util.Vector;

public class Offset {

    public static Vector rotateYaw(Vector vec, double yaw) {
        double sin = Math.sin(yaw);
        double cos = Math.cos(yaw);
        double x = vec.getX() * cos - vec.getZ() * sin;
        double z = vec.getX() * sin + vec.getZ() * cos;
        return vec.setX(x).setZ(z);
    }

    public static Vector rotatePitch(Vector vec, double pitch) {
        double sin = Math.sin(pitch);
        double cos = Math.cos(pitch);
        double y = vec.getY() * cos - vec.getZ() * sin;
        double z = vec.getY() * sin + vec.getZ() * cos;
        return vec.setY(y).setZ(z);
    }
}
