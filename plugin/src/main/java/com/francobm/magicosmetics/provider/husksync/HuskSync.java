package com.francobm.magicosmetics.provider.husksync;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.cache.PlayerData;
import net.william278.husksync.api.HuskSyncAPI;
import net.william278.husksync.data.BukkitData;
import net.william278.husksync.data.Data;
import net.william278.husksync.data.DataSnapshot;
import net.william278.husksync.data.Identifier;
import net.william278.husksync.event.BukkitDataSaveEvent;
import net.william278.husksync.event.BukkitSyncCompleteEvent;
import net.william278.husksync.user.BukkitUser;
import net.william278.husksync.user.OnlineUser;
import net.william278.husksync.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import javax.swing.text.html.Option;
import java.util.Optional;

import static net.william278.husksync.libraries.nbtapi.NBT.getPersistentData;

public class HuskSync implements Listener {
    private static final Identifier COSMETICS_ID = Identifier.from("magicosmetics", "cosmetics");
    private final HuskSyncAPI huskSyncAPI;
    private final MagicCosmetics plugin = MagicCosmetics.getInstance();

    public HuskSync() {
        this.huskSyncAPI = HuskSyncAPI.getInstance();
        register();
    }

    private void register() {
        huskSyncAPI.registerDataSerializer(COSMETICS_ID, new CosmeticSerializer(huskSyncAPI));
    }

    public void loadDataToPlayer(Player player) {
        huskSyncAPI.getUser(player.getUniqueId()).thenAccept((optionalUser) -> {
            if(!optionalUser.isPresent()) {
                Bukkit.getLogger().severe("OU HuskSync: Could not find user for UUID: " + player.getUniqueId());
                return;
            }
            huskSyncAPI.getCurrentData(optionalUser.get()).thenAccept((dataToPlayer) -> {
                if(!dataToPlayer.isPresent()) {
                    Bukkit.getLogger().severe("DTP HuskSync: Could not find user for UUID: " + player.getUniqueId());
                    return;
                }
                PlayerData playerData = PlayerData.getPlayer(player);
                DataSnapshot.Unpacked snapshot = dataToPlayer.get();
                Optional<CosmeticData> optionalCosmeticData = (Optional<CosmeticData>) snapshot.getData(COSMETICS_ID);
                if(!optionalCosmeticData.isPresent()) {
                    Bukkit.getLogger().severe("OCD HuskSync: Could not find user for UUID: " + playerData.getUniqueId());
                    return;
                }
                CosmeticData cosmeticData = optionalCosmeticData.get();
                Bukkit.getLogger().info("CosmeticData: " + cosmeticData.getCosmetics() + " - " + cosmeticData.getCosmeticsInUse());
                playerData.loadCosmetics(cosmeticData.getCosmetics(), cosmeticData.getCosmeticsInUse());
            });
        });
    }

    public void saveDataToPlayer(PlayerData playerData) {
        huskSyncAPI.getUser(playerData.getUniqueId()).thenAccept((optionalUser) ->{
            if(!optionalUser.isPresent()) {
                Bukkit.getLogger().severe("S OU HuskSync: Could not find user for UUID: " + playerData.getUniqueId());
                return;
            }
            User user = optionalUser.get();
            huskSyncAPI.getCurrentData(user).thenAccept(optionalSnapshot -> {
               if(!optionalSnapshot.isPresent()) {
                   Bukkit.getLogger().severe("S OS HuskSync: Could not find user for UUID: " + playerData.getUniqueId());
                   return;
               }
               DataSnapshot.Unpacked snapshot = optionalSnapshot.get();
               if(snapshot.getData(COSMETICS_ID).isPresent()){
                   Bukkit.getLogger().info("CosmeticData Already Exist data");
                   return;
               }
               snapshot.setData(COSMETICS_ID, new CosmeticData(playerData.saveCosmetics(), playerData.getCosmeticsInUse()));
               snapshot.setSaveCause(DataSnapshot.SaveCause.API);
               huskSyncAPI.setCurrentData(user, snapshot);
               Bukkit.getLogger().info("CosmeticData save data");
            });
        });
    }


    @EventHandler
    public void onDataSave(BukkitDataSaveEvent event) {
        event.editData(unpacked -> {
            PlayerData playerData = PlayerData.getPlayer(Bukkit.getOfflinePlayer(event.getUser().getUuid()));
            if(unpacked.getData(COSMETICS_ID).isPresent()) {
                CosmeticData cosmeticData = (CosmeticData) unpacked.getData(COSMETICS_ID).get();
                cosmeticData.setCosmetics(playerData.saveCosmetics());
                cosmeticData.setCosmeticsInUse(playerData.getCosmeticsInUse());
                return;
            }
            unpacked.setData(COSMETICS_ID, new CosmeticData(playerData.saveCosmetics(), playerData.getCosmeticsInUse()));
        });
    }

    /*
    @EventHandler
    public void onSyncComplete(BukkitSyncCompleteEvent event) {
        OnlineUser onlineUser = event.getUser();
        Player player = Bukkit.getPlayer(onlineUser.getUsername());
        plugin.getSql().loadPlayerAsync(player).thenAccept(playerData -> {
            if(!onlineUser.getData(COSMETICS_ID).isPresent()) return;
            CosmeticData cosmeticData = (CosmeticData) onlineUser.getData(COSMETICS_ID).get();
            playerData.loadCosmetics(cosmeticData.getCosmetics(), cosmeticData.getCosmeticsInUse());
        });
    }*/
}
