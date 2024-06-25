package com.francobm.magicosmetics.listeners;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.cache.PlayerData;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.event.SkinApplyEvent;
import org.bukkit.entity.Player;

public class SkinListener {
    private final MagicCosmetics plugin = MagicCosmetics.getInstance();

    public SkinListener() {
        SkinsRestorerProvider.get().getEventBus().subscribe(plugin, SkinApplyEvent.class, event -> {
            Player player = event.getPlayer(Player.class);
            PlayerData playerData = PlayerData.getPlayer(player);
            plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, playerData::clearBag, 20L);
        });
    }
}
