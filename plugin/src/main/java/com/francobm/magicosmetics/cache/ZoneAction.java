package com.francobm.magicosmetics.cache;

import com.francobm.magicosmetics.MagicCosmetics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class ZoneAction {
    private final MagicCosmetics plugin = MagicCosmetics.getInstance();
    private final String id;
    private List<String> commands;

    public ZoneAction(String id, List<String> commands) {
        this.id = id;
        this.commands = commands;
    }

    public String getId() {
        return id;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public void executeCommands(Player player, String zoneId) {
        for(String command : commands){
            if(plugin.isPlaceholderAPI())
                command = plugin.getPlaceholderAPI().setPlaceholders(player, command.replace("%player%", player.getName()).replace("%zone%", zoneId));
            else
                command = command.replace("%player%", player.getName()).replace("%zone%", zoneId);
            if(command.startsWith("[console] ")){
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("[console] ", ""));
                continue;
            }
            if(command.startsWith("[player] ")) {
                player.performCommand(command.replace("[player] ", ""));
                continue;
            }
            player.performCommand(command);
        }
    }
}
