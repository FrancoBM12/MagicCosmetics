package com.francobm.customcosmetics.commands;

import com.francobm.customcosmetics.CustomCosmetics;
import com.francobm.customcosmetics.cache.*;
import com.francobm.customcosmetics.cache.inventories.Menu;
import com.francobm.customcosmetics.cache.items.Items;
import com.francobm.customcosmetics.files.FileCreator;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

public class Command implements CommandExecutor {
    private final CustomCosmetics plugin = CustomCosmetics.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        FileCreator messages = plugin.getMessages();
        if(sender instanceof Player){
            Player player = (Player) sender;
            if(args.length > 0){
                switch (args[0].toLowerCase()){
                    case "test":
                        ArmorStand armorStand = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
                        player.addPassenger(armorStand);
                        return true;
                    case "unlock":
                        if(args.length < 2){
                            return true;
                        }
                        Player p = Bukkit.getPlayer(args[1]);
                        if(p == null) return true;
                        PlayerCache playerCache = PlayerCache.getPlayer(p);
                        playerCache.setZone(false);
                        return true;
                    case "add":
                        //cosmetics add <player> <id>
                        if(args.length < 3){
                            player.sendMessage(plugin.prefix + messages.getString("commands.add-usage"));
                            return true;
                        }
                        Player target = Bukkit.getPlayer(args[1]);
                        if(target == null){
                            player.sendMessage(plugin.prefix + messages.getString("offline-player"));
                            return true;
                        }
                        plugin.getCosmeticsManager().addCosmetic(player, target, args[2]);
                        return true;
                    case "reload":
                        if(!player.hasPermission("customcosmetics.admin")){
                            player.sendMessage(plugin.prefix + messages.getString("no-permission"));
                            return true;
                        }
                        plugin.getCosmeticsManager().cancelTasks();
                        plugin.getConfig().reload();
                        plugin.getCosmetics().reload();
                        plugin.getMessages().reload();
                        plugin.getSounds().reload();
                        plugin.getMenus().reload();
                        plugin.getTokens().reload();
                        plugin.getZones().reload();
                        for(BossBar bar : plugin.getBossBar()){
                            bar.removeAll();
                        }
                        plugin.getBossBar().clear();
                        for(String lines : messages.getStringList("bossbar")){
                            BossBar boss = plugin.getServer().createBossBar(lines, BarColor.YELLOW, BarStyle.SOLID);
                            boss.setVisible(true);
                            plugin.getBossBar().add(boss);
                        }
                        plugin.prefix = messages.getString("prefix");
                        Cosmetic.loadCosmetics();
                        Color.loadColors();
                        Items.loadItems();
                        Token.loadTokens();
                        Sound.loadSounds();
                        Menu.loadMenus();
                        Zone.loadZones();
                        PlayerCache.reload();
                        plugin.getCosmeticsManager().runTasks();
                        player.sendMessage(plugin.prefix + messages.getString("reload"));
                        return true;
                    case "use":
                        //cosmetics use <id>
                        if(args.length < 2){
                            player.sendMessage(plugin.prefix + messages.getString("commands.use-usage"));
                            return true;
                        }
                        plugin.getCosmeticsManager().useCosmetic(player, args[1]);
                        return true;
                    case "preview":
                        if(args.length < 2){
                            player.sendMessage(plugin.prefix + messages.getString("commands.use-usage"));
                            return true;
                        }
                        plugin.getCosmeticsManager().previewCosmetic(player, args[1]);
                        return true;
                    case "unuse":
                        if(args.length < 2){
                            player.sendMessage(plugin.prefix + messages.getString("commands.use-usage"));
                            return true;
                        }
                        plugin.getCosmeticsManager().unUseCosmetic(player, args[1]);
                        return true;
                    case "unset":
                        if(args.length < 2){
                            player.sendMessage(plugin.prefix + messages.getString("commands.unuse-usage"));
                            return true;
                        }
                        plugin.getCosmeticsManager().unSetCosmetic(player, args[1]);
                        return true;
                    case "open":
                        //cosmetics open <menu-id>
                        if(args.length < 2){
                            player.sendMessage(plugin.prefix + messages.getString("commands.menu-usage"));
                            return true;
                        }
                        plugin.getCosmeticsManager().openMenu(player, args[1]);
                        return true;
                    case "spec":
                        plugin.getVersion().setSpectator(player);
                        return true;
                    case "spawn":
                        if(plugin.getVersion().getNPC(player) == null){
                            plugin.getVersion().createNPC(player);
                            return true;
                        }
                        plugin.getVersion().removeNPC(player);
                        return true;
                    case "zones":
                        //cosmetics zones add <name>
                        if(args.length < 2){
                            for(String msg : plugin.getMessages().getStringList("commands.zones-usage")){
                                player.sendMessage(msg);
                            }
                            return true;
                        }
                        if(args[1].equalsIgnoreCase("add")){
                            if(args.length < 3){
                                for(String msg : plugin.getMessages().getStringList("commands.zones-usage")){
                                    player.sendMessage(msg);
                                }
                                return true;
                            }
                            plugin.getCosmeticsManager().addZone(player, args[2]);
                            return true;
                        }
                        if(args[1].equalsIgnoreCase("setnpc")){
                            if(args.length < 3){
                                for(String msg : plugin.getMessages().getStringList("commands.zones-usage")){
                                    player.sendMessage(msg);
                                }
                                return true;
                            }
                            plugin.getCosmeticsManager().setZoneNPC(player, args[2]);
                            return true;
                        }
                        if(args[1].equalsIgnoreCase("setballoon")){
                            if(args.length < 3){
                                for(String msg : plugin.getMessages().getStringList("commands.zones-usage")){
                                    player.sendMessage(msg);
                                }
                                return true;
                            }
                            plugin.getCosmeticsManager().setBalloonNPC(player, args[2]);
                            return true;
                        }
                        if(args[1].equalsIgnoreCase("setenter")){
                            if(args.length < 3){
                                for(String msg : plugin.getMessages().getStringList("commands.zones-usage")){
                                    player.sendMessage(msg);
                                }
                                return true;
                            }
                            plugin.getCosmeticsManager().setZoneEnter(player, args[2]);
                            return true;
                        }
                        if(args[1].equalsIgnoreCase("setexit")){
                            if(args.length < 3){
                                for(String msg : plugin.getMessages().getStringList("commands.zones-usage")){
                                    player.sendMessage(msg);
                                }
                                return true;
                            }
                            plugin.getCosmeticsManager().setZoneExit(player, args[2]);
                            return true;
                        }
                        if(args[1].equalsIgnoreCase("givecorns")){
                            if(args.length < 3){
                                for(String msg : plugin.getMessages().getStringList("commands.zones-usage")){
                                    player.sendMessage(msg);
                                }
                                return true;
                            }
                            plugin.getCosmeticsManager().giveCorn(player, args[2]);
                            return true;
                        }
                        if(args[1].equalsIgnoreCase("enable")){
                            if(args.length < 3){
                                for(String msg : plugin.getMessages().getStringList("commands.zones-usage")){
                                    player.sendMessage(msg);
                                }
                                return true;
                            }
                            plugin.getCosmeticsManager().enableZone(player, args[2]);
                            return true;
                        }
                        if(args[1].equalsIgnoreCase("disable")){
                            if(args.length < 3){
                                for(String msg : plugin.getMessages().getStringList("commands.zones-usage")){
                                    player.sendMessage(msg);
                                }
                                return true;
                            }
                            plugin.getCosmeticsManager().disableZone(player, args[2]);
                            return true;
                        }
                        if(args[1].equalsIgnoreCase("save")){
                            if(args.length < 3){
                                for(String msg : plugin.getMessages().getStringList("commands.zones-usage")){
                                    player.sendMessage(msg);
                                }
                                return true;
                            }
                            plugin.getCosmeticsManager().saveZone(player, args[2]);
                            return true;
                        }
                        return true;
                    case "token":
                        //cosmetics token give <player> <name>
                        if(args.length < 3){
                            player.sendMessage(plugin.prefix + plugin.getMessages().getString("commands.token-usage"));
                            return true;
                        }
                        if(args[1].equalsIgnoreCase("give")){
                            Player t = Bukkit.getPlayer(args[2]);
                            plugin.getCosmeticsManager().giveToken(player, t, args[3]);
                            return true;
                        }
                        return true;
                }
            }
            return true;
        }
        return true;
    }
}
