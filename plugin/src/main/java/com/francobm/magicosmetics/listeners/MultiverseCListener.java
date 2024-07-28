package com.francobm.magicosmetics.listeners;

import com.francobm.magicosmetics.cache.PlayerData;
import com.onarandombox.MultiverseCore.event.MVTeleportEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MultiverseCListener implements Listener {

    @EventHandler
    public void onTeleportMultiverse(MVTeleportEvent event) {
        if(!event.getDestination().isValid()) return;
        Player player = event.getTeleportee();
        PlayerData playerData = PlayerData.getPlayer(player);
        if(playerData.isZone()){
            if(!playerData.isSpectator()) return;
            event.setCancelled(true);
        }
        playerData.clearCosmeticsInUse(false);
    }
}
