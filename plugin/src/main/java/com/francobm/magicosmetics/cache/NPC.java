package com.francobm.magicosmetics.cache;

import java.util.UUID;

public class NPC {
    private int id;
    private Object npc;
    private final UUID uuid;
    private final NPCType npcType;
    private String cosmetics;
    private EntityCache entityCache;
    private boolean load;

    public NPC(int id, NPCType npcType, String cosmetics) {
        this.id = id;
        this.npcType = npcType;
        this.cosmetics = cosmetics;
        this.uuid = null;
    }

    public NPC(UUID uuid, NPCType npcType, String cosmetics) {
        this.uuid = uuid;
        this.npcType = npcType;
        this.cosmetics = cosmetics;
    }

    public int getId() {
        return id;
    }

    public NPCType getNpcType() {
        return npcType;
    }

    public Object getNpc() {
        return npc;
    }

    public void setNpc(Object npc) {
        this.npc = npc;
    }

    public String getCosmetics() {
        return cosmetics;
    }

    public void setCosmetics(String cosmetics) {
        this.cosmetics = cosmetics;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setEntityCache(EntityCache entityCache) {
        this.entityCache = entityCache;
    }

    public EntityCache getEntityCache() {
        return entityCache;
    }

    public boolean isVanilla(){
        return uuid != null;
    }

    public void setLoad(boolean load) {
        this.load = load;
    }

    public boolean isLoad() {
        return load;
    }
}
