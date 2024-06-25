package com.francobm.magicosmetics.provider;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.cache.PlayerData;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class WorldGuard implements Listener {
    private final MagicCosmetics plugin;
    private StateFlag customFlag;

    public WorldGuard(MagicCosmetics plugin){
        registerFlag();
        this.plugin = plugin;
    }

    public void registerFlag(){
        FlagRegistry registry = com.sk89q.worldguard.WorldGuard.getInstance().getFlagRegistry();
        try {
            // create a flag with the name "my-custom-flag", defaulting to true
            StateFlag flag = new StateFlag("cosmetics", true);
            registry.register(flag);
            customFlag = flag; // only set our field if there was no error
        } catch (FlagConflictException e) {
            // some other plugin registered a flag by the same name already.
            // you can use the existing flag, but this may cause conflicts - be sure to check type
            Flag<?> existing = registry.get("cosmetics");
            if (existing instanceof StateFlag) {
                customFlag = (StateFlag) existing;
            } else {
                // types don't match - this is bad news! some other plugin conflicts with you
                // hopefully this never actually happens
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        org.bukkit.Location to = event.getTo();
        if(to == null) return;
        PlayerData playerData = PlayerData.getPlayer(player);
        Location location = BukkitAdapter.adapt(to);
        ApplicableRegionSet applicableRegionSet = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(location);
        StateFlag.State flagState = applicableRegionSet.queryState(null, customFlag);
        if(flagState == null || flagState.equals(StateFlag.State.DENY)){
            playerData.hideAllCosmetics();
            return;
        }
        playerData.showAllCosmetics();
    }
}
