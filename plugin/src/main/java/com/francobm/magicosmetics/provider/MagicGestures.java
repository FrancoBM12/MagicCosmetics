package com.francobm.magicosmetics.provider;

import com.francobm.magicgestures.api.data.PlayerData;
import org.bukkit.entity.Player;

public class MagicGestures {

    public boolean hasInWardrobe(Player player){
        PlayerData playerData = com.francobm.magicgestures.MagicGestures.getInstance().getPlayerDataLoader().getPlayerData(player);
        if(playerData == null) return false;
        return playerData.isInWardrobe();
    }
}
