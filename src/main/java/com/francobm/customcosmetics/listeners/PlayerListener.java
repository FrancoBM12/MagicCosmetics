package com.francobm.customcosmetics.listeners;

import com.francobm.customcosmetics.CustomCosmetics;
import com.francobm.customcosmetics.cache.PlayerCache;
import com.francobm.customcosmetics.cache.Zone;
import com.francobm.customcosmetics.cache.nms.PlayerBag;
import com.francobm.customcosmetics.cache.nms.PlayerBalloon;
import com.francobm.customcosmetics.events.UnknownEntityInteractEvent;
import com.francobm.customcosmetics.models.PacketReader;
import com.francobm.customcosmetics.nms.NPC.ItemSlot;
import com.francobm.customcosmetics.nms.NPC.NPC;
import com.francobm.customcosmetics.utils.Utils;
import com.francobm.customcosmetics.utils.XMaterial;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Iterator;

public class PlayerListener implements Listener {
    private final CustomCosmetics plugin = CustomCosmetics.getInstance();

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        plugin.getSql().loadPlayer(player);
        PacketReader packetReader = new PacketReader(player);
        packetReader.inject();
        PlayerBag.addPlayerBagByPlayer(player);
        PlayerBalloon.addPlayerBalloonByPlayer(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        if(playerCache.isZone()){
            playerCache.exitZoneSync();
        }
        plugin.getSql().savePlayer(playerCache);
        PlayerBag.removePlayerBagByPlayer(player);
        PlayerBalloon.removePlayerBalloonByPlayer(player);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event){
        Player player = event.getPlayer();
        PlayerBag.refreshPlayerBag(player);
        PlayerBalloon.refreshPlayerBalloon(player);
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        playerCache.clearCosmeticsInUse();
    }

    @EventHandler
    public void onLeash(PlayerUnleashEntityEvent event){
        if(!(event.getEntity() instanceof PufferFish)) return;
        if(!event.getEntity().hasMetadata("cosmetics")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        if(playerCache == null) return;
        if(!playerCache.isZone()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onUnkInteract(UnknownEntityInteractEvent event){
        Player player = event.getPlayer();
        NPC entity = event.getUnknownEntity();
        if(!(entity.getEntity() instanceof Player)) return;
        if(event.getAction() == com.francobm.customcosmetics.events.Action.INTERACT) return;
        plugin.getCosmeticsManager().openMenu(player, "hat");
        //entity.equipNPC(player, ItemSlot.HELMET, XMaterial.DIAMOND_HELMET.parseItem());
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event){
        Player player = event.getPlayer();
        if(!event.isSneaking()) return;
        plugin.getCosmeticsManager().exitZone(player);
    }

    @EventHandler
    public void onDead(PlayerDeathEvent event){
        Player player = event.getEntity();
        PlayerBag.refreshPlayerBag(player);
        PlayerBalloon.refreshPlayerBalloon(player);
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        if(playerCache == null) return;
        playerCache.clearBalloon();
        playerCache.clearBag();
        Iterator<ItemStack> stackList = event.getDrops().iterator();
        while (stackList.hasNext()){
            ItemStack itemStack = stackList.next();
            if(playerCache.getHat() != null){
                if(playerCache.getHat().isCosmetic(itemStack)){
                    stackList.remove();
                }
            }
            if(playerCache.getWStick() != null){
                if(playerCache.getWStick().isCosmetic(itemStack)){
                    stackList.remove();
                }
            }
        }
    }

    @EventHandler
    public void onItemFrame(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();
        if(!(event.getRightClicked() instanceof ItemFrame)) return;
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        if(playerCache.getWStick() == null) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event){
        Player player = event.getPlayer();
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        ItemStack itemStack = event.getItem();
        if(itemStack == null) return;
        //plugin.getLogger().info("Material: " + itemStack.getType());
        if(itemStack.getType() == XMaterial.BLAZE_ROD.parseMaterial()){
            if(!itemStack.hasItemMeta()) return;
            if(!itemStack.getItemMeta().hasDisplayName()) return;
            if(itemStack.getItemMeta().getDisplayName().split(" ").length < 6) return;
            ItemMeta itemMeta = itemStack.getItemMeta();
            Zone zone = Zone.getZone(itemMeta.getDisplayName().split(" ")[6]);
            if(zone == null) return;
            event.setCancelled(true);
            if(event.getAction() == Action.LEFT_CLICK_BLOCK){
                Location location = event.getClickedBlock().getLocation();
                zone.setCorn1(location);
                player.sendMessage(plugin.prefix + plugin.getMessages().getString("set-corn1").replace("%name%", zone.getName()));
                return;
            }
            if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
                Location location = event.getClickedBlock().getLocation();
                zone.setCorn2(location);
                player.sendMessage(plugin.prefix + plugin.getMessages().getString("set-corn2").replace("%name%", zone.getName()));
                return;
            }
            return;
        }
        if(itemStack.getType().toString().toUpperCase().endsWith("HELMET")){
            if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (playerCache.getHat() != null) {
                    if(player.getInventory().getHelmet() == null) return;
                    if(playerCache.getHat().isCosmetic(player.getInventory().getHelmet())) {
                        player.getInventory().setHelmet(itemStack.clone());
                        player.getInventory().removeItem(itemStack);
                    }
                }
            }
        }
        if(playerCache.getHat() == null) return;
        if(event.getHand() != EquipmentSlot.HAND) return;
        if (!playerCache.getHat().isCosmetic(itemStack)) return;
        player.getInventory().removeItem(itemStack);
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerChange(PlayerSwapHandItemsEvent event){
        Player player = event.getPlayer();
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        ItemStack mainHand = event.getMainHandItem();
        ItemStack offHand = event.getOffHandItem();

        if(mainHand != null){
            if(playerCache.getWStick() != null){
                if(playerCache.getWStick().isCosmetic(mainHand)){
                    mainHand.setType(XMaterial.AIR.parseMaterial());
                }
                if(offHand == null){
                    playerCache.getWStick().active(player);
                    return;
                }
                if(offHand.getType() == XMaterial.AIR.parseMaterial()){
                    playerCache.getWStick().active(player);
                    return;
                }
                return;
            }
        }
        if(offHand != null) {
            if (playerCache.getWStick() != null) {
                if(playerCache.getWStick().isCosmetic(offHand)){
                    offHand.setType(XMaterial.AIR.parseMaterial());
                }
            }
        }
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event){
        if(!(event.getDamager() instanceof Player)) return;
        if(!(event.getEntity() instanceof PufferFish)) return;
        if(!event.getEntity().hasMetadata("cosmetics")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void playerHeld(PlayerItemHeldEvent event){
        Player player = event.getPlayer();
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        ItemStack oldItem = player.getInventory().getItem(event.getPreviousSlot());
        if (oldItem != null) {
            PlayerCache playerCache = PlayerCache.getPlayer(player);
            if (playerCache.getHat() == null) return;
            if (!playerCache.getHat().isCosmetic(oldItem)) return;
            player.getInventory().removeItem(oldItem);
        }
        if(newItem != null) {
            PlayerCache playerCache = PlayerCache.getPlayer(player);
            if (playerCache.getHat() == null) return;
            if (!playerCache.getHat().isCosmetic(newItem)) return;
            player.getInventory().removeItem(newItem);
        }
    }

    @EventHandler
    public void playerDrop(PlayerDropItemEvent event){
        Player player = event.getPlayer();
        Item item = event.getItemDrop();
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        if(playerCache.getHat() == null) return;
        if(!playerCache.getHat().isCosmetic(item.getItemStack())) return;
        item.remove();
    }

    @EventHandler
    public void onInventory(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        if(playerCache == null) return;
        if(event.getClickedInventory() == null) return;
        //plugin.getLogger().info("Inv Slot: " + event.getSlot());
        if(playerCache.getWStick() != null) {
            if (playerCache.getWStick().isCosmetic(player.getInventory().getItemInOffHand())) {
                if (event.getClick() == ClickType.SWAP_OFFHAND) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        if(playerCache.getHat() != null) {
            if (event.getSlotType() == InventoryType.SlotType.QUICKBAR) {
                if (event.getCurrentItem() == null) {
                    return;
                }
                if (playerCache.getHat().isCosmetic(event.getCurrentItem())) {
                    event.setCurrentItem(null);
                    event.setCancelled(true);
                    return;
                }
            }
        }
        if(event.getSlotType() == InventoryType.SlotType.ARMOR){
            if(playerCache.getHat() != null) {
                if(event.getCurrentItem() == null){
                    return;
                }
                if(playerCache.getHat().isCosmetic(event.getCurrentItem())) {
                    if (event.getSlot() == 5 || event.getSlot() == 39) {
                        event.setCancelled(true);
                        return;
                    }else{
                        event.setCurrentItem(null);
                        event.setCancelled(true);
                    }
                    return;
                }
                if (event.getSlot() == 5 || event.getSlot() == 39) {
                    playerCache.getHat().active(player);
                    return;
                }
            }
        }
        if(event.getClickedInventory().getType() == InventoryType.PLAYER){
            if(playerCache.getWStick() != null){
                if(event.getCurrentItem() == null) return;
                if(playerCache.getWStick().isCosmetic(event.getCurrentItem())) {
                    if(event.getSlot() == 40){
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
