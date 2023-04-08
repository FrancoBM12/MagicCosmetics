package com.francobm.magicosmetics.provider;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.state.ModelState;
import com.ticxo.modelengine.api.generator.model.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.nms.entity.fake.BoneRenderer;
import org.bukkit.Color;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;

public class NewModelEngine extends ModelEngine{

    @Override
    public boolean existAnimation(String modelId, String animationName) {
        ModelBlueprint modelBlueprint = ModelEngineAPI.getBlueprint(modelId);
        return modelBlueprint != null && modelBlueprint.getAnimations().containsKey(animationName);
    }

    @Override
    public ModelEngineAPI getModelEngineAPI() {
        return ModelEngineAPI.api;
    }

    @Override
    public ModeledEntity createModeledEntity(Entity entity) {
        return ModelEngineAPI.createModeledEntity(entity);
    }

    @Override
    public ActiveModel createActiveModel(String modelId) {
        return ModelEngineAPI.createActiveModel(modelId);
    }

    @Override
    public void stopAnimations(ActiveModel activeModel) {
        activeModel.getAnimationHandler().forceStopAllAnimations();
    }

    @Override
    public void stopAnimationExcept(ActiveModel activeModel, String animationId) {
        activeModel.getAnimationHandler().getAnimations().forEach(animationProperty -> {
            if(!animationProperty.getBlueprintAnimation().getName().equalsIgnoreCase(animationId))
                animationProperty.stop();
        });
    }

    @Override
    public boolean isPlayingAnimation(ActiveModel activeModel, String animationId) {
        return activeModel.getAnimationHandler().isPlayingAnimation(animationId);
    }

    @Override
    public void playAnimation(ActiveModel activeModel, String animationId) {
        activeModel.getAnimationHandler().playAnimation(animationId, 1, 1, 1, false);
    }

    @Override
    public void removeModeledEntity(ModeledEntity modeledEntity, ActiveModel activeModel) {
        modeledEntity.destroy();
        for(Player p : modeledEntity.getRangeManager().getPlayerInRange()) {
            modeledEntity.getRangeManager().removePlayer(p);
        }
        ModelEngineAPI.removeModeledEntity(modeledEntity.getBase().getUniqueId());
    }

    @Override
    public ModeledEntity spawnModel(LivingEntity entity, String modelId, ActiveModel activeModel) {
        activeModel.setHurt(false);
        activeModel.setCanHurt(false);
        ModeledEntity modeledEntity = ModelEngineAPI.createModeledEntity(entity);
        modeledEntity.addModel(activeModel, false);
        modeledEntity.setRenderRadius(100);
        modeledEntity.setState(ModelState.IDLE);
        modeledEntity.getRangeManager().setRenderDistance(100);
        //modeledEntity.getBodyRotationController().setBodyClampUneven(false);
        return modeledEntity;
    }

    @Override
    public void detectPlayers(ModeledEntity modeledEntity) {

    }

    @Override
    public Set<String> getAllBonesIds(ActiveModel activeModel) {
        return activeModel.getBoneIndex().keySet();
    }

    @Override
    public void tint(ActiveModel activeModel, Color color, String boneId) {
        for(Map.Entry<String, BoneRenderer> boneRenderer : activeModel.getRendererHandler().getFakeEntity().entrySet()) {
            if(!boneRenderer.getKey().equalsIgnoreCase(boneId)) continue;
            boneRenderer.getValue().setColor(color);
            boneRenderer.getValue().updateModel();
        }
    }
}
