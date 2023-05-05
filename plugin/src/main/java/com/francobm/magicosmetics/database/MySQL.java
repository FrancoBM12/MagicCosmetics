package com.francobm.magicosmetics.database;

import com.francobm.magicosmetics.cache.EntityCache;
import com.francobm.magicosmetics.cache.PlayerData;
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

public class MySQL extends SQL{

    private final String table;

    public MySQL() {
        FileCreator config = plugin.getConfig();
        String hostname = config.getString("MySQL.host");
        int port = config.getInt("MySQL.port");
        String username = config.getString("MySQL.user");
        String password = config.getString("MySQL.password");
        String database = config.getString("MySQL.database");
        table = config.getString("MySQL.table");;
        hikariCP = new HikariCP(hostname, port, username, password, database);
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
    public void loadPlayer(Player player, boolean async){
        loadPlayerInfo(player, async);
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
                player.clearCosmeticsInUse();
        }
    }

    @Override
    public void savePlayers() {
        Connection connection = null;
        PreparedStatement statement = null;
        try{
            connection = hikariCP.getHikariDataSource().getConnection();
            for(PlayerData player : PlayerData.players.values()){
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
            plugin.getLogger().info("Players data was saved.");
        }
    }

    @Override
    public void asyncSavePlayer(PlayerData playerData) {
        asyncSavePlayerInfo(playerData);
    }

    private void asyncSavePlayerInfo(PlayerData player){
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
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
                //player.clearCosmeticsInUse(false);
                //PlayerData.removePlayer(player);
            }catch (SQLException throwable) {
                plugin.getLogger().severe("Failed to save player information: " + throwable.getMessage());
            } finally {
                closeConnections(statement, connection, null);
                player.clearCosmeticsInUse();
            }
        });
    }

    @Override
    public void loadEntity(UUID uuid) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String queryBuilder = "SELECT * FROM " + table + " WHERE UUID = ?";
        try {
            connection = hikariCP.getHikariDataSource().getConnection();
            statement = connection.prepareStatement(queryBuilder);
            statement.setString(1, uuid.toString());
            resultSet = statement.executeQuery();
            EntityCache entityCache = EntityCache.getOrCreateEntity(uuid);
            if(resultSet == null){
                return;
            }
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
            closeConnections(statement, connection, resultSet);
        }
    }

    private void loadPlayerInfo(Player player, boolean async){
        String queryBuilder = "SELECT * FROM " + table + " WHERE UUID = ?";
        if(plugin.isBungee()){
            if(async){
                plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                    Connection connection = null;
                    PreparedStatement statement = null;
                    ResultSet resultSet = null;
                    try {
                        connection = hikariCP.getHikariDataSource().getConnection();
                        statement = connection.prepareStatement(queryBuilder);
                        statement.setString(1, player.getUniqueId().toString());
                        resultSet = statement.executeQuery();
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
                        closeConnections(statement, connection, resultSet);
                    }
                }, 20L);
                return;
            }
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
                closeConnections(statement, connection, resultSet);
            }
            return;
        }
        if(async){
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
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
                    plugin.getLogger().severe("Failed to load async player information: " + throwable.getMessage());
                } finally {
                    closeConnections(statement, connection, resultSet);
                }
            });
            return;
        }
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
            closeConnections(statement, connection, resultSet);
        }
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
    public void removeEntity(UUID uuid) {
        if(!checkInfo(uuid)) return;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String queryBuilder = "DELETE FROM " + table + " WHERE UUID = ?";
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
        PreparedStatement statement = null;
        try{
            connection = hikariCP.getHikariDataSource().getConnection();
            for(EntityCache entityCache : EntityCache.entities.values()){
                if(!checkInfo(entityCache.getUniqueId())){
                    String query = "INSERT INTO " + table + " (id, UUID, Player, Hat, Bag, WStick, Balloon, Spray, Available) VALUES(NULL, ?, ?, ?, ?, ?, ?, ?, ?);";
                    statement = connection.prepareStatement(query);
                    statement.setString(1, entityCache.getUniqueId().toString());
                    statement.setString(2, "[NPC]");
                    statement.setString(3, entityCache.getHat() == null ? "" : entityCache.getHat().getId() + "|" + entityCache.getHat().getColor().asRGB());
                    statement.setString(4, entityCache.getBag() == null ? "" : entityCache.getBag().getId() + "|" + entityCache.getBag().getColor().asRGB());
                    statement.setString(5, entityCache.getWStick() == null ? "" : entityCache.getWStick().getId() + "|" + entityCache.getWStick().getColor().asRGB());
                    statement.setString(6, entityCache.getBalloon() == null ? "" : entityCache.getBalloon().getId() + "|" + entityCache.getBalloon().getColor().asRGB());
                    statement.setString(7, "");
                    statement.setString(8, "");
                    statement.executeUpdate();
                    return;
                }
                String query = "UPDATE " + table + " SET Player = ?, Hat = ?, Bag = ?, WStick = ?, Balloon = ?, Spray = ?, Available = ? WHERE UUID = ?";
                statement = connection.prepareStatement(query);
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
        } finally {
            closeConnections(statement, connection, null);
            plugin.getLogger().info("Entities/NPC data was saved.");
        }
    }

    @Override
    public void saveEntity(EntityCache entityCache) {
        saveEntityInfo(entityCache);
    }

    private void saveEntityInfo(EntityCache entityCache){
        Connection connection = null;
        PreparedStatement statement = null;
        try{
            connection = hikariCP.getHikariDataSource().getConnection();
            if(!checkInfo(entityCache.getUniqueId())){
                String query = "INSERT INTO " + table + " (id, UUID, Player, Hat, Bag, WStick, Balloon, Spray, Available) VALUES(NULL, ?, ?, ?, ?, ?, ?, ?, ?);";
                statement = connection.prepareStatement(query);
                statement.setString(1, entityCache.getUniqueId().toString());
                statement.setString(2, "[NPC]");
                statement.setString(3, entityCache.getHat() == null ? "" : entityCache.getHat().getId() + "|" + entityCache.getHat().getColor().asRGB());
                statement.setString(4, entityCache.getBag() == null ? "" : entityCache.getBag().getId() + "|" + entityCache.getBag().getColor().asRGB());
                statement.setString(5, entityCache.getWStick() == null ? "" : entityCache.getWStick().getId() + "|" + entityCache.getWStick().getColor().asRGB());
                statement.setString(6, entityCache.getBalloon() == null ? "" : entityCache.getBalloon().getId() + "|" + entityCache.getBalloon().getColor().asRGB());
                statement.setString(7, "");
                statement.setString(8, "");
                statement.executeUpdate();
                return;
            }
            String query = "UPDATE " + table + " SET Player = ?, Hat = ?, Bag = ?, WStick = ?, Balloon = ?, Spray = ?, Available = ? WHERE UUID = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, "[NPC]");
            statement.setString(2, entityCache.saveHat());
            statement.setString(3, entityCache.saveBag());
            statement.setString(4, entityCache.saveWStick());
            statement.setString(5, entityCache.saveBalloon());
            statement.setString(6, "");
            statement.setString(7, "");
            statement.setString(8, entityCache.getUniqueId().toString());
            statement.executeUpdate();
        }catch (SQLException throwable) {
            plugin.getLogger().severe("Failed to save entity information: " + throwable.getMessage());
        } finally {
            closeConnections(statement, connection, null);
            //EntityCache.removeEntity(entityCache.getUniqueId());
        }
    }

    @Override
    public void asyncSaveEntity(EntityCache entityCache) {
        asyncSaveEntityInfo(entityCache);
    }

    private void asyncSaveEntityInfo(EntityCache entityCache){
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            Connection connection = null;
            PreparedStatement statement = null;
            try{
                connection = hikariCP.getHikariDataSource().getConnection();
                if(!checkInfo(entityCache.getUniqueId())){
                    String query = "INSERT INTO " + table + " (id, UUID, Player, Hat, Bag, WStick, Balloon, Spray, Available) VALUES(NULL, ?, ?, ?, ?, ?, ?, ?, ?);";
                    statement = connection.prepareStatement(query);
                    statement.setString(1, entityCache.getUniqueId().toString());
                    statement.setString(2, "[NPC]");
                    statement.setString(3, entityCache.saveHat());
                    statement.setString(4, entityCache.saveBag());
                    statement.setString(5, entityCache.saveWStick());
                    statement.setString(6, entityCache.saveBalloon());
                    statement.setString(7, "");
                    statement.setString(8, "");
                    statement.executeUpdate();
                    return;
                }
                String query = "UPDATE " + table + " SET Player = ?, Hat = ?, Bag = ?, WStick = ?, Balloon = ?, Spray = ?, Available = ? WHERE UUID = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, "[NPC]");
                statement.setString(2, entityCache.saveHat());
                statement.setString(3, entityCache.saveBag());
                statement.setString(4, entityCache.saveWStick());
                statement.setString(5, entityCache.saveBalloon());
                statement.setString(6, "");
                statement.setString(7, "");
                statement.setString(8, entityCache.getUniqueId().toString());
                statement.executeUpdate();
            }catch (SQLException throwable) {
                plugin.getLogger().severe("Failed to save player information: " + throwable.getMessage());
            } finally {
                closeConnections(statement, connection, null);
                entityCache.clearCosmeticsInUse();
            }
            //EntityCache.removeEntity(entityCache.getUniqueId());
        });
    }

    @Override
    public DatabaseType getDatabaseType() {
        return DatabaseType.MYSQL;
    }
}
