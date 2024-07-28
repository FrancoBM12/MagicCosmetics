package com.francobm.magicosmetics.nms.v1_20_R1.models;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.api.CosmeticType;
import com.francobm.magicosmetics.cache.PlayerData;
import com.francobm.magicosmetics.cache.cosmetics.Hat;
import com.francobm.magicosmetics.cache.cosmetics.WStick;
import com.francobm.magicosmetics.events.CosmeticInventoryUpdateEvent;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.entity.LevelEntityGetter;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class MCChannelHandler extends ChannelDuplexHandler {
    private static Method entityGetter;

    static {
        for(Method method : WorldServer.class.getMethods()) {
            if(LevelEntityGetter.class.isAssignableFrom(method.getReturnType()) && method.getReturnType() != LevelEntityGetter.class) {
                entityGetter = method;
                break;
            }
        }
    }

    private final EntityPlayer player;

    public MCChannelHandler(EntityPlayer player){
        this.player = player;
    }
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if(msg instanceof PacketPlayOutSetSlot) {
            PacketPlayOutSetSlot packetPlayOutSetSlot = (PacketPlayOutSetSlot) msg;
            if(packetPlayOutSetSlot.a() == 0)
                CallUpdateInvEvent(packetPlayOutSetSlot.c(), packetPlayOutSetSlot.d());
        }else if(msg instanceof ClientboundBundlePacket) {
            ClientboundBundlePacket packet = (ClientboundBundlePacket) msg;
            for(Packet<?> subPacket : packet.a()){
                if(subPacket instanceof PacketPlayOutSpawnEntity) {
                    PacketPlayOutSpawnEntity otherPacket = (PacketPlayOutSpawnEntity) subPacket;
                    handleEntitySpawn(otherPacket.a());
                }else if(subPacket instanceof PacketPlayOutEntityDestroy) {
                    PacketPlayOutEntityDestroy otherPacket = (PacketPlayOutEntityDestroy) subPacket;
                    for(int id : otherPacket.a()){
                        handleEntityDespawn(id);
                    }
                }
            }
        }else if(msg instanceof PacketPlayOutSpawnEntity) {
            PacketPlayOutSpawnEntity otherPacket = (PacketPlayOutSpawnEntity) msg;
            handleEntitySpawn(otherPacket.a());
        }else if(msg instanceof PacketPlayOutEntityDestroy) {
            PacketPlayOutEntityDestroy otherPacket = (PacketPlayOutEntityDestroy) msg;
            for(int id : otherPacket.a()){
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
        plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getCosmeticsManager().openMenu(player.getBukkitEntity(), plugin.getMainMenu()));
    }

    private void CallUpdateInvEvent(int slot, ItemStack itemStack) {
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        CosmeticInventoryUpdateEvent event;
        PlayerData playerData = PlayerData.getPlayer(player.getBukkitEntity());
        if(playerData.isZone()) return;
        if(slot == 5){
            Hat hat = playerData.getHat();
            if(hat == null) return;
            event = new CosmeticInventoryUpdateEvent(player.getBukkitEntity(), CosmeticType.HAT, hat, CraftItemStack.asBukkitCopy(itemStack));
        }else if(slot == 45){
            WStick wStick = playerData.getWStick();
            if(wStick == null) return;
            event = new CosmeticInventoryUpdateEvent(player.getBukkitEntity(), CosmeticType.WALKING_STICK, wStick, CraftItemStack.asBukkitCopy(itemStack));
        }else {
            return;
        }
        plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getServer().getPluginManager().callEvent(event));
    }

    private void handleEntitySpawn(int id) {
        org.bukkit.entity.Entity entity = this.getEntityAsync(this.player.x(), id);
        if(!(entity instanceof Player)) return;
        Player otherPlayer = (Player) entity;
        PlayerData playerData = PlayerData.getPlayer(otherPlayer);
        if(playerData == null) return;
        if(playerData.getBag() == null) return;
        playerData.getBag().spawn(this.player.getBukkitEntity());
    }

    private void handleEntityDespawn(int id) {
        org.bukkit.entity.Entity entity = this.getEntityAsync(this.player.x(), id);
        if(!(entity instanceof Player)) return;
        Player otherPlayer = (Player) entity;
        PlayerData playerData = PlayerData.getPlayer(otherPlayer);
        if(playerData == null) return;
        if(playerData.getBag() == null) return;
        playerData.getBag().despawn(this.player.getBukkitEntity());
    }

    protected org.bukkit.entity.Entity getEntityAsync(WorldServer world, int id) {
        net.minecraft.world.entity.Entity entity = getEntityGetter(world).a(id);
        return entity == null ? null : entity.getBukkitEntity();
    }

    public static LevelEntityGetter<Entity> getEntityGetter(WorldServer level) {
        if(entityGetter == null)
            return level.M.d();
        try {
            return (LevelEntityGetter<net.minecraft.world.entity.Entity>) entityGetter.invoke(level);
        }catch (Throwable ignored) {
            return null;
        }
    }
}
