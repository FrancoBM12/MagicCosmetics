package com.francobm.magicosmetics.database;

import com.francobm.magicosmetics.api.CosmeticType;
import com.francobm.magicosmetics.cache.PlayerData;
import com.francobm.magicosmetics.events.PlayerDataLoadEvent;
import com.francobm.magicosmetics.files.FileCreator;
import com.francobm.magicosmetics.nms.bag.EntityBag;
import com.francobm.magicosmetics.nms.bag.PlayerBag;
import com.francobm.magicosmetics.nms.balloon.EntityBalloon;
import com.francobm.magicosmetics.nms.balloon.PlayerBalloon;
import com.francobm.magicosmetics.nms.spray.CustomSpray;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MySQL extends SQL{

    private final String table;

    public MySQL() {
        FileCreator config = plugin.getConfig();
        String hostname = config.getString("MySQL.host");
        int port = config.getInt("MySQL.port");
        String username = config.getString("MySQL.user");
        String password = config.getString("MySQL.password");
        String database = config.getString("MySQL.database");
        String options = config.getString("MySQL.options");;
        table = config.getString("MySQL.table");;
        hikariCP = new HikariCP(hostname, port, username, password, database, options);
        hikariCP.setProperties(this);
        createTable();
    }

    @Override
    public void createTable() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = hikariCP.getHikariDataSource().getConnection();
            preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + table + "` (id INT AUTO_INCREMENT, UUID VARCHAR(255), Player VARCHAR(255), Hat VARCHAR(255), Bag VARCHAR(255), WStick VARCHAR(255), Balloon VARCHAR(255), Spray VARCHAR(255), Available VARCHAR(10000), PRIMARY KEY (id))");
            preparedStatement.executeUpdate();
            plugin.getLogger().info("MySQL table created successfully");
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            plugin.getLogger().severe("Could not create table: " + throwable.getMessage());
        } finally {
            closeConnections(preparedStatement, connection, null);
        }
    }

    @Override
    public void loadPlayer(Player player){
        loadPlayerInfo(player);
    }

    private void loadPlayerInfo(Player player){
        String queryBuilder = "SELECT * FROM " + table + " WHERE UUID = ?";
        if(plugin.isBungee()){
            EntityBag.updateEntityBag(player);
            EntityBalloon.updateEntityBalloon(player);
            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                connection = hikariCP.getHikariDataSource().getConnection();
                statement = connection.prepareStatement(queryBuilder);
                statement.setString(1, player.getUniqueId().toString());
                resultSet = statement.executeQuery();
                PlayerData playerData = PlayerData.getPlayer(player);
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
                    playerData.setOfflinePlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
                    playerData.loadCosmetics(cosmetics);
                    playerData.setCosmetic(CosmeticType.HAT, playerData.getCosmeticById(hat));
                    playerData.setCosmetic(CosmeticType.BAG,playerData.getCosmeticById(bag));
                    playerData.setCosmetic(CosmeticType.WALKING_STICK,playerData.getCosmeticById(wStick));
                    playerData.setCosmetic(CosmeticType.BALLOON, playerData.getCosmeticById(balloon));
                    playerData.setCosmetic(CosmeticType.SPRAY, playerData.getCosmeticById(spray));
                    CustomSpray.updateSpray(player);
                    PlayerBag.updatePlayerBag(player);
                    PlayerBalloon.updatePlayerBalloon(player);
                    plugin.getServer().getPluginManager().callEvent(new PlayerDataLoadEvent(playerData, playerData.cosmeticsInUse()));
                }
            }catch (SQLException throwable){
                plugin.getLogger().severe("Failed to load player information: " + throwable.getMessage());
            } finally {
                closeConnections(statement, connection, resultSet);
            }
            return;
        }
        EntityBag.updateEntityBag(player);
        EntityBalloon.updateEntityBalloon(player);
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = hikariCP.getHikariDataSource().getConnection();
            statement = connection.prepareStatement(queryBuilder);
            statement.setString(1, player.getUniqueId().toString());
            resultSet = statement.executeQuery();
            PlayerData playerData = PlayerData.getPlayer(player);
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
                playerData.setOfflinePlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
                playerData.loadCosmetics(cosmetics);
                playerData.setCosmetic(CosmeticType.HAT, playerData.getCosmeticById(hat));
                playerData.setCosmetic(CosmeticType.BAG,playerData.getCosmeticById(bag));
                playerData.setCosmetic(CosmeticType.WALKING_STICK,playerData.getCosmeticById(wStick));
                playerData.setCosmetic(CosmeticType.BALLOON, playerData.getCosmeticById(balloon));
                playerData.setCosmetic(CosmeticType.SPRAY, playerData.getCosmeticById(spray));
                CustomSpray.updateSpray(player);
                PlayerBag.updatePlayerBag(player);
                PlayerBalloon.updatePlayerBalloon(player);
                plugin.getServer().getPluginManager().callEvent(new PlayerDataLoadEvent(playerData, playerData.cosmeticsInUse()));
            }
        }catch (SQLException throwable){
            plugin.getLogger().severe("Failed to load player information: " + throwable.getMessage());
        } finally {
            closeConnections(statement, connection, resultSet);
        }
    }

    @Override
    public void savePlayer(PlayerData playerData, boolean close){
        savePlayerInfo(playerData, close);
    }

    private void savePlayerInfo(PlayerData player, boolean close){
        Connection connection = null;
        PreparedStatement statement = null;
        try{
            connection = hikariCP.getHikariDataSource().getConnection();
            if(!checkInfo(player.getUniqueId())){
                String query = "INSERT INTO " + table + " (id, UUID, Player, Hat, Bag, WStick, Balloon, Spray, Available) VALUES(NULL, ?, ?, ?, ?, ?, ?, ?, ?);";
                statement = connection.prepareStatement(query);
                statement.setString(1, player.getUniqueId().toString());
                statement.setString(2, player.getOfflinePlayer().getName());
                statement.setString(3, player.getHat() == null ? "" : player.getHat().getId());
                statement.setString(4, player.getBag() == null ? "" : player.getBag().getId());
                statement.setString(5, player.getWStick() == null ? "" : player.getWStick().getId());
                statement.setString(6, player.getBalloon() == null ? "" : player.getBalloon().getId());
                statement.setString(7, player.getSpray() == null ? "" : player.getSpray().getId());
                statement.setString(8, player.saveCosmetics());
                statement.executeUpdate();
                return;
            }
            String query = "UPDATE " + table + " SET Player = ?, Hat = ?, Bag = ?, WStick = ?, Balloon = ?, Spray = ?, Available = ? WHERE UUID = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, player.getOfflinePlayer().getName());
            statement.setString(2, player.getHat() == null ? "" : player.getHat().getId());
            statement.setString(3, player.getBag() == null ? "" : player.getBag().getId());
            statement.setString(4, player.getWStick() == null ? "" : player.getWStick().getId());
            statement.setString(5, player.getBalloon() == null ? "" : player.getBalloon().getId());
            statement.setString(6, player.getSpray() == null ? "" : player.getSpray().getId());
            statement.setString(7, player.saveCosmetics());
            statement.setString(8, player.getUniqueId().toString());
            statement.executeUpdate();
        }catch (SQLException throwable) {
            plugin.getLogger().severe("Failed to save player information: " + throwable.getMessage());
        } finally {
            closeConnections(statement, connection, null);
            if(close)
                player.clearCosmeticsToSaveData();
        }
    }

    @Override
    public void savePlayers() {
        Connection connection = null;
        PreparedStatement statement = null;
        try{
            connection = hikariCP.getHikariDataSource().getConnection();
            for(PlayerData player : PlayerData.players.values()){
                player.clearCosmeticsToSaveData();
                if(!checkInfo(player.getUniqueId())){
                    String query = "INSERT INTO " + table + " (id, UUID, Player, Hat, Bag, WStick, Balloon, Spray, Available) VALUES(NULL, ?, ?, ?, ?, ?, ?, ?, ?);";
                    statement = connection.prepareStatement(query);
                    statement.setString(1, player.getUniqueId().toString());
                    statement.setString(2, player.getOfflinePlayer().getName());
                    statement.setString(3, player.getHat() == null ? "" : player.getHat().getId());
                    statement.setString(4, player.getBag() == null ? "" : player.getBag().getId());
                    statement.setString(5, player.getWStick() == null ? "" : player.getWStick().getId());
                    statement.setString(6, player.getBalloon() == null ? "" : player.getBalloon().getId());
                    statement.setString(7, player.getSpray() == null ? "" : player.getSpray().getId());
                    statement.setString(8, player.saveCosmetics());
                    statement.executeUpdate();
                    return;
                }
                String query = "UPDATE " + table + " SET Player = ?, Hat = ?, Bag = ?, WStick = ?, Balloon = ?, Spray = ?, Available = ? WHERE UUID = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, player.getOfflinePlayer().getName());
                statement.setString(2, player.getHat() == null ? "" : player.getHat().getId());
                statement.setString(3, player.getBag() == null ? "" : player.getBag().getId());
                statement.setString(4, player.getWStick() == null ? "" : player.getWStick().getId());
                statement.setString(5, player.getBalloon() == null ? "" : player.getBalloon().getId());
                statement.setString(6, player.getSpray() == null ? "" : player.getSpray().getId());
                statement.setString(7, player.saveCosmetics());
                statement.setString(8, player.getUniqueId().toString());
                statement.executeUpdate();
            }
        }catch (SQLException throwable) {
            plugin.getLogger().severe("Failed to save player information: " + throwable.getMessage());
        } finally {
            closeConnections(statement, connection, null);
        }
    }

    @Override
    public CompletableFuture<Void> loadPlayerAsync(Player player) {
        return loadPlayerInfoAsync(player);
    }

    private CompletableFuture<Void> loadPlayerInfoAsync(Player player) {
        return CompletableFuture.runAsync(() -> {
            String queryBuilder = "SELECT * FROM " + table + " WHERE UUID = ?";
            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                connection = hikariCP.getHikariDataSource().getConnection();
                statement = connection.prepareStatement(queryBuilder);
                statement.setString(1, player.getUniqueId().toString());
                resultSet = statement.executeQuery();
                PlayerData playerData = PlayerData.getPlayer(player);
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
                    playerData.setOfflinePlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
                    playerData.loadCosmetics(cosmetics);
                    playerData.setCosmetic(CosmeticType.BAG,playerData.getCosmeticById(bag));
                    playerData.setCosmetic(CosmeticType.BALLOON, playerData.getCosmeticById(balloon));
                    playerData.setCosmetic(CosmeticType.SPRAY, playerData.getCosmeticById(spray));
                    EntityBag.updateEntityBag(player);
                    EntityBalloon.updateEntityBalloon(player);
                    CustomSpray.updateSpray(player);
                    PlayerBag.updatePlayerBag(player);
                    PlayerBalloon.updatePlayerBalloon(player);
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        playerData.setCosmetic(CosmeticType.HAT, playerData.getCosmeticById(hat));
                        playerData.setCosmetic(CosmeticType.WALKING_STICK,playerData.getCosmeticById(wStick));
                    });
                    plugin.getServer().getPluginManager().callEvent(new PlayerDataLoadEvent(playerData, playerData.cosmeticsInUse()));
                }
            }catch (SQLException throwable){
                plugin.getLogger().severe("Failed to load async player information: " + throwable.getMessage());
            } finally {
                closeConnections(statement, connection, resultSet);
            }
        });
    }

    @Override
    public CompletableFuture<Void> savePlayerAsync(PlayerData playerData) {
        return savePlayerInfoAsync(playerData);
    }

    private CompletableFuture<Void> savePlayerInfoAsync(PlayerData player){
        player.clearCosmeticsToSaveData();
        return checkInfoAsync(player.getUniqueId()).thenCompose(check -> CompletableFuture.runAsync(() -> {
            Connection connection = null;
            PreparedStatement statement = null;
            try{
                connection = hikariCP.getHikariDataSource().getConnection();
                if(!check){
                    String query = "INSERT INTO " + table + " (id, UUID, Player, Hat, Bag, WStick, Balloon, Spray, Available) VALUES(NULL, ?, ?, ?, ?, ?, ?, ?, ?);";
                    statement = connection.prepareStatement(query);
                    statement.setString(1, player.getUniqueId().toString());
                    statement.setString(2, player.getOfflinePlayer().getName());
                    statement.setString(3, player.getHat() == null ? "" : player.getHat().getId());
                    statement.setString(4, player.getBag() == null ? "" : player.getBag().getId());
                    statement.setString(5, player.getWStick() == null ? "" : player.getWStick().getId());
                    statement.setString(6, player.getBalloon() == null ? "" : player.getBalloon().getId());
                    statement.setString(7, player.getSpray() == null ? "" : player.getSpray().getId());
                    statement.setString(8, player.saveCosmetics());
                    statement.executeUpdate();
                    return;
                }
                String query = "UPDATE " + table + " SET Player = ?, Hat = ?, Bag = ?, WStick = ?, Balloon = ?, Spray = ?, Available = ? WHERE UUID = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, player.getOfflinePlayer().getName());
                statement.setString(2, player.getHat() == null ? "" : player.getHat().getId());
                statement.setString(3, player.getBag() == null ? "" : player.getBag().getId());
                statement.setString(4, player.getWStick() == null ? "" : player.getWStick().getId());
                statement.setString(5, player.getBalloon() == null ? "" : player.getBalloon().getId());
                statement.setString(6, player.getSpray() == null ? "" : player.getSpray().getId());
                statement.setString(7, player.saveCosmetics());
                statement.setString(8, player.getUniqueId().toString());
                statement.executeUpdate();
                //player.clearCosmeticsInUse(false);
                //PlayerData.removePlayer(player);
            }catch (SQLException throwable) {
                plugin.getLogger().severe("Failed to save player information: " + throwable.getMessage());
            } finally {
                closeConnections(statement, connection, null);
            }
        }));
    }

    private CompletableFuture<Boolean> checkInfoAsync(UUID uuid){
        return CompletableFuture.supplyAsync(() -> {
            String queryBuilder = "SELECT * FROM " + table + " WHERE UUID = ?";
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                connection = hikariCP.getHikariDataSource().getConnection();
                preparedStatement = connection.prepareStatement(queryBuilder);
                preparedStatement.setString(1, uuid.toString());
                resultSet = preparedStatement.executeQuery();
                if(resultSet != null && resultSet.next()){
                    return true;
                }
            }catch (SQLException throwable){
                plugin.getLogger().severe("Player information could not be verified.: " + throwable.getMessage());
            } finally {
                closeConnections(preparedStatement, connection, resultSet);
            }
            return false;
        });
    }

    private boolean checkInfo(UUID uuid){
        String queryBuilder = "SELECT * FROM " + table + " WHERE UUID = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = hikariCP.getHikariDataSource().getConnection();
            preparedStatement = connection.prepareStatement(queryBuilder);
            preparedStatement.setString(1, uuid.toString());
            resultSet = preparedStatement.executeQuery();
            if(resultSet != null && resultSet.next()){
                return true;
            }
        }catch (SQLException throwable){
            plugin.getLogger().severe("Player information could not be verified.: " + throwable.getMessage());
        } finally {
            closeConnections(preparedStatement, connection, resultSet);
        }
        return false;
    }

    @Override
    public DatabaseType getDatabaseType() {
        return DatabaseType.MYSQL;
    }
}
