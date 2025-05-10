package com.francobm.magicosmetics.nms.v1_18_R2.models;

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
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;

public class MCChannelHandler extends ChannelDuplexHandler {
    private final EntityPlayer player;

    public MCChannelHandler(EntityPlayer player){
        this.player = player;
    }
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if(msg instanceof PacketPlayOutSetSlot) {
            PacketPlayOutSetSlot packetPlayOutSetSlot = (PacketPlayOutSetSlot) msg;
            if(packetPlayOutSetSlot.b() == 0)
                CallUpdateInvEvent(packetPlayOutSetSlot.c(), packetPlayOutSetSlot.d());
        }else if(msg instanceof PacketPlayOutNamedEntitySpawn) {
            PacketPlayOutNamedEntitySpawn otherPacket = (PacketPlayOutNamedEntitySpawn) msg;
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
        int id = packetPlayOutMount.c();
        int[] ids =packetPlayOutMount.b();
        org.bukkit.entity.Entity entity = this.getEntityAsync(this.player.x(), id);
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
        data.d(id);
        data.a(newIds);
        return new PacketPlayOutMount(data);
    }

    private void handleEntitySpawn(int id) {
        org.bukkit.entity.Entity entity = this.getEntityAsync(this.player.x(), id);
        if(!(entity instanceof Player)) return;
        Player otherPlayer = (Player) entity;
        PlayerData playerData = PlayerData.getPlayer(otherPlayer);
        if(playerData == null) return;
        if(playerData.getBag() == null) return;
        Bukkit.getServer().getScheduler().runTask(MagicCosmetics.getInstance(), () -> playerData.getBag().spawn(this.player.getBukkitEntity()));
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
        PersistentEntitySectionManager<Entity> entityManager = world.O;
        Entity entity = entityManager.d().a(id);
        return entity == null ? null : entity.getBukkitEntity();
    }
}
