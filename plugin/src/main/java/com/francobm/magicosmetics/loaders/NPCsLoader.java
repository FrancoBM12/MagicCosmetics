package com.francobm.magicosmetics.loaders;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.cache.EntityCache;
import com.francobm.magicosmetics.cache.NPC;
import com.francobm.magicosmetics.cache.NPCType;
import com.francobm.magicosmetics.files.FileCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NPCsLoader {
    private final MagicCosmetics plugin = MagicCosmetics.getInstance();
    private final Map<String, NPC> NPCs;

    public NPCsLoader(){
        this.NPCs = new HashMap<>();
        load();
    }

    public void load(){
        NPCs.clear();
        FileCreator fileCreator = plugin.getNPCs();
        ConfigurationSection configurationSection = fileCreator.getConfigurationSection("NPCs");
        if(configurationSection == null) return;
        int i = 0;
        for(String key : configurationSection.getKeys(false)){
            for(String keyId : fileCreator.getConfigurationSection("NPCs." + key).getKeys(false)){
                switch (key.toLowerCase()){
                    case "citizens":
                        int id = Integer.parseInt(keyId);
                        String cosmetics = fileCreator.getString("NPCs." + key + "." + keyId);
                        NPCs.put(id+NPCType.CITIZENS.getKey(), new NPC(id, NPCType.CITIZENS, cosmetics));
                        i++;
                        break;
                    case "znpcsplus":
                        id = Integer.parseInt(keyId);
                        cosmetics = fileCreator.getString("NPCs." + key + "." + keyId);
                        NPCs.put(id+NPCType.Z_NPC_PLUS.getKey(), new NPC(id, NPCType.Z_NPC_PLUS, cosmetics));
                        i++;
                        break;
                    case "vanilla":
                        UUID uuid = UUID.fromString(keyId);
                        cosmetics = fileCreator.getString("NPCs." + key + "." + keyId);
                        NPCs.put(keyId+NPCType.VANILLA.getKey(), new NPC(uuid, NPCType.VANILLA, cosmetics));
                        i++;
                        break;
                }
            }
        }
        plugin.getLogger().info(i + " NPCs has been loaded");
    }

    public void addNPC(int id, NPCType npcType, EntityCache entityCache, String cosmeticId){
        NPC npc = new NPC(id, npcType, cosmeticId);
        npc.setEntityCache(entityCache);
        NPCs.put(id+npcType.getKey(), npc);
    }

    public boolean hasNPC(int id, NPCType npcType){
        return NPCs.containsKey(id+npcType.getKey());
    }

    public NPC getNPC(int id, NPCType npcType){
        return NPCs.get(id+npcType.getKey());
    }

    public void removeNPC(int id, NPCType npcType){
        NPC npc = getNPC(id, npcType);
        if(npc == null) return;
        EntityCache entityCache = npc.getEntityCache();
        if(entityCache != null) {
            entityCache.clearCosmeticsInUse();
            EntityCache.removeEntity(entityCache.getUniqueId());
        }
        NPCs.remove(id+npcType.getKey());
    }

    public void save() {
        FileCreator fileCreator = plugin.getNPCs();
        fileCreator.set("NPCs", null);
        int i = 0;
        for(NPC npc : NPCs.values()){
            if(npc.getEntityCache() == null) continue;
            switch (npc.getNpcType()){
                case CITIZENS:
                    fileCreator.set("NPCs.Citizens." + npc.getId(), npc.getEntityCache().saveCosmetics());
                    break;
                case Z_NPC_PLUS:
                    fileCreator.set("NPCs.ZNPCsPlus." + npc.getId(), npc.getEntityCache().saveCosmetics());
                    break;
                case VANILLA:
                    fileCreator.set("NPCs.Vanilla." + ((Entity)npc.getNpc()).getUniqueId(), npc.getEntityCache().saveCosmetics());
                    break;
            }
            i++;
        }
        plugin.getLogger().info("Data for " + i + " NPCs have been saved.");
        fileCreator.save();
    }

    public Map<String, NPC> getNPCs() {
        return NPCs;
    }
}
