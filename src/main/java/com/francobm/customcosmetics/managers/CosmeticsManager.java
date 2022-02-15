package com.francobm.customcosmetics.managers;

import com.francobm.customcosmetics.CustomCosmetics;
import com.francobm.customcosmetics.cache.*;
import com.francobm.customcosmetics.cache.inventories.Menu;
import com.francobm.customcosmetics.cache.inventories.menus.*;
import com.francobm.customcosmetics.cache.items.Items;
import com.francobm.customcosmetics.nms.NPC.NPC;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class CosmeticsManager {
    private final CustomCosmetics plugin = CustomCosmetics.getInstance();
    private BukkitTask bagTask;
    private BukkitTask zoneCosmeticTask;
    private BukkitTask npcTask;
    private BukkitTask balloon;

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
        balloon = new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()){
                    PlayerCache playerCache = PlayerCache.getPlayer(player);
                    playerCache.activeBalloon();
                    //playerCache.activePB();
                }
            }
        }.runTaskTimer(plugin, 0L, plugin.getConfig().getLong("balloons-updown"));
    }

    public void cancelTasks(){
        if(bagTask != null) bagTask.cancel();
        if(zoneCosmeticTask != null) zoneCosmeticTask.cancel();
        if(npcTask != null) npcTask.cancel();
        if(balloon != null) balloon.cancel();
    }

    public void reload(){
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
        plugin.prefix = plugin.getMessages().getString("prefix");
        Cosmetic.loadCosmetics();
        Color.loadColors();
        Items.loadItems();
        Token.loadTokens();
        Sound.loadSounds();
        Menu.loadMenus();
        Zone.loadZones();
        PlayerCache.reload();
        plugin.getCosmeticsManager().runTasks();
    }

    public void saveZone(Player player, String name){
        if(!player.hasPermission("customcosmetics.admin")){
            player.sendMessage(plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        if(Zone.getZone(name) == null){
            player.sendMessage(plugin.prefix + plugin.getMessages().getString("zone-not-exist"));
            return;
        }
        Zone.saveZone(name);
        player.sendMessage(plugin.prefix + plugin.getMessages().getString("zone-save").replace("%name%", name));
    }

    public void addZone(Player player, String name){
        if(!player.hasPermission("customcosmetics.admin")){
            player.sendMessage(plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        if(Zone.getZone(name) != null){
            player.sendMessage(plugin.prefix + plugin.getMessages().getString("zone-exist"));
            return;
        }
        Zone.addZone(name);
        player.sendMessage(plugin.prefix + plugin.getMessages().getString("zone-created").replace("%name%", name));
        giveCorn(player, name);
    }

    public void giveCorn(Player player, String name){
        if(!player.hasPermission("customcosmetics.admin")){
            player.sendMessage(plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        Zone zone = Zone.getZone(name);
        if(zone == null){
            player.sendMessage(plugin.prefix + plugin.getMessages().getString("zone-not-exist"));
            return;
        }
        zone.giveCorns(player);
        player.sendMessage(plugin.prefix + plugin.getMessages().getString("give-corns"));
    }

    public void setBalloonNPC(Player player, String name){
        if(!player.hasPermission("customcosmetics.admin")){
            player.sendMessage(plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        Zone zone = Zone.getZone(name);
        if(zone == null){
            player.sendMessage(plugin.prefix + plugin.getMessages().getString("zone-not-exist"));
            return;
        }
        zone.setBalloon(player.getLocation());
        player.sendMessage(plugin.prefix + plugin.getMessages().getString("set-balloon").replace("%name%", zone.getName()));
    }

    public void setZoneNPC(Player player, String name){
        if(!player.hasPermission("customcosmetics.admin")){
            player.sendMessage(plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        Zone zone = Zone.getZone(name);
        if(zone == null){
            player.sendMessage(plugin.prefix + plugin.getMessages().getString("zone-not-exist"));
            return;
        }
        zone.setNpc(player.getLocation());
        player.sendMessage(plugin.prefix + plugin.getMessages().getString("set-npc").replace("%name%", zone.getName()));
    }

    public void setZoneEnter(Player player, String name){
        if(!player.hasPermission("customcosmetics.admin")){
            player.sendMessage(plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        Zone zone = Zone.getZone(name);
        if(zone == null){
            player.sendMessage(plugin.prefix + plugin.getMessages().getString("zone-not-exist"));
            return;
        }
        zone.setEnter(player.getLocation());
        player.sendMessage(plugin.prefix + plugin.getMessages().getString("set-enter").replace("%name%", zone.getName()));
    }

    public void setZoneExit(Player player, String name){
        if(!player.hasPermission("customcosmetics.admin")){
            player.sendMessage(plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        Zone zone = Zone.getZone(name);
        if(zone == null){
            player.sendMessage(plugin.prefix + plugin.getMessages().getString("zone-not-exist"));
            return;
        }
        zone.setExit(player.getLocation());
        player.sendMessage(plugin.prefix + plugin.getMessages().getString("set-exit").replace("%name%", zone.getName()));
    }

    public void disableZone(Player player, String name){
        Zone zone = Zone.getZone(name);
        if(zone == null){
            player.sendMessage(plugin.prefix + plugin.getMessages().getString("zone-not-exist"));
            return;
        }
        zone.setActive(false);
        player.sendMessage(plugin.prefix + plugin.getMessages().getString("zone-disable").replace("%name%", name));
    }

    public void enableZone(Player player, String name){
        Zone zone = Zone.getZone(name);
        if(zone == null){
            player.sendMessage(plugin.prefix + plugin.getMessages().getString("zone-not-exist"));
            return;
        }
        zone.setActive(true);
        player.sendMessage(plugin.prefix + plugin.getMessages().getString("zone-enable").replace("%name%", name));
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
        playerCache.addCosmetic(cosmetic);
        for(String msg : plugin.getMessages().getStringList("change-token-to-cosmetic")){
            player.sendMessage(msg);
        }
    }

    public void addCosmetic(Player player, Player target, String cosmeticId){
        if(!player.hasPermission("customcosmetics.admin")){
            player.sendMessage(plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        Cosmetic cosmetic = Cosmetic.getCloneCosmetic(cosmeticId);
        if(cosmetic == null) {
            player.sendMessage(plugin.prefix + plugin.getMessages().getString("cosmetic-notfound"));
            return;
        }
        PlayerCache playerCache = PlayerCache.getPlayer(target);
        if(playerCache.getCosmeticById(cosmetic.getId()) != null){
            player.sendMessage(plugin.prefix + plugin.getMessages().getString("already-cosmetic"));
            return;
        }
        playerCache.addCosmetic(cosmetic);
        player.sendMessage(plugin.prefix + plugin.getMessages().getString("add-cosmetic"));
    }

    public void giveToken(Player player, Player target, String tokenId){
        if(!player.hasPermission("customcosmetics.admin")){
            player.sendMessage(plugin.prefix + plugin.getMessages().getString("no-permission"));
            return;
        }
        Token token = Token.getToken(tokenId);
        if(token == null) {
            player.sendMessage(plugin.prefix + plugin.getMessages().getString("not-exist-token").replace("%id%", tokenId));
            return;
        }
        if(target.getInventory().firstEmpty() == -1){
            target.getWorld().dropItemNaturally(target.getLocation(), token.getItemStack().clone());
            player.sendMessage(plugin.prefix + plugin.getMessages().getString("add-token"));
            return;
        }
        target.getInventory().addItem(token.getItemStack().clone());
        player.sendMessage(plugin.prefix + plugin.getMessages().getString("add-token"));
    }

    public void useCosmetic(Player player, String id){
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        for(Cosmetic cosmetic : playerCache.getCosmetics().values()){
            if(!cosmetic.getId().equalsIgnoreCase(id)) continue;
            playerCache.setCosmetic(cosmetic);
            player.sendMessage(plugin.prefix + plugin.getMessages().getString("use-cosmetic").replace("%id%", id));
            return;
        }
        player.sendMessage(plugin.prefix + plugin.getMessages().getString("not-have-cosmetic"));
    }

    public void previewCosmetic(Player player, String id){
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        Cosmetic cosmetic = Cosmetic.getCosmetic(id);
        if(cosmetic == null){
            player.sendMessage(plugin.prefix + plugin.getMessages().getString("not-have-cosmetic"));
            return;
        }
        playerCache.setPreviewCosmetic(cosmetic);
    }

    public void openMenu(Player player, String id){
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        Menu menu = Menu.inventories.get(id);
        if(menu == null){
            player.sendMessage(plugin.prefix + plugin.getMessages().getString("not-exist-menu").replace("%id%", id));
            return;
        }
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
            case TOKEN:
                new TokenMenu(playerCache, menu).open();
                break;
        }
    }

    public void openMenuColor(Player player, String id, Color color, Cosmetic cosmetic){
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        Menu menu = Menu.inventories.get(id);
        if(menu == null){
            player.sendMessage(plugin.prefix + plugin.getMessages().getString("not-exist-menu").replace("%id%", id));
            return;
        }
        switch (menu.getContentMenu().getInventoryType()){
            case HAT:
            case BAG:
            case WALKING_STICK:
            case FREE:
            case TOKEN:
            case BALLOON:
                break;
            case COLORED:
                new ColoredMenu(playerCache, menu, color, cosmetic).open();
                break;
        }
    }

    public void unSetCosmetic(Player player, String cosmeticId){
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        playerCache.removePreviewEquip(cosmeticId);
        playerCache.removeEquip(cosmeticId);
    }

    public void unUseCosmetic(Player player, String cosmeticId){
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        Token token = Token.getTokenByCosmetic(cosmeticId);
        if(token == null) return;
        if(playerCache.getCosmeticById(cosmeticId) != null) {
            playerCache.removeCosmetic(cosmeticId);
            playerCache.removeEquip(cosmeticId);
            playerCache.getInventory().put(playerCache.getFreeSlotInventory(), token.getItemStack().clone());
            for(String msg : plugin.getMessages().getStringList("change-cosmetic-to-token")){
                player.sendMessage(msg);
            }
        }
    }

}