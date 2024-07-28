package com.francobm.magicosmetics.commands;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.api.Cosmetic;
import com.francobm.magicosmetics.api.CosmeticType;
import com.francobm.magicosmetics.cache.*;
import com.francobm.magicosmetics.cache.inventories.Menu;
import com.francobm.magicosmetics.files.FileCreator;
import com.francobm.magicosmetics.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Command implements CommandExecutor, TabCompleter {
    private final MagicCosmetics plugin = MagicCosmetics.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        FileCreator messages = plugin.getMessages();
        if(sender instanceof ConsoleCommandSender){
            if(args.length >= 1){
                Player target;
                switch (args[0].toLowerCase()){
                    case "addall":
                        if(args.length < 2){
                            Utils.sendMessage(sender,plugin.prefix + messages.getString("commands.add-all-usage"));
                            return true;
                        }
                        target = Bukkit.getPlayer(args[1]);
                        if(target == null){
                            Utils.sendMessage(sender,plugin.prefix + messages.getString("offline-player"));
                            return true;
                        }
                        plugin.getCosmeticsManager().addAllCosmetics(sender, target);
                        return true;
                    case "add":
                        //cosmetics add <player> <id>
                        if(args.length < 3){

                            Utils.sendMessage(sender,plugin.prefix + messages.getString("commands.add-usage"));
                            return true;
                        }
                        target = Bukkit.getPlayer(args[1]);
                        if(target == null){
                            Utils.sendMessage(sender,plugin.prefix + messages.getString("offline-player"));
                            return true;
                        }
                        plugin.getCosmeticsManager().addCosmetic(sender, target, args[2]);
                        return true;
                    case "remove":
                        //cosmetics add <player> <id>
                        if(args.length < 3){
                            Utils.sendMessage(sender,plugin.prefix + messages.getString("commands.remove-usage"));
                            return true;
                        }
                        target = Bukkit.getPlayer(args[1]);
                        if(target == null){
                            Utils.sendMessage(sender,plugin.prefix + messages.getString("offline-player"));
                            return true;
                        }
                        plugin.getCosmeticsManager().removeCosmetic(sender, target, args[2]);
                        return true;
                    case "removeall":
                        if(args.length < 2){
                            Utils.sendMessage(sender,plugin.prefix + messages.getString("commands.remove-all-usage"));
                            return true;
                        }
                        target = Bukkit.getPlayer(args[1]);
                        if(target == null){
                            Utils.sendMessage(sender,plugin.prefix + messages.getString("offline-player"));
                            return true;
                        }
                        plugin.getCosmeticsManager().removeAllCosmetics(sender, target);
                        return true;
                    case "reload":
                        plugin.getCosmeticsManager().reload(sender);
                        return true;
                    case "equip":
                        //cosmetics equip <player> <id> <color> <force>
                        if(args.length < 3){
                            Utils.sendMessage(sender,plugin.prefix + messages.getString("commands.equip-usage"));
                            return true;
                        }
                        target = Bukkit.getPlayer(args[1]);
                        if(target == null){
                            Utils.sendMessage(sender,plugin.prefix + messages.getString("offline-player"));
                            return true;
                        }
                        if(args.length == 4) {
                            if(!args[3].startsWith("#")) {
                                plugin.getCosmeticsManager().equipCosmetic(target, args[2], null, false);
                            }
                            plugin.getCosmeticsManager().equipCosmetic(target, args[2], args[3], false);
                            return true;
                        }
                        if(args.length == 5) {
                            plugin.getCosmeticsManager().equipCosmetic(target, args[2], args[3], Boolean.parseBoolean(args[4]));
                            return true;
                        }
                        plugin.getCosmeticsManager().equipCosmetic(target, args[2], null, false);
                        return true;
                    case "unequip":
                        // /cosmetics unequip <player> <id>
                        if(args.length < 3){
                            return true;
                        }
                        target = Bukkit.getPlayer(args[1]);
                        if(target == null){
                            Utils.sendMessage(sender,plugin.prefix + messages.getString("offline-player"));
                            return true;
                        }
                        if(args[2].equalsIgnoreCase("all")){
                            plugin.getCosmeticsManager().unEquipAll(sender, target);
                            return true;
                        }
                        plugin.getCosmeticsManager().unSetCosmetic(target, args[2]);
                        return true;
                    case "open":
                        //cosmetics open <menu-id> <player>
                        if(args.length < 3){
                            Utils.sendMessage(sender,plugin.prefix + messages.getString("commands.menu-usage"));
                            return true;
                        }
                        target = Bukkit.getPlayer(args[2]);
                        if(target == null){
                            Utils.sendMessage(sender,plugin.prefix + messages.getString("offline-player"));
                            return true;
                        }
                        plugin.getCosmeticsManager().openMenu(target, args[1]);
                        return true;
                    case "token":
                        //cosmetics token give <player> <name>
                        if(args.length < 3){
                            Utils.sendMessage(sender,plugin.prefix + plugin.getMessages().getString("commands.token-usage"));
                            return true;
                        }
                        if(args[1].equalsIgnoreCase("give")){
                            target = Bukkit.getPlayer(args[2]);
                            if(target == null){
                                Utils.sendMessage(sender,plugin.prefix + messages.getString("offline-player"));
                                return true;
                            }
                            plugin.getCosmeticsManager().giveToken(sender, target, args[3]);
                            return true;
                        }
                        return true;
                    default:
                        Utils.sendMessage(sender,plugin.prefix + plugin.getMessages().getString("commands.not-found"));
                        return true;
                }
            }
            return true;
        }
        if(sender instanceof Player){
            Player player = (Player) sender;
            Player target;
            if(args.length >= 1){
                switch (args[0].toLowerCase()){
                    case "test":
                        target = Bukkit.getPlayer(args[1]);
                        if(target == null){
                            Utils.sendMessage(player,plugin.prefix + messages.getString("offline-player"));
                            return true;
                        }
                        player.addPassenger(target);
                        return true;
                    case "unlock":
                        if(args.length < 2){
                            return true;
                        }
                        Player p = Bukkit.getPlayer(args[1]);
                        if(p == null) return true;
                        PlayerData playerData = PlayerData.getPlayer(p);
                        playerData.setZone(false);
                        return true;
                    case "addall":
                        if(args.length < 2){
                            Utils.sendMessage(player,plugin.prefix + messages.getString("commands.add-all-usage"));
                            return true;
                        }
                        target = Bukkit.getPlayer(args[1]);
                        if(target == null){
                            Utils.sendMessage(player,plugin.prefix + messages.getString("offline-player"));
                            return true;
                        }
                        plugin.getCosmeticsManager().addAllCosmetics(player, target);
                        return true;
                    case "add":
                        //cosmetics add <player> <id>
                        if(args.length < 3){

                            Utils.sendMessage(player,plugin.prefix + messages.getString("commands.add-usage"));
                            return true;
                        }
                        target = Bukkit.getPlayer(args[1]);
                        if(target == null){
                            Utils.sendMessage(player,plugin.prefix + messages.getString("offline-player"));
                            return true;
                        }
                        plugin.getCosmeticsManager().addCosmetic(player, target, args[2]);
                        return true;
                    case "remove":
                        //cosmetics add <player> <id>
                        if(args.length < 3){
                            Utils.sendMessage(player,plugin.prefix + messages.getString("commands.remove-usage"));
                            return true;
                        }
                        target = Bukkit.getPlayer(args[1]);
                        if(target == null){
                            Utils.sendMessage(player,plugin.prefix + messages.getString("offline-player"));
                            return true;
                        }
                        plugin.getCosmeticsManager().removeCosmetic(player, target, args[2]);
                        return true;
                    case "removeall":
                        if(args.length < 2){
                            Utils.sendMessage(player,plugin.prefix + messages.getString("commands.remove-all-usage"));
                            return true;
                        }
                        target = Bukkit.getPlayer(args[1]);
                        if(target == null){
                            Utils.sendMessage(player,plugin.prefix + messages.getString("offline-player"));
                            return true;
                        }
                        plugin.getCosmeticsManager().removeAllCosmetics(player, target);
                        return true;
                    case "reload":
                        plugin.getCosmeticsManager().reload(sender);
                        return true;
                    case "use":
                        //cosmetics use <id> <color>
                        if(args.length < 2){
                            Utils.sendMessage(player,plugin.prefix + messages.getString("commands.use-usage"));
                            return true;
                        }
                        if(args.length == 3) {
                            plugin.getCosmeticsManager().equipCosmetic(player, args[1], args[2], false);
                            return true;
                        }
                        plugin.getCosmeticsManager().equipCosmetic(player, args[1], null, false);
                        return true;
                    case "preview":
                        if(args.length < 2){
                            Utils.sendMessage(player,plugin.prefix + messages.getString("commands.use-usage"));
                            return true;
                        }
                        plugin.getCosmeticsManager().previewCosmetic(player, args[1]);
                        return true;
                    case "unuse":
                        if(args.length < 2){
                            Utils.sendMessage(player,plugin.prefix + messages.getString("commands.use-usage"));
                            return true;
                        }
                        plugin.getCosmeticsManager().unUseCosmetic(player, args[1]);
                        return true;
                    case "unset":
                        if(args.length < 2){
                            Utils.sendMessage(player,plugin.prefix + messages.getString("commands.use-usage"));
                            return true;
                        }
                        plugin.getCosmeticsManager().unSetCosmetic(player, args[1]);
                        return true;
                    case "unequip":
                        if(args.length < 2){
                            return true;
                        }
                        if(args[1].equalsIgnoreCase("all")){
                            plugin.getCosmeticsManager().unEquipAll(player);
                            return true;
                        }
                        plugin.getCosmeticsManager().unSetCosmetic(player, args[1]);
                        return true;
                    case "open":
                        //cosmetics open <menu-id>
                        if(args.length < 2){
                            Utils.sendMessage(player,plugin.prefix + messages.getString("commands.menu-usage"));
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
                    case "hide":
                        plugin.getCosmeticsManager().hideSelfCosmetic(player, CosmeticType.BAG);
                        return true;
                    case "toggle":
                        playerData = PlayerData.getPlayer(player);
                        playerData.toggleHiddeCosmetics();
                        return true;
                    case "zones":
                        //cosmetics zones add <name>
                        if(args.length < 2){
                            for(String msg : plugin.getMessages().getStringList("commands.zones-usage")){
                                Utils.sendMessage(player,msg);
                            }
                            return true;
                        }
                        if(args[1].equalsIgnoreCase("add")){
                            if(args.length < 3){
                                for(String msg : plugin.getMessages().getStringList("commands.zones-usage")){
                                    Utils.sendMessage(player,msg);
                                }
                                return true;
                            }
                            plugin.getZonesManager().addZone(player, args[2]);
                            return true;
                        }
                        if(args[1].equalsIgnoreCase("remove")){
                            if(args.length < 3){
                                for(String msg : plugin.getMessages().getStringList("commands.zones-usage")){
                                    Utils.sendMessage(player,msg);
                                }
                                return true;
                            }
                            plugin.getZonesManager().removeZone(player, args[2]);
                            return true;
                        }
                        if(args[1].equalsIgnoreCase("setnpc")){
                            if(args.length < 3){
                                for(String msg : plugin.getMessages().getStringList("commands.zones-usage")){
                                    Utils.sendMessage(player,msg);
                                }
                                return true;
                            }
                            plugin.getZonesManager().setZoneNPC(player, args[2]);
                            return true;
                        }
                        if(args[1].equalsIgnoreCase("setballoon")){
                            if(args.length < 3){
                                for(String msg : plugin.getMessages().getStringList("commands.zones-usage")){
                                    Utils.sendMessage(player,msg);
                                }
                                return true;
                            }
                            plugin.getZonesManager().setBalloonNPC(player, args[2]);
                            return true;
                        }
                        if(args[1].equalsIgnoreCase("setspray")){
                            if(args.length < 3){
                                for(String msg : plugin.getMessages().getStringList("commands.zones-usage")){
                                    Utils.sendMessage(player,msg);
                                }
                                return true;
                            }
                            plugin.getZonesManager().setSpray(player, args[2]);
                            return true;
                        }
                        if(args[1].equalsIgnoreCase("setenter")){
                            if(args.length < 3){
                                for(String msg : plugin.getMessages().getStringList("commands.zones-usage")){
                                    Utils.sendMessage(player,msg);
                                }
                                return true;
                            }
                            plugin.getZonesManager().setZoneEnter(player, args[2]);
                            return true;
                        }
                        if(args[1].equalsIgnoreCase("setexit")){
                            if(args.length < 3){
                                for(String msg : plugin.getMessages().getStringList("commands.zones-usage")){
                                    Utils.sendMessage(player,msg);
                                }
                                return true;
                            }
                            plugin.getZonesManager().setZoneExit(player, args[2]);
                            return true;
                        }
                        if(args[1].equalsIgnoreCase("givecorns")){
                            if(args.length < 3){
                                for(String msg : plugin.getMessages().getStringList("commands.zones-usage")){
                                    Utils.sendMessage(player,msg);
                                }
                                return true;
                            }
                            plugin.getZonesManager().giveCorn(player, args[2]);
                            return true;
                        }
                        if(args[1].equalsIgnoreCase("enable")){
                            if(args.length < 3){
                                for(String msg : plugin.getMessages().getStringList("commands.zones-usage")){
                                    Utils.sendMessage(player,msg);
                                }
                                return true;
                            }
                            plugin.getZonesManager().enableZone(player, args[2]);
                            return true;
                        }
                        if(args[1].equalsIgnoreCase("disable")){
                            if(args.length < 3){
                                for(String msg : plugin.getMessages().getStringList("commands.zones-usage")){
                                    Utils.sendMessage(player,msg);
                                }
                                return true;
                            }
                            plugin.getZonesManager().disableZone(player, args[2]);
                            return true;
                        }
                        if(args[1].equalsIgnoreCase("save")){
                            if(args.length < 3){
                                for(String msg : plugin.getMessages().getStringList("commands.zones-usage")){
                                    Utils.sendMessage(player,msg);
                                }
                                return true;
                            }
                            plugin.getZonesManager().saveZone(player, args[2]);
                            return true;
                        }
                        return true;
                    case "token":
                        //cosmetics token give <player> <name>
                        if(args.length < 4){
                            Utils.sendMessage(player,plugin.prefix + plugin.getMessages().getString("commands.token-usage"));
                            return true;
                        }
                        if(args[1].equalsIgnoreCase("give")){
                            target = Bukkit.getPlayer(args[2]);
                            if(target == null){
                                Utils.sendMessage(sender,plugin.prefix + messages.getString("offline-player"));
                                return true;
                            }
                            plugin.getCosmeticsManager().giveToken(player, target, args[3]);
                            return true;
                        }
                        return true;
                    case "check":
                        plugin.getCosmeticsManager().sendCheck(player);
                        return true;
                    case "npc":
                        if(!plugin.isCitizens()){
                            Utils.sendMessage(player, plugin.prefix + "&cCitizens is not installed!");
                            return true;
                        }
                        //cosmetics npc 1 <cosmetic-id> <color>
                        if(args.length == 2 && args[1].equalsIgnoreCase("save")){
                            plugin.getNPCsLoader().save();
                            return true;
                        }
                        if(args.length < 3){
                            Utils.sendMessage(player,plugin.prefix + plugin.getMessages().getString("commands.npc-usage"));
                            return true;
                        }
                        try{
                            plugin.getCitizens().equipCosmetic(player, args[1], args[2], args[3]);
                        }catch (ArrayIndexOutOfBoundsException exception){
                            plugin.getCitizens().equipCosmetic(player, args[1], args[2], null);
                        }
                        return true;
                    case "tint":
                        //cosmetics tint <color>
                        if(args.length < 2){
                            Utils.sendMessage(player,plugin.prefix + plugin.getMessages().getString("commands.tint-usage"));
                            return true;
                        }
                        plugin.getCosmeticsManager().tintItem(player, args[1]);
                        return true;
                    default:
                        Utils.sendMessage(player,plugin.prefix + plugin.getMessages().getString("commands.not-found"));
                        return true;
                }
            }
            if(player.hasPermission("magicosmetics.cosmetics.use")) {
                plugin.getCosmeticsManager().openMenu(player, plugin.getMainMenu());
                if(plugin.getOnExecuteCosmetics().isEmpty()) return true;
                player.performCommand(plugin.getOnExecuteCosmetics());
            }
            return true;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        List<String> arguments = new ArrayList<>();
        if(sender.hasPermission("magicosmetics.cosmetics")) {
            arguments.add("add");
            arguments.add("remove");
            arguments.add("addAll");
            if(plugin.isCitizens()) {
                arguments.add("npc");
            }
        }
        if(sender.hasPermission("magicosmetics.menus")) {
            arguments.add("open");
        }
        if(sender.hasPermission("magicosmetics.zones")) {
            arguments.add("zones");
        }
        if(sender.hasPermission("magicosmetics.tokens")) {
            arguments.add("token");
        }
        if(sender.hasPermission("magicosmetics.reload")) {
            arguments.add("reload");
        }
        if(sender.hasPermission("magicosmetics.hide")) {
            arguments.add("hide");
        }
        if(sender.hasPermission("magicosmetics.toggle")){
            arguments.add("toggle");
        }
        if(sender.hasPermission("magicosmetics.equip")){
            arguments.add("use");
            arguments.add("unequip");
        }
        if(sender.hasPermission("magicosmetics.tint")){
            arguments.add("tint");
        }
        if(arguments.size() == 0) return arguments;
        List<String> result = new ArrayList<>();
        switch (args.length){
            case 1:
                for(String a : arguments){
                    if(a.toLowerCase().startsWith(args[0].toLowerCase()))
                        result.add(a);
                }
                return result;
            case 2:
                switch (args[0].toLowerCase()){
                    case "hide":
                    case "toggle":
                    case "add":
                    case "addall":
                    case "remove":
                        return null;
                    case "npc":
                        if(!plugin.isCitizens()) return null;
                        result.add("save");
                        result.addAll(plugin.getCitizens().getNPCs());
                        return result;
                    case "unequip":
                    case "use":
                        if(!sender.hasPermission("magicosmetics.equip")) return null;
                        result.add("all");
                        result.addAll(Cosmetic.cosmetics.keySet());
                        return result;
                    case "open":
                        if(!sender.hasPermission("magicosmetics.menus")) return null;
                        result.addAll(Menu.inventories.keySet());
                        return result;
                    case "zones":
                        if(!sender.hasPermission("magicosmetics.zones")) return null;
                        result.add("add");
                        result.add("remove");
                        result.add("setNPC");
                        result.add("setBalloon");
                        result.add("setSpray");
                        result.add("setEnter");
                        result.add("setExit");
                        result.add("giveCorns");
                        result.add("enable");
                        result.add("disable");
                        result.add("save");
                        return result;
                    case "token":
                        if(!sender.hasPermission("magicosmetics.tokens")) return null;
                        result.add("give");
                        return result;
                    case "tint":
                        if(!sender.hasPermission("magicosmetics.tint")) return null;
                        result.add("#FFFFFF");
                        return result;
                }
            case 3:
                switch (args[0].toLowerCase()){
                    case "add":
                    case "remove":
                    case "npc":
                        if(!sender.hasPermission("magicosmetics.cosmetics")) return null;
                        result.addAll(Cosmetic.cosmetics.keySet());
                        return result;
                    case "use":
                    case "equip":
                        if(!sender.hasPermission("magicosmetics.equip")) return null;
                        result.add("#FFFFFF");
                        result.add("null");
                        return result;
                    case "zones":
                        if(!sender.hasPermission("magicosmetics.zones")) return null;
                        if(args[1].equalsIgnoreCase("add")) return new ArrayList<>();
                        result.addAll(Zone.zones.keySet());
                        return result;
                    case "token":
                        return null;
                }
            case 4:
                if(args[0].equalsIgnoreCase("token") && args[1].equalsIgnoreCase("give")){
                    if(!sender.hasPermission("magicosmetics.tokens")) return null;
                    result.addAll(Token.tokens.keySet());
                    return result;
                }
                if(args[0].equalsIgnoreCase("npc")){
                    if(!plugin.isCitizens()) return null;
                    if(!sender.hasPermission("magicosmetics.cosmetics")) return null;
                    result.add("#FFFFFF");
                    return result;
                }

        }

        return null;
    }
}
