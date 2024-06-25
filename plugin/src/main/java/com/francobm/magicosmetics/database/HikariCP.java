package com.francobm.magicosmetics.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

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

    public void setProperties(SQL sql) {
        HikariConfig config = new HikariConfig();
        if(sql.getDatabaseType() == DatabaseType.MYSQL){
            String mysql = "jdbc:mysql://" + hostname + ":" + port + "/" + database + "?" + options;
            config.setJdbcUrl(mysql);
            config.setUsername(username);
            config.setPassword(password);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.setMaximumPoolSize(10);
            //config.setConnectionTimeout(30000);

            //config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        }else{
            String sqlite = "jdbc:sqlite:" + ((SQLite)sql).getFileSQL();
            config.setJdbcUrl(sqlite);
            config.setDriverClassName("org.sqlite.JDBC");
        }
        hikariDataSource = new HikariDataSource(config);
    }

    public HikariCP() {

    }

    public HikariDataSource getHikariDataSource() {
        return hikariDataSource;
    }
}
