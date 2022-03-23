package com.francobm.magicosmetics.database;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.cache.PlayerCache;
import org.bukkit.entity.Player;

import java.sql.Connection;

public abstract class SQL {
    protected MagicCosmetics plugin = MagicCosmetics.getInstance();
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
