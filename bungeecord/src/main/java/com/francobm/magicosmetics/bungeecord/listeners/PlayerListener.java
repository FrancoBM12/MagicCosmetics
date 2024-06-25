package com.francobm.magicosmetics.bungeecord.listeners;

import com.francobm.magicosmetics.bungeecord.MagicCosmetics;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerListener implements Listener {
    private final MagicCosmetics plugin;

    public PlayerListener(MagicCosmetics plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSwitchServer(ServerSwitchEvent event) {
        ProxiedPlayer player = event.getPlayer();
        plugin.sendLoadPlayerData(player);
        plugin.getLogger().info(player.getServer().getInfo().getName());
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        plugin.sendQuitPlayerData(player);
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        String tag = event.getTag();
        plugin.getLogger().info("Tag: " + tag);
        if(!tag.equals("Bungeecord")) return;
        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String subChannel = in.readUTF();
        plugin.getLogger().info("Tag: " + tag + " subChannel: " + subChannel);
        if(subChannel.equals("save_cosmetics")){
            String playerName = in.readUTF();
            String cosmetics = in.readUTF();
            plugin.getCosmetics().put(plugin.getProxy().getPlayer(playerName), cosmetics);
        }else if(subChannel.equals("load_cosmetics")) {

        }

    }
}
