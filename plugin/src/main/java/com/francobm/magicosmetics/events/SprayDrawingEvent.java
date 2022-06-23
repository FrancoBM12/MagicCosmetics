package com.francobm.magicosmetics.events;

import com.francobm.magicosmetics.api.SprayKeys;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Event called when a player is drawing a spray.
 */
public class SprayDrawingEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final SprayKeys key;
    private final Block sprayedBlock;
    private boolean isCancelled;

    public SprayDrawingEvent(Player player, Block sprayedBlock, SprayKeys key) {
        super(player);
        this.key = key;
        this.sprayedBlock = sprayedBlock;
        this.isCancelled = false;
    }

    /**
     * Gets the key of the spray being drawn.
     *
     * @return the key of the spray being drawn.
     */
    public SprayKeys getKey() {
        return key;
    }

    /**
     * Gets the block being sprayed.
     *
     * @return the block being sprayed.
     */
    public Block getSprayedBlock() {
        return sprayedBlock;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
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
