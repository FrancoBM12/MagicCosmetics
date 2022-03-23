package com.francobm.magicosmetics.database;

import com.francobm.magicosmetics.cache.PlayerCache;
import com.francobm.magicosmetics.files.FileCreator;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQL extends SQL{

    @Override
    public void connect() {
        FileCreator config = plugin.getConfig();
        if(isConnected()) return;
        try {
            String host = config.getString("MySQL.host");
            int port = config.getInt("MySQL.port");
            String user = config.getString("MySQL.user");
            String pass = config.getString("MySQL.password");
            String database = config.getString("MySQL.database");
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", user, pass);
            plugin.getLogger().info("MySQL connection established");
        } catch (SQLException throwable) {
            plugin.getLogger().severe("the database could not be established: " + throwable.getMessage());
            plugin.getPluginLoader().disablePlugin(plugin);
        }
    }

    @Override
    public void disconnect() {
        if(!isConnected()) return;
        try {
            connection.close();
            plugin.getLogger().info("MySQL connection closed");
        } catch (SQLException throwable) {
            plugin.getLogger().severe("the database could not be closed: " + throwable.getMessage());
        }
    }

    @Override
    public void createTable() {
        if(!isConnected()) return;
        FileCreator config = plugin.getConfig();
        try {
            String table = config.getString("MySQL.table");
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + table + " (id INTEGER PRIMARY KEY AUTOINCREMENT, UUID VARCHAR(255), Player VARCHAR(255), Hat VARCHAR(255), Bag VARCHAR(255), WStick VARCHAR(255), Balloon VARCHAR(255), Available VARCHAR(255))").executeUpdate();
            plugin.getLogger().info("MySQL table created successfully");
        } catch (SQLException throwable) {
            plugin.getLogger().severe("Could not create table: " + throwable.getMessage());
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
        if(!isConnected()) return;
        FileCreator config = plugin.getConfig();
        String table = config.getString("MySQL.table");
        try{
            if(!checkPlayerInfo(player)){
                String query = "INSERT INTO " + table + " (id, UUID, Player, Hat, Bag, WStick, Balloon, Available) VALUES(NULL, ?, ?, ?, ?, ?, ?, ?);";
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
            String query = "UPDATE " + table + " SET Player = ?, Hat = ?, Bag = ?, WStick = ?, Balloon = ?, Available = ? WHERE UUID = ?";
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
        if(!isConnected()) return;
        FileCreator config = plugin.getConfig();
        String table = config.getString("MySQL.table");
        String queryBuilder = "SELECT * FROM " + table + " WHERE UUID = ?";
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
                    }
                }.runTaskLater(plugin, 25L);
            }
        }catch (SQLException throwable){
            plugin.getLogger().severe("Failed to load player information: " + throwable.getMessage());
        }
    }

    private boolean checkPlayerInfo(PlayerCache playerCache){
        FileCreator config = plugin.getConfig();
        String table = config.getString("MySQL.table");
        String queryBuilder = "SELECT * FROM " + table + " WHERE UUID = ?;";
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
