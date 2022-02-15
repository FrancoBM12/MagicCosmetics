package com.francobm.customcosmetics.listeners;

import com.francobm.customcosmetics.CustomCosmetics;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ItemsAdderListener implements Listener {
    private final CustomCosmetics plugin = CustomCosmetics.getInstance();

    @EventHandler
    public void onIALoadEvent(ItemsAdderLoadDataEvent event){
        plugin.getCosmeticsManager().reload();
    }
}
