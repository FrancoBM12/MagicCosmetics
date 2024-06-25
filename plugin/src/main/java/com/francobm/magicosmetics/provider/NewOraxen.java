package com.francobm.magicosmetics.provider;

import com.francobm.magicosmetics.MagicCosmetics;
import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.compatibilities.CompatibilitiesManager;
import io.th0rgal.oraxen.compatibilities.CompatibilityProvider;
import io.th0rgal.oraxen.font.FontManager;
import io.th0rgal.oraxen.font.Glyph;
import io.th0rgal.oraxen.items.ItemBuilder;
import org.bukkit.inventory.ItemStack;

public class NewOraxen extends CompatibilityProvider<MagicCosmetics> implements Oraxen {

    public void register(){
        CompatibilitiesManager.addCompatibility("MagicCosmetics", NewOraxen.class);
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
            if(glyph.getCharacter().isEmpty()) continue;
            if(!id.contains(glyph.getName())) continue;
            id = id.replace(glyph.getName(), glyph.getCharacter());
        }
        return id;
    }
}
