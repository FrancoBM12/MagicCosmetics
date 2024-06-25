package com.francobm.magicosmetics.cache;

import com.francobm.magicosmetics.api.Cosmetic;
import com.francobm.magicosmetics.api.CosmeticType;
import com.francobm.magicosmetics.api.SprayKeys;
import com.francobm.magicosmetics.cache.cosmetics.backpacks.Bag;
import com.francobm.magicosmetics.cache.cosmetics.Hat;
import com.francobm.magicosmetics.cache.cosmetics.Spray;
import com.francobm.magicosmetics.cache.cosmetics.WStick;
import com.francobm.magicosmetics.cache.cosmetics.balloons.Balloon;
import com.francobm.magicosmetics.events.*;
import com.francobm.magicosmetics.nms.NPC.ItemSlot;
import com.francobm.magicosmetics.nms.NPC.NPC;
import com.francobm.magicosmetics.utils.Utils;
import com.francobm.magicosmetics.utils.XMaterial;
import com.francobm.magicosmetics.MagicCosmetics;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.*;
import org.bukkit.Color;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class PlayerData {
    public static Map<UUID, PlayerData> players = new HashMap<>();
    private OfflinePlayer offlinePlayer;
    private final UUID uniqueId;
    private final String name;
    private Hat hat;
    private Cosmetic bag;
    private WStick wStick;
    private Cosmetic balloon;
    private Cosmetic spray;
    private final Map<String, Cosmetic> cosmetics;
    private Cosmetic previewHat;
    private Cosmetic previewBag;
    private Cosmetic previewWStick;
    private Cosmetic previewBalloon;
    private Cosmetic previewSpray;
    private boolean isZone;
    private boolean sneak;
    private boolean spectator;
    private Zone zone;
    private final Map<Integer, ItemStack> inventory;
    private GameMode gameMode;
    private float speedFly;
    private boolean hideCosmetics;
    private boolean hasInBlackList;

    public PlayerData(UUID uniqueId, String name){
        this.uniqueId = uniqueId;
        this.name = name;
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
        this.offlinePlayer = Bukkit.getOfflinePlayer(uniqueId);
    }

    public static PlayerData getPlayer(OfflinePlayer player){
        if(!players.containsKey(player.getUniqueId())){
            PlayerData playerData = new PlayerData(player.getUniqueId(), player.getName());
            players.put(player.getUniqueId(), playerData);
            return playerData;
        }
        return players.get(player.getUniqueId());
    }

    public void setOfflinePlayer(OfflinePlayer offlinePlayer) {
        this.offlinePlayer = offlinePlayer;
    }

    public static void reload(){
        for(Player player : Bukkit.getOnlinePlayers()){
            PlayerData playerData = getPlayer(player);
            playerData.updateCosmetics();
        }
    }

    public void updateCosmetics(){
        clearCosmeticsInUse(true);
        if(hat != null && !hat.update()){
            removeHat();
        }
        if(wStick != null && !wStick.update()) {
            removeWStick();
        }
        if(balloon != null && !balloon.update()) {
            removeBalloon();
        }
        if(spray != null && !spray.update()) {
            removeSpray();
        }
        /*for(Cosmetic cosmetic : cosmetics){
            Cosmetic newCosmetic = Cosmetic.getCloneCosmetic(cosmetic.getId());
            if(newCosmetic == null) {
                plugin.getLogger().warning("Player " + name  + " is using a cosmetic that no longer exists (ID: " + cosmetic.getId() + " )");
                removeCosmetic(cosmetic.getId());
                plugin.getLogger().info("Removing the non-existent cosmetic from the player...");
                continue;
            }
            if(cosmetic.getColor() != null) newCosmetic.setColor(cosmetic.getColor());
            addCosmetic(newCosmetic);
            setCosmetic(newCosmetic);
        }*/
    }

    public int getCosmeticCount(CosmeticType cosmeticType){
        int i = 0;
        if(MagicCosmetics.getInstance().isPermissions()){
            for(Cosmetic cosmetic : Cosmetic.cosmetics.values()){
                if(!cosmetic.hasPermission(getOfflinePlayer().getPlayer())) continue;
                if(cosmetic.getCosmeticType() != cosmeticType) continue;
                i++;
            }
            return i;
        }
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

    public Cosmetic getPreviewSpray() {
        return previewSpray;
    }

    public void setPreviewSpray(Cosmetic previewSpray) {
        this.previewSpray = previewSpray;
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

    public static void removePlayer(PlayerData player){
        players.remove(player.getUniqueId());
    }

    public Hat getHat() {
        return hat;
    }

    public void setHat(Hat hat) {
        this.hat = hat;
        if(this.hat == null) return;
        this.hat.setPlayer(offlinePlayer.getPlayer());
        activeHat();
    }

    public Cosmetic getBag() {
        return bag;
    }

    public void setBag(Cosmetic bag) {
        this.bag = bag;
        if(this.bag == null) return;
        this.bag.setPlayer(offlinePlayer.getPlayer());
    }

    public WStick getWStick() {
        return wStick;
    }

    public void setWStick(WStick wStick) {
        this.wStick = wStick;
        if(this.wStick == null) return;
        this.wStick.setPlayer(offlinePlayer.getPlayer());
        activeWStick();
    }

    public Cosmetic getBalloon() {
        return balloon;
    }

    public void setBalloon(Cosmetic balloon) {
        this.balloon = balloon;
        if(this.balloon == null) return;
        this.balloon.setPlayer(offlinePlayer.getPlayer());
    }

    public Cosmetic getSpray() {
        return spray;
    }

    public void setSpray(Cosmetic spray) {
        this.spray = spray;
        if(this.spray == null) return;
        this.spray.setPlayer(offlinePlayer.getPlayer());
    }

    public void removeCosmetic(String cosmeticId){
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        Cosmetic cosmetic = getCosmeticById(cosmeticId);
        if(cosmetic == null) {
            cosmetic = Cosmetic.getCloneCosmetic(cosmeticId);
            if(cosmetic == null) return;
        }
        CosmeticUnEquipEvent event = new CosmeticUnEquipEvent(getOfflinePlayer().getPlayer(), cosmetic);
        MagicCosmetics.getInstance().getServer().getPluginManager().callEvent(event);
        if(event.isCancelled()) return;
        if(plugin.isPermissions() && !cosmetic.getPermission().isEmpty() && plugin.isLuckPerms()) {
            removeEquip(cosmeticId);
            removePreviewEquip(cosmeticId);
            plugin.getLuckPerms().removePermission(getUniqueId(), cosmetic.getPermission());
            return;
        }
        removeEquip(cosmeticId);
        removePreviewEquip(cosmeticId);
        cosmetics.remove(cosmeticId);
    }

    public void setCosmetic(Cosmetic cosmetic){
        if(cosmetic == null) return;

        switch (cosmetic.getCosmeticType()){
            case HAT:
                clearHat();
                setHat((Hat) cosmetic);
                break;
            case BAG:
                clearBag();
                setBag(cosmetic);
                break;
            case WALKING_STICK:
                clearWStick();
                setWStick((WStick) cosmetic);
                break;
            case BALLOON:
                clearBalloon();
                setBalloon(cosmetic);
                break;
            case SPRAY:
                clearSpray();
                setSpray(cosmetic);
                break;
        }
    }

    public void setCosmetic(CosmeticType cosmeticType, Cosmetic cosmetic){
        if(cosmetic == null) return;
        switch (cosmeticType){
            case HAT:
                clearHat();
                setHat((Hat) cosmetic);
                break;
            case BAG:
                clearBag();
                setBag(cosmetic);
                break;
            case WALKING_STICK:
                clearWStick();
                setWStick((WStick) cosmetic);
                break;
            case BALLOON:
                clearBalloon();
                setBalloon(cosmetic);
                break;
            case SPRAY:
                clearSpray();
                setSpray(cosmetic);
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
            case SPRAY:
                clearPreviewSpray();
                setPreviewSpray(cosmetic);
                activePreviewSpray();
                break;
        }
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public OfflinePlayer getOfflinePlayer(){
        return offlinePlayer;
    }

    public Map<String, Cosmetic> getCosmetics() {
        return cosmetics;
    }

    public List<Cosmetic> getCosmeticsPerm() {
        List<Cosmetic> cosmetics = new ArrayList<>();
        for (Cosmetic cosmetic : Cosmetic.cosmetics.values()) {
            if(!cosmetic.hasPermission(getOfflinePlayer().getPlayer())) continue;
            cosmetics.add(cosmetic);
        }
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
        if(id == null || id.isEmpty()) return null;
        return cosmetics.get(id);
    }

    public boolean hasCosmeticById(String id) {
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        Cosmetic cosmetic = Cosmetic.getCosmetic(id);
        if(plugin.isPermissions() && !cosmetic.getPermission().isEmpty() && plugin.isLuckPerms()) {
            return cosmetic.hasPermission(getOfflinePlayer().getPlayer());
        }
        return cosmetics.containsKey(id);
    }

    public String saveCosmetics(){
        List<String> ids = new ArrayList<>();
        if(MagicCosmetics.getInstance().isPermissions()){
            for(Cosmetic cosmetic : cosmeticsInUse()){
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
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        List<String> cosmetics = new ArrayList<>(Arrays.asList(ids.split(",")));
        this.cosmetics.clear();
        for(String cosmetic : cosmetics){
            String[] color = cosmetic.split("\\|");
            if(color.length > 1){
                Cosmetic cosmetic1 = Cosmetic.getCloneCosmetic(color[0]);
                if(cosmetic1 == null) continue;
                if(plugin.isPermissions() && plugin.isLuckPerms() && plugin.getLuckPerms().isExpirePermission(offlinePlayer.getUniqueId(), cosmetic1.getPermission()))
                    continue;
                if(this.cosmetics.containsKey(color[0])) continue;
                cosmetic1.setColor(Color.fromRGB(Integer.parseInt(color[1])));
                addCosmetic(cosmetic1);
                continue;
            }
            Cosmetic cosmetic1 = Cosmetic.getCloneCosmetic(cosmetic);
            if(cosmetic1 == null) continue;
            if(plugin.isPermissions() && plugin.isLuckPerms() && plugin.getLuckPerms().isExpirePermission(offlinePlayer.getUniqueId(), cosmetic1.getPermission()))
                continue;
            if(this.cosmetics.containsKey(cosmetic)) continue;
            addCosmetic(cosmetic1);
        }
    }

    public void addCosmetic(Cosmetic cosmetic){
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        if(cosmetic == null) return;
        if(plugin.isPermissions() && !cosmetic.getPermission().isEmpty() && plugin.isLuckPerms()) {
            if(!cosmetic.hasPermission(getOfflinePlayer().getPlayer())) {
                plugin.getLuckPerms().addPermission(getUniqueId(), cosmetic.getPermission());
            }
        }
        Player player = offlinePlayer.getPlayer();
        switch (cosmetic.getCosmeticType()){
            case HAT:
                Hat hat = (Hat) cosmetic;
                Hat newHat = new Hat(hat.getId(), hat.getName(), hat.getItemStack().clone(), hat.getModelData(), hat.isColored(), hat.getCosmeticType(), hat.getColor(), hat.isOverlaps(), hat.getPermission(), hat.isTexture(), hat.isHideMenu(), hat.isUseEmote(), hat.getOffSetY(), hat.getNamespacedKey());
                newHat.setColorBlocked(cosmetic.isColorBlocked());
                newHat.setPlayer(player);
                cosmetics.put(cosmetic.getId(), newHat);
                return;
            case BAG:
                Bag bag = (Bag) cosmetic;
                Bag newBag = new Bag(bag.getId(), bag.getName(), bag.getItemStack().clone(), bag.getModelData(), bag.getBagForMe(), bag.isColored(), bag.getSpace(), bag.getCosmeticType(), bag.getColor(), bag.getDistance(), bag.getPermission(), bag.isTexture(), bag.isHideMenu(), bag.getHeight(), bag.isUseEmote(), bag.getBackPackEngine() != null ? bag.getBackPackEngine().getClone() : null, bag.getNamespacedKey());
                newBag.setColorBlocked(cosmetic.isColorBlocked());
                newBag.setPlayer(player);
                cosmetics.put(cosmetic.getId(), newBag);
                return;
            case WALKING_STICK:
                WStick wStick = (WStick) cosmetic;
                WStick newWStick = new WStick(wStick.getId(), wStick.getName(), wStick.getItemStack().clone(), wStick.getModelData(), wStick.isColored(), wStick.getCosmeticType(), wStick.getColor(), wStick.getPermission(), wStick.isTexture(), wStick.isOverlaps(), wStick.isHideMenu(), wStick.isUseEmote(), wStick.getNamespacedKey());
                newWStick.setColorBlocked(cosmetic.isColorBlocked());
                newWStick.setPlayer(player);
                cosmetics.put(cosmetic.getId(), newWStick);
                break;
            case BALLOON:
                Balloon balloon = (Balloon) cosmetic;
                Balloon newBalloon = new Balloon(balloon.getId(), balloon.getName(), balloon.getItemStack().clone(), balloon.getModelData(), balloon.isColored(), balloon.getSpace(), balloon.getCosmeticType(), balloon.getColor(), balloon.isRotation(), balloon.getRotationType(), balloon.getBalloonEngine() != null ? balloon.getBalloonEngine().getClone() : null, balloon.getBalloonIA() != null ? balloon.getBalloonIA().getClone() : null, balloon.getDistance(), balloon.getPermission(), balloon.isTexture(), balloon.isBigHead(), balloon.isHideMenu(), balloon.isInvisibleLeash(), balloon.isUseEmote(), balloon.isInstantFollow(), balloon.getNamespacedKey());
                newBalloon.setColorBlocked(cosmetic.isColorBlocked());
                newBalloon.setPlayer(player);
                cosmetics.put(cosmetic.getId(), newBalloon);
                break;
            case SPRAY:
                Spray spray = (Spray) cosmetic;
                Spray newSpray = new Spray(spray.getId(), spray.getName(), spray.getItemStack().clone(), spray.getModelData(), spray.isColored(), spray.getCosmeticType(), spray.getColor(), spray.getPermission(), spray.isTexture(), spray.getImage(), spray.isItemImage(), spray.isHideMenu(), spray.isUseEmote(), spray.getNamespacedKey());
                newSpray.setColorBlocked(cosmetic.isColorBlocked());
                newSpray.setPlayer(player);
                cosmetics.put(cosmetic.getId(), newSpray);
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

    public void removeSpray(){
        clearSpray();
        spray = null;
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

    public void removePreviewSpray(){
        clearPreviewSpray();
        previewSpray = null;
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
            case SPRAY:
                if(spray == null) return null;
                return spray;
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
        if(spray != null){
            if(spray.getId().equalsIgnoreCase(id)){
                return spray;
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
        if(spray != null){
            if(spray.getId().equalsIgnoreCase(id)){
                removeSpray();
            }
        }
    }
    public void removeEquip(CosmeticType cosmeticType){
        switch (cosmeticType){
            case HAT:
                removeHat();
                return;
            case BAG:
                removeBag();
                return;
            case WALKING_STICK:
                removeWStick();
                return;
            case BALLOON:
                removeBalloon();
                return;
            case SPRAY:
                removeSpray();
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
        if(previewSpray != null){
            if(previewSpray.getId().equalsIgnoreCase(id)){
                removePreviewSpray();
            }
        }
    }

    public void clearHat(){
        Player player = getOfflinePlayer().getPlayer();
        if(player == null){
            return;
        }
        if(hat == null){
            return;
        }
        if(hat.isRemovedLendEntity()) return;
        hat.clear();
    }

    public void clearBag(){
        Player player = getOfflinePlayer().getPlayer();
        if(player == null){
            return;
        }
        if(bag == null){
            return;
        }
        if(bag.isRemovedLendEntity()) return;
        bag.clear();
    }

    public void clearWStick(){
        Player player = getOfflinePlayer().getPlayer();
        if(player == null){
            return;
        }
        if(wStick == null){
            return;
        }
        if(wStick.isRemovedLendEntity()) return;
        wStick.clear();
    }

    public void clearBalloon(){
        Player player = getOfflinePlayer().getPlayer();
        if(player == null){
            return;
        }
        if(balloon == null){
            return;
        }
        if(balloon.isRemovedLendEntity()) return;
        balloon.clear();
    }

    public void clearSpray(){
        Player player = getOfflinePlayer().getPlayer();
        if(player == null){
            return;
        }
        if(spray == null){
            return;
        }
        if(spray.isRemovedLendEntity()) return;
        spray.clear();
    }

    public void clearPreviewHat(){
        Player player = getOfflinePlayer().getPlayer();
        if(player == null){
            return;
        }
        if(previewHat == null){
            return;
        }
        NPC npc = MagicCosmetics.getInstance().getVersion().getNPC(player);
        if(npc == null) return;
        npc.equipNPC(player, ItemSlot.HELMET, XMaterial.AIR.parseItem());
    }

    public void clearPreviewBag(){
        Player player = getOfflinePlayer().getPlayer();
        if(player == null){
            return;
        }
        if(previewBag == null){
            return;
        }
        NPC npc = MagicCosmetics.getInstance().getVersion().getNPC(player);
        if(npc == null) return;
        npc.armorStandSetItem(player, XMaterial.AIR.parseItem());
    }

    public void clearPreviewWStick(){
        Player player = getOfflinePlayer().getPlayer();
        if(player == null){
            return;
        }
        if(previewWStick == null){
            return;
        }
        NPC npc = MagicCosmetics.getInstance().getVersion().getNPC(player);
        if(npc == null) return;
        npc.equipNPC(player, ItemSlot.OFF_HAND, XMaterial.AIR.parseItem());
    }

    public void clearPreviewBalloon(){
        Player player = getOfflinePlayer().getPlayer();
        if(player == null){
            return;
        }
        if(previewBalloon == null){
            return;
        }
        NPC npc = MagicCosmetics.getInstance().getVersion().getNPC(player);
        if(npc == null) return;
        npc.removeBalloon(player);
    }

    public void clearPreviewSpray(){
        Player player = getOfflinePlayer().getPlayer();
        if(player == null){
            return;
        }
        if(previewSpray == null){
            return;
        }
        previewSpray.clear();
    }

    public void activeHat(){
        Player player = getOfflinePlayer().getPlayer();
        if(player == null){
            return;
        }
        if(hat == null){
            return;
        }
        if(isZone) return;
        if(MagicCosmetics.getInstance().isItemsAdder()){
            if(MagicCosmetics.getInstance().getItemsAdder().hasEmote(player) && hat.isUseEmote()){
                hat.active();
                return;
            }
        }
        if(player.isInvisible() || player.hasPotionEffect(PotionEffectType.INVISIBILITY)){
            clearHat();
            return;
        }
        hat.active();
    }

    public void activeBag(){
        Player player = getOfflinePlayer().getPlayer();
        if(player == null){
            return;
        }
        if(bag == null){
            return;
        }
        if(isZone) return;
        if(MagicCosmetics.getInstance().isItemsAdder()){
            if(MagicCosmetics.getInstance().getItemsAdder().hasEmote(player) && bag.isUseEmote()){
                bag.active();
                return;
            }
        }
        if(player.getPose() == Pose.SLEEPING || player.getPose() == Pose.SWIMMING || player.isGliding() || player.isInvisible() || player.hasPotionEffect(PotionEffectType.INVISIBILITY)){
            clearBag();
            return;
        }
        bag.active();
    }

    public void activeWStick(){
        Player player = getOfflinePlayer().getPlayer();
        if(player == null){
            return;
        }
        if(wStick == null){
            return;
        }
        if(isZone) return;
        if(MagicCosmetics.getInstance().isItemsAdder()){
            if(MagicCosmetics.getInstance().getItemsAdder().hasEmote(player) && wStick.isUseEmote()){
                wStick.active();
                return;
            }
        }
        if(player.isInvisible() || player.hasPotionEffect(PotionEffectType.INVISIBILITY)){
            clearWStick();
            return;
        }
        wStick.active();
    }

    public void activeBalloon(){
        Player player = getOfflinePlayer().getPlayer();
        if(player == null){
            return;
        }
        if(balloon == null){
            return;
        }
        if(isZone) return;
        if(MagicCosmetics.getInstance().isItemsAdder()){
            if(MagicCosmetics.getInstance().getItemsAdder().hasEmote(player) && balloon.isUseEmote()){
                balloon.active();
                return;
            }
        }
        if(balloon.getLendEntity() != null){
            balloon.lendToEntity();
            return;
        }
        balloon.active();
    }

    public void previewDraw(){
        Player player = getOfflinePlayer().getPlayer();
        if(player == null){
            return;
        }
        if(previewSpray == null){
            return;
        }
        Zone zone = getZone();
        if(zone == null) return;
        if(zone.getSprayFace() == null || zone.getSprayLoc() == null) return;
        ((Spray)previewSpray).draw(player, zone.getSprayFace(), zone.getSprayLoc(), zone.getRotation());
    }

    public void draw(SprayKeys key){
        Player player = getOfflinePlayer().getPlayer();
        if(player == null){
            return;
        }
        if(spray == null){
            return;
        }
        if(isZone) return;
        ((Spray)spray).draw(player, key);
    }

    public void activeSpray(){
        Player player = getOfflinePlayer().getPlayer();
        if(player == null){
            return;
        }
        if(spray == null){
            return;
        }
        if(isZone) return;
        spray.active();
    }

    public void activePreviewHat(){
        Player player = getOfflinePlayer().getPlayer();
        if(player == null){
            return;
        }
        if(previewHat == null){
            return;
        }
        NPC npc = MagicCosmetics.getInstance().getVersion().getNPC(player);
        if(npc == null) return;
        npc.equipNPC(player, ItemSlot.HELMET, previewHat.getItemColor(player));
    }

    public void activePreviewBag(){
        Player player = getOfflinePlayer().getPlayer();
        if(player == null){
            return;
        }
        if(previewBag == null){
            return;
        }
        NPC npc = MagicCosmetics.getInstance().getVersion().getNPC(player);
        if(npc == null) return;
        npc.armorStandSetItem(player, previewBag.getItemColor(player));
    }

    public void activePreviewWStick(){
        Player player = getOfflinePlayer().getPlayer();
        if(player == null){
            return;
        }
        if(previewWStick == null){
            return;
        }
        NPC npc = MagicCosmetics.getInstance().getVersion().getNPC(player);
        if(npc == null) return;
        npc.equipNPC(player, ItemSlot.OFF_HAND, previewWStick.getItemColor(player));
    }

    public void activePB(){
        Player player = getOfflinePlayer().getPlayer();
        if(player == null){
            return;
        }
        if(previewBalloon == null){
            return;
        }
        NPC npc = MagicCosmetics.getInstance().getVersion().getNPC(player);
        if(npc == null) return;
        npc.animation(player);
    }

    public void activePreviewBalloon(){
        Player player = getOfflinePlayer().getPlayer();
        if(player == null){
            return;
        }
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
        npc.balloonNPC(player, location, previewBalloon.getItemColor(player), ((Balloon)previewBalloon).isBigHead());
    }

    public void activePreviewSpray(){
        previewDraw();
    }

    public void activeCosmetics(){
        //activeHat();
        activeBag();
        //activeWStick();
        activePB();
        //activeSpray();
    }

    public void clearCosmeticsInUse(boolean inventory){
        if(inventory)
            clearCosmeticsInventory();
        clearBag();
        clearBalloon();
        clearSpray();
    }

    public void clearCosmeticsToSaveData() {
        if(hat != null){
            hat.clearClose();
        }
        if(wStick != null)
            wStick.clearClose();
        if(balloon != null)
            balloon.clearClose();
        if(bag != null)
            bag.clearClose();
        if(spray != null)
            spray.clearClose();
    }

    public void clearCosmeticsInventory() {
        clearHat();
        clearWStick();
    }

    public void activeCosmeticsInventory(){
        activeHat();
        activeWStick();
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

    public boolean removeHelmet(){
        if(!getOfflinePlayer().isOnline()) return false;
        Player player = getOfflinePlayer().getPlayer();
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        ItemStack helmet = player.getInventory().getHelmet();
        if(hat == null){
            if(helmet != null){
                if(player.getInventory().firstEmpty() == -1){
                    Utils.sendSound(player, Sound.getSound("on_enter_zone_error"));
                    plugin.getCosmeticsManager().sendMessage(player,plugin.prefix + plugin.getMessages().getString("zone-exit-by-helmet"));
                    return false;
                }
            }
            return true;
        }
        if(helmet != null) {
            if (player.getInventory().firstEmpty() == -1) {
                Utils.sendSound(player, Sound.getSound("on_enter_zone_error"));
                plugin.getCosmeticsManager().sendMessage(player,plugin.prefix + plugin.getMessages().getString("zone-exit-by-helmet"));
                return false;
            }
        }
        ItemStack savedItem = hat.leftItemAndGet();
        if(savedItem != null)
            player.getInventory().addItem(savedItem);
        return true;
    }

    public boolean removeOffHand(){
        if(!getOfflinePlayer().isOnline()) return false;
        Player player = getOfflinePlayer().getPlayer();
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        ItemStack offHand = player.getInventory().getItemInOffHand();
        if(wStick == null) {
            if (!offHand.getType().isAir()) {
                if (player.getInventory().firstEmpty() == -1) {
                    Utils.sendSound(player, Sound.getSound("on_enter_zone_error"));
                    plugin.getCosmeticsManager().sendMessage(player, plugin.prefix + plugin.getMessages().getString("zone-exit-by-offhand"));
                    return false;
                }
            }
            return true;
        }
        if(!offHand.getType().isAir()) {
            if(player.getInventory().firstEmpty() == -1){
                Utils.sendSound(player, Sound.getSound("on_enter_zone_error"));
                plugin.getCosmeticsManager().sendMessage(player,plugin.prefix + plugin.getMessages().getString("zone-exit-by-offhand"));
                return false;
            }
        }
        ItemStack savedItem = wStick.leftItemAndGet();
        if(savedItem != null)
            player.getInventory().addItem(savedItem);
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
            if(spectator){
                if(!player.getLocation().equals(zone.getEnter())) {
                    player.teleport(zone.getEnter());
                }
            }
            return;
        }
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        ZoneEnterEvent event = new ZoneEnterEvent(player, zone);
        plugin.getServer().getPluginManager().callEvent(event);
        if(plugin.getMagicCrates() != null && plugin.getMagicCrates().hasInCrate(player) || event.isCancelled()) {
            setZone(null);
            player.teleport(zone.getExit());
            return;
        }
        gameMode = player.getGameMode();
        Utils.sendSound(player, Sound.getSound("on_enter_zone"));
        plugin.getVersion().setSpectator(player);
        String title = plugin.getMessages().getString("title-zone.enter");
        if(player.getGameMode() != GameMode.SPECTATOR){
            exitZone();
            return;
        }
        if(plugin.isItemsAdder()){
            if(plugin.getItemsAdder().hasEmote(player)){
                plugin.getItemsAdder().stopEmote(player);
            }
            title = plugin.getItemsAdder().replaceFontImages(title);
        }
        if(plugin.isOraxen()){
            title = plugin.getOraxen().replaceFontImages(title);
        }
        ZoneExitEvent exitEvent;
        if(!removeHelmet()){
            exitEvent = new ZoneExitEvent(player, zone, Reason.ITEM_IN_HELMET);
            plugin.getServer().getPluginManager().callEvent(exitEvent);
            isZone = false;
            sneak = false;
            spectator = false;
            player.teleport(zone.getExit());
            setSpeedFly(getSpeedFly() == 0 ? 0.1f : getSpeedFly());
            player.setFlySpeed(getSpeedFly());
            plugin.getVersion().setCamera(player, player);
            setZone(null);
            if(plugin.gameMode == null){
                player.setGameMode(gameMode);
            }else{
                player.setGameMode(plugin.gameMode);
            }
            return;
        }
        if(!removeOffHand()){
            exitEvent = new ZoneExitEvent(player, zone, Reason.ITEM_IN_OFF_HAND);
            plugin.getServer().getPluginManager().callEvent(exitEvent);
            isZone = false;
            sneak = false;
            spectator = false;
            player.teleport(zone.getExit());
            setSpeedFly(getSpeedFly() == 0 ? 0.1f : getSpeedFly());
            player.setFlySpeed(getSpeedFly());
            plugin.getVersion().setCamera(player, player);
            setZone(null);
            if(plugin.gameMode == null){
                player.setGameMode(gameMode);
            }else{
                player.setGameMode(plugin.gameMode);
            }
            return;
        }
        player.sendTitle(title, "", 15, 7, 15);
        clearCosmeticsInUse(true);
        plugin.getServer().getScheduler().runTaskLater(plugin, (task) -> {
            if(hat != null)
                hat.setHideCosmetic(true);
            if(wStick != null)
                wStick.setHideCosmetic(true);
            for(BossBar bossBar : plugin.getBossBar()){
                if(bossBar.getPlayers().contains(player)) continue;
                bossBar.addPlayer(player);
            }
            saveItems();
            if(player.getGameMode() == GameMode.SPECTATOR) {
                //player.setSpectatorTarget(zone.getSpec());
                player.teleport(zone.getEnter());
                setSpeedFly(player.getFlySpeed());
                player.setFlySpeed(0);
                spectator = true;
            }
            plugin.getVersion().createNPC(player, zone.getNpc());
            plugin.getVersion().getNPC(player).spawnPunch(player, zone.getEnter());
            setPreviewCosmetic(hat);
            setPreviewCosmetic(bag);
            setPreviewCosmetic(wStick);
            setPreviewCosmetic(balloon);
            setPreviewCosmetic(spray);
            if(plugin.getCosmeticsManager().npcTaskStopped())
                plugin.getCosmeticsManager().reRunTasks();
        }, 12);
        isZone = true;
    }

    public void exitZoneSync(){
        Zone zone = getZone();
        if(zone == null) return;
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        Player player = getOfflinePlayer().getPlayer();
        Utils.showPlayer(player);
        sneak = false;
        spectator = false;
        isZone = false;
        for(BossBar bossBar : plugin.getBossBar()){
            bossBar.removePlayer(player);
        }
        loadItems();
        if(plugin.gameMode == null){
            player.setGameMode(gameMode);
        }else{
            player.setGameMode(plugin.gameMode);
        }
        setSpeedFly(getSpeedFly() == 0 ? 0.1f : getSpeedFly());
        player.setFlySpeed(getSpeedFly());
        player.teleport(zone.getExit());
        plugin.getVersion().removeNPC(player);
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
        String title = plugin.getMessages().getString("title-zone.exit");
        if(plugin.isItemsAdder()){
            title = plugin.getItemsAdder().replaceFontImages(title);
        }
        if(plugin.isOraxen()){
            title = plugin.getOraxen().replaceFontImages(title);
        }
        player.sendTitle(title, "", 15, 7, 15);
        Utils.showPlayer(player);
        sneak = true;
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            for(BossBar bossBar : plugin.getBossBar()){
                bossBar.removePlayer(player);
            }
            loadItems();
            if(plugin.gameMode == null){
                player.setGameMode(gameMode);
            }else{
                player.setGameMode(plugin.gameMode);
            }
            Utils.sendSound(player, Sound.getSound("on_exit_zone"));
            plugin.getVersion().removeNPC(player);
            int count = 0;
            if(previewHat != null && previewHat.isColorBlocked() || previewBag != null && previewBag.isColorBlocked() || previewWStick != null && previewWStick.isColorBlocked() || previewBalloon != null && previewBalloon.isColorBlocked() || previewSpray != null && previewSpray.isColorBlocked()){
                isZone = false;
                sneak = false;
                spectator = false;
                setSpeedFly(getSpeedFly() == 0 ? 0.1f : getSpeedFly());
                player.setFlySpeed(getSpeedFly());
                plugin.getVersion().setCamera(player, player);
                player.teleport(zone.getExit());
                setZone(null);
                plugin.getCosmeticsManager().sendMessage(player, plugin.prefix + plugin.getMessages().getString("exit-color-without-perm"));
                return;
            }
            if(plugin.isPermissions()){
                if(previewHat != null){
                    if(!previewHat.hasPermission(player)){
                        count++;
                    }
                    setPreviewHat(null);
                }
                if(previewBag != null){
                    if(!previewBag.hasPermission(player)){
                        count++;
                    }
                    setPreviewBag(null);
                }
                if(previewWStick != null){
                    if(!previewWStick.hasPermission(player)){
                        count++;
                    }
                    setPreviewWStick(null);
                }
                if(previewBalloon != null){
                    if(!previewBalloon.hasPermission(player)){
                        count++;
                    }
                    setPreviewBalloon(null);
                }
                if(previewSpray != null){
                    if(!previewSpray.hasPermission(player)){
                        count++;
                    }
                    setPreviewSpray(null);
                }
            }else{
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
                if(previewSpray != null){
                    if(getCosmeticById(previewSpray.getId()) == null){
                        count++;
                    }
                    setPreviewSpray(null);
                }
            }
            if(count != 0){
                if(count == 4){
                    plugin.getCosmeticsManager().sendMessage(player,plugin.prefix + plugin.getMessages().getString("exit-all-cosmetics"));
                    isZone = false;
                    sneak = false;
                    spectator = false;
                    setSpeedFly(getSpeedFly() == 0 ? 0.1f : getSpeedFly());
                    player.setFlySpeed(getSpeedFly());
                    plugin.getVersion().setCamera(player, player);
                    player.teleport(zone.getExit());
                    setZone(null);
                    return;
                }
                plugin.getCosmeticsManager().sendMessage(player,plugin.prefix + plugin.getMessages().getString("exit-some-cosmetics").replace("%count%", String.valueOf(count)));
            }
            isZone = false;
            sneak = false;
            spectator = false;
            setSpeedFly(getSpeedFly() == 0 ? 0.1f : getSpeedFly());
            player.setFlySpeed(getSpeedFly());
            plugin.getVersion().setCamera(player, player);
            player.teleport(zone.getExit());
            setZone(null);
            if(hat != null)
                hat.setHideCosmetic(false);
            if(wStick != null)
                wStick.setHideCosmetic(false);
        }, 17);
    }

    public ItemStack getTokenInPlayer(){
        Player player = getOfflinePlayer().getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();
        if(!mainHand.getType().isAir()){
            Token token = Token.getTokenByItem(mainHand);
            if(token == null){
                token = Token.getOldTokenByItem(mainHand);
            }
            if(token != null)
                return mainHand;
        }
        if(!offHand.getType().isAir()){
            Token token = Token.getTokenByItem(offHand);
            if(token == null && wStick != null && !wStick.isCosmetic(offHand)){
                token = Token.getOldTokenByItem(offHand);
            }
            if(token != null)
                return offHand;
        }
        for(int i = 0; i < 8; i++){
            ItemStack itemStack = player.getInventory().getItem(i);
            Token token = Token.getTokenByItem(itemStack);
            if(token == null) continue;
            return itemStack;
        }
        for(int i = 0; i < 8; i++){
            ItemStack itemStack = player.getInventory().getItem(i);
            Token token = Token.getOldTokenByItem(itemStack);
            if(token == null) continue;
            return itemStack;
        }
        return null;
    }

    public boolean removeTokenInPlayer(){
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        Player player = getOfflinePlayer().getPlayer();
        PlayerData playerData = PlayerData.getPlayer(player);
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();
        if(mainHand.getType() != XMaterial.AIR.parseMaterial()){
            Token token = Token.getTokenByItem(mainHand);
            if(token != null){
                Cosmetic cosmetic = Cosmetic.getCosmetic(token.getCosmetic());
                if(cosmetic == null) {
                    return false;
                }
                if(mainHand.getAmount() < token.getItemStack().getAmount()){
                    plugin.getCosmeticsManager().sendMessage(player, plugin.prefix + plugin.getMessages().getString("insufficient-tokens"));
                    return false;
                }
                if(!cosmetic.getPermission().isEmpty() && plugin.isLuckPerms()){
                    if(cosmetic.hasPermission(player)){
                        plugin.getCosmeticsManager().sendMessage(player, plugin.prefix + plugin.getMessages().getString("already-token"));
                        return false;
                    }
                }else {
                    if (playerData.getCosmeticById(token.getCosmetic()) != null) {
                        plugin.getCosmeticsManager().sendMessage(player, plugin.prefix + plugin.getMessages().getString("already-token"));
                        return false;
                    }
                }
                if(mainHand.getAmount() > token.getItemStack().getAmount()){
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
            if(token != null) {
                Cosmetic cosmetic = Cosmetic.getCosmetic(token.getCosmetic());
                if(cosmetic == null) {
                    return false;
                }
                if(offHand.getAmount() < token.getItemStack().getAmount()){
                    plugin.getCosmeticsManager().sendMessage(player, plugin.prefix + plugin.getMessages().getString("insufficient-tokens"));
                    return false;
                }
                if(!cosmetic.getPermission().isEmpty() && plugin.isLuckPerms()){
                    if(cosmetic.hasPermission(player)){
                        plugin.getCosmeticsManager().sendMessage(player, plugin.prefix + plugin.getMessages().getString("already-token"));
                        return false;
                    }
                }else {
                    if (playerData.getCosmeticById(token.getCosmetic()) != null) {
                        plugin.getCosmeticsManager().sendMessage(player, plugin.prefix + plugin.getMessages().getString("already-token"));
                        return false;
                    }
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
            if(itemStack == null) continue;
            Token token = Token.getTokenByItem(itemStack);
            if(token == null) continue;
            Cosmetic cosmetic = Cosmetic.getCosmetic(token.getCosmetic());
            if(cosmetic == null) {
                return false;
            }
            if(itemStack.getAmount() < token.getItemStack().getAmount()){
                plugin.getCosmeticsManager().sendMessage(player, plugin.prefix + plugin.getMessages().getString("insufficient-tokens"));
                return false;
            }
            if(!cosmetic.getPermission().isEmpty() && plugin.isLuckPerms()){
                if(cosmetic.hasPermission(player)){
                    plugin.getCosmeticsManager().sendMessage(player, plugin.prefix + plugin.getMessages().getString("already-token"));
                    return false;
                }
            }else {
                if (playerData.getCosmeticById(token.getCosmetic()) != null) {
                    plugin.getCosmeticsManager().sendMessage(player, plugin.prefix + plugin.getMessages().getString("already-token"));
                    return false;
                }
            }
            player.getInventory().removeItem(token.getItemStack().clone());
            return true;
        }
        return false;
    }

    public void saveItems(){
        if(!MagicCosmetics.getInstance().isZoneHideItems()) return;
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
        for(int i = 0; i < player.getInventory().getStorageContents().length; i++){
            if(inventory.get(i) == null){
                return i;
            }
        }
        return -1;
    }

    public void loadItems(){
        if(!MagicCosmetics.getInstance().isZoneHideItems()) return;
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
        if(spray != null){
            count++;
        }
        return count;
    }

    public Set<Cosmetic> cosmeticsInUse(){
        Set<Cosmetic> cosmetics = new HashSet<>();
        if(hat != null){
            cosmetics.add(hat);
        }
        if(bag != null){
            cosmetics.add(bag);
        }
        if(wStick != null){
            cosmetics.add(wStick);
        }
        if(balloon != null){
            cosmetics.add(balloon);
        }
        if(spray != null){
            cosmetics.add(spray);
        }
        return cosmetics;
    }

    public float getSpeedFly() {
        return speedFly;
    }

    public void setSpeedFly(float speedFly) {
        this.speedFly = speedFly;
    }

    public boolean isSpectator() {
        return spectator;
    }

    public void toggleHiddeCosmetics(){
        hideCosmetics = !hideCosmetics;
        if(hideCosmetics) {
            hideAllCosmetics();
            return;
        }
        showAllCosmetics();
    }

    public void hideAllCosmetics() {
        if(hat != null && !hat.isHideCosmetic()){
            hat.setHideCosmetic(true);
        }
        if(bag != null && !bag.isHideCosmetic()){
            bag.setHideCosmetic(true);
        }
        if(wStick != null && !wStick.isHideCosmetic()){
            wStick.setHideCosmetic(true);
        }
        if(balloon != null && !balloon.isHideCosmetic()){
            balloon.setHideCosmetic(true);
        }
    }

    public void showAllCosmetics() {
        if (hat != null && hat.isHideCosmetic()) {
            hat.setHideCosmetic(false);
        }
        if (bag != null && bag.isHideCosmetic()) {
            bag.setHideCosmetic(false);
        }
        if (wStick != null && wStick.isHideCosmetic()) {
            wStick.setHideCosmetic(false);
        }
        if (balloon != null && balloon.isHideCosmetic()) {
            balloon.setHideCosmetic(false);
        }
    }

    public boolean isHasInBlackList() {
        return hasInBlackList;
    }

    public void setHasInBlackList(boolean hasInBlackList) {
        this.hasInBlackList = hasInBlackList;
    }

    //Proxy

    public void sendSavePlayerData()
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        try {
            dataOutputStream.writeUTF("save_cosmetics"); // the channel could be whatever you want
            dataOutputStream.writeUTF(getOfflinePlayer().getName());
            dataOutputStream.writeUTF(saveCosmetics());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        getOfflinePlayer().getPlayer().sendPluginMessage( MagicCosmetics.getInstance(), "mc:player", outputStream.toByteArray() );
        MagicCosmetics.getInstance().getLogger().info("Send Save Data");
    }

    //
}
