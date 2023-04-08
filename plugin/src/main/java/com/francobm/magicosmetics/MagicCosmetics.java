package com.francobm.magicosmetics;

import com.francobm.magicosmetics.api.Cosmetic;
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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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
    private Citizens citizens;
    public String ava = "";
    public String unAva = "";
    public String equip = "";
    public BarColor bossBarColor = BarColor.YELLOW;
    public double balloonRotation = 0;
    private boolean bungee = false;
    private boolean permissions = false;
    private boolean zoneHideItems = true;
    private SprayKeys sprayKey;
    private int sprayStayTime = 60;
    private int sprayCooldown = 5;
    private LuckPerms luckPerms;

    private Floodgate floodgate;

    private boolean placeholders;
    private String mainMenu = "hat";
    public int saveDataDelay;

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
            case "v1_19_R2":
                version = new com.francobm.magicosmetics.nms.Packets.v1_19_R2.VersionHandler();
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
        if (config.getBoolean("MySQL.enabled")) {
            sql = new MySQL();
        } else {
            sql = new SQLite();
        }
        if(getCosmetic()) return;

        if (getServer().getPluginManager().getPlugin("ItemsAdder") != null && Utils.existPluginClass("dev.lone.itemsadder.api.FontImages.FontImageWrapper")) {
            itemsAdder = new ItemsAdder();
        }

        if (getServer().getPluginManager().getPlugin("Oraxen") != null) {
            if(Utils.existPluginClass("io.th0rgal.oraxen.api.OraxenItems")){
                oraxen = new NewOraxen();
            }else{
                oraxen = new OldOraxen();
            }
            oraxen.register();
        }

        if(getServer().getPluginManager().isPluginEnabled("ModelEngine")) {
            String version = getServer().getPluginManager().getPlugin("ModelEngine").getDescription().getVersion().split("\\.")[0];
            if(version.equalsIgnoreCase("R3")){
                modelEngine = new NewModelEngine();
                getLogger().info("ModelEngine 3.0.0 found, using new model engine");
            }else{
                modelEngine = new OldModelEngine();
                getLogger().info("ModelEngine 2.5.X found, using old model engine");
            }
        }

        if(getServer().getPluginManager().getPlugin("Citizens") != null){
            citizens = new Citizens();
        }

        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            placeholderAPI = new PlaceholderAPI();
        }
        if(getServer().getPluginManager().isPluginEnabled("LuckPerms")){
            luckPerms = new LuckPerms();
        }

        if(Utils.existPluginClass("org.geysermc.floodgate.api.FloodgateApi")){
            floodgate = new Floodgate();
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
            for(String lines : messages.getStringList("bossbar")){
                if(isOraxen())
                    lines = getOraxen().replaceFontImages(lines);
                BossBar boss = getServer().createBossBar(lines, bossBarColor, BarStyle.SOLID);
                boss.setVisible(true);
                bossBar.add(boss);
            }
            Cosmetic.loadCosmetics();
            Color.loadColors();
            Items.loadItems();
            Zone.loadZones();
            Token.loadTokens();
            Sound.loadSounds();
            Menu.loadMenus();
        }

        cosmeticsManager = new CosmeticsManager();
        registerData();
        cosmeticsManager.runTasks();
        registerCommands();
        registerListeners();
        for(Player player : Bukkit.getOnlinePlayers()){
            if(player == null || !player.isOnline()) continue;
            sql.loadPlayer(player, true);
        }
    }

    private void registerData(){
        this.prefix = messages.getString("prefix");
        if(config.contains("leave-wardrobe-gamemode")) {
            try {
                gameMode = GameMode.valueOf(config.getString("leave-wardrobe-gamemode").toUpperCase());
            }catch (IllegalArgumentException exception){
                getLogger().severe("Gamemode in config path: leave-wardrobe-gamemode Not Found!");
            }
        }
        if(config.contains("main-menu"))
            mainMenu = config.getString("main-menu");
        if(config.contains("placeholder-api")){
            placeholders = config.getBoolean("placeholder-api");
        }
        equipMessage = false;
        if(config.contains("permissions")){
            setPermissions(config.getBoolean("permissions"));
        }
        if(config.contains("equip-message")){
            equipMessage = config.getBoolean("equip-message");
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
        if(config.contains("save-data-delay")) {
            saveDataDelay = config.getInt("save-data-delay");
        }
        balloonRotation = config.getDouble("balloons-rotation");
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
        if(getServer().getPluginManager().getPlugin("SkinsRestorer") != null) {
            getServer().getPluginManager().registerEvents(new SkinListener(), this);
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
            PlayerData playerData = PlayerData.getPlayer(player);
            if(playerData.isZone()){
                playerData.exitZoneSync();
            }
            sql.savePlayer(playerData, true);
        }
        PlayerData.players.clear();
        if(isCitizens()) {
            for(EntityCache entityCache : EntityCache.entities.values()){
                if(!entityCache.isCosmeticUse()) {
                    sql.removeEntity(entityCache.getUniqueId());
                    continue;
                }
                sql.saveEntity(entityCache);
            }
            EntityCache.entities.clear();
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

    public FileCreator getConfig() {
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

    public void setSql(SQL sql) {
        this.sql = sql;
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

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    public boolean isLuckPerms() {
        return luckPerms != null;
    }

    public Floodgate getFloodgate() {
        return floodgate;
    }

    public void setPlaceholders(boolean placeholders) {
        this.placeholders = placeholders;
    }

    public boolean isPlaceholders() {
        return placeholders;
    }

    public String getMainMenu() {
        return mainMenu;
    }

    public void setMainMenu(String mainMenu) {
        this.mainMenu = mainMenu;
    }
}
