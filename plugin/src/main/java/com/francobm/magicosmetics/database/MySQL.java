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

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

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
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + table + "` (id INT AUTO_INCREMENT, UUID VARCHAR(255), Player VARCHAR(255), Hat VARCHAR(255), Bag VARCHAR(255), WStick VARCHAR(255), Balloon VARCHAR(255), Spray VARCHAR(255), Available VARCHAR(255), PRIMARY KEY (id))").executeUpdate();
            plugin.getLogger().info("MySQL table created successfully");
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            plugin.getLogger().severe("Could not create table: " + throwable.getMessage());
        }
    }

    @Override
    public void loadPlayer(Player player, boolean async){
        loadPlayerInfo(player, async);
    }

    @Override
    public void savePlayer(PlayerCache playerCache, boolean close){
        savePlayerInfo(playerCache, close);
    }

    private void savePlayerInfo(PlayerCache player, boolean close){
        if(!isConnected()) return;
        FileCreator config = plugin.getConfig();
        String table = config.getString("MySQL.table");
        try{
            if(!checkInfo(player.getUuid())){
                String query = "INSERT INTO " + table + " (id, UUID, Player, Hat, Bag, WStick, Balloon, Spray, Available) VALUES(NULL, ?, ?, ?, ?, ?, ?, ?, ?);";
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
                return;
            }
            String query = "UPDATE " + table + " SET Player = ?, Hat = ?, Bag = ?, WStick = ?, Balloon = ?, Spray = ?, Available = ? WHERE UUID = ?";
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
        if(!isConnected()) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                FileCreator config = plugin.getConfig();
                String table = config.getString("MySQL.table");
                try{
                    if(!checkInfo(player.getUuid())){
                        String query = "INSERT INTO " + table + " (id, UUID, Player, Hat, Bag, WStick, Balloon, Spray, Available) VALUES(NULL, ?, ?, ?, ?, ?, ?, ?, ?);";
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
                        return;
                    }
                    String query = "UPDATE " + table + " SET Player = ?, Hat = ?, Bag = ?, WStick = ?, Balloon = ?, Spray = ?, Available = ? WHERE UUID = ?";
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
                }catch (SQLException throwable) {
                    plugin.getLogger().severe("Failed to save player information: " + throwable.getMessage());
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    @Override
    public void loadEntity(UUID uuid) {
        if(!isConnected()) return;
        FileCreator config = plugin.getConfig();
        String table = config.getString("MySQL.table");
        String queryBuilder = "SELECT * FROM " + table + " WHERE UUID = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(queryBuilder);
            statement.setString(1, uuid.toString());
            ResultSet resultSet = statement.executeQuery();
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
        }
    }

    private void loadPlayerInfo(Player player, boolean async){
        FileCreator config = plugin.getConfig();
        String table = config.getString("MySQL.table");
        String queryBuilder = "SELECT * FROM " + table + " WHERE UUID = ?";
        if(plugin.isBungee()){
            if(async){
                new BukkitRunnable(){
                    @Override
                    public void run() {
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
                                String spray = resultSet.getString("Spray");
                                new BukkitRunnable(){
                                    @Override
                                    public void run() {
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
                }.runTaskLaterAsynchronously(plugin, 20L);
                return;
            }
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
            return;
        }
        if(async){
            new BukkitRunnable(){
                @Override
                public void run() {
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
                            String spray = resultSet.getString("Spray");
                            new BukkitRunnable(){
                                @Override
                                public void run() {
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
        FileCreator config = plugin.getConfig();
        String table = config.getString("MySQL.table");
        String queryBuilder = "SELECT * FROM " + table + " WHERE UUID = ?;";
        try {
            PreparedStatement statement = connection.prepareStatement(queryBuilder);
            statement.setString(1, uuid.toString());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet != null && resultSet.next()){
                return true;
            }
        }catch (SQLException throwable){
            plugin.getLogger().severe("Player information could not be verified.: " + throwable.getMessage());
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
        FileCreator config = plugin.getConfig();
        String table = config.getString("MySQL.table");
        try{
            if(!checkInfo(entityCache.getUniqueId())){
                String query = "INSERT INTO " + table + " (id, UUID, Player, Hat, Bag, WStick, Balloon, Spray, Available) VALUES(NULL, ?, ?, ?, ?, ?, ?, ?, ?);";
                PreparedStatement statement = connection.prepareStatement(query);
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
                FileCreator config = plugin.getConfig();
                String table = config.getString("MySQL.table");
                try{
                    if(!checkInfo(entityCache.getUniqueId())){
                        String query = "INSERT INTO " + table + " (id, UUID, Player, Hat, Bag, WStick, Balloon, Spray, Available) VALUES(NULL, ?, ?, ?, ?, ?, ?, ?, ?);";
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
                        return;
                    }
                    String query = "UPDATE " + table + " SET Player = ?, Hat = ?, Bag = ?, WStick = ?, Balloon = ?, Spray = ?, Available = ? WHERE UUID = ?";
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
                }catch (SQLException throwable) {
                    plugin.getLogger().severe("Failed to save player information: " + throwable.getMessage());
                }
                entityCache.clearCosmeticsInUse();
                //EntityCache.removeEntity(entityCache.getUniqueId());
            }
        }.runTaskAsynchronously(plugin);
    }
}
