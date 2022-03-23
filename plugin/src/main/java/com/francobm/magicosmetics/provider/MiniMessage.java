package com.francobm.magicosmetics.provider;

import com.francobm.magicosmetics.MagicCosmetics;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.time.Duration;

public class MiniMessage {

    private final BukkitAudiences bukkitAudiences;

    public MiniMessage(){
        bukkitAudiences = BukkitAudiences.create(MagicCosmetics.getInstance());
    }

    public void sendMessage(Player player, String message){
        Audience audience = bukkitAudiences.player(player);

        net.kyori.adventure.text.minimessage.MiniMessage miniMessage = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage();
        Component component = miniMessage.deserialize(message);
        audience.sendMessage(component);
    }

    /*public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut){
        Audience audience = bukkitAudiences.player(player);
        net.kyori.adventure.text.minimessage.MiniMessage miniMessage = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage();
        Component titleComponent = miniMessage.deserialize(title);
        Component subTitleComponent = subtitle.isEmpty() ? Component.empty() : miniMessage.deserialize(subtitle);
        audience.showTitle(Title.title(titleComponent, subTitleComponent, Title.Times.times(Duration.ofMillis(fadeIn), Duration.ofMillis(stay), Duration.ofMillis(fadeOut))));
    }*/
}
