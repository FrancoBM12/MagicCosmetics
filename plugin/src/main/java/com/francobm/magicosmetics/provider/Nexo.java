package com.francobm.magicosmetics.provider;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.fonts.FontManager;
import com.nexomc.nexo.fonts.Glyph;
import com.nexomc.nexo.items.ItemBuilder;
import org.bukkit.inventory.ItemStack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Nexo implements ResourcePlugin {

    Pattern pattern = Pattern.compile(":\\w+:");

    public ItemStack getItemStackById(String id){
        if(!NexoItems.exists(id)) return null;
        ItemBuilder itemBuilder = NexoItems.itemFromId(id);
        if(itemBuilder == null) return null;
        return itemBuilder.build();
    }

    @Override
    public String replaceFontImageWithoutColor(String id) {
        return replaceFontImages(id);
    }

    public ItemStack getItemStackByItem(ItemStack itemStack){
        String id = NexoItems.idFromItem(itemStack);
        if(id == null) return null;
        ItemBuilder itemBuilder = NexoItems.itemFromId(id);
        if(itemBuilder == null) return null;
        return itemBuilder.build();
    }

    public String replaceFontImages(String id){
        NexoPlugin nexoPlugin = NexoPlugin.instance();
        FontManager fontManager = nexoPlugin.fontManager();
        Matcher matcher = pattern.matcher(id);
        while (matcher.find()) {
            String placeholder = matcher.group();
            String glyphName = placeholder.replace(":", "");
            Glyph glyph = fontManager.glyphFromID(glyphName);
            if(glyph == null) continue;
            id = id.replace(placeholder, glyph.character());
        }

        return id;
    }

    @Override
    public String getProviderName() {
        return "Nexo";
    }

    @Override
    public String getPathName() {
        return "nexo";
    }
}
