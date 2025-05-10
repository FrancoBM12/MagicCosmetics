package com.francobm.magicosmetics.nms.v1_21_R1.models;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.api.CosmeticType;
import com.francobm.magicosmetics.cache.PlayerData;
import com.francobm.magicosmetics.cache.cosmetics.Hat;
import com.francobm.magicosmetics.cache.cosmetics.WStick;
import com.francobm.magicosmetics.cache.cosmetics.backpacks.Bag;
import com.francobm.magicosmetics.events.CosmeticInventoryUpdateEvent;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.entity.LevelEntityGetter;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Arrays;

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
            if(packetPlayOutSetSlot.b() == 0)
                CallUpdateInvEvent(packetPlayOutSetSlot.e(), packetPlayOutSetSlot.f());
        }else if(msg instanceof ClientboundBundlePacket) {
            ClientboundBundlePacket packet = (ClientboundBundlePacket) msg;
            for(Packet<?> subPacket : packet.b()){
                if(subPacket instanceof PacketPlayOutSpawnEntity) {
                    PacketPlayOutSpawnEntity otherPacket = (PacketPlayOutSpawnEntity) subPacket;
                    handleEntitySpawn(otherPacket.b());
                }else if(subPacket instanceof PacketPlayOutEntityDestroy) {
                    PacketPlayOutEntityDestroy otherPacket = (PacketPlayOutEntityDestroy) subPacket;
                    for(int id : otherPacket.b()){
                        handleEntityDespawn(id);
                    }
                }
            }
        }else if(msg instanceof PacketPlayOutSpawnEntity) {
            PacketPlayOutSpawnEntity otherPacket = (PacketPlayOutSpawnEntity) msg;
            handleEntitySpawn(otherPacket.b());
        }else if(msg instanceof PacketPlayOutEntityDestroy) {
            PacketPlayOutEntityDestroy otherPacket = (PacketPlayOutEntityDestroy) msg;
            for(int id : otherPacket.b()){
                handleEntityDespawn(id);
            }
        }else if(msg instanceof PacketPlayOutMount) {
            PacketPlayOutMount otherPacket = (PacketPlayOutMount) msg;
            msg = handleEntityMount(otherPacket);
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

    private boolean isOffHand(){
        PlayerData playerData = PlayerData.getPlayer(player.getBukkitEntity());
        return playerData.getWStick() != null;
    }

    private void openMenu() {
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getCosmeticsManager().openMenu(player.getBukkitEntity(), plugin.getMainMenu()));
    }

    private void CallUpdateInvEvent(int slot, ItemStack itemStack) {
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


    private PacketPlayOutMount handleEntityMount(PacketPlayOutMount packetPlayOutMount) {
        int id = packetPlayOutMount.e();
        int[] ids =packetPlayOutMount.b();
        org.bukkit.entity.Entity entity = this.getEntityAsync(this.player.A(), id);
        if(!(entity instanceof Player)) return packetPlayOutMount;
        Player otherPlayer = (Player) entity;
        PlayerData playerData = PlayerData.getPlayer(otherPlayer);
        if(playerData.getBag() == null) return packetPlayOutMount;

        Bag bag = (Bag) playerData.getBag();
        if(bag.getBackpackId() == -1) return packetPlayOutMount;
        int[] newIds = new int[ids.length + 1];
        newIds[0] = bag.getBackpackId();
        for(int i = 0; i < ids.length; i++){
            if(ids[i] == bag.getBackpackId()) continue;
            newIds[i + 1] = ids[i];
        }
        PacketDataSerializer data = new PacketDataSerializer(Unpooled.buffer());
        data.c(id);
        data.a(newIds);
        return PacketPlayOutMount.a.decode(data);
    }

    private void handleEntitySpawn(int id) {
        org.bukkit.entity.Entity entity = this.getEntityAsync(this.player.A(), id);
        if(!(entity instanceof Player)) return;
        Player otherPlayer = (Player) entity;
        PlayerData playerData = PlayerData.getPlayer(otherPlayer);
        if(playerData == null) return;
        if(playerData.getBag() == null) return;
        Bukkit.getServer().getScheduler().runTask(MagicCosmetics.getInstance(), () -> playerData.getBag().spawn(this.player.getBukkitEntity()));
    }

    private void handleEntityDespawn(int id) {
        org.bukkit.entity.Entity entity = this.getEntityAsync(this.player.A(), id);
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
            return level.N.d();
        try {
            return (LevelEntityGetter<net.minecraft.world.entity.Entity>) entityGetter.invoke(level);
        }catch (Throwable ignored) {
            return null;
        }
    }
}
