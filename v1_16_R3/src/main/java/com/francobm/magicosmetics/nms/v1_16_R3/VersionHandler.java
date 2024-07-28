package com.francobm.magicosmetics.nms.v1_16_R3;

import com.francobm.magicosmetics.nms.IRangeManager;
import com.francobm.magicosmetics.nms.v1_16_R3.cache.*;
import com.francobm.magicosmetics.nms.v1_16_R3.models.PacketReaderHandler;
import com.francobm.magicosmetics.nms.NPC.ItemSlot;
import com.francobm.magicosmetics.nms.NPC.NPC;
import com.francobm.magicosmetics.nms.version.Version;
import com.francobm.magicosmetics.nms.bag.EntityBag;
import com.francobm.magicosmetics.nms.bag.PlayerBag;
import com.francobm.magicosmetics.nms.balloon.EntityBalloon;
import com.francobm.magicosmetics.nms.balloon.PlayerBalloon;
import com.francobm.magicosmetics.nms.spray.CustomSpray;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.map.MapView;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class VersionHandler extends Version {

    public VersionHandler() {
        this.packetReader = new PacketReaderHandler();
    }

    @Override
    public void setSpectator(Player player) {
        if(player.getGameMode() == GameMode.SPECTATOR) return;
        player.setGameMode(GameMode.SPECTATOR);
        EntityPlayer p = ((CraftPlayer)player).getHandle();
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_GAME_MODE, p);
        try {
            Field packetField = packet.getClass().getDeclaredField("b");
            packetField.setAccessible(true);
            Constructor<?> infoDataConstructor = PacketUtil();
            List<Object> list = Collections.singletonList(infoDataConstructor.newInstance(packet, p.getProfile(), p.ping, EnumGamemode.CREATIVE, p.getPlayerListName()));
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
    public PlayerBag createPlayerBag(Player player, double distance, float height, ItemStack backPackItem, ItemStack backPackForMe) {
        return new PlayerBagHandler(player, createRangeManager(player), distance, height, backPackItem, backPackForMe);
    }

    @Override
    public EntityBag createEntityBag(Entity entity, double distance) {
        return new EntityBagHandler(entity, distance);
    }

    @Override
    public PlayerBalloon createPlayerBalloon(Player player, double space, double distance, boolean bigHead, boolean invisibleLeash) {
        return new PlayerBalloonHandler(player, space, distance, bigHead, invisibleLeash);
    }

    public EntityBalloon createEntityBalloon(Entity entity, double space, double distance, boolean bigHead, boolean invisibleLeash) {
        return new EntityBalloonHandler(entity, space, distance, bigHead, invisibleLeash);
    }

    @Override
    public CustomSpray createCustomSpray(Player player, Location location, BlockFace blockFace, ItemStack itemStack, MapView mapView, int rotation) {
        return new CustomSprayHandler(player, location, blockFace, itemStack, mapView, rotation);
    }

    public Constructor<?> PacketUtil() {
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
    public void equip(LivingEntity livingEntity, ItemSlot itemSlot, ItemStack itemStack) {
        //for(Player p : Bukkit.getOnlinePlayers()){
        List<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>> list = new ArrayList<>();
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
        for(Player p : Bukkit.getOnlinePlayers()){
            PlayerConnection connection = ((CraftPlayer)p).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutEntityEquipment(livingEntity.getEntityId(), list));
            if(!livingEntity.getUniqueId().equals(p.getUniqueId())) continue;
            if(!(livingEntity instanceof Player)) continue;
            Player player = (Player) livingEntity;
            org.bukkit.SoundCategory category = SoundCategory.PLAYERS;
            player.stopSound(org.bukkit.Sound.ITEM_ARMOR_EQUIP_CHAIN,category);
            player.stopSound(org.bukkit.Sound.ITEM_ARMOR_EQUIP_LEATHER,category);
            player.stopSound(org.bukkit.Sound.ITEM_ARMOR_EQUIP_IRON,category);
            player.stopSound(org.bukkit.Sound.ITEM_ARMOR_EQUIP_DIAMOND,category);
            player.stopSound(org.bukkit.Sound.ITEM_ARMOR_EQUIP_GOLD,category);
            player.stopSound(org.bukkit.Sound.ITEM_ARMOR_EQUIP_GENERIC,category);
            player.stopSound(org.bukkit.Sound.ITEM_ARMOR_EQUIP_NETHERITE,category);
        }
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

    @Override
    public void setCamera(Player player, Entity entity) {
        net.minecraft.server.v1_16_R3.Entity e = ((CraftEntity)entity).getHandle();
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        entityPlayer.playerConnection.sendPacket(new PacketPlayOutCamera(e));
    }

    @Override
    public ItemStack setNBTCosmetic(ItemStack itemStack, String key) {
        if(itemStack == null) return null;
        net.minecraft.server.v1_16_R3.ItemStack itemCosmetic = CraftItemStack.asNMSCopy(itemStack);
        itemCosmetic.getOrCreateTag().setString("magic_cosmetic", key);
        return CraftItemStack.asBukkitCopy(itemCosmetic);
    }

    @Override
    public String isNBTCosmetic(ItemStack itemStack) {
        if(itemStack == null) return null;
        net.minecraft.server.v1_16_R3.ItemStack itemCosmetic = CraftItemStack.asNMSCopy(itemStack);
        return itemCosmetic.getOrCreateTag().getString("magic_cosmetic");
    }

    @Override
    public PufferFish spawnFakePuffer(Location location) {
        EntityPufferFish entityPufferFish = new EntityPufferFish(EntityTypes.PUFFERFISH, ((CraftWorld)location.getWorld()).getHandle());
        entityPufferFish.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        return (PufferFish) entityPufferFish.getBukkitEntity();
    }

    @Override
    public ArmorStand spawnArmorStand(Location location) {
        EntityArmorStand entityPufferFish = new EntityArmorStand(EntityTypes.ARMOR_STAND, ((CraftWorld)location.getWorld()).getHandle());
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
            view.playerConnection.sendPacket(packet);
            view.playerConnection.sendPacket(metadata);
        }
    }

    @Override
    public void despawnFakeEntity(Entity entity, Player... viewers) {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entity.getEntityId());
        for(Player viewer : viewers) {
            EntityPlayer view = ((CraftPlayer)viewer).getHandle();
            view.playerConnection.sendPacket(packet);
        }
    }

    @Override
    public void attachFakeEntity(Entity entity, Entity leashed, Player... viewers) {
        EntityPlayer entityPlayer = ((CraftPlayer) entity).getHandle();
        PacketPlayOutAttachEntity packet = new PacketPlayOutAttachEntity(((CraftEntity)leashed).getHandle(), entityPlayer);
        for(Player viewer : viewers) {
            EntityPlayer view = ((CraftPlayer)viewer).getHandle();
            view.playerConnection.sendPacket(packet);
        }
    }

    @Override
    public void updatePositionFakeEntity(Entity leashed, Location location) {
        net.minecraft.server.v1_16_R3.Entity entity = ((CraftEntity)leashed).getHandle();
        entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    @Override
    public void teleportFakeEntity(Entity leashed, Set<Player> viewers) {
        net.minecraft.server.v1_16_R3.Entity entity = ((CraftEntity)leashed).getHandle();
        PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(entity);
        for(Player viewer : viewers) {
            EntityPlayer view = ((CraftPlayer)viewer).getHandle();
            view.playerConnection.sendPacket(packet);
        }
    }

    @Override
    public ItemStack getItemWithNBTsCopy(ItemStack itemToCopy, ItemStack cosmetic) {
        net.minecraft.server.v1_16_R3.ItemStack copy = CraftItemStack.asNMSCopy(itemToCopy);
        if(!copy.hasTag()) return cosmetic;
        net.minecraft.server.v1_16_R3.ItemStack cosmeticItem = CraftItemStack.asNMSCopy(cosmetic);
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
        net.minecraft.server.v1_16_R3.ItemStack copy = CraftItemStack.asNMSCopy(itemCombined);
        if(!copy.hasTag()) return itemStack;
        net.minecraft.server.v1_16_R3.ItemStack realItem = CraftItemStack.asNMSCopy(itemStack);
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

    @Override
    public IRangeManager createRangeManager(Entity entity) {
        WorldServer level = ((CraftWorld)entity.getWorld()).getHandle();
        PlayerChunkMap.EntityTracker trackedEntity = level.getChunkProvider().playerChunkMap.trackedEntities.get(entity.getEntityId());
        return new RangeManager(trackedEntity);
    }
}
