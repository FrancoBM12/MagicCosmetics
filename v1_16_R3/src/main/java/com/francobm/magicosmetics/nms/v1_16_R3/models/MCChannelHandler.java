package com.francobm.magicosmetics.nms.v1_16_R3.models;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.api.CosmeticType;
import com.francobm.magicosmetics.cache.PlayerData;
import com.francobm.magicosmetics.cache.cosmetics.Hat;
import com.francobm.magicosmetics.cache.cosmetics.WStick;
import com.francobm.magicosmetics.events.CosmeticInventoryUpdateEvent;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

public class MCChannelHandler extends ChannelDuplexHandler {
    private final EntityPlayer player;

    public MCChannelHandler(EntityPlayer player){
        this.player = player;
    }
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if(msg instanceof PacketPlayOutSetSlot) {
            PacketPlayOutSetSlot packetPlayOutSetSlot = (PacketPlayOutSetSlot) msg;
            CallUpdateInvEvent(packetPlayOutSetSlot);
        }else if(msg instanceof PacketPlayOutSpawnEntity) {
            PacketPlayOutSpawnEntity otherPacket = (PacketPlayOutSpawnEntity) msg;
            handleEntitySpawn(getIntPacket(otherPacket, "a"));
        }else if(msg instanceof PacketPlayOutEntityDestroy) {
            PacketPlayOutEntityDestroy otherPacket = (PacketPlayOutEntityDestroy) msg;
            for(int id : getArrayIntsPacket(otherPacket, "a")){
                handleEntityDespawn(id);
            }
        }
        super.write(ctx, msg, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof PacketPlayInArmAnimation){
            if(checkInZone()){
                openMenu();
            }
        }
        super.channelRead(ctx, msg);
    }

    private boolean checkInZone(){
        PlayerData playerData = PlayerData.getPlayer(player.getBukkitEntity());
        return playerData.isZone();
    }

    private void openMenu() {
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        if(player.activeContainer != player.defaultContainer) return;
        plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getCosmeticsManager().openMenu(player.getBukkitEntity(), plugin.getMainMenu()));
    }

    private void CallUpdateInvEvent(PacketPlayOutSetSlot packetPlayOutSetSlot) {
        PacketDataSerializer packetDataSerializer = new PacketDataSerializer(Unpooled.buffer());
        try {
            packetPlayOutSetSlot.b(packetDataSerializer);
        } catch (IOException e) {
            return;
        }
        int containerId = packetDataSerializer.readByte();
        int slot = packetDataSerializer.readShort();
        ItemStack itemStack = packetDataSerializer.n();
        if(containerId != 0) return;
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        CosmeticInventoryUpdateEvent event;
        if(slot == 5){
            PlayerData playerData = PlayerData.getPlayer(player.getBukkitEntity());
            Hat hat = playerData.getHat();
            if(hat == null) return;
            event = new CosmeticInventoryUpdateEvent(player.getBukkitEntity(), CosmeticType.HAT, hat, CraftItemStack.asBukkitCopy(itemStack));
        }else if(slot == 45){
            PlayerData playerData = PlayerData.getPlayer(player.getBukkitEntity());
            WStick wStick = playerData.getWStick();
            if(wStick == null) return;
            event = new CosmeticInventoryUpdateEvent(player.getBukkitEntity(), CosmeticType.WALKING_STICK, wStick, CraftItemStack.asBukkitCopy(itemStack));
        }else {
            return;
        }
        plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getServer().getPluginManager().callEvent(event));
    }

    private void handleEntitySpawn(int id) {
        org.bukkit.entity.Entity entity = this.getEntityAsync(this.player.getWorldServer(), id);
        if(!(entity instanceof Player)) return;
        Player otherPlayer = (Player) entity;
        PlayerData playerData = PlayerData.getPlayer(otherPlayer);
        if(playerData == null) return;
        if(playerData.getBag() == null) return;
        playerData.getBag().spawn(this.player.getBukkitEntity());
    }

    private void handleEntityDespawn(int id) {
        org.bukkit.entity.Entity entity = this.getEntityAsync(this.player.getWorldServer(), id);
        if(!(entity instanceof Player)) return;
        Player otherPlayer = (Player) entity;
        PlayerData playerData = PlayerData.getPlayer(otherPlayer);
        if(playerData == null) return;
        if(playerData.getBag() == null) return;
        playerData.getBag().despawn(this.player.getBukkitEntity());
    }

    protected org.bukkit.entity.Entity getEntityAsync(WorldServer world, int id) {
        Entity entity = world.entitiesById.get(id);
        return entity == null ? null : entity.getBukkitEntity();
    }

    public int getIntPacket(Object packet, String fieldName) {
        try {
            Field field = packet.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.getInt(packet);
        }catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int[] getArrayIntsPacket(Object packet, String fieldName) {
        try {
            Field field = packet.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object arrayObject = field.get(packet);
            if (arrayObject != null && arrayObject.getClass().isArray()) {
                int length = Array.getLength(arrayObject);
                int[] result = new int[length];
                for (int i = 0; i < length; i++) {
                    result[i] = (int) Array.get(arrayObject, i);
                }
                return result;
            }
        }catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }
}
