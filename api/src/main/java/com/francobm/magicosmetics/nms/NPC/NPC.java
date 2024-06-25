package com.francobm.magicosmetics.nms.NPC;

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
    protected Entity punch;
    protected Entity armorStand;
    protected Location balloonPosition;
    protected boolean floatLoop = true;
    protected double y = 0;
    protected double height = 0;
    protected boolean heightLoop = true;
    protected float rotate = -0.4f;
    protected double rot = 0;
    protected boolean rotateLoop = true;
    protected boolean bigHead = false;

    public abstract void spawnNPC(Player player);

    public abstract void removeNPC(Player player);

    public abstract void removeBalloon(Player player);

    public abstract void addNPC(Player player);

    public abstract void addNPC(Player player, Location location);

    public abstract void lookNPC(Player player, float yaw);

    public abstract void equipNPC(Player player, ItemSlot itemSlot, ItemStack itemStack);

    public abstract void animation(Player player);

    public abstract NPC getNPC(Player player);

    public abstract void addPassenger(Player player);

    public abstract void balloonNPC(Player player, Location location, ItemStack itemStack, boolean bigHead);

    public abstract void armorStandSetItem(Player player, ItemStack itemStack);

    public abstract void balloonSetItem(Player player, ItemStack itemStack);

    protected void addNPC(NPC npc, Player player){
        npcs.put(player.getUniqueId(), npc);
    }

    public abstract void spawnPunch(Player player, Location location);

    public Entity getEntity() {
        return entity;
    }

    public Entity getPunchEntity(){
        return punch;
    }

    public boolean isBigHead() {
        return bigHead;
    }
}
