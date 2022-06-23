package com.francobm.magicosmetics.database;

import com.francobm.magicosmetics.cache.EntityCache;
import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.cache.PlayerCache;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.util.UUID;

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

    public abstract void loadPlayer(Player player , boolean async);

    public abstract void savePlayer(PlayerCache playerCache, boolean closed);

    public abstract void asyncSavePlayer(PlayerCache playerCache);

    public abstract void loadEntity(UUID uuid);

    public abstract void removeEntity(UUID uuid);

    public abstract void saveEntity(EntityCache entityCache);

    public abstract void asyncSaveEntity(EntityCache entityCache);

}
