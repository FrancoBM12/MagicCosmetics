package com.francobm.magicosmetics.cache;
public class Sound {
    private final String id;
    private final String soundCustom;
    private final org.bukkit.Sound soundBukkit;
    private final float yaw;
    private final float pitch;

    public Sound(String id, String soundCustom, org.bukkit.Sound soundBukkit, float yaw, float pitch){
        this.id = id;
        this.soundCustom = soundCustom;
        this.soundBukkit = soundBukkit;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public String getId() {
        return id;
    }

    public String getSoundCustom() {
        return soundCustom;
    }

    public boolean isCustom(){
        return !soundCustom.isEmpty();
    }

    public org.bukkit.Sound getSoundBukkit() {
        return soundBukkit;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }
}
