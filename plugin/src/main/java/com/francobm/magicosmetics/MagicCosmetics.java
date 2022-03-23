package com.francobm.magicosmetics;

import com.francobm.magicosmetics.cache.*;
import com.francobm.magicosmetics.cache.inventories.Menu;
import com.francobm.magicosmetics.cache.items.Items;
import com.francobm.magicosmetics.commands.Command;
import com.francobm.magicosmetics.database.MySQL;
import com.francobm.magicosmetics.database.SQL;
import com.francobm.magicosmetics.database.SQLite;
import com.francobm.magicosmetics.files.FileCreator;
import com.francobm.magicosmetics.listeners.EntityListener;
import com.francobm.magicosmetics.listeners.InventoryListener;
import com.francobm.magicosmetics.listeners.ItemsAdderListener;
import com.francobm.magicosmetics.listeners.PlayerListener;
import com.francobm.magicosmetics.managers.CosmeticsManager;
import com.francobm.magicosmetics.nms.Version.Version;
import com.francobm.magicosmetics.nms.Packets.v1_17_R1.VersionHandler;
import com.francobm.magicosmetics.provider.*;
import com.francobm.magicosmetics.utils.Maths;
import com.francobm.magicosmetics.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class MagicCosmetics extends JavaPlugin {
    private static MagicCosmetics instance;
    private FileCreator config;
    private FileCreator messages;
    private FileCreator cosmetics;
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
    public GameMode gameMode;
    public boolean equipMessage;
    private MiniMessage miniMessage;
    public String ava = "";
    public String unAva = "";
    public String equip = "";

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
        this.cosmetics = new FileCreator(this, "cosmetics");
        this.menus = new FileCreator(this, "menus");
        this.zones = new FileCreator(this, "zones");
        this.tokens = new FileCreator(this, "tokens");
        this.sounds = new FileCreator(this, "sounds");
        this.prefix = messages.getString("prefix");
        gameMode = GameMode.SURVIVAL;
        if(config.contains("leave-wardrobe-gamemode")) {
            try {
                gameMode = GameMode.valueOf(config.getString("leave-wardrobe-gamemode").toUpperCase());
            }catch (IllegalArgumentException exception){
                getLogger().severe("Gamemode in config path: leave-wardrobe-gamemode Not Found!");
            }
        }
        equipMessage = false;
        if(config.contains("equip-message")){
            equipMessage = config.getBoolean("equip-message");
        }
        //if(getCosmetic()) return;
        if (config.getBoolean("MySQL.enabled")) {
            sql = new MySQL();
        } else {
            sql = new SQLite();
        }
        if(!sql.isConnected()) return;

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

        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            placeholderAPI = new PlaceholderAPI();
            placeholderAPI.register();
        }

        for(String lines : messages.getStringList("bossbar")){
            BossBar boss = getServer().createBossBar(lines, BarColor.YELLOW, BarStyle.SOLID);
            boss.setVisible(true);
            bossBar.add(boss);
        }

        ava = MagicCosmetics.getInstance().getMessages().getString("edge.available");
        unAva = MagicCosmetics.getInstance().getMessages().getString("edge.unavailable");
        equip = MagicCosmetics.getInstance().getMessages().getString("edge.equip");
        if(isItemsAdder()){
            ava = getItemsAdder().replaceFontImages(ava);
            unAva = getItemsAdder().replaceFontImages(unAva);
            equip = getItemsAdder().replaceFontImages(equip);
        }
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
    }

    public void registerListeners(){
        getServer().getPluginManager().registerEvents(new EntityListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        if(isItemsAdder()) {
            getServer().getPluginManager().registerEvents(new ItemsAdderListener(), this);
        }
    }

    public void registerCommands(){
        getCommand("magicosmetics").setExecutor(new Command());
        getCommand("magicosmetics").setTabCompleter(new Command());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for(Zone zone : Zone.zones.values()){
            zone.removeSpec();
        }
        if(cosmeticsManager != null) {
            cosmeticsManager.cancelTasks();
        }
        for(Player player : Bukkit.getOnlinePlayers()){
            if(player == null || !player.isOnline()) continue;
            PlayerCache playerCache = PlayerCache.getPlayer(player);
            if(playerCache.isZone()){
                playerCache.exitZoneSync();
            }
            sql.savePlayer(playerCache);
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

    public FileCreator getCosmetics() {
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
        return Maths.m();
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
}
