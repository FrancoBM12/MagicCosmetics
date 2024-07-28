package com.francobm.magicosmetics.nms;

import org.bukkit.entity.Player;

import java.util.Set;

public interface IRangeManager {
    void addPlayer(Player player);
    void removePlayer(Player player);

    Set<Player> getPlayerInRange();
}
