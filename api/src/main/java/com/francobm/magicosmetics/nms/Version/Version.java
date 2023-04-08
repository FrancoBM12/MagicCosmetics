package com.francobm.magicosmetics.nms.Version;

import com.francobm.magicosmetics.models.PacketReader;
import com.francobm.magicosmetics.nms.NPC.ItemSlot;
import com.francobm.magicosmetics.nms.NPC.NPC;
import com.francobm.magicosmetics.nms.bag.EntityBag;
import com.francobm.magicosmetics.nms.bag.PlayerBag;
import com.francobm.magicosmetics.nms.balloon.EntityBalloon;
import com.francobm.magicosmetics.nms.balloon.PlayerBalloon;
import com.francobm.magicosmetics.nms.spray.CustomSpray;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

public abstract class Version {
    private static Version version;

    public static Version getVersion(){
        return version;
    }

    public static void setVersion(Version version2){
        version = version2;
    }

    public abstract void setSpectator(Player player);

    public abstract void createNPC(Player player);

    public abstract void createNPC(Player player, Location location);

    public abstract NPC getNPC(Player player);

    public abstract void removeNPC(Player player);

    public abstract NPC getNPC();

    public abstract void equip(LivingEntity livingEntity, ItemSlot itemSlot, ItemStack itemStack);

    public abstract void setCamera(Player player, Entity entity);

    public abstract PacketReader getPacketReader(Player player);

    public abstract PlayerBag createPlayerBag(Player player, double distance, int height, ItemStack backPackItem, ItemStack backPackItemForMe);

    public abstract EntityBag createEntityBag(Entity entity, double distance);

    public abstract PlayerBalloon createPlayerBalloon(Player player, double space, double distance, boolean bigHead, boolean invisibleLeash);

    public abstract EntityBalloon createEntityBalloon(Entity entity, double space, double distance, boolean bigHead, boolean invisibleLeash);

    public abstract CustomSpray createCustomSpray(Player player, Location location, BlockFace blockFace, ItemStack itemStack, MapView mapView, int rotation);

    public abstract void updateTitle(Player player, String title);

    public abstract ItemStack setNBTCosmetic(ItemStack itemStack, String key);

    public abstract String isNBTCosmetic(ItemStack itemStack);
}
