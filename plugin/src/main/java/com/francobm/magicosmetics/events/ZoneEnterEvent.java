package com.francobm.magicosmetics.events;

import com.francobm.magicosmetics.cache.Zone;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class ZoneEnterEvent extends PlayerEvent implements Cancellable {

    private final Zone zone;
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean isCancelled;

    public ZoneEnterEvent(Player player, Zone zone) {
        super(player);
        this.zone = zone;
        this.isCancelled = false;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public Zone getZone() {
        return zone;
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
