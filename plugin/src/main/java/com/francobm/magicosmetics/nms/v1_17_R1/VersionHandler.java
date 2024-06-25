package com.francobm.magicosmetics.nms.v1_17_R1;

import com.francobm.magicosmetics.nms.v1_17_R1.cache.*;
import com.francobm.magicosmetics.nms.NPC.ItemSlot;
import com.francobm.magicosmetics.nms.NPC.NPC;
import com.francobm.magicosmetics.nms.Version.Version;
import com.francobm.magicosmetics.nms.bag.EntityBag;
import com.francobm.magicosmetics.nms.bag.PlayerBag;
import com.francobm.magicosmetics.nms.balloon.EntityBalloon;
import com.francobm.magicosmetics.nms.balloon.PlayerBalloon;
import com.francobm.magicosmetics.nms.spray.CustomSpray;
import com.francobm.magicosmetics.nms.v1_17_R1.models.PacketReaderHandler;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.animal.EntityPufferFish;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.inventory.Containers;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.map.MapView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class VersionHandler extends Version {

    public VersionHandler() {
        this.packetReader = new PacketReaderHandler();
    }
    @Override
    public void setSpectator(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
        EntityPlayer p = ((CraftPlayer)player).getHandle();
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.b, p);
        try {
            Field packetField = packet.getClass().getDeclaredField("b");
            packetField.setAccessible(true);
            ArrayList<PacketPlayOutPlayerInfo.PlayerInfoData> list = Lists.newArrayList();
            list.add(new PacketPlayOutPlayerInfo.PlayerInfoData(p.getProfile(), 0, net.minecraft.world.level.EnumGamemode.b, p.getPlayerListName()));
            packetField.set(packet, list);
            p.b.sendPacket(packet);
            PacketPlayOutGameStateChange packetPlayOutGameStateChange = new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.d, 3f);
            p.b.sendPacket(packetPlayOutGameStateChange);
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
    public PlayerBag createPlayerBag(Player player, double distance, float height, ItemStack backPackItem, ItemStack backPackForMe) {
        return new PlayerBagHandler(player, distance, height, backPackItem, backPackForMe);
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
    public EntityBag createEntityBag(Entity entity, double distance) {
        return new EntityBagHandler(entity, distance);
    }

    @Override
    public CustomSpray createCustomSpray(Player player, Location location, BlockFace blockFace, ItemStack itemStack, MapView mapView, int rotation) {
        return new CustomSprayHandler(player, location, blockFace, itemStack, mapView, rotation);
    }

    @Override
    public void equip(LivingEntity livingEntity, ItemSlot itemSlot, ItemStack itemStack) {
        if(itemStack == null) return;
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
            connection.sendPacket(new PacketPlayOutEntityEquipment(livingEntity.getEntityId(), list));
        }
    }

    @Override
    public void updateTitle(Player player, String title) {
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        if(player.getOpenInventory().getTopInventory().getType() != InventoryType.CHEST) return;
        PacketPlayOutOpenWindow packet = null;
        switch (player.getOpenInventory().getTopInventory().getSize()/9){
            case 1:
                packet = new PacketPlayOutOpenWindow(entityPlayer.bV.j, Containers.a, new ChatMessage(title));
                break;
            case 2:
                packet = new PacketPlayOutOpenWindow(entityPlayer.bV.j, Containers.b, new ChatMessage(title));
                break;
            case 3:
                packet = new PacketPlayOutOpenWindow(entityPlayer.bV.j, Containers.c, new ChatMessage(title));
                break;
            case 4:
                packet = new PacketPlayOutOpenWindow(entityPlayer.bV.j, Containers.d, new ChatMessage(title));
                break;
            case 5:
                packet = new PacketPlayOutOpenWindow(entityPlayer.bV.j, Containers.e, new ChatMessage(title));
                break;
            case 6:
                packet = new PacketPlayOutOpenWindow(entityPlayer.bV.j, Containers.f, new ChatMessage(title));
                break;
        }
        if(packet == null) return;
        entityPlayer.b.sendPacket(packet);
        entityPlayer.bV.updateInventory();
    }

    @Override
    public void setCamera(Player player, Entity entity) {
        net.minecraft.world.entity.Entity e = ((CraftEntity)entity).getHandle();
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        entityPlayer.b.sendPacket(new PacketPlayOutCamera(e));
    }

    @Override
    public ItemStack setNBTCosmetic(ItemStack itemStack, String key) {
        if(itemStack == null) return null;
        net.minecraft.world.item.ItemStack itemCosmetic = CraftItemStack.asNMSCopy(itemStack);
        itemCosmetic.getOrCreateTag().setString("magic_cosmetic", key);
        return CraftItemStack.asBukkitCopy(itemCosmetic);
    }

    @Override
    public String isNBTCosmetic(ItemStack itemStack) {
        if(itemStack == null) return null;
        net.minecraft.world.item.ItemStack itemCosmetic = CraftItemStack.asNMSCopy(itemStack);
        return itemCosmetic.getOrCreateTag().getString("magic_cosmetic");
    }

    @Override
    public PufferFish spawnFakePuffer(Location location) {
        EntityPufferFish entityPufferFish = new EntityPufferFish(EntityTypes.at, ((CraftWorld)location.getWorld()).getHandle());
        entityPufferFish.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        return (PufferFish) entityPufferFish.getBukkitEntity();
    }

    @Override
    public ArmorStand spawnArmorStand(Location location) {
        EntityArmorStand entityPufferFish = new EntityArmorStand(EntityTypes.c, ((CraftWorld)location.getWorld()).getHandle());
        entityPufferFish.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        return (ArmorStand) entityPufferFish.getBukkitEntity();
    }

    @Override
    public void showEntity(LivingEntity entity, Player... viewers) {
        EntityLiving entityClient = ((CraftLivingEntity) entity).getHandle();
        entityClient.setInvisible(true);
        DataWatcher dataWatcher = entityClient.getDataWatcher();
        PacketPlayOutSpawnEntity packet = new PacketPlayOutSpawnEntity(entityClient);
        PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(entity.getEntityId(), dataWatcher, true);
        for(Player viewer : viewers) {
            EntityPlayer view = ((CraftPlayer)viewer).getHandle();
            view.b.sendPacket(packet);
            view.b.sendPacket(metadata);
        }
    }

    @Override
    public void despawnFakeEntity(Entity entity, Player... viewers) {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entity.getEntityId());
        for(Player viewer : viewers) {
            EntityPlayer view = ((CraftPlayer)viewer).getHandle();
            view.b.sendPacket(packet);
        }
    }

    @Override
    public void attachFakeEntity(Entity entity, Entity leashed, Player... viewers) {
        EntityPlayer entityPlayer = ((CraftPlayer) entity).getHandle();
        PacketPlayOutAttachEntity packet = new PacketPlayOutAttachEntity(((CraftEntity)leashed).getHandle(), entityPlayer);
        for(Player viewer : viewers) {
            EntityPlayer view = ((CraftPlayer)viewer).getHandle();
            view.b.sendPacket(packet);
        }
    }

    @Override
    public void updatePositionFakeEntity(Entity leashed, Location location) {
        net.minecraft.world.entity.Entity entity = ((CraftEntity)leashed).getHandle();
        entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    @Override
    public void teleportFakeEntity(Entity leashed, Set<Player> viewers) {
        net.minecraft.world.entity.Entity entity = ((CraftEntity)leashed).getHandle();
        PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(entity);
        for(Player viewer : viewers) {
            EntityPlayer view = ((CraftPlayer)viewer).getHandle();
            view.b.sendPacket(packet);
        }
    }

    @Override
    public ItemStack getItemWithNBTsCopy(ItemStack itemToCopy, ItemStack cosmetic) {
        net.minecraft.world.item.ItemStack copy = CraftItemStack.asNMSCopy(itemToCopy);
        if(!copy.hasTag()) return cosmetic;
        net.minecraft.world.item.ItemStack cosmeticItem = CraftItemStack.asNMSCopy(cosmetic);
        for(String key : copy.getTag().getKeys()){
            if(key.equals("display") || key.equals("CustomModelData")) continue;
            if(key.equals("PublicBukkitValues")) {
                NBTTagCompound compound = copy.getTag().getCompound(key);
                NBTTagCompound realCompound = cosmeticItem.getTag().getCompound(key);
                Set<String> keys = compound.getKeys();
                for (String compoundKey : keys){
                    realCompound.set(compoundKey, compound.get(compoundKey));
                }
                cosmeticItem.getOrCreateTag().set(key, realCompound);
                continue;
            }
            cosmeticItem.getOrCreateTag().set(key, copy.getTag().get(key));
        }
        return CraftItemStack.asBukkitCopy(cosmeticItem);
    }

    public ItemStack getItemSavedWithNBTsUpdated(ItemStack itemCombined, ItemStack itemStack) {
        net.minecraft.world.item.ItemStack copy = CraftItemStack.asNMSCopy(itemCombined);
        if(!copy.hasTag()) return itemStack;
        net.minecraft.world.item.ItemStack realItem = CraftItemStack.asNMSCopy(itemStack);
        if(!realItem.hasTag()) return itemStack;
        for(String key : copy.getTag().getKeys()){
            if(key.equals("display") || key.equals("CustomModelData")) continue;
            if(key.equals("PublicBukkitValues")) {
                NBTTagCompound compound = copy.getTag().getCompound(key);
                NBTTagCompound realCompound = realItem.getTag().getCompound(key);
                Set<String> keys = compound.getKeys();
                for (String compoundKey : keys){
                    if(!realCompound.hasKey(compoundKey)) continue;
                    realCompound.set(compoundKey, compound.get(compoundKey));
                }
                realItem.getTag().set(key, realCompound);
                continue;
            }
            if(!realItem.getTag().hasKey(key)) continue;
            realItem.getTag().set(key, copy.getTag().get(key));
        }
        return CraftItemStack.asBukkitCopy(realItem);
    }

    public ItemStack getCustomHead(ItemStack itemStack, String texture){
        if(itemStack == null) return null;
        if(texture.isEmpty()){
            return itemStack;
        }
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        if(skullMeta == null) return itemStack;
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "");
        gameProfile.getProperties().put("textures", new Property("textures", texture));
        try{
            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, gameProfile);
        }catch (Exception e){
            e.printStackTrace();
        }
        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }
}
