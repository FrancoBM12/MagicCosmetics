package com.francobm.magicosmetics.provider.husksync;

import com.francobm.magicosmetics.MagicCosmetics;
import net.william278.husksync.BukkitHuskSync;
import net.william278.husksync.adapter.Adaptable;
import net.william278.husksync.data.BukkitData;
import net.william278.husksync.user.BukkitUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CosmeticData extends BukkitData implements Adaptable {

    private String cosmetics;
    private String cosmeticsInUse;
    private boolean isCosmeticsLoaded;

    public CosmeticData(String cosmetics, String cosmeticsInUse) {
        this.cosmetics = cosmetics;
        this.cosmeticsInUse = cosmeticsInUse;
    }

    private CosmeticData() {

    }

    @Override
    public void apply(BukkitUser bukkitUser, BukkitHuskSync bukkitHuskSync) {
        if(isCosmeticsLoaded) return;
        Player player = bukkitUser.getPlayer();
        MagicCosmetics.getInstance().getSql().loadPlayerAsync(player).thenAccept(playerData -> {
            playerData.forceClearCosmeticsInventory();
            playerData.loadCosmetics(getCosmetics(), getCosmeticsInUse());
            isCosmeticsLoaded = true;
            //Bukkit.getLogger().info("CD: Apply cosmetics to player");
        });
    }

    public String getCosmetics() {
        return cosmetics;
    }

    public String getCosmeticsInUse() {
        return cosmeticsInUse;
    }

    public void setCosmeticsInUse(String cosmeticsInUse) {
        this.cosmeticsInUse = cosmeticsInUse;
    }

    public void setCosmetics(String cosmetics) {
        this.cosmetics = cosmetics;
    }

    public void setCosmeticsLoaded(boolean cosmeticsLoaded) {
        isCosmeticsLoaded = cosmeticsLoaded;
    }
}
