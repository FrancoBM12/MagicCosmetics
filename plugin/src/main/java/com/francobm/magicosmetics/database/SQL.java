package com.francobm.magicosmetics.database;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.cache.PlayerData;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public abstract class SQL {
    protected MagicCosmetics plugin = MagicCosmetics.getInstance();
    protected HikariCP hikariCP;

    public abstract void createTable();

    public abstract void loadPlayer(Player player);

    public abstract CompletableFuture<Void> loadPlayerAsync(Player player);

    public abstract void savePlayer(PlayerData playerData, boolean closed);

    public abstract CompletableFuture<Void> savePlayerAsync(PlayerData playerData);

    public abstract void savePlayers();

    public abstract DatabaseType getDatabaseType();

    protected void closeConnections(PreparedStatement preparedStatement, Connection connection, ResultSet resultSet){
        if(connection == null) return;
        try{
            if(connection.isClosed()) return;
            if(resultSet != null) {
                resultSet.close();
            }
            if(preparedStatement != null) {
                preparedStatement.close();
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
