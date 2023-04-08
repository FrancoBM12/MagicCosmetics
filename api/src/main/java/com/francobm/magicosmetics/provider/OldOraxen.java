package com.francobm.magicosmetics.provider;

import com.francobm.magicosmetics.MagicCosmetics;
import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.compatibilities.CompatibilitiesManager;
import io.th0rgal.oraxen.compatibilities.CompatibilityProvider;
import io.th0rgal.oraxen.font.FontManager;
import io.th0rgal.oraxen.font.Glyph;
import io.th0rgal.oraxen.items.ItemBuilder;
import io.th0rgal.oraxen.items.OraxenItems;
import org.bukkit.inventory.ItemStack;

public class OldOraxen extends CompatibilityProvider<MagicCosmetics> implements Oraxen {

    public void register(){
        CompatibilitiesManager.addCompatibility("MagicCosmetics", OldOraxen.class);
    }

    public ItemStack getItemStackById(String id){
        if(!OraxenItems.exists(id)) return null;
        ItemBuilder itemBuilder = OraxenItems.getItemById(id);
        if(itemBuilder == null) return null;
        return itemBuilder.build();
    }

    public ItemStack getItemStackByItem(ItemStack itemStack){
        String id = OraxenItems.getIdByItem(itemStack);
        if(id == null) return null;
        ItemBuilder itemBuilder = OraxenItems.getItemById(id);
        if(itemBuilder == null) return null;
        return itemBuilder.build();
    }

    public String replaceFontImages(String id){
        OraxenPlugin oraxenPlugin = OraxenPlugin.get();
        if(oraxenPlugin == null) return id;
        FontManager fontManager = oraxenPlugin.getFontManager();
        if(fontManager == null) return id;
        for(Glyph glyph : fontManager.getGlyphs()){
            if(!id.contains(glyph.getName())) continue;
            id = id.replace(glyph.getName(), String.valueOf(glyph.getCharacter()));
        }
        return id;
    }
}
