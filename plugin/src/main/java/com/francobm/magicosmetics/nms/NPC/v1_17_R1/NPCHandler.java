package com.francobm.magicosmetics.nms.NPC.v1_17_R1;

import com.francobm.magicosmetics.MagicCosmetics;
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
import net.minecraft.world.entity.ambient.EntityBat;
import net.minecraft.world.entity.animal.EntityBee;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.UUID;

public class NPCHandler extends NPC {
    private EntityArmorStand balloon;
    private EntityLiving leashed;
    private boolean floatLoop;
    private double y = 0;

    @Override
    public void spawnPunch(Player player, Location location) {
        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        EntityArmorStand entityPunch = ((CraftArmorStand)this.punch).getHandle();
        entityPunch.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        connection.sendPacket(new PacketPlayOutSpawnEntityLiving(entityPunch));
        DataWatcher watcher = entityPunch.getDataWatcher();
        watcher.set(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte)0x20);
        connection.sendPacket(new PacketPlayOutEntityMetadata(entityPunch.getId(), watcher, true));
    }

    @Override
    public void addNPC(Player player) {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) player.getWorld()).getHandle();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), player.getName());
        EntityPlayer npc = new EntityPlayer(server, world, gameProfile);

        EntityArmorStand armorStand = new EntityArmorStand(EntityTypes.c, world);
        armorStand.setPositionRotation(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), 0);
        armorStand.setInvisible(true); //Invisible
        armorStand.setInvulnerable(true); //Invulnerable
        npc.setPositionRotation(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), 0);
        //npc.b(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
        //balloon
        balloon = new EntityArmorStand(EntityTypes.c, world);
        balloon.setInvulnerable(true); //invulnerable true
        balloon.setInvisible(true); //Invisible true
        EntityArmorStand entityPunch = new EntityArmorStand(EntityTypes.c, world);
        entityPunch.setInvulnerable(true);
        entityPunch.setInvisible(true);
        entityPunch.setPositionRotation(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
        leashed = new EntityBee(EntityTypes.g, world);
        ((EntityBee)leashed).setLeashHolder(npc, true);
        leashed.setInvisible(true);
        leashed.setInvulnerable(true);
        leashed.setSilent(true); //silent true
        floatLoop = true;
        //balloon
        //skin
        try {
            String[] skin = getFromPlayer(player);
            npc.getProfile().getProperties().put("textures", new Property("textures", skin[0], skin[1]));
        }catch (NoSuchElementException ignored){

        }
        /*try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(String.format("https://api.ashcon.app/mojang/v2/user/%s", "__"+player.getName())).openConnection();
            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                ArrayList<String> lines = new ArrayList<>();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                reader.lines().forEach(lines::add);

                String reply = String.join(" ",lines);
                int indexOfValue = reply.indexOf("\"value\": \"");
                int indexOfSignature = reply.indexOf("\"signature\": \"");
                String skin = reply.substring(indexOfValue + 10, reply.indexOf("\"", indexOfValue + 10));
                String signature = reply.substring(indexOfSignature + 14, reply.indexOf("\"", indexOfSignature + 14));

                npc.fp().getProperties().put("textures", new Property("textures", skin, signature));
            }

            else {
                Bukkit.getConsoleSender().sendMessage("Connection could not be opened when fetching player skin (Response code " + connection.getResponseCode() + ", " + connection.getResponseMessage() + ")");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //skin
        // The client settings.
        DataWatcher watcher = armorStand.getDataWatcher();
        watcher.set(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte)0x20);
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(armorStand.getId(), watcher, true);
        ((CraftPlayer)player).getHandle().b.sendPacket(packet);

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
        armorStand.setPositionRotation(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), 0);
        npc.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), 0);
        //balloon
        balloon = new EntityArmorStand(EntityTypes.c, world);
        balloon.setInvulnerable(true); //invulnerable true
        balloon.setInvisible(true); //Invisible true
        EntityArmorStand entityPunch = new EntityArmorStand(EntityTypes.c, world);
        entityPunch.setInvulnerable(true);
        entityPunch.setInvisible(true);
        entityPunch.setPositionRotation(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
        leashed = new EntityBee(EntityTypes.g, world);
        ((EntityBee)leashed).setLeashHolder(npc, true);
        leashed.setInvulnerable(true);
        leashed.setInvisible(true);
        leashed.setSilent(true); //silent true
        floatLoop = true;

        //balloon
        //skin
        try {
            String[] skin = getFromPlayer(player);
            npc.getProfile().getProperties().put("textures", new Property("textures", skin[0], skin[1]));
        }catch (NoSuchElementException ignored){

        }
        /*try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(String.format("https://api.ashcon.app/mojang/v2/user/%s", "__"+player.getName())).openConnection();
            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                ArrayList<String> lines = new ArrayList<>();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                reader.lines().forEach(lines::add);

                String reply = String.join(" ",lines);
                int indexOfValue = reply.indexOf("\"value\": \"");
                int indexOfSignature = reply.indexOf("\"signature\": \"");
                String skin = reply.substring(indexOfValue + 10, reply.indexOf("\"", indexOfValue + 10));
                String signature = reply.substring(indexOfSignature + 14, reply.indexOf("\"", indexOfSignature + 14));

                npc.fp().getProperties().put("textures", new Property("textures", skin, signature));
            }

            else {
                Bukkit.getConsoleSender().sendMessage("Connection could not be opened when fetching player skin (Response code " + connection.getResponseCode() + ", " + connection.getResponseMessage() + ")");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
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
        EntityPlayer entityPlayer = ((CraftPlayer)this.entity).getHandle();
        EntityArmorStand entityArmorStand = ((CraftArmorStand)this.armorStand).getHandle();
        EntityArmorStand entityPunch = ((CraftArmorStand)this.punch).getHandle();
        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        connection.sendPacket(new PacketPlayOutEntityDestroy(entityArmorStand.getId()));
        connection.sendPacket(new PacketPlayOutEntityDestroy(entityPlayer.getId()));
        connection.sendPacket(new PacketPlayOutEntityDestroy(entityPunch.getId()));
        removeBalloon(player);
    }

    @Override
    public void removeEntity(Player player, Entity entity) {
        net.minecraft.world.entity.Entity e = ((CraftEntity)entity).getHandle();
        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        connection.sendPacket(new PacketPlayOutEntityDestroy(e.getId()));
    }

    @Override
    public void removeBalloon(Player player) {
        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        connection.sendPacket(new PacketPlayOutEntityDestroy(balloon.getId()));
        connection.sendPacket(new PacketPlayOutEntityDestroy(leashed.getId()));
    }

    @Override
    public void spawnNPC(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer)this.entity).getHandle();
        EntityArmorStand armorStand = ((CraftArmorStand)this.armorStand).getHandle();
        armorStand.setInvulnerable(true); //invulnerable true
        armorStand.setInvisible(true); //Invisible true
        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, entityPlayer));
        connection.sendPacket(new PacketPlayOutNamedEntitySpawn(entityPlayer));
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(entityPlayer, (byte) (player.getLocation().getYaw() * 256 / 360)));
        connection.sendPacket(new PacketPlayOutSpawnEntityLiving(armorStand));
        //client settings
        DataWatcher watcher = armorStand.getDataWatcher();
        watcher.set(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte)0x20);
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(armorStand.getId(), watcher, true);
        connection.sendPacket(packet);
        //
        watcher = entityPlayer.getDataWatcher();
        byte bitmask = ((CraftPlayer)player).getHandle().getDataWatcher().get(new DataWatcherObject<>(17, DataWatcherRegistry.a));
        watcher.set(new DataWatcherObject<>(17, DataWatcherRegistry.a), bitmask);
        connection.sendPacket(new PacketPlayOutEntityMetadata(entityPlayer.getId(), watcher, true));
        new BukkitRunnable() {
            @Override
            public void run() {
                connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, entityPlayer));
            }
        }.runTaskLater(MagicCosmetics.getInstance(), 20L);
        addPassenger(player);
    }

    @Override
    public void setInvisible(Player player, Entity entity, boolean invisible) {
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        net.minecraft.world.entity.Entity e = ((CraftEntity)entity).getHandle();
        e.setInvisible(invisible); //Invisible

        DataWatcher watcher = e.getDataWatcher();
        watcher.set(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte)0x20);
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(e.getId(), watcher, true);
        entityPlayer.b.sendPacket(packet);
    }

    @Override
    public void setInvisible(Entity entity, boolean invisible) {
        for(Player player : Bukkit.getOnlinePlayers()){
            EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
            net.minecraft.world.entity.Entity e = ((CraftEntity)entity).getHandle();
            e.setInvisible(invisible); //Invisible

            DataWatcher watcher = e.getDataWatcher();
            watcher.set(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte)0x20);
            PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(e.getId(), watcher, true);
            entityPlayer.b.sendPacket(packet);
        }
    }

    @Override
    public void lookNPC(Player player, float yaw, float pitch) {
        EntityPlayer entityPlayer = ((CraftPlayer)this.entity).getHandle();
        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(entityPlayer, (byte)(yaw * 256 / 360)));
        connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(entityPlayer.getId(), (byte)(yaw * 256 / 360), (byte)(pitch * 256 / 360), true));
        //connection.sendPacket();(new PacketPlayOutEntityTeleport(entityPlayer));
    }

    @Override
    public void lookNPC(Player player, float yaw) {
        EntityPlayer entityPlayer = ((CraftPlayer)this.entity).getHandle();
        EntityArmorStand armorStand = ((CraftArmorStand)this.armorStand).getHandle();
        armorStand.setInvulnerable(true); //invulnerable true
        armorStand.setInvisible(true); //Invisible true
        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(armorStand, (byte)(yaw * 256 / 360)));
        connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(armorStand.getId(), (byte)(yaw * 256 / 360), (byte)0, true));

        connection.sendPacket(new PacketPlayOutEntityHeadRotation(entityPlayer, (byte)(yaw * 256 / 360)));
        connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(entityPlayer.getId(), (byte)(yaw * 256 / 360), (byte)0, true));
        //connection.sendPacket();(new PacketPlayOutEntityTeleport(entityPlayer));
    }

    @Override
    public void armorStandSetItem(Player player, ItemStack itemStack) {
        EntityArmorStand entityPlayer = ((CraftArmorStand)this.armorStand).getHandle();
        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> list = new ArrayList<>();
        list.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(itemStack)));
        connection.sendPacket(new PacketPlayOutEntityEquipment(entityPlayer.getId(), list));
    }

    @Override
    public void ArmorStandSetHelmet(Player player, Entity entity, ItemStack itemStack) {
        if(entity == null) return;
        EntityArmorStand entityPlayer = ((CraftArmorStand)entity).getHandle();
        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> list = new ArrayList<>();
        list.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(itemStack)));
        connection.sendPacket(new PacketPlayOutEntityEquipment(entityPlayer.getId(), list));
    }

    @Override
    public void balloonSetItem(Player player, ItemStack itemStack) {
        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> list = new ArrayList<>();
        list.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(itemStack)));
        connection.sendPacket(new PacketPlayOutEntityEquipment(balloon.getId(), list));
    }

    @Override
    public void lookEntity(Player p, Entity entity, float yaw, float pitch) {
        for(Player player : Bukkit.getOnlinePlayers()){
            EntityArmorStand armorStand = ((CraftArmorStand)entity).getHandle();
            PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
            DataWatcher watcher = armorStand.getDataWatcher();
            watcher.set(new DataWatcherObject<>(16, DataWatcherRegistry.k), new Vector3f(0, 0, 0));
            PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(armorStand.getId(), watcher, true);
            connection.sendPacket(packet);
            connection.sendPacket(new PacketPlayOutEntityHeadRotation(armorStand, (byte)(yaw * 256 / 360)));
            connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(armorStand.getId(), (byte)(yaw * 256 / 360), (byte)(pitch * 256 / 360), true));
        }
    }

    @Override
    public void lookEntityPlayer(Player player, Entity entity, float quantity) {
        EntityArmorStand armorStand = ((CraftArmorStand)entity).getHandle();
        EntityPlayer p = ((CraftPlayer)player).getHandle();
        DataWatcher watcher = armorStand.getDataWatcher();
        watcher.set(new DataWatcherObject<>(16, DataWatcherRegistry.k), new Vector3f(quantity, 0, 0));
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(armorStand.getId(), watcher, true);
        p.b.sendPacket(packet);
    }

    @Override
    public void teleportEntityPlayer(Player player, Entity entity, Location location) {
        EntityArmorStand armorStand = ((CraftArmorStand)entity).getHandle();
        EntityPlayer p = ((CraftPlayer)player).getHandle();
        armorStand.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(armorStand);
        /*DataWatcher watcher = armorStand.getDataWatcher();
        watcher.set();(new DataWatcherObject<>(16, DataWatcherRegistry.k), new Vector3f(quantity, 0, 0));
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(armorStand.getId(), watcher, true);*/
        p.b.sendPacket(packet);
    }

    @Override
    public void balloonNPC(Player player, Location location, ItemStack itemStack){
        removeBalloon(player);
        //balloon
        EntityPlayer entityPlayer = ((CraftPlayer)this.entity).getHandle();
        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;

        balloon.setPositionRotation(location.getX(), location.getY()-1.2, location.getZ(), location.getYaw(), location.getPitch());

        leashed.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        //bat.e(true) //is no gravity true
        //balloon
        connection.sendPacket(new PacketPlayOutSpawnEntityLiving(balloon));
        connection.sendPacket(new PacketPlayOutSpawnEntityLiving(leashed));
        DataWatcher watcher1 = balloon.getDataWatcher();
        watcher1.set(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte)0x20);
        connection.sendPacket(new PacketPlayOutEntityMetadata(balloon.getId(), watcher1, true));
        DataWatcher watcher2 = leashed.getDataWatcher();
        watcher2.set(new DataWatcherObject<>(4, DataWatcherRegistry.i), leashed.isSilent());
        connection.sendPacket(new PacketPlayOutEntityMetadata(leashed.getId(), watcher2, true));
        connection.sendPacket(new PacketPlayOutAttachEntity(leashed, entityPlayer));
    }

    @Override
    public void equipNPC(Player player, ItemSlot itemSlot, ItemStack itemStack) {
        EntityPlayer entityPlayer = ((CraftPlayer)this.entity).getHandle();
        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> list = new ArrayList<>();
        switch (itemSlot){
            case MAIN_HAND:
                list.add(new Pair<>(EnumItemSlot.a, CraftItemStack.asNMSCopy(itemStack)));
                connection.sendPacket(new PacketPlayOutEntityEquipment(entityPlayer.getId(), list));
                break;
            case OFF_HAND:
                list.add(new Pair<>(EnumItemSlot.b, CraftItemStack.asNMSCopy(itemStack)));
                connection.sendPacket(new PacketPlayOutEntityEquipment(entityPlayer.getId(), list));
                break;
            case BOOTS:
                list.add(new Pair<>(EnumItemSlot.c, CraftItemStack.asNMSCopy(itemStack)));
                connection.sendPacket(new PacketPlayOutEntityEquipment(entityPlayer.getId(), list));
                break;
            case LEGGINGS:
                list.add(new Pair<>(EnumItemSlot.d, CraftItemStack.asNMSCopy(itemStack)));
                connection.sendPacket(new PacketPlayOutEntityEquipment(entityPlayer.getId(), list));
                break;
            case CHESTPLATE:
                list.add(new Pair<>(EnumItemSlot.e, CraftItemStack.asNMSCopy(itemStack)));
                connection.sendPacket(new PacketPlayOutEntityEquipment(entityPlayer.getId(), list));
                break;
            case HELMET:
                list.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(itemStack)));
                connection.sendPacket(new PacketPlayOutEntityEquipment(entityPlayer.getId(), list));
                break;
        }
    }

    @Override
    public Location getLocation() {
        EntityPlayer entityPlayer = ((CraftPlayer)this.entity).getHandle();
        return null;
    }

    @Override
    public void addPassenger(Player player) {
        if(entity == null) return;
        EntityArmorStand armorStand = ((CraftArmorStand)this.armorStand).getHandle();
        EntityPlayer p = ((CraftPlayer)player).getHandle();
        EntityPlayer entityPlayer = ((CraftPlayer)this.entity).getHandle();
        PacketPlayOutMount packetPlayOutMount = this.createDataSerializer(packetDataSerializer -> {
            packetDataSerializer.d(entityPlayer.getId());
            packetDataSerializer.a(new int[]{armorStand.getId()});
            return new PacketPlayOutMount(packetDataSerializer);
        });
        p.b.sendPacket(packetPlayOutMount);
    }

    @Override
    public void addPassenger(Player player, Entity entity1, Entity entity2) {
        if(entity1 == null) return;
        if(entity2 == null) return;
        net.minecraft.world.entity.Entity entity = ((CraftEntity)entity1).getHandle();
        EntityArmorStand armorStand = ((CraftArmorStand)entity2).getHandle();
        EntityPlayer p = ((CraftPlayer)player).getHandle();
        PacketPlayOutMount packetPlayOutMount = this.createDataSerializer(packetDataSerializer -> {
            packetDataSerializer.d(entity.getId());
            packetDataSerializer.a(new int[]{armorStand.getId()});
            return new PacketPlayOutMount(packetDataSerializer);
        });
        p.b.sendPacket(packetPlayOutMount);
    }

    public void addPassenger(Player player, net.minecraft.world.entity.Entity entity1, net.minecraft.world.entity.Entity entity2) {
        if(entity1 == null) return;
        if(entity2 == null) return;
        EntityPlayer p = ((CraftPlayer)player).getHandle();
        PacketPlayOutMount packetPlayOutMount = this.createDataSerializer(packetDataSerializer -> {
            packetDataSerializer.d(entity1.getId());
            packetDataSerializer.a(new int[]{entity2.getId()});
            return new PacketPlayOutMount(packetDataSerializer);
        });
        p.b.sendPacket(packetPlayOutMount);
    }

    public void animation(Player player){
        EntityPlayer p = ((CraftPlayer)player).getHandle();
        //
        Location bLocation = balloon.getBukkitEntity().getLocation();
        Location lLocation = leashed.getBukkitEntity().getLocation();
        if (!floatLoop) {
            y += 0.01;
            balloon.setPositionRotation(bLocation.getX(), bLocation.getY() + 0.01, bLocation.getZ(), bLocation.getYaw(), bLocation.getPitch());
            leashed.setPositionRotation(lLocation.getX(), lLocation.getY() + 0.01, lLocation.getZ(), lLocation.getYaw(), lLocation.getPitch());
            if (y > 0.10) floatLoop = true;
        } else {
            y -= 0.01;
            balloon.setPositionRotation(bLocation.getX(), bLocation.getY() - 0.01, bLocation.getZ(), bLocation.getYaw(), bLocation.getPitch());
            leashed.setPositionRotation(lLocation.getX(), lLocation.getY() - 0.01, lLocation.getZ(), lLocation.getYaw(), lLocation.getPitch());
            if (y < (-0.10 + 0)) floatLoop = false;
        }
        p.b.sendPacket(new PacketPlayOutEntityTeleport(leashed));
        p.b.sendPacket(new PacketPlayOutEntityTeleport(balloon));
    }

    @Override
    public void moveNPC(Player player, double x, double y, double z) {
        EntityPlayer npc = ((CraftPlayer)entity).getHandle();
        EntityPlayer p = ((CraftPlayer)player).getHandle();
        p.b.sendPacket(new PacketPlayOutEntity.PacketPlayOutRelEntityMove(npc.getId(), (short)(x * 4096), (short)(y * 4096), (short)(z * 4096), true));
    }

    @Override
    public void changeSkinNPC(Player player, String username) {
        removeNPC(player);
        EntityPlayer entityPlayer = ((CraftPlayer)this.entity).getHandle();
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(String.format("https://api.ashcon.app/mojang/v2/user/%s", username)).openConnection();
            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                ArrayList<String> lines = new ArrayList<>();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                reader.lines().forEach(lines::add);

                String reply = String.join(" ",lines);
                int indexOfValue = reply.indexOf("\"value\": \"");
                int indexOfSignature = reply.indexOf("\"signature\": \"");
                String skin = reply.substring(indexOfValue + 10, reply.indexOf("\"", indexOfValue + 10));
                String signature = reply.substring(indexOfSignature + 14, reply.indexOf("\"", indexOfSignature + 14));

                entityPlayer.getProfile().getProperties().put("textures", new Property("textures", skin, signature));
            }

            else {
                Bukkit.getConsoleSender().sendMessage("Connection could not be opened when fetching player skin (Response code " + connection.getResponseCode() + ", " + connection.getResponseMessage() + ")");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // The client settings.
        DataWatcher watcher = entityPlayer.getDataWatcher();
        watcher.set(new DataWatcherObject<>(16, DataWatcherRegistry.a), (byte)127);
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(entityPlayer.getId(), watcher, true);
        ((CraftPlayer)player).getHandle().b.sendPacket(packet);

        spawnNPC(player);

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

    public String[] getFromPlayer(Player playerBukkit) throws NoSuchElementException {
        EntityPlayer playerNMS = ((CraftPlayer) playerBukkit).getHandle();
        GameProfile profile = playerNMS.getProfile();

        Property property = profile.getProperties().get("textures").iterator().next();
        String texture = property.getValue();
        String signature = property.getSignature();
        return new String[] {texture, signature};
        //CustomCosmetics.getInstance().getLogger().warning("NPC Skin: Player " + playerBukkit.getName() + " not have skin!");
    }

    public String[] getFromName(String name) {
        try {
            URL url_0 = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());
            String uuid = new JsonParser().parse(reader_0).getAsJsonObject().get("id").getAsString();

            URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
            JsonObject textureProperty = new JsonParser().parse(reader_1).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
            String texture = textureProperty.get("value").getAsString();
            String signature = textureProperty.get("signature").getAsString();

            return new String[] {texture, signature};
        } catch (Exception e) {
            MagicCosmetics.getInstance().getLogger().severe("Could not get skin data from session servers!");
            MagicCosmetics.getInstance().getLogger().severe("parsing to player skin..");
            return null;
        }
    }
}
