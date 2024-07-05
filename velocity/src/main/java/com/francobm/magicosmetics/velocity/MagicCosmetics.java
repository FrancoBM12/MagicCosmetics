package com.francobm.magicosmetics.velocity;

import com.francobm.magicosmetics.velocity.listeners.PlayerListener;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

@Plugin(
        id = "magiccosmetics",
        name = "MagicCosmetics",
        version = "1.0.0",
        authors = {"FrancoBM"})
public class MagicCosmetics {
    private final ProxyServer server;
    private final Logger logger;
    private Map<Player, String> cosmetics;
    public static final MinecraftChannelIdentifier IDENTIFIER = MinecraftChannelIdentifier.from("mc:player");

    @Inject
    public MagicCosmetics(ProxyServer server, Logger logger){
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        registerChannels();
        cosmetics = new HashMap<>();
        registerListeners();
    }

    public void registerChannels() {
        server.getChannelRegistrar().register(IDENTIFIER);
    }

    public void registerListeners() {
        server.getEventManager().register(this, new PlayerListener(this));
    }

    public void sendQuitPlayerData(Player player)
    {
        if(player == null) return;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF( "quit" ); // the channel could be whatever you want
        out.writeUTF(player.getUsername());
        player.getCurrentServer().map(serverConnection -> serverConnection.sendPluginMessage(MinecraftChannelIdentifier.from("mc:player"), out.toByteArray()));
    }

    public void sendLoadPlayerData(Player player)
    {
        if(player == null) return;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF( "load_cosmetics" ); // the channel could be whatever you want
        out.writeUTF(player.getUsername());
        out.writeUTF(cosmetics.getOrDefault(player, ""));
        player.getCurrentServer().map(serverConnection -> serverConnection.sendPluginMessage(MinecraftChannelIdentifier.from("mc:player"), out.toByteArray()));
    }

    public Logger getLogger() {
        return logger;
    }

    public ProxyServer getServer() {
        return server;
    }

    public Map<Player, String> getCosmetics() {
        return cosmetics;
    }
}
