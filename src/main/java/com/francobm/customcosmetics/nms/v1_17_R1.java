package com.francobm.customcosmetics.nms;

import com.francobm.customcosmetics.cache.Sound;
import com.francobm.customcosmetics.nms.NPC.NPC;
import com.google.common.collect.Lists;
import net.minecraft.network.protocol.game.PacketPlayOutGameStateChange;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class v1_17_R1 extends Version{
    @Override
    public void setSpectator(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
        EntityPlayer p = ((CraftPlayer)player).getHandle();
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.b, p);
        try {
            Field packetField = packet.getClass().getDeclaredField("b");
            packetField.setAccessible(true);
            ArrayList<PacketPlayOutPlayerInfo.PlayerInfoData> list = Lists.newArrayList();
            list.add(new PacketPlayOutPlayerInfo.PlayerInfoData(p.getProfile(), 0, net.minecraft.world.level.EnumGamemode.b, p.listName));
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
        NPC npc = new com.francobm.customcosmetics.nms.NPC.v1_17_R1();
        npc.addNPC(player);
        npc.spawnNPC(player);
    }

    @Override
    public void createNPC(Player player, Location location) {
        NPC npc = new com.francobm.customcosmetics.nms.NPC.v1_17_R1();
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
        return new com.francobm.customcosmetics.nms.NPC.v1_17_R1();
    }

    @Override
    public void sendSound(Player player, Sound sound) {
        if(player == null) return;
        if(sound == null) return;

        if(sound.isCustom()) {
            player.playSound(player.getLocation(), sound.getSoundCustom(), sound.getYaw(), sound.getPitch());
            return;
        }
        player.playSound(player.getLocation(), sound.getSoundBukkit(), sound.getYaw(), sound.getPitch());
    }
}
