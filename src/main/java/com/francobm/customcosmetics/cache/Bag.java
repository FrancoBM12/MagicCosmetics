package com.francobm.customcosmetics.cache;

import com.francobm.customcosmetics.CustomCosmetics;
import com.francobm.customcosmetics.cache.nms.PlayerBag;
import com.francobm.customcosmetics.utils.XMaterial;
import org.bukkit.Color;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.EulerAngle;

public class Bag extends Cosmetic {
    private PlayerBag bag1;
    private final int modelDataForMe;
    private final double space;

    public Bag(String id, String name, ItemStack itemStack, int modelData, int modelDataForMe, boolean colored, double space, CosmeticType cosmeticType, Color color) {
        super(id, name, itemStack, modelData, colored, cosmeticType, color);
        this.modelDataForMe = modelDataForMe;
        this.space = space;
    }

    public double getSpace() {
        return space;
    }

    @Override
    public void active(Player player) {
        if(bag1 == null){
            if(player.isDead()) return;

            clear(player);

            bag1 = PlayerBag.createBag(player);
            if(bag1 == null) {
                CustomCosmetics.getInstance().getLogger().severe("Plugin not support this version!!");
                return;
            }
            bag1.spawnBag(true, true);
            /*bag1.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 100, false, false));
            MetadataValue metadataValue = new FixedMetadataValue(CustomCosmetics.getInstance(), "balloon");
            bag1.setMetadata("cosmetics", metadataValue);*/

            //
        }
        bag1.setItemOnHelmet(getItemColor(), true);
        bag1.lookEntity(player.getLocation().getYaw(), player.getLocation().getPitch(), true);
        //armorStand.teleport(behind);
    }

    @Override
    public void clear(Player player) {
        if(bag1 != null){
            bag1.remove(true);
        }
        bag1 = null;
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

}
