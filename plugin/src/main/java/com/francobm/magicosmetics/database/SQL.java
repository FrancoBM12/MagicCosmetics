package com.francobm.magicosmetics.database;

import com.francobm.magicosmetics.cache.EntityCache;
import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.cache.PlayerData;
import com.zaxxer.hikari.HikariConfig;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public abstract class SQL {
    protected MagicCosmetics plugin = MagicCosmetics.getInstance();
    protected HikariCP hikariCP;

    public abstract void createTable();

    public abstract void loadPlayer(Player player , boolean async);

    public abstract void savePlayer(PlayerData playerData, boolean closed);

    public abstract void asyncSavePlayer(PlayerData playerData);

    public abstract void savePlayers();

    public abstract void loadEntity(UUID uuid);

    public abstract void removeEntity(UUID uuid);

    public abstract void saveEntity(EntityCache entityCache);

    public abstract void saveEntities();

    public abstract void asyncSaveEntity(EntityCache entityCache);

    public abstract DatabaseType getDatabaseType();

    protected void closeConnections(PreparedStatement preparedStatement, Connection connection, ResultSet resultSet){
        if(connection == null) return;
        try{
            if(connection.isClosed()) return;
            if(resultSet != null)
                resultSet.close();
            if(preparedStatement != null)
                preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
