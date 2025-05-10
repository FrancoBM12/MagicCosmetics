package com.francobm.magicosmetics.listeners;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.api.Cosmetic;
import com.francobm.magicosmetics.api.CosmeticType;
import com.francobm.magicosmetics.api.SprayKeys;
import com.francobm.magicosmetics.cache.*;
import com.francobm.magicosmetics.cache.cosmetics.CosmeticInventory;
import com.francobm.magicosmetics.events.CosmeticInventoryUpdateEvent;
import com.francobm.magicosmetics.events.PlayerChangeBlacklistEvent;
import com.francobm.magicosmetics.events.PlayerDataLoadEvent;
import com.francobm.magicosmetics.utils.Utils;
import com.francobm.magicosmetics.utils.XMaterial;
import org.bukkit.Location;
import org.bukkit.World;
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
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Iterator;
import java.util.Random;

public class PlayerListener implements Listener {
    private final MagicCosmetics plugin = MagicCosmetics.getInstance();

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        plugin.getVersion().getPacketReader().injectPlayer(player);
        if(plugin.isHuskSync() || plugin.isMpdb()){
            return;
        }
        plugin.getSql().loadPlayerAsync(player).thenAccept(playerData -> {
            if(plugin.isProxy()) {
                plugin.getServer().getScheduler().runTask(plugin, playerData::sendLoadPlayerData);
            }
        });
    }

    @EventHandler
    public void onLoadData(PlayerDataLoadEvent event){
        PlayerData playerData = event.getPlayerData();
        playerData.verifyWorldBlackList(plugin);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event){
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.getPlayer(player);
        if(!playerData.isZone()) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.getPlayer(player);
        /*if(plugin.isHuskSync()){
            plugin.getHuskSync().saveDataToPlayer(playerData);
            return;
        }*/
        plugin.getVersion().getPacketReader().removePlayer(player);
        if(playerData.isZone()){
            playerData.exitZoneSync();
        }
        plugin.getSql().savePlayerAsync(playerData);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTeleport(PlayerTeleportEvent event){
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.getPlayer(player);
        if(playerData.isZone()){
            if(!playerData.isSpectator()) return;
            event.setCancelled(true);
        }

        playerData.clearCosmeticsInUse(false);
        //PlayerBag.refreshPlayerBag(player);
        /*if(event.getFrom().getWorld() != null && event.getTo().getWorld() != null && event.getFrom().getWorld().getUID().equals(event.getTo().getWorld().getUID())) {
            if (event.getFrom().distanceSquared(event.getTo()) < 10) return;
        }*/
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
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.getPlayer(player);
        playerData.activeCosmeticsInventory();
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event){
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.getPlayer(player);
        if(playerData.getSpray() != null) {
            if (plugin.getSprayKey() == null) return;
            if (!plugin.getSprayKey().isKey(SprayKeys.SHIFT_Q)) return;
            if (!player.isSneaking()) return;
            event.setCancelled(true);
            playerData.draw(plugin.getSprayKey());
        }
        if(!Utils.isNewerThan1206()) return;
        //Method to prevent duplicated items when dropping
        String nbt = plugin.getVersion().isNBTCosmetic(event.getItemDrop().getItemStack());
        if(nbt == null || nbt.isEmpty()) return;
        event.getItemDrop().remove();
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event){
        Player player = event.getPlayer();
        if(!event.isSneaking()) return;
        plugin.getZonesManager().exitZone(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDead(PlayerDeathEvent event){
        Player player = event.getEntity();
        PlayerData playerData = PlayerData.getPlayer(player);
        if(playerData == null) return;
        playerData.clearCosmeticsInUse(false);
        if(event.getKeepInventory()) return;
        Iterator<ItemStack> stackList = event.getDrops().iterator();
        while (stackList.hasNext()){
            ItemStack itemStack = stackList.next();
            if(itemStack == null) break;
            if(playerData.getHat() != null && playerData.getHat().isCosmetic(itemStack)){
                stackList.remove();
                continue;
            }
            if(playerData.getWStick() != null && playerData.getWStick().isCosmetic(itemStack)){
                stackList.remove();
            }
        }
        if(playerData.getHat() != null && playerData.getHat().getCurrentItemSaved() != null){
            if(!event.getKeepInventory() && playerData.getHat().isOverlaps())
                event.getDrops().add(playerData.getHat().leftItemAndGet());
        }

        if(playerData.getWStick() != null && playerData.getWStick().getCurrentItemSaved() != null){
            if(!event.getKeepInventory() && playerData.getWStick().isOverlaps())
                event.getDrops().add(playerData.getWStick().leftItemAndGet());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemFrame(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.getPlayer(player);
        if(event.getHand() != EquipmentSlot.OFF_HAND) return;
        if(playerData.getWStick() == null) return;
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
    public void onInteractDupe(PlayerInteractEvent event){
        if(event.getHand() != EquipmentSlot.OFF_HAND) return;
        if(Utils.isNewerThan1206()) {
            if(!(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) {
                Player player = event.getPlayer();
                PlayerData playerData = PlayerData.getPlayer(player);
                if (playerData.getWStick() == null) return;
                ItemStack itemStack = event.getItem();
                if (itemStack == null) return;
                String nbt = plugin.getVersion().isNBTCosmetic(itemStack);
                if (nbt == null || nbt.isEmpty()) return;
                event.setCancelled(true);
                return;
            }
        }
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.getPlayer(player);
        if(playerData.getWStick() == null) return;
        ItemStack itemStack = event.getItem();
        if(itemStack == null) return;
        String nbt = plugin.getVersion().isNBTCosmetic(itemStack);
        if(nbt == null || nbt.isEmpty()) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.getPlayer(player);
        ItemStack itemStack = event.getItem();
        if(itemStack != null) {
            if(itemStack.getType() == XMaterial.BLAZE_ROD.parseMaterial()){
                String nbt = plugin.getVersion().isNBTCosmetic(itemStack);
                //plugin.getLogger().info("NBT: " + nbt);
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
                        if(playerData.getHat().isHideCosmetic()) return;
                        event.setCancelled(true);
                        ItemStack returnItem = playerData.getHat().changeItem(itemStack);
                        if(event.getHand() == EquipmentSlot.OFF_HAND){
                            player.getInventory().setItemInOffHand(returnItem);
                        }else{
                            player.getInventory().setItemInMainHand(returnItem);
                        }
                    }
                }
            }
            /*
            if(playerData.getWStick() != null && event.getHand() == EquipmentSlot.OFF_HAND) {
                event.setCancelled(true);
            }*/
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChange(PlayerSwapHandItemsEvent event){
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.getPlayer(player);
        ItemStack mainHand = event.getMainHandItem();
        if(playerData.getWStick() != null) {
            event.setCancelled(true);
            /*if(playerData.getWStick().isCosmetic(mainHand)) {
                if(!playerData.getWStick().isOverlaps()){
                    event.setMainHandItem(new ItemStack(Material.AIR));
                    return;
                }
                ItemStack itemStack = playerData.getWStick().leftItemAndGet();
                if(itemStack == null) return;
                event.setMainHandItem(itemStack);
            }*/
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
    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerDrop(PlayerDropItemEvent event){
        Player player = event.getPlayer();
        Item item = event.getItemDrop();
        PlayerData playerData = PlayerData.getPlayer(player);
        if(playerData.getHat() != null) {
            if (playerData.getHat().isCosmetic(item.getItemStack())){
                event.setCancelled(false);
                item.remove();
            }
        }
        if(playerData.getWStick() != null){
            if (playerData.getWStick().isCosmetic(item.getItemStack())) {
                event.setCancelled(false);
                item.remove();
            }
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChangeWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerData.getPlayer(player);
        playerData.verifyWorldBlackList(plugin);
    }

    @EventHandler
    public void onInteractInventory(CosmeticInventoryUpdateEvent event) {
        Player player = event.getPlayer();
        Cosmetic cosmetic = event.getCosmetic();
        if(cosmetic.isHideCosmetic()) return;
        ItemStack itemStack = event.getItemToChange();
        CosmeticInventory cosmeticInventory = (CosmeticInventory) cosmetic;
        if(itemStack == null || itemStack.getType().isAir()){
            if(!cosmeticInventory.isOverlaps()) {
                cosmeticInventory.setCurrentItemSaved(null);
            }
            cosmetic.update();
            return;
        }
        if(plugin.getMagicCrates() != null && plugin.getMagicCrates().hasInCrate(player)) return;
        if(plugin.getMagicGestures() != null && plugin.getMagicGestures().hasInWardrobe(player)) return;
        boolean hasItemSaved = cosmeticInventory.getCurrentItemSaved() != null;
        if(hasItemSaved) {
            if (itemStack.isSimilar(cosmeticInventory.getCurrentItemSaved())) return;
        }
        if(!cosmeticInventory.isOverlaps()) {
            if(cosmetic.isCosmetic(itemStack)) return;
            if(player.getInventory().getItemInMainHand().getType().isAir() || cosmetic.isCosmetic(player.getInventory().getItemInMainHand())){
                player.getInventory().setItemInMainHand(null);
            }
            cosmeticInventory.setCurrentItemSaved(itemStack);
            return;
        }
        ItemStack oldItem = cosmeticInventory.changeItem(itemStack);
        if(oldItem == null) {
            if(cosmetic.isCosmetic(player.getInventory().getItemInMainHand())) {
                player.getInventory().setItemInMainHand(null);
            }
            return;
        }
        if(hasItemSaved && oldItem.isSimilar(cosmeticInventory.getCurrentItemSaved())) return;
        if(itemStack.isSimilar(oldItem)) return;
        if(player.getOpenInventory().getType() == InventoryType.PLAYER) {
            player.setItemOnCursor(oldItem);
            return;
        }
        if(player.getInventory().getItemInMainHand().getType().isAir() || cosmetic.isCosmetic(player.getInventory().getItemInMainHand())){
            player.getInventory().setItemInMainHand(oldItem);
            return;
        }
        player.getInventory().addItem(oldItem);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInventory(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        PlayerData playerData = PlayerData.getPlayer(player);
        if(playerData == null) return;
        if(event.getClickedInventory() == null) return;
        if(event.getClickedInventory().getType() != InventoryType.PLAYER) {
            if(playerData.getWStick() != null && event.getClick() == ClickType.SWAP_OFFHAND) event.setCancelled(true);
            return;
        }
        if(playerData.getWStick() != null) {
            if(playerData.getWStick().isHideCosmetic()) return;
            if (event.getClick() == ClickType.SWAP_OFFHAND) {
                event.setCancelled(true);
                return;
            }
            if(event.getCursor() != null) {
                if (playerData.getWStick().isCosmetic(event.getCursor()))
                    player.setItemOnCursor(null);
            }
            if(event.getSlotType() == InventoryType.SlotType.QUICKBAR && event.getSlot() == 40){
                if(event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT || event.getClick() == ClickType.RIGHT || (playerData.getWStick().isCosmetic(event.getCurrentItem()) && event.getCursor() == null && playerData.getWStick().getCurrentItemSaved() == null || playerData.getWStick().isCosmetic(event.getCurrentItem()) && event.getCursor() != null && event.getCursor().getType().isAir() && playerData.getWStick().getCurrentItemSaved() == null)) {
                    event.setCancelled(true);
                    return;
                }

                if (event.getClick() == ClickType.DROP || event.getClick() == ClickType.CONTROL_DROP) {
                    if(playerData.getWStick().getCurrentItemSaved() != null){
                        playerData.getWStick().dropItem(event.getClick() == ClickType.CONTROL_DROP);
                        event.setCancelled(playerData.getWStick().isOverlaps());
                    }
                    return;
                }
                event.setCancelled(true);
                ItemStack returnItem = playerData.getWStick().changeItem(event.getCursor() != null && event.getCursor().getType().isAir() ? null : event.getCursor());
                player.setItemOnCursor(returnItem);
                return;
            }
        }
        if (playerData.getHat() != null) {
            if(playerData.getHat().isHideCosmetic()) return;
            if(event.getCursor() != null){
                if(playerData.getHat().isCosmetic(event.getCursor()))
                    player.setItemOnCursor(null);
            }
            if(event.getSlotType() == InventoryType.SlotType.ARMOR && event.getSlot() == 39){
                if(event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT || event.getClick() == ClickType.RIGHT || (playerData.getHat().isCosmetic(event.getCurrentItem()) && event.getCursor() == null && playerData.getHat().getCurrentItemSaved() == null || playerData.getHat().isCosmetic(event.getCurrentItem()) && event.getCursor() != null && event.getCursor().getType().isAir() && playerData.getHat().getCurrentItemSaved() == null)) {
                    event.setCancelled(true);
                    return;
                }

                if (event.getClick() == ClickType.DROP || event.getClick() == ClickType.CONTROL_DROP) {
                    if(playerData.getHat().getCurrentItemSaved() != null){
                        playerData.getHat().dropItem(event.getClick() == ClickType.CONTROL_DROP);
                        event.setCancelled(playerData.getHat().isOverlaps());
                        //plugin.getLogger().info("Hat Cosmetic Dropped");
                    }
                    return;
                }

                event.setCancelled(true);
                if(event.getCursor() == null || event.getCursor().getType().isAir() || event.getCursor().getType().name().endsWith("HELMET") || event.getCursor().getType().name().endsWith("HEAD") || player.hasPermission("magicosmetics.hat.use")) {
                    ItemStack returnItem = playerData.getHat().changeItem(event.getCursor() != null && event.getCursor().getType().isAir() ? null : event.getCursor());
                    player.setItemOnCursor(returnItem);
                }
            }
        }
    }
}