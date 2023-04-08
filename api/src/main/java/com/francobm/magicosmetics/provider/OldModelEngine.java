package com.francobm.magicosmetics.provider;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import org.bukkit.Color;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.HashSet;
import java.util.Set;

public class OldModelEngine extends  ModelEngine {
    private final ModelEngineAPI modelEngineAPI;

    public boolean existAnimation(String modelId, String animationName) {
        if(ModelEngineAPI.getModelBlueprint(modelId) == null) return false;
        return ModelEngineAPI.getModelBlueprint(modelId).getAnimation(animationName) != null;
    }

    public OldModelEngine(){
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

    @Override
    public void stopAnimations(ActiveModel activeModel) {
        activeModel.getStates().clear();
    }

    @Override
    public void stopAnimationExcept(ActiveModel activeModel, String animationId) {
        activeModel.getStates().removeIf(s -> !s.equals(animationId));
    }

    @Override
    public boolean isPlayingAnimation(ActiveModel activeModel, String animationId) {
        return activeModel.getStates().contains(animationId);
    }

    @Override
    public void playAnimation(ActiveModel activeModel, String animationId) {
        activeModel.addState(animationId, 1, 1, 1);
    }

    @Override
    public void removeModeledEntity(ModeledEntity modeledEntity, ActiveModel activeModel) {
        activeModel.getStates().clear();
        activeModel.canRemove();
        modeledEntity.removeModel(activeModel.getModelId());
        modeledEntity.getEntity().remove();
        modeledEntity.clearModels();
    }

    @Override
    public ModeledEntity spawnModel(LivingEntity entity, String modelId, ActiveModel activeModel) {
        ModeledEntity modeledEntity = ModelEngineAPI.api.getModelManager().createModeledEntity(entity);
        activeModel.setClamp(0);
        activeModel.getStates().clear();
        activeModel.setDamageTint(false);
        modeledEntity.setInvisible(true);
        modeledEntity.addActiveModel(activeModel);
        return modeledEntity;
    }

    @Override
    public void detectPlayers(ModeledEntity modeledEntity) {
        modeledEntity.detectPlayers();
    }

    @Override
    public Set<String> getAllBonesIds(ActiveModel activeModel) {
        return new HashSet<>(activeModel.getBlueprint().getAllBoneIds());
    }

    @Override
    public void tint(ActiveModel activeModel, Color color, String boneId) {
        activeModel.setTint(color, boneId, true);
    }
}
