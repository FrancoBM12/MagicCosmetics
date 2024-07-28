package com.francobm.magicosmetics.bungeecord;

import com.francobm.magicosmetics.bungeecord.cache.PlayerData;
import com.francobm.magicosmetics.bungeecord.listeners.PlayerListener;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MagicCosmetics extends Plugin {

    //Crear Servidor Socket en Proxy y Cliente Socket en servers backend. El Servidor Socket tendrá acceso a la base de datos cuando el proxy esté activado y los servers backend no podrán usar la base de datos.
    //Si es posible también integrar MongoDB y Redis de una vez por todas.
    @Override
    public void onEnable() {
        getLogger().info("Hello Bungeecord!");
        registerChannels();
        registerListeners();
    }

    public void registerChannels() {
        getProxy().registerChannel("mc:player");
    }

    public void registerListeners() {
        getProxy().getPluginManager().registerListener(this, new PlayerListener(this));
    }

    public void unregisterChannels() {
        getProxy().unregisterChannel("mc:player");
    }

    public void unregisterListeners() {
        getProxy().getPluginManager().unregisterListeners(this);
    }

    public void sendPingPlayer(ProxiedPlayer player)
    {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF( "ping"); // the channel could be whatever you want
        out.writeUTF(player.getName());
        player.getServer().getInfo().sendData( "mc:player", out.toByteArray() );
    }

    public void sendLoadPlayerData(ProxiedPlayer player)
    {
        PlayerData playerData = PlayerData.getPlayer(player);
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
        out.writeUTF( "load_cosmetics"); // the channel could be whatever you want
        out.writeUTF(player.getName());
        out.writeUTF(cosmetics);
        out.writeUTF(cosmeticsInUse);
        out.writeUTF(status);
        player.getServer().getInfo().sendData( "mc:player", out.toByteArray() );
    }

    public void executePluginMessage(String tag, byte[] data) {
        if(!tag.equals("mc:player")) return;
        //getLogger().info("Passed PluginMessageEvent");
        ByteArrayDataInput in = ByteStreams.newDataInput(data);
        String subChannel = in.readUTF();
        //getLogger().info("Tag: " + tag + " subChannel: " + subChannel);
        if (subChannel.equals("save_cosmetics")) {
            String playerName = in.readUTF();
            String cosmetics = in.readUTF();
            String cosmeticsInUse = in.readUTF();
            PlayerData playerData = PlayerData.getPlayer(getProxy().getPlayer(playerName));
            playerData.setCosmetics(cosmetics);
            playerData.setCosmeticsInUse(cosmeticsInUse);
            playerData.setFirstJoin(false);
            //getLogger().info("Guardando cosmeticos del jugador: " + playerName);
        } else if (subChannel.equals("load_cosmetics")) {
            String playerName = in.readUTF();
            ProxiedPlayer player = getProxy().getPlayer(playerName);
            sendLoadPlayerData(player);
            //getLogger().info("Cargando cosmeticos del jugador: " + player.getName() + " en el servidor:" + player.getServer().getInfo().getName());
        }
        /*CompletableFuture.runAsync(() -> {

        });*/
    }

    @Override
    public void onDisable() {
        unregisterChannels();
        unregisterListeners();
    }
}