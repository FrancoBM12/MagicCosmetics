package com.francobm.magicosmetics.managers;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.cache.*;
import com.francobm.magicosmetics.cache.balloons.Balloon;
import com.francobm.magicosmetics.cache.inventories.Menu;
import com.francobm.magicosmetics.cache.inventories.menus.*;
import com.francobm.magicosmetics.cache.items.Items;
import com.francobm.magicosmetics.events.CosmeticChangeEquipEvent;
import com.francobm.magicosmetics.events.CosmeticEquipEvent;
import com.francobm.magicosmetics.events.CosmeticUnequipEvent;
import com.francobm.magicosmetics.nms.NPC.NPC;
import com.francobm.magicosmetics.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

public class CosmeticsManager {
    private final MagicCosmetics plugin = MagicCosmetics.getInstance();
    private BukkitTask bagTask;
    private BukkitTask zoneCosmeticTask;
    private BukkitTask npcTask;

    public void runTasks(){
        bagTask = new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()){
                    PlayerCache playerCache = PlayerCache.getPlayer(player);
                    playerCache.activeCosmetics();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
        zoneCosmeticTask = new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()){
                    PlayerCache playerCache = PlayerCache.getPlayer(player);
                    playerCache.enterZone();
                }
            }
        }.runTaskTimer(plugin, 0L, 0L);
        npcTask = new BukkitRunnable() {
            int i = 0;
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()){
                    NPC npc = plugin.getVersion().getNPC(player);
                    if(npc == null) continue;
                    npc.lookNPC(player, i);
                }
                i = i+10;
            }
        }.runTaskTimer(plugin, 0L, plugin.getConfig().getLong("npc-rotation"));
        /*balloon = new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()){
                    PlayerCache playerCache = PlayerCache.getPlayer(player);
                    ((Balloon)playerCache.getBalloon()).update(player);
                    //playerCache.activePB();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);*/
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
        if(bagTask != null) bagTask.cancel();
        if(zoneCosmeticTask != null) zoneCosmeticTask.cancel();
        if(npcTask != null) npcTask.cancel();
    }

    public void reload(CommandSender sender){
        if(!sender.hasPermission("magicosmetics.reload")){
            if(sender instanceof Player) {
                sendMessage((Player) sender, plugin.prefix + plugin.getMessages().getString("no-permission"));
                return;
            }
            sender.sendMessage(plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
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
        for(String lines : plugin.getMessages().getStringList("bossbar")){
            BossBar boss = plugin.getServer().createBossBar(lines, BarColor.YELLOW, BarStyle.SOLID);
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
        plugin.prefix = plugin.getMessages().getString("prefix");
        plugin.gameMode = GameMode.SURVIVAL;
        if(plugin.getConfig().contains("leave-wardrobe-gamemode")) {
            try {
                plugin.gameMode = GameMode.valueOf(plugin.getConfig().getString("leave-wardrobe-gamemode").toUpperCase());
            }catch (IllegalArgumentException exception){
                plugin.getLogger().severe("Gamemode in config path: leave-wardrobe-gamemode Not Found!");
            }
        }
        plugin.equipMessage = false;
        if(plugin.getConfig().contains("equip-message")){
            plugin.equipMessage = plugin.getConfig().getBoolean("equip-message");
        }
        Cosmetic.loadCosmetics();
        Color.loadColors();
        Items.loadItems();
        Token.loadTokens();
        Sound.loadSounds();
        Menu.loadMenus();
        Zone.loadZones();
        PlayerCache.reload();
        plugin.getCosmeticsManager().runTasks();
        if(sender instanceof Player) {
            sendMessage((Player) sender, plugin.prefix + plugin.getMessages().getString("reload"));
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
        zone.setEnter(player.getLocation());
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
        if(plugin.getUser() == null) return;
        zone.setActive(true);
        sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-enable").replace("%name%", name));
    }

    public void exitZone(Player player){
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        playerCache.exitZone();
    }

    public void addCosmetic(Player player, String cosmeticId){
        Cosmetic cosmetic = Cosmetic.getCloneCosmetic(cosmeticId);
        if(cosmetic == null) return;
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        if(playerCache.getCosmeticById(cosmeticId) != null){
            return;
        }
        if(plugin.getUser() == null) return;
        playerCache.addCosmetic(cosmetic);
        for(String msg : plugin.getMessages().getStringList("change-token-to-cosmetic")){
            sendMessage(player, msg);
        }
    }

    public void addAllCosmetics(CommandSender sender, Player target){
        if(!sender.hasPermission("magicosmetics.cosmetics")){
            sendMessage(sender, plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        PlayerCache playerCache = PlayerCache.getPlayer(target);
        if(plugin.getUser() == null) return;
        if(playerCache.getCosmetics().size() == Cosmetic.cosmetics.size()){
            sendMessage(sender, plugin.prefix + plugin.getMessages().getString("already-all-cosmetics"));
            return;
        }
        for(String id : Cosmetic.cosmetics.keySet()){
            if(playerCache.getCosmeticById(id) != null) continue;
            Cosmetic cosmetic = Cosmetic.getCloneCosmetic(id);
            playerCache.addCosmetic(cosmetic);
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
        PlayerCache playerCache = PlayerCache.getPlayer(target);
        if(playerCache.getCosmeticById(cosmetic.getId()) != null){
            sendMessage(sender, plugin.prefix + plugin.getMessages().getString("already-cosmetic"));
            return;
        }
        playerCache.addCosmetic(cosmetic);
        sendMessage(sender, plugin.prefix + plugin.getMessages().getString("add-cosmetic"));
    }

    public void removeCosmetic(CommandSender sender, Player target, String cosmeticId){
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
        PlayerCache playerCache = PlayerCache.getPlayer(target);
        if(playerCache.getCosmeticById(cosmetic.getId()) == null){
            sendMessage(sender, plugin.prefix + plugin.getMessages().getString("not-have-cosmetic"));
            return;
        }
        playerCache.removeCosmetic(cosmeticId);
        sendMessage(sender, plugin.prefix + plugin.getMessages().getString("remove-cosmetic"));
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

    public void useCosmetic(Player player, String id){
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        if(plugin.getUser() == null) return;
        for(Cosmetic cosmetic : playerCache.getCosmetics().values()){
            if(!cosmetic.getId().equalsIgnoreCase(id)) continue;
            Cosmetic equip = playerCache.getEquip(cosmetic.getCosmeticType());
            if(equip == null){
                CosmeticEquipEvent event = new CosmeticEquipEvent(player, cosmetic);
                MagicCosmetics.getInstance().getServer().getPluginManager().callEvent(event);
                if(event.isCancelled()) return;
            }else{
                CosmeticChangeEquipEvent event = new CosmeticChangeEquipEvent(player, equip, cosmetic);
                MagicCosmetics.getInstance().getServer().getPluginManager().callEvent(event);
                if(event.isCancelled()) return;
            }
            playerCache.setCosmetic(cosmetic);
            if(plugin.equipMessage) {
                sendMessage(player, plugin.prefix + plugin.getMessages().getString("use-cosmetic").replace("%id%", id).replace("%name%", cosmetic.getName()));
            }
            return;
        }
        sendMessage(player, plugin.prefix + plugin.getMessages().getString("not-have-cosmetic"));
    }

    public void previewCosmetic(Player player, String id){
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        Cosmetic cosmetic = Cosmetic.getCosmetic(id);
        if(cosmetic == null){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("not-have-cosmetic"));
            return;
        }
        if(plugin.getUser() == null) return;
        playerCache.setPreviewCosmetic(cosmetic);
    }

    public void previewCosmetic(Player player, Cosmetic cosmetic){
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        if(cosmetic == null){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("not-have-cosmetic"));
            return;
        }
        if(plugin.getUser() == null) return;
        playerCache.setPreviewCosmetic(cosmetic);
    }

    public void openMenu(Player player, String id){
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        Menu menu = Menu.inventories.get(id);
        if(menu == null){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("not-exist-menu").replace("%id%", id));
            return;
        }
        if(plugin.getUser() == null) return;
        switch (menu.getContentMenu().getInventoryType()){
            case HAT:
                new HatMenu(playerCache, menu).open();
                break;
            case BAG:
                new BagMenu(playerCache, menu).open();
                break;
            case WALKING_STICK:
                new WStickMenu(playerCache, menu).open();
                break;
            case FREE:
                new FreeMenu(playerCache, menu).open();
                break;
            case BALLOON:
                new BalloonMenu(playerCache, menu).open();
                break;
            case COLORED:
                break;
            case FREE_COLORED:
                new FreeColoredMenu(playerCache, menu, Color.getColor("color1")).open();
                break;
            case TOKEN:
                new TokenMenu(playerCache, menu).open();
                break;
        }
    }

    public void openMenuColor(Player player, String id, Color color, Cosmetic cosmetic){
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        Menu menu = Menu.inventories.get(id);
        if(menu == null){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("not-exist-menu").replace("%id%", id));
            return;
        }
        if(plugin.getUser() == null) return;
        switch (menu.getContentMenu().getInventoryType()){
            case HAT:
            case BAG:
            case WALKING_STICK:
            case FREE:
            case TOKEN:
            case BALLOON:
            case FREE_COLORED:
                break;
            case COLORED:
                new ColoredMenu(playerCache, menu, color, cosmetic).open();
                break;
        }
    }

    public void openFreeMenuColor(Player player, String id, Color color, ItemStack itemStack){
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        Menu menu = Menu.inventories.get(id);
        if(menu == null){
            sendMessage(player, plugin.prefix + plugin.getMessages().getString("not-exist-menu").replace("%id%", id));
            return;
        }
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
                new FreeColoredMenu(playerCache, menu, color, itemStack).open();
                break;
        }
    }

    public void unSetCosmetic(Player player, CosmeticType cosmeticType){
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        Cosmetic equip = playerCache.getEquip(cosmeticType);
        if(equip == null) return;
        if(plugin.getUser() == null) return;
        CosmeticUnequipEvent event = new CosmeticUnequipEvent(player, equip);
        MagicCosmetics.getInstance().getServer().getPluginManager().callEvent(event);
        if(event.isCancelled()) return;
        playerCache.removePreviewEquip(equip.getId());
        playerCache.removeEquip(equip.getId());
    }

    public void unSetCosmetic(Player player, String cosmeticId){
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        Cosmetic equip = playerCache.getEquip(cosmeticId);
        if(equip == null) return;
        if(plugin.getUser() == null) return;
        CosmeticUnequipEvent event = new CosmeticUnequipEvent(player, equip);
        MagicCosmetics.getInstance().getServer().getPluginManager().callEvent(event);
        if(event.isCancelled()) return;
        playerCache.removePreviewEquip(cosmeticId);
        playerCache.removeEquip(cosmeticId);
    }

    public void unUseCosmetic(Player player, String cosmeticId){
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        Token token = Token.getTokenByCosmetic(cosmeticId);
        if(token == null) return;
        if(plugin.getUser() == null) return;
        if(playerCache.getCosmeticById(cosmeticId) != null) {
            playerCache.removeCosmetic(cosmeticId);
            playerCache.removeEquip(cosmeticId);
            if(playerCache.isZone()) {
                playerCache.getInventory().put(playerCache.getFreeSlotInventory(), token.getItemStack().clone());
            }else{
                player.getInventory().addItem(token.getItemStack().clone());
            }
            for(String msg : plugin.getMessages().getStringList("change-cosmetic-to-token")){
                sendMessage(player, msg);
            }
        }
    }

    public void hideSelfCosmetic(Player player, CosmeticType cosmeticType){
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        if(cosmeticType != CosmeticType.BAG) return;
        Bag bag = (Bag) playerCache.getEquip(cosmeticType);
        if(bag == null) return;
        bag.hideSelf(true);
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
            if (MagicCosmetics.getInstance().isMiniMessage()) {
                MagicCosmetics.getInstance().getMiniMessage().sendMessage(player, string);
                return;
            }
            player.sendMessage(string);
        }
    }

}