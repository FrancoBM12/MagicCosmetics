package com.francobm.magicosmetics.cache.cosmetics.balloons;

import dev.lone.itemsadder.api.CustomEntity;
import org.bukkit.Color;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class BalloonIA {
    private CustomEntity customEntity;
    private final String modelId;
    private final List<String> colorParts;
    private final String walk_animation;
    private final String idle_animation;

    public BalloonIA(String modelId, List<String> colorParts, String walk_animation, String idle_animation) {
        this.modelId = modelId;
        this.colorParts = colorParts;
        this.walk_animation = walk_animation == null ? "walk" : walk_animation;
        this.idle_animation = idle_animation == null ? "idle" : idle_animation;
    }

    public BalloonIA getClone() {
        return new BalloonIA(modelId, new ArrayList<>(colorParts), walk_animation, idle_animation);
    }

    public void spawn(Location location){
        if(customEntity != null){
            customEntity.getEntity().remove();
        }
        customEntity = CustomEntity.spawn(modelId, location, false, true, true);
    }

    public void paintBalloon(Color color) {
        if(colorParts.isEmpty()){
            customEntity.setColorAllBones(color.asRGB());
            return;
        }
        for(CustomEntity.Bone bone : customEntity.getBones()){
            if(!colorParts.contains(bone.getName())) continue;
            bone.setColor(color.asRGB());
        }
    }

    public void setState(int state){
        switch (state){
            case 0:
                if(!getCustomEntity().hasAnimation(idle_animation)) return;
                if(getCustomEntity().isPlayingAnimation(idle_animation)){
                    getCustomEntity().stopAnimation();
                    return;
                }
                getCustomEntity().playAnimation(idle_animation);
                break;
            case 1:
                if(!getCustomEntity().hasAnimation(walk_animation)) return;
                if(getCustomEntity().isPlayingAnimation(walk_animation)){
                    getCustomEntity().stopAnimation();
                    return;
                }
                getCustomEntity().playAnimation(walk_animation);
                break;
        }
    }

    public void remove(){
        if(customEntity != null){
            customEntity.destroy();
            customEntity = null;
        }
    }

    public String getModelId() {
        return modelId;
    }

    public CustomEntity getCustomEntity() {
        return customEntity;
    }
}
