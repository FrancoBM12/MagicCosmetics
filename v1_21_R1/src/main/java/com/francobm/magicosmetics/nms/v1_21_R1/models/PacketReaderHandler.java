package com.francobm.magicosmetics.nms.v1_21_R1.models;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.models.PacketReader;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PacketReaderHandler extends PacketReader{

    public void injectPlayer(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        MCChannelHandler cdh = new MCChannelHandler(entityPlayer);
        ChannelPipeline pipeline = getPrivateChannelPipeline(entityPlayer.c);
        if(pipeline == null) return;
        for (String name : pipeline.toMap().keySet()) {
            if (pipeline.get(name) instanceof NetworkManager) {
                pipeline.addBefore(name, "magic_cosmetics_packet_handler", cdh);
                break;
            }
        }
    }

    public void removePlayer(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        Channel channel = getPrivateChannel(craftPlayer.getHandle().c);
        if(channel == null) return;
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove("magic_cosmetics_packet_handler");
        });
    }

    private ChannelPipeline getPrivateChannelPipeline(PlayerConnection playerConnection) {
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        if(plugin.getServer().getPluginManager().isPluginEnabled("Denizen")){
            String className = "com.denizenscript.denizen.nms.v1_20.impl.network.handlers.DenizenNetworkManagerImpl";
            String methodName = "getConnection";
            try {
                Class<?> clazz = Class.forName(className);
                Class<?>[] typeParameters = { EntityPlayer.class };
                Method method = clazz.getMethod(methodName, typeParameters);
                Object[] parameters = { playerConnection.f };
                NetworkManager result = (NetworkManager) method.invoke(null, parameters);
                return result.n.pipeline();
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                     IllegalAccessException exception) {
                throw new RuntimeException(exception);
            }
        }
        try {
            Field privateNetworkManager = ServerCommonPacketListenerImpl.class.getDeclaredField("e");
            privateNetworkManager.setAccessible(true);
            NetworkManager networkManager = (NetworkManager) privateNetworkManager.get(playerConnection);
            return networkManager.n.pipeline();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Bukkit.getLogger().severe("Error: Channel Pipeline not found");
            return null;
        }
    }

    private Channel getPrivateChannel(PlayerConnection playerConnection) {
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        if(plugin.getServer().getPluginManager().isPluginEnabled("Denizen")){
            String className = "com.denizenscript.denizen.nms.v1_20.impl.network.handlers.DenizenNetworkManagerImpl";
            String methodName = "getConnection";
            try {
                Class<?> clazz = Class.forName(className);
                Class<?>[] typeParameters = { EntityPlayer.class };
                Method method = clazz.getMethod(methodName, typeParameters);
                Object[] parameters = { playerConnection.f };
                NetworkManager result = (NetworkManager) method.invoke(null, parameters);
                return result.n;
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                     IllegalAccessException exception) {
                throw new RuntimeException(exception);
            }
        }
        try {
            Field privateNetworkManager = ServerCommonPacketListenerImpl.class.getDeclaredField("e");
            privateNetworkManager.setAccessible(true);
            NetworkManager networkManager = (NetworkManager) privateNetworkManager.get(playerConnection);
            return networkManager.n;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Bukkit.getLogger().severe("Error: Channel not found");
            return null;
        }
    }
}
