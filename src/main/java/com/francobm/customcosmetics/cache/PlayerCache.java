package com.francobm.customcosmetics.cache;

import com.francobm.customcosmetics.CustomCosmetics;
import com.francobm.customcosmetics.cache.balloons.Balloon;
import com.francobm.customcosmetics.nms.NPC.ItemSlot;
import com.francobm.customcosmetics.nms.NPC.NPC;
import com.francobm.customcosmetics.utils.Utils;
import com.francobm.customcosmetics.utils.XMaterial;
import org.bukkit.*;
import org.bukkit.Color;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
        removeEquip(cosmeticId);
        removePreviewEquip(cosmeticId);
        cosmetic.clear(getOfflinePlayer().getPlayer());
        cosmetics.remove(cosmeticId);
    }

    public void setCosmetic(Cosmetic cosmetic){
        if(cosmetic == null) return;

        switch (cosmetic.getCosmeticType()){
            case HAT:
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
                cosmetics.put(cosmetic.getId(), new Hat(hat.getId(), hat.getName(), hat.getItemStack().clone(), hat.getModelData(), hat.isColored(), hat.getCosmeticType(), hat.getColor()));
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
                cosmetics.put(cosmetic.getId(), new Balloon(balloon.getId(), balloon.getName(), balloon.getItemStack().clone(), balloon.getModelData(), balloon.isColored(), balloon.getSpace(), balloon.getCosmeticType(), balloon.getColor(), balloon.isRotation(), balloon.getRotationType(), balloon.getBalloonEngine() == null ? "" : balloon.getBalloonEngine().getModelId()));
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
        balloon = null;
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
        NPC npc = CustomCosmetics.getInstance().getVersion().getNPC(player);
        if(npc == null) return;
        npc.equipNPC(player, ItemSlot.HELMET, XMaterial.AIR.parseItem());
    }

    public void clearPreviewBag(){
        Player player = getOfflinePlayer().getPlayer();
        if(previewBag == null){
            return;
        }
        NPC npc = CustomCosmetics.getInstance().getVersion().getNPC(player);
        if(npc == null) return;
        npc.armorStandSetItem(player, XMaterial.AIR.parseItem());
    }

    public void clearPreviewWStick(){
        Player player = getOfflinePlayer().getPlayer();
        if(previewWStick == null){
            return;
        }
        NPC npc = CustomCosmetics.getInstance().getVersion().getNPC(player);
        if(npc == null) return;
        npc.equipNPC(player, ItemSlot.OFF_HAND, XMaterial.AIR.parseItem());
    }

    public void clearPreviewBalloon(){
        Player player = getOfflinePlayer().getPlayer();
        if(previewBalloon == null){
            return;
        }
        NPC npc = CustomCosmetics.getInstance().getVersion().getNPC(player);
        if(npc == null) return;
        npc.removeBalloon(player);
    }

    public void activeHat(){
        Player player = getOfflinePlayer().getPlayer();
        if(hat == null){
            return;
        }
        if(isZone) return;
        hat.active(player);
    }

    public void activeBag(){
        Player player = getOfflinePlayer().getPlayer();
        if(bag == null){
            return;
        }
        if(isZone) return;
        bag.active(player);
    }

    public void activeWStick(){
        Player player = getOfflinePlayer().getPlayer();
        if(wStick == null){
            return;
        }
        if(isZone) return;
        wStick.active(player);
    }

    public void activeBalloon(){
        Player player = getOfflinePlayer().getPlayer();
        if(balloon == null){
            return;
        }
        if(isZone) return;
        balloon.active(player);
    }

    public void activePreviewHat(){
        Player player = getOfflinePlayer().getPlayer();
        if(previewHat == null){
            return;
        }
        NPC npc = CustomCosmetics.getInstance().getVersion().getNPC(player);
        if(npc == null) return;
        npc.equipNPC(player, ItemSlot.HELMET, previewHat.getItemColor());
    }

    public void activePreviewBag(){
        Player player = getOfflinePlayer().getPlayer();
        if(previewBag == null){
            return;
        }
        NPC npc = CustomCosmetics.getInstance().getVersion().getNPC(player);
        if(npc == null) return;
        npc.armorStandSetItem(player, previewBag.getItemColor());
    }

    public void activePreviewWStick(){
        Player player = getOfflinePlayer().getPlayer();
        if(previewWStick == null){
            return;
        }
        NPC npc = CustomCosmetics.getInstance().getVersion().getNPC(player);
        if(npc == null) return;
        npc.equipNPC(player, ItemSlot.OFF_HAND, previewWStick.getItemColor());
    }

    public void activePB(){
        Player player = getOfflinePlayer().getPlayer();
        if(previewBalloon == null){
            return;
        }
        NPC npc = CustomCosmetics.getInstance().getVersion().getNPC(player);
        if(npc == null) return;
        npc.animation(player);
    }

    public void activePreviewBalloon(){
        Player player = getOfflinePlayer().getPlayer();
        if(previewBalloon == null){
            return;
        }
        NPC npc = CustomCosmetics.getInstance().getVersion().getNPC(player);
        if(npc == null) return;
        Zone zone = getZone();
        if(zone == null) return;
        Location location;
        if(zone.getBalloon() == null){
            location = ((Balloon)previewBalloon).updatePosition(npc.getEntity());
        }else{
            location = zone.getBalloon();
        }
        npc.balloonNPC(player, location, previewBalloon.getItemColor());
        npc.balloonSetItem(player, previewBalloon.getItemColor());
    }

    public void activeCosmetics(){
        activeHat();
        activeBag();
        activeWStick();
        //activeBalloon();
    }

    public void clearCosmeticsInUse(){
        clearHat();
        clearBag();
        clearWStick();
        clearBalloon();
    }

    public Zone getZone(){
        for(Zone z : Zone.zones.values()){
            if(!z.isInZone(getOfflinePlayer().getPlayer().getLocation().getBlock())) continue;
            return z;
        }
        return null;
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
        CustomCosmetics plugin = CustomCosmetics.getInstance();
        ItemStack helmet = player.getInventory().getHelmet();
        if(hat == null){
            if(helmet != null){
                if(player.getInventory().firstEmpty() == -1){
                    player.sendMessage(plugin.prefix + plugin.getMessages().getString("zone-exit-by-helmet"));
                    return false;
                }
                player.getInventory().setHelmet(null);
                player.getInventory().addItem(helmet.clone());
            }
            return true;
        }
        if(helmet != null) {
            if (!hat.isCosmetic(helmet)) {
                if (player.getInventory().firstEmpty() == -1) {
                    player.sendMessage(plugin.prefix + plugin.getMessages().getString("zone-exit-by-helmet"));
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
        CustomCosmetics plugin = CustomCosmetics.getInstance();
        ItemStack offHand = player.getInventory().getItemInOffHand();
        if(wStick == null){
            if(offHand.getType() != XMaterial.AIR.parseMaterial()){
                if(player.getInventory().firstEmpty() == -1){
                    plugin.getVersion().sendSound(player, Sound.getSound("on_enter_zone_error"));
                    player.sendMessage(plugin.prefix + plugin.getMessages().getString("zone-exit-by-offhand"));
                    return false;
                }
                player.getInventory().setItemInOffHand(null);
                player.getInventory().addItem(offHand.clone());
            }
            return true;
        }
        if(offHand.getType() != XMaterial.AIR.parseMaterial()) {
            if(!wStick.isCosmetic(offHand)){
                if(player.getInventory().firstEmpty() == -1){
                    plugin.getVersion().sendSound(player, Sound.getSound("on_enter_zone_error"));
                    player.sendMessage(plugin.prefix + plugin.getMessages().getString("zone-exit-by-offhand"));
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
        if(zone == null) return;
        if(!zone.isActive()){
            isZone = false;
            return;
        }
        if(isZone) return;
        CustomCosmetics plugin = CustomCosmetics.getInstance();
        Player player = getOfflinePlayer().getPlayer();
        plugin.getVersion().sendSound(player, Sound.getSound("on_enter_zone"));
        plugin.getVersion().setSpectator(player);
        Utils.hidePlayer(player);
        String title = plugin.getMessages().getString("title-zone");
        if(plugin.isItemsAdder()){
            if(plugin.getItemsAdder().isFontImageWrapper(title)){
                title = plugin.getItemsAdder().getFontImageWrapperString(title);
            }
        }
        player.sendTitle(title, "", 15, 7, 15);
        new BukkitRunnable() {
            @Override
            public void run() {
                if(!removeHelmet()){
                    player.teleport(zone.getExit());
                    isZone = false;
                    player.setGameMode(GameMode.SURVIVAL);
                    cancel();
                    return;
                }
                if(!removeOffHand()){
                    player.teleport(zone.getExit());
                    isZone = false;
                    player.setGameMode(GameMode.SURVIVAL);
                    cancel();
                    return;
                }
                for(BossBar bossBar : plugin.getBossBar()){
                    if(bossBar.getPlayers().contains(player)) continue;
                    bossBar.addPlayer(player);
                }
                saveItems();
                player.setSpectatorTarget(zone.getSpec());
                plugin.getVersion().createNPC(player, zone.getNpc());
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
        CustomCosmetics plugin = CustomCosmetics.getInstance();
        Player player = getOfflinePlayer().getPlayer();
        Utils.showPlayer(player);
        sneak = true;
        for(BossBar bossBar : plugin.getBossBar()){
            bossBar.removePlayer(player);
        }
        loadItems();
        player.setGameMode(GameMode.SURVIVAL);
        plugin.getVersion().removeNPC(player);
        player.teleport(zone.getExit());
    }

    public void exitZone(){
        Zone zone = getZone();
        if(zone == null) return;
        if(!isZone) return;
        if(sneak) return;
        CustomCosmetics plugin = CustomCosmetics.getInstance();
        Player player = getOfflinePlayer().getPlayer();
        String title = plugin.getMessages().getString("title-zone");
        if(plugin.isItemsAdder()){
            if(plugin.getItemsAdder().isFontImageWrapper(title)){
                title = plugin.getItemsAdder().getFontImageWrapperString(title);
            }
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
                player.setGameMode(GameMode.SURVIVAL);
                plugin.getVersion().sendSound(player, Sound.getSound("on_exit_zone"));
                plugin.getVersion().removeNPC(player);
                player.teleport(zone.getExit());
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
                /*if(previewHat != null){
                    if(getCosmeticById(previewHat.getId()) != null) {
                        removeCosmetic(previewHat.getId());
                        addCosmetic(previewHat);
                        setCosmetic(getCosmeticById(previewHat.getId()));
                        previewHat = null;
                    }else{
                        previewHat = null;
                        count++;
                    }
                }
                if(previewBag != null){
                    if(getCosmeticById(previewBag.getId()) != null) {
                        removeCosmetic(previewBag.getId());
                        addCosmetic(previewBag);
                        setCosmetic(getCosmeticById(previewBag.getId()));
                        previewBag = null;
                    }else{
                        previewBag = null;
                        count++;
                    }
                }
                if(previewWStick != null){
                    if(getCosmeticById(previewWStick.getId()) != null) {
                        removeCosmetic(previewWStick.getId());
                        addCosmetic(previewWStick);
                        setCosmetic(getCosmeticById(previewWStick.getId()));
                        previewWStick = null;
                    }else{
                        previewWStick = null;
                        count++;
                    }
                }
                if(previewBalloon != null){
                    if(getCosmeticById(previewBalloon.getId()) != null){
                        removeCosmetic(previewBalloon.getId());
                        addCosmetic(previewBalloon);
                        setCosmetic(getCosmeticById(previewBalloon.getId()));
                        previewBalloon = null;
                    }else{
                        previewBalloon = null;
                        count++;
                    }
                }*/
                if(count != 0){
                    if(count == 4){
                        player.sendMessage(plugin.prefix + plugin.getMessages().getString("exit-all-cosmetics"));
                        isZone = false;
                        sneak = false;
                        return;
                    }
                    player.sendMessage(plugin.prefix + plugin.getMessages().getString("exit-some-cosmetics").replace("%count%", String.valueOf(count)));
                }
                isZone = false;
                sneak = false;
            }
        }.runTaskLater(plugin, 17);
    }

    public Token getTokenInPlayer(){
        Player player = getOfflinePlayer().getPlayer();
        for(int i = 0; i < 8; i++){
            ItemStack itemStack = player.getInventory().getItem(i);
            Token token = Token.getTokenByItem(itemStack);
            if(token == null) continue;
            return token;
        }
        return null;
    }

    public boolean removeTokenInPlayer(){
        Player player = getOfflinePlayer().getPlayer();
        for(int i = 0; i < 8; i++){
            ItemStack itemStack = player.getInventory().getItem(i);
            Token token = Token.getTokenByItem(itemStack);
            if(token == null) continue;
            player.getInventory().removeItem(itemStack);
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
}
