package com.francobm.magicosmetics.cache;

import com.francobm.magicosmetics.files.FileCreator;
import com.francobm.magicosmetics.MagicCosmetics;

import java.util.HashMap;
import java.util.Map;

public class Sound {
    public static Map<String, Sound> sounds = new HashMap<>();
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

    public static Sound getSound(String id){
        return sounds.get(id);
    }

    public static void loadSounds(){
        sounds.clear();
        FileCreator soundsConfig = MagicCosmetics.getInstance().getSounds();
        for(String key : soundsConfig.getConfigurationSection("sounds").getKeys(false)){

            String soundCustom = "";
            org.bukkit.Sound soundBukkit = null;
            float yaw = 1;
            float pitch = 0.5f;
            if(soundsConfig.contains("sounds." + key + ".type")) {
                String sound = soundsConfig.getString("sounds." + key + ".type");
                try{
                    soundBukkit = org.bukkit.Sound.valueOf(sound.toUpperCase());
                }catch (IllegalArgumentException exception){
                    MagicCosmetics.getInstance().getLogger().info("Sound '" + sound + "' not Found in Bukkit... Transform custom");
                    soundCustom = sound;
                }
            }
            if(soundsConfig.contains("sounds." + key + ".yaw")) {
                yaw = (float) soundsConfig.getDouble("sounds." + key + ".yaw");
            }
            if(soundsConfig.contains("sounds." + key + ".pitch")) {
                pitch = (float) soundsConfig.getDouble("sounds." + key + ".pitch");
            }
            sounds.put(key, new Sound(key, soundCustom, soundBukkit, yaw, pitch));
        }
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
