package com.francobm.magicosmetics.nms.v1_21_R4.cache;

import com.francobm.magicosmetics.nms.IRangeManager;
import net.minecraft.server.level.PlayerChunkMap;
import org.bukkit.craftbukkit.v1_21_R4.entity.CraftPlayer;
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
        tracked.f.add(((CraftPlayer) player).getHandle().f);
    }

    @Override
    public void removePlayer(Player player) {
        tracked.f.remove(((CraftPlayer) player).getHandle().f);
    }

    @Override
    public Set<Player> getPlayerInRange() {
        Set<Player> list = new HashSet<>();
        if(tracked == null) return list;
        tracked.f.forEach(serverPlayerConnection -> list.add(serverPlayerConnection.o().getBukkitEntity()));
        return list;
    }
}
