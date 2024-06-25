package com.francobm.magicosmetics.cache.cosmetics.balloons;

import com.francobm.magicosmetics.MagicCosmetics;
import dev.lone.itemsadder.api.CustomEntity;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.PufferFish;

import java.util.*;
import java.util.stream.Collectors;

public class BalloonIA {
    private CustomEntity customEntity;
    private final String modelId;
    private final List<String> colorParts;
    private final List<UUID> players;
    private final String walk_animation;
    private final String idle_animation;
    private final double distance;
    private CustomEntity.Bone leashBone;

    public BalloonIA(String modelId, List<String> colorParts, String walk_animation, String idle_animation, double distance) {
        this.modelId = modelId;
        this.colorParts = colorParts;
        this.walk_animation = walk_animation == null ? "walk" : walk_animation;
        this.idle_animation = idle_animation == null ? "idle" : idle_animation;
        this.players = new ArrayList<>();
        this.distance = distance;
    }

    public BalloonIA getClone() {
        return new BalloonIA(modelId, new ArrayList<>(colorParts), walk_animation, idle_animation, distance);
    }

    public void spawn(Location location){
        if(customEntity != null){
            customEntity.getEntity().remove();
        }
        customEntity = CustomEntity.spawn(modelId, location, false, true, true);
        for (CustomEntity.Bone bone : customEntity.getBones()) {
            if(bone.getName().startsWith("l_")){
                this.leashBone = bone;
                break;
            }
        }
    }

    public void paintBalloon(Color color) {
        if(colorParts.isEmpty()){
            customEntity.setColorAllBones(color.asRGB());
            return;
        }
        for(CustomEntity.Bone bone : customEntity.getBones()){
            if(!colorParts.contains(bone.getName())) continue;
            bone.setColor(color.asRGB());
        }
    }

    public void setState(int state){
        switch (state){
            case 0:
                if(!getCustomEntity().hasAnimation(idle_animation)) return;
                if(getCustomEntity().isPlayingAnimation(idle_animation)){
                    getCustomEntity().stopAnimation();
                    return;
                }
                getCustomEntity().playAnimation(idle_animation);
                break;
            case 1:
                if(!getCustomEntity().hasAnimation(walk_animation)) return;
                if(getCustomEntity().isPlayingAnimation(walk_animation)){
                    getCustomEntity().stopAnimation();
                    return;
                }
                getCustomEntity().playAnimation(walk_animation);
                break;
        }
    }

    public void remove(PufferFish pufferFish){
        if(customEntity != null){
            for(Player player : Bukkit.getOnlinePlayers()) {
                removePlayer(pufferFish, player);
            }
            customEntity.destroy();
            customEntity = null;
        }
    }

    public void detectPlayers(PufferFish pufferFish, Entity owner) {
        if(customEntity == null) return;
        if(pufferFish == null) return;
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
            addPlayer(pufferFish, owner, player);
        }
    }

    private void addPlayer(PufferFish pufferFish, Entity owner, Player player) {
        if(pufferFish == null) return;
        if(customEntity == null) return;
        if(players.contains(player.getUniqueId())) return;
        players.add(player.getUniqueId());
        MagicCosmetics.getInstance().getVersion().showEntity(pufferFish, player);
        MagicCosmetics.getInstance().getVersion().attachFakeEntity(owner, pufferFish, player);
    }

    public void removePlayer(PufferFish pufferFish, Player player) {
        if(pufferFish == null) return;
        if(customEntity == null) return;
        players.remove(player.getUniqueId());
        MagicCosmetics.getInstance().getVersion().despawnFakeEntity(pufferFish, player);
    }

    public String getModelId() {
        return modelId;
    }

    public CustomEntity getCustomEntity() {
        return customEntity;
    }

    public PufferFish spawnLeash(Location location) {
        return MagicCosmetics.getInstance().getVersion().spawnFakePuffer(location);
    }

    public CustomEntity.Bone getLeashBone() {
        return leashBone;
    }

    public Location getModelLocation() {
        if(customEntity == null) return null;
        return customEntity.getLocation();
    }

    public void updateTeleport(PufferFish leashed) {
        if(leashed == null) return;
        MagicCosmetics.getInstance().getVersion().updatePositionFakeEntity(leashed, leashBone.getLocation().add(0, 1.6, 0));
        Set<Player> players = this.players.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toSet());
        MagicCosmetics.getInstance().getVersion().teleportFakeEntity(leashed, players);
    }
}
