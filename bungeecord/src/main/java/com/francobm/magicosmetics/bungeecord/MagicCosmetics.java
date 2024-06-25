package com.francobm.magicosmetics.bungeecord;

import com.francobm.magicosmetics.bungeecord.listeners.PlayerListener;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class MagicCosmetics extends Plugin {

    private Map<ProxiedPlayer, String> cosmetics;

    //Crear Servidor Socket en Proxy y Cliente Socket en servers backend. El Servidor Socket tendrá acceso a la base de datos cuando el proxy esté activado y los servers backend no podrán usar la base de datos.
    //Si es posible también integrar MongoDB y Redis de una vez por todas.
    @Override
    public void onEnable() {
        registerChannels();
        cosmetics = new HashMap<>();
        registerListeners();
    }

    public void registerChannels() {
        getProxy().registerChannel("mc:player");
    }

    public void registerListeners() {
        getProxy().getPluginManager().registerListener(this, new PlayerListener(this));
    }

    public void sendQuitPlayerData(ProxiedPlayer player)
    {
        if(player == null) return;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF( "quit" ); // the channel could be whatever you want
        out.writeUTF(player.getName());
        player.getServer().getInfo().sendData( "mc:player", out.toByteArray() );
    }

    public void sendLoadPlayerData(ProxiedPlayer player)
    {
        if(player == null) return;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF( "load_cosmetics" ); // the channel could be whatever you want
        out.writeUTF(player.getName());
        out.writeUTF(cosmetics.getOrDefault(player, ""));
        player.getServer().getInfo().sendData( "mc:player", out.toByteArray() );
    }

    @Override
    public void onDisable() {

    }

    public Map<ProxiedPlayer, String> getCosmetics() {
        return cosmetics;
    }
}