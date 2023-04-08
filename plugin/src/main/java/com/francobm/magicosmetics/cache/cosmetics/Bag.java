package com.francobm.magicosmetics.cache.cosmetics;

import com.francobm.magicosmetics.api.Cosmetic;
import com.francobm.magicosmetics.api.CosmeticType;
import com.francobm.magicosmetics.nms.bag.EntityBag;
import com.francobm.magicosmetics.nms.bag.PlayerBag;
import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.utils.XMaterial;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.util.EulerAngle;

public class Bag extends Cosmetic {
    private PlayerBag bag1;
    private EntityBag bag2;
    private final ItemStack bagForMe;
    private final double space;
    private boolean hide = false;
    private boolean spectator = false;
    private final double distance;
    private final int height;

    public Bag(String id, String name, ItemStack itemStack, int modelData, ItemStack bagForMe, boolean colored, double space, CosmeticType cosmeticType, Color color, double distance, String permission, boolean texture, boolean hideMenu, int height, boolean useEmote) {
        super(id, name, itemStack, modelData, colored, cosmeticType, color, permission, texture, hideMenu, useEmote);
        this.bagForMe = bagForMe;
        this.space = space;
        this.distance = distance;
        this.height = height;
    }

    public double getSpace() {
        return space;
    }

    public void active(Entity entity){
        if(entity == null) return;
        if(bag2 == null){
            if(entity.isDead() || !entity.isValid()) {
                clear(null);
                return;
            }
            clear(null);
            bag2 = MagicCosmetics.getInstance().getVersion().createEntityBag(entity, distance);
            /*bag1.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 100, false, false));
            MetadataValue metadataValue = new FixedMetadataValue(CustomCosmetics.getInstance(), "balloon");
            bag1.setMetadata("cosmetics", metadataValue);*/
            bag2.spawnBag();
            //
        }
        bag2.addPassenger();
        bag2.setItemOnHelmet(getItemColor());
        bag2.lookEntity();
    }

    @Override
    public void active(Player player) {
        if(isHideCosmetic()) {
            clear(player);
            return;
        }
        if(bag1 == null){
            if(player.isDead()) return;
            if(player.getGameMode() == GameMode.SPECTATOR) return;

            clear(player);
            bag1 = MagicCosmetics.getInstance().getVersion().createPlayerBag(player, getDistance(), height, getItemColor(player), getBagForMe() != null ? getItemColorForMe(player) : null);
            /*bag1.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 100, false, false));
            MetadataValue metadataValue = new FixedMetadataValue(CustomCosmetics.getInstance(), "balloon");
            bag1.setMetadata("cosmetics", metadataValue);*/
            if(hide){
                hideSelf(false);
            }
            //
        }
        bag1.addPassenger(true);
        bag1.lookEntity(player.getLocation().getYaw(), player.getLocation().getPitch(), true);
        bag1.spawn(true);
        if (player.getLocation().getPitch() >= space && space != 0) {
            if(bag1.getPlayers().contains(player.getUniqueId())) {
                bag1.remove(player);
            }
            return;
        }
        if(hide) return;
        bag1.spawnSelf(player);
        bag1.lookEntity(player.getLocation().getYaw(), player.getLocation().getPitch(), false);
    }

    @Override
    public void clear(Player player) {
        if(bag1 != null){
            bag1.remove();
        }
        if(bag2 != null){
            bag2.remove();
        }
        bag1 = null;
        bag2 = null;
    }

    public void setHeadPos(ArmorStand as, double yaw, double pitch){
        double yint = Math.cos(yaw/Math.PI);
        double zint = Math.sin(yaw/Math.PI);
        //This will convert the yaw to a yint and zint between -1 and 1. Here are some examples of how the yaw changes:
        /*
        yaw = 0 : yint = 1. zint = 0;  East
        yaw = 90 : yint = 0. zint = 1; South
        yaw = 180: yint = -1. zint = 0; North
        yaw = 270 : yint = 0. zint = -1; West
        */
        double xint = Math.sin(pitch/Math.PI);
        //This converts the pitch to a yint
        EulerAngle ea = as.getHeadPose();
        ea.setX(xint);
        ea.setY(yint);
        ea.setZ(zint);
        as.setHeadPose(ea);
        //This gets the EulerAngle of the armorStand, sets the values, and then updates the armorstand.
    }

    public ItemStack getBagForMe() {
        return bagForMe;
    }

    public ItemStack getItemColorForMe() {
        if(bagForMe == null) return null;
        ItemStack itemStack = this.bagForMe.clone();
        if(itemStack.getItemMeta() instanceof LeatherArmorMeta){
            LeatherArmorMeta itemMeta = (LeatherArmorMeta) itemStack.getItemMeta();
            if(getColor() != null) {
                itemMeta.setColor(getColor());
            }
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }
        if(itemStack.getItemMeta() instanceof PotionMeta){
            PotionMeta itemMeta = (PotionMeta) itemStack.getItemMeta();
            if(getColor() != null) {
                itemMeta.setColor(getColor());
            }
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }
        if(itemStack.getItemMeta() instanceof MapMeta){
            MapMeta itemMeta = (MapMeta) itemStack.getItemMeta();
            if(getColor() != null) {
                itemMeta.setColor(getColor());
            }
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }
        return itemStack;
    }

    public ItemStack getItemColorForMe(Player player){
        if(isTexture()) return getItemColorForMe();
        ItemStack itemStack = getItemColorForMe();
        if(itemStack.getType() != XMaterial.PLAYER_HEAD.parseMaterial()) return itemStack;
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setOwningPlayer(player);
        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }

    public void hideSelf(boolean change){
        if(bag1 == null) return;
        Player player = bag1.getPlayer();
        if(change) {
            hide();
        }
        if(hide){
            if(!bag1.getPlayers().contains(player.getUniqueId())) return;
            bag1.remove(player);
            return;
        }
        if(bag1.getPlayers().contains(player.getUniqueId())) return;
        bag1.spawnSelf(player);
    }

    public void hide(){
        hide = !hide;
    }

    public void setSpectator(boolean spectator) {
        this.spectator = spectator;
    }

    public boolean isSpectator() {
        return spectator;
    }

    public PlayerBag getBag() {
        return bag1;
    }

    public double getDistance() {
        return distance;
    }

    public boolean isHide() {
        return hide;
    }

    public int getHeight() {
        return height;
    }
}
