package com.francobm.magicosmetics.velocity.listeners;

import com.francobm.magicosmetics.velocity.MagicCosmetics;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;

import java.util.Optional;

public class PlayerListener {
    private final MagicCosmetics plugin;

    public PlayerListener(MagicCosmetics plugin) {
        this.plugin = plugin;
    }

    /*
    @Subscribe(order = PostOrder.EARLY)
    public void onSwitchServer(ServerPostConnectEvent event) {
        Player player = event.getPlayer();
        plugin.sendLoadPlayerData(player);
        if(player.getCurrentServer().isEmpty()) return;
        ServerConnection serverConnection = player.getCurrentServer().get();
        //plugin.getLogger().info(serverConnection.getServerInfo().getName());
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        Player player = event.getPlayer();
        plugin.sendQuitPlayerData(player);
    }*/

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        plugin.executePluginMessage(event.getIdentifier().getId(), event.getData());

    }
}
