package com.francobm.magicosmetics.events;

import com.francobm.magicosmetics.cache.Cosmetic;
import com.francobm.magicosmetics.cache.CosmeticType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when player equip a cosmetic
 */
public class CosmeticEquipEvent extends Event implements Cancellable {

    private final Player player;
    private final Cosmetic cosmetic;
    private final CosmeticType cosmeticType;

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean isCancelled;

    public CosmeticEquipEvent(Player player, Cosmetic cosmetic, CosmeticType cosmeticType) {
        this.player = player;
        this.cosmetic = cosmetic;
        this.cosmeticType = cosmeticType;
        this.isCancelled = false;
    }

    public CosmeticEquipEvent(Player player, Cosmetic cosmetic) {
        this.player = player;
        this.cosmetic = cosmetic;
        this.cosmeticType = cosmetic.getCosmeticType();
        this.isCancelled = false;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public Player getPlayer() {
        return player;
    }

    public Cosmetic getCosmetic() {
        return cosmetic;
    }

    public CosmeticType getCosmeticType() {
        return cosmeticType;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }
}
