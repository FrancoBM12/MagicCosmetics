package com.francobm.magicosmetics.provider;

import com.francobm.magicosmetics.utils.OffsetModel;
import com.francobm.magicosmetics.utils.PositionModelType;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public abstract class ModelEngine {

    public abstract boolean existAnimation(String modelId, String animationName);

    public abstract void loopAnimation(UUID balloonUniqueId, String modelId, String animationName);

    public abstract Object getModelEngineAPI();

    public abstract UUID spawnModel(Entity entity, String modelId, Location location, OffsetModel offsetModel);

    public abstract void spawnLeash(Entity entity, UUID balloonUniqueId, String modelId);

    public abstract Object spawnModelBackPack(Entity entity, String modelId, Location location, OffsetModel offsetModel, PositionModelType positionModelType);

    public abstract void stopAnimations(UUID balloonUniqueId, String modelId);

    public abstract void stopAnimationExcept(UUID balloonUniqueId, String modelId, String animationId);

    public abstract boolean isPlayingAnimation(UUID balloonUniqueId, String modelId, String animationId);

    public abstract void playAnimation(UUID balloonUniqueId, String modelId, String animationId);

    public abstract void removeModeledEntity(UUID balloonUniqueId, String modelId);

    public abstract void removeBackPack(UUID balloonUniqueId, String modelId);

    public abstract Set<Player> getTrackedPlayers(UUID balloonUniqueId);

    public abstract void hideModel(UUID balloonUniqueId, Player player);

    public abstract void showModel(UUID balloonUniqueId, Player player);

    public abstract Set<String> getAllBonesIds(UUID balloonUniqueId, String modelId);

    public abstract void tint(UUID balloonUniqueId, String modelId, Color color, String boneId);

    public abstract void movementModel(UUID balloonUniqueId, Location location);
}
