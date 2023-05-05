package com.francobm.magicosmetics.database;

import com.francobm.magicosmetics.cache.EntityCache;
import com.francobm.magicosmetics.cache.PlayerData;
import com.francobm.magicosmetics.nms.bag.EntityBag;
import com.francobm.magicosmetics.nms.bag.PlayerBag;
import com.francobm.magicosmetics.nms.balloon.EntityBalloon;
import com.francobm.magicosmetics.nms.balloon.PlayerBalloon;
import com.francobm.magicosmetics.nms.spray.CustomSpray;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.*;
import java.util.UUID;

public class SQLite extends SQL {
    private final File fileSQL;
    public SQLite() {
        hikariCP = new HikariCP();
        fileSQL = new File(plugin.getDataFolder(), "cosmetics.db");
        hikariCP.setProperties(this);
        createTable();
    }

    @Override
    public void createTable() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = hikariCP.getHikariDataSource().getConnection();
            preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS player_cosmetics (id INTEGER PRIMARY KEY AUTOINCREMENT, UUID VARCHAR(255), Player VARCHAR(255), Hat VARCHAR(255), Bag VARCHAR(255), WStick VARCHAR(255), Balloon VARCHAR(255), Spray VARCHAR(255), Available VARCHAR(10000))");
            preparedStatement.executeUpdate();
            plugin.getLogger().info("SQLite table created successfully");
        } catch (SQLException throwable) {
            plugin.getLogger().severe("Could not create table: " + throwable.getMessage());
        } finally {
            closeConnections(preparedStatement, connection, null);
        }
    }

    @Override
    public void loadPlayer(Player player, boolean async) {
        loadPlayerInfo(player, async);
    }

    @Override
    public void savePlayer(PlayerData playerData, boolean close) {
        savePlayerInfo(playerData, close);
    }

    @Override
    public void asyncSavePlayer(PlayerData playerData) {
        asyncSavePlayerInfo(playerData);
    }

    @Override
    public void savePlayers() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try{
            connection = hikariCP.getHikariDataSource().getConnection();
            for(PlayerData player : PlayerData.players.values()){
                if(!checkInfo(player.getUniqueId())){
                    String query = "INSERT INTO player_cosmetics (id, UUID, Player, Hat, Bag, WStick, Balloon, Spray, Available) VALUES(NULL, ?, ?, ?, ?, ?, ?, ?, ?);";
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, player.getUniqueId().toString());
                    preparedStatement.setString(2, player.getOfflinePlayer().getName());
                    preparedStatement.setString(3, player.getHat() == null ? "" : player.getHat().getId());
                    preparedStatement.setString(4, player.getBag() == null ? "" : player.getBag().getId());
                    preparedStatement.setString(5, player.getWStick() == null ? "" : player.getWStick().getId());
                    preparedStatement.setString(6, player.getBalloon() == null ? "" : player.getBalloon().getId());
                    preparedStatement.setString(7, player.getSpray() == null ? "" : player.getSpray().getId());
                    preparedStatement.setString(8, player.saveCosmetics());
                    preparedStatement.executeUpdate();

                }else {
                    String query = "UPDATE player_cosmetics SET Player = ?, Hat = ?, Bag = ?, WStick = ?, Balloon = ?, Spray = ?, Available = ? WHERE UUID = ?";
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, player.getOfflinePlayer().getName());
                    preparedStatement.setString(2, player.getHat() == null ? "" : player.getHat().getId());
                    preparedStatement.setString(3, player.getBag() == null ? "" : player.getBag().getId());
                    preparedStatement.setString(4, player.getWStick() == null ? "" : player.getWStick().getId());
                    preparedStatement.setString(5, player.getBalloon() == null ? "" : player.getBalloon().getId());
                    preparedStatement.setString(6, player.getSpray() == null ? "" : player.getSpray().getId());
                    preparedStatement.setString(7, player.saveCosmetics());
                    preparedStatement.setString(8, player.getUniqueId().toString());
                    preparedStatement.executeUpdate();
                }
            }
        }catch (SQLException throwable) {
            plugin.getLogger().severe("Failed to save player information: " + throwable.getMessage());
        }finally {
            closeConnections(preparedStatement, connection, null);
            plugin.getLogger().info("Players data was saved.");
        }
    }

    private void savePlayerInfo(PlayerData player, boolean close){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        player.setOfflinePlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
        try{
            connection = hikariCP.getHikariDataSource().getConnection();
            if(!checkInfo(player.getUniqueId())){
                String query = "INSERT INTO player_cosmetics (id, UUID, Player, Hat, Bag, WStick, Balloon, Spray, Available) VALUES(NULL, ?, ?, ?, ?, ?, ?, ?, ?);";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setString(2, player.getOfflinePlayer().getName());
                preparedStatement.setString(3, player.getHat() == null ? "" : player.getHat().getId());
                preparedStatement.setString(4, player.getBag() == null ? "" : player.getBag().getId());
                preparedStatement.setString(5, player.getWStick() == null ? "" : player.getWStick().getId());
                preparedStatement.setString(6, player.getBalloon() == null ? "" : player.getBalloon().getId());
                preparedStatement.setString(7, player.getSpray() == null ? "" : player.getSpray().getId());
                preparedStatement.setString(8, player.saveCosmetics());
                preparedStatement.executeUpdate();

            }else {
                String query = "UPDATE player_cosmetics SET Player = ?, Hat = ?, Bag = ?, WStick = ?, Balloon = ?, Spray = ?, Available = ? WHERE UUID = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, player.getOfflinePlayer().getName());
                preparedStatement.setString(2, player.getHat() == null ? "" : player.getHat().getId());
                preparedStatement.setString(3, player.getBag() == null ? "" : player.getBag().getId());
                preparedStatement.setString(4, player.getWStick() == null ? "" : player.getWStick().getId());
                preparedStatement.setString(5, player.getBalloon() == null ? "" : player.getBalloon().getId());
                preparedStatement.setString(6, player.getSpray() == null ? "" : player.getSpray().getId());
                preparedStatement.setString(7, player.saveCosmetics());
                preparedStatement.setString(8, player.getUniqueId().toString());
                preparedStatement.executeUpdate();
            }
        }catch (SQLException throwable) {
            plugin.getLogger().severe("Failed to save player information: " + throwable.getMessage());
        }finally {
            closeConnections(preparedStatement, connection, null);
            if(close)
                player.clearCosmeticsInUse();
        }
    }

    private void asyncSavePlayerInfo(PlayerData player){
        player.setOfflinePlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            try{
                connection = hikariCP.getHikariDataSource().getConnection();
                if(!checkInfo(player.getUniqueId())){
                    String query = "INSERT INTO player_cosmetics (id, UUID, Player, Hat, Bag, WStick, Balloon, Spray, Available) VALUES(NULL, ?, ?, ?, ?, ?, ?, ?, ?);";
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, player.getUniqueId().toString());
                    preparedStatement.setString(2, player.getOfflinePlayer().getName());
                    preparedStatement.setString(3, player.getHat() == null ? "" : player.getHat().getId());
                    preparedStatement.setString(4, player.getBag() == null ? "" : player.getBag().getId());
                    preparedStatement.setString(5, player.getWStick() == null ? "" : player.getWStick().getId());
                    preparedStatement.setString(6, player.getBalloon() == null ? "" : player.getBalloon().getId());
                    preparedStatement.setString(7, player.getSpray() == null ? "" : player.getSpray().getId());
                    preparedStatement.setString(8, player.saveCosmetics());
                    preparedStatement.executeUpdate();
                }else {
                    String query = "UPDATE player_cosmetics SET Player = ?, Hat = ?, Bag = ?, WStick = ?, Balloon = ?, Spray = ?, Available = ? WHERE UUID = ?";
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, player.getOfflinePlayer().getName());
                    preparedStatement.setString(2, player.getHat() == null ? "" : player.getHat().getId());
                    preparedStatement.setString(3, player.getBag() == null ? "" : player.getBag().getId());
                    preparedStatement.setString(4, player.getWStick() == null ? "" : player.getWStick().getId());
                    preparedStatement.setString(5, player.getBalloon() == null ? "" : player.getBalloon().getId());
                    preparedStatement.setString(6, player.getSpray() == null ? "" : player.getSpray().getId());
                    preparedStatement.setString(7, player.saveCosmetics());
                    preparedStatement.setString(8, player.getUniqueId().toString());
                    preparedStatement.executeUpdate();
                    //player.clearCosmeticsInUse(false);
                    //PlayerData.removePlayer(player);
                }
            }catch (SQLException throwable) {
                plugin.getLogger().severe("Failed to save player information: " + throwable.getMessage());
            } finally {
                closeConnections(preparedStatement, connection, null);
                player.clearCosmeticsInUse();
            }
        });
    }

    @Override
    public void loadEntity(UUID uuid) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String queryBuilder = "SELECT * FROM player_cosmetics WHERE UUID = ?";
        try {
            connection = hikariCP.getHikariDataSource().getConnection();
            preparedStatement = connection.prepareStatement(queryBuilder);
            preparedStatement.setString(1, uuid.toString());
            resultSet = preparedStatement.executeQuery();
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
        } finally {
            closeConnections(preparedStatement, connection, resultSet);
        }
    }

    private void loadPlayerInfo(Player player, boolean async){
        String queryBuilder = "SELECT * FROM player_cosmetics WHERE UUID = ?";
        if(async){
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                Connection connection = null;
                PreparedStatement preparedStatement = null;
                ResultSet resultSet = null;
                try{
                    connection = hikariCP.getHikariDataSource().getConnection();
                    preparedStatement = connection.prepareStatement(queryBuilder);
                    preparedStatement.setString(1, player.getUniqueId().toString());
                    resultSet = preparedStatement.executeQuery();
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

                        PlayerData playerData = PlayerData.getPlayer(player);
                        playerData.setOfflinePlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
                        playerData.loadCosmetics(cosmetics);
                        playerData.setCosmetic(playerData.getCosmeticById(hat));
                        playerData.setCosmetic(playerData.getCosmeticById(bag));
                        playerData.setCosmetic(playerData.getCosmeticById(wStick));
                        playerData.setCosmetic(playerData.getCosmeticById(balloon));
                        playerData.setCosmetic(playerData.getCosmeticById(spray));
                        //playerData.clearCosmeticsInUse(false);
                        CustomSpray.updateSpray(player);
                        PlayerBag.updatePlayerBag(player);
                        PlayerBalloon.updatePlayerBalloon(player);
                        if(plugin.isCitizens()) {
                            EntityBag.updateEntityBag(player);
                            EntityBalloon.updateEntityBalloon(player);
                        }
                    }
                }catch (SQLException throwable){
                    plugin.getLogger().severe("Failed to load async player information: " + throwable.getMessage());
                } finally {
                    closeConnections(preparedStatement, connection, resultSet);
                }
            });
            return;
        }
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = hikariCP.getHikariDataSource().getConnection();
            preparedStatement = connection.prepareStatement(queryBuilder);
            preparedStatement.setString(1, player.getUniqueId().toString());
            resultSet = preparedStatement.executeQuery();
            if(resultSet == null){
                return;
            }
            PlayerData playerData = PlayerData.getPlayer(player);
            if(resultSet.next()){
                String cosmetics = resultSet.getString("Available");
                String hat = resultSet.getString("Hat");
                String bag = resultSet.getString("Bag");
                String wStick = resultSet.getString("WStick");
                String balloon = resultSet.getString("Balloon");
                String spray = resultSet.getString("Spray");
                playerData.setOfflinePlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
                playerData.loadCosmetics(cosmetics);
                playerData.setCosmetic(playerData.getCosmeticById(hat));
                playerData.setCosmetic(playerData.getCosmeticById(bag));
                playerData.setCosmetic(playerData.getCosmeticById(wStick));
                playerData.setCosmetic(playerData.getCosmeticById(balloon));
                playerData.setCosmetic(playerData.getCosmeticById(spray));
                playerData.clearCosmeticsInUse();
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
        } finally {
            closeConnections(preparedStatement, connection, resultSet);
        }
    }

    private boolean checkInfo(UUID uuid){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String queryBuilder = "SELECT * FROM player_cosmetics WHERE UUID = ?";
        try {
            connection = hikariCP.getHikariDataSource().getConnection();
            preparedStatement = connection.prepareStatement(queryBuilder);
            preparedStatement.setString(1, uuid.toString());
            resultSet = preparedStatement.executeQuery();
            if(resultSet != null && resultSet.next()){
                return true;
            }
        }catch (SQLException throwable){
            //plugin.getLogger().severe("Player information could not be verified.: " + throwable.getMessage());
        } finally {
            closeConnections(preparedStatement, connection, resultSet);
        }
        return false;
    }

    @Override
    public void removeEntity(UUID uuid) {
        if(!checkInfo(uuid)) return;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String queryBuilder = "DELETE FROM player_cosmetics WHERE UUID = ?";
        try {
            connection = hikariCP.getHikariDataSource().getConnection();
            preparedStatement = connection.prepareStatement(queryBuilder);
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.executeUpdate();
        }catch (SQLException throwable){
            plugin.getLogger().severe("Failed to remove entity information: " + throwable.getMessage());
        } finally {
            closeConnections(preparedStatement, connection, null);
        }
    }

    @Override
    public void saveEntities() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try{
            connection = hikariCP.getHikariDataSource().getConnection();
            for(EntityCache entityCache : EntityCache.entities.values()){
                if(!checkInfo(entityCache.getUniqueId())){
                    String query = "INSERT INTO player_cosmetics (id, UUID, Player, Hat, Bag, WStick, Balloon, Spray, Available) VALUES(NULL, ?, ?, ?, ?, ?, ?, ?, ?);";
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, entityCache.getUniqueId().toString());
                    preparedStatement.setString(2, "[NPC]");
                    preparedStatement.setString(3, entityCache.saveHat());
                    preparedStatement.setString(4, entityCache.saveBag());
                    preparedStatement.setString(5, entityCache.saveWStick());
                    preparedStatement.setString(6, entityCache.saveBalloon());
                    preparedStatement.setString(7, "");
                    preparedStatement.setString(8, "");
                    preparedStatement.executeUpdate();
                }else {
                    String query = "UPDATE player_cosmetics SET Player = ?, Hat = ?, Bag = ?, WStick = ?, Balloon = ?, Spray = ?, Available = ? WHERE UUID = ?";
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, "[NPC]");
                    preparedStatement.setString(2, entityCache.saveHat());
                    preparedStatement.setString(3, entityCache.saveBag());
                    preparedStatement.setString(4, entityCache.saveWStick());
                    preparedStatement.setString(5, entityCache.saveBalloon());
                    preparedStatement.setString(6, "");
                    preparedStatement.setString(7, "");
                    preparedStatement.setString(8, entityCache.getUniqueId().toString());
                    preparedStatement.executeUpdate();
                }
            }
        }catch (SQLException throwable) {
            plugin.getLogger().severe("Failed to save entity information: " + throwable.getMessage());
        } finally {
            closeConnections(preparedStatement, connection, null);
            plugin.getLogger().info("Entities/NPC data was saved.");
        }
    }

    @Override
    public void saveEntity(EntityCache entityCache) {
        saveEntityInfo(entityCache);
    }

    private void saveEntityInfo(EntityCache entityCache){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try{
            connection = hikariCP.getHikariDataSource().getConnection();
            if(!checkInfo(entityCache.getUniqueId())){
                String query = "INSERT INTO player_cosmetics (id, UUID, Player, Hat, Bag, WStick, Balloon, Spray, Available) VALUES(NULL, ?, ?, ?, ?, ?, ?, ?, ?);";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, entityCache.getUniqueId().toString());
                preparedStatement.setString(2, "[NPC]");
                preparedStatement.setString(3, entityCache.saveHat());
                preparedStatement.setString(4, entityCache.saveBag());
                preparedStatement.setString(5, entityCache.saveWStick());
                preparedStatement.setString(6, entityCache.saveBalloon());
                preparedStatement.setString(7, "");
                preparedStatement.setString(8, "");
                preparedStatement.executeUpdate();
            }else {
                String query = "UPDATE player_cosmetics SET Player = ?, Hat = ?, Bag = ?, WStick = ?, Balloon = ?, Spray = ?, Available = ? WHERE UUID = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, "[NPC]");
                preparedStatement.setString(2, entityCache.saveHat());
                preparedStatement.setString(3, entityCache.saveBag());
                preparedStatement.setString(4, entityCache.saveWStick());
                preparedStatement.setString(5, entityCache.saveBalloon());
                preparedStatement.setString(6, "");
                preparedStatement.setString(7, "");
                preparedStatement.setString(8, entityCache.getUniqueId().toString());
                preparedStatement.executeUpdate();
            }
        }catch (SQLException throwable) {
            plugin.getLogger().severe("Failed to save entity information: " + throwable.getMessage());
        } finally {
            closeConnections(preparedStatement, connection, null);
        }
    }

    @Override
    public void asyncSaveEntity(EntityCache entityCache) {
        asyncSaveEntityInfo(entityCache);
    }

    private void asyncSaveEntityInfo(EntityCache entityCache){
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            try{
                connection = hikariCP.getHikariDataSource().getConnection();
                if(!checkInfo(entityCache.getUniqueId())){
                    String query = "INSERT INTO player_cosmetics (id, UUID, Player, Hat, Bag, WStick, Balloon, Spray, Available) VALUES(NULL, ?, ?, ?, ?, ?, ?, ?, ?);";
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, entityCache.getUniqueId().toString());
                    preparedStatement.setString(2, "[NPC]");
                    preparedStatement.setString(3, entityCache.saveHat());
                    preparedStatement.setString(4, entityCache.saveBag());
                    preparedStatement.setString(5, entityCache.saveWStick());
                    preparedStatement.setString(6, entityCache.saveBalloon());
                    preparedStatement.setString(7, "");
                    preparedStatement.setString(8, "");
                    preparedStatement.executeUpdate();
                }else {
                    String query = "UPDATE player_cosmetics SET Player = ?, Hat = ?, Bag = ?, WStick = ?, Balloon = ?, Spray = ?, Available = ? WHERE UUID = ?";
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, "[NPC]");
                    preparedStatement.setString(2, entityCache.saveHat());
                    preparedStatement.setString(3, entityCache.saveBag());
                    preparedStatement.setString(4, entityCache.saveWStick());
                    preparedStatement.setString(5, entityCache.saveBalloon());
                    preparedStatement.setString(6, "");
                    preparedStatement.setString(7, "");
                    preparedStatement.setString(8, entityCache.getUniqueId().toString());
                    preparedStatement.executeUpdate();
                }
            }catch (SQLException throwable) {
                plugin.getLogger().severe("Failed to save entity information: " + throwable.getMessage());
            } finally {
                closeConnections(preparedStatement, connection, null);
                entityCache.clearCosmeticsInUse();
            }
            //EntityCache.removeEntity(entityCache.getUniqueId());
        });
    }

    @Override
    public DatabaseType getDatabaseType() {
        return DatabaseType.SQLITE;
    }

    public File getFileSQL() {
        return fileSQL;
    }
}
