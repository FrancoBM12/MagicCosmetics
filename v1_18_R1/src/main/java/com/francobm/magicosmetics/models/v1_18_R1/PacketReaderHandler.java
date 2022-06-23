package com.francobm.magicosmetics.models.v1_18_R1;

import com.francobm.magicosmetics.events.Action;
import com.francobm.magicosmetics.events.UnknownEntityInteractEvent;
import com.francobm.magicosmetics.models.PacketReader;
import com.francobm.magicosmetics.nms.NPC.NPC;
import com.francobm.magicosmetics.nms.Version.Version;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.network.protocol.game.PacketPlayInUseEntity;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class PacketReaderHandler extends PacketReader{

    protected final Player player;
    protected int count = 0;

    public PacketReaderHandler(Player player){
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
        Version version = Version.getVersion();
        if(version == null) return;
        Plugin plugin = Bukkit.getPluginManager().getPlugin("MagicCosmetics");
        if(plugin == null) return;
        Object object = getValue(packetPlayInUseEntity, "b");
        if (object.toString().split("\\$")[1].charAt(0) == '1'){
            // call event
            NPC npc = version.getNPC(player);
            if(npc == null) return;
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
            NPC npc = version.getNPC(player);
            if(npc == null) return;
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getPluginManager().callEvent(new UnknownEntityInteractEvent(player, npc, Action.INTERACT));
                }
            }.runTask(plugin);
        }
    }
}
