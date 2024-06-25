package com.francobm.magicosmetics.models;

import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public abstract class PacketReader {

    public abstract void injectPlayer(Player player);

    public abstract void removePlayer(Player player);

    protected Object getValue(Object instance, String name){
        Object result = null;
        try {
            Field field = instance.getClass().getDeclaredField(name);
            field.setAccessible(true);
            result = field.get(instance);
            field.setAccessible(false);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return result;
    }
}
