package com.francobm.magicosmetics.listeners;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.cache.ZoneAction;
import com.francobm.magicosmetics.events.ZoneEnterEvent;
import com.francobm.magicosmetics.events.ZoneExitEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ZoneListener implements Listener {
    private final MagicCosmetics plugin = MagicCosmetics.getInstance();
    @EventHandler
    public void onEnterZone(ZoneEnterEvent event) {
        Player player = event.getPlayer();
        ZoneAction onEnterAction = plugin.getZoneActions().getOnEnter();
        if(onEnterAction == null) return;
        onEnterAction.executeCommands(player, event.getZone().getId());
    }

    @EventHandler
    public void onExitZone(ZoneExitEvent event) {
        Player player = event.getPlayer();
        ZoneAction onExitAction = plugin.getZoneActions().getOnExit();
        if(onExitAction == null) return;
        onExitAction.executeCommands(player, event.getZone().getId());
    }
}
