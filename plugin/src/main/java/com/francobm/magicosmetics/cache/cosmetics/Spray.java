package com.francobm.magicosmetics.cache.cosmetics;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.api.Cosmetic;
import com.francobm.magicosmetics.api.CosmeticType;
import com.francobm.magicosmetics.api.SprayKeys;
import com.francobm.magicosmetics.cache.Sound;
import com.francobm.magicosmetics.events.SprayDrawingEvent;
import com.francobm.magicosmetics.nms.spray.CustomSpray;
import com.francobm.magicosmetics.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.RayTraceResult;

import java.awt.image.BufferedImage;

public class Spray extends Cosmetic {

    private CustomSpray customSpray;
    private BukkitTask bukkitTask;
    private final BufferedImage image;
    private final boolean itemImage;
    private boolean paint = false;
    private long coolDown;

    public Spray(String id, String name, ItemStack itemStack, int modelData, boolean colored, CosmeticType cosmeticType, Color color, String permission, boolean texture, BufferedImage image, boolean itemImage, boolean hideMenu, boolean useEmote) {
        super(id, name, itemStack, modelData, colored, cosmeticType, color, permission, texture, hideMenu, useEmote);
        this.itemImage = itemImage;
        if(image == null) {
            this.image = null;
            return;
        }
        this.image = Utils.deepCopy(image);
    }

    public void draw(Player player, BlockFace blockFace, Location location, int rotation) {
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        clear(player);
        if(itemImage) {
            ItemStack item = getItemColor(player);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName("");
            item.setItemMeta(itemMeta);
            Utils.sendSound(player, Sound.getSound("spray"));
            customSpray = plugin.getVersion().createCustomSpray(player, location, blockFace, item, null, rotation);
            customSpray.spawn(player);
            customSpray.setPreview(true);
            bukkitTask = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if(customSpray == null) {
                    bukkitTask.cancel();
                    return;
                }
                clear(player);
            }, plugin.getSprayStayTime());
            return;
        }

        if(image != null) {
            ItemStack map = Utils.getMapImage(player, image, this);
            MapView mapView = ((MapMeta) map.getItemMeta()).getMapView();
            Utils.sendSound(player, Sound.getSound("spray"));
            customSpray = plugin.getVersion().createCustomSpray(player, location, blockFace, map.clone(), mapView, rotation);
            customSpray.spawn(player);
            customSpray.setPreview(true);
        }

        bukkitTask = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if(customSpray == null) {
                bukkitTask.cancel();
                return;
            }
            clear(player);
        }, plugin.getSprayStayTime());
    }

    public void draw(Player player, SprayKeys key){
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        if(plugin.getSprayCooldown() > 0) {
            if (coolDown > System.currentTimeMillis()) {
                int seconds = (int) ((coolDown - System.currentTimeMillis()) / 1000);
                plugin.getCosmeticsManager().sendMessage(player, plugin.prefix + plugin.getMessages().getString("spray-cooldown").replace("%time%", Utils.getTime(seconds)));
                return;
            }
            long milliseconds = plugin.getSprayCooldown() * 1000L;
            coolDown = System.currentTimeMillis() + milliseconds;
        }
        clear(player);
        if(itemImage) {
            ItemStack item = getItemColor(player);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName("");
            item.setItemMeta(itemMeta);
            Location location = player.getEyeLocation();
            RayTraceResult result = location.getWorld().rayTrace(location, location.getDirection(), 10, FluidCollisionMode.ALWAYS, false, 1, (entity) -> false);
            if(result == null) return;
            if(result.getHitEntity() != null && result.getHitEntity().getType() == EntityType.ITEM_FRAME) return;
            final int rotation;
            if(result.getHitBlockFace() == BlockFace.UP || result.getHitBlockFace() == BlockFace.DOWN) {
                rotation = Utils.getRotation(player.getLocation().getYaw(), false) * 45;
            } else {
                rotation = 0;
            }
            SprayDrawingEvent event = new SprayDrawingEvent(player, result.getHitBlock(), key);
            Bukkit.getPluginManager().callEvent(event);
            if(event.isCancelled()) return;
            Location frameLoc = result.getHitBlock().getRelative(result.getHitBlockFace()).getLocation();
            Utils.sendAllSound(frameLoc, Sound.getSound("spray"));
            customSpray = plugin.getVersion().createCustomSpray(player, frameLoc, result.getHitBlockFace(), item, null, rotation);
            active(player);
            bukkitTask = plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                if(customSpray == null) {
                    bukkitTask.cancel();
                    return;
                }
                clear(player);
            }, plugin.getSprayStayTime());
            return;
        }
        if(image != null) {
            ItemStack map = Utils.getMapImage(player, image, this);
            MapView mapView = ((MapMeta) map.getItemMeta()).getMapView();
            Location location = player.getEyeLocation();
            RayTraceResult result = location.getWorld().rayTrace(location, location.getDirection(), 10, FluidCollisionMode.ALWAYS, false, 1, (entity) -> false);
            if(result == null) return;
            if(result.getHitEntity() != null && result.getHitEntity().getType() == EntityType.ITEM_FRAME) return;
            final int rotation;
            if(result.getHitBlockFace() == BlockFace.UP || result.getHitBlockFace() == BlockFace.DOWN) {
                rotation = Utils.getRotation(player.getLocation().getYaw(), false) * 45;
            } else {
                rotation = 0;
            }
            SprayDrawingEvent event = new SprayDrawingEvent(player, result.getHitBlock(), key);
            Bukkit.getPluginManager().callEvent(event);
            if(event.isCancelled()) return;
            Location frameLoc = result.getHitBlock().getRelative(result.getHitBlockFace()).getLocation();
            Utils.sendAllSound(frameLoc, Sound.getSound("spray"));
            customSpray = plugin.getVersion().createCustomSpray(player, frameLoc, result.getHitBlockFace(), map.clone(), mapView, rotation);
            active(player);
        }

        bukkitTask = plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            if(customSpray == null) {
                bukkitTask.cancel();
                return;
            }
            clear(player);
        }, plugin.getSprayStayTime());
    }

    @Override
    public void active(Player player) {
        if(customSpray == null) return;
        if(customSpray.isPreview()) return;
        customSpray.spawn(false);
    }

    @Override
    public void clear(Player player) {
        if(customSpray != null) {
            customSpray.setPreview(false);
            customSpray.remove();
            customSpray = null;
        }
        if(bukkitTask != null) {
            bukkitTask.cancel();
            bukkitTask = null;
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    public boolean isPaint() {
        return paint;
    }

    public void setPaint(boolean paint) {
        this.paint = paint;
    }

    public boolean isItemImage() {
        return itemImage;
    }
}
