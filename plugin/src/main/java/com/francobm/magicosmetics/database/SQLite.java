package com.francobm.magicosmetics.database;

import com.francobm.magicosmetics.cache.EntityCache;
import com.francobm.magicosmetics.cache.Cosmetic;
import com.francobm.magicosmetics.cache.PlayerCache;
import com.francobm.magicosmetics.files.FileCreator;
import com.francobm.magicosmetics.nms.bag.EntityBag;
import com.francobm.magicosmetics.nms.bag.PlayerBag;
import com.francobm.magicosmetics.nms.balloon.EntityBalloon;
import com.francobm.magicosmetics.nms.balloon.PlayerBalloon;
import com.francobm.magicosmetics.nms.spray.CustomSpray;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLite extends SQL {

    @Override
    public void connect() {
        try {
            plugin.getLogger().info("Connecting the database with SQLite...");
            File FileSQL = new File(plugin.getDataFolder(), "cosmetics.db");
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + FileSQL);
            plugin.getLogger().info("SQLite connection established");
        } catch (Exception e) {
            plugin.getLogger().severe("the database could not be established: " + e.getMessage());
            this.plugin.getPluginLoader().disablePlugin(this.plugin);
        }
    }

    @Override
    public void disconnect() {
        if(isConnected()){
            try {
                connection.close();
                plugin.getLogger().info("SQLite connection closed");
            } catch (SQLException throwable) {
                plugin.getLogger().severe("the database could not be closed: " + throwable.getMessage());
            }
        }
    }

    @Override
    public void createTable() {
        if(isConnected()) {
            try {
                connection.prepareStatement("CREATE TABLE IF NOT EXISTS player_cosmetics (id INTEGER PRIMARY KEY AUTOINCREMENT, UUID VARCHAR(255), Player VARCHAR(255), Hat VARCHAR(255), Bag VARCHAR(255), WStick VARCHAR(255), Balloon VARCHAR(255), Spray VARCHAR(255), Available VARCHAR(255))").executeUpdate();
                plugin.getLogger().info("SQLite table created successfully");
            } catch (SQLException throwable) {
                plugin.getLogger().severe("Could not create table: " + throwable.getMessage());
            }
        }
    }

    @Override
    public void loadPlayer(Player player, boolean async) {
        loadPlayerInfo(player, async);
    }

    @Override
    public void savePlayer(PlayerCache playerCache, boolean close) {
        savePlayerInfo(playerCache, close);
    }

    private void savePlayerInfo(PlayerCache player, boolean close){
        try{
            if(!checkInfo(player.getUuid())){
                String query = "INSERT INTO player_cosmetics (id, UUID, Player, Hat, Bag, WStick, Balloon, Spray, Available) VALUES(NULL, ?, ?, ?, ?, ?, ?, ?, ?);";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, player.getUuid().toString());
                statement.setString(2, player.getOfflinePlayer().getName());
                statement.setString(3, player.getHat() == null ? "" : player.getHat().getId());
                statement.setString(4, player.getBag() == null ? "" : player.getBag().getId());
                statement.setString(5, player.getWStick() == null ? "" : player.getWStick().getId());
                statement.setString(6, player.getBalloon() == null ? "" : player.getBalloon().getId());
                statement.setString(7, player.getSpray() == null ? "" : player.getSpray().getId());
                statement.setString(8, player.saveCosmetics());
                statement.executeUpdate();

            }else {
                String query = "UPDATE player_cosmetics SET Player = ?, Hat = ?, Bag = ?, WStick = ?, Balloon = ?, Spray = ?, Available = ? WHERE UUID = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, player.getOfflinePlayer().getName());
                statement.setString(2, player.getHat() == null ? "" : player.getHat().getId());
                statement.setString(3, player.getBag() == null ? "" : player.getBag().getId());
                statement.setString(4, player.getWStick() == null ? "" : player.getWStick().getId());
                statement.setString(5, player.getBalloon() == null ? "" : player.getBalloon().getId());
                statement.setString(6, player.getSpray() == null ? "" : player.getSpray().getId());
                statement.setString(7, player.saveCosmetics());
                statement.setString(8, player.getUuid().toString());
                statement.executeUpdate();
            }
        }catch (SQLException throwable) {
            plugin.getLogger().severe("Failed to save player information: " + throwable.getMessage());
        }
        player.clearCosmeticsInUse(close);
        PlayerCache.removePlayer(player);
    }

    @Override
    public void asyncSavePlayer(PlayerCache playerCache) {
        asyncSavePlayerInfo(playerCache);
    }

    private void asyncSavePlayerInfo(PlayerCache player){
        new BukkitRunnable() {
            @Override
            public void run() {
                try{
                    if(!checkInfo(player.getUuid())){
                        String query = "INSERT INTO player_cosmetics (id, UUID, Player, Hat, Bag, WStick, Balloon, Spray, Available) VALUES(NULL, ?, ?, ?, ?, ?, ?, ?, ?);";
                        PreparedStatement statement = connection.prepareStatement(query);
                        statement.setString(1, player.getUuid().toString());
                        statement.setString(2, player.getOfflinePlayer().getName());
                        statement.setString(3, player.getHat() == null ? "" : player.getHat().getId());
                        statement.setString(4, player.getBag() == null ? "" : player.getBag().getId());
                        statement.setString(5, player.getWStick() == null ? "" : player.getWStick().getId());
                        statement.setString(6, player.getBalloon() == null ? "" : player.getBalloon().getId());
                        statement.setString(7, player.getSpray() == null ? "" : player.getSpray().getId());
                        statement.setString(8, player.saveCosmetics());
                        statement.executeUpdate();
                    }else {
                        String query = "UPDATE player_cosmetics SET Player = ?, Hat = ?, Bag = ?, WStick = ?, Balloon = ?, Spray = ?, Available = ? WHERE UUID = ?";
                        PreparedStatement statement = connection.prepareStatement(query);
                        statement.setString(1, player.getOfflinePlayer().getName());
                        statement.setString(2, player.getHat() == null ? "" : player.getHat().getId());
                        statement.setString(3, player.getBag() == null ? "" : player.getBag().getId());
                        statement.setString(4, player.getWStick() == null ? "" : player.getWStick().getId());
                        statement.setString(5, player.getBalloon() == null ? "" : player.getBalloon().getId());
                        statement.setString(6, player.getSpray() == null ? "" : player.getSpray().getId());
                        statement.setString(7, player.saveCosmetics());
                        statement.setString(8, player.getUuid().toString());
                        statement.executeUpdate();
                    }
                }catch (SQLException throwable) {
                    plugin.getLogger().severe("Failed to save player information: " + throwable.getMessage());
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    @Override
    public void loadEntity(UUID uuid) {
        String queryBuilder = "SELECT * FROM player_cosmetics WHERE UUID = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(queryBuilder);
            statement.setString(1, uuid.toString());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet == null){
                return;
            }
            EntityCache entityCache = EntityCache.getOrCreateEntity(uuid);
            if(resultSet.next()){
                String hat = resultSet.getString("Hat");
                String bag = resultSet.getString("Bag");
                String wStick = resultSet.getString("WStick");
                String balloon = resultSet.getString("Balloon");
                String spray = resultSet.getString("Spray");
                String ids = hat + "," + bag + "," + wStick + "," + balloon + "," + spray;
                entityCache.loadCosmetics(ids);
            }
        }catch (SQLException throwable){
            plugin.getLogger().severe("Failed to load entity information: " + throwable.getMessage());
        }
    }

    private void loadPlayerInfo(Player player, boolean async){
        String queryBuilder = "SELECT * FROM player_cosmetics WHERE UUID = ?";
        if(async){
            new BukkitRunnable() {
                @Override
                public void run() {
                    try{
                        PreparedStatement statement = connection.prepareStatement(queryBuilder);
                        statement.setString(1, player.getUniqueId().toString());
                        ResultSet resultSet = statement.executeQuery();
                        if(resultSet == null){
                            return;
                        }
                        if(resultSet.next()){
                            String cosmetics = resultSet.getString("Available");
                            String hat = resultSet.getString("Hat");
                            String bag = resultSet.getString("Bag");
                            String wStick = resultSet.getString("WStick");
                            String balloon = resultSet.getString("Balloon");
                            String spray = resultSet.getString("Spray");
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    PlayerCache playerCache = PlayerCache.getPlayer(player);
                                    playerCache.loadCosmetics(cosmetics);
                                    playerCache.setCosmetic(playerCache.getCosmeticById(hat));
                                    playerCache.setCosmetic(playerCache.getCosmeticById(bag));
                                    playerCache.setCosmetic(playerCache.getCosmeticById(wStick));
                                    playerCache.setCosmetic(playerCache.getCosmeticById(balloon));
                                    playerCache.setCosmetic(playerCache.getCosmeticById(spray));
                                    playerCache.clearCosmeticsInUse(false);
                                    CustomSpray.updateSpray(player);
                                    PlayerBag.updatePlayerBag(player);
                                    PlayerBalloon.updatePlayerBalloon(player);
                                    if(plugin.isCitizens()) {
                                        EntityBag.updateEntityBag(player);
                                        EntityBalloon.updateEntityBalloon(player);
                                    }
                                }
                            }.runTask(plugin);
                        }
                    }catch (SQLException throwable){
                        plugin.getLogger().severe("Failed to load async player information: " + throwable.getMessage());
                    }
                }
            }.runTaskAsynchronously(plugin);
            return;
        }
        try {
            PreparedStatement statement = connection.prepareStatement(queryBuilder);
            statement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet == null){
                return;
            }
            PlayerCache playerCache = PlayerCache.getPlayer(player);
            if(resultSet.next()){
                String cosmetics = resultSet.getString("Available");
                String hat = resultSet.getString("Hat");
                String bag = resultSet.getString("Bag");
                String wStick = resultSet.getString("WStick");
                String balloon = resultSet.getString("Balloon");
                String spray = resultSet.getString("Spray");
                playerCache.loadCosmetics(cosmetics);
                playerCache.setCosmetic(playerCache.getCosmeticById(hat));
                playerCache.setCosmetic(playerCache.getCosmeticById(bag));
                playerCache.setCosmetic(playerCache.getCosmeticById(wStick));
                playerCache.setCosmetic(playerCache.getCosmeticById(balloon));
                playerCache.setCosmetic(playerCache.getCosmeticById(spray));
                playerCache.clearCosmeticsInUse(false);
                CustomSpray.updateSpray(player);
                PlayerBag.updatePlayerBag(player);
                PlayerBalloon.updatePlayerBalloon(player);
                if(plugin.isCitizens()) {
                    EntityBag.updateEntityBag(player);
                    EntityBalloon.updateEntityBalloon(player);
                }
            }
        }catch (SQLException throwable){
            plugin.getLogger().severe("Failed to load player information: " + throwable.getMessage());
        }
    }

    private boolean checkInfo(UUID uuid){
        String queryBuilder = "SELECT * FROM player_cosmetics WHERE UUID = ?;";
        try {
            PreparedStatement statement = connection.prepareStatement(queryBuilder);
            statement.setString(1, uuid.toString());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet != null && resultSet.next()){
                return true;
            }
        }catch (SQLException throwable){
            //plugin.getLogger().severe("Player information could not be verified.: " + throwable.getMessage());
        }
        return false;
    }

    @Override
    public void removeEntity(UUID uuid) {
        if(!checkInfo(uuid)) return;
        String queryBuilder = "DELETE FROM player_cosmetics WHERE UUID = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(queryBuilder);
            statement.setString(1, uuid.toString());
            statement.executeUpdate();
        }catch (SQLException throwable){
            plugin.getLogger().severe("Failed to remove entity information: " + throwable.getMessage());
        }
    }

    @Override
    public void saveEntity(EntityCache entityCache) {
        saveEntityInfo(entityCache);
    }

    private void saveEntityInfo(EntityCache entityCache){
        try{
            if(!checkInfo(entityCache.getUniqueId())){
                String query = "INSERT INTO player_cosmetics (id, UUID, Player, Hat, Bag, WStick, Balloon, Spray, Available) VALUES(NULL, ?, ?, ?, ?, ?, ?, ?, ?);";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, entityCache.getUniqueId().toString());
                statement.setString(2, "[NPC]");
                statement.setString(3, entityCache.saveHat());
                statement.setString(4, entityCache.saveBag());
                statement.setString(5, entityCache.saveWStick());
                statement.setString(6, entityCache.saveBalloon());
                statement.setString(7, "");
                statement.setString(8, "");
                statement.executeUpdate();
            }else {
                String query = "UPDATE player_cosmetics SET Player = ?, Hat = ?, Bag = ?, WStick = ?, Balloon = ?, Spray = ?, Available = ? WHERE UUID = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, "[NPC]");
                statement.setString(2, entityCache.saveHat());
                statement.setString(3, entityCache.saveBag());
                statement.setString(4, entityCache.saveWStick());
                statement.setString(5, entityCache.saveBalloon());
                statement.setString(6, "");
                statement.setString(7, "");
                statement.setString(8, entityCache.getUniqueId().toString());
                statement.executeUpdate();
            }
        }catch (SQLException throwable) {
            plugin.getLogger().severe("Failed to save entity information: " + throwable.getMessage());
        }
        entityCache.clearCosmeticsInUse();
        //EntityCache.removeEntity(entityCache.getUniqueId());
        entityCache = null;
    }

    @Override
    public void asyncSaveEntity(EntityCache entityCache) {
        asyncSaveEntityInfo(entityCache);
    }

    private void asyncSaveEntityInfo(EntityCache entityCache){
        new BukkitRunnable() {
            @Override
            public void run() {
                try{
                    if(!checkInfo(entityCache.getUniqueId())){
                        String query = "INSERT INTO player_cosmetics (id, UUID, Player, Hat, Bag, WStick, Balloon, Spray, Available) VALUES(NULL, ?, ?, ?, ?, ?, ?, ?, ?);";
                        PreparedStatement statement = connection.prepareStatement(query);
                        statement.setString(1, entityCache.getUniqueId().toString());
                        statement.setString(2, "[NPC]");
                        statement.setString(3, entityCache.saveHat());
                        statement.setString(4, entityCache.saveBag());
                        statement.setString(5, entityCache.saveWStick());
                        statement.setString(6, entityCache.saveBalloon());
                        statement.setString(7, "");
                        statement.setString(8, "");
                        statement.executeUpdate();
                    }else {
                        String query = "UPDATE player_cosmetics SET Player = ?, Hat = ?, Bag = ?, WStick = ?, Balloon = ?, Spray = ?, Available = ? WHERE UUID = ?";
                        PreparedStatement statement = connection.prepareStatement(query);
                        statement.setString(1, "[NPC]");
                        statement.setString(2, entityCache.saveHat());
                        statement.setString(3, entityCache.saveBag());
                        statement.setString(4, entityCache.saveWStick());
                        statement.setString(5, entityCache.saveBalloon());
                        statement.setString(6, "");
                        statement.setString(7, "");
                        statement.setString(8, entityCache.getUniqueId().toString());
                        statement.executeUpdate();
                    }
                }catch (SQLException throwable) {
                    plugin.getLogger().severe("Failed to save entity information: " + throwable.getMessage());
                }
                entityCache.clearCosmeticsInUse();
                //EntityCache.removeEntity(entityCache.getUniqueId());
            }
        }.runTaskAsynchronously(plugin);
    }
}
