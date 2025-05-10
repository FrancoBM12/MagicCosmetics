package com.francobm.magicosmetics.provider.mpdb;

import com.francobm.magicosmetics.MagicCosmetics;
import net.craftersland.data.bridge.PD;
import net.craftersland.data.bridge.api.API;
import net.craftersland.data.bridge.api.events.SyncCompleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MysqlPlayerDataBridge implements Listener {
    private final MagicCosmetics plugin = MagicCosmetics.getInstance();
    private API api;

    public MysqlPlayerDataBridge() {
        api = PD.api;
    }

    @EventHandler
    public void onSyncInventory(SyncCompleteEvent event) {
        Player player = event.getPlayer();
        plugin.getSql().loadPlayerAsync(player);
    }
}
