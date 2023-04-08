package com.francobm.magicosmetics.events;

import com.francobm.magicosmetics.api.Cosmetic;
import com.francobm.magicosmetics.api.CosmeticType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 *  Called when player unequipped a cosmetic
 */
public class CosmeticUnEquipEvent extends PlayerEvent implements Cancellable {

    private final Cosmetic cosmetic;
    private final CosmeticType cosmeticType;

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean isCancelled;

    public CosmeticUnEquipEvent(Player player, Cosmetic cosmetic, CosmeticType cosmeticType) {
        super(player);
        this.cosmetic = cosmetic;
        this.cosmeticType = cosmeticType;
        this.isCancelled = false;
    }

    public CosmeticUnEquipEvent(Player player, Cosmetic cosmetic) {
        super(player);
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
