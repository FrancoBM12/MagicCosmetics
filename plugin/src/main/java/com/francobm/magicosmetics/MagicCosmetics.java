package com.francobm.magicosmetics;

import com.francobm.magicosmetics.api.SprayKeys;
import com.francobm.magicosmetics.cache.*;
import com.francobm.magicosmetics.cache.inventories.Menu;
import com.francobm.magicosmetics.cache.items.Items;
import com.francobm.magicosmetics.commands.Command;
import com.francobm.magicosmetics.database.MySQL;
import com.francobm.magicosmetics.database.SQL;
import com.francobm.magicosmetics.database.SQLite;
import com.francobm.magicosmetics.files.FileCosmetics;
import com.francobm.magicosmetics.files.FileCreator;
import com.francobm.magicosmetics.listeners.*;
import com.francobm.magicosmetics.managers.CosmeticsManager;
import com.francobm.magicosmetics.nms.Version.Version;
import com.francobm.magicosmetics.nms.Packets.v1_17_R1.VersionHandler;
import com.francobm.magicosmetics.provider.*;
import com.francobm.magicosmetics.utils.MathUtils;
import com.francobm.magicosmetics.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class MagicCosmetics extends JavaPlugin {
    private static MagicCosmetics instance;
    private FileCreator config;
    private FileCreator messages;
    private FileCosmetics cosmetics;
    private FileCreator menus;
    private FileCreator zones;
    private FileCreator tokens;
    private FileCreator sounds;
    private SQL sql;
    public String prefix;
    private CosmeticsManager cosmeticsManager;
    private Version version;
    public boolean wkasdwk;
    private List<BossBar> bossBar;
    private ModelEngine modelEngine;
    private ItemsAdder itemsAdder;
    private Oraxen oraxen;
    private User user;
    private PlaceholderAPI placeholderAPI;
    public GameMode gameMode = null;
    public boolean equipMessage;
    private MiniMessage miniMessage;
    private Citizens citizens;
    public String ava = "";
    public String unAva = "";
    public String equip = "";
    public boolean saveOnQuit = true;
    public BarColor bossBarColor = BarColor.YELLOW;
    public double balloonRotation = 0;
    private boolean bungee = false;
    private boolean permissions = false;
    private boolean zoneHideItems = true;
    private SprayKeys sprayKey;
    private int sprayStayTime = 60;
    private int sprayCooldown = 5;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        switch (Utils.getVersion()){
            case "v1_16_R3":
                version = new com.francobm.magicosmetics.nms.Packets.v1_16_R3.VersionHandler();
                break;
            case "v1_17_R1":
                version = new VersionHandler();
                break;
            case "v1_18_R1":
                version = new com.francobm.magicosmetics.nms.Packets.v1_18_R1.VersionHandler();
                break;
            case "v1_18_R2":
                version = new com.francobm.magicosmetics.nms.Packets.v1_18_R2.VersionHandler();
                break;
            case "v1_19_R1":
                version = new com.francobm.magicosmetics.nms.Packets.v1_19_R1.VersionHandler();
                break;
        }
        Version.setVersion(version);
        if(version == null){
            getLogger().severe(Utils.bsc("VmVyc2lvbjog") + Utils.getVersion() + Utils.bsc("IE5vdCBTdXBwb3J0ZWQh"));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info(Utils.bsc("VmVyc2lvbjog") + Utils.getVersion() + Utils.bsc("IERldGVjdGVkIQ=="));
        this.bossBar = new ArrayList<>();
        this.config = new FileCreator(this, "config");
        this.messages = new FileCreator(this, "messages");
        this.cosmetics = new FileCosmetics();
        this.menus = new FileCreator(this, "menus");
        this.zones = new FileCreator(this, "zones");
        this.tokens = new FileCreator(this, "tokens");
        this.sounds = new FileCreator(this, "sounds");
        createDefaultSpray();
        this.prefix = messages.getString("prefix");
        if(config.contains("leave-wardrobe-gamemode")) {
            try {
                gameMode = GameMode.valueOf(config.getString("leave-wardrobe-gamemode").toUpperCase());
            }catch (IllegalArgumentException exception){
                getLogger().severe("Gamemode in config path: leave-wardrobe-gamemode Not Found!");
            }
        }
        equipMessage = false;
        if(config.contains("permissions")){
            setPermissions(config.getBoolean("permissions"));
        }
        if(config.contains("equip-message")){
            equipMessage = config.getBoolean("equip-message");
        }
        if(config.contains("save-on-quit")){
            saveOnQuit = config.getBoolean("save-on-quit");
        }
        if(config.contains("zones-hide-items")){
            zoneHideItems = config.getBoolean("zones-hide-items");
        }
        if(config.contains("bossbar-color")){
            try {
                bossBarColor = BarColor.valueOf(config.getString("bossbar-color").toUpperCase());
            }catch (IllegalArgumentException exception){
                getLogger().severe("Bossbar color in config path: bossbar-color Not Valid!");
            }
        }
        if(config.contains("bungeecord")){
            bungee = config.getBoolean("bungeecord");
        }
        if(config.contains("spray-key")){
            try {
                sprayKey = SprayKeys.valueOf(config.getString("spray-key").toUpperCase());
            }catch (IllegalArgumentException exception){
                getLogger().severe("Spray key in config path: spray-key Not Valid!");
            }
        }
        if(config.contains("spray-stay-time")){
            sprayStayTime = config.getInt("spray-stay-time");
        }
        if(config.contains("spray-cooldown")){
            sprayCooldown = config.getInt("spray-cooldown");
        }
        balloonRotation = MagicCosmetics.getInstance().getConfig().getDouble("balloons-rotation");
        if (config.getBoolean("MySQL.enabled")) {
            sql = new MySQL();
        } else {
            sql = new SQLite();
        }
        if(!sql.isConnected()) return;
        if(getCosmetic()) return;

        if (getServer().getPluginManager().getPlugin("ItemsAdder") != null) {
            itemsAdder = new ItemsAdder();
        }

        if (getServer().getPluginManager().getPlugin("Oraxen") != null) {
            oraxen = new Oraxen();
            oraxen.register();
        }

        if (getServer().getPluginManager().getPlugin("ModelEngine") != null) {
            modelEngine = new ModelEngine();
        }

        if(getServer().getPluginManager().getPlugin("Citizens") != null){
            citizens = new Citizens();
        }

        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            placeholderAPI = new PlaceholderAPI();
        }

        for(String lines : messages.getStringList("bossbar")){
            BossBar boss = getServer().createBossBar(lines, bossBarColor, BarStyle.SOLID);
            boss.setVisible(true);
            bossBar.add(boss);
        }

        ava = MagicCosmetics.getInstance().getMessages().getString("edge.available");
        unAva = MagicCosmetics.getInstance().getMessages().getString("edge.unavailable");
        equip = MagicCosmetics.getInstance().getMessages().getString("edge.equip");
        if(isOraxen()){
            ava = getOraxen().replaceFontImages(ava);
            unAva = getOraxen().replaceFontImages(unAva);
            equip = getOraxen().replaceFontImages(equip);
        }

        if (!isItemsAdder()) {
            Cosmetic.loadCosmetics();
            Color.loadColors();
            Items.loadItems();
            Zone.loadZones();
            Token.loadTokens();
            Sound.loadSounds();
            Menu.loadMenus();
        }

        cosmeticsManager = new CosmeticsManager();
        cosmeticsManager.runTasks();
        registerCommands();
        registerListeners();
        for(Player player : Bukkit.getOnlinePlayers()){
            if(player == null || !player.isOnline()) continue;
            sql.loadPlayer(player, true);
        }
    }

    public void registerListeners(){
        getServer().getPluginManager().registerEvents(new EntityListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        if(isItemsAdder()) {
            getServer().getPluginManager().registerEvents(new ItemsAdderListener(), this);
        }
        if(isCitizens()){
            getServer().getPluginManager().registerEvents(new CitizensListener(), this);
        }
    }

    public void registerCommands(){
        getCommand("magicosmetics").setExecutor(new Command());
        getCommand("magicosmetics").setTabCompleter(new Command());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if(cosmeticsManager != null) {
            cosmeticsManager.cancelTasks();
        }
        for(Player player : Bukkit.getOnlinePlayers()){
            if(player == null || !player.isOnline()) continue;
            PlayerCache playerCache = PlayerCache.getPlayer(player);
            if(playerCache.isZone()){
                playerCache.exitZoneSync();
            }
            sql.savePlayer(playerCache, true);
        }
        if(isCitizens()) {
            Iterator<EntityCache> iterator = EntityCache.entities.values().iterator();
            while (iterator.hasNext()) {
                EntityCache entityCache = iterator.next();
                if(!entityCache.isCosmeticUse()) {
                    sql.removeEntity(entityCache.getUniqueId());
                    iterator.remove();
                    continue;
                }
                sql.saveEntity(entityCache);
                iterator.remove();
            }
        }

        if(sql != null){
            sql.disconnect();
        }
        if(bossBar != null) {
            for (BossBar bar : bossBar) {
                bar.removeAll();
            }
        }

    }

    public static MagicCosmetics getInstance() {
        return instance;
    }

    public @NotNull FileCreator getConfig() {
        return this.config;
    }

    public FileCreator getMessages() {
        return this.messages;
    }

    public FileCosmetics getCosmetics() {
        return this.cosmetics;
    }

    public FileCreator getMenus() {
        return this.menus;
    }

    public FileCreator getZones() {
        return this.zones;
    }

    public FileCreator getTokens() {
        return this.tokens;
    }

    public SQL getSql() {
        return this.sql;
    }

    public CosmeticsManager getCosmeticsManager() {
        return this.cosmeticsManager;
    }

    public Version getVersion() {
        return this.version;
    }

    public boolean getCosmetic() {
        MathUtils.floor(1.0f, 2.0f);
        User user = getUser();
        if(user == null) {
            getLogger().warning("Your user does not exist, how strange isn't it...?");
            return true;
        }
        getLogger().info(" ");
        getLogger().info("Welcome " + user.getName() + "!");
        getLogger().info("Thank you for using MagicCosmetics =)!");
        getLogger().info(" ");
        return false;
    }

    public FileCreator getSounds() {
        return this.sounds;
    }

    public List<BossBar> getBossBar() {
        return this.bossBar;
    }

    public ModelEngine getModelEngine() {
        return this.modelEngine;
    }

    public boolean isModelEngine() {
        return this.modelEngine != null;
    }

    public ItemsAdder getItemsAdder() {
        return this.itemsAdder;
    }

    public boolean isItemsAdder() {
        return this.itemsAdder != null;
    }

    public Oraxen getOraxen() {
        return this.oraxen;
    }

    public boolean isOraxen(){
        return this.oraxen != null;
    }

    public PlaceholderAPI getPlaceholderAPI() {
        return placeholderAPI;
    }

    public boolean isPlaceholderAPI() {
        return this.placeholderAPI != null;
    }

    public MiniMessage getMiniMessage() {
        return miniMessage;
    }

    public boolean isMiniMessage(){
        return this.miniMessage != null;
    }

    public User getUser() {
        return new User();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isCitizens(){
        return this.citizens != null;
    }

    public Citizens getCitizens() {
        return citizens;
    }

    public boolean isBungee() {
        return bungee;
    }

    public void setBungee(boolean bungee) {
        this.bungee = bungee;
    }

    public boolean isPermissions() {
        return permissions;
    }

    public void createDefaultSpray(){
        File file = new File(getDataFolder(), "sprays");
        if(file.exists()) return;
        new FileCreator(this, "sprays/first", ".png", getDataFolder());
    }

    public void setPermissions(boolean permissions) {
        this.permissions = permissions;
    }

    public SprayKeys getSprayKey() {
        return sprayKey;
    }

    public void setSprayKey(SprayKeys sprayKey) {
        this.sprayKey = sprayKey;
    }

    public int getSprayStayTime() {
        return sprayStayTime;
    }

    public void setSprayStayTime(int sprayStayTime) {
        this.sprayStayTime = sprayStayTime;
    }

    public int getSprayCooldown() {
        return sprayCooldown;
    }

    public void setSprayCooldown(int sprayCooldown) {
        this.sprayCooldown = sprayCooldown;
    }

    public boolean isZoneHideItems() {
        return zoneHideItems;
    }

    public void setZoneHideItems(boolean zoneHideItems) {
        this.zoneHideItems = zoneHideItems;
    }
}
