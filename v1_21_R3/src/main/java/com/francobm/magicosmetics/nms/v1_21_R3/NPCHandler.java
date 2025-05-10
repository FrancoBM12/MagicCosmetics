package com.francobm.magicosmetics.nms.v1_21_R3;

import com.francobm.magicosmetics.nms.NPC.ItemSlot;
import com.francobm.magicosmetics.nms.NPC.NPC;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import io.netty.buffer.Unpooled;
import net.minecraft.core.Vector3f;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.EntityPufferFish;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_21_R3.CraftServer;
import org.bukkit.craftbukkit.v1_21_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class NPCHandler extends NPC {
    private EntityArmorStand balloon;
    private EntityLiving leashed;

    @Override
    public void spawnPunch(Player player, Location location) {
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        EntityLiving entityPunch = ((CraftLivingEntity)this.punch).getHandle();
        entityPunch.b(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        float yaw = location.getYaw() * 256.0F / 360.0F;
        PacketPlayOutSpawnEntity packetPlayOutSpawnEntity = new PacketPlayOutSpawnEntity(entityPunch.ar(), entityPunch.cG(), location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw(), entityPunch.aq(), 0, entityPunch.dy(), entityPunch.cA());
        entityPlayer.f.b(packetPlayOutSpawnEntity);
        entityPlayer.f.b(new PacketPlayOutEntityHeadRotation(entityPunch, (byte) yaw));
        entityPlayer.f.b(new PacketPlayOutEntityMetadata(entityPunch.ar(), entityPunch.au().c()));
        entityPlayer.f.b(new PacketPlayOutCamera(entityPunch));
    }

    @Override
    public void addNPC(Player player) {
        addNPC(player, player.getLocation());
    }

    @Override
    public void addNPC(Player player, Location location) {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) player.getWorld()).getHandle();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), player.getName());
        EntityPlayer npc = new EntityPlayer(server, world, gameProfile, ClientInformation.a());

        EntityArmorStand armorStand = new EntityArmorStand(EntityTypes.f, world);
        armorStand.k(true); //Invisible
        armorStand.n(true); //Invulnerable
        armorStand.b(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), 0);
        npc.b(location.getX(), location.getY(), location.getZ(), location.getYaw(), 0);
        //balloon
        balloon = new EntityArmorStand(EntityTypes.f, world);
        balloon.n(true); //invulnerable true
        balloon.k(true); //Invisible true
        EntityArmorStand entityPunch = new EntityArmorStand(EntityTypes.f, world);
        entityPunch.n(true);
        entityPunch.k(true);
        entityPunch.b(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
        leashed = new EntityPufferFish(EntityTypes.aW, world);
        ((EntityPufferFish)leashed).b(npc, true);
        leashed.n(true);
        leashed.k(true);
        leashed.d(true); //silent true
        //balloon
        //skin
        try {
            String[] skin = getFromPlayer(player);
            npc.getBukkitEntity().getProfile().getProperties().put("textures", new Property("textures", skin[0], skin[1]));
        }catch (NoSuchElementException ignored){

        }
        //skin
        //
        this.entity = npc.getBukkitEntity();
        this.punch = entityPunch.getBukkitEntity();
        this.armorStand = armorStand.getBukkitEntity();

        addNPC(this, player);
    }

    @Override
    public void removeNPC(Player player) {
        PlayerConnection connection = ((CraftPlayer)player).getHandle().f;
        connection.b(new PacketPlayOutEntityDestroy(armorStand.getEntityId(), entity.getEntityId(), punch.getEntityId(), balloon.ar(), leashed.ar()));
    }

    @Override
    public void removeBalloon(Player player) {
        PlayerConnection connection = ((CraftPlayer)player).getHandle().f;
        connection.b(new PacketPlayOutEntityDestroy(balloon.ar(), leashed.ar()));
    }

    @Override
    public void spawnNPC(Player player) {
        Location npcLocation = entity.getLocation();
        Location armorStandLocation = armorStand.getLocation();
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        EntityPlayer npc = ((CraftPlayer)this.entity).getHandle();
        EntityArmorStand armorStand = ((CraftArmorStand)this.armorStand).getHandle();
        armorStand.n(true); //invulnerable true
        armorStand.k(true); //Invisible true
        npc.f = entityPlayer.f;
        PacketPlayOutSpawnEntity npcSpawnPacket = new PacketPlayOutSpawnEntity(npc.ar(), npc.cG(), npcLocation.getX(), npcLocation.getY(), npcLocation.getZ(), npcLocation.getPitch(), npcLocation.getYaw(), npc.aq(), 0, npc.dy(), npc.cA());
        PacketPlayOutSpawnEntity armorStandSpawnPacket = new PacketPlayOutSpawnEntity(armorStand.ar(), armorStand.cG(), armorStandLocation.getX(), armorStandLocation.getY(), armorStandLocation.getZ(), armorStandLocation.getPitch(), armorStandLocation.getYaw(), armorStand.aq(), 0, armorStand.dy(), armorStand.cA());
        entityPlayer.f.b(new ClientboundPlayerInfoUpdatePacket(Enum.valueOf(ClientboundPlayerInfoUpdatePacket.a.class, "ADD_PLAYER"), npc));
        entityPlayer.f.b(npcSpawnPacket);
        entityPlayer.f.b(new PacketPlayOutEntityHeadRotation(npc, (byte) (player.getLocation().getYaw() * 256 / 360)));
        entityPlayer.f.b(armorStandSpawnPacket);
        //client settings
        entityPlayer.f.b(new PacketPlayOutEntityMetadata(armorStand.ar(), armorStand.au().c()));
        //
        DataWatcher watcher = npc.au();
        byte bitmask = ((CraftPlayer)player).getHandle().au().a(new DataWatcherObject<>(17, DataWatcherRegistry.a));
        watcher.a(new DataWatcherObject<>(17, DataWatcherRegistry.a), bitmask);
        entityPlayer.f.b(new PacketPlayOutEntityMetadata(npc.ar(), watcher.c()));
        new BukkitRunnable() {
            @Override
            public void run() {
                entityPlayer.f.b(new ClientboundPlayerInfoRemovePacket(Collections.singletonList(npc.getBukkitEntity().getUniqueId())));
            }
        }.runTaskLater(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("MagicCosmetics")), 20L);
        addPassenger(player);
    }

    @Override
    public void lookNPC(Player player, float yaw) {
        EntityPlayer entityPlayer = ((CraftPlayer)this.entity).getHandle();
        EntityArmorStand armorStand = ((CraftArmorStand)this.armorStand).getHandle();
        armorStand.n(true); //invulnerable true
        armorStand.k(true); //Invisible true
        PlayerConnection connection = ((CraftPlayer)player).getHandle().f;
        connection.b(new PacketPlayOutEntityHeadRotation(armorStand, (byte)(yaw * 256 / 360)));
        connection.b(new PacketPlayOutEntity.PacketPlayOutEntityLook(armorStand.ar(), (byte)(yaw * 256 / 360), (byte)0, true));

        connection.b(new PacketPlayOutEntityHeadRotation(entityPlayer, (byte)(yaw * 256 / 360)));
        connection.b(new PacketPlayOutEntity.PacketPlayOutEntityLook(entityPlayer.ar(), (byte)(yaw * 256 / 360), (byte)0, true));
        //connection.b(new PacketPlayOutEntityTeleport(entityPlayer));
    }

    @Override
    public void armorStandSetItem(Player player, ItemStack itemStack) {
        EntityArmorStand entityPlayer = ((CraftArmorStand)this.armorStand).getHandle();
        PlayerConnection connection = ((CraftPlayer)player).getHandle().f;
        ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> list = new ArrayList<>();
        list.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(itemStack)));
        connection.b(new PacketPlayOutEntityEquipment(entityPlayer.ar(), list));
    }

    @Override
    public void balloonSetItem(Player player, ItemStack itemStack) {
        PlayerConnection connection = ((CraftPlayer)player).getHandle().f;
        ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> list = new ArrayList<>();
        if(isBigHead()){
            list.add(new Pair<>(EnumItemSlot.a, CraftItemStack.asNMSCopy(itemStack)));
        }else {
            list.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(itemStack)));
        }
        connection.b(new PacketPlayOutEntityEquipment(balloon.ar(), list));
    }

    @Override
    public void balloonNPC(Player player, Location location, ItemStack itemStack, boolean bigHead){
        removeBalloon(player);
        //balloon
        EntityPlayer entityPlayer = ((CraftPlayer)this.entity).getHandle();
        EntityPlayer realPlayer = ((CraftPlayer)player).getHandle();
        balloonPosition = location.clone();
        Location balloonPos = location.clone();
        balloonPos.setX(balloonPos.getY()-1.3);
        balloon.b(balloonPos.getX(), balloonPos.getY(), balloonPos.getZ(), balloonPos.getYaw(), balloonPos.getPitch());

        leashed.b(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        this.bigHead = bigHead;
        if(isBigHead()){
            balloon.d(new Vector3f(balloon.C().b(), 0, 0));
        }
        PacketPlayOutSpawnEntity balloonSpawnPacket = new PacketPlayOutSpawnEntity(balloon.ar(), balloon.cG(), balloonPos.getX(), balloonPos.getY(), balloonPos.getZ(), balloonPos.getPitch(), balloonPos.getYaw(), balloon.aq(), 0, balloon.dy(), balloon.cA());
        PacketPlayOutSpawnEntity leashedSpawnPacket = new PacketPlayOutSpawnEntity(leashed.ar(), leashed.cG(), balloonPosition.getX(), balloonPosition.getY(), balloonPosition.getZ(), balloonPosition.getPitch(), balloonPosition.getYaw(), leashed.aq(), 0, leashed.dy(), leashed.cA());
        realPlayer.f.b(balloonSpawnPacket);
        realPlayer.f.b(leashedSpawnPacket);
        realPlayer.f.b(new PacketPlayOutEntityMetadata(balloon.ar(), balloon.au().c()));
        realPlayer.f.b(new PacketPlayOutEntityMetadata(leashed.ar(), leashed.au().c()));
        realPlayer.f.b(new PacketPlayOutAttachEntity(leashed, entityPlayer));
        balloonSetItem(player, itemStack);
    }

    @Override
    public void equipNPC(Player player, ItemSlot itemSlot, ItemStack itemStack) {
        EntityPlayer entityPlayer = ((CraftPlayer)this.entity).getHandle();
        PlayerConnection connection = ((CraftPlayer)player).getHandle().f;
        ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> list = new ArrayList<>();
        switch (itemSlot){
            case MAIN_HAND:
                list.add(new Pair<>(EnumItemSlot.a, CraftItemStack.asNMSCopy(itemStack)));
                connection.b(new PacketPlayOutEntityEquipment(entityPlayer.ar(), list));
                break;
            case OFF_HAND:
                list.add(new Pair<>(EnumItemSlot.b, CraftItemStack.asNMSCopy(itemStack)));
                connection.b(new PacketPlayOutEntityEquipment(entityPlayer.ar(), list));
                break;
            case BOOTS:
                list.add(new Pair<>(EnumItemSlot.c, CraftItemStack.asNMSCopy(itemStack)));
                connection.b(new PacketPlayOutEntityEquipment(entityPlayer.ar(), list));
                break;
            case LEGGINGS:
                list.add(new Pair<>(EnumItemSlot.d, CraftItemStack.asNMSCopy(itemStack)));
                connection.b(new PacketPlayOutEntityEquipment(entityPlayer.ar(), list));
                break;
            case CHESTPLATE:
                list.add(new Pair<>(EnumItemSlot.e, CraftItemStack.asNMSCopy(itemStack)));
                connection.b(new PacketPlayOutEntityEquipment(entityPlayer.ar(), list));
                break;
            case HELMET:
                list.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(itemStack)));
                connection.b(new PacketPlayOutEntityEquipment(entityPlayer.ar(), list));
                break;
        }
    }

    @Override
    public void addPassenger(Player player) {
        if(entity == null) return;
        EntityArmorStand armorStand = ((CraftArmorStand)this.armorStand).getHandle();
        EntityPlayer p = ((CraftPlayer)player).getHandle();
        EntityPlayer entityPlayer = ((CraftPlayer)this.entity).getHandle();
        PacketPlayOutMount packetPlayOutMount = this.createDataSerializer(packetDataSerializer -> {
            packetDataSerializer.c(entityPlayer.ar());
            packetDataSerializer.a(new int[]{armorStand.ar()});
            return PacketPlayOutMount.a.decode(packetDataSerializer);
        });
        p.f.b(packetPlayOutMount);
    }

    public void addPassenger(Player player, net.minecraft.world.entity.Entity entity1, net.minecraft.world.entity.Entity entity2) {
        if(entity1 == null) return;
        if(entity2 == null) return;
        EntityPlayer p = ((CraftPlayer)player).getHandle();
        PacketPlayOutMount packetPlayOutMount = this.createDataSerializer(packetDataSerializer -> {
            packetDataSerializer.c(entity1.ar());
            packetDataSerializer.a(new int[]{entity2.ar()});
            return PacketPlayOutMount.a.decode(packetDataSerializer);
        });
        p.f.b(packetPlayOutMount);
    }

    public void animation(Player player){
        if(isBigHead()) {
            animationBigHead(player);
            return;
        }
        EntityPlayer p = ((CraftPlayer)player).getHandle();
        //
        if(balloonPosition == null) return;
        if (!floatLoop) {
            y += 0.01;
            balloonPosition.add(0, 0.01, 0);
            //standToLoc.setYaw(standToLoc.getYaw() - 3F);
            if (y > 0.10) {
                floatLoop = true;
            }
        } else {
            y -= 0.01;
            balloonPosition.subtract(0, 0.01, 0);
            //standToLoc.setYaw(standToLoc.getYaw() + 3F);
            if (y < (-0.11 + 0)) {
                floatLoop = false;
                rotate *= -1;
            }
        }
        if (!rotateLoop) {
            rot += 0.01;
            balloon.a(new Vector3f(balloon.B().b() - 0.5f, balloon.B().c(), balloon.B().d() + rotate));
            //armorStand.setHeadPose(armorStand.getHeadPose().add(0, 0, rotate).subtract(0.008, 0, 0));
            if (rot > 0.20) {
                rotateLoop = true;
            }
        } else {
            rot -= 0.01;
            balloon.a(new Vector3f(balloon.B().b() + 0.5f, balloon.B().c(), balloon.B().d() + rotate));
            //armorStand.setHeadPose(armorStand.getHeadPose().add(0.008, 0, rotate));//.subtract(0.006, 0, 0));
            if (rot < -0.20) {
                rotateLoop = false;
            }
        }
        leashed.a(balloonPosition.getX(), balloonPosition.getY(), balloonPosition.getZ(), balloonPosition.getYaw(), balloonPosition.getPitch());
        balloon.a(balloonPosition.getX(), balloonPosition.getY() - 1.3, balloonPosition.getZ(), balloonPosition.getYaw(), balloonPosition.getPitch());
        p.f.b(new PacketPlayOutEntityMetadata(balloon.ar(), balloon.au().c()));
        p.f.b(new PacketPlayOutEntityTeleport(leashed.ar(), PositionMoveRotation.a(leashed), Relative.j,  false));
        p.f.b(new PacketPlayOutEntityTeleport(balloon.ar(), PositionMoveRotation.a(balloon), Relative.j,  false));
    }

    public void animationBigHead(Player player){
        EntityPlayer p = ((CraftPlayer)player).getHandle();
        //
        if(balloonPosition == null) return;
        if (!floatLoop) {
            y += 0.01;
            balloonPosition.add(0, 0.01, 0);
            //standToLoc.setYaw(standToLoc.getYaw() - 3F);
            if (y > 0.10) {
                floatLoop = true;
            }
        } else {
            y -= 0.01;
            balloonPosition.subtract(0, 0.01, 0);
            //standToLoc.setYaw(standToLoc.getYaw() + 3F);
            if (y < (-0.11 + 0)) {
                floatLoop = false;
                rotate *= -1;
            }
        }
        if (!rotateLoop) {
            rot += 0.01;
            balloon.d(new Vector3f(balloon.C().b() - 0.5f, balloon.C().c(), balloon.C().d() + rotate));
            //armorStand.setHeadPose(armorStand.getHeadPose().add(0, 0, rotate).subtract(0.008, 0, 0));
            if (rot > 0.20) {
                rotateLoop = true;
            }
        } else {
            rot -= 0.01;
            balloon.d(new Vector3f(balloon.C().b() + 0.5f, balloon.C().c(), balloon.C().d() + rotate));
            //armorStand.setHeadPose(armorStand.getHeadPose().add(0.008, 0, rotate));//.subtract(0.006, 0, 0));
            if (rot < -0.20) {
                rotateLoop = false;
            }
        }
        leashed.a(balloonPosition.getX(), balloonPosition.getY(), balloonPosition.getZ(), balloonPosition.getYaw(), balloonPosition.getPitch());
        balloon.a(balloonPosition.getX(), balloonPosition.getY() - 1.3, balloonPosition.getZ(), balloonPosition.getYaw(), balloonPosition.getPitch());
        p.f.b(new PacketPlayOutEntityMetadata(balloon.ar(), balloon.au().c()));
        p.f.b(new PacketPlayOutEntityTeleport(leashed.ar(), PositionMoveRotation.a(leashed), Relative.j,  false));
        p.f.b(new PacketPlayOutEntityTeleport(balloon.ar(), PositionMoveRotation.a(balloon), Relative.j,  false));
    }

    @Override
    public NPC getNPC(Player player) {
        return npcs.get(player.getUniqueId());
    }

    private <T> T createDataSerializer(UnsafeFunction<PacketDataSerializer, T> callback) {
        PacketDataSerializer data = new PacketDataSerializer(Unpooled.buffer());
        T result = null;
        try {
            result = callback.apply(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            data.release();
        }
        return result;
    }

    @FunctionalInterface
    private interface UnsafeFunction<K, T> {
        T apply(K k) throws Exception;
    }

    public String[] getFromPlayer(Player playerBukkit) throws NoSuchElementException{
        EntityPlayer playerNMS = ((CraftPlayer) playerBukkit).getHandle();
        GameProfile profile = playerNMS.getBukkitEntity().getProfile();

        Property property = profile.getProperties().get("textures").iterator().next();
        String texture = property.value();
        String signature = property.signature();
        return new String[] {texture, signature};
    }
}
