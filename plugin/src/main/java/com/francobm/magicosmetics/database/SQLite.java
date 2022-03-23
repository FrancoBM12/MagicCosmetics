package com.francobm.magicosmetics.database;

import com.francobm.magicosmetics.cache.PlayerCache;
import com.francobm.magicosmetics.nms.bag.PlayerBag;
import com.francobm.magicosmetics.nms.balloon.PlayerBalloon;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
                connection.prepareStatement("CREATE TABLE IF NOT EXISTS player_cosmetics (id INTEGER PRIMARY KEY AUTOINCREMENT, UUID VARCHAR(255), Player VARCHAR(255), Hat VARCHAR(255), Bag VARCHAR(255), WStick VARCHAR(255), Balloon VARCHAR(255), Available VARCHAR(255))").executeUpdate();
                plugin.getLogger().info("SQLite table created successfully");
            } catch (SQLException throwable) {
                plugin.getLogger().severe("Could not create table: " + throwable.getMessage());
            }
        }
    }

    @Override
    public void loadPlayer(Player player){
        loadPlayerInfo(player);
    }

    @Override
    public void savePlayer(PlayerCache playerCache){
        savePlayerInfo(playerCache);
    }

    private void savePlayerInfo(PlayerCache player){
        try{
            if(!checkPlayerInfo(player)){
                String query = "INSERT INTO player_cosmetics (id, UUID, Player, Hat, Bag, WStick, Balloon, Available) VALUES(NULL, ?, ?, ?, ?, ?, ?, ?);";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, player.getUuid().toString());
                statement.setString(2, player.getOfflinePlayer().getName());
                statement.setString(3, player.getHat() == null ? "" : player.getHat().getId());
                statement.setString(4, player.getBag() == null ? "" : player.getBag().getId());
                statement.setString(5, player.getWStick() == null ? "" : player.getWStick().getId());
                statement.setString(6, player.getBalloon() == null ? "" : player.getBalloon().getId());
                statement.setString(7, player.saveCosmetics());
                statement.executeUpdate();
                return;
            }
            String query = "UPDATE player_cosmetics SET Player = ?, Hat = ?, Bag = ?, WStick = ?, Balloon = ?, Available = ? WHERE UUID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, player.getOfflinePlayer().getName());
            statement.setString(2, player.getHat() == null ? "" : player.getHat().getId());
            statement.setString(3, player.getBag() == null ? "" : player.getBag().getId());
            statement.setString(4, player.getWStick() == null ? "" : player.getWStick().getId());
            statement.setString(5, player.getBalloon() == null ? "" : player.getBalloon().getId());
            statement.setString(6, player.saveCosmetics());
            statement.setString(7, player.getUuid().toString());
            statement.executeUpdate();
        }catch (SQLException throwable) {
            plugin.getLogger().severe("Failed to save player information: " + throwable.getMessage());
        }
        player.clearCosmeticsInUse();
        PlayerCache.removePlayer(player);
        player = null;
    }

    private void loadPlayerInfo(Player player){
        String queryBuilder = "SELECT * FROM player_cosmetics WHERE UUID = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(queryBuilder);
            statement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = statement.executeQuery();
            PlayerCache playerCache = PlayerCache.getPlayer(player);
            if(resultSet == null){
                return;
            }
            if(resultSet.next()){
                String cosmetics = resultSet.getString("Available");
                String hat = resultSet.getString("Hat");
                String bag = resultSet.getString("Bag");
                String wStick = resultSet.getString("WStick");
                String balloon = resultSet.getString("Balloon");
                playerCache.loadCosmetics(cosmetics);
                playerCache.setCosmetic(playerCache.getCosmeticById(hat));
                playerCache.setCosmetic(playerCache.getCosmeticById(bag));
                playerCache.setCosmetic(playerCache.getCosmeticById(wStick));
                playerCache.setCosmetic(playerCache.getCosmeticById(balloon));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        playerCache.clearCosmeticsInUse();
                        PlayerBag.updatePlayerBag(player);
                        PlayerBalloon.updatePlayerBalloon(player);
                    }
                }.runTaskLater(plugin, 26L);
            }
        }catch (SQLException throwable){
            plugin.getLogger().severe("Failed to load player information: " + throwable.getMessage());
        }
    }

    private boolean checkPlayerInfo(PlayerCache playerCache){
        String queryBuilder = "SELECT * FROM player_cosmetics WHERE UUID = ?;";
        try {
            PreparedStatement statement = connection.prepareStatement(queryBuilder);
            statement.setString(1, playerCache.getUuid().toString());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet != null && resultSet.next()){
                return true;
            }
        }catch (SQLException throwable){
            plugin.getLogger().severe("Player information could not be verified.: " + throwable.getMessage());
        }
        return false;
    }

}
