package com.francobm.customcosmetics.nms.NPC;

import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class NPC {

    public static Map<UUID, NPC> npcs = new HashMap<>();
    protected Entity entity;
    protected Entity armorStand;

    public abstract void spawnNPC(Player player);

    public abstract void removeNPC(Player player);

    public abstract void removeBalloon(Player player);

    public abstract void addNPC(Player player);

    public abstract void addNPC(Player player, Location location);

    public abstract void lookNPC(Player player, float yaw, float pitch);

    public abstract void lookNPC(Player player, float yaw);

    public abstract void equipNPC(Player player, ItemSlot itemSlot, ItemStack itemStack);

    public abstract Location getLocation();

    public abstract void moveNPC(Player player, double x, double y, double z);

    public abstract void animation(Player player);

    public abstract void changeSkinNPC(Player player, String username);

    public abstract void setInvisible(Player player, Entity entity, boolean invisible);

    public abstract void setInvisible(Entity entity, boolean invisible);

    public abstract NPC getNPC(Player player);

    public abstract void addPassenger(Player player);

    public abstract void addPassenger(Player player, Entity entity1, Entity entity2);

    public abstract void balloonNPC(Player player, Location location, ItemStack itemStack);

    public abstract void armorStandSetItem(Player player, ItemStack itemStack);

    public abstract void ArmorStandSetHelmet(Player player, Entity entity, ItemStack itemStack);

    public abstract void removeEntity(Player player, Entity entity);

    public abstract void balloonSetItem(Player player, ItemStack itemStack);

    protected void addNPC(NPC npc, Player player){
        npcs.put(player.getUniqueId(), npc);
    }

    public abstract void lookEntity(Player player, Entity entity, float yaw, float pitch);

    public abstract void teleportEntityPlayer(Player player, Entity entity, Location location);

    public abstract void lookEntityPlayer(Player player, Entity entity, float quantity);

    public Entity getEntity() {
        return entity;
    }
}
