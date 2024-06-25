package com.francobm.magicosmetics.provider.znpcplus;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.cache.EntityCache;
import lol.pyr.znpcsplus.api.NpcApi;
import lol.pyr.znpcsplus.api.NpcApiProvider;
import lol.pyr.znpcsplus.api.entity.EntityProperty;
import lol.pyr.znpcsplus.api.npc.NpcEntry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class ZNPCsPlus {
    private final MagicCosmetics plugin = MagicCosmetics.getInstance();
    private final NpcApi npcApi;
    public ZNPCsPlus(){
        npcApi = NpcApiProvider.get();
        NpcApiProvider.register(plugin, npcApi);
        a();
    }

    private void a(){
        for(EntityProperty<?> entityProperty : npcApi.getPropertyRegistry().getAll())
            plugin.getLogger().info("EntityProperty: " + entityProperty.getName() + " - " + entityProperty.getDefaultValue());
        for(NpcEntry npc : npcApi.getNpcRegistry().getAll()){
            //npc.getNpcPojo().getNpcEquip().put(ItemSlot.HELMET, null);
        }
    }

    public void EquipmentNPC(com.francobm.magicosmetics.nms.NPC.ItemSlot itemSlot, int id, ItemStack itemStack){
        /*NPC npc = NPC.find(id);
        if(npc == null) return;
        switch (itemSlot){
            case HELMET:
                npc.getNpcPojo().getNpcEquip().put(ItemSlot.HELMET, itemStack.clone());
                return;
            case CHESTPLATE:
                npc.getNpcPojo().getNpcEquip().put(ItemSlot.CHESTPLATE, itemStack.clone());
                return;
            case LEGGINGS:
                npc.getNpcPojo().getNpcEquip().put(ItemSlot.LEGGINGS, itemStack.clone());
                return;
            case BOOTS:
                npc.getNpcPojo().getNpcEquip().put(ItemSlot.BOOTS, itemStack.clone());
                return;
            case MAIN_HAND:
                npc.getNpcPojo().getNpcEquip().put(ItemSlot.HAND, itemStack.clone());
                return;
            case OFF_HAND:
                npc.getNpcPojo().getNpcEquip().put(ItemSlot.OFFHAND, itemStack.clone());
                return;
        }*/
    }

    public void equipCosmetic(CommandSender sender, String npcID, String id, String colorHex) {
        /*try{
            int ID = Integer.parseInt(npcID);
            NPC npc = NPC.find(ID);
            if(npc == null){
                plugin.getCosmeticsManager().sendMessage(sender, plugin.prefix + plugin.getMessages().getString("invalid-npc-id"));
                return;
            }
            npc.onLoad();
            EntityCache entityCache = EntityCache.getEntityOrCreate((Entity) npc.getBukkitEntity());
            if (plugin.getUser() == null) return;
            Cosmetic cosmetic = Cosmetic.getCloneCosmetic(id);
            Color color = null;
            if(colorHex != null) {
                color = com.francobm.magicosmetics.cache.Color.hex2Rgb(colorHex);
            }
            if(cosmetic == null) return;
            if(entityCache.hasEquipped(cosmetic)){
                entityCache.unSetCosmetic(cosmetic.getCosmeticType());
                return;
            }
            if(color != null) {
                cosmetic.setColor(color);
            }
            entityCache.setCosmetic(cosmetic);
            if (plugin.equipMessage) {
                plugin.getCosmeticsManager().sendMessage(sender, plugin.prefix + plugin.getMessages().getString("use-cosmetic").replace("%id%", id).replace("%name%", cosmetic.getName()));
            }
        }catch (NumberFormatException exception){
            plugin.getCosmeticsManager().sendMessage(sender, plugin.prefix + plugin.getMessages().getString("invalid-npc-id"));
        }*/
    }

    public void loadNPC(NpcEntry znpc, com.francobm.magicosmetics.cache.NPC npc){
        NpcApi npcApiProvider = NpcApiProvider.get();
        npcApiProvider.getPropertyRegistry().getAll();
        EntityCache entityCache = EntityCache.getEntityOrCreate(znpc.getNpc().getProperty(npcApi.getPropertyRegistry().getByName("entity", Entity.class)));
        if (plugin.getUser() == null) return;
        entityCache.loadCosmetics(npc.getCosmetics());
    }
}
