package com.francobm.magicosmetics.nms.v1_18_R1;

import com.francobm.magicosmetics.nms.NPC.ItemSlot;
import com.francobm.magicosmetics.nms.NPC.NPC;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.animal.EntityPufferFish;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class NPCHandler extends NPC {
    private EntityArmorStand balloon;
    private EntityLiving leashed;

    @Override
    public void spawnPunch(Player player, Location location) {
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        EntityArmorStand entityPunch = ((CraftArmorStand)this.punch).getHandle();
        entityPunch.b(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        float yaw = location.getYaw() * 256.0F / 360.0F;
        entityPlayer.b.a(new PacketPlayOutSpawnEntityLiving(entityPunch));
        entityPlayer.b.a(new PacketPlayOutEntityHeadRotation(entityPunch, (byte) yaw));
        entityPlayer.b.a(new PacketPlayOutEntityMetadata(entityPunch.ae(), entityPunch.ai(), true));
        entityPlayer.b.a(new PacketPlayOutCamera(entityPunch));
    }

    @Override
    public void addNPC(Player player) {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) player.getWorld()).getHandle();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), player.getName());
        EntityPlayer npc = new EntityPlayer(server, world, gameProfile);

        EntityArmorStand armorStand = new EntityArmorStand(EntityTypes.c, world);
        armorStand.b(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), 0);
        armorStand.j(true); //Invisible
        armorStand.m(true); //Invulnerable
        npc.b(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), 0);
        //npc.b(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
        //balloon
        balloon = new EntityArmorStand(EntityTypes.c, world);
        balloon.m(true); //invulnerable true
        balloon.j(true); //Invisible true

        EntityArmorStand entityPunch = new EntityArmorStand(EntityTypes.c, world);
        entityPunch.m(true);
        entityPunch.j(true);
        entityPunch.b(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
        leashed = new EntityPufferFish(EntityTypes.at, world);
        ((EntityPufferFish)leashed).b(npc, true);
        leashed.m(true);
        leashed.j(true);
        leashed.d(true); //silent true
        //balloon
        //skin
        try {
            String[] skin = getFromPlayer(player);
            npc.fp().getProperties().put("textures", new Property("textures", skin[0], skin[1]));
        }catch (NoSuchElementException ignored){

        }

        //skin
        // The client settings.
        DataWatcher watcher = armorStand.ai();
        watcher.b(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte)0x20);
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(armorStand.ae(), watcher, true);
        ((CraftPlayer)player).getHandle().b.a(packet);

        //
        this.entity = npc.getBukkitEntity();
        this.punch = entityPunch.getBukkitEntity();
        this.armorStand = armorStand.getBukkitEntity();
        addNPC(this, player);
    }

    @Override
    public void addNPC(Player player, Location location) {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) player.getWorld()).getHandle();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), player.getName());
        EntityPlayer npc = new EntityPlayer(server, world, gameProfile);
        EntityArmorStand armorStand = new EntityArmorStand(EntityTypes.c, world);
        armorStand.b(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), 0);
        npc.b(location.getX(), location.getY(), location.getZ(), location.getYaw(), 0);
        //balloon
        balloon = new EntityArmorStand(EntityTypes.c, world);
        balloon.m(true); //invulnerable true
        balloon.j(true); //Invisible true
        EntityArmorStand entityPunch = new EntityArmorStand(EntityTypes.c, world);
        entityPunch.m(true);
        entityPunch.j(true);
        entityPunch.b(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
        leashed = new EntityPufferFish(EntityTypes.at, world);
        ((EntityPufferFish)leashed).b(npc, true);
        leashed.m(true);
        leashed.j(true);
        leashed.d(true); //silent true
        //balloon
        //skin
        try {
            String[] skin = getFromPlayer(player);
            npc.fp().getProperties().put("textures", new Property("textures", skin[0], skin[1]));
        }catch (NoSuchElementException ignored){

        }
        //skin

        // The client settings.

        //
        this.entity = npc.getBukkitEntity();
        this.punch = entityPunch.getBukkitEntity();
        this.armorStand = armorStand.getBukkitEntity();

        addNPC(this, player);
    }

    @Override
    public void removeNPC(Player player) {
        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        connection.a(new PacketPlayOutEntityDestroy(armorStand.getEntityId(), entity.getEntityId(), punch.getEntityId(), balloon.ae(), leashed.ae()));
    }

    @Override
    public void removeBalloon(Player player) {
        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        connection.a(new PacketPlayOutEntityDestroy(balloon.ae(), leashed.ae()));
    }

    @Override
    public void spawnNPC(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer)this.entity).getHandle();
        EntityArmorStand armorStand = ((CraftArmorStand)this.armorStand).getHandle();
        armorStand.m(true); //invulnerable true
        armorStand.j(true); //Invisible true
        armorStand.t(true); //Marker true
        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        connection.a(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, entityPlayer));
        connection.a(new PacketPlayOutNamedEntitySpawn(entityPlayer));
        connection.a(new PacketPlayOutEntityHeadRotation(entityPlayer, (byte) (player.getLocation().getYaw() * 256 / 360)));
        connection.a(new PacketPlayOutSpawnEntityLiving(armorStand));
        //client settings
        connection.a(new PacketPlayOutEntityMetadata(armorStand.ae(), armorStand.ai(), true));
        DataWatcher watcher = entityPlayer.ai();
        byte bitmask = ((CraftPlayer)player).getHandle().ai().a(new DataWatcherObject<>(17, DataWatcherRegistry.a));
        watcher.b(new DataWatcherObject<>(17, DataWatcherRegistry.a), bitmask);
        connection.a(new PacketPlayOutEntityMetadata(entityPlayer.ae(), watcher, true));
        //
        new BukkitRunnable() {
            @Override
            public void run() {
                connection.a(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, entityPlayer));
            }
        }.runTaskLater(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("MagicCosmetics")), 20L);
        //addPassenger(player);
        addPassenger(player);
    }

    @Override
    public void lookNPC(Player player, float yaw) {
        EntityPlayer entityPlayer = ((CraftPlayer)this.entity).getHandle();
        EntityArmorStand armorStand = ((CraftArmorStand)this.armorStand).getHandle();

        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        connection.a(new PacketPlayOutEntityHeadRotation(armorStand, (byte)(yaw * 256 / 360)));
        connection.a(new PacketPlayOutEntity.PacketPlayOutEntityLook(armorStand.ae(), (byte)(yaw * 256 / 360), (byte)0, true));

        connection.a(new PacketPlayOutEntityHeadRotation(entityPlayer, (byte)(yaw * 256 / 360)));
        connection.a(new PacketPlayOutEntity.PacketPlayOutEntityLook(entityPlayer.ae(), (byte)(yaw * 256 / 360), (byte)0, true));
        //connection.a(new PacketPlayOutEntityTeleport(entityPlayer));
    }

    @Override
    public void armorStandSetItem(Player player, ItemStack itemStack) {
        EntityArmorStand entityPlayer = ((CraftArmorStand)this.armorStand).getHandle();
        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> list = new ArrayList<>();
        list.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(itemStack)));
        connection.a(new PacketPlayOutEntityEquipment(entityPlayer.ae(), list));
        /*if(itemStack == null || itemStack.getType() == Material.AIR){
            return;
        }*/
    }

    @Override
    public void balloonSetItem(Player player, ItemStack itemStack) {
        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> list = new ArrayList<>();
        if(isBigHead()){
            list.add(new Pair<>(EnumItemSlot.a, CraftItemStack.asNMSCopy(itemStack)));
        }else {
            list.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(itemStack)));
        }
        connection.a(new PacketPlayOutEntityEquipment(balloon.ae(), list));
    }

    @Override
    public void balloonNPC(Player player, Location location, ItemStack itemStack, boolean bigHead){
        removeBalloon(player);
        //balloon
        EntityPlayer entityPlayer = ((CraftPlayer)this.entity).getHandle();
        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        balloonPosition = location.clone();
        balloon.b(location.getX(), location.getY()-1.2, location.getZ(), location.getYaw(), location.getPitch());

        leashed.b(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        this.bigHead = bigHead;
        if(isBigHead()){
            balloon.d(new Vector3f(balloon.cj.b(), 0, 0));
        }
        connection.a(new PacketPlayOutSpawnEntityLiving(balloon));
        connection.a(new PacketPlayOutSpawnEntityLiving(leashed));
        DataWatcher watcher1 = balloon.ai();
        watcher1.b(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte)0x20);
        connection.a(new PacketPlayOutEntityMetadata(balloon.ae(), watcher1, true));
        DataWatcher watcher2 = leashed.ai();
        watcher2.b(new DataWatcherObject<>(4, DataWatcherRegistry.i), leashed.aL());
        connection.a(new PacketPlayOutEntityMetadata(leashed.ae(), watcher2, true));
        connection.a(new PacketPlayOutAttachEntity(leashed, entityPlayer));
        balloonSetItem(player, itemStack);
    }

    @Override
    public void equipNPC(Player player, ItemSlot itemSlot, ItemStack itemStack) {
        EntityPlayer entityPlayer = ((CraftPlayer)this.entity).getHandle();
        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> list = new ArrayList<>();
        switch (itemSlot){
            case MAIN_HAND:
                list.add(new Pair<>(EnumItemSlot.a, CraftItemStack.asNMSCopy(itemStack)));
                connection.a(new PacketPlayOutEntityEquipment(entityPlayer.ae(), list));
                break;
            case OFF_HAND:
                list.add(new Pair<>(EnumItemSlot.b, CraftItemStack.asNMSCopy(itemStack)));
                connection.a(new PacketPlayOutEntityEquipment(entityPlayer.ae(), list));
                break;
            case BOOTS:
                list.add(new Pair<>(EnumItemSlot.c, CraftItemStack.asNMSCopy(itemStack)));
                connection.a(new PacketPlayOutEntityEquipment(entityPlayer.ae(), list));
                break;
            case LEGGINGS:
                list.add(new Pair<>(EnumItemSlot.d, CraftItemStack.asNMSCopy(itemStack)));
                connection.a(new PacketPlayOutEntityEquipment(entityPlayer.ae(), list));
                break;
            case CHESTPLATE:
                list.add(new Pair<>(EnumItemSlot.e, CraftItemStack.asNMSCopy(itemStack)));
                connection.a(new PacketPlayOutEntityEquipment(entityPlayer.ae(), list));
                break;
            case HELMET:
                list.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(itemStack)));
                connection.a(new PacketPlayOutEntityEquipment(entityPlayer.ae(), list));
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
            packetDataSerializer.d(entityPlayer.ae());
            packetDataSerializer.a(new int[]{armorStand.ae()});
            return new PacketPlayOutMount(packetDataSerializer);
        });
        p.b.a(packetPlayOutMount);
    }

    public void addPassenger(Player player, net.minecraft.world.entity.Entity entity) {
        EntityPlayer p = ((CraftPlayer)player).getHandle();
        PacketPlayOutMount packetPlayOutMount = this.createDataSerializer(packetDataSerializer -> {
            packetDataSerializer.d(p.ae());
            packetDataSerializer.a(new int[]{entity.ae()});
            return new PacketPlayOutMount(packetDataSerializer);
        });
        p.b.a(packetPlayOutMount);
    }

    public void addPassenger(Player player, net.minecraft.world.entity.Entity entity1, net.minecraft.world.entity.Entity entity2) {
        if(entity1 == null) return;
        if(entity2 == null) return;
        EntityPlayer p = ((CraftPlayer)player).getHandle();
        PacketPlayOutMount packetPlayOutMount = this.createDataSerializer(packetDataSerializer -> {
            packetDataSerializer.d(entity1.ae());
            packetDataSerializer.a(new int[]{entity2.ae()});
            return new PacketPlayOutMount(packetDataSerializer);
        });
        p.b.a(packetPlayOutMount);
    }

    public void animation(Player player){
        if(isBigHead()){
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
            balloon.a(new Vector3f(balloon.u().b() - 0.5f, balloon.u().c(), balloon.u().d() + rotate));
            //armorStand.setHeadPose(armorStand.getHeadPose().add(0, 0, rotate).subtract(0.008, 0, 0));
            if (rot > 0.20) {
                rotateLoop = true;
            }
        } else {
            rot -= 0.01;
            balloon.a(new Vector3f(balloon.u().b() + 0.5f, balloon.u().c(), balloon.u().d() + rotate));
            //armorStand.setHeadPose(armorStand.getHeadPose().add(0.008, 0, rotate));//.subtract(0.006, 0, 0));
            if (rot < -0.20) {
                rotateLoop = false;
            }
        }
        leashed.a(balloonPosition.getX(), balloonPosition.getY(), balloonPosition.getZ(), balloonPosition.getYaw(), balloonPosition.getPitch());
        balloon.a(balloonPosition.getX(), balloonPosition.getY() - 1.3, balloonPosition.getZ(), balloonPosition.getYaw(), balloonPosition.getPitch());
        p.b.a(new PacketPlayOutEntityMetadata(balloon.ae(), balloon.ai(), true));
        p.b.a(new PacketPlayOutEntityTeleport(leashed));
        p.b.a(new PacketPlayOutEntityTeleport(balloon));
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
            balloon.d(new Vector3f(balloon.cj.b() - 0.5f, balloon.cj.c(), balloon.cj.d() + rotate));
            //armorStand.setHeadPose(armorStand.getHeadPose().add(0, 0, rotate).subtract(0.008, 0, 0));
            if (rot > 0.20) {
                rotateLoop = true;
            }
        } else {
            rot -= 0.01;
            balloon.d(new Vector3f(balloon.cj.b() + 0.5f, balloon.cj.c(), balloon.cj.d() + rotate));
            //armorStand.setHeadPose(armorStand.getHeadPose().add(0.008, 0, rotate));//.subtract(0.006, 0, 0));
            if (rot < -0.20) {
                rotateLoop = false;
            }
        }
        leashed.a(balloonPosition.getX(), balloonPosition.getY(), balloonPosition.getZ(), balloonPosition.getYaw(), balloonPosition.getPitch());
        balloon.a(balloonPosition.getX(), balloonPosition.getY() - 1.3, balloonPosition.getZ(), balloonPosition.getYaw(), balloonPosition.getPitch());
        p.b.a(new PacketPlayOutEntityMetadata(balloon.ae(), balloon.ai(), true));
        p.b.a(new PacketPlayOutEntityTeleport(leashed));
        p.b.a(new PacketPlayOutEntityTeleport(balloon));
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
        GameProfile profile = playerNMS.fp();

        Property property = profile.getProperties().get("textures").iterator().next();
        String texture = property.getValue();
        String signature = property.getSignature();
        return new String[] {texture, signature};
        //CustomCosmetics.getInstance().getLogger().warning("NPC Skin: Player " + playerBukkit.getName() + " not have skin!");
    }

    public String[] getFromName(String name) throws IOException {
        URL url_0 = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
        InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());
        String uuid = JsonParser.parseReader(reader_0).getAsJsonObject().get("id").getAsString();

        URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
        InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
        JsonObject textureProperty = JsonParser.parseReader(reader_1).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
        String texture = textureProperty.get("value").getAsString();
        String signature = textureProperty.get("signature").getAsString();

        return new String[] {texture, signature};
        //CustomCosmetics.getInstance().getLogger().severe("Could not get skin data from session servers!");
        //CustomCosmetics.getInstance().getLogger().severe("parsing to player skin..");
    }
}
