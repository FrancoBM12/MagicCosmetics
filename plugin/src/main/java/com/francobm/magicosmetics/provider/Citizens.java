package com.francobm.magicosmetics.provider;

import com.francobm.magicosmetics.cache.EntityCache;
import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.api.Cosmetic;
import com.francobm.magicosmetics.nms.NPC.ItemSlot;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Citizens {
    private final MagicCosmetics plugin = MagicCosmetics.getInstance();

    public void loadNPCCosmetics(){
        for(NPC npc : CitizensAPI.getNPCRegistry().sorted()){
            plugin.getSql().loadEntity(npc.getUniqueId());
        }
    }

    public NPC getNPC(UUID uuid){
        return CitizensAPI.getNPCRegistry().getByUniqueId(uuid);
    }


    public List<String> getNPCs(){
        List<String> list = new ArrayList<>();
        for(NPC npc : CitizensAPI.getNPCRegistry().sorted()){
            list.add(String.valueOf(npc.getId()));
        }
        return list;
    }

    public void EquipmentNPC(ItemSlot itemSlot, UUID uuid, ItemStack itemStack){
        switch (itemSlot){
            case HELMET:
                NPC npc = CitizensAPI.getNPCRegistry().getByUniqueId(uuid);
                if(npc == null) return;
                Equipment equipment = npc.getOrAddTrait(Equipment.class);
                equipment.set(Equipment.EquipmentSlot.HELMET, itemStack.clone());
                return; //TODO add helmet trait to npc and equip it here.
            case CHESTPLATE:
                npc = CitizensAPI.getNPCRegistry().getByUniqueId(uuid);
                if(npc == null) return;
                equipment = npc.getOrAddTrait(Equipment.class);
                equipment.set(Equipment.EquipmentSlot.CHESTPLATE, itemStack.clone());
                return; //TODO add chestplate trait to npc and equip it here.
            case LEGGINGS:
                npc = CitizensAPI.getNPCRegistry().getByUniqueId(uuid);
                if(npc == null) return;
                equipment = npc.getOrAddTrait(Equipment.class);
                equipment.set(Equipment.EquipmentSlot.LEGGINGS, itemStack.clone());
                return; //TODO add leggings trait to npc and equip it here.
            case BOOTS:
                npc = CitizensAPI.getNPCRegistry().getByUniqueId(uuid);
                if(npc == null) return;
                equipment = npc.getOrAddTrait(Equipment.class);
                equipment.set(Equipment.EquipmentSlot.BOOTS, itemStack.clone());
                return; //TODO add boots trait to npc and equip it here.
            case MAIN_HAND:
                npc = CitizensAPI.getNPCRegistry().getByUniqueId(uuid);
                if(npc == null) return;
                equipment = npc.getOrAddTrait(Equipment.class);
                equipment.set(Equipment.EquipmentSlot.HAND, itemStack.clone());
                return; //TODO add main hand trait to npc and equip it here.
            case OFF_HAND:
                npc = CitizensAPI.getNPCRegistry().getByUniqueId(uuid);
                if(npc == null) return;
                equipment = npc.getOrAddTrait(Equipment.class);
                equipment.set(Equipment.EquipmentSlot.OFF_HAND, itemStack.clone());
                //TODO add off hand trait to npc and equip it here.
        }
    }

    public void equipCosmetic(CommandSender sender, String npcID, String id, String colorHex) {
        try{
            int ID = Integer.parseInt(npcID);
            NPC npc = CitizensAPI.getNPCRegistry().getById(ID);
            if(npc == null){
                plugin.getCosmeticsManager().sendMessage(sender, plugin.prefix + plugin.getMessages().getString("invalid-npc-id"));
                return;
            }
            EntityCache entityCache = EntityCache.getOrCreateEntity(npc.getUniqueId());
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
        }
    }
}