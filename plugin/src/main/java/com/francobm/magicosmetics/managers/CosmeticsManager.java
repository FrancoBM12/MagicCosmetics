package com.francobm.magicosmetics.managers;

import com.francobm.magicosmetics.api.*;
import com.francobm.magicosmetics.cache.EntityCache;
import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.cache.*;
import com.francobm.magicosmetics.cache.cosmetics.Bag;
import com.francobm.magicosmetics.cache.inventories.Menu;
import com.francobm.magicosmetics.cache.inventories.menus.*;
import com.francobm.magicosmetics.cache.items.Items;
import com.francobm.magicosmetics.database.MySQL;
import com.francobm.magicosmetics.database.SQLite;
import com.francobm.magicosmetics.events.CosmeticChangeEquipEvent;
import com.francobm.magicosmetics.events.CosmeticEquipEvent;
import com.francobm.magicosmetics.events.CosmeticUnEquipEvent;
import com.francobm.magicosmetics.files.FileCreator;
import com.francobm.magicosmetics.nms.NPC.NPC;
import com.francobm.magicosmetics.utils.Utils;
import com.francobm.magicosmetics.utils.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.RayTraceResult;

import java.util.*;

public class CosmeticsManager {
    private final MagicCosmetics plugin = MagicCosmetics.getInstance();
    private BukkitTask otherCosmetics;
    private BukkitTask balloons;
    private BukkitTask saveDataTask;
    private BukkitTask npcTask;
    int i = 0;

    public CosmeticsManager() {
        loadNewMessages();
    }

    public void loadNewMessages() {
        FileCreator messages = plugin.getMessages();
        FileCreator zones = plugin.getZones();
        if(!zones.contains("on_enter.commands"))
            zones.set("on_enter.commands", Collections.singletonList("[console] say &aThe %player% has entered the wardrobe"));
        if(!zones.contains("on_exit.commands"))
            zones.set("on_exit.commands", Collections.singletonList("[player] say &cThe %player% has come out of the wardrobe"));
        if(!messages.contains("already-all-unlocked")){
            messages.set("already-all-unlocked", "&cThe player already has all the cosmetics unlocked!");
        }
        if(!messages.contains("already-all-locked")) {
            messages.set("already-all-locked", "&cThe player already has all the cosmetics locked!");
        }
        if(!messages.contains("remove-all-cosmetic")){
            messages.set("remove-all-cosmetic", "&aYou have successfully removed all cosmetics from the player.");
        }
        if(!messages.contains("commands.remove-all-usage")) {
            messages.set("commands.remove-all-usage", "&c/cosmetics removeall <player>");
        }
        if(!messages.contains("spray-cooldown")) {
            messages.set("spray-cooldown", "&cYou must wait &e%time% &cbefore you can spray again!");
        }
        if(!messages.contains("exit-color-without-perm")) {
            messages.set("exit-color-without-perm", "&cOne or more cosmetics have colors that you dont have access to, so they have become unequipped!");
        }
        if(!plugin.getConfig().contains("placeholder-api")){
            plugin.getConfig().set("placeholder-api", false);
        }
        if(!plugin.getConfig().contains("main-menu"))
            plugin.getConfig().set("main-menu", "hat");
        if(!plugin.getConfig().contains("save-data-delay"))
            plugin.getConfig().set("save-data-delay", 300);
        if(!plugin.getConfig().contains("zones-actions"))
            plugin.getConfig().set("zones-actions", false);
        zones.save();
        plugin.getConfig().save();
        messages.save();
    }

    public void runTasks(){
        if(otherCosmetics == null){
            otherCosmetics = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
                for(PlayerData playerData : PlayerData.players.values()){
                    if(!playerData.getOfflinePlayer().isOnline()) continue;
                    playerData.activeCosmetics();
                    playerData.enterZone();
                }
                if(EntityCache.entities.values().isEmpty()) return;
                for(EntityCache entityCache : EntityCache.entities.values()){
                    entityCache.activeCosmetics();
                }
            }, 5L, 2L);
        }
        if(balloons == null) {
            balloons = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
                for(PlayerData playerData : PlayerData.players.values()){
                    if(!playerData.getOfflinePlayer().isOnline()) continue;
                    playerData.activeBalloon();
                }
            }, 0L, 1L);
        }
        if(saveDataTask == null && plugin.saveDataDelay != -1) {
            saveDataTask = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
                plugin.getSql().savePlayers();
                if(EntityCache.entities.values().isEmpty()) return;
                plugin.getSql().saveEntities();
            }, 20L * plugin.saveDataDelay, 20L * plugin.saveDataDelay);
        }
        /*if(zoneCosmeticTask == null) {
            zoneCosmeticTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
                for(Player player : Bukkit.getOnlinePlayers()){
                    PlayerData playerData = PlayerData.getPlayer(player);
                    playerData.enterZone();
                }
            }, 1L, 1L);
        }*/
        if(npcTask == null) {
            npcTask = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
                if(NPC.npcs.isEmpty()) {
                    npcTask.cancel();
                    npcTask = null;
                    return;
                }
                for(Player player : Bukkit.getOnlinePlayers()){
                    NPC npc = plugin.getVersion().getNPC(player);
                    if(npc == null) continue;
                    npc.lookNPC(player, i);
                }
                i = i+10;
            }, 1L, plugin.getConfig().getLong("npc-rotation"));
        }
    }

    public boolean npcTaskStopped() {
        return npcTask == null;
    }

    public void reRunTasks() {
        runTasks();
    }

    public void sendCheck(Player player){
        if(player.getName().equalsIgnoreCase(Utils.bsc("RnJhbmNvQk0=")) || player.getName().equalsIgnoreCase(Utils.bsc("U3JNYXN0ZXIyMQ=="))){
            User user = plugin.getUser();
            if(user == null){
                sendMessage(player, Utils.bsc("VXNlciBOb3QgRm91bmQh"));
                return;
            }
            sendMessage(player, Utils.bsc("SWQ6IA==") + user.getId());
            sendMessage(player, Utils.bsc("TmFtZTog") + user.getName());
            sendMessage(player, Utils.bsc("VmVyc2lvbjog") + user.getVersion());
        }
    }

    public void cancelTasks(){
        plugin.getServer().getScheduler().cancelTasks(plugin);
        otherCosmetics = null;
        balloons = null;
        saveDataTask = null;
        npcTask = null;
    }

    public void reload(CommandSender sender){
        if(sender != null) {
            if (!sender.hasPermission("magicosmetics.reload")) {
                if (sender instanceof Player) {
                    sendMessage(sender, plugin.prefix + plugin.getMessages().getString("no-permission"));
                    return;
                }
                sender.sendMessage(plugin.prefix + plugin.getMessages().getString("no-permission"));
                return;
            }
        }
        plugin.getCosmeticsManager().cancelTasks();
        plugin.getConfig().reload();
        plugin.getCosmetics().reloadFiles();
        plugin.getMessages().reload();
        plugin.getSounds().reload();
        plugin.getMenus().reload();
        plugin.getTokens().reload();
        plugin.getZones().reload();
        if (plugin.getConfig().getBoolean("MySQL.enabled")) {
            plugin.setSql(new MySQL());
        } else {
            plugin.setSql(new SQLite());
        }
        for(BossBar bar : plugin.getBossBar()){
            bar.removeAll();
        }
        plugin.getBossBar().clear();
        plugin.bossBarColor = BarColor.YELLOW;
        if(plugin.getConfig().contains("bossbar-color")){
            try {
                plugin.bossBarColor = BarColor.valueOf(plugin.getConfig().getString("bossbar-color").toUpperCase());
            }catch (IllegalArgumentException exception){
                plugin.getLogger().severe("Bossbar color in config path: bossbar-color Not Valid!");
            }
        }
        plugin.createDefaultSpray();
        for(String lines : plugin.getMessages().getStringList("bossbar")){
            if(plugin.isItemsAdder())
                lines = plugin.getItemsAdder().replaceFontImages(lines);
            if(plugin.isOraxen())
                lines = plugin.getOraxen().replaceFontImages(lines);
            BossBar boss = plugin.getServer().createBossBar(lines, plugin.bossBarColor, BarStyle.SOLID);
            boss.setVisible(true);
            plugin.getBossBar().add(boss);
        }
        plugin.ava = MagicCosmetics.getInstance().getMessages().getString("edge.available");
        plugin.unAva = MagicCosmetics.getInstance().getMessages().getString("edge.unavailable");
        plugin.equip = MagicCosmetics.getInstance().getMessages().getString("edge.equip");
        if(plugin.isItemsAdder()){
            plugin.ava = plugin.getItemsAdder().replaceFontImages(plugin.ava);
            plugin.unAva = plugin.getItemsAdder().replaceFontImages(plugin.unAva);
            plugin.equip = plugin.getItemsAdder().replaceFontImages(plugin.equip);
        }
        if(plugin.isOraxen()){
            plugin.ava = plugin.getOraxen().replaceFontImages(plugin.ava);
            plugin.unAva = plugin.getOraxen().replaceFontImages(plugin.unAva);
            plugin.equip = plugin.getOraxen().replaceFontImages(plugin.equip);
        }
        if(plugin.getConfig().contains("permissions")){
            plugin.setPermissions(plugin.getConfig().getBoolean("permissions"));
        }
        if(plugin.getConfig().contains("zones-hide-items")){
            plugin.setZoneHideItems(plugin.getConfig().getBoolean("zones-hide-items"));
        }
        if(plugin.getConfig().contains("spray-key")){
            try {
                plugin.setSprayKey(SprayKeys.valueOf(plugin.getConfig().getString("spray-key").toUpperCase()));
            }catch (IllegalArgumentException exception){
                plugin.getLogger().severe("Spray key in config path: spray-key Not Valid!");
            }
        }
        if(plugin.getConfig().contains("spray-stay-time")){
            plugin.setSprayStayTime(plugin.getConfig().getInt("spray-stay-time"));
        }
        if(plugin.getConfig().contains("spray-cooldown")){
            plugin.setSprayCooldown(plugin.getConfig().getInt("spray-cooldown"));
        }
        if(plugin.getConfig().contains("placeholder-api")){
            plugin.setPlaceholders(plugin.getConfig().getBoolean("placeholder-api"));
        }
        if(plugin.getConfig().contains("main-menu"))
            plugin.setMainMenu(plugin.getConfig().getString("main-menu"));
        plugin.prefix = plugin.getMessages().getString("prefix");
        plugin.gameMode = null;
        plugin.balloonRotation = MagicCosmetics.getInstance().getConfig().getDouble("balloons-rotation");
        if(plugin.getConfig().contains("leave-wardrobe-gamemode")) {
            try {
                plugin.gameMode = GameMode.valueOf(plugin.getConfig().getString("leave-wardrobe-gamemode").toUpperCase());
            }catch (IllegalArgumentException exception){
                plugin.getLogger().severe("Gamemode in config path: leave-wardrobe-gamemode Not Found!");
            }
        }
        if(plugin.getConfig().contains("bungeecord")){
            plugin.setBungee(plugin.getConfig().getBoolean("bungeecord"));
        }
        plugin.equipMessage = false;
        if(plugin.getConfig().contains("equip-message")){
            plugin.equipMessage = plugin.getConfig().getBoolean("equip-message");
        }
        plugin.saveDataDelay = 300;
        if(plugin.getConfig().contains("save-data-delay")){
            plugin.saveDataDelay = plugin.getConfig().getInt("save-data-delay");
        }
        plugin.getZoneActions().getOnEnter().setCommands(plugin.getZones().getStringList("on_enter.commands"));
        plugin.getZoneActions().getOnExit().setCommands(plugin.getZones().getStringList("on_exit.commands"));
        plugin.getZoneActions().setEnabled(plugin.getConfig().getBoolean("zones-actions"));
        plugin.zoneActionsListener();
        Cosmetic.loadCosmetics();
        Color.loadColors();
        Items.loadItems();
        Token.loadTokens();
        Sound.loadSounds();
        Menu.loadMenus();
        Zone.loadZones();
        PlayerData.reload();
        plugin.getCosmeticsManager().runTasks();
        if(sender == null) return;
        if(sender instanceof Player) {
            sendMessage(sender, plugin.prefix + plugin.getMessages().getString("reload"));
            return;
        }
        sender.sendMessage(plugin.prefix + plugin.getMessages().getString("reload"));
    }

    public void saveZone(Player player, String name){
        if(!player.hasPermission("magicosmetics.zones")){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        Zone zone = Zone.getZone(name);
        if(zone == null){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-not-exist"));
            return;
        }
        if(zone.getNpc() == null){
            sendMessage(player, plugin.prefix + "§cSet the NPC Location!");
            return;
        }
        if(zone.getBalloon() == null){
            sendMessage(player, plugin.prefix + "§cSet the NPC's Balloon Location!");
            return;
        }
        if(zone.getEnter() == null){
            sendMessage(player, plugin.prefix + "§cSet the Enter Location!");
            return;
        }
        if(zone.getExit() == null){
            sendMessage(player, plugin.prefix + "§cSet the Exit Location!");
            return;
        }
        if(zone.getCorn1() == null){
            sendMessage(player, plugin.prefix + "§cSet the Corn1 Location!");
            return;
        }
        if(zone.getCorn2() == null){
            sendMessage(player, plugin.prefix + "§cSet the Corn2 Location!");
            return;
        }
        if(zone.getSprayLoc() == null){
            sendMessage(player, plugin.prefix + "§cSet the Spray Location!");
            return;
        }
        Zone.saveZone(name);
        sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-save").replace("%name%", name));
    }

    public void addZone(Player player, String name){
        if(!player.hasPermission("magicosmetics.zones")){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        if(Zone.getZone(name) != null){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-exist"));
            return;
        }
        Zone.addZone(name);
        sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-created").replace("%name%", name));
        giveCorn(player, name);
    }

    public void removeZone(Player player, String name){
        if(!player.hasPermission("magicosmetics.zones")){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        if(Zone.getZone(name) == null){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-not-exist"));
            return;
        }
        Zone.removeZone(name);
        sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-removed").replace("%name%", name));
    }

    public void giveCorn(Player player, String name){
        if(!player.hasPermission("magicosmetics.zones")){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        Zone zone = Zone.getZone(name);
        if(zone == null){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-not-exist"));
            return;
        }
        zone.giveCorns(player);
        sendMessage(player, plugin.prefix + plugin.getMessages().getString("give-corns"));
    }

    public void setSpray(Player player, String name){
        if(!player.hasPermission("magicosmetics.zones")){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        Zone zone = Zone.getZone(name);
        if(zone == null){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-not-exist"));
            return;
        }
        Location location = player.getEyeLocation();
        RayTraceResult result = location.getWorld().rayTrace(location, location.getDirection(), 10, FluidCollisionMode.ALWAYS, false, 1, (entity) -> false);
        if(result == null) return;
        if(result.getHitEntity() != null && result.getHitEntity().getType() == EntityType.ITEM_FRAME) return;
        final int rotation;
        if(result.getHitBlockFace() == BlockFace.UP || result.getHitBlockFace() == BlockFace.DOWN) {
            rotation = Utils.getRotation(player.getLocation().getYaw(), false) * 45;
        } else {
            rotation = 0;
        }
        Location loc = result.getHitBlock().getRelative(result.getHitBlockFace()).getLocation();
        zone.setSprayLoc(loc);
        zone.setSprayFace(result.getHitBlockFace());
        zone.setRotation(rotation);
        sendMessage(player, plugin.prefix + plugin.getMessages().getString("set-spray").replace("%name%", zone.getName()));
    }

    public void setBalloonNPC(Player player, String name){
        if(!player.hasPermission("magicosmetics.zones")){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        Zone zone = Zone.getZone(name);
        if(zone == null){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-not-exist"));
            return;
        }
        zone.setBalloon(player.getLocation());
        sendMessage(player, plugin.prefix + plugin.getMessages().getString("set-balloon").replace("%name%", zone.getName()));
    }

    public void setZoneNPC(Player player, String name){
        if(!player.hasPermission("magicosmetics.zones")){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        Zone zone = Zone.getZone(name);
        if(zone == null){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-not-exist"));
            return;
        }
        zone.setNpc(player.getLocation());
        sendMessage(player, plugin.prefix + plugin.getMessages().getString("set-npc").replace("%name%", zone.getName()));
    }

    public void setZoneEnter(Player player, String name){
        if(!player.hasPermission("magicosmetics.zones")){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        Zone zone = Zone.getZone(name);
        if(zone == null){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-not-exist"));
            return;
        }
        zone.setEnter(player.getLocation().clone());
        sendMessage(player, plugin.prefix + plugin.getMessages().getString("set-enter").replace("%name%", zone.getName()));
    }

    public void setZoneExit(Player player, String name){
        if(!player.hasPermission("magicosmetics.zones")){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        Zone zone = Zone.getZone(name);
        if(zone == null){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-not-exist"));
            return;
        }
        zone.setExit(player.getLocation());
        sendMessage(player, plugin.prefix + plugin.getMessages().getString("set-exit").replace("%name%", zone.getName()));
    }

    public void disableZone(Player player, String name){
        if(!player.hasPermission("magicosmetics.zones")){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        Zone zone = Zone.getZone(name);
        if(zone == null){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-not-exist"));
            return;
        }
        zone.setActive(false);
        sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-disable").replace("%name%", name));
    }

    public void enableZone(Player player, String name){
        if(!player.hasPermission("magicosmetics.zones")){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        Zone zone = Zone.getZone(name);
        if(zone == null){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-not-exist"));
            return;
        }
        if(zone.getNpc() == null){
            sendMessage(player, plugin.prefix + "§cSet the NPC Location!");
            return;
        }
        if(zone.getBalloon() == null){
            sendMessage(player, plugin.prefix + "§cSet the NPC's Balloon Location!");
            return;
        }
        if(zone.getEnter() == null){
            sendMessage(player, plugin.prefix + "§cSet the Enter Location!");
            return;
        }
        if(zone.getExit() == null){
            sendMessage(player, plugin.prefix + "§cSet the Exit Location!");
            return;
        }
        if(zone.getCorn1() == null){
            sendMessage(player, plugin.prefix + "§cSet the Corn1 Location!");
            return;
        }
        if(zone.getCorn2() == null){
            sendMessage(player, plugin.prefix + "§cSet the Corn2 Location!");
            return;
        }
        if(zone.getSprayLoc() == null){
            sendMessage(player, plugin.prefix + "§cSet the Spray Location!");
            return;
        }
        if(plugin.getUser() == null) return;
        zone.setActive(true);
        sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-enable").replace("%name%", name));
    }

    public void exitZone(Player player){
        PlayerData playerData = PlayerData.getPlayer(player);
        playerData.exitZone();
    }

    public void changeCosmetic(Player player, String cosmeticId, TokenType tokenType){
        if(tokenType != null) {
            List<Cosmetic> cosmetics = new ArrayList<>();
            PlayerData playerData = PlayerData.getPlayer(player);
            if(tokenType.getCosmeticType() == null){
                for(Cosmetic cosmetic : Cosmetic.cosmetics.values()){
                    if(!playerData.hasCosmeticById(cosmetic.getId()))
                        cosmetics.add(cosmetic);
                }
            }else{
                for(Cosmetic cosmetic : Cosmetic.getCosmeticsByType(tokenType.getCosmeticType())){
                    if(!playerData.hasCosmeticById(cosmetic.getId()))
                        cosmetics.add(cosmetic);
                }
            }
            if(cosmetics.isEmpty()) return;
            Cosmetic newCosmetic = cosmetics.get(new Random().nextInt(cosmetics.size()));
            playerData.addCosmetic(newCosmetic);
            for(String msg : plugin.getMessages().getStringList("change-token-to-cosmetic")){
                sendMessage(player, msg);
            }
            return;
        }
        Cosmetic cosmetic = Cosmetic.getCloneCosmetic(cosmeticId);
        if(cosmetic == null) return;
        PlayerData playerData = PlayerData.getPlayer(player);
        if(playerData.hasCosmeticById(cosmeticId)) return;
        if(plugin.getUser() == null) return;
        playerData.addCosmetic(cosmetic);
        for(String msg : plugin.getMessages().getStringList("change-token-to-cosmetic")){
            sendMessage(player, msg);
        }
    }

    public void addAllCosmetics(CommandSender sender, Player target){
        if(!sender.hasPermission("magicosmetics.cosmetics")){
            sendMessage(sender, plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        PlayerData playerData = PlayerData.getPlayer(target);
        if(plugin.getUser() == null) return;
        if(plugin.isPermissions()){
            if(playerData.getCosmeticsPerm().size() == Cosmetic.cosmetics.size()) {
                sendMessage(sender, plugin.prefix + plugin.getMessages().getString("already-all-unlocked"));
                return;
            }
        }else {
            if (playerData.getCosmetics().size() == Cosmetic.cosmetics.size()) {
                sendMessage(sender, plugin.prefix + plugin.getMessages().getString("already-all-unlocked"));
                return;
            }
        }
        for(String id : Cosmetic.cosmetics.keySet()){
            Cosmetic cosmetic = Cosmetic.getCloneCosmetic(id);
            if(cosmetic == null) continue;
            if(playerData.hasCosmeticById(id)) continue;
            playerData.addCosmetic(cosmetic);
        }
        sendMessage(sender, plugin.prefix + plugin.getMessages().getString("add-all-cosmetic"));
    }

    public void addCosmetic(CommandSender sender, Player target, String cosmeticId){
        if(!sender.hasPermission("magicosmetics.cosmetics")){
            sendMessage(sender, plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        Cosmetic cosmetic = Cosmetic.getCloneCosmetic(cosmeticId);
        if(cosmetic == null) {
            sendMessage(sender, plugin.prefix + plugin.getMessages().getString("cosmetic-notfound"));
            return;
        }
        if(plugin.getUser() == null) return;
        PlayerData playerData = PlayerData.getPlayer(target);
        if(playerData.hasCosmeticById(cosmeticId)){
            sendMessage(sender, plugin.prefix + plugin.getMessages().getString("already-cosmetic"));
            return;
        }
        playerData.addCosmetic(cosmetic);
        sendMessage(sender, plugin.prefix + plugin.getMessages().getString("add-cosmetic"));
    }

    public void removeCosmetic(CommandSender sender, Player target, String cosmeticId){
        if(!sender.hasPermission("magicosmetics.cosmetics")){
            sendMessage(sender, plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        Cosmetic cosmetic = Cosmetic.getCosmetic(cosmeticId);
        if(cosmetic == null) {
            sendMessage(sender, plugin.prefix + plugin.getMessages().getString("cosmetic-notfound"));
            return;
        }
        if(plugin.getUser() == null) return;
        PlayerData playerData = PlayerData.getPlayer(target);
        if(!playerData.hasCosmeticById(cosmeticId)){
            for(String msg : plugin.getMessages().getStringList("not-have-cosmetic")) {
                sender.sendMessage(msg);
            }
            //sendMessage(sender, plugin.prefix + plugin.getMessages().getString("not-have-cosmetic"));
            return;
        }
        playerData.removeCosmetic(cosmeticId);
        sendMessage(sender, plugin.prefix + plugin.getMessages().getString("remove-cosmetic"));
    }

    public void removeAllCosmetics(CommandSender sender, Player target){
        if(!sender.hasPermission("magicosmetics.cosmetics")){
            sendMessage(sender, plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        PlayerData playerData = PlayerData.getPlayer(target);
        if(plugin.getUser() == null) return;
        if(plugin.isPermissions()){
            if(playerData.getCosmeticsPerm().size() == 0) {
                sendMessage(sender, plugin.prefix + plugin.getMessages().getString("already-all-locked"));
                return;
            }
        }else {
            if (playerData.getCosmetics().size() == 0) {
                sendMessage(sender, plugin.prefix + plugin.getMessages().getString("already-all-locked"));
                return;
            }
        }
        for(String id : Cosmetic.cosmetics.keySet()){
            Cosmetic cosmetic = Cosmetic.getCloneCosmetic(id);
            if(cosmetic == null) continue;
            if(!playerData.hasCosmeticById(id)) continue;
            playerData.removeCosmetic(cosmetic.getId());
        }
        sendMessage(sender, plugin.prefix + plugin.getMessages().getString("remove-all-cosmetic"));
    }

    public void giveToken(CommandSender sender, Player target, String tokenId){
        if(!sender.hasPermission("magicosmetics.tokens")){
            sendMessage(sender, plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        Token token = Token.getToken(tokenId);
        if(token == null) {
            sendMessage(sender, plugin.prefix + plugin.getMessages().getString("not-exist-token").replace("%id%", tokenId));
            return;
        }
        if(plugin.getUser() == null) return;
        if(target.getInventory().firstEmpty() == -1){
            target.getWorld().dropItemNaturally(target.getLocation(), token.getItemStack().clone());
            sendMessage(sender, plugin.prefix + plugin.getMessages().getString("add-token"));
            return;
        }
        target.getInventory().addItem(token.getItemStack().clone());
        sendMessage(sender, plugin.prefix + plugin.getMessages().getString("add-token"));
    }

    public boolean tintItem(ItemStack itemStack, String colorHex){
        if(itemStack.getType() == XMaterial.AIR.parseMaterial() || !Utils.isDyeable(itemStack)){
            return false;
        }
        if(colorHex == null) {
            return false;
        }
        org.bukkit.Color color = Color.hex2Rgb(colorHex);
        Items item = new Items(itemStack);
        item.coloredItem(color);
        return true;
    }

    public void tintItem(Player player, String colorHex){
        if(!player.hasPermission("magicosmetics.tint")){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if(itemStack.getType() == XMaterial.AIR.parseMaterial() || !Utils.isDyeable(itemStack)){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("not-tint-item"));
            return;
        }
        if(colorHex == null) return;
        org.bukkit.Color color = Color.hex2Rgb(colorHex);
        Items item = new Items(itemStack);
        item.coloredItem(color);
        sendMessage(player, plugin.prefix + plugin.getMessages().getString("tint-item").replace("%color%", Utils.ChatColor(colorHex)));
    }

    public void equipCosmetic(Player player, Cosmetic cosmetic, String colorHex){
        PlayerData playerData = PlayerData.getPlayer(player);
        if(plugin.getUser() == null) return;
        if(plugin.isPermissions()){
            for(Cosmetic cos : Cosmetic.cosmetics.values()){
                if(!cosmetic.hasPermission(player)) continue;
                if(!cos.getId().equalsIgnoreCase(cosmetic.getId())) continue;
                Cosmetic equip = playerData.getEquip(cosmetic.getCosmeticType());
                if(equip == null){
                    CosmeticEquipEvent event = new CosmeticEquipEvent(player, cosmetic);
                    MagicCosmetics.getInstance().getServer().getPluginManager().callEvent(event);
                    if(event.isCancelled()) return;
                }else{
                    CosmeticChangeEquipEvent event = new CosmeticChangeEquipEvent(player, equip, cosmetic);
                    MagicCosmetics.getInstance().getServer().getPluginManager().callEvent(event);
                    if(event.isCancelled()) return;
                }
                if(colorHex != null){
                    org.bukkit.Color color = Color.hex2Rgb(colorHex);
                    cosmetic.setColor(color);
                }
                playerData.setCosmetic(cosmetic);
                if(plugin.equipMessage) {
                    for(String msg : plugin.getMessages().getStringList("use-cosmetic")) {
                        player.sendMessage(msg.replace("%id%", cosmetic.getId()).replace("%name%", cosmetic.getName()));
                    }
                    //sendMessage(player, plugin.prefix + plugin.getMessages().getString("use-cosmetic").replace("%id%", cosmetic.getId()).replace("%name%", cosmetic.getName()));
                }
                return;
            }
            for(String msg : plugin.getMessages().getStringList("not-have-cosmetic")) {
                player.sendMessage(msg);
            }
            //sendMessage(player, plugin.prefix + plugin.getMessages().getString("not-have-cosmetic"));
            return;
        }
        for(Cosmetic cos : playerData.getCosmetics().values()){
            if(!cos.getId().equalsIgnoreCase(cosmetic.getId())) continue;
            Cosmetic equip = playerData.getEquip(cosmetic.getCosmeticType());
            if(equip == null){
                CosmeticEquipEvent event = new CosmeticEquipEvent(player, cosmetic);
                MagicCosmetics.getInstance().getServer().getPluginManager().callEvent(event);
                if(event.isCancelled()) return;
            }else{
                CosmeticChangeEquipEvent event = new CosmeticChangeEquipEvent(player, equip, cosmetic);
                MagicCosmetics.getInstance().getServer().getPluginManager().callEvent(event);
                if(event.isCancelled()) return;
            }
            if(colorHex != null){
                org.bukkit.Color color = Color.hex2Rgb(colorHex);
                cosmetic.setColor(color);
            }
            playerData.setCosmetic(cosmetic);
            if(plugin.equipMessage) {
                sendMessage(player, plugin.prefix + plugin.getMessages().getString("use-cosmetic").replace("%id%", cosmetic.getId()).replace("%name%", cosmetic.getName()));
            }
            return;
        }
        sendMessage(player, plugin.prefix + plugin.getMessages().getString("not-have-cosmetic"));
    }

    public void equipCosmetic(Player player, String id, String colorHex, boolean force){
        PlayerData playerData = PlayerData.getPlayer(player);
        if(plugin.getUser() == null) return;
        if(force){
            Cosmetic cosmetic = Cosmetic.getCloneCosmetic(id);
            Cosmetic equip = playerData.getEquip(cosmetic.getCosmeticType());
            if(equip == null){
                CosmeticEquipEvent event = new CosmeticEquipEvent(player, cosmetic);
                MagicCosmetics.getInstance().getServer().getPluginManager().callEvent(event);
                if(event.isCancelled()) return;
            }else{
                CosmeticChangeEquipEvent event = new CosmeticChangeEquipEvent(player, equip, cosmetic);
                MagicCosmetics.getInstance().getServer().getPluginManager().callEvent(event);
                if(event.isCancelled()) return;
            }
            if(colorHex != null){
                org.bukkit.Color color = Color.hex2Rgb(colorHex);
                cosmetic.setColor(color);
            }
            playerData.setCosmetic(cosmetic);
            if(plugin.equipMessage) {
                sendMessage(player, plugin.prefix + plugin.getMessages().getString("use-cosmetic").replace("%id%", id).replace("%name%", cosmetic.getName()));
            }
        }
        if(plugin.isPermissions()){
            Cosmetic cosmetic = Cosmetic.getCloneCosmetic(id);
            if(cosmetic == null) {
                for(String msg : plugin.getMessages().getStringList("cosmetic-notfound")) {
                    player.sendMessage(msg);
                }
                //sendMessage(player, plugin.prefix + plugin.getMessages().getString("cosmetic-notfound"));
                return;
            }
            if(!cosmetic.hasPermission(player)) return;
            Cosmetic equip = playerData.getEquip(cosmetic.getCosmeticType());
            if(equip == null){
                CosmeticEquipEvent event = new CosmeticEquipEvent(player, cosmetic);
                MagicCosmetics.getInstance().getServer().getPluginManager().callEvent(event);
                if(event.isCancelled()) return;
            }else{
                CosmeticChangeEquipEvent event = new CosmeticChangeEquipEvent(player, equip, cosmetic);
                MagicCosmetics.getInstance().getServer().getPluginManager().callEvent(event);
                if(event.isCancelled()) return;
            }
            if(colorHex != null){
                org.bukkit.Color color = Color.hex2Rgb(colorHex);
                cosmetic.setColor(color);
            }
            playerData.setCosmetic(cosmetic);
            if(plugin.equipMessage) {
                sendMessage(player, plugin.prefix + plugin.getMessages().getString("use-cosmetic").replace("%id%", id).replace("%name%", cosmetic.getName()));
            }
            return;
        }
        for(Cosmetic cosmetic : playerData.getCosmetics().values()){
            if(!cosmetic.getId().equalsIgnoreCase(id)) continue;
            Cosmetic equip = playerData.getEquip(cosmetic.getCosmeticType());
            if(equip == null){
                CosmeticEquipEvent event = new CosmeticEquipEvent(player, cosmetic);
                MagicCosmetics.getInstance().getServer().getPluginManager().callEvent(event);
                if(event.isCancelled()) return;
            }else{
                CosmeticChangeEquipEvent event = new CosmeticChangeEquipEvent(player, equip, cosmetic);
                MagicCosmetics.getInstance().getServer().getPluginManager().callEvent(event);
                if(event.isCancelled()) return;
            }
            if(colorHex != null){
                org.bukkit.Color color = Color.hex2Rgb(colorHex);
                cosmetic.setColor(color);
            }
            playerData.setCosmetic(cosmetic);
            if(plugin.equipMessage) {
                sendMessage(player, plugin.prefix + plugin.getMessages().getString("use-cosmetic").replace("%id%", id).replace("%name%", cosmetic.getName()));
            }
            return;
        }
        for(String msg : plugin.getMessages().getStringList("not-have-cosmetic")) {
            player.sendMessage(msg);
        }
        //sendMessage(player, plugin.prefix + plugin.getMessages().getString("not-have-cosmetic"));
    }

    public void previewCosmetic(Player player, String id){
        PlayerData playerData = PlayerData.getPlayer(player);
        Cosmetic cosmetic = Cosmetic.getCosmetic(id);
        if(cosmetic == null){
            for(String msg : plugin.getMessages().getStringList("not-have-cosmetic")) {
                player.sendMessage(msg);
            }
            //sendMessage(player, plugin.prefix + plugin.getMessages().getString("not-have-cosmetic"));
            return;
        }
        if(plugin.getUser() == null) return;
        playerData.setPreviewCosmetic(cosmetic);
    }

    public void previewCosmetic(Player player, Cosmetic cosmetic){
        PlayerData playerData = PlayerData.getPlayer(player);
        if(cosmetic == null){
            for(String msg : plugin.getMessages().getStringList("not-have-cosmetic")) {
                player.sendMessage(msg);
            }
            //sendMessage(player, plugin.prefix + plugin.getMessages().getString("not-have-cosmetic"));
            return;
        }
        if(plugin.getUser() == null) return;
        playerData.setPreviewCosmetic(cosmetic);
    }

    public void openMenu(Player player, String id){
        PlayerData playerData = PlayerData.getPlayer(player);
        Menu menu = Menu.inventories.get(id);
        if(menu == null){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("not-exist-menu").replace("%id%", id));
            return;
        }
        if(plugin.getUser() == null) return;
        if(!menu.getPermission().isEmpty()){
            if(!player.hasPermission(menu.getPermission())){
                MagicCosmetics.getInstance().getCosmeticsManager().sendMessage(player, plugin.prefix + plugin.getMessages().getString("no-permission"));
                return;
            }
        }
        switch (menu.getContentMenu().getInventoryType()){
            case HAT:
                new HatMenu(playerData, menu).open();
                break;
            case BAG:
                new BagMenu(playerData, menu).open();
                break;
            case WALKING_STICK:
                new WStickMenu(playerData, menu).open();
                break;
            case BALLOON:
                new BalloonMenu(playerData, menu).open();
                break;
            case SPRAY:
                new SprayMenu(playerData, menu).open();
                break;
            case FREE:
                new FreeMenu(playerData, menu).open();
                break;
            case COLORED:
            case FREE_COLORED:
                openFreeMenuColor(player, id, Color.getColor("color1"));
                break;
            case TOKEN:
                ((TokenMenu)menu).getClone(playerData).open();
                break;
        }
    }

    public void openMenuColor(Player player, String id, Color color, Cosmetic cosmetic){
        PlayerData playerData = PlayerData.getPlayer(player);
        Menu menu = Menu.inventories.get(id);
        if(menu == null){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("not-exist-menu").replace("%id%", id));
            return;
        }
        if(!(menu instanceof ColoredMenu)) return;
        ColoredMenu coloredMenu = (ColoredMenu) menu;
        if(plugin.getUser() == null) return;
        switch (menu.getContentMenu().getInventoryType()){
            case HAT:
            case BAG:
            case WALKING_STICK:
            case FREE:
            case TOKEN:
            case BALLOON:
            case SPRAY:
            case FREE_COLORED:
                break;
            case COLORED:
                coloredMenu.getClone(playerData, color, cosmetic).open();
                break;
        }
    }

    public void openFreeMenuColor(Player player, String id, Color color){
        PlayerData playerData = PlayerData.getPlayer(player);
        Menu menu = Menu.inventories.get(id);
        if(menu == null){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("not-exist-menu").replace("%id%", id));
            return;
        }
        FreeColoredMenu freeColoredMenu = (FreeColoredMenu) menu;
        if(plugin.getUser() == null) return;
        switch (menu.getContentMenu().getInventoryType()){
            case HAT:
            case BAG:
            case WALKING_STICK:
            case FREE:
            case TOKEN:
            case BALLOON:
            case COLORED:
                break;
            case FREE_COLORED:
                freeColoredMenu.getClone(playerData, color).open();
                break;
        }
    }

    public void unSetCosmetic(Player player, CosmeticType cosmeticType){
        PlayerData playerData = PlayerData.getPlayer(player);
        Cosmetic equip = playerData.getEquip(cosmeticType);
        if(equip == null) return;
        if(plugin.getUser() == null) return;
        CosmeticUnEquipEvent event = new CosmeticUnEquipEvent(player, equip);
        MagicCosmetics.getInstance().getServer().getPluginManager().callEvent(event);
        if(event.isCancelled()) return;
        playerData.removePreviewEquip(equip.getId());
        playerData.removeEquip(equip.getId());
    }

    public void unSetCosmetic(Player player, String cosmeticId){
        PlayerData playerData = PlayerData.getPlayer(player);
        Cosmetic equip = playerData.getEquip(cosmeticId);
        if(equip == null) return;
        if(plugin.getUser() == null) return;
        CosmeticUnEquipEvent event = new CosmeticUnEquipEvent(player, equip);
        MagicCosmetics.getInstance().getServer().getPluginManager().callEvent(event);
        if(event.isCancelled()) return;
        playerData.removePreviewEquip(cosmeticId);
        playerData.removeEquip(cosmeticId);
    }

    public void unEquip(Player player, String type){
        CosmeticType cosmeticType;
        try{
            cosmeticType = CosmeticType.valueOf(type.toUpperCase());
            MagicAPI.UnEquipCosmetic(player, cosmeticType);
        }catch (IllegalArgumentException e){
            sendMessage(player, "");
        }
    }

    public void unEquipAll(CommandSender sender, Player player){
        if(!sender.hasPermission("magicosmetics.equip")){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        PlayerData playerData = PlayerData.getPlayer(player);
        if(plugin.getUser() == null) return;
        for(Cosmetic cosmetic : playerData.cosmeticsInUse()){
            if(cosmetic == null) continue;
            CosmeticUnEquipEvent event = new CosmeticUnEquipEvent(player, cosmetic);
            MagicCosmetics.getInstance().getServer().getPluginManager().callEvent(event);
            if(event.isCancelled()) continue;
            playerData.removePreviewEquip(cosmetic.getId());
            playerData.removeEquip(cosmetic.getId());
        }
    }

    public void unEquipAll(Player player){
        if(!player.hasPermission("magicosmetics.equip")){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        PlayerData playerData = PlayerData.getPlayer(player);
        if(plugin.getUser() == null) return;
        for(Cosmetic cosmetic : playerData.cosmeticsInUse()){
            if(cosmetic == null) continue;
            CosmeticUnEquipEvent event = new CosmeticUnEquipEvent(player, cosmetic);
            MagicCosmetics.getInstance().getServer().getPluginManager().callEvent(event);
            if(event.isCancelled()) continue;
            playerData.removePreviewEquip(cosmetic.getId());
            playerData.removeEquip(cosmetic.getId());
        }
    }

    public boolean unUseCosmetic(Player player, String cosmeticId){
        PlayerData playerData = PlayerData.getPlayer(player);
        Token token = Token.getTokenByCosmetic(cosmeticId);
        if(token == null) return false;
        if(plugin.getUser() == null) return false;
        if(!token.isExchangeable()) {
            return false;
        }
        if(!playerData.hasCosmeticById(cosmeticId)) return false;
        int freeSlot = playerData.getFreeSlotInventory();
        if(freeSlot == -1) return false;
        playerData.removeCosmetic(cosmeticId);
        if(playerData.isZone()) {
            playerData.getInventory().put(freeSlot, token.getItemStack().clone());
        }else{
            player.getInventory().addItem(token.getItemStack().clone());
        }
        for(String msg : plugin.getMessages().getStringList("change-cosmetic-to-token")){
            sendMessage(player, msg);
        }
        return true;
    }

    public void hideSelfCosmetic(Player player, CosmeticType cosmeticType){
        PlayerData playerData = PlayerData.getPlayer(player);
        if(cosmeticType != CosmeticType.BAG) return;
        Bag bag = (Bag) playerData.getEquip(cosmeticType);
        if(bag == null) return;
        bag.hideSelf(true);
        if(bag.isHide()){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("hide-backpack"));
            return;
        }
        sendMessage(player, plugin.prefix + plugin.getMessages().getString("show-backpack"));
    }

    public boolean hasPermission(CommandSender sender, String permission){
        return sender.hasPermission("magicosmetics.*") || sender.hasPermission(permission);
    }

    public void sendMessage(CommandSender sender, String string){
        if(sender instanceof ConsoleCommandSender){
            plugin.getLogger().info(string);
        }
        if(sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage(string);
        }
    }

}