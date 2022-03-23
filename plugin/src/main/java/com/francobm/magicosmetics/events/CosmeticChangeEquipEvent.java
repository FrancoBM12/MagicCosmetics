package com.francobm.magicosmetics.events;

import com.francobm.magicosmetics.cache.Cosmetic;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *  Called when a player changes their cosmetic
 */
public class CosmeticChangeEquipEvent extends Event implements Cancellable {

    private final Player player;
    private final Cosmetic newCosmetic;
    private final Cosmetic oldCosmetic;

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean isCancelled;

    public CosmeticChangeEquipEvent(Player player, Cosmetic oldCosmetic, Cosmetic newCosmetic) {
        this.player = player;
        this.oldCosmetic = oldCosmetic;
        this.newCosmetic = newCosmetic;
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

    public Cosmetic getOldCosmetic() {
        return oldCosmetic;
    }

    public Cosmetic getNewCosmetic() {
        return newCosmetic;
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
