package com.francobm.magicosmetics.cache.cosmetics.balloons;

import com.francobm.magicosmetics.MagicCosmetics;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.PufferFish;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BalloonEngine {
    private ModeledEntity modeledEntity;
    private final List<UUID> players;
    private ActiveModel activeModel;
    private final String modelId;
    private final List<String> colorParts;
    private final String walk_animation;
    private final String idle_animation;
    private final double distance;

    public BalloonEngine(String modelId, List<String> colorParts, String walk_animation, String idle_animation, double distance) {
        this.modelId = modelId;
        this.colorParts = colorParts;
        this.walk_animation = walk_animation == null ? "walk" : walk_animation;
        this.idle_animation = idle_animation == null ? "idle" : idle_animation;
        this.modeledEntity = null;
        this.activeModel = null;
        this.distance = distance;
        players = new ArrayList<>();
    }

    public BalloonEngine getClone() {
        return new BalloonEngine(modelId, new ArrayList<>(colorParts), walk_animation, idle_animation, distance);
    }

    public void setState(int state){
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        switch (state){
            case 0:
                if(!plugin.getModelEngine().existAnimation(modelId, idle_animation)) return;
                //activeModel.addState("idle", 1, 1, 1);
                if(plugin.getModelEngine().isPlayingAnimation(activeModel, idle_animation)) {
                    plugin.getModelEngine().stopAnimationExcept(activeModel, idle_animation);
                    return;
                }
                plugin.getModelEngine().stopAnimations(activeModel);
                plugin.getModelEngine().playAnimation(activeModel, idle_animation);
                return;
            case 1:
                if(!MagicCosmetics.getInstance().getModelEngine().existAnimation(modelId, walk_animation)) return;
                if(plugin.getModelEngine().isPlayingAnimation(activeModel, walk_animation)){
                    plugin.getModelEngine().stopAnimationExcept(activeModel, walk_animation);
                    return;
                }
                plugin.getModelEngine().stopAnimations(activeModel);
                plugin.getModelEngine().playAnimation(activeModel, walk_animation);
                //activeModel.setState(ActiveModel.ModelState.WALK);
        }
    }

    public void remove(PufferFish leash){
        if(activeModel == null) return;
        if(modeledEntity == null) return;
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        for(Player player : modeledEntity.getRangeManager().getPlayerInRange()) {
            removePlayer(leash, player);
        }
        plugin.getModelEngine().removeModeledEntity(modeledEntity, activeModel);
        setActiveModel(null);
        setModeledEntity(null);
    }

    public void detectPlayers(PufferFish pufferFish, Entity owner) {
        if(modeledEntity == null) return;
        for(Player player : Bukkit.getOnlinePlayers()){
            if(players.contains(player.getUniqueId())) {
                if(!owner.getWorld().equals(player.getWorld())) {
                    removePlayer(pufferFish, player);
                    continue;
                }
                if(owner.getLocation().distanceSquared(player.getLocation()) > distance) {
                    removePlayer(pufferFish, player);
                    continue;
                }
            }
            if(!owner.getWorld().equals(player.getWorld())) continue;
            if(owner.getLocation().distanceSquared(player.getLocation()) > distance) continue;
            addPlayer(owner, pufferFish, player);
        }
        //MagicCosmetics.getInstance().getModelEngine().detectPlayers(modeledEntity, players);
    }

    private void addPlayer(Entity owner, PufferFish pufferFish, Player player) {
        if(modeledEntity == null) return;
        if(players.contains(player.getUniqueId())) return;
        modeledEntity.getRangeManager().forceSpawn(player);
        players.add(player.getUniqueId());
        if(pufferFish == null) return;
        MagicCosmetics.getInstance().getVersion().showFakePuffer(pufferFish, player);
        MagicCosmetics.getInstance().getVersion().attachFakeEntity(owner, pufferFish, player);
    }

    public void removePlayer(PufferFish pufferFish, Player player) {
        if(modeledEntity == null) return;
        modeledEntity.getRangeManager().removePlayer(player);
        players.remove(player.getUniqueId());
        if(pufferFish == null) return;
        MagicCosmetics.getInstance().getVersion().despawnFakeEntity(pufferFish, player);
    }

    public Set<String> getBones() {
        if(activeModel == null) return null;
        return MagicCosmetics.getInstance().getModelEngine().getAllBonesIds(activeModel);
    }

    public void spawnModel(Location location){
        modeledEntity = MagicCosmetics.getInstance().getModelEngine().spawnModel(modelId, location);
        activeModel = modeledEntity.getModel(modelId);
    }

    public void movementModel(Location location) {
        if(modeledEntity == null) return;
        MagicCosmetics.getInstance().getModelEngine().movementModel(modeledEntity, location);
    }

    public Location getMovementModel() {
        if(modeledEntity == null) return null;
        return modeledEntity.getBase().getLocation();
    }

    public void tintModel(Color color) {
        if(color == null) return;
        if(activeModel == null) return;
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        for (String id : getBones()) {
            if (getColorParts() != null && !getColorParts().isEmpty()) {
                if (!getColorParts().contains(id)) continue;
            }
            plugin.getModelEngine().tint(activeModel, color, id);
        }
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

    public List<String> getColorParts() {
        return colorParts;
    }

    public PufferFish spawnLeash(Location location) {
        return MagicCosmetics.getInstance().getVersion().spawnFakePuffer(location);
    }

    public void attachPufferFish(Player owner, Entity leashed) {
        if(modeledEntity == null) return;
        MagicCosmetics.getInstance().getVersion().attachFakeEntity(owner, leashed, modeledEntity.getRangeManager().getPlayerInRange().toArray(new Player[0]));
    }

    public void updatePositionLeash(Entity leashed, Location location) {
        MagicCosmetics.getInstance().getVersion().updatePositionFakeEntity(leashed, location);
    }

    public void teleportLeash(Entity leashed) {
        MagicCosmetics.getInstance().getVersion().teleportFakeEntity(leashed, modeledEntity.getRangeManager().getPlayerInRange().toArray(new Player[0]));
    }
}
