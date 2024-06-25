package com.francobm.magicosmetics.cache.cosmetics.backpacks;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.utils.OffsetModel;
import com.francobm.magicosmetics.utils.PositionModelType;
import org.bukkit.Color;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BackPackEngine {
    private UUID backPackUniqueId;
    private final String modelId;
    private final List<String> colorParts;
    private final String idle_animation;
    private final double distance;
    private final OffsetModel offsetModel;
    private final PositionModelType positionModelType;

    public BackPackEngine(String modelId, List<String> colorParts, String idle_animation, double distance, OffsetModel offsetModel, PositionModelType positionModelType) {
        this.modelId = modelId;
        this.colorParts = colorParts;
        this.idle_animation = idle_animation == null ? "idle" : idle_animation;
        this.distance = distance;
        this.offsetModel = offsetModel;
        this.positionModelType = positionModelType;
    }

    public BackPackEngine getClone() {
        return new BackPackEngine(modelId, new ArrayList<>(colorParts), idle_animation, distance, offsetModel, positionModelType);
    }

    public void remove(){
        if(backPackUniqueId == null) return;
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        plugin.getModelEngine().removeBackPack(backPackUniqueId, modelId);
    }

    public Set<String> getBones() {
        if(backPackUniqueId == null) return null;
        return MagicCosmetics.getInstance().getModelEngine().getAllBonesIds(backPackUniqueId, modelId);
    }

    public void spawnModel(Entity owner){
        backPackUniqueId = owner.getUniqueId();
        MagicCosmetics.getInstance().getModelEngine().spawnModelBackPack(owner, modelId, owner.getLocation(), offsetModel, positionModelType);
    }

    public void showModel(Player player) {
        MagicCosmetics.getInstance().getModelEngine().showModel(backPackUniqueId, player);
    }

    public void hideModel(Player player) {
        MagicCosmetics.getInstance().getModelEngine().hideModel(backPackUniqueId, player);
    }

    public void tintModel(Entity owner, Color color) {
        if(color == null) return;
        if(backPackUniqueId == null) return;
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        for (String id : getBones()) {
            if (getColorParts() != null && !getColorParts().isEmpty()) {
                if (!getColorParts().contains(id)) continue;
            }
            plugin.getModelEngine().tint(backPackUniqueId, modelId, color, id);
        }
    }

    public String getModelId() {
        return modelId;
    }

    public List<String> getColorParts() {
        return colorParts;
    }

    public UUID getBackPackUniqueId() {
        return backPackUniqueId;
    }
}
