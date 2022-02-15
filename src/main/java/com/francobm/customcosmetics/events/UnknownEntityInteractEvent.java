package com.francobm.customcosmetics.events;

import com.francobm.customcosmetics.nms.NPC.NPC;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UnknownEntityInteractEvent extends Event implements Cancellable {

    private final Player player;
    private final NPC unknownEntity;
    private final Action action;


    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean isCancelled;

    public UnknownEntityInteractEvent(Player player, NPC unknownEntity, Action action) {
        this.player = player;
        this.unknownEntity = unknownEntity;
        this.action = action;
        this.isCancelled = false;
    }

    public UnknownEntityInteractEvent(boolean isAsync, Player player, NPC unknownEntity, Action action) {
        super(isAsync);
        this.player = player;
        this.unknownEntity = unknownEntity;
        this.action = action;
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

    public NPC getUnknownEntity() {
        return unknownEntity;
    }

    public Action getAction() {
        return action;
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
