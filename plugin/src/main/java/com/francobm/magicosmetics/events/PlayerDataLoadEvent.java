package com.francobm.magicosmetics.events;

import com.francobm.magicosmetics.api.Cosmetic;
import com.francobm.magicosmetics.cache.PlayerData;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Set;

/**
 *  The event is called when the player's data has just been loaded.
 */
public class PlayerDataLoadEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final PlayerData playerData;
    private final Set<Cosmetic> equippedCosmetics;

    public PlayerDataLoadEvent(PlayerData playerData, Set<Cosmetic> equippedCosmetics){
        super(true);
        this.playerData = playerData;
        this.equippedCosmetics = equippedCosmetics;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public Set<Cosmetic> getEquippedCosmetics() {
        return equippedCosmetics;
    }
}
