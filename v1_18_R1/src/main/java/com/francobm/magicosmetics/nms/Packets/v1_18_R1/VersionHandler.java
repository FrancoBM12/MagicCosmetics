package com.francobm.magicosmetics.nms.Packets.v1_18_R1;

import com.francobm.magicosmetics.cache.nms.v1_18_R1.*;
import com.francobm.magicosmetics.models.PacketReader;
import com.francobm.magicosmetics.models.v1_18_R1.PacketReaderHandler;
import com.francobm.magicosmetics.nms.NPC.ItemSlot;
import com.francobm.magicosmetics.nms.NPC.NPC;
import com.francobm.magicosmetics.nms.NPC.v1_18_R1.NPCHandler;
import com.francobm.magicosmetics.nms.Version.Version;
import com.francobm.magicosmetics.nms.bag.EntityBag;
import com.francobm.magicosmetics.nms.bag.PlayerBag;
import com.francobm.magicosmetics.nms.balloon.EntityBalloon;
import com.francobm.magicosmetics.nms.balloon.PlayerBalloon;
import com.francobm.magicosmetics.nms.spray.CustomSpray;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.inventory.Containers;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class VersionHandler extends Version {

    @Override
    public void setSpectator(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
        EntityPlayer p = ((CraftPlayer)player).getHandle();
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.b, p);
        try {
            Field packetField = packet.getClass().getDeclaredField("b");
            packetField.setAccessible(true);
            ArrayList<PacketPlayOutPlayerInfo.PlayerInfoData> list = Lists.newArrayList();
            list.add(new PacketPlayOutPlayerInfo.PlayerInfoData(p.fp(), 0, net.minecraft.world.level.EnumGamemode.b, p.listName));
            packetField.set(packet, list);
            p.b.a(packet);
            PacketPlayOutGameStateChange packetPlayOutGameStateChange = new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.d, 3f);
            p.b.a(packetPlayOutGameStateChange);
        } catch (NoSuchFieldException | IllegalAccessException e) {
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
    public PacketReader getPacketReader(Player player) {
        return new PacketReaderHandler(player);
    }

    @Override
    public PlayerBag createPlayerBag(Player player, double distance, int height, ItemStack backPackItem, ItemStack backPackForMe) {
        return new PlayerBagHandler(player, distance, height, backPackItem, backPackForMe);
    }

    @Override
    public EntityBag createEntityBag(Entity entity, double distance) {
        return new EntityBagHandler(entity, distance);
    }

    @Override
    public PlayerBalloon createPlayerBalloon(Player player, double space, double distance, boolean bigHead, boolean invisibleLeash) {
        return new PlayerBalloonHandler(player, space, distance, bigHead, invisibleLeash);
    }

    @Override
    public EntityBalloon createEntityBalloon(Entity entity, double space, double distance, boolean bigHead, boolean invisibleLeash) {
        return new EntityBalloonHandler(entity, space, distance, bigHead, invisibleLeash);
    }

    @Override
    public CustomSpray createCustomSpray(Player player, Location location, BlockFace blockFace, ItemStack itemStack, MapView mapView, int rotation) {
        return new CustomSprayHandler(player, location, blockFace, itemStack, mapView, rotation);
    }

    @Override
    public void equip(LivingEntity livingEntity, ItemSlot itemSlot, ItemStack itemStack) {
        ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> list = new ArrayList<>();
        switch (itemSlot){
            case MAIN_HAND:
                list.add(new Pair<>(EnumItemSlot.a, CraftItemStack.asNMSCopy(itemStack)));
                break;
            case OFF_HAND:
                list.add(new Pair<>(EnumItemSlot.b, CraftItemStack.asNMSCopy(itemStack)));
                break;
            case BOOTS:
                list.add(new Pair<>(EnumItemSlot.c, CraftItemStack.asNMSCopy(itemStack)));
                break;
            case LEGGINGS:
                list.add(new Pair<>(EnumItemSlot.d, CraftItemStack.asNMSCopy(itemStack)));
                break;
            case CHESTPLATE:
                list.add(new Pair<>(EnumItemSlot.e, CraftItemStack.asNMSCopy(itemStack)));
                break;
            case HELMET:
                list.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(itemStack)));
                break;
        }
        //for(Player p : Bukkit.getOnlinePlayers()){
        for(Player p : Bukkit.getOnlinePlayers()){
            PlayerConnection connection = ((CraftPlayer)p).getHandle().b;
            connection.a(new PacketPlayOutEntityEquipment(livingEntity.getEntityId(), list));
        }
    }

    @Override
    public void updateTitle(Player player, String title) {
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        if(player.getOpenInventory().getTopInventory().getType() != InventoryType.CHEST) return;
        PacketPlayOutOpenWindow packet = null;
        switch (player.getOpenInventory().getTopInventory().getSize()/9){
            case 1:
                packet = new PacketPlayOutOpenWindow(entityPlayer.bW.j, Containers.a, new ChatMessage(title));
                break;
            case 2:
                packet = new PacketPlayOutOpenWindow(entityPlayer.bW.j, Containers.b, new ChatMessage(title));
                break;
            case 3:
                packet = new PacketPlayOutOpenWindow(entityPlayer.bW.j, Containers.c, new ChatMessage(title));
                break;
            case 4:
                packet = new PacketPlayOutOpenWindow(entityPlayer.bW.j, Containers.d, new ChatMessage(title));
                break;
            case 5:
                packet = new PacketPlayOutOpenWindow(entityPlayer.bW.j, Containers.e, new ChatMessage(title));
                break;
            case 6:
                packet = new PacketPlayOutOpenWindow(entityPlayer.bW.j, Containers.f, new ChatMessage(title));
                break;
        }
        if(packet == null) return;
        entityPlayer.b.a(packet);
        entityPlayer.bW.b();
    }

    @Override
    public void setCamera(Player player, Entity entity) {
        net.minecraft.world.entity.Entity e = ((CraftEntity)entity).getHandle();
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        entityPlayer.b.a(new PacketPlayOutCamera(e));
    }

    @Override
    public ItemStack setNBTCosmetic(ItemStack itemStack, String key) {
        if(itemStack == null) return null;
        net.minecraft.world.item.ItemStack itemCosmetic = CraftItemStack.asNMSCopy(itemStack);
        itemCosmetic.t().a("magic_cosmetic", key);
        return CraftItemStack.asBukkitCopy(itemCosmetic);
    }

    @Override
    public String isNBTCosmetic(ItemStack itemStack) {
        if(itemStack == null) return null;
        net.minecraft.world.item.ItemStack itemCosmetic = CraftItemStack.asNMSCopy(itemStack);
        return itemCosmetic.t().l("magic_cosmetic");
    }
}
