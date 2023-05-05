package com.francobm.magicosmetics.provider;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.state.ModelState;
import com.ticxo.modelengine.api.entity.Dummy;
import com.ticxo.modelengine.api.generator.model.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.nms.entity.fake.BoneRenderer;
import com.ticxo.modelengine.api.nms.entity.impl.ManualRangeManager;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
            if(!animationProperty.getName().equalsIgnoreCase(animationId))
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
        activeModel.destroy();
        modeledEntity.destroy();
        ModelEngineAPI.removeModeledEntity(modeledEntity.getBase().getUniqueId());
    }

    @Override
    public ModeledEntity spawnModel(String modelId, Location location) {
        Dummy dummy = ModelEngineAPI.createDummy();
        dummy.setMoving(true);
        dummy.setLocation(location);
        dummy.wrapMoveControl();
        dummy.wrapLookControl();
        dummy.wrapBodyRotationControl();
        dummy.wrapNavigation();
        dummy.setYBodyRot(location.getYaw());
        dummy.setYHeadRot(location.getYaw());
        dummy.setXHeadRot(location.getPitch());
        ModeledEntity modeledEntity = ModelEngineAPI.createModeledEntity(dummy);
        dummy.setRangeManager(new ManualRangeManager(dummy, modeledEntity));
        ActiveModel activeModel = ModelEngineAPI.createActiveModel(modelId);
        activeModel.setHurt(false);
        activeModel.setCanHurt(false);
        modeledEntity.addModel(activeModel, false);
        modeledEntity.setState(ModelState.IDLE);
        modeledEntity.setRenderRadius(50);
        modeledEntity.getRangeManager().setRenderDistance(50);
        //modeledEntity.getBodyRotationController().setBodyClampUneven(false);
        return modeledEntity;
    }

    @Override
    public void detectPlayers(ModeledEntity modeledEntity, List<UUID> playerList) {
        for(UUID uniqueId : playerList) {
            Player player = Bukkit.getPlayer(uniqueId);
            if(player == null) continue;
            modeledEntity.getRangeManager().forceSpawn(player);
        }
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


    @Override
    public void movementModel(ModeledEntity modeledEntity, Location location) {
        Dummy dummy = (Dummy) modeledEntity.getBase();
        dummy.setLocation(location);
    }
}
