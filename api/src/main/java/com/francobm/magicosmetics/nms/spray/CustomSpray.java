package com.francobm.magicosmetics.nms.spray;

import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class CustomSpray {
    public static Map<UUID, CustomSpray> customSprays = new ConcurrentHashMap<>();
    protected UUID uuid;
    protected List<UUID> players;
    protected ItemFrame entity;
    protected boolean preview;

    public static void updateSpray(Player player){
        for(CustomSpray spray : customSprays.values()){
            spray.remove(player);
            spray.spawn(player);
        }
    }

    public static void removeSpray(Player player){
        for(CustomSpray spray : customSprays.values()){
            if(!spray.players.contains(player.getUniqueId())) continue;
            spray.remove(player);
        }
    }

    public abstract void spawn(Player player);

    public abstract void spawn(boolean exception);

    public abstract void remove();

    public abstract void remove(Player player);

    public ItemFrame getEntity() {
        return entity;
    }

    public UUID getUuid() {
        return uuid;
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public void setPreview(boolean preview) {
        this.preview = preview;
    }

    public boolean isPreview() {
        return preview;
    }
}
