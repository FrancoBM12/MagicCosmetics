package com.francobm.customcosmetics.models;

import com.francobm.customcosmetics.CustomCosmetics;
import com.francobm.customcosmetics.events.Action;
import com.francobm.customcosmetics.events.UnknownEntityInteractEvent;
import com.francobm.customcosmetics.nms.NPC.NPC;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.network.protocol.game.PacketPlayInUseEntity;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.List;

public class PacketReader {

    protected final Player player;
    protected int count = 0;

    public PacketReader(Player player){
        this.player = player;
    }

    public boolean inject() {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        Channel channel = craftPlayer.getHandle().b.a.k;

        if(channel.pipeline().get("PacketInjector") != null) return false;

        channel.pipeline().addAfter("decoder", "PacketInjector", new MessageToMessageDecoder<PacketPlayInUseEntity>() {
            @Override
            protected void decode(ChannelHandlerContext ctx, PacketPlayInUseEntity msg, List<Object> out) throws Exception {
                out.add(msg);
                read(msg);
            }
        });
        return true;
    }

    public void unject() {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        Channel channel = craftPlayer.getHandle().b.a.k;
        channel.pipeline().remove("PacketInjector");
    }

    private void read(PacketPlayInUseEntity packetPlayInUseEntity) {
        CustomCosmetics plugin = CustomCosmetics.getInstance();
        int entityID = (int) getValue(packetPlayInUseEntity, "a");
        Object object = getValue(packetPlayInUseEntity, "b");
        if (object.toString().split("\\$")[1].charAt(0) == '1'){
            // call event
            NPC npc = plugin.getVersion().getNPC(player);
            if(npc == null) return;
            if((npc.getEntity().getEntityId() != entityID)) return;
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getPluginManager().callEvent(new UnknownEntityInteractEvent(player, npc, Action.ATTACK));
                }
            }.runTask(plugin);
            return;
        }
        count++;
        if(count == 4){
            // call event
            NPC npc = plugin.getVersion().getNPC(player);
            if(npc == null) return;
            if((npc.getEntity().getEntityId() != entityID)) return;
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getPluginManager().callEvent(new UnknownEntityInteractEvent(player, npc, Action.INTERACT));
                }
            }.runTask(plugin);
        }
    }

    protected Object getValue(Object instance, String name){
        Object result = null;
        try {
            Field field = instance.getClass().getDeclaredField(name);
            field.setAccessible(true);
            result = field.get(instance);
            field.setAccessible(false);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return result;
    }
}
