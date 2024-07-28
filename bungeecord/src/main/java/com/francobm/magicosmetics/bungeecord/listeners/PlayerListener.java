package com.francobm.magicosmetics.bungeecord.listeners;

import com.francobm.magicosmetics.bungeecord.MagicCosmetics;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerListener implements Listener {
    private final MagicCosmetics plugin;

    public PlayerListener(MagicCosmetics plugin) {
        this.plugin = plugin;
    }

    /*
    @EventHandler
    public void onSwitchServer(ServerSwitchEvent event) {
        ProxiedPlayer player = event.getPlayer();
        plugin.sendPingPlayer(player);
        plugin.getLogger().info("Enviando ping al jugador: " + player.getName() + " en el servidor:" + player.getServer().getInfo().getName());
    }

    
    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        plugin.getCosmetics().remove(player);
        plugin.sendQuitPlayerData(player);
    }*/

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        plugin.executePluginMessage(event.getTag(), event.getData());
    }
}
