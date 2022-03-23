package com.francobm.magicosmetics.cache.balloons;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;

import java.util.List;

public class BalloonEngine {
    private ModeledEntity modeledEntity;
    private ActiveModel activeModel;
    private final String modelId;
    private final List<String> colorableParts;
    private boolean walk;
    private boolean iddle;

    public BalloonEngine(String modelId, List<String> colorableParts){
        this.modelId = modelId;
        this.colorableParts = colorableParts;
        this.modeledEntity = null;
        this.activeModel = null;
    }

    public void setIddle(){
        if(activeModel == null) return;
        if(isIddle()) return;
        activeModel.setState(ActiveModel.ModelState.IDLE);
        setIddle(true);
        setWalk(false);
    }

    public void setWalk(){
        if(activeModel == null) return;
        if(isWalk()) return;
        activeModel.setState(ActiveModel.ModelState.WALK);
        setIddle(false);
        setWalk(true);
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

    public boolean isWalk() {
        return walk;
    }

    public boolean isIddle() {
        return iddle;
    }

    public void setIddle(boolean iddle) {
        this.iddle = iddle;
    }

    public void setWalk(boolean walk) {
        this.walk = walk;
    }
}
