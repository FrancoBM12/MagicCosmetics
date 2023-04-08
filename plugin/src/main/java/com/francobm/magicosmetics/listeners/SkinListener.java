package com.francobm.magicosmetics.listeners;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.cache.PlayerData;
import net.skinsrestorer.api.bukkit.events.SkinApplyBukkitEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SkinListener implements Listener {
    private final MagicCosmetics plugin = MagicCosmetics.getInstance();

    @EventHandler
    public void onChangeSkin(SkinApplyBukkitEvent event) {
        Player player = event.getWho();
        PlayerData playerData = PlayerData.getPlayer(player);
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, playerData::clearBag, 20L);
    }
}
