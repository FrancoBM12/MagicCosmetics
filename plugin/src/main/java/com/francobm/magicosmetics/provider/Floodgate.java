package com.francobm.magicosmetics.provider;

import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

public class Floodgate {

    private final FloodgateApi floodgateApi;

    public Floodgate() {
        this.floodgateApi = FloodgateApi.getInstance();
    }

    public boolean isBedrockPlayer(Player player){
        return floodgateApi.isFloodgatePlayer(player.getUniqueId());
    }

    public FloodgateApi getAPI() {
        return floodgateApi;
    }
}
