package com.francobm.magicosmetics.listeners;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.utils.XMaterial;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

public class EntityListener implements Listener {

    private MagicCosmetics plugin = MagicCosmetics.getInstance();

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
        plugin.getServer().getScheduler().runTask(plugin, (task) -> {
            livingEntity.setLeashHolder(player);
            Optional<Item> lead = livingEntity.getNearbyEntities(15, 15, 15).stream()
                    .filter(entity -> entity instanceof Item)
                    .map(entity -> (Item)entity)
                    .filter(item -> item.getItemStack().getType() == XMaterial.LEAD.parseMaterial())
                    .findFirst();

            if(!lead.isPresent()){
                task.cancel();
                return;
            }
            lead.get().remove();
        });
    }
}
