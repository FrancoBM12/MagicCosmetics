package com.francobm.magicosmetics.listeners;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.api.SprayKeys;
import com.francobm.magicosmetics.cache.*;
import com.francobm.magicosmetics.nms.NPC.NPC;
import com.francobm.magicosmetics.events.UnknownEntityInteractEvent;
import com.francobm.magicosmetics.utils.XMaterial;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;

public class PlayerListener implements Listener {
    private final MagicCosmetics plugin = MagicCosmetics.getInstance();

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        plugin.getSql().loadPlayer(player, true);
        plugin.getVersion().getPacketReader(player).inject();
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event){
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.getPlayer(player);
        if(!playerData.isZone()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.getPlayer(player);
        if(playerData.isZone()){
            playerData.exitZoneSync();
        }
        plugin.getSql().asyncSavePlayer(playerData);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event){
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.getPlayer(player);
        if(playerData.isZone()){
            if(!playerData.isSpectator()) return;
            event.setCancelled(true);
        }
        //PlayerBag.refreshPlayerBag(player);
        playerData.clearCosmeticsInUse();
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
        if(entity == null) return;
        if(event.getAction() == com.francobm.magicosmetics.events.Action.INTERACT) return;
        plugin.getCosmeticsManager().openMenu(player, plugin.getMainMenu());
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event){
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.getPlayer(player);
        if(playerData.getSpray() == null) return;
        if(plugin.getSprayKey() == null)  return;
        if (!plugin.getSprayKey().isKey(SprayKeys.SHIFT_Q)) return;
        if (!player.isSneaking()) return;
        event.setCancelled(true);
        playerData.draw(plugin.getSprayKey());
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
        PlayerData playerData = PlayerData.getPlayer(player);
        if(playerData == null) return;
        playerData.clearCosmeticsInUse();
        Iterator<ItemStack> stackList = event.getDrops().iterator();
        while (stackList.hasNext()){
            ItemStack itemStack = stackList.next();
            if(itemStack == null) break;
            if(playerData.getHat() != null && playerData.getHat().isCosmetic(itemStack)){
                stackList.remove();
            }
            if(playerData.getWStick() != null && playerData.getWStick().isCosmetic(itemStack)){
                stackList.remove();
            }
        }
    }

    @EventHandler
    public void onItemFrame(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();
        if(!(event.getRightClicked() instanceof ItemFrame)) return;
        PlayerData playerData = PlayerData.getPlayer(player);
        if(event.getHand() != EquipmentSlot.OFF_HAND) return;
        if(playerData.getWStick() == null) return;
        if(!playerData.getWStick().isCosmetic(player.getInventory().getItemInOffHand())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlock(BlockPlaceEvent event) {
        if(event.getHand() != EquipmentSlot.OFF_HAND) return;
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.getPlayer(player);
        if(playerData.getWStick() == null) return;
        if(!playerData.getWStick().isCosmetic(event.getItemInHand())) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.getPlayer(player);
        ItemStack itemStack = event.getItem();
        if(itemStack != null) {
            //plugin.getLogger().info("Material: " + itemStack.getType());
            if(itemStack.getType() == XMaterial.BLAZE_ROD.parseMaterial()){
                String nbt = plugin.getVersion().isNBTCosmetic(itemStack);
                if(!nbt.startsWith("wand")) return;
                Zone zone = Zone.getZone(nbt.substring(4));
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
                    if (playerData.getHat() != null) {
                        if(player.getInventory().getHelmet() == null) return;
                        if(playerData.getHat().isCosmetic(player.getInventory().getHelmet())) {
                            player.getInventory().setHelmet(itemStack.clone());
                            if(event.getHand() == EquipmentSlot.OFF_HAND){
                                player.getInventory().setItemInOffHand(null);
                            }else{
                                player.getInventory().removeItem(itemStack);
                            }
                        }
                    }
                }
            }
            if(playerData.getHat() != null) {
                if (playerData.getHat().isCosmetic(itemStack)) {
                    player.getInventory().removeItem(itemStack);
                    event.setCancelled(true);
                    return;
                }
            }
            if(playerData.getWStick() != null) {
                if(event.getHand() == EquipmentSlot.OFF_HAND) {
                    if (playerData.getWStick().isCosmetic(itemStack)) {
                        player.getInventory().removeItem(itemStack);
                        event.setUseInteractedBlock(Event.Result.DENY);
                        event.setUseItemInHand(Event.Result.DENY);
                        event.setCancelled(true);
                    }
                }
            }
        }
        if(plugin.getSprayKey() == null) return;
        if(playerData.getSpray() == null) return;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (!plugin.getSprayKey().isKey(SprayKeys.SHIFT_RC)) return;
            if (!player.isSneaking()) return;
            playerData.draw(plugin.getSprayKey());
            event.setCancelled(true);
        }
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (!plugin.getSprayKey().isKey(SprayKeys.SHIFT_LC)) return;
            if (!player.isSneaking()) return;
            playerData.draw(plugin.getSprayKey());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerChange(PlayerSwapHandItemsEvent event){
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.getPlayer(player);
        ItemStack mainHand = event.getMainHandItem();
        ItemStack offHand = event.getOffHandItem();

        if(mainHand != null){
            if(playerData.getWStick() != null){
                if(playerData.getWStick().isCosmetic(mainHand)){
                    mainHand.setType(XMaterial.AIR.parseMaterial());
                }
                if(offHand == null){
                    playerData.getWStick().active(player);
                }
                if(offHand != null && offHand.getType() == XMaterial.AIR.parseMaterial()){
                    playerData.getWStick().active(player);
                }
            }
        }
        if(offHand != null) {
            if (playerData.getWStick() != null) {
                if(playerData.getWStick().isCosmetic(offHand)){
                    offHand.setType(XMaterial.AIR.parseMaterial());
                }
            }
        }

        if(playerData.getSpray() == null) return;
        if(plugin.getSprayKey() == null) return;
        if (!plugin.getSprayKey().isKey(SprayKeys.SHIFT_F)) return;
        if (!player.isSneaking()) return;
        playerData.draw(plugin.getSprayKey());
        event.setCancelled(true);
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
            PlayerData playerData = PlayerData.getPlayer(player);
            if (playerData.getHat() != null) {
                if (playerData.getHat().isCosmetic(oldItem)) {
                    player.getInventory().removeItem(oldItem);
                }
            }
            if (playerData.getWStick() != null) {
                if (playerData.getWStick().isCosmetic(oldItem)) {
                    player.getInventory().removeItem(oldItem);
                }
            }
        }
        if(newItem != null) {
            PlayerData playerData = PlayerData.getPlayer(player);
            if (playerData.getHat() != null) {
                if (playerData.getHat().isCosmetic(newItem)) {
                    player.getInventory().removeItem(newItem);
                }
            }
            if(playerData.getWStick() != null){
                if (playerData.getWStick().isCosmetic(newItem)) {
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
        PlayerData playerData = PlayerData.getPlayer(player);
        if(playerData.getHat() != null) {
            if (playerData.getHat().isCosmetic(item.getItemStack())){
                item.remove();
            }
        }
        if(playerData.getWStick() != null){
            if (playerData.getWStick().isCosmetic(item.getItemStack())) {
                item.remove();
            }
        }
    }

    @EventHandler
    public void onInventory(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        PlayerData playerData = PlayerData.getPlayer(player);
        if(playerData == null) return;
        if(event.getClickedInventory() == null) return;
        if(event.getClickedInventory().getType() == InventoryType.PLAYER) {
            if(playerData.getWStick() != null) {
                if (event.getClick() == ClickType.SWAP_OFFHAND) {
                    event.setCancelled(true);
                    return;
                }
                if (playerData.getWStick().isCosmetic(player.getInventory().getItemInOffHand())) {
                    if(event.getSlotType() == InventoryType.SlotType.QUICKBAR && event.getSlot() == 40){
                        event.setCancelled(true);
                        if(player.getItemOnCursor().getType() == XMaterial.AIR.parseMaterial()) return;
                        event.setCurrentItem(player.getItemOnCursor());
                        player.setItemOnCursor(XMaterial.AIR.parseItem());
                        return;
                    }
                }
                if(event.getCurrentItem() == null) return;
                if(playerData.getWStick().isCosmetic(event.getCurrentItem())){
                    event.setCancelled(true);
                    event.setCurrentItem(XMaterial.AIR.parseItem());
                    return;
                }
            }
            if (playerData.getHat() != null) {
                if (event.getCurrentItem() == null) {
                    return;
                }
                if(playerData.getHat().isCosmetic(event.getCursor())){
                    player.setItemOnCursor(XMaterial.AIR.parseItem());
                }
                if (playerData.getHat().isCosmetic(player.getInventory().getHelmet())) {
                    if(event.getSlotType() == InventoryType.SlotType.ARMOR && event.getSlot() == 39){
                        event.setCancelled(true);
                        if(player.getItemOnCursor().getType() == XMaterial.AIR.parseMaterial()) return;
                        if(player.getItemOnCursor().getType().name().endsWith("HELMET") || player.getItemOnCursor().getType().name().endsWith("HEAD")) {
                            event.setCurrentItem(event.getCursor());
                            player.setItemOnCursor(XMaterial.AIR.parseItem());
                        }
                        return;
                    }

                    if (playerData.getHat().isCosmetic(event.getCurrentItem())) {
                        event.setCurrentItem(XMaterial.AIR.parseItem());
                        event.setCancelled(true);
                        return;
                    }
                }

                if (playerData.getHat().isCosmetic(event.getCurrentItem())) {
                    event.setCurrentItem(XMaterial.AIR.parseItem());
                    event.setCancelled(true);
                }

            }
            return;
        }
        if(playerData.getWStick() != null) {
            if (playerData.getWStick().isCosmetic(player.getInventory().getItemInOffHand())) {
                if (event.getClick() == ClickType.SWAP_OFFHAND) {
                    event.setCancelled(true);
                    return;
                }
            }}
        if (playerData.getHat() != null) {
            if (event.getCurrentItem() == null) {
                return;
            }
            if(playerData.getHat().isCosmetic(event.getCursor())){
                player.setItemOnCursor(XMaterial.AIR.parseItem());
            }
            if (playerData.getHat().isCosmetic(player.getInventory().getHelmet())) {
                if(event.getSlotType() == InventoryType.SlotType.ARMOR && event.getSlot() == 39){
                    event.setCancelled(true);
                    if(player.getItemOnCursor().getType() == XMaterial.AIR.parseMaterial()) return;
                    if(player.getItemOnCursor().getType().name().endsWith("HELMET") || player.getItemOnCursor().getType().name().endsWith("HEAD")) {
                        event.setCurrentItem(event.getCursor());
                        player.setItemOnCursor(XMaterial.AIR.parseItem());
                    }
                }
            }

        }
    }
}