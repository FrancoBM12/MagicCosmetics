package com.francobm.magicosmetics.provider;

import com.francobm.magicosmetics.cache.cosmetics.backpacks.FakeBackpack;
import com.francobm.magicosmetics.cache.cosmetics.balloons.FakeBalloon;
import com.francobm.magicosmetics.utils.OffsetModel;
import com.francobm.magicosmetics.utils.PositionModelType;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.blueprint.LoopMode;
import com.ticxo.modelengine.api.animation.state.ModelState;
import com.ticxo.modelengine.api.entity.Dummy;
import com.ticxo.modelengine.api.generator.model.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.Leashable;
import com.ticxo.modelengine.api.model.bone.Renderer;
import com.ticxo.modelengine.api.nms.entity.fake.BoneRenderer;
import com.ticxo.modelengine.api.nms.entity.wrapper.RangeManager;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ModelEngine3 extends ModelEngine{

    @Override
    public boolean existAnimation(String modelId, String animationName) {
        ModelBlueprint modelBlueprint = ModelEngineAPI.getBlueprint(modelId);
        return modelBlueprint != null && modelBlueprint.getAnimations().containsKey(animationName);
    }

    @Override
    public void loopAnimation(UUID balloonUniqueId, String modelId, String animationName) {
        ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(balloonUniqueId);
        if(modeledEntity == null) return;
        ActiveModel activeModel = modeledEntity.getModel(modelId);
        if(activeModel == null) return;
        activeModel.getAnimationHandler().getAnimation(animationName).setForceLoopMode(LoopMode.LOOP);
    }

    @Override
    public ModelEngineAPI getModelEngineAPI() {
        return ModelEngineAPI.api;
    }

    @Override
    public void stopAnimations(UUID balloonUniqueId, String modelId) {
        ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(balloonUniqueId);
        if(modeledEntity == null) return;
        ActiveModel activeModel = modeledEntity.getModel(modelId);
        if(activeModel == null) return;
        activeModel.getAnimationHandler().forceStopAllAnimations();
    }

    @Override
    public void stopAnimationExcept(UUID balloonUniqueId, String modelId, String animationId) {
        ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(balloonUniqueId);
        if(modeledEntity == null) return;
        ActiveModel activeModel = modeledEntity.getModel(modelId);
        if(activeModel == null) return;
        activeModel.getAnimationHandler().getAnimations().forEach(animationProperty -> {
            if(!animationProperty.getName().equalsIgnoreCase(animationId))
                animationProperty.stop();
        });
    }

    @Override
    public boolean isPlayingAnimation(UUID balloonUniqueId, String modelId, String animationId) {
        ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(balloonUniqueId);
        if(modeledEntity == null) return false;
        ActiveModel activeModel = modeledEntity.getModel(modelId);
        if(activeModel == null) return false;
        return activeModel.getAnimationHandler().isPlayingAnimation(animationId);
    }

    @Override
    public void playAnimation(UUID balloonUniqueId, String modelId, String animationId) {
        ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(balloonUniqueId);
        if(modeledEntity == null) return;
        ActiveModel activeModel = modeledEntity.getModel(modelId);
        if(activeModel == null) return;
        activeModel.getAnimationHandler().playAnimation(animationId, 1, 1, 1, false);
    }

    @Override
    public void removeModeledEntity(UUID balloonUniqueId, String modelId) {
        ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(balloonUniqueId);
        if(modeledEntity == null) return;
        ActiveModel activeModel = modeledEntity.getModel(modelId);
        if(activeModel == null) return;
        activeModel.destroy();
        modeledEntity.destroy();
        ModelEngineAPI.removeModeledEntity(balloonUniqueId);
    }

    @Override
    public void removeBackPack(UUID balloonUniqueId, String modelId) {
        ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(balloonUniqueId);
        if(modeledEntity == null) return;
        ActiveModel activeModel = modeledEntity.getModel(modelId);
        if(activeModel == null) return;
        RangeManager rangeManager = modeledEntity.getRangeManager();
        if(rangeManager instanceof RangeManager.Disguise){
            RangeManager.Disguise disguise = (RangeManager.Disguise) modeledEntity.getRangeManager();
            for(Player player : disguise.getViewers()) {
                rangeManager.removePlayer(player);
                activeModel.hideFromPlayer(player);
            }
        }else {
            for (Player player : rangeManager.getPlayerInRange()) {
                rangeManager.removePlayer(player);
                activeModel.hideFromPlayer(player);
            }
        }
        activeModel.destroy();
        ModelEngineAPI.removeModeledEntity(balloonUniqueId);
    }

    @Override
    public UUID spawnModel(Entity entity, String modelId, Location location, OffsetModel offsetModel) {
        FakeBalloon fake = new FakeBalloon((LivingEntity) entity, offsetModel, PositionModelType.HEAD);
        ModeledEntity modeledEntity = ModelEngineAPI.createModeledEntity(fake);
        fake.wrapRangeManager(modeledEntity);
        fake.wrapNavigation();
        fake.wrapBodyRotationControl();
        fake.wrapNavigation();
        fake.wrapLookControl();
        fake.wrapMoveControl();
        fake.setYBodyRot(location.getYaw());
        fake.setYHeadRot(location.getYaw());
        ActiveModel activeModel = ModelEngineAPI.createActiveModel(modelId);

        activeModel.setCanHurt(false);
        modeledEntity.setBaseEntityVisible(false);
        modeledEntity.setModelRotationLock(false);
        modeledEntity.addModel(activeModel, false);
        modeledEntity.setState(ModelState.IDLE);
        modeledEntity.setRenderRadius(50);
        modeledEntity.getRangeManager().setRenderDistance(50);
        //modeledEntity.getBodyRotationController().setBodyClampUneven(false);
        return fake.getUniqueId();
    }

    @Override
    public ModeledEntity spawnModelBackPack(Entity entity, String modelId, Location location, OffsetModel offsetModel, PositionModelType positionModelType) {
        ModelBlueprint blueprint = ModelEngineAPI.getBlueprint(modelId);
        ActiveModel activeModel = ModelEngineAPI.createActiveModel(blueprint);
        activeModel.getAnimationHandler().playAnimation("idle", 1.0, 0, 1.0, true);
        activeModel.getAnimationHandler().getAnimation("idle").setForceLoopMode(LoopMode.LOOP);
        activeModel.setCanHurt(false);
        ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(entity.getUniqueId());
        if(modeledEntity == null){
            modeledEntity = ModelEngineAPI.createModeledEntity(new FakeBackpack(entity, offsetModel, positionModelType));
            modeledEntity.setBaseEntityVisible(true);
            if(entity instanceof Player)
                ModelEngineAPI.getEntityHandler().setSelfFakeInvisible((Player) entity, false);
            RangeManager rangeManager = modeledEntity.getRangeManager();
            if(rangeManager instanceof RangeManager.Disguise){
                RangeManager.Disguise disguise = (RangeManager.Disguise) rangeManager;
                disguise.setIncludeSelf(true);
            }
        }
        modeledEntity.destroy();
        modeledEntity.addModel(activeModel, false);
        return modeledEntity;
    }

    @Override
    public void spawnLeash(Entity entity, UUID balloonUniqueId, String modelId) {
        ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(balloonUniqueId);
        if(modeledEntity == null) return;
        ActiveModel activeModel = modeledEntity.getModel(modelId);
        if(activeModel == null) return;
        for (Leashable leash : activeModel.getLeashHandler().getBones().values()) {
            leash.spawn();
            leash.setHolder(entity.getEntityId());
        }
    }

    @Override
    public Set<Player> getTrackedPlayers(UUID balloonUniqueId) {
        ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(balloonUniqueId);
        if(modeledEntity == null) return new HashSet<>();
        return modeledEntity.getRangeManager().getPlayerInRange();
    }

    @Override
    public void hideModel(UUID balloonUniqueId, Player player) {
        ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(balloonUniqueId);
        if(modeledEntity == null) return;
        modeledEntity.getRangeManager().removePlayer(player);
    }

    @Override
    public void showModel(UUID balloonUniqueId, Player player) {
        ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(balloonUniqueId);
        if(modeledEntity == null) return;
        modeledEntity.getRangeManager().forceSpawn(player);
    }

    @Override
    public Set<String> getAllBonesIds(UUID balloonUniqueId, String modelId) {
        ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(balloonUniqueId);
        if(modeledEntity == null) return new HashSet<>();
        ActiveModel activeModel = modeledEntity.getModel(modelId);
        if(activeModel == null) return new HashSet<>();
        return activeModel.getBoneIndex().keySet();
    }

    @Override
    public void tint(UUID balloonUniqueId, String modelId, Color color, String boneId) {
        ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(balloonUniqueId);
        if(modeledEntity == null) return;
        ActiveModel activeModel = modeledEntity.getModel(modelId);
        if(activeModel == null) return;
        for(Map.Entry<String, BoneRenderer> boneRenderer : activeModel.getRendererHandler().getFakeEntity().entrySet()) {
            if(!boneRenderer.getKey().equalsIgnoreCase(boneId)) continue;
            boneRenderer.getValue().setColor(color);
            boneRenderer.getValue().updateModel();
        }
    }

    @Override
    public void movementModel(UUID balloonUniqueId, Location location) {
        ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(balloonUniqueId);
        if(modeledEntity == null) return;
        Dummy dummy = (Dummy) modeledEntity.getBase();
        dummy.setLocation(location);
        //dummy.setXHeadRot(location.getPitch());
        //dummy.setYBodyRot(location.getYaw());
        //dummy.setYHeadRot(location.getYaw());
    }
}
