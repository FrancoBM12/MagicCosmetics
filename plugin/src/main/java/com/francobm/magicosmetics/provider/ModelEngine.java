package com.francobm.magicosmetics.provider;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import org.bukkit.entity.Entity;

public class ModelEngine {
    private final ModelEngineAPI modelEngineAPI;

    public ModelEngine(){
        this.modelEngineAPI = ModelEngineAPI.api;
    }

    public ModelEngineAPI getModelEngineAPI() {
        return modelEngineAPI;
    }

    public ModeledEntity createModeledEntity(Entity entity){
        return modelEngineAPI.getModelManager().createModeledEntity(entity);
    }

    public ActiveModel createActiveModel(String modelId){
        return modelEngineAPI.getModelManager().createActiveModel(modelId);
    }

    public void coloredActiveModel(){
    }
}
