package com.francobm.magicosmetics.cache;

import org.bukkit.Color;
import org.bukkit.entity.Player;

public class SecondaryColor {
    private final Color color;
    private final String permission;

    public SecondaryColor(Color color) {
        this(color, "");
    }
    public SecondaryColor(Color color, String permission) {
        this.color = color;
        this.permission = permission;
    }

    public Color getColor() {
        return color;
    }

    public String getPermission() {
        return permission;
    }

    public boolean hasPermission(Player player){
        if(permission.isEmpty()) return true;
        return player.hasPermission(permission);
    }
}
