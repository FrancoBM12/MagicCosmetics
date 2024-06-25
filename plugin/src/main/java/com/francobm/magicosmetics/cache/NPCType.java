package com.francobm.magicosmetics.cache;

public enum NPCType {
    Z_NPC_PLUS("_znpcsplus"),
    CITIZENS("_citizens"),
    VANILLA("_vanilla");

     final String key;
    NPCType(String key){
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
