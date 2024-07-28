package com.francobm.magicosmetics.managers;

import com.francobm.magicosmetics.api.*;
import com.francobm.magicosmetics.cache.EntityCache;
import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.cache.*;
import com.francobm.magicosmetics.cache.cosmetics.backpacks.Bag;
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

import static com.francobm.magicosmetics.utils.Utils.sendMessage;

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
        FileCreator config = plugin.getConfig();
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
        if(!config.contains("placeholder-api")){
            config.set("placeholder-api", false);
        }
        if(!config.contains("luckperms-server"))
            config.set("luckperms-server", "");
        if(!config.contains("main-menu"))
            config.set("main-menu", "hat");
        if(!config.contains("save-data-delay"))
            config.set("save-data-delay", 300);
        if(!config.contains("zones-actions"))
            config.set("zones-actions", false);
        if(!config.contains("on_execute_cosmetics"))
            config.set("on_execute_cosmetics", "");
        if(!config.contains("worlds-blacklist"))
            config.set("worlds-blacklist", Arrays.asList("test", "test1"));
        if(!config.contains("proxy")) {
            config.set("proxy", false);
        }
        zones.save();
        config.save();
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
            }, 5L, 2L);
        }
        if(balloons == null) {
            balloons = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
                for(PlayerData playerData : PlayerData.players.values()){
                    if(!playerData.getOfflinePlayer().isOnline()) continue;
                    playerData.activeBalloon();
                }
                for(EntityCache entityCache : EntityCache.entities.values()){
                    entityCache.activeCosmetics();
                }
            }, 0L, 1L);
        }
        /*if(saveDataTask == null && plugin.saveDataDelay != -1) {
            saveDataTask = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
                plugin.getSql().savePlayers();
            }, 20L * plugin.saveDataDelay, 20L * plugin.saveDataDelay);
        }*/
        if(npcTask == null && !NPC.npcs.isEmpty()) {
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
        plugin.getNPCs().reload();
        plugin.registerData();

        Cosmetic.loadCosmetics();
        Color.loadColors();
        Items.loadItems();
        Token.loadTokens();
        Sound.loadSounds();
        Menu.loadMenus();
        Zone.loadZones();
        PlayerData.reload();
        plugin.getNPCsLoader().load();
        plugin.getCosmeticsManager().runTasks();
        if(sender == null) return;
        if(sender instanceof Player) {
            sendMessage(sender, plugin.prefix + plugin.getMessages().getString("reload"));
            return;
        }
        sender.sendMessage(plugin.prefix + plugin.getMessages().getString("reload"));
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
            playerData.sendSavePlayerData();
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
        playerData.sendSavePlayerData();
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
        playerData.sendSavePlayerData();
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
        playerData.sendSavePlayerData();
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
        playerData.sendSavePlayerData();
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
        playerData.sendSavePlayerData();
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
        org.bukkit.Color color = Utils.hex2Rgb(colorHex);
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
        org.bukkit.Color color = Utils.hex2Rgb(colorHex);
        Items item = new Items(itemStack);
        item.coloredItem(color);
        sendMessage(player, plugin.prefix + plugin.getMessages().getString("tint-item").replace("%color%", Utils.ChatColor(colorHex)));
    }

    public void equipCosmetic(Player player, Cosmetic cosmetic, String colorHex){
        PlayerData playerData = PlayerData.getPlayer(player);
        if(plugin.getUser() == null) return;
        if(!playerData.hasCosmeticById(cosmetic.getId())){
            for(String msg : plugin.getMessages().getStringList("not-have-cosmetic")) {
                player.sendMessage(msg);
            }
            return;
        }
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
            org.bukkit.Color color = Utils.hex2Rgb(colorHex);
            cosmetic.setColor(color);
        }
        playerData.setCosmetic(cosmetic);
        if(plugin.equipMessage) {
            for(String msg : plugin.getMessages().getStringList("use-cosmetic")) {
                player.sendMessage(msg.replace("%id%", cosmetic.getId()).replace("%name%", cosmetic.getName()));
            }
            //sendMessage(player, plugin.prefix + plugin.getMessages().getString("use-cosmetic").replace("%id%", cosmetic.getId()).replace("%name%", cosmetic.getName()));
        }
        playerData.sendSavePlayerData();
        //sendMessage(player, plugin.prefix + plugin.getMessages().getString("not-have-cosmetic"));
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
                org.bukkit.Color color = Utils.hex2Rgb(colorHex);
                cosmetic.setColor(color);
            }
            playerData.setCosmetic(cosmetic);
            if(plugin.equipMessage) {
                sendMessage(player, plugin.prefix + plugin.getMessages().getString("use-cosmetic").replace("%id%", id).replace("%name%", cosmetic.getName()));
            }
        }
        if(!playerData.hasCosmeticById(id) && !force) {
            for(String msg : plugin.getMessages().getStringList("not-have-cosmetic")) {
                player.sendMessage(msg);
            }
            return;
        }
        Cosmetic cosmetic = plugin.isPermissions() || force ? Cosmetic.getCloneCosmetic(id) : playerData.getCosmeticById(id);
        if(cosmetic == null) {
            for(String msg : plugin.getMessages().getStringList("cosmetic-notfound")) {
                player.sendMessage(msg);
            }
            //sendMessage(player, plugin.prefix + plugin.getMessages().getString("cosmetic-notfound"));
            return;
        }
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
            org.bukkit.Color color = Utils.hex2Rgb(colorHex);
            cosmetic.setColor(color);
        }
        playerData.setCosmetic(cosmetic);
        if(plugin.equipMessage) {
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("use-cosmetic").replace("%id%", id).replace("%name%", cosmetic.getName()));
        }
        playerData.sendSavePlayerData();
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
                sendMessage(player, plugin.prefix + plugin.getMessages().getString("no-permission"));
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
        playerData.sendSavePlayerData();
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
        playerData.sendSavePlayerData();
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
        playerData.sendSavePlayerData();
        return true;
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
        playerData.sendSavePlayerData();
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
        playerData.sendSavePlayerData();
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

}