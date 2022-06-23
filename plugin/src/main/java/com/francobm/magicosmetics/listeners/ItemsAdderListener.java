package com.francobm.magicosmetics.listeners;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.cache.*;
import com.francobm.magicosmetics.cache.inventories.Menu;
import com.francobm.magicosmetics.cache.items.Items;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class ItemsAdderListener implements Listener {
    private final MagicCosmetics plugin = MagicCosmetics.getInstance();

    @EventHandler
    public void onIALoadEvent(ItemsAdderLoadDataEvent event){
        if(event.getCause() != ItemsAdderLoadDataEvent.Cause.FIRST_LOAD) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.ava = plugin.getItemsAdder().replaceFontImages(plugin.ava);
                plugin.unAva = plugin.getItemsAdder().replaceFontImages(plugin.unAva);
                plugin.equip = plugin.getItemsAdder().replaceFontImages(plugin.equip);
                Cosmetic.loadCosmetics();
                Color.loadColors();
                Items.loadItems();
                Zone.loadZones();
                Token.loadTokens();
                Sound.loadSounds();
                Menu.loadMenus();
                if(!plugin.isCitizens()) return;
                plugin.getCitizens().loadNPCCosmetics();
            }
        }.runTask(plugin);
    }
}
