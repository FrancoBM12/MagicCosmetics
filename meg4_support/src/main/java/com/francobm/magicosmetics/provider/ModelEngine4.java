package com.francobm.magicosmetics.provider;

import com.francobm.magicosmetics.cache.cosmetics.backpacks.FakeBackpack4;
import com.francobm.magicosmetics.utils.OffsetModel;
import com.francobm.magicosmetics.utils.PositionModelType;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.BlueprintAnimation;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.animation.property.SimpleProperty;
import com.ticxo.modelengine.api.entity.Dummy;
import com.ticxo.modelengine.api.entity.data.BukkitEntityData;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public class ModelEngine4 extends  ModelEngine {

    public boolean existAnimation(String modelId, String animationName) {
        ModelBlueprint modelBlueprint = ModelEngineAPI.getBlueprint(modelId);
        return modelBlueprint != null && modelBlueprint.getAnimations().containsKey(animationName);
    }

    @Override
    public void loopAnimation(UUID balloonUniqueId, String modelId, String animationName) {

    }

    public ModelEngineAPI getModelEngineAPI() {
        return ModelEngineAPI.getAPI();
    }

    @Override
    public void stopAnimations(UUID balloonUniqueId, String modelId) {
        ModeledEntity entity = ModelEngineAPI.getModeledEntity(balloonUniqueId);
        if (entity == null) return;
        entity.getModel(modelId).ifPresent(activeModel -> {
            activeModel.getAnimationHandler().forceStopAllAnimations();
        });
    }

    @Override
    public void stopAnimationExcept(UUID balloonUniqueId, String modelId, String animationId) {
        ModeledEntity entity = ModelEngineAPI.getModeledEntity(balloonUniqueId);
        if (entity == null) return;
        entity.getModel(modelId).ifPresent(activeModel -> {
            activeModel.getBlueprint().getAnimations().forEach((key, value) -> {
                if (!key.equalsIgnoreCase(animationId))
                    activeModel.getAnimationHandler().stopAnimation(key);
            });
        });
    }

    @Override
    public boolean isPlayingAnimation(UUID balloonUniqueId, String modelId, String animationId) {
        ModeledEntity entity = ModelEngineAPI.getModeledEntity(balloonUniqueId);
        if (entity == null) return false;
        Optional<ActiveModel> activeModel = entity.getModel(modelId);
        return activeModel.map(model -> model.getAnimationHandler().isPlayingAnimation(animationId)).orElse(false);
    }

    @Override
    public void playAnimation(UUID balloonUniqueId, String modelId, String animationId) {
        ModeledEntity entity = ModelEngineAPI.getModeledEntity(balloonUniqueId);
        if (entity == null) return;
        entity.getModel(modelId).ifPresent(activeModel -> activeModel.getAnimationHandler().playAnimation(animationId, 1, 1, 1, false));
    }

    @Override
    public void removeModeledEntity(UUID balloonUniqueId, String modelId) {
        ModeledEntity entity = ModelEngineAPI.getModeledEntity(balloonUniqueId);
        if (entity == null) return;
        entity.removeModel(modelId).ifPresent(ActiveModel::destroy);
        entity.markRemoved();
        ModelEngineAPI.removeModeledEntity(balloonUniqueId);
    }

    @Override
    public void removeBackPack(UUID balloonUniqueId, String modelId) {
        ModeledEntity entity = ModelEngineAPI.getModeledEntity(balloonUniqueId);
        if (entity == null) return;
        entity.removeModel(modelId).ifPresent(ActiveModel::destroy);
        entity.markRemoved();
        ModelEngineAPI.removeModeledEntity(balloonUniqueId);
    }

    @Override
    public UUID spawnModel(Entity entity, String modelId, Location location, OffsetModel offsetModel) {
        ModelBlueprint blueprint = ModelEngineAPI.getBlueprint(modelId);
        ActiveModel activeModel = ModelEngineAPI.createActiveModel(blueprint);
        activeModel.setCanHurt(false);
        Dummy<?> dummy = new Dummy<>();
        ModeledEntity modeledEntity = ModelEngineAPI.createModeledEntity(dummy);
        dummy.setDetectingPlayers(true);
        dummy.syncLocation(location);
        modeledEntity.destroy();
        modeledEntity.addModel(activeModel, false);
        return dummy.getUUID();
    }

    @Override
    public ModeledEntity spawnModelBackPack(Entity entity, String modelId, Location location, OffsetModel offsetModel, PositionModelType positionModelType) {
        ModelBlueprint blueprint = ModelEngineAPI.getBlueprint(modelId);
        ActiveModel activeModel = ModelEngineAPI.createActiveModel(blueprint);
        IAnimationProperty animationProperty = new SimpleProperty(activeModel, activeModel.getBlueprint().getAnimations().get("idle"), 1.0, 0, 1.0);
        animationProperty.setForceLoopMode(BlueprintAnimation.LoopMode.LOOP);
        activeModel.getAnimationHandler().playAnimation(animationProperty, true);
        activeModel.setCanHurt(false);
        ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(entity.getUniqueId());
        if (modeledEntity == null) {
            FakeBackpack4 fakeBackpack4 = new FakeBackpack4(entity, offsetModel, positionModelType);
            fakeBackpack4.wrapBodyRotationControl();
            modeledEntity = ModelEngineAPI.createModeledEntity(fakeBackpack4);
            IEntityData entityData = modeledEntity.getBase().getData();
            if (entityData instanceof BukkitEntityData) {
                BukkitEntityData bukkitEntityData = (BukkitEntityData) entityData;
                if (entity instanceof Player)
                    bukkitEntityData.getTracked().addForcedPairing((Player) entity);
            }
        }
        modeledEntity.destroy();
        modeledEntity.addModel(activeModel, false);
        return modeledEntity;
    }

    @Override
    public void spawnLeash(Entity entity, UUID balloonUniqueId, String modelId) {
        ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(balloonUniqueId);
        if (modeledEntity == null) return;
        Optional<ActiveModel> activeModel = modeledEntity.getModel(modelId);
        if(!activeModel.isPresent()) return;
        activeModel.get().getLeashManager().ifPresent(leashManager -> {
            leashManager.getLeashes().values().forEach(leash -> {
                leash.connect(entity);
            });
        });
    }

    @Override
    public Set<Player> getTrackedPlayers(UUID balloonUniqueId) {
        ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(balloonUniqueId);
        if (modeledEntity == null) return new HashSet<>();
        IEntityData entityData = modeledEntity.getBase().getData();
        if (!(entityData instanceof BukkitEntityData)) return new HashSet<>();
        BukkitEntityData bukkitEntityData = (BukkitEntityData) entityData;
        return bukkitEntityData.getTracked().getTrackedPlayer();
    }

    @Override
    public void hideModel(UUID balloonUniqueId, Player player) {
        ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(balloonUniqueId);
        if (modeledEntity == null) return;
        IEntityData entityData = modeledEntity.getBase().getData();
        if (!(entityData instanceof BukkitEntityData)) return;
        BukkitEntityData bukkitEntityData = (BukkitEntityData) entityData;
        bukkitEntityData.getTracked().addForcedHidden(player);
    }

    @Override
    public void showModel(UUID balloonUniqueId, Player player) {
        ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(balloonUniqueId);
        if (modeledEntity == null) return;
        IEntityData entityData = modeledEntity.getBase().getData();
        if (!(entityData instanceof BukkitEntityData)) return;
        BukkitEntityData bukkitEntityData = (BukkitEntityData) entityData;
        bukkitEntityData.getTracked().addForcedPairing(player);
    }

    @Override
    public Set<String> getAllBonesIds(UUID balloonUniqueId, String modelId) {
        ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(balloonUniqueId);
        if (modeledEntity == null) return new HashSet<>();
        Optional<ActiveModel> activeModel = modeledEntity.getModel(modelId);
        return activeModel.map(model -> model.getBones().keySet()).orElseGet(HashSet::new);
    }

    @Override
    public void tint(UUID balloonUniqueId, String modelId, Color color, String boneId) {
        ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(balloonUniqueId);
        if (modeledEntity == null) return;
        Optional<ActiveModel> activeModel = modeledEntity.getModel(modelId);
        if(!activeModel.isPresent()) return;
        activeModel.get().getBone(boneId).ifPresent(modelBone -> {
            modelBone.setDefaultTint(color);
        });
    }

    @Override
    public void movementModel(UUID balloonUniqueId, Location location) {
        ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity(balloonUniqueId);
        if (modeledEntity == null) return;
        Dummy<?> dummy = (Dummy<?>) modeledEntity.getBase();
        dummy.syncLocation(location);
    }
}
