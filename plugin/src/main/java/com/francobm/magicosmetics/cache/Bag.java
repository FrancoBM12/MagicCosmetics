package com.francobm.magicosmetics.cache;

import com.francobm.magicosmetics.api.CosmeticType;
import com.francobm.magicosmetics.nms.bag.EntityBag;
import com.francobm.magicosmetics.nms.bag.PlayerBag;
import com.francobm.magicosmetics.utils.XMaterial;
import com.francobm.magicosmetics.MagicCosmetics;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.EulerAngle;

public class Bag extends Cosmetic {
    private PlayerBag bag1;
    private EntityBag bag2;
    private final int modelDataForMe;
    private final double space;
    private boolean hide = false;
    private boolean spectator = false;
    private final double distance;

    public Bag(String id, String name, ItemStack itemStack, int modelData, int modelDataForMe, boolean colored, double space, CosmeticType cosmeticType, Color color, double distance, String permission, boolean texture, boolean hideMenu) {
        super(id, name, itemStack, modelData, colored, cosmeticType, color, permission, texture, hideMenu);
        this.modelDataForMe = modelDataForMe;
        this.space = space;
        this.distance = distance;
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
        if(bag1 == null){
            if(player.isDead()) return;
            if(player.getGameMode() == GameMode.SPECTATOR) return;

            clear(player);
            bag1 = MagicCosmetics.getInstance().getVersion().createPlayerBag(player, getDistance());
            /*bag1.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 100, false, false));
            MetadataValue metadataValue = new FixedMetadataValue(CustomCosmetics.getInstance(), "balloon");
            bag1.setMetadata("cosmetics", metadataValue);*/
            bag1.spawn(false);
            if(hide){
                hideSelf(false);
            }
            //
        }
        bag1.addPassenger(false);
        bag1.setItemOnHelmet(getItemColor(player), true);
        bag1.lookEntity(player.getLocation().getYaw(), player.getLocation().getPitch(), true);
        bag1.spawn(true);
        if (player.getLocation().getPitch() >= space && space != 0) {
            if(bag1.getPlayers().contains(player.getUniqueId())) {
                bag1.remove(player);
            }
            return;
        }
        if(hide) return;
        bag1.spawn(player);
        //armorStand.teleport(behind);
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

    public int getModelDataForMe() {
        return modelDataForMe;
    }

    public ItemStack getItemColor(boolean forMe){
        if(getItemStack() == null) return null;
        ItemStack itemStack = getItemStack().clone();
        if(itemStack.getType() == XMaterial.LEATHER_HORSE_ARMOR.parseMaterial()){
            LeatherArmorMeta itemMeta = (LeatherArmorMeta) itemStack.getItemMeta();
            itemMeta.setDisplayName(getName());
            itemMeta.setLore(itemMeta.getLore());
            if(itemMeta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)){
                itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            if(itemMeta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            }
            itemMeta.setUnbreakable(itemMeta.isUnbreakable());
            if(forMe){
                itemMeta.setCustomModelData(modelDataForMe);
            }else{
                itemMeta.setCustomModelData(getModelData());
            }
            if(getColor() != null) {
                itemMeta.setColor(getColor());
            }
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }
        return getItemStack();
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
        bag1.spawn(player);
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
}
