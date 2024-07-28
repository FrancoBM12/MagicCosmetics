package com.francobm.magicosmetics.velocity;

import com.francobm.magicosmetics.velocity.cache.PlayerData;
import com.francobm.magicosmetics.velocity.listeners.PlayerListener;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Plugin(
        id = "magiccosmetics",
        name = "MagicCosmetics",
        version = "1.0.0",
        authors = {"FrancoBM"})
public class MagicCosmetics {
    private final ProxyServer server;
    private final Logger logger;
    private Map<Player, PlayerData> cosmetics;
    public static final MinecraftChannelIdentifier IDENTIFIER = MinecraftChannelIdentifier.from("mc:player");

    @Inject
    public MagicCosmetics(ProxyServer server, Logger logger){
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("Hello Velocity!");
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
        player.getCurrentServer().map(serverConnection -> serverConnection.sendPluginMessage(IDENTIFIER, out.toByteArray()));
    }

    public void sendLoadPlayerData(Player player)
    {
        PlayerData playerData = cosmetics.get(player);
        String cosmetics;
        String cosmeticsInUse;
        String status;
        if(playerData.isFirstJoin()) {
            cosmetics = "";
            cosmeticsInUse = "";
            status = "0";
        }else {
            cosmetics = playerData.getCosmetics();
            cosmeticsInUse = playerData.getCosmeticsInUse();
            status = "1";
        }
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF( "load_cosmetics" ); // the channel could be whatever you want
        out.writeUTF(player.getUsername());
        out.writeUTF(cosmetics);
        out.writeUTF(cosmeticsInUse);
        out.writeUTF(status);
        if(player.getCurrentServer().isEmpty()) return;
        ServerConnection serverConnection = player.getCurrentServer().get();
        serverConnection.sendPluginMessage(IDENTIFIER, out.toByteArray());
    }

    public void executePluginMessage(String tag, byte[] data) {
        if(!tag.equals("mc:player")) return;
        //getLogger().info("Passed PluginMessageEvent");
        ByteArrayDataInput in = ByteStreams.newDataInput(data);
        String subChannel = in.readUTF();
        //getLogger().info("Tag: {} subChannel: {}", tag, subChannel);
        if (subChannel.equals("save_cosmetics")) {
            String playerName = in.readUTF();
            String cosmetics = in.readUTF();
            String cosmeticsInUse = in.readUTF();
            Optional<Player> optionalPlayer = getServer().getPlayer(playerName);
            if(optionalPlayer.isEmpty()) return;
            Player player = optionalPlayer.get();
            PlayerData playerData = PlayerData.getPlayer(player);
            playerData.setCosmetics(cosmetics);
            playerData.setCosmeticsInUse(cosmeticsInUse);
            playerData.setFirstJoin(false);
            //getLogger().info("Guardando cosmeticos del jugador: {}", playerName);
        } else if (subChannel.equals("load_cosmetics")) {
            String playerName = in.readUTF();
            Optional<Player> optionalPlayer = getServer().getPlayer(playerName);
            if(optionalPlayer.isEmpty()) return;
            Player player = optionalPlayer.get();
            sendLoadPlayerData(player);
            //if(player.getCurrentServer().isEmpty()) return;
            //ServerConnection serverConnection = player.getCurrentServer().get();
            //getLogger().info("Cargando cosmeticos del jugador: {} en el servidor:{}", player.getUsername(), serverConnection.getServerInfo().getName());
        }
    }

    public Logger getLogger() {
        return logger;
    }

    public ProxyServer getServer() {
        return server;
    }

    public Map<Player, PlayerData> getCosmetics() {
        return cosmetics;
    }
}
