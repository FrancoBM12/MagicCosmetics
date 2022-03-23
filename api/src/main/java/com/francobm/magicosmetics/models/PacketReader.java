package com.francobm.magicosmetics.models;

import java.lang.reflect.Field;

public abstract class PacketReader {

    public abstract boolean inject();

    public abstract void unject();

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
