package com.francobm.magicosmetics.cache.balloons;

import com.francobm.magicosmetics.MagicCosmetics;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.PartEntity;
import org.bukkit.Location;

import java.util.List;

public class BalloonEngine {
    private ModeledEntity modeledEntity;
    private ActiveModel activeModel;
    private final String modelId;
    private final List<String> colorableParts;
    private final List<String> animationParts;

    public BalloonEngine(String modelId, List<String> colorableParts, List<String> animationParts) {
        this.modelId = modelId;
        this.colorableParts = colorableParts;
        this.animationParts = animationParts;
        this.modeledEntity = null;
        this.activeModel = null;
    }

    public void setState(int state){
        switch (state){
            case 0:
                //activeModel.addState("idle", 1, 1, 1);
                if(activeModel.getStates().contains("idle")) {
                    activeModel.getStates().removeIf(s -> !s.equals("idle"));
                    return;
                }
                activeModel.getStates().clear();
                activeModel.setState(ActiveModel.ModelState.IDLE);
                return;
            case 1:
                if(activeModel.getStates().contains("walk")){
                    activeModel.getStates().removeIf(s -> !s.equals("walk"));
                    return;
                }
                activeModel.getStates().clear();
                activeModel.setState(ActiveModel.ModelState.WALK);
                //activeModel.setState(ActiveModel.ModelState.WALK);
        }
    }

    public void remove(){
        if(activeModel == null) return;
        if(modeledEntity == null) return;
        activeModel.clearModel();
        modeledEntity.clearModels();
        modeledEntity.removeModel(modelId);
        setActiveModel(null);
        setModeledEntity(null);
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

    public List<String> getColorableParts() {
        return colorableParts;
    }

    public List<String> getAnimationParts() {
        return animationParts;
    }
}
