package com.francobm.magicosmetics.velocity.listeners;

import com.francobm.magicosmetics.velocity.MagicCosmetics;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;

import java.util.Optional;

public class PlayerListener {
    private final MagicCosmetics plugin;

    public PlayerListener(MagicCosmetics plugin) {
        this.plugin = plugin;
    }

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
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        ChannelIdentifier channelIdentifier = event.getIdentifier();
        //plugin.getLogger().info("identifier: {}", channelIdentifier.getId());
        if(channelIdentifier != MagicCosmetics.IDENTIFIER) return;
        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String subChannel = in.readUTF();
        //plugin.getLogger().info("Tag: {} subChannel: {}", channelIdentifier.getId(), subChannel);
        if(subChannel.equals("save_cosmetics")){
            String playerName = in.readUTF();
            String cosmetics = in.readUTF();
            Optional<Player> optionalPlayer = plugin.getServer().getPlayer(playerName);
            if(optionalPlayer.isEmpty()) return;
            plugin.getCosmetics().put(optionalPlayer.get(), cosmetics);
        }else if(subChannel.equals("load_cosmetics")) {

        }

    }
}
