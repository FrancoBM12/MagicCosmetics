package com.francobm.magicosmetics.velocity.cache;

import com.velocitypowered.api.proxy.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {

    public static Map<UUID, PlayerData> players = new HashMap<>();

    private final UUID uniqueId;
    private final String name;
    private String cosmetics;
    private String cosmeticsInUse;
    private boolean firstJoin;

    public PlayerData(Player player, String cosmetics, String cosmeticsInUse) {
        uniqueId = player.getUniqueId();
        name = player.getUsername();
        this.cosmetics = cosmetics;
        this.cosmeticsInUse = cosmeticsInUse;
        firstJoin = true;
    }

    public PlayerData(Player player) {
        this(player, "", "");
    }

    public static void removePlayer(UUID uniqueId) {
        players.remove(uniqueId);
    }

    public static PlayerData getPlayer(Player player){
        if(!players.containsKey(player.getUniqueId())){
            PlayerData playerData = new PlayerData(player);
            players.put(player.getUniqueId(), playerData);
            return playerData;
        }
        return players.get(player.getUniqueId());
    }

    public String getCosmeticsInUse() {
        return cosmeticsInUse;
    }

    public String getCosmetics() {
        return cosmetics;
    }

    public void setCosmetics(String cosmetics) {
        this.cosmetics = cosmetics;
    }

    public void setCosmeticsInUse(String cosmeticsInUse) {
        this.cosmeticsInUse = cosmeticsInUse;
    }

    public void setFirstJoin(boolean firstJoin) {
        this.firstJoin = firstJoin;
    }

    public String getName() {
        return name;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public boolean isFirstJoin() {
        return firstJoin;
    }
}
