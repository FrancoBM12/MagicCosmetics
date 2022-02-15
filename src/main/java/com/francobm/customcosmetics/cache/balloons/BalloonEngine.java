package com.francobm.customcosmetics.cache.balloons;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;

public class BalloonEngine {
    private ModeledEntity modeledEntity;
    private ActiveModel activeModel;
    private final String modelId;

    public BalloonEngine(String modelId){
        this.modelId = modelId;
        this.modeledEntity = null;
        this.activeModel = null;
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
}
