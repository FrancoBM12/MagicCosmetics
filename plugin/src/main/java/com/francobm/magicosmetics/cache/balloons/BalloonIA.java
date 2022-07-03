package com.francobm.magicosmetics.cache.balloons;

import dev.lone.itemsadder.api.CustomEntity;
import org.bukkit.Color;
import org.bukkit.Location;

public class BalloonIA {
    private CustomEntity customEntity;
    private final String id;

    public BalloonIA(String id){
        this.id = id;
    }

    public void spawn(Location location){
        if(customEntity != null){
            customEntity.getEntity().remove();
        }
        customEntity = CustomEntity.spawn(id, location);
        customEntity.teleport(location);
        customEntity.getEntity().setGravity(false);
        customEntity.getEntity().setInvulnerable(true);
        customEntity.getEntity().setSilent(true);
    }

    public void colorizePart(Color color){
        if(customEntity == null) return;
        customEntity.getBone(0).setColor(color.asRGB());
    }

    public void remove(){
        if(customEntity != null){
            customEntity.destroy();
            customEntity = null;
        }
    }

    public String getId() {
        return id;
    }

    public CustomEntity getCustomEntity() {
        return customEntity;
    }
}
