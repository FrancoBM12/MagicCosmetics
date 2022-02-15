package com.francobm.customcosmetics.nms.NPC;

import com.francobm.customcosmetics.CustomCosmetics;
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
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
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

public class V1_18_R1 extends NPC{

    private EntityArmorStand balloon;
    private EntityLiving leashed;

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
        leashed = new EntityBat(EntityTypes.f, world);
        ((EntityBat)leashed).b(npc, true);
        leashed.m(true);
        leashed.j(true);
        leashed.d(true); //silent true
        //balloon
        //skin
        String[] skin = getFromPlayer(player);
        if(skin != null){
            npc.fp().getProperties().put("textures", new Property("textures", skin[0], skin[1]));
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
        DataWatcher watcher = armorStand.ai();
        watcher.b(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte)0x20);
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(armorStand.ae(), watcher, true);
        ((CraftPlayer)player).getHandle().b.a(packet);

        //
        this.entity = npc.getBukkitEntity();
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
        leashed = new EntityBat(EntityTypes.f, world);
        ((EntityBat)leashed).b(npc, true);
        leashed.m(true);
        leashed.j(true);
        leashed.d(true); //silent true
        //balloon
        //skin
        String[] skin = getFromPlayer(player);
        if(skin != null){
            npc.fp().getProperties().put("textures", new Property("textures", skin[0], skin[1]));
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
        this.armorStand = armorStand.getBukkitEntity();

        addNPC(this, player);
    }

    @Override
    public void removeNPC(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer)this.entity).getHandle();
        EntityArmorStand entityArmorStand = ((CraftArmorStand)this.armorStand).getHandle();
        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        connection.a(new PacketPlayOutEntityDestroy(entityArmorStand.ae()));
        connection.a(new PacketPlayOutEntityDestroy(entityPlayer.ae()));
        removeBalloon(player);
    }

    @Override
    public void removeEntity(Player player, Entity entity) {
        net.minecraft.world.entity.Entity e = ((CraftEntity)entity).getHandle();
        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        connection.a(new PacketPlayOutEntityDestroy(e.ae()));
    }

    @Override
    public void removeBalloon(Player player) {
        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        connection.a(new PacketPlayOutEntityDestroy(balloon.ae()));
        connection.a(new PacketPlayOutEntityDestroy(leashed.ae()));
    }

    @Override
    public void spawnNPC(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer)this.entity).getHandle();
        EntityArmorStand armorStand = ((CraftArmorStand)this.armorStand).getHandle();
        armorStand.m(true); //invulnerable true
        armorStand.j(true); //Invisible true
        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        connection.a(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, entityPlayer));
        connection.a(new PacketPlayOutNamedEntitySpawn(entityPlayer));
        connection.a(new PacketPlayOutEntityHeadRotation(entityPlayer, (byte) (player.getLocation().getYaw() * 256 / 360)));
        connection.a(new PacketPlayOutSpawnEntityLiving(armorStand));
        //client settings
        DataWatcher watcher = armorStand.ai();
        watcher.b(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte)0x20);
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(armorStand.ae(), watcher, true);
        connection.a(packet);
        //
        new BukkitRunnable() {
            @Override
            public void run() {
                connection.a(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, entityPlayer));
            }
        }.runTaskLater(CustomCosmetics.getInstance(), 20L);
        addPassenger(player);
    }

    @Override
    public void setInvisible(Player player, Entity entity, boolean invisible) {
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        net.minecraft.world.entity.Entity e = ((CraftEntity)entity).getHandle();
        e.j(invisible); //Invisible

        DataWatcher watcher = e.ai();
        watcher.b(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte)0x20);
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(e.ae(), watcher, true);
        entityPlayer.b.a(packet);
    }

    @Override
    public void setInvisible(Entity entity, boolean invisible) {
        for(Player player : Bukkit.getOnlinePlayers()){
            EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
            net.minecraft.world.entity.Entity e = ((CraftEntity)entity).getHandle();
            e.j(invisible); //Invisible

            DataWatcher watcher = e.ai();
            watcher.b(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte)0x20);
            PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(e.ae(), watcher, true);
            entityPlayer.b.a(packet);
        }
    }

    @Override
    public void lookNPC(Player player, float yaw, float pitch) {
        EntityPlayer entityPlayer = ((CraftPlayer)this.entity).getHandle();
        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        connection.a(new PacketPlayOutEntityHeadRotation(entityPlayer, (byte)(yaw * 256 / 360)));
        connection.a(new PacketPlayOutEntity.PacketPlayOutEntityLook(entityPlayer.ae(), (byte)(yaw * 256 / 360), (byte)(pitch * 256 / 360), true));
        //connection.a(new PacketPlayOutEntityTeleport(entityPlayer));
    }

    @Override
    public void lookNPC(Player player, float yaw) {
        EntityPlayer entityPlayer = ((CraftPlayer)this.entity).getHandle();
        EntityArmorStand armorStand = ((CraftArmorStand)this.armorStand).getHandle();
        armorStand.m(true); //invulnerable true
        armorStand.j(true); //Invisible true
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
    }

    @Override
    public void ArmorStandSetHelmet(Player player, Entity entity, ItemStack itemStack) {
        if(entity == null) return;
        EntityArmorStand entityPlayer = ((CraftArmorStand)entity).getHandle();
        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> list = new ArrayList<>();
        list.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(itemStack)));
        connection.a(new PacketPlayOutEntityEquipment(entityPlayer.ae(), list));
    }

    @Override
    public void balloonSetItem(Player player, ItemStack itemStack) {
        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
        ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> list = new ArrayList<>();
        list.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(itemStack)));
        connection.a(new PacketPlayOutEntityEquipment(balloon.ae(), list));
    }

    @Override
    public void lookEntity(Player p, Entity entity, float yaw, float pitch) {
        for(Player player : Bukkit.getOnlinePlayers()){
            EntityArmorStand armorStand = ((CraftArmorStand)entity).getHandle();
            PlayerConnection connection = ((CraftPlayer)player).getHandle().b;
            DataWatcher watcher = armorStand.ai();
            watcher.b(new DataWatcherObject<>(16, DataWatcherRegistry.k), new Vector3f(0, 0, 0));
            PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(armorStand.ae(), watcher, true);
            connection.a(packet);
            connection.a(new PacketPlayOutEntityHeadRotation(armorStand, (byte)(yaw * 256 / 360)));
            connection.a(new PacketPlayOutEntity.PacketPlayOutEntityLook(armorStand.ae(), (byte)(yaw * 256 / 360), (byte)(pitch * 256 / 360), true));
        }
    }

    @Override
    public void lookEntityPlayer(Player player, Entity entity, float quantity) {
        EntityArmorStand armorStand = ((CraftArmorStand)entity).getHandle();
        EntityPlayer p = ((CraftPlayer)player).getHandle();
        DataWatcher watcher = armorStand.ai();
        watcher.b(new DataWatcherObject<>(16, DataWatcherRegistry.k), new Vector3f(quantity, 0, 0));
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(armorStand.ae(), watcher, true);
        p.b.a(packet);
    }

    @Override
    public void teleportEntityPlayer(Player player, Entity entity, Location location) {
        EntityArmorStand armorStand = ((CraftArmorStand)entity).getHandle();
        EntityPlayer p = ((CraftPlayer)player).getHandle();
        armorStand.b(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(armorStand);
        /*DataWatcher watcher = armorStand.ai();
        watcher.b(new DataWatcherObject<>(16, DataWatcherRegistry.k), new Vector3f(quantity, 0, 0));
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(armorStand.ae(), watcher, true);*/
        p.b.a(packet);
    }

    @Override
    public void balloonNPC(Player player, Location location, ItemStack itemStack){
        removeBalloon(player);
        //balloon
        EntityPlayer entityPlayer = ((CraftPlayer)this.entity).getHandle();
        PlayerConnection connection = ((CraftPlayer)player).getHandle().b;

        balloon.b(location.getX(), location.getY()-1.2, location.getZ(), location.getYaw(), location.getPitch());

        leashed.b(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        //bat.e(true) //is no gravity true
        //balloon
        connection.a(new PacketPlayOutSpawnEntityLiving(balloon));
        connection.a(new PacketPlayOutSpawnEntityLiving(leashed));
        DataWatcher watcher1 = balloon.ai();
        watcher1.b(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte)0x20);
        connection.a(new PacketPlayOutEntityMetadata(balloon.ae(), watcher1, true));
        DataWatcher watcher2 = leashed.ai();
        watcher2.b(new DataWatcherObject<>(4, DataWatcherRegistry.i), leashed.aL());
        connection.a(new PacketPlayOutEntityMetadata(leashed.ae(), watcher2, true));
        connection.a(new PacketPlayOutAttachEntity(leashed, entityPlayer));
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
           packetDataSerializer.d(entityPlayer.ae());
           packetDataSerializer.a(new int[]{armorStand.ae()});
           return new PacketPlayOutMount(packetDataSerializer);
        });
        p.b.a(packetPlayOutMount);
    }

    @Override
    public void addPassenger(Player player, Entity entity1, Entity entity2) {
        if(entity1 == null) return;
        if(entity2 == null) return;
        net.minecraft.world.entity.Entity entity = ((CraftEntity)entity1).getHandle();
        EntityArmorStand armorStand = ((CraftArmorStand)entity2).getHandle();
        EntityPlayer p = ((CraftPlayer)player).getHandle();
        PacketPlayOutMount packetPlayOutMount = this.createDataSerializer(packetDataSerializer -> {
            packetDataSerializer.d(entity.ae());
            packetDataSerializer.a(new int[]{armorStand.ae()});
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

    private boolean status = true;
    public void animation(Player player){
        EntityPlayer p = ((CraftPlayer)player).getHandle();
        short y = (short) (0.5D + (status ? 0.1D : -0.1D));
        status = !status;
        p.b.a(new PacketPlayOutEntity.PacketPlayOutRelEntityMove(balloon.ae(), (short)0, (short) (y * 4096), (short)0, false));
        CustomCosmetics.getInstance().getLogger().info("Y: " + y + " Status:" + status);
    }

    @Override
    public void moveNPC(Player player, double x, double y, double z) {
        EntityPlayer npc = ((CraftPlayer)entity).getHandle();
        EntityPlayer p = ((CraftPlayer)player).getHandle();
        p.b.a(new PacketPlayOutEntity.PacketPlayOutRelEntityMove(npc.ae(), (short)(x * 4096), (short)(y * 4096), (short)(z * 4096), true));
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

                entityPlayer.fp().getProperties().put("textures", new Property("textures", skin, signature));
            }

            else {
                Bukkit.getConsoleSender().sendMessage("Connection could not be opened when fetching player skin (Response code " + connection.getResponseCode() + ", " + connection.getResponseMessage() + ")");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // The client settings.
        DataWatcher watcher = entityPlayer.ai();
        watcher.b(new DataWatcherObject<>(16, DataWatcherRegistry.a), (byte)127);
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(entityPlayer.ae(), watcher, true);
        ((CraftPlayer)player).getHandle().b.a(packet);

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

    public String[] getFromPlayer(Player playerBukkit) {
        EntityPlayer playerNMS = ((CraftPlayer) playerBukkit).getHandle();
        GameProfile profile = playerNMS.fp();
        try {
            Property property = profile.getProperties().get("textures").iterator().next();
            String texture = property.getValue();
            String signature = property.getSignature();
            return new String[] {texture, signature};
        }catch (NoSuchElementException exception){
            CustomCosmetics.getInstance().getLogger().warning("NPC Skin: Player " + playerBukkit.getName() + " not have skin!");
        }
        return null;
    }

    public String[] getFromName(String name) {
        try {
            URL url_0 = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());
            String uuid = JsonParser.parseReader(reader_0).getAsJsonObject().get("id").getAsString();

            URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
            JsonObject textureProperty = JsonParser.parseReader(reader_1).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
            String texture = textureProperty.get("value").getAsString();
            String signature = textureProperty.get("signature").getAsString();

            return new String[] {texture, signature};
        } catch (Exception e) {
            CustomCosmetics.getInstance().getLogger().severe("Could not get skin data from session servers!");
            CustomCosmetics.getInstance().getLogger().severe("parsing to player skin..");
            return null;
        }
    }
}
