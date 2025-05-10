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
import com.francobm.magicosmetics.loaders.NPCsLoader;
import com.francobm.magicosmetics.managers.CosmeticsManager;
import com.francobm.magicosmetics.managers.ZonesManager;
import com.francobm.magicosmetics.nms.version.Version;
import com.francobm.magicosmetics.nms.v1_17_R1.VersionHandler;
import com.francobm.magicosmetics.provider.*;
import com.francobm.magicosmetics.provider.citizens.Citizens;
import com.francobm.magicosmetics.provider.husksync.HuskSync;
import com.francobm.magicosmetics.provider.mpdb.MysqlPlayerDataBridge;
import com.francobm.magicosmetics.provider.znpcplus.ZNPCsPlus;
import com.francobm.magicosmetics.utils.MathUtils;
import com.francobm.magicosmetics.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
    private FileCreator npcs;
    private NPCsLoader NPCsLoader;

    private SQL sql;
    public String prefix;

    private CosmeticsManager cosmeticsManager;
    private ZonesManager zonesManager;

    private Version version;
    public boolean wkasdwk;
    private List<BossBar> bossBar;
    public ModelEngine modelEngine;
    public ResourcePlugin resourcePlugin;
    private User user;
    public PlaceholderAPI placeholderAPI;
    public GameMode gameMode = null;
    public boolean equipMessage;
    public Citizens citizens;
    private ZNPCsPlus zNPCsPlus;
    public String ava = "";
    public String unAva = "";
    public String equip = "";
    public BarColor bossBarColor = BarColor.YELLOW;
    public double balloonRotation = 0;
    private boolean permissions = false;
    private boolean zoneHideItems = true;
    private SprayKeys sprayKey;
    private int sprayStayTime = 60;
    private int sprayCooldown = 5;
    public LuckPerms luckPerms;
    private boolean placeholders;
    private String mainMenu = "hat";
    public int saveDataDelay;
    private ZoneActions zoneActions;
    private String luckPermsServer;
    private String onExecuteCosmetics;
    private MagicCrates magicCrates;
    private MagicGestures magicGestures;
    private List<String> worldsBlacklist;
    private WorldGuard worldGuard;
    private HuskSync huskSync;
    private MysqlPlayerDataBridge mpdb;
    private boolean proxy;
    private boolean showAllCosmeticsInMenu;

    @Override
    public void onLoad() {
        if(getServer().getPluginManager().getPlugin("WorldGuard") != null)
            worldGuard = new WorldGuard(this);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        switch (Utils.getVersion()){
            case "1.16.5":
                version = new com.francobm.magicosmetics.nms.v1_16_R3.VersionHandler();
                break;
            case "1.17":
            case "1.17.1":
                version = new VersionHandler();
                break;
            case "1.18":
            case "1.18.1":
                version = new com.francobm.magicosmetics.nms.v1_18_R1.VersionHandler();
                break;
            case "1.18.2":
                version = new com.francobm.magicosmetics.nms.v1_18_R2.VersionHandler();
                break;
            case "1.19":
            case "1.19.1":
            case "1.19.2":
                version = new com.francobm.magicosmetics.nms.v1_19_R1.VersionHandler();
                break;
            case "1.19.3":
                version = new com.francobm.magicosmetics.nms.v1_19_R2.VersionHandler();
                break;
            case "1.19.4":
                version = new com.francobm.magicosmetics.nms.v1_19_R3.VersionHandler();
                break;
            case "1.20":
            case "1.20.1":
                version = new com.francobm.magicosmetics.nms.v1_20_R1.VersionHandler();
                break;
            case "1.20.2":
                version = new com.francobm.magicosmetics.nms.v1_20_R2.VersionHandler();
                break;
            case "1.20.3":
            case "1.20.4":
                version = new com.francobm.magicosmetics.nms.v1_20_R3.VersionHandler();
                break;
            case "1.20.5":
            case "1.20.6":
                version = new com.francobm.magicosmetics.nms.v1_20_R4.VersionHandler();
                break;
            case "1.21":
            case "1.21.1":
                version = new com.francobm.magicosmetics.nms.v1_21_R1.VersionHandler();
                break;
            case "1.21.2":
            case "1.21.3":
                version = new com.francobm.magicosmetics.nms.v1_21_R2.VersionHandler();
                break;
            case "1.21.4":
                version = new com.francobm.magicosmetics.nms.v1_21_R3.VersionHandler();
                break;
            case "1.21.5":
                version = new com.francobm.magicosmetics.nms.v1_21_R4.VersionHandler();
                break;
        }
        //checkIfProxy();
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
        this.npcs = new FileCreator(this, "npcs");
        this.NPCsLoader = new NPCsLoader();
        createDefaultSpray();
        if(getCosmetic()) return;

        if(getServer().getPluginManager().getPlugin("HuskSync") != null){
            huskSync = new HuskSync();
        }

        if(getServer().getPluginManager().getPlugin("MysqlPlayerDataBridge") != null){
            mpdb = new MysqlPlayerDataBridge();
        }

        if (getServer().getPluginManager().getPlugin("ItemsAdder") != null && Utils.existPluginClass("dev.lone.itemsadder.api.FontImages.FontImageWrapper")) {
            resourcePlugin = new ItemsAdder();
        }

        if (getServer().getPluginManager().getPlugin("Oraxen") != null) {
            if(Utils.existPluginClass("io.th0rgal.oraxen.api.OraxenItems")){
                resourcePlugin = new NewOraxen();
                ((NewOraxen)resourcePlugin).register();
            }else{
                getLogger().warning("This version of Oraxen lacks classes needed to use the api.");
            }
        }

        if(getServer().getPluginManager().getPlugin("Nexo") != null) {
            resourcePlugin = new Nexo();
        }

        if(getServer().getPluginManager().isPluginEnabled("ModelEngine")) {
            String version = getServer().getPluginManager().getPlugin("ModelEngine").getDescription().getVersion().split("\\.")[0];
            if(version.equalsIgnoreCase("R3")){
                modelEngine = new ModelEngine3();
                getLogger().info("ModelEngine 3.0.0 found, using old model engine");
            }else{
                modelEngine = new ModelEngine4();
                getLogger().info("ModelEngine 4 found, using new model engine");
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

        if(getServer().getPluginManager().isPluginEnabled("MagicCrates")){
            magicCrates = new MagicCrates();
        }

        if(getServer().getPluginManager().isPluginEnabled("MagicGestures")) {
            magicGestures = new MagicGestures();
        }

        //SkinsRestorer Listener unnecessary.
        /*if(getServer().getPluginManager().isPluginEnabled("SkinsRestorer") && !isProxy()) {
            new SkinListener();
        }*/

        if (!isResourcePlugin() || !(resourcePlugin instanceof ItemsAdder)) {
            Cosmetic.loadCosmetics();
            Color.loadColors();
            Items.loadItems();
            Zone.loadZones();
            Token.loadTokens();
            Sound.loadSounds();
            Menu.loadMenus();
        }

        cosmeticsManager = new CosmeticsManager();
        zonesManager = new ZonesManager();
        registerData();
        cosmeticsManager.runTasks();
        registerCommands();
        registerListeners();
        for(Player player : Bukkit.getOnlinePlayers()){
            if(player == null || !player.isOnline()) continue;
            sql.loadPlayerAsync(player);
        }
    }

    public void registerData(){
        if (config.getBoolean("MySQL.enabled")) {
            sql = new MySQL();
        } else {
            sql = new SQLite();
        }
        for(BossBar bar : bossBar){
            bar.removeAll();
        }
        bossBar.clear();

        for(String lines : messages.getStringList("bossbar")){
            if(isResourcePlugin())
                lines = resourcePlugin.replaceFontImages(lines);
            BossBar boss = getServer().createBossBar(lines, bossBarColor, BarStyle.SOLID);
            boss.setVisible(true);
            bossBar.add(boss);
        }

        showAllCosmeticsInMenu = config.getBoolean("show-all-cosmetics-in-menu", true);

        ava = MagicCosmetics.getInstance().getMessages().getString("edge.available");
        unAva = MagicCosmetics.getInstance().getMessages().getString("edge.unavailable");
        equip = MagicCosmetics.getInstance().getMessages().getString("edge.equip");
        if(isResourcePlugin()){
            ava = resourcePlugin.replaceFontImages(ava);
            unAva = resourcePlugin.replaceFontImages(unAva);
            equip = resourcePlugin.replaceFontImages(equip);
        }
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
        if(config.contains("permissions")){
            setPermissions(config.getBoolean("permissions"));
        }
        equipMessage = false;
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
                bossBarColor = BarColor.YELLOW;
                getLogger().severe("Bossbar color in config path: bossbar-color Not Valid!");
            }
        }
        if(config.contains("proxy")){
            proxy = config.getBoolean("proxy");
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
        saveDataDelay = 300;
        if(config.contains("save-data-delay")) {
            saveDataDelay = config.getInt("save-data-delay");
        }
        if(config.contains("luckperms-server"))
            luckPermsServer = config.getString("luckperms-server");
        if(config.contains("on_execute_cosmetics"))
            onExecuteCosmetics = config.getString("on_execute_cosmetics");
        if(config.contains("worlds-blacklist"))
            worldsBlacklist = config.getStringListWF("worlds-blacklist");
        balloonRotation = config.getDouble("balloons-rotation");
        ZoneAction onEnter = null;
        ZoneAction onExit = null;
        if(zoneActions != null) {
            zoneActions.getOnEnter().setCommands(zones.getStringList("on_enter.commands"));
            zoneActions.getOnExit().setCommands(zones.getStringList("on_exit.commands"));
            zoneActions.setEnabled(config.getBoolean("zones-actions"));
            zoneActionsListener();
        }else {
            if (zones.contains("on_enter.commands"))
                onEnter = new ZoneAction("onEnter", zones.getStringList("on_enter.commands"));
            if (zones.contains("on_exit.commands"))
                onExit = new ZoneAction("onEnter", zones.getStringList("on_exit.commands"));
            zoneActions = new ZoneActions(onEnter, onExit);
            zoneActions.setEnabled(getConfig().getBoolean("zones-actions"));
        }
    }

    public void registerListeners(){
        getServer().getPluginManager().registerEvents(new EntityListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        if(isResourcePlugin() && resourcePlugin instanceof ItemsAdder) {
            getServer().getPluginManager().registerEvents(new ItemsAdderListener(), this);
        }
        if(isCitizens()){
            getServer().getPluginManager().registerEvents(new CitizensListener(), this);
        }
        if(isHuskSync()){
            getServer().getPluginManager().registerEvents(huskSync, this);
        }
        if(isMpdb()){
            getServer().getPluginManager().registerEvents(mpdb, this);
        }
        if(worldGuard != null){
            getServer().getPluginManager().registerEvents(worldGuard, this);
        }
        if(getServer().getPluginManager().isPluginEnabled("Multiverse-Core")){
            getServer().getPluginManager().registerEvents(new MultiverseCListener(), this);
        }
        zoneActionsListener();
        if(isProxy()){
            getServer().getMessenger().registerIncomingPluginChannel(this, "mc:player", new ProxyListener());
            getServer().getMessenger().registerOutgoingPluginChannel(this, "mc:player");
        }
    }

    private void checkIfProxy()
    {
        Path spigotPath = Paths.get("spigot.yml");
        if(Files.exists(spigotPath) && YamlConfiguration.loadConfiguration(spigotPath.toFile()).getBoolean("settings.bungeecord")){
            getLogger().info( "Enabling BungeeMode!");
            setProxy(true);
            getServer().getMessenger().registerIncomingPluginChannel(this, "mc:player", new ProxyListener());
            getServer().getMessenger().registerOutgoingPluginChannel(this, "mc:player");
            return;
        }
        Path oldPaperPath = Paths.get("paper.yml");
        if(Utils.isPaper()) {
            if(Files.exists(oldPaperPath) && YamlConfiguration.loadConfiguration(oldPaperPath.toFile()).getBoolean("settings.velocity-support.enabled")){
                getLogger().info( "Enabling VelocityMode!");
                setProxy(true);
                getServer().getMessenger().registerIncomingPluginChannel(this, "mc:player", new ProxyListener());
                getServer().getMessenger().registerOutgoingPluginChannel(this, "mc:player");
                return;
            }
            YamlConfiguration config = Utils.getPaperConfig(getServer());
            if(config != null && (config.getBoolean("settings.velocity-support.enabled") || config.getBoolean("proxies.velocity.enabled"))) {
                getLogger().info( "Enabling VelocityMode!");
                setProxy(true);
                getServer().getMessenger().registerIncomingPluginChannel(this, "mc:player", new ProxyListener());
                getServer().getMessenger().registerOutgoingPluginChannel(this, "mc:player");
            }
        }
    }

    public void zoneActionsListener(){
        if(zoneActions.isEnabled()){
            if(HandlerList.getRegisteredListeners(this).stream().anyMatch(registeredListener -> registeredListener.getListener().equals(getZoneActions().getZoneListener()))) return;
            getServer().getPluginManager().registerEvents(getZoneActions().getZoneListener(), this);
            return;
        }
        HandlerList.unregisterAll(getZoneActions().getZoneListener());
    }

    public void registerCommands(){
        getCommand("magicosmetics").setExecutor(new Command());
        getCommand("magicosmetics").setTabCompleter(new Command());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if(proxy){
            getServer().getMessenger().unregisterIncomingPluginChannel(this);
            getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        }
        if(cosmeticsManager != null) {
            cosmeticsManager.cancelTasks();
        }
        for(Player player : Bukkit.getOnlinePlayers()){
            if(player == null || !player.isOnline()) continue;
            PlayerData playerData = PlayerData.getPlayer(player);
            if(!playerData.isZone()) continue;
            playerData.exitZoneSync();
        }
        sql.savePlayers();
        if(bossBar != null) {
            for (BossBar bar : bossBar) {
                bar.removeAll();
            }
            bossBar.clear();
        }
        NPCsLoader.save();
    }

    public boolean isProxy() {
        return proxy;
    }

    public void setProxy(boolean proxy) {
        this.proxy = proxy;
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

    public CosmeticsManager getCosmeticsManager() {
        return this.cosmeticsManager;
    }

    public ZonesManager getZonesManager() {
        return zonesManager;
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

    public ResourcePlugin getResourcePlugin() {
        return this.resourcePlugin;
    }

    public boolean isResourcePlugin(){
        return this.resourcePlugin != null;
    }

    public PlaceholderAPI getPlaceholderAPI() {
        return placeholderAPI;
    }

    public boolean isPlaceholderAPI() {
        return this.placeholderAPI != null;
    }

    public User getUser() {
        return this.user;
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

    public ZNPCsPlus getzNPCsPlus() {
        return zNPCsPlus;
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

    public ZoneActions getZoneActions() {
        return zoneActions;
    }

    public void setZoneActions(ZoneActions zoneActions) {
        this.zoneActions = zoneActions;
    }

    public String getLuckPermsServer() {
        return luckPermsServer;
    }

    public void setLuckPermsServer(String luckPermsServer) {
        this.luckPermsServer = luckPermsServer;
    }

    public String getOnExecuteCosmetics() {
        return onExecuteCosmetics;
    }

    public void setOnExecuteCosmetics(String onExecuteCosmetics) {
        this.onExecuteCosmetics = onExecuteCosmetics;
    }

    public FileCreator getNPCs() {
        return npcs;
    }

    public com.francobm.magicosmetics.loaders.NPCsLoader getNPCsLoader() {
        return NPCsLoader;
    }

    public MagicCrates getMagicCrates() {
        return magicCrates;
    }

    public MagicGestures getMagicGestures() {
        return magicGestures;
    }

    public List<String> getWorldsBlacklist() {
        return worldsBlacklist;
    }

    public HuskSync getHuskSync() {
        return huskSync;
    }

    public boolean isHuskSync() {
        return huskSync != null;
    }

    public boolean isShowAllCosmeticsInMenu() {
        return showAllCosmeticsInMenu;
    }

    public MysqlPlayerDataBridge getMpdb() {
        return mpdb;
    }

    public boolean isMpdb() {
        return mpdb != null;
    }
}
