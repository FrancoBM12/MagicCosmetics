package com.francobm.magicosmetics.events;

import com.francobm.magicosmetics.cache.Zone;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ZoneExitEvent extends Event implements Cancellable {

    private final Player player;
    private final Zone zone;
    private final Reason reason;

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean isCancelled;

    public ZoneExitEvent(Player player, Zone zone, Reason reason) {
        this.player = player;
        this.zone = zone;
        this.reason = reason;
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

    public Zone getZone() {
        return zone;
    }

    public Reason getReason() {
        return reason;
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
