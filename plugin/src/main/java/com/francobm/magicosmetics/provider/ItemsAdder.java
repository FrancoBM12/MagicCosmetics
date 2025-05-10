package com.francobm.magicosmetics.provider;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.cache.PlayerData;
import com.francobm.magicosmetics.cache.cosmetics.backpacks.Bag;
import com.francobm.magicosmetics.cache.cosmetics.balloons.Balloon;
import com.francobm.magicosmetics.nms.v1_18_R2.VersionHandler;
import dev.lone.itemsadder.api.CustomEntity;
import dev.lone.itemsadder.api.CustomPlayer;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import org.bukkit.ChatColor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemsAdder implements ResourcePlugin {

    public boolean existModel(String namespaceId) {
        return CustomEntity.isInRegistry(namespaceId);
    }

    public CustomStack getCustomStack(String id){
        return CustomStack.getInstance(id);
    }

    public CustomStack getCustomStack(ItemStack itemStack){
        return CustomStack.byItemStack(itemStack);
    }

    public ItemStack getCustomItemStack(String id){
        CustomStack customStack = CustomStack.getInstance(id);
        if(customStack == null) return null;
        return customStack.getItemStack();
    }

    @Override
    public ItemStack getItemStackById(String id) {
        return getCustomItemStack(id);
    }

    public String replaceFontImageWithoutColor(String id){
        return ChatColor.stripColor(FontImageWrapper.replaceFontImages(id));
    }

    public String replaceFontImages(String id){
        return FontImageWrapper.replaceFontImages(id);
    }

    public void stopEmote(Player player){
        CustomPlayer.stopEmote(player);
    }

    public boolean hasEmote(Player player){
        try {
            return CustomPlayer.byAlreadySpawned(player) != null;
        } catch (Exception ignored) { // api disabled
        }
        return false;
    }

    public void balloonEmote(Player player) {
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        CustomPlayer customPlayer = CustomPlayer.byAlreadySpawned(player);
        PlayerData playerData = PlayerData.getPlayer(player);
        if(playerData.getBalloon() == null) {
            plugin.getLogger().info("No balloon found for player " + player.getName());
            return;
        }
        if(customPlayer == null) {
            plugin.getLogger().warning("Player " + player.getName() + " is not spawned");
            return;
        }
        ArmorStand armorStand = ((VersionHandler)plugin.getVersion()).createArmor(player);
        ((Balloon)playerData.getBalloon()).setLeashedHolder(armorStand);
        CustomEntity.Bone bone = customPlayer.getBone("pbody_2");
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, (task) -> {
            if(bone == null) {
                task.cancel();
                ((Balloon)playerData.getBalloon()).setLeashedHolder(player);
                plugin.getLogger().warning("Could not find bone left_arm_slot for player " + player.getName());
                return;
            }
            if(!hasEmote(player)) {
                task.cancel();
                ((Balloon)playerData.getBalloon()).setLeashedHolder(player);
                plugin.getLogger().warning("ArmorStand for player " + player.getName() + " is invalid");
                return;
            }

            armorStand.teleport(bone.getLocation());
        }, 0, 1);
    }

    public void backPackEmote(Player player) {
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        CustomPlayer customPlayer = CustomPlayer.byAlreadySpawned(player);
        PlayerData playerData = PlayerData.getPlayer(player);
        if(customPlayer == null) return;
        if(playerData.getBag() == null) {
            plugin.getLogger().info("No backpack found for player " + player.getName());
            return;
        }
        Bag bag = (Bag) playerData.getBag();
        customPlayer.addPassenger((LivingEntity) bag.getBag().getEntity());
    }

    @Override
    public String getProviderName() {
        return "ItemsAdder";
    }

    @Override
    public String getPathName() {
        return "item-adder";
    }
}
