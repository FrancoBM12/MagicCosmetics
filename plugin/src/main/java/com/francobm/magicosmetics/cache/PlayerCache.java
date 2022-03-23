package com.francobm.magicosmetics.cache;

import com.francobm.magicosmetics.cache.balloons.Balloon;
import com.francobm.magicosmetics.events.*;
import com.francobm.magicosmetics.nms.NPC.ItemSlot;
import com.francobm.magicosmetics.nms.NPC.NPC;
import com.francobm.magicosmetics.utils.Utils;
import com.francobm.magicosmetics.utils.XMaterial;
import com.francobm.magicosmetics.MagicCosmetics;
import org.bukkit.*;
import org.bukkit.Color;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PlayerCache {
    public static Map<UUID, PlayerCache> players = new HashMap<>();
    private final UUID uuid;
    private Cosmetic hat;
    private Cosmetic bag;
    private Cosmetic wStick;
    private Cosmetic balloon;
    private final Map<String, Cosmetic> cosmetics;
    private Cosmetic previewHat;
    private Cosmetic previewBag;
    private Cosmetic previewWStick;
    private Cosmetic previewBalloon;
    private boolean isZone;
    private boolean sneak;
    private boolean spectator;
    private Zone zone;
    private final Map<Integer, ItemStack> inventory;

    public PlayerCache(UUID uuid){
        this.uuid = uuid;
        this.hat = null;
        this.bag = null;
        this.wStick = null;
        this.balloon = null;
        this.cosmetics = new HashMap<>();
        this.previewHat = null;
        this.previewBag = null;
        this.previewWStick = null;
        this.previewBalloon = null;
        this.isZone = false;
        this.sneak = false;
        this.spectator = false;
        this.zone = null;
        this.inventory = new HashMap<>();
    }

    public static PlayerCache getPlayer(Player player){
        if(!players.containsKey(player.getUniqueId())){
            PlayerCache playerCache = new PlayerCache(player.getUniqueId());
            players.put(player.getUniqueId(), playerCache);
            return playerCache;
        }
        return players.get(player.getUniqueId());
    }

    public static void reload(){
        for(Player player : Bukkit.getOnlinePlayers()){
            PlayerCache playerCache = getPlayer(player);
            playerCache.clearCosmeticsInUse();
        }
    }

    public int getCosmeticCount(CosmeticType cosmeticType){
        int i = 0;
        for(Cosmetic cosmetic : cosmetics.values()){
            if(cosmetic.getCosmeticType() != cosmeticType) continue;
            i++;
        }
        return i;
    }

    public Cosmetic getPreviewBalloon() {
        return previewBalloon;
    }

    public void setPreviewBalloon(Cosmetic previewBalloon) {
        this.previewBalloon = previewBalloon;
    }

    public Cosmetic getPreviewHat() {
        return previewHat;
    }

    public void setPreviewHat(Cosmetic previewHat) {
        this.previewHat = previewHat;
    }

    public Cosmetic getPreviewBag() {
        return previewBag;
    }

    public void setPreviewBag(Cosmetic previewBag) {
        this.previewBag = previewBag;
    }

    public Cosmetic getPreviewWStick() {
        return previewWStick;
    }

    public void setPreviewWStick(Cosmetic previewWStick) {
        this.previewWStick = previewWStick;
    }

    public static void removePlayer(PlayerCache player){
        players.remove(player.getUuid());
    }

    public Cosmetic getHat() {
        return hat;
    }

    public void setHat(Cosmetic hat) {
        this.hat = hat;
    }

    public Cosmetic getBag() {
        return bag;
    }

    public void setBag(Cosmetic bag) {
        this.bag = bag;
    }

    public Cosmetic getWStick() {
        return wStick;
    }

    public void setWStick(Cosmetic wStick) {
        this.wStick = wStick;
    }

    public Cosmetic getBalloon() {
        return balloon;
    }

    public void setBalloon(Cosmetic balloon) {
        this.balloon = balloon;
    }

    public void removeCosmetic(String cosmeticId){
        Cosmetic cosmetic = getCosmeticById(cosmeticId);
        if(cosmetic == null) return;
        removeEquip(cosmeticId);
        removePreviewEquip(cosmeticId);
        cosmetic.clear(getOfflinePlayer().getPlayer());
        cosmetics.remove(cosmeticId);
    }

    public void setCosmetic(Cosmetic cosmetic){
        if(cosmetic == null) return;

        switch (cosmetic.getCosmeticType()){
            case HAT:
                if(getHat() == null){
                    CosmeticEquipEvent event = new CosmeticEquipEvent(getOfflinePlayer().getPlayer(), cosmetic);
                    MagicCosmetics.getInstance().getServer().getPluginManager().callEvent(event);
                    if(event.isCancelled()) return;
                }else{
                    CosmeticChangeEquipEvent event = new CosmeticChangeEquipEvent(getOfflinePlayer().getPlayer(), getHat(), cosmetic);
                    MagicCosmetics.getInstance().getServer().getPluginManager().callEvent(event);
                    if(event.isCancelled()) return;
                }
                clearHat();
                setHat(cosmetic);
                break;
            case BAG:
                clearBag();
                setBag(cosmetic);
                break;
            case WALKING_STICK:
                clearWStick();
                setWStick(cosmetic);
                break;
            case BALLOON:
                clearBalloon();
                setBalloon(cosmetic);
                break;
        }
    }

    public void setPreviewCosmetic(Cosmetic cosmetic){
        if(cosmetic == null) return;

        switch (cosmetic.getCosmeticType()){
            case HAT:
                clearPreviewHat();
                setPreviewHat(cosmetic);
                activePreviewHat();
                break;
            case BAG:
                clearPreviewBag();
                setPreviewBag(cosmetic);
                activePreviewBag();
                break;
            case WALKING_STICK:
                clearPreviewWStick();
                setPreviewWStick(cosmetic);
                activePreviewWStick();
                break;
            case BALLOON:
                clearPreviewBalloon();
                setPreviewBalloon(cosmetic);
                activePreviewBalloon();
                break;
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public OfflinePlayer getOfflinePlayer(){
        return Bukkit.getOfflinePlayer(uuid);
    }

    public Map<String, Cosmetic> getCosmetics() {
        return cosmetics;
    }

    public Cosmetic getCosmeticByName(String name){
        for(Cosmetic cosmetic : cosmetics.values()){
            if(cosmetic.getName().equalsIgnoreCase(name)){
                return cosmetic;
            }
        }
        return null;
    }

    public Cosmetic getCosmeticById(String id){
        return cosmetics.get(id);
    }

    public String saveCosmetics(){
        List<String> ids = new ArrayList<>();
        for(Cosmetic cosmetic : cosmetics.values()){
            if(ids.contains(cosmetic.getId())) continue;
            if(cosmetic.getColor() != null){
                ids.add(cosmetic.getId()+"|"+cosmetic.getColor().asRGB());
                continue;
            }
            ids.add(cosmetic.getId());
        }
        if(ids.isEmpty()) return "";
        return String.join(",", ids);
    }

    public void loadCosmetics(String ids){
        if(ids.isEmpty()) return;
        List<String> cosmetics = new ArrayList<>(Arrays.asList(ids.split(",")));
        this.cosmetics.clear();
        for(String cosmetic : cosmetics){
            String[] color = cosmetic.split("\\|");
            if(color.length > 1){
                Cosmetic cosmetic1 = Cosmetic.getCosmetic(color[0]);
                if(cosmetic1 == null) continue;
                if(this.cosmetics.containsKey(color[0])) continue;
                cosmetic1.setColor(Color.fromRGB(Integer.parseInt(color[1])));
                addCosmetic(cosmetic1);
                continue;
            }
            Cosmetic cosmetic1 = Cosmetic.getCosmetic(cosmetic);
            if(cosmetic1 == null) continue;
            if(this.cosmetics.containsKey(cosmetic)) continue;
            addCosmetic(cosmetic1);
        }
    }

    public void addCosmetic(Cosmetic cosmetic){
        if(cosmetic == null) return;

        switch (cosmetic.getCosmeticType()){
            case HAT:
                Hat hat = (Hat) cosmetic;
                cosmetics.put(cosmetic.getId(), new Hat(hat.getId(), hat.getName(), hat.getItemStack().clone(), hat.getModelData(), hat.isColored(), hat.getCosmeticType(), hat.getColor(), hat.isOverlaps()));
                return;
            case BAG:
                Bag bag = (Bag) cosmetic;
                cosmetics.put(cosmetic.getId(), new Bag(bag.getId(), bag.getName(), bag.getItemStack().clone(), bag.getModelData(), bag.getModelDataForMe(), bag.isColored(), bag.getSpace(), bag.getCosmeticType(), bag.getColor()));
                return;
            case WALKING_STICK:
                WStick wStick = (WStick) cosmetic;
                cosmetics.put(cosmetic.getId(), new WStick(wStick.getId(), wStick.getName(), wStick.getItemStack().clone(), wStick.getModelData(), wStick.isColored(), wStick.getCosmeticType(), wStick.getColor()));
                break;
            case BALLOON:
                Balloon balloon = (Balloon) cosmetic;
                cosmetics.put(cosmetic.getId(), new Balloon(balloon.getId(), balloon.getName(), balloon.getItemStack().clone(), balloon.getModelData(), balloon.isColored(), balloon.getSpace(), balloon.getCosmeticType(), balloon.getColor(), balloon.isRotation(), balloon.getRotationType(), balloon.getBalloonEngine()));
                break;
        }
    }

    public void removeHat(){
        clearHat();
        hat = null;
    }

    public void removeBag(){
        clearBag();
        bag = null;
    }

    public void removeWStick(){
        clearWStick();
        wStick = null;
    }

    public void removeBalloon(){
        clearBalloon();
        balloon = null;
    }

    public void removePreviewHat(){
        clearPreviewHat();
        previewHat = null;
    }

    public void removePreviewBag(){
        clearPreviewBag();
        previewBag = null;
    }

    public void removePreviewWStick(){
        clearPreviewWStick();
        previewWStick = null;
    }

    public void removePreviewBalloon(){
        clearPreviewBalloon();
        previewBalloon = null;
    }

    public Cosmetic getEquip(CosmeticType cosmeticType){
        switch (cosmeticType){
            case HAT:
                if(hat == null) return null;
                return hat;
            case BAG:
                if(bag == null) return null;
                return bag;
            case WALKING_STICK:
                if(wStick == null) return null;
                return wStick;
            case BALLOON:
                if(balloon == null) return null;
                return balloon;
        }
        return null;
    }

    public Cosmetic getEquip(String id){
        if(hat != null){
            if(hat.getId().equalsIgnoreCase(id)){
                return hat;
            }
        }
        if(bag != null){
            if(bag.getId().equalsIgnoreCase(id)){
                return bag;
            }
        }
        if(wStick != null){
            if(wStick.getId().equalsIgnoreCase(id)){
                return wStick;
            }
        }
        if(balloon != null){
            if(balloon.getId().equalsIgnoreCase(id)){
                return balloon;
            }
        }
        return null;
    }

    public void removeEquip(String id){
        if(hat != null){
            if(hat.getId().equalsIgnoreCase(id)){
                removeHat();
                return;
            }
        }
        if(bag != null){
            if(bag.getId().equalsIgnoreCase(id)){
                removeBag();
                return;
            }
        }
        if(wStick != null){
            if(wStick.getId().equalsIgnoreCase(id)){
                removeWStick();
                return;
            }
        }
        if(balloon != null){
            if(balloon.getId().equalsIgnoreCase(id)){
                removeBalloon();
            }
        }
    }

    public void removePreviewEquip(String id){
        if(previewHat != null){
            if(previewHat.getId().equalsIgnoreCase(id)){
                removePreviewHat();
            }
        }
        if(previewBag != null){
            if(previewBag.getId().equalsIgnoreCase(id)){
                removePreviewBag();
            }
        }
        if(previewWStick != null){
            if(previewWStick.getId().equalsIgnoreCase(id)){
                removePreviewWStick();
            }
        }
        if(previewBalloon != null){
            if(previewBalloon.getId().equalsIgnoreCase(id)){
                removePreviewBalloon();
            }
        }
    }

    public void clearHat(){
        Player player = getOfflinePlayer().getPlayer();
        if(hat == null){
            return;
        }
        hat.clear(player);
    }

    public void clearBag(){
        Player player = getOfflinePlayer().getPlayer();
        if(bag == null){
            return;
        }
        bag.clear(player);
    }

    public void clearWStick(){
        Player player = getOfflinePlayer().getPlayer();
        if(wStick == null){
            return;
        }
        wStick.clear(player);
    }

    public void clearBalloon(){
        Player player = getOfflinePlayer().getPlayer();
        if(balloon == null){
            return;
        }
        balloon.clear(player);
    }

    public void clearPreviewHat(){
        Player player = getOfflinePlayer().getPlayer();
        if(previewHat == null){
            return;
        }
        NPC npc = MagicCosmetics.getInstance().getVersion().getNPC(player);
        if(npc == null) return;
        npc.equipNPC(player, ItemSlot.HELMET, XMaterial.AIR.parseItem());
    }

    public void clearPreviewBag(){
        Player player = getOfflinePlayer().getPlayer();
        if(previewBag == null){
            return;
        }
        NPC npc = MagicCosmetics.getInstance().getVersion().getNPC(player);
        if(npc == null) return;
        npc.armorStandSetItem(player, XMaterial.AIR.parseItem());
    }

    public void clearPreviewWStick(){
        Player player = getOfflinePlayer().getPlayer();
        if(previewWStick == null){
            return;
        }
        NPC npc = MagicCosmetics.getInstance().getVersion().getNPC(player);
        if(npc == null) return;
        npc.equipNPC(player, ItemSlot.OFF_HAND, XMaterial.AIR.parseItem());
    }

    public void clearPreviewBalloon(){
        Player player = getOfflinePlayer().getPlayer();
        if(previewBalloon == null){
            return;
        }
        NPC npc = MagicCosmetics.getInstance().getVersion().getNPC(player);
        if(npc == null) return;
        npc.removeBalloon(player);
    }

    public void activeHat(){
        Player player = getOfflinePlayer().getPlayer();
        if(hat == null){
            return;
        }
        if(isZone) return;
        if(player.isInvisible() || player.hasPotionEffect(PotionEffectType.INVISIBILITY)){
            clearHat();
            return;
        }
        hat.active(player);
    }

    public void activeBag(){
        Player player = getOfflinePlayer().getPlayer();
        if(bag == null){
            return;
        }
        if(isZone) return;
        if(player.isSwimming() || player.getPose() == Pose.SWIMMING || player.isGliding() || player.isInvisible() || player.hasPotionEffect(PotionEffectType.INVISIBILITY)){
            clearBag();
            return;
        }
        bag.active(player);
    }

    public void activeWStick(){
        Player player = getOfflinePlayer().getPlayer();
        if(wStick == null){
            return;
        }
        if(isZone) return;
        if(player.isInvisible() || player.hasPotionEffect(PotionEffectType.INVISIBILITY)){
            clearWStick();
            return;
        }
        wStick.active(player);
    }

    public void activeBalloon(){
        Player player = getOfflinePlayer().getPlayer();
        if(balloon == null){
            return;
        }
        if(isZone) return;
        if(player.isInvisible() || player.hasPotionEffect(PotionEffectType.INVISIBILITY)){
            clearBalloon();
            return;
        }
        balloon.active(player);
    }

    public void activePreviewHat(){
        Player player = getOfflinePlayer().getPlayer();
        if(previewHat == null){
            return;
        }
        NPC npc = MagicCosmetics.getInstance().getVersion().getNPC(player);
        if(npc == null) return;
        npc.equipNPC(player, ItemSlot.HELMET, previewHat.getItemColor(player));
    }

    public void activePreviewBag(){
        Player player = getOfflinePlayer().getPlayer();
        if(previewBag == null){
            return;
        }
        NPC npc = MagicCosmetics.getInstance().getVersion().getNPC(player);
        if(npc == null) return;
        npc.armorStandSetItem(player, previewBag.getItemColor(player));
    }

    public void activePreviewWStick(){
        Player player = getOfflinePlayer().getPlayer();
        if(previewWStick == null){
            return;
        }
        NPC npc = MagicCosmetics.getInstance().getVersion().getNPC(player);
        if(npc == null) return;
        npc.equipNPC(player, ItemSlot.OFF_HAND, previewWStick.getItemColor(player));
    }

    public void activePB(){
        Player player = getOfflinePlayer().getPlayer();
        if(previewBalloon == null){
            return;
        }
        NPC npc = MagicCosmetics.getInstance().getVersion().getNPC(player);
        if(npc == null) return;
        npc.animation(player);
    }

    public void activePreviewBalloon(){
        Player player = getOfflinePlayer().getPlayer();
        if(previewBalloon == null){
            return;
        }
        NPC npc = MagicCosmetics.getInstance().getVersion().getNPC(player);
        if(npc == null) return;
        Zone zone = getZone();
        if(zone == null) return;
        Location location;
        if(zone.getBalloon() == null){
            location = npc.getEntity().getLocation();
        }else{
            location = zone.getBalloon();
        }
        npc.balloonNPC(player, location, previewBalloon.getItemColor(player));
        npc.balloonSetItem(player, previewBalloon.getItemColor(player));
    }

    public void activeCosmetics(){
        activeHat();
        activeBag();
        activeWStick();
        activeBalloon();
        activePB();
    }

    public void clearCosmeticsInUse(){
        clearBalloon();
        clearBag();
        clearHat();
        clearWStick();
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public Zone getZone(){
        return zone;
    }

    public boolean isSneak() {
        return sneak;
    }

    public void setSneak(boolean sneak) {
        this.sneak = sneak;
    }

    public boolean isZone() {
        return isZone;
    }

    private boolean removeHelmet(){
        Player player = getOfflinePlayer().getPlayer();
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        ItemStack helmet = player.getInventory().getHelmet();
        if(hat == null){
            if(helmet != null){
                if(player.getInventory().firstEmpty() == -1){
                    plugin.getCosmeticsManager().sendMessage(player,plugin.prefix + plugin.getMessages().getString("zone-exit-by-helmet"));
                    return false;
                }
                //player.getInventory().setHelmet(null);
                //player.getInventory().addItem(helmet.clone());
            }
            return true;
        }
        if(helmet != null) {
            if (!hat.isCosmetic(helmet)) {
                if (player.getInventory().firstEmpty() == -1) {
                    plugin.getCosmeticsManager().sendMessage(player,plugin.prefix + plugin.getMessages().getString("zone-exit-by-helmet"));
                    return false;
                }
                player.getInventory().setHelmet(null);
                player.getInventory().addItem(helmet.clone());
                return true;
            }
        }
        return true;
    }

    private boolean removeOffHand(){
        Player player = getOfflinePlayer().getPlayer();
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        ItemStack offHand = player.getInventory().getItemInOffHand();
        if(wStick == null) {
            if (offHand.getType() != XMaterial.AIR.parseMaterial()) {
                if (player.getInventory().firstEmpty() == -1) {
                    plugin.getVersion().sendSound(player, Sound.getSound("on_enter_zone_error"));
                    plugin.getCosmeticsManager().sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-exit-by-offhand"));
                    return false;
                }
            }
            return true;
        }
        /*if(wStick == null){
            if(offHand.getType() != XMaterial.AIR.parseMaterial()){
                if(player.getInventory().firstEmpty() == -1){
                    plugin.getVersion().sendSound(player, Sound.getSound("on_enter_zone_error"));
                    plugin.getCosmeticsManager().sendMessage(player,plugin.prefix + plugin.getMessages().getString("zone-exit-by-offhand"));
                    return false;
                }
                player.getInventory().setItemInOffHand(null);
                player.getInventory().addItem(offHand.clone());
            }
            return true;
        }*/
        if(offHand.getType() != XMaterial.AIR.parseMaterial()) {
            if(!wStick.isCosmetic(offHand)){
                if(player.getInventory().firstEmpty() == -1){
                    plugin.getVersion().sendSound(player, Sound.getSound("on_enter_zone_error"));
                    plugin.getCosmeticsManager().sendMessage(player,plugin.prefix + plugin.getMessages().getString("zone-exit-by-offhand"));
                    return false;
                }
                player.getInventory().setItemInOffHand(null);
                player.getInventory().addItem(offHand.clone());
                return true;
            }
        }
        return true;
    }

    public void enterZone(){
        Zone zone = getZone();
        if(zone == null) {
            for(Zone z : Zone.zones.values()){
                if(!z.isInZone(getOfflinePlayer().getPlayer().getLocation().getBlock())) continue;
                setZone(z);
                break;
            }
            return;
        }
        if(!zone.isActive()){
            isZone = false;
            return;
        }
        Player player = getOfflinePlayer().getPlayer();
        if(!getOfflinePlayer().isOnline()) return;
        if(isZone) {
            if(spectator) {
                if (player.getSpectatorTarget() == null) {
                    if(player.getGameMode() == GameMode.SPECTATOR) {
                        player.setSpectatorTarget(zone.getSpec());
                    }
                }
            }
            return;
        }
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        ZoneEnterEvent event = new ZoneEnterEvent(player, zone);
        plugin.getServer().getPluginManager().callEvent(event);
        if(event.isCancelled()) {
            player.teleport(zone.getExit());
            return;
        }
        plugin.getVersion().sendSound(player, Sound.getSound("on_enter_zone"));
        plugin.getVersion().setSpectator(player);
        Utils.hidePlayer(player);
        String title = plugin.getMessages().getString("title-zone");
        if(player.getGameMode() != GameMode.SPECTATOR){
            exitZone();
            return;
        }
        if(plugin.isItemsAdder()){
            title = plugin.getItemsAdder().replaceFontImages(title);
        }
        if(plugin.isOraxen()){
            title = plugin.getOraxen().replaceFontImages(title);
        }
        player.sendTitle(title, "", 15, 7, 15);
        new BukkitRunnable() {
            @Override
            public void run() {
                if(!removeHelmet()){
                    ZoneExitEvent event = new ZoneExitEvent(player, zone, Reason.ITEM_IN_HELMET);
                    plugin.getServer().getPluginManager().callEvent(event);
                    isZone = false;
                    sneak = false;
                    spectator = false;
                    player.teleport(zone.getExit());
                    setZone(null);
                    player.setGameMode(GameMode.SURVIVAL);
                    cancel();
                    return;
                }
                if(!removeOffHand()){
                    ZoneExitEvent event = new ZoneExitEvent(player, zone, Reason.ITEM_IN_OFF_HAND);
                    plugin.getServer().getPluginManager().callEvent(event);
                    isZone = false;
                    sneak = false;
                    spectator = false;
                    player.teleport(zone.getExit());
                    setZone(null);
                    player.setGameMode(GameMode.SURVIVAL);
                    cancel();
                    return;
                }
                for(BossBar bossBar : plugin.getBossBar()){
                    if(bossBar.getPlayers().contains(player)) continue;
                    bossBar.addPlayer(player);
                }
                saveItems();
                if(player.getGameMode() == GameMode.SPECTATOR) {
                    player.setSpectatorTarget(zone.getSpec());
                    spectator = true;
                }
                plugin.getVersion().createNPC(player, zone.getNpc());
                plugin.getVersion().getNPC(player).spawnPunch(player, zone.getEnter());
                player.teleport(zone.getEnter());
                setPreviewCosmetic(hat);
                setPreviewCosmetic(bag);
                setPreviewCosmetic(wStick);
                setPreviewCosmetic(balloon);
            }
        }.runTaskLater(plugin, 12);
        clearHat();
        clearBag();
        clearWStick();
        clearBalloon();
        isZone = true;
    }

    public void exitZoneSync(){
        Zone zone = getZone();
        if(zone == null) return;
        if(!isZone) return;
        if(sneak) return;
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        Player player = getOfflinePlayer().getPlayer();
        Utils.showPlayer(player);
        sneak = true;
        spectator = false;
        isZone = false;
        for(BossBar bossBar : plugin.getBossBar()){
            bossBar.removePlayer(player);
        }
        loadItems();
        player.setGameMode(plugin.gameMode);
        plugin.getVersion().removeNPC(player);
        player.teleport(zone.getExit());
        setZone(null);
    }

    public void exitZone(){
        Zone zone = getZone();
        if(zone == null) return;
        if(!isZone) return;
        if(sneak) return;
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        Player player = getOfflinePlayer().getPlayer();
        ZoneExitEvent event = new ZoneExitEvent(player, zone, Reason.NORMAL);
        plugin.getServer().getPluginManager().callEvent(event);
        if(event.isCancelled()) return;
        String title = plugin.getMessages().getString("title-zone");
        if(plugin.isItemsAdder()){
            title = plugin.getItemsAdder().replaceFontImages(title);
        }
        if(plugin.isOraxen()){
            title = plugin.getOraxen().replaceFontImages(title);
        }
        player.sendTitle(title, "", 15, 7, 15);
        Utils.showPlayer(player);
        sneak = true;
        new BukkitRunnable() {
            @Override
            public void run() {
                for(BossBar bossBar : plugin.getBossBar()){
                    bossBar.removePlayer(player);
                }
                loadItems();
                player.setGameMode(plugin.gameMode);
                plugin.getVersion().sendSound(player, Sound.getSound("on_exit_zone"));
                plugin.getVersion().removeNPC(player);
                int count = 0;
                if(previewHat != null){
                    if(getCosmeticById(previewHat.getId()) == null){
                        count++;
                    }
                    setPreviewHat(null);
                }
                if(previewBag != null){
                    if(getCosmeticById(previewBag.getId()) == null){
                        count++;
                    }
                    setPreviewBag(null);
                }
                if(previewWStick != null){
                    if(getCosmeticById(previewWStick.getId()) == null){
                        count++;
                    }
                    setPreviewWStick(null);
                }
                if(previewBalloon != null){
                    if(getCosmeticById(previewBalloon.getId()) == null){
                        count++;
                    }
                    setPreviewBalloon(null);
                }
                if(count != 0){
                    if(count == 4){
                        plugin.getCosmeticsManager().sendMessage(player,plugin.prefix + plugin.getMessages().getString("exit-all-cosmetics"));
                        isZone = false;
                        sneak = false;
                        spectator = false;
                        player.teleport(zone.getExit());
                        setZone(null);
                        return;
                    }
                    plugin.getCosmeticsManager().sendMessage(player,plugin.prefix + plugin.getMessages().getString("exit-some-cosmetics").replace("%count%", String.valueOf(count)));
                }
                isZone = false;
                sneak = false;
                spectator = false;
                player.teleport(zone.getExit());
                setZone(null);
            }
        }.runTaskLater(plugin, 17);
    }

    public Token getTokenInPlayer(){
        Player player = getOfflinePlayer().getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();
        if(mainHand.getType() != XMaterial.AIR.parseMaterial()){
            Token token = Token.getTokenByItem(mainHand);
            if(token != null){
                return token;
            }
        }
        if(offHand.getType() != XMaterial.AIR.parseMaterial()){
            Token token = Token.getTokenByItem(offHand);
            if(token != null){
                return token;
            }
        }
        for(int i = 0; i < 8; i++){
            ItemStack itemStack = player.getInventory().getItem(i);
            Token token = Token.getTokenByItem(itemStack);
            if(token == null) continue;
            return token;
        }
        return null;
    }

    public boolean removeTokenInPlayer(){
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        Player player = getOfflinePlayer().getPlayer();
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();
        if(mainHand.getType() != XMaterial.AIR.parseMaterial()){
            Token token = Token.getTokenByItem(mainHand);
            if(token != null){
                if(mainHand.getAmount() < token.getItemStack().getAmount()){
                    plugin.getCosmeticsManager().sendMessage(player, plugin.prefix + plugin.getMessages().getString("insufficient-tokens"));
                    return false;
                }
                if(playerCache.getCosmeticById(token.getCosmetic()) != null){
                    plugin.getCosmeticsManager().sendMessage(player, plugin.prefix + plugin.getMessages().getString("already-token"));
                    return false;
                }
                if(mainHand.getAmount() > token.getItemStack().getAmount() && token.getItemStack().getAmount() > 1){
                    ItemStack newItem = token.getItemStack().clone();
                    newItem.setAmount(mainHand.getAmount() - token.getItemStack().getAmount());
                    player.getInventory().setItemInMainHand(newItem);
                    return true;
                }
                player.getInventory().setItemInMainHand(XMaterial.AIR.parseItem());
                return true;
            }
        }
        if(offHand.getType() != XMaterial.AIR.parseMaterial()){
            Token token = Token.getTokenByItem(offHand);
            if(token != null){
                if(offHand.getAmount() < token.getItemStack().getAmount()){
                    plugin.getCosmeticsManager().sendMessage(player, plugin.prefix + plugin.getMessages().getString("insufficient-tokens"));
                    return false;
                }
                if(playerCache.getCosmeticById(token.getCosmetic()) != null){
                    plugin.getCosmeticsManager().sendMessage(player, plugin.prefix + plugin.getMessages().getString("already-token"));
                    return false;
                }
                if(offHand.getAmount() > token.getItemStack().getAmount() && token.getItemStack().getAmount() > 1){
                    ItemStack newItem = token.getItemStack().clone();
                    newItem.setAmount(offHand.getAmount() - token.getItemStack().getAmount());
                    player.getInventory().setItemInOffHand(newItem);
                    return true;
                }
                player.getInventory().setItemInOffHand(XMaterial.AIR.parseItem());
                return true;
            }
        }
        for(int i = 0; i < 8; i++){
            ItemStack itemStack = player.getInventory().getItem(i);
            Token token = Token.getTokenByItem(itemStack);
            if(token == null) continue;
            if(itemStack.getAmount() < token.getItemStack().getAmount()){
                plugin.getCosmeticsManager().sendMessage(player, plugin.prefix + plugin.getMessages().getString("insufficient-tokens"));
                return false;
            }
            if(playerCache.getCosmeticById(token.getCosmetic()) != null){
                plugin.getCosmeticsManager().sendMessage(player, plugin.prefix + plugin.getMessages().getString("already-token"));
                return false;
            }
            player.getInventory().removeItem(token.getItemStack().clone());
            return true;
        }
        return false;
    }

    public void saveItems(){
        Player player = getOfflinePlayer().getPlayer();
        for(int i = 0; i < player.getInventory().getSize(); i++){
            ItemStack itemStack = player.getInventory().getItem(i);
            if(itemStack == null) {
                inventory.put(i, null);
                continue;
            }
            if(itemStack.getType() == XMaterial.AIR.parseMaterial()){
                inventory.put(i, null);
                continue;
            }
            inventory.put(i, itemStack.clone());
        }
        player.getInventory().clear();
    }

    public int getFreeSlotInventory(){
        Player player = getOfflinePlayer().getPlayer();
        for(int i = 0; i < player.getInventory().getSize(); i++){
            if(inventory.get(i) == null){
                return i;
            }
        }
        return -1;
    }

    public void loadItems(){
        Player player = getOfflinePlayer().getPlayer();
        for(Map.Entry<Integer, ItemStack> inv : inventory.entrySet()){
            player.getInventory().setItem(inv.getKey(), inv.getValue());
        }
        inventory.clear();
    }

    public Map<Integer, ItemStack> getInventory() {
        return inventory;
    }

    public void setZone(boolean zone) {
        isZone = zone;
    }

    public int getEquippedCount(){
        int count = 0;
        if(hat != null){
            count++;
        }
        if(bag != null){
            count++;
        }
        if(wStick != null){
            count++;
        }
        if(balloon != null){
            count++;
        }
        return count;
    }
}
