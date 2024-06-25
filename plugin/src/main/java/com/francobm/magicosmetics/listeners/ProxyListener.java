package com.francobm.magicosmetics.listeners;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.cache.PlayerData;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class ProxyListener implements PluginMessageListener {
    private final MagicCosmetics plugin = MagicCosmetics.getInstance();

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
        if(!channel.equals("mc:player")) return;
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
        switch (subChannel) {
            case "load_cosmetics": {
                String playerName = in.readUTF();
                String loadCosmetics = in.readUTF();
                Player p = Bukkit.getPlayer(playerName);
                if (p == null) return;
                plugin.getSql().loadPlayer(p, true);
                plugin.getVersion().getPacketReader().injectPlayer(p);
                PlayerData playerData = PlayerData.getPlayer(p);
                if(!loadCosmetics.isEmpty())
                    playerData.loadCosmetics(loadCosmetics);
                break;
            }
            case "save_cosmetics": {
                String playerName = in.readUTF();
                Player p = Bukkit.getPlayer(playerName);
                if (p == null) return;
                PlayerData playerData = PlayerData.getPlayer(p);
                plugin.getVersion().getPacketReader().removePlayer(player);
                if (playerData.isZone()) {
                    playerData.exitZoneSync();
                }
                playerData.sendSavePlayerData();
                break;
            }
            case "quit": {
                String playerName = in.readUTF();
                Player p = Bukkit.getPlayer(playerName);
                if (p == null) return;
                plugin.getVersion().getPacketReader().removePlayer(p);
                PlayerData playerData = PlayerData.getPlayer(p);
                if (playerData.isZone()) {
                    playerData.exitZoneSync();
                }
                plugin.getSql().asyncSavePlayer(playerData);
                break;
            }
        }
    }
}
