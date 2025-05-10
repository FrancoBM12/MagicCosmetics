package com.francobm.magicosmetics.nms.version;

import com.francobm.magicosmetics.models.PacketReader;
import com.francobm.magicosmetics.nms.IRangeManager;
import com.francobm.magicosmetics.nms.NPC.ItemSlot;
import com.francobm.magicosmetics.nms.NPC.NPC;
import com.francobm.magicosmetics.nms.bag.EntityBag;
import com.francobm.magicosmetics.nms.bag.PlayerBag;
import com.francobm.magicosmetics.nms.balloon.EntityBalloon;
import com.francobm.magicosmetics.nms.balloon.PlayerBalloon;
import com.francobm.magicosmetics.nms.spray.CustomSpray;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;

public abstract class Version {
    protected static final UUID RANDOM_UUID = UUID.fromString("92864445-51c5-4c3b-9039-517c9927d1b4");


    protected PacketReader packetReader;

    public PacketReader getPacketReader() {
        return packetReader;
    }

    public abstract void setSpectator(Player player);

    public abstract void createNPC(Player player);

    public abstract void createNPC(Player player, Location location);

    public abstract NPC getNPC(Player player);

    public abstract void removeNPC(Player player);

    public abstract NPC getNPC();

    public abstract void equip(LivingEntity livingEntity, ItemSlot itemSlot, ItemStack itemStack);

    public abstract void setCamera(Player player, Entity entity);


    public abstract PlayerBag createPlayerBag(Player player, double distance, float height, ItemStack backPackItem, ItemStack backPackItemForMe, boolean isDisplay);

    public abstract EntityBag createEntityBag(Entity entity, double distance);

    public abstract PlayerBalloon createPlayerBalloon(Player player, double space, double distance, boolean bigHead, boolean invisibleLeash);

    public abstract EntityBalloon createEntityBalloon(Entity entity, double space, double distance, boolean bigHead, boolean invisibleLeash);

    public abstract CustomSpray createCustomSpray(Player player, Location location, BlockFace blockFace, ItemStack itemStack, MapView mapView, int rotation);

    public abstract void updateTitle(Player player, String title);

    public abstract ItemStack setNBTCosmetic(ItemStack itemStack, String key);

    public abstract String isNBTCosmetic(ItemStack itemStack);

    public abstract PufferFish spawnFakePuffer(Location location);

    public abstract ArmorStand spawnArmorStand(Location location);

    public abstract void showEntity(LivingEntity entity, Player ...viewers);

    public abstract void despawnFakeEntity(Entity entity, Player ...viewers);

    public abstract void attachFakeEntity(Entity entity, Entity leashed, Player ...viewers);

    public abstract void updatePositionFakeEntity(Entity leashed, Location location);

    public abstract void teleportFakeEntity(Entity leashed, Set<Player> viewers);

    public abstract ItemStack getItemWithNBTsCopy(ItemStack itemToCopy, ItemStack cosmetic);

    public abstract ItemStack getItemSavedWithNBTsUpdated(ItemStack itemCombined, ItemStack itemStack);

    public abstract ItemStack getCustomHead(ItemStack itemStack, String texture);

    public abstract IRangeManager createRangeManager(Entity entity);

    protected URL getUrlFromBase64(String base64) throws MalformedURLException {
        String decoded = new String(Base64.getDecoder().decode(base64));
        // We simply remove the "beginning" and "ending" part of the JSON, so we're left with only the URL. You could use a proper
        // JSON parser for this, but that's not worth it. The String will always start exactly with this stuff anyway
        return new URL(decoded.substring("{\"textures\":{\"SKIN\":{\"url\":\"".length(), decoded.length() - "\"}}}".length()));
    }
}
