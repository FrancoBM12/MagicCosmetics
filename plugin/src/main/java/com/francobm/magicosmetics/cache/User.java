package com.francobm.magicosmetics.cache;

public class User {
    private final String id;
    private final String name;
    private final String version;
    private final String resource;
    private final String token;
    private final String nonce;
    private final String agent;
    private final String time;

    public User(){
        this.id = "ROwROw";
        this.name = "";
        this.version = "";
        this.resource = "";
        this.token = "";
        this.nonce = "";
        this.agent = "";
        this.time = "";
    }

    public User(String id, String name, String version, String resource, String token, String nonce, String agent, String time) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.resource = resource;
        this.token = token;
        this.nonce = nonce;
        this.agent = agent;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getResource() {
        return resource;
    }

    public String getToken() {
        return token;
    }

    public String getNonce() {
        return nonce;
    }

    public String getAgent() {
        return agent;
    }

    public String getTime() {
        return time;
    }
}
