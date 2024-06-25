package com.francobm.magicosmetics.provider;

import com.francobm.magiccrates.cache.PlayerData;
import org.bukkit.entity.Player;

public class MagicCrates {

    public boolean hasInCrate(Player player){
        PlayerData playerData = com.francobm.magiccrates.MagicCrates.getInstance().getManager().getPlayerData(player);
        if(playerData == null) return false;
        return playerData.isInCrate();
    }
}
