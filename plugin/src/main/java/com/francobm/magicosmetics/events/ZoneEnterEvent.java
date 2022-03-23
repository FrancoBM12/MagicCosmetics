package com.francobm.magicosmetics.events;

import com.francobm.magicosmetics.cache.Zone;
import com.francobm.magicosmetics.nms.NPC.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ZoneEnterEvent extends Event implements Cancellable {

    private final Player player;
    private final Zone zone;

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean isCancelled;

    public ZoneEnterEvent(Player player, Zone zone) {
        this.player = player;
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

    public Player getPlayer() {
        return player;
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
