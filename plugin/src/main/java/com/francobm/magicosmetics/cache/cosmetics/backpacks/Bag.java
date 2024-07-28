package com.francobm.magicosmetics.cache.cosmetics.backpacks;

import com.francobm.magicosmetics.api.Cosmetic;
import com.francobm.magicosmetics.api.CosmeticType;
import com.francobm.magicosmetics.nms.bag.EntityBag;
import com.francobm.magicosmetics.nms.bag.PlayerBag;
import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.utils.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.util.EulerAngle;

public class Bag extends Cosmetic {
    private PlayerBag bag1;
    private EntityBag bag2;
    private ItemStack bagForMe;
    private BackPackEngine backPackEngine;
    private double space;
    private boolean hide = false;
    private boolean spectator = false;
    private double distance;
    private float height;

    public Bag(String id, String name, ItemStack itemStack, int modelData, ItemStack bagForMe, boolean colored, double space, CosmeticType cosmeticType, Color color, double distance, String permission, boolean texture, boolean hideMenu, float height, boolean useEmote, BackPackEngine backPackEngine, NamespacedKey namespacedKey) {
        super(id, name, itemStack, modelData, colored, cosmeticType, color, permission, texture, hideMenu, useEmote, namespacedKey);
        this.bagForMe = bagForMe;
        this.space = space;
        this.distance = distance;
        this.height = height;
        this.backPackEngine = backPackEngine;
    }

    @Override
    protected void updateCosmetic(Cosmetic cosmetic) {
        super.updateCosmetic(cosmetic);
        Bag bag = (Bag) cosmetic;
        this.bagForMe = bag.bagForMe;
        this.space = bag.space;
        this.distance = bag.distance;
        this.height = bag.height;
        this.backPackEngine = bag.backPackEngine;
    }

    public double getSpace() {
        return space;
    }

    public void active(Entity entity){
        if(entity == null) return;
        if(backPackEngine != null){
            if(backPackEngine.getBackPackUniqueId() == null) {
                if(entity.isDead()) return;
                remove();
                backPackEngine.spawnModel(entity);
                if (isColored()) {
                    backPackEngine.tintModel(entity, getColor());
                }
            }
            return;
        }
        if(bag2 == null){
            if(entity.isDead()) {
                remove();
                return;
            }
            remove();
            bag2 = MagicCosmetics.getInstance().getVersion().createEntityBag(entity, distance);
            bag2.spawnBag();
            //
        }
        bag2.addPassenger();
        bag2.setItemOnHelmet(getItemColor());
        bag2.lookEntity();
    }

    @Override
    public void lendToEntity() {
        if(bag1 == null){
            if(lendEntity.isDead()) return;
            remove();
            bag1 = MagicCosmetics.getInstance().getVersion().createPlayerBag(player, getDistance(), height, getItemColor(player), getBagForMe() != null ? getItemColorForMe(player) : null);
            bag1.setLendEntityId(lendEntity.getEntityId());
            if(hide){
                hideSelf(false);
            }
        }
        bag1.addPassenger(true);
        bag1.lookEntity(lendEntity.getLocation().getYaw(), lendEntity.getLocation().getPitch(), true);
        bag1.spawn(true);
        if(hide) return;
        bag1.spawnSelf(player);
        bag1.lookEntity(lendEntity.getLocation().getYaw(), lendEntity.getLocation().getPitch(), false);
    }

    @Override
    public void hide(Player player) {
        if(backPackEngine != null){
            backPackEngine.hideModel(player);
            return;
        }
        if(bag1 != null){
            bag1.addHideViewer(player);
        }
    }

    @Override
    public void show(Player player) {
        if(backPackEngine != null){
            backPackEngine.showModel(player);
            return;
        }
        if(bag1 != null){
            bag1.removeHideViewer(player);
        }
    }

    @Override
    public void update() {
        if(lendEntity != null){
            lendToEntity();
            return;
        }
        if(isHideCosmetic()) {
            remove();
            return;
        }
        if(backPackEngine != null){
            if(backPackEngine.getBackPackUniqueId() == null) {
                if(player.isDead()) return;
                if(player.getGameMode() == GameMode.SPECTATOR) return;
                remove();
                backPackEngine.spawnModel(player);

                if (isColored()) {
                    backPackEngine.tintModel(player, getColor());
                }
            }
            return;
        }
        if(bag1 == null){
            if(player.isDead()) return;
            if(player.getGameMode() == GameMode.SPECTATOR) return;

            remove();
            bag1 = MagicCosmetics.getInstance().getVersion().createPlayerBag(player, getDistance(), height, getItemColor(player), getBagForMe() != null ? getItemColorForMe(player) : null);
            if(hide){
                hideSelf(false);
            }
            bag1.spawn(false);
            //Bukkit.getLogger().info("Spawning bag");
            /*if(hide) return;
            bag1.spawnSelf(player);*/
            //
        }
        //bag1.addPassenger(true);
        //bag1.lookEntity(player.getLocation().getYaw(), player.getLocation().getPitch(), true);
        bag1.lookEntity(player.getLocation().getYaw(), player.getLocation().getPitch(), true);
    }

    @Override
    public void remove() {
        if(backPackEngine != null) {
            backPackEngine.remove();
        }
        if(bag1 != null){
            bag1.remove();
        }
        if(bag2 != null){
            bag2.remove();
        }
        bag1 = null;
        bag2 = null;
    }

    @Override
    public void clearClose() {
        if(backPackEngine != null) {
            backPackEngine.remove();
        }
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

    public float getHeight() {
        return height;
    }

    public BackPackEngine getBackPackEngine() {
        return backPackEngine;
    }

    @Override
    public void spawn(Player player) {
        if(bag1 != null) {
            bag1.spawn(player);
        }
        if(bag2 != null) {
            bag2.spawnBag(player);
        }
    }

    @Override
    public void despawn(Player player) {
        if(bag1 != null) {
            bag1.remove(player);
        }
        if(bag2 != null) {
            bag2.remove(player);
        }
    }
}
