package com.francobm.magicosmetics.nms.Packets.v1_16_R3;

import com.francobm.magicosmetics.cache.Sound;
import com.francobm.magicosmetics.cache.nms.v1_16_R3.PlayerBagHandler;
import com.francobm.magicosmetics.models.PacketReader;
import com.francobm.magicosmetics.models.v1_16_R3.PacketReaderHandler;
import com.francobm.magicosmetics.nms.NPC.ItemSlot;
import com.francobm.magicosmetics.nms.NPC.NPC;
import com.francobm.magicosmetics.nms.NPC.v1_16_R3.NPCHandler;
import com.francobm.magicosmetics.nms.Version.Version;
import com.francobm.magicosmetics.nms.bag.PlayerBag;
import com.francobm.magicosmetics.nms.balloon.PlayerBalloon;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VersionHandler extends Version {

    @Override
    public void setSpectator(Player player) {
        if(player.getGameMode() == GameMode.SPECTATOR) return;
        player.setGameMode(GameMode.SPECTATOR);
        EntityPlayer p = ((CraftPlayer)player).getHandle();
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_GAME_MODE, p);
        try {
            Field packetField = packet.getClass().getDeclaredField("b");
            packetField.setAccessible(true);
            //ArrayList<PacketPlayOutPlayerInfo.PlayerInfoData> list = Lists.newArrayList();
            //list.add(packet.new PlayerInfoData(p.getProfile(), p.ping, EnumGamemode.CREATIVE, p.listName));
            Constructor infoDataConstructor = PacketUtil();
            List<Object> list = Collections.singletonList(infoDataConstructor.newInstance(packet, p.getProfile(), p.ping, EnumGamemode.CREATIVE, p.listName));
            packetField.set(packet, list);
            p.playerConnection.sendPacket(packet);
            PacketPlayOutGameStateChange packetPlayOutGameStateChange = new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.d, 3f);
            p.playerConnection.sendPacket(packetPlayOutGameStateChange);
        } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createNPC(Player player) {
        NPC npc = new NPCHandler();
        npc.addNPC(player);
        npc.spawnNPC(player);
    }

    @Override
    public void createNPC(Player player, Location location) {
        NPC npc = new NPCHandler();
        npc.addNPC(player, location);
        npc.spawnNPC(player);
    }

    @Override
    public NPC getNPC(Player player) {
        return NPC.npcs.get(player.getUniqueId());
    }

    @Override
    public void removeNPC(Player player) {
        NPC npc = NPC.npcs.get(player.getUniqueId());
        if(npc == null) return;
        npc.removeNPC(player);
        NPC.npcs.remove(player.getUniqueId());
    }

    @Override
    public NPC getNPC() {
        return new NPCHandler();
    }

    @Override
    public void sendSound(Player player, Sound sound) {
        if(player == null) return;
        if(sound == null) return;

        if(sound.isCustom()) {
            player.playSound(player.getLocation(), sound.getSoundCustom(), sound.getYaw(), sound.getPitch());
            return;
        }
        player.playSound(player.getLocation(), sound.getSoundBukkit(), sound.getYaw(), sound.getPitch());
    }

    @Override
    public PacketReader getPacketReader(Player player) {
        return new PacketReaderHandler(player);
    }

    @Override
    public PlayerBag createPlayerBag(Player player) {
        return new PlayerBagHandler(player);
    }

    @Override
    public PlayerBalloon createPlayerBalloon(Player player, double space) {
        return null;
    }

    public Constructor PacketUtil() {
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            Class<?> clazz = Class.forName("net.minecraft.server."+version+".PacketPlayOutPlayerInfo$PlayerInfoData");
            return clazz.getDeclaredConstructor(PacketPlayOutPlayerInfo.class, GameProfile.class, int.class, EnumGamemode.class, IChatBaseComponent.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void equip(Player player, ItemSlot itemSlot, ItemStack itemStack, boolean all) {
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        if(all){
            for(Player p : Bukkit.getOnlinePlayers()){
                PlayerConnection connection = ((CraftPlayer)p).getHandle().playerConnection;
                ArrayList<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>> list = new ArrayList<>();
                switch (itemSlot){
                    case MAIN_HAND:
                        list.add(new Pair<>(EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(itemStack)));
                        break;
                    case OFF_HAND:
                        list.add(new Pair<>(EnumItemSlot.OFFHAND, CraftItemStack.asNMSCopy(itemStack)));
                        break;
                    case BOOTS:
                        list.add(new Pair<>(EnumItemSlot.FEET, CraftItemStack.asNMSCopy(itemStack)));
                        break;
                    case LEGGINGS:
                        list.add(new Pair<>(EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(itemStack)));
                        break;
                    case CHESTPLATE:
                        list.add(new Pair<>(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(itemStack)));
                        break;
                    case HELMET:
                        list.add(new Pair<>(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(itemStack)));
                        break;
                }
                connection.sendPacket(new PacketPlayOutEntityEquipment(entityPlayer.getId(), list));
            }
            return;
        }
        PlayerConnection connection = entityPlayer.playerConnection;
        ArrayList<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>> list = new ArrayList<>();
        switch (itemSlot){
            case MAIN_HAND:
                list.add(new Pair<>(EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(itemStack)));
                break;
            case OFF_HAND:
                list.add(new Pair<>(EnumItemSlot.OFFHAND, CraftItemStack.asNMSCopy(itemStack)));
                break;
            case BOOTS:
                list.add(new Pair<>(EnumItemSlot.FEET, CraftItemStack.asNMSCopy(itemStack)));
                break;
            case LEGGINGS:
                list.add(new Pair<>(EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(itemStack)));
                break;
            case CHESTPLATE:
                list.add(new Pair<>(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(itemStack)));
                break;
            case HELMET:
                list.add(new Pair<>(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(itemStack)));
                break;
        }
        connection.sendPacket(new PacketPlayOutEntityEquipment(entityPlayer.getId(), list));
    }

    @Override
    public void updateTitle(Player player, String title) {
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        if(player.getOpenInventory().getTopInventory().getType() != InventoryType.CHEST) return;
        PacketPlayOutOpenWindow packet = null;
        switch (player.getOpenInventory().getTopInventory().getSize()/9){
            case 1:
                packet = new PacketPlayOutOpenWindow(entityPlayer.activeContainer.windowId, Containers.GENERIC_9X1, new ChatMessage(title));
                break;
            case 2:
                packet = new PacketPlayOutOpenWindow(entityPlayer.activeContainer.windowId, Containers.GENERIC_9X2, new ChatMessage(title));
                break;
            case 3:
                packet = new PacketPlayOutOpenWindow(entityPlayer.activeContainer.windowId, Containers.GENERIC_9X3, new ChatMessage(title));
                break;
            case 4:
                packet = new PacketPlayOutOpenWindow(entityPlayer.activeContainer.windowId, Containers.GENERIC_9X4, new ChatMessage(title));
                break;
            case 5:
                packet = new PacketPlayOutOpenWindow(entityPlayer.activeContainer.windowId, Containers.GENERIC_9X5, new ChatMessage(title));
                break;
            case 6:
                packet = new PacketPlayOutOpenWindow(entityPlayer.activeContainer.windowId, Containers.GENERIC_9X6, new ChatMessage(title));
                break;
        }
        if(packet == null) return;
        entityPlayer.playerConnection.sendPacket(packet);
        entityPlayer.updateInventory(entityPlayer.activeContainer);
    }
}
