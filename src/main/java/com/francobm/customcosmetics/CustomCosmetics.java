package com.francobm.customcosmetics;

import com.francobm.customcosmetics.cache.*;
import com.francobm.customcosmetics.cache.inventories.Menu;
import com.francobm.customcosmetics.cache.items.Items;
import com.francobm.customcosmetics.cache.nms.v1_17_R1.PlayerBagHandler;
import com.francobm.customcosmetics.database.MySQL;
import com.francobm.customcosmetics.database.SQL;
import com.francobm.customcosmetics.database.SQLite;
import com.francobm.customcosmetics.files.FileCreator;
import com.francobm.customcosmetics.managers.CosmeticsManager;
import com.francobm.customcosmetics.nms.V1_18_R1;
import com.francobm.customcosmetics.nms.Version;
import com.francobm.customcosmetics.nms.v1_17_R1;
import com.francobm.customcosmetics.provider.ItemsAdder;
import com.francobm.customcosmetics.provider.ModelEngine;
import com.francobm.customcosmetics.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class CustomCosmetics extends JavaPlugin {
    private static CustomCosmetics instance;
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

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        this.bossBar = new ArrayList<>();
        this.config = new FileCreator(this, "config");
        this.messages = new FileCreator(this, "messages");
        this.cosmetics = new FileCreator(this, "cosmetics");
        this.menus = new FileCreator(this, "menus");
        this.zones = new FileCreator(this, "zones");
        this.tokens = new FileCreator(this, "tokens");
        this.sounds = new FileCreator(this, "sounds");
        this.prefix = this.messages.getString("prefix");
        getCosmetic();
        if (this.config.getBoolean("MySQL.enabled")) {
            this.sql = new MySQL();
        } else {
            this.sql = new SQLite();
        }

        if (this.getServer().getPluginManager().isPluginEnabled("ItemsAdder")) {
            this.itemsAdder = new ItemsAdder();
        }

        if (this.getServer().getPluginManager().isPluginEnabled("ModelEngine")) {
            this.modelEngine = new ModelEngine();
        }
        for(String lines : messages.getStringList("bossbar")){
            BossBar boss = getServer().createBossBar(lines, BarColor.YELLOW, BarStyle.SOLID);
            boss.setVisible(true);
            bossBar.add(boss);
        }

        switch (Utils.getVersion()){
            case "v1_17_R1":
                version = new v1_17_R1();
                break;
            case "v1_18_R1":
                version = new V1_18_R1();
                break;
        }

        if (!this.isItemsAdder()) {
            Cosmetic.loadCosmetics();
            Color.loadColors();
            Items.loadItems();
            Zone.loadZones();
            Token.loadTokens();
            Sound.loadSounds();
            Menu.loadMenus();
        }

        this.cosmeticsManager = new CosmeticsManager();
        this.cosmeticsManager.runTasks();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for(Player player : Bukkit.getOnlinePlayers()){
            if(player == null || !player.isOnline()) continue;
            PlayerCache playerCache = PlayerCache.getPlayer(player);
            sql.savePlayer(playerCache);
        }
        if(cosmeticsManager != null) {
            cosmeticsManager.cancelTasks();
        }

        if(sql != null){
            sql.disconnect();
        }

        for(BossBar bar : bossBar){
            bar.removeAll();
        }

    }

    public static CustomCosmetics getInstance() {
        return instance;
    }

    public FileCreator getConfig() {
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

    public void getCosmetic() {
        Utils.isNew();
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
}
