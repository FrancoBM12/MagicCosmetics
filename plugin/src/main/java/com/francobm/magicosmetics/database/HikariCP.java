package com.francobm.magicosmetics.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;

public class HikariCP {

    private HikariDataSource hikariDataSource;
    protected String hostname;
    protected int port;
    protected String database;
    protected String username;
    protected String password;
    protected String options;

    public HikariCP(String hostname, int port, String username, String password, String database, String options) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.database = database;
        this.password = password;
        this.options = options;
    }

    public HikariCP() {
    }

    public void setProperties(SQL sql) {
        HikariConfig config = new HikariConfig();
        try{
            if(sql.getDatabaseType() == DatabaseType.MYSQL){
                //String mysql = "jdbc:mysql://" + username + ":" + password + "@" + hostname + ":" + port + "/" + database + "?" + options;
                String mysql = "jdbc:mysql://" + hostname + ":" + port + "/" + database + "?" + options;
                config.setJdbcUrl(mysql);
                config.setUsername(username);
                config.setPassword(password);
                config.addDataSourceProperty("cachePrepStmts", "true");
                config.addDataSourceProperty("prepStmtCacheSize", "250");
                config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                config.setMaximumPoolSize(10);
                config.setConnectionTimeout(30000); // 30 seconds
                config.setMaxLifetime(1800000); // 30 minutes
                config.setValidationTimeout(5000); // 5 seconds
                config.setIdleTimeout(600000); // 10 minutes

                //config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            }else{
                String sqlite = "jdbc:sqlite:" + ((SQLite)sql).getFileSQL();
                config.setJdbcUrl(sqlite);
                config.setDriverClassName("org.sqlite.JDBC");
            }
            hikariDataSource = new HikariDataSource(config);
        }catch (Exception e){
            Bukkit.getLogger().warning("Problem with HikariCP:" +  e.getMessage());
        }
    }

    public HikariDataSource getHikariDataSource() {
        return hikariDataSource;
    }

    public void close() {
        if (hikariDataSource != null && !hikariDataSource.isClosed()) {
            hikariDataSource.close();
        }
    }
}
