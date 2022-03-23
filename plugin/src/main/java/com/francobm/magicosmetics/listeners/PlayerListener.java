package com.francobm.magicosmetics.listeners;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.cache.*;
import com.francobm.magicosmetics.nms.NPC.NPC;
import com.francobm.magicosmetics.nms.bag.PlayerBag;
import com.francobm.magicosmetics.events.UnknownEntityInteractEvent;
import com.francobm.magicosmetics.nms.balloon.PlayerBalloon;
import com.francobm.magicosmetics.utils.XMaterial;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;

public class PlayerListener implements Listener {
    private final MagicCosmetics plugin = MagicCosmetics.getInstance();

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        plugin.getSql().loadPlayer(player);
        plugin.getVersion().getPacketReader(player).inject();
    }

    /*@EventHandler
    public void onGameMode(PlayerGameModeChangeEvent event){
        Player player = event.getPlayer();
        GameMode gameMode = event.getNewGameMode();
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        if(playerCache.getEquip(CosmeticType.BAG) != null) {
            Bag bag = (Bag) playerCache.getEquip(CosmeticType.BAG);
            if(bag.isSpectator()){
                bag.setSpectator(false);
                playerCache.clearBag();
                return;
            }
            if (gameMode == GameMode.SPECTATOR) {
                playerCache.clearCosmeticsInUse();
                bag.setSpectator(true);
            }

        }
    }*/

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event){
        Player player = event.getPlayer();
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        if(!playerCache.isZone()) return;
        event.setCancelled(true);
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
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        if(playerCache.isZone()){
            event.setCancelled(true);
        }
        //PlayerBag.refreshPlayerBag(player);
        playerCache.clearCosmeticsInUse();
    }

    @EventHandler
    public void onUnleash(PlayerUnleashEntityEvent event){
        if(!(event.getEntity() instanceof PufferFish)) return;
        if(!event.getEntity().hasMetadata("cosmetics")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void OnLeash(PlayerLeashEntityEvent event){
        if(!(event.getEntity() instanceof PufferFish)) return;
        if(!event.getEntity().hasMetadata("cosmetics")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onUnkInteract(UnknownEntityInteractEvent event){
        Player player = event.getPlayer();
        NPC entity = event.getUnknownEntity();
        if(!(entity.getPunchEntity() instanceof ArmorStand)) return;
        if(event.getAction() == com.francobm.magicosmetics.events.Action.INTERACT) return;
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
        //PlayerBag.refreshPlayerBag(player);
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
        if(playerCache.getHat() != null) {
            if (playerCache.getHat().isCosmetic(itemStack)) {
                player.getInventory().removeItem(itemStack);
                event.setCancelled(true);
                return;
            }
        }
        if(playerCache.getWStick() != null) {
            if (playerCache.getWStick().isCosmetic(itemStack)) {
                player.getInventory().removeItem(itemStack);
                event.setCancelled(true);
            }
        }
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
            if (playerCache.getHat() != null) {
                if (playerCache.getHat().isCosmetic(oldItem)) {
                    player.getInventory().removeItem(oldItem);
                }
            }
            if (playerCache.getWStick() != null) {
                if (playerCache.getWStick().isCosmetic(oldItem)) {
                    player.getInventory().removeItem(oldItem);
                }
            }
        }
        if(newItem != null) {
            PlayerCache playerCache = PlayerCache.getPlayer(player);
            if (playerCache.getHat() != null) {
                if (playerCache.getHat().isCosmetic(newItem)) {
                    player.getInventory().removeItem(newItem);
                }
            }
            if(playerCache.getWStick() != null){
                if (playerCache.getWStick().isCosmetic(newItem)) {
                    player.getInventory().removeItem(newItem);
                }
            }
        }
    }

    /**
     * remove te item when drop
     */
    @EventHandler
    public void playerDrop(PlayerDropItemEvent event){
        Player player = event.getPlayer();
        Item item = event.getItemDrop();
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        if(playerCache.getHat() != null) {
            if (playerCache.getHat().isCosmetic(item.getItemStack())){
                item.remove();
            }
        }
        if(playerCache.getWStick() != null){
            if (playerCache.getWStick().isCosmetic(item.getItemStack())) {
                item.remove();
            }
        }
    }

    @EventHandler
    public void onInventory(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        if(playerCache == null) return;
        if(event.getClickedInventory() == null) return;
        if(playerCache.getWStick() != null) {
            if (playerCache.getWStick().isCosmetic(player.getInventory().getItemInOffHand())) {
                if (event.getClick() == ClickType.SWAP_OFFHAND) {
                    event.setCancelled(true);
                    return;
                }
                if(event.getSlotType() == InventoryType.SlotType.QUICKBAR && event.getSlot() == 40){
                    event.setCancelled(true);
                    event.setCurrentItem(player.getItemOnCursor());
                    player.setItemOnCursor(XMaterial.AIR.parseItem());
                    return;
                }
            }
            if(event.getCurrentItem() == null) return;
            /*if(event.getSlotType() == InventoryType.SlotType.QUICKBAR){
                if(playerCache.getWStick().isCosmetic(event.getCurrentItem())) {
                    if(event.getSlot() == 40){
                        event.setCancelled(true);
                        return;
                    }
                }
            }*/
            if(playerCache.getWStick().isCosmetic(event.getCurrentItem())){
                event.setCancelled(true);
                event.setCurrentItem(XMaterial.AIR.parseItem());
                return;
            }
        }
        if (playerCache.getHat() != null) {
            if (event.getCurrentItem() == null) {
                return;
            }

            if (playerCache.getHat().isCosmetic(event.getCurrentItem())) {
                event.setCurrentItem(XMaterial.AIR.parseItem());
                event.setCancelled(true);
                return;
            }

            if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
                if (event.getCurrentItem() == null) {
                    return;
                }

                if (playerCache.getHat().isCosmetic(event.getCurrentItem())) {
                    if (event.getSlot() != 5 && event.getSlot() != 39) {
                        event.setCurrentItem(null);
                        event.setCancelled(true);
                        playerCache.activeHat();
                        return;
                    }

                    event.setCancelled(true);
                    return;
                }

                if (!playerCache.isZone() && (event.getSlot() == 5 || event.getSlot() == 39)) {
                    playerCache.getHat().active(player);
                }
            }
        }
    }
}
