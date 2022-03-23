package com.francobm.magicosmetics.listeners;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.cache.PlayerCache;
import com.francobm.magicosmetics.utils.XMaterial;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

public class EntityListener implements Listener {

    @EventHandler
    public void onInteractArmorStand(PlayerArmorStandManipulateEvent event){
        ArmorStand armorStand = event.getRightClicked();
        if(!armorStand.hasMetadata("cosmetics")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void EntityUnleash(EntityUnleashEvent event){
        if(!(event.getEntity() instanceof PufferFish)) return;
        if(!event.getEntity().hasMetadata("cosmetics")) return;
        LivingEntity livingEntity = (LivingEntity) event.getEntity();
        if(!(livingEntity.getLeashHolder() instanceof Player)) return;
        Player player = (Player) livingEntity.getLeashHolder();
        livingEntity.setLeashHolder(null);

        new BukkitRunnable() {
            @Override
            public void run() {
                livingEntity.setLeashHolder(player);
                Optional<Item> lead = livingEntity.getNearbyEntities(15, 15, 15).stream()
                        .filter(entity -> entity instanceof Item)
                        .map(entity -> (Item)entity)
                        .filter(item -> item.getItemStack().getType() == XMaterial.LEAD.parseMaterial())
                        .findFirst();

                if(!lead.isPresent()){
                    cancel();
                    return;
                }
                lead.get().remove();
            }
        }.runTask(MagicCosmetics.getInstance());
    }
}
