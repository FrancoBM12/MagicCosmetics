package com.francobm.magicosmetics.cache.cosmetics.balloons;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.utils.OffsetModel;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.*;

import java.util.*;

public class BalloonEngine {
    private UUID balloonUniqueId;
    private final String modelId;
    private final List<String> colorParts;
    private final String walk_animation;
    private final String idle_animation;
    private final double distance;
    private final OffsetModel offsetModel;
    private boolean playOn;

    public BalloonEngine(String modelId, List<String> colorParts, String walk_animation, String idle_animation, double distance, OffsetModel offsetModel) {
        this.modelId = modelId;
        this.colorParts = colorParts;
        this.walk_animation = walk_animation == null ? "walk" : walk_animation;
        this.idle_animation = idle_animation == null ? "idle" : idle_animation;
        this.distance = distance;
        this.offsetModel = offsetModel;
    }

    public BalloonEngine getClone() {
        return new BalloonEngine(modelId, new ArrayList<>(colorParts), walk_animation, idle_animation, distance, offsetModel);
    }

    public void setStatePlayOn(int state){
        if(playOn) return;
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        switch (state){
            case 0:
                if(!plugin.getModelEngine().existAnimation(modelId, idle_animation)) return;
                if(plugin.getModelEngine().isPlayingAnimation(balloonUniqueId, modelId, idle_animation)) {
                    plugin.getModelEngine().stopAnimationExcept(balloonUniqueId, modelId, idle_animation);
                    break;
                }
                plugin.getModelEngine().stopAnimations(balloonUniqueId, modelId);
                plugin.getModelEngine().playAnimation(balloonUniqueId, modelId, idle_animation);
                plugin.getModelEngine().loopAnimation(balloonUniqueId, modelId, idle_animation);
                break;
            case 1:
                if(!MagicCosmetics.getInstance().getModelEngine().existAnimation(modelId, walk_animation)) return;
                if(plugin.getModelEngine().isPlayingAnimation(balloonUniqueId, modelId, walk_animation)){
                    plugin.getModelEngine().stopAnimationExcept(balloonUniqueId, modelId, walk_animation);
                    break;
                }
                plugin.getModelEngine().stopAnimations(balloonUniqueId, modelId);
                plugin.getModelEngine().playAnimation(balloonUniqueId, modelId, walk_animation);
                plugin.getModelEngine().loopAnimation(balloonUniqueId, modelId, walk_animation);
                break;
        }
        playOn = true;
    }

    public void setState(int state){
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        switch (state){
            case 0:
                if(!plugin.getModelEngine().existAnimation(modelId, idle_animation)) return;
                //activeModel.addState("idle", 1, 1, 1);
                if(plugin.getModelEngine().isPlayingAnimation(balloonUniqueId, modelId, idle_animation)) {
                    plugin.getModelEngine().stopAnimationExcept(balloonUniqueId, modelId, idle_animation);
                    return;
                }
                plugin.getModelEngine().stopAnimations(balloonUniqueId, modelId);
                plugin.getModelEngine().playAnimation(balloonUniqueId, modelId, idle_animation);
                return;
            case 1:
                if(!MagicCosmetics.getInstance().getModelEngine().existAnimation(modelId, walk_animation)) return;
                if(plugin.getModelEngine().isPlayingAnimation(balloonUniqueId, modelId, walk_animation)){
                    plugin.getModelEngine().stopAnimationExcept(balloonUniqueId, modelId, walk_animation);
                    return;
                }
                plugin.getModelEngine().stopAnimations(balloonUniqueId, modelId);
                plugin.getModelEngine().playAnimation(balloonUniqueId, modelId, walk_animation);
                //activeModel.setState(ActiveModel.ModelState.WALK);
        }
    }

    public void remove(LivingEntity pufferFish){
        if(balloonUniqueId == null) return;
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        plugin.getModelEngine().removeModeledEntity(balloonUniqueId, modelId);
        balloonUniqueId = null;
    }

    public Set<String> getBones() {
        if(balloonUniqueId == null) return null;
        return MagicCosmetics.getInstance().getModelEngine().getAllBonesIds(balloonUniqueId, modelId);
    }

    public ArmorStand spawnModel(Location location){
        ArmorStand armorStand = MagicCosmetics.getInstance().getVersion().spawnArmorStand(location);
        balloonUniqueId = MagicCosmetics.getInstance().getModelEngine().spawnModel(armorStand, modelId, location, offsetModel);
        return armorStand;
    }

    public void updateTeleport(LivingEntity leashed, Location location) {
        if(balloonUniqueId == null) return;
        MagicCosmetics.getInstance().getVersion().updatePositionFakeEntity(leashed, location);
        Set<Player> players = MagicCosmetics.getInstance().getModelEngine().getTrackedPlayers(balloonUniqueId);
        MagicCosmetics.getInstance().getVersion().teleportFakeEntity(leashed, players);
        MagicCosmetics.getInstance().getModelEngine().movementModel(balloonUniqueId, location);
    }

    public void tintModel(Color color) {
        if(color == null) return;
        if(balloonUniqueId == null) return;
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        for (String id : getBones()) {
            if (getColorParts() != null && !getColorParts().isEmpty()) {
                if (!getColorParts().contains(id)) continue;
            }
            plugin.getModelEngine().tint(balloonUniqueId, modelId, color, id);
        }
    }

    public void showModel(Player player) {
        MagicCosmetics.getInstance().getModelEngine().showModel(balloonUniqueId, player);
    }

    public void hideModel(Player player) {
        MagicCosmetics.getInstance().getModelEngine().hideModel(balloonUniqueId, player);
    }

    public UUID getBalloonUniqueId() {
        return balloonUniqueId;
    }

    public String getModelId() {
        return modelId;
    }

    public List<String> getColorParts() {
        return colorParts;
    }

    public void spawnLeash(Entity entity) {
        if(balloonUniqueId == null) return;
        MagicCosmetics.getInstance().getModelEngine().spawnLeash(entity, balloonUniqueId, modelId);
    }
}
