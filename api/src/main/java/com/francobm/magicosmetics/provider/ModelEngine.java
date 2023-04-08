package com.francobm.magicosmetics.provider;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import org.bukkit.Color;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.Set;

public abstract class ModelEngine {

    public abstract boolean existAnimation(String modelId, String animationName);

    public abstract ModelEngineAPI getModelEngineAPI();

    public abstract ModeledEntity createModeledEntity(Entity entity);

    public abstract ActiveModel createActiveModel(String modelId);

    public abstract ModeledEntity spawnModel(LivingEntity entity, String modelId, ActiveModel activeModel);

    public abstract void stopAnimations(ActiveModel activeModel);

    public abstract void stopAnimationExcept(ActiveModel activeModel, String animationId);

    public abstract boolean isPlayingAnimation(ActiveModel activeModel, String animationId);

    public abstract void playAnimation(ActiveModel activeModel, String animationId);

    public abstract void removeModeledEntity(ModeledEntity modeledEntity, ActiveModel activeModel);

    public abstract void detectPlayers(ModeledEntity modeledEntity);

    public abstract Set<String> getAllBonesIds(ActiveModel activeModel);

    public abstract void tint(ActiveModel activeModel, Color color, String boneId);
}
