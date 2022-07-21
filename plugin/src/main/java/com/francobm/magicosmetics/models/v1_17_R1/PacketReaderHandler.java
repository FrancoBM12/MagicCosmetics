package com.francobm.magicosmetics.models.v1_17_R1;

import com.francobm.magicosmetics.events.Action;
import com.francobm.magicosmetics.events.UnknownEntityInteractEvent;
import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.models.PacketReader;
import com.francobm.magicosmetics.nms.NPC.NPC;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.network.protocol.game.PacketPlayInUseEntity;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class PacketReaderHandler extends PacketReader {

    protected final Player player;
    protected int count = 0;

    public PacketReaderHandler(Player player){
        this.player = player;
    }

    public boolean inject() {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        Channel channel = craftPlayer.getHandle().b.a.k;

        if(channel.pipeline().get("PIMagicCosmetics") != null) return false;
        channel.pipeline().addAfter("decoder", "PIMagicCosmetics", new MessageToMessageDecoder<PacketPlayInUseEntity>() {
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
        channel.pipeline().remove("PIMagicCosmetics");
    }

    private void read(PacketPlayInUseEntity packetPlayInUseEntity) {
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        int entityID = (int) getValue(packetPlayInUseEntity, "a");
        Object object = getValue(packetPlayInUseEntity, "b");
        if (object.toString().split("\\$")[1].charAt(0) == '1'){
            // call event
            NPC npc = plugin.getVersion().getNPC(player);
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
            NPC npc = plugin.getVersion().getNPC(player);
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
