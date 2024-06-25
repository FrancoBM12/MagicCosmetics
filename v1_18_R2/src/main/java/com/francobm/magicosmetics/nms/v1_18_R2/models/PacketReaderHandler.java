package com.francobm.magicosmetics.nms.v1_18_R2.models;

import com.francobm.magicosmetics.models.PacketReader;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketReaderHandler extends PacketReader{

    public void injectPlayer(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        MCChannelHandler cdh = new MCChannelHandler(entityPlayer);
        ChannelPipeline pipeline = entityPlayer.b.a.m.pipeline();
        for (String name : pipeline.toMap().keySet()) {
            if (pipeline.get(name) instanceof NetworkManager) {
                pipeline.addBefore(name, "magic_cosmetics_packet_handler", cdh);
                break;
            }
        }
    }

    public void removePlayer(Player player) {
        Channel channel = ((CraftPlayer)player).getHandle().b.a.m;
        if(channel == null) return;
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove("magic_cosmetics_packet_handler");
        });
    }
}
