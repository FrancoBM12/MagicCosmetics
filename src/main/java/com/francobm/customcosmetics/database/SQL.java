package com.francobm.customcosmetics.database;

import com.francobm.customcosmetics.CustomCosmetics;
import com.francobm.customcosmetics.cache.PlayerCache;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class SQL {
    protected CustomCosmetics plugin = CustomCosmetics.getInstance();
    protected Connection connection;

    public SQL(){
        connect();
        createTable();
    }

    public abstract void connect();

    public abstract void disconnect();

    public boolean isConnected(){
        return connection != null;
    }

    public abstract void createTable();

    public abstract void loadPlayer(Player player);

    public abstract void savePlayer(PlayerCache playerCache);

}
