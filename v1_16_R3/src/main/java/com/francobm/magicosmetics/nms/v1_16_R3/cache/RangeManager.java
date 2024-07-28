package com.francobm.magicosmetics.nms.v1_16_R3.cache;

import com.francobm.magicosmetics.nms.IRangeManager;
import net.minecraft.server.v1_16_R3.PlayerChunkMap;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class RangeManager implements IRangeManager {

    private final PlayerChunkMap.EntityTracker tracked;

    public RangeManager(PlayerChunkMap.EntityTracker tracked) {
        this.tracked = tracked;
    }

    @Override
    public void addPlayer(Player player) {
        tracked.trackedPlayers.add(((CraftPlayer) player).getHandle().playerConnection.player);
    }

    @Override
    public void removePlayer(Player player) {
        tracked.trackedPlayers.remove(((CraftPlayer) player).getHandle().playerConnection.player);
    }

    @Override
    public Set<Player> getPlayerInRange() {
        Set<Player> list = new HashSet<>();
        if(tracked == null) return list;
        tracked.trackedPlayers.forEach(serverPlayerConnection -> list.add(serverPlayerConnection.getBukkitEntity()));
        return list;
    }
}
