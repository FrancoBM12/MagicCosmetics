package com.francobm.magicosmetics.listeners;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.cache.EntityCache;
import net.citizensnpcs.api.event.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CitizensListener implements Listener {

    @EventHandler
    public void onLoad(CitizensEnableEvent event) {
        if(MagicCosmetics.getInstance().isItemsAdder()) return;
        MagicCosmetics.getInstance().getCitizens().loadNPCCosmetics();
    }

    @EventHandler
    public void onRemove(NPCRemoveByCommandSenderEvent event){
        EntityCache entityCache = EntityCache.getOrCreateEntity(event.getNPC().getUniqueId());
        entityCache.clearCosmeticsInUse();
        EntityCache.removeEntity(event.getNPC().getUniqueId());
    }

    @EventHandler
    public void onDespawn(NPCDespawnEvent event){
        EntityCache entityCache = EntityCache.getOrCreateEntity(event.getNPC().getUniqueId());
        entityCache.clearCosmeticsInUse();
    }

    @EventHandler
    public void onDeath(NPCDeathEvent event){
        EntityCache entityCache = EntityCache.getOrCreateEntity(event.getNPC().getUniqueId());
        entityCache.clearCosmeticsInUse();
    }

    @EventHandler
    public void onTeleport(NPCTeleportEvent event){
        EntityCache entityCache = EntityCache.getOrCreateEntity(event.getNPC().getUniqueId());
        entityCache.clearCosmeticsInUse();
    }
}
