package com.francobm.magicosmetics.cache.cosmetics.balloons;

import com.francobm.magicosmetics.MagicCosmetics;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import org.bukkit.Color;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BalloonEngine {
    private ModeledEntity modeledEntity;
    private ActiveModel activeModel;
    private final String modelId;
    private final List<String> colorParts;
    private final String walk_animation;
    private final String idle_animation;
    private LivingEntity entity;

    public BalloonEngine(String modelId, List<String> colorParts, String walk_animation, String idle_animation) {
        this.modelId = modelId;
        this.colorParts = colorParts;
        this.walk_animation = walk_animation == null ? "walk" : walk_animation;
        this.idle_animation = idle_animation == null ? "idle" : idle_animation;
        this.modeledEntity = null;
        this.activeModel = null;
    }

    public BalloonEngine getClone() {
        return new BalloonEngine(modelId, new ArrayList<>(colorParts), walk_animation, idle_animation);
    }

    public void setState(int state){
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        switch (state){
            case 0:
                if(!plugin.getModelEngine().existAnimation(modelId, idle_animation)) return;
                //activeModel.addState("idle", 1, 1, 1);
                if(plugin.getModelEngine().isPlayingAnimation(activeModel, idle_animation)) {
                    plugin.getModelEngine().stopAnimationExcept(activeModel, idle_animation);
                    return;
                }
                plugin.getModelEngine().stopAnimations(activeModel);
                plugin.getModelEngine().playAnimation(activeModel, idle_animation);
                return;
            case 1:
                if(!MagicCosmetics.getInstance().getModelEngine().existAnimation(modelId, walk_animation)) return;
                if(plugin.getModelEngine().isPlayingAnimation(activeModel, walk_animation)){
                    plugin.getModelEngine().stopAnimationExcept(activeModel, walk_animation);
                    return;
                }
                plugin.getModelEngine().stopAnimations(activeModel);
                plugin.getModelEngine().playAnimation(activeModel, walk_animation);
                //activeModel.setState(ActiveModel.ModelState.WALK);
        }
    }

    public void remove(){
        if(activeModel == null) return;
        if(modeledEntity == null) return;
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        plugin.getModelEngine().removeModeledEntity(modeledEntity, activeModel);
        setActiveModel(null);
        setModeledEntity(null);
    }

    public void detectPlayers() {
        if(modeledEntity == null) return;
        MagicCosmetics.getInstance().getModelEngine().detectPlayers(modeledEntity);
    }

    public Set<String> getBones() {
        if(activeModel == null) return null;
        return MagicCosmetics.getInstance().getModelEngine().getAllBonesIds(activeModel);
    }

    public void tintModel(Color color) {
        if(activeModel == null) return;
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        for (String id : getBones()) {
            if (getColorParts() != null && !getColorParts().isEmpty()) {
                if (!getColorParts().contains(id)) continue;
            }
            plugin.getModelEngine().tint(activeModel, color, id);
        }
    }

    public ActiveModel getActiveModel() {
        return activeModel;
    }

    public ModeledEntity getModeledEntity() {
        return modeledEntity;
    }

    public void setActiveModel(ActiveModel activeModel) {
        this.activeModel = activeModel;
    }

    public void setModeledEntity(ModeledEntity modeledEntity) {
        this.modeledEntity = modeledEntity;
    }

    public String getModelId() {
        return modelId;
    }

    public List<String> getColorParts() {
        return colorParts;
    }

    public void setEntity(LivingEntity entity) {
        this.entity = entity;
    }

    public LivingEntity getEntity() {
        return entity;
    }
}
