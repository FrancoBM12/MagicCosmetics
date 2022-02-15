package com.francobm.customcosmetics.cache.inventories.menus;

import com.francobm.customcosmetics.CustomCosmetics;
import com.francobm.customcosmetics.cache.Cosmetic;
import com.francobm.customcosmetics.cache.PlayerCache;
import com.francobm.customcosmetics.cache.Sound;
import com.francobm.customcosmetics.cache.Token;
import com.francobm.customcosmetics.cache.inventories.ActionType;
import com.francobm.customcosmetics.cache.inventories.ContentMenu;
import com.francobm.customcosmetics.cache.inventories.Menu;
import com.francobm.customcosmetics.cache.inventories.SlotMenu;
import com.francobm.customcosmetics.cache.items.Items;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;
import java.util.Collections;

public class TokenMenu extends Menu {

    public TokenMenu(String id, ContentMenu contentMenu) {
        super(id, contentMenu);
    }

    public TokenMenu(PlayerCache playerCache, Menu menu) {
        super(playerCache, menu);
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        SlotMenu slotMenu = getContentMenu().getSlotMenuBySlot(slot);
        if(slotMenu == null) return;
        slotMenu.action(player);
    }

    @Override
    public void setItems() {
        setup();
        for(SlotMenu slotMenu : getContentMenu().getSlotMenu().values()){
            setItemInMenu(slotMenu);
        }
    }

    private void setup(){
        Token token = playerCache.getTokenInPlayer();
        if(token == null){
            //CustomCosmetics.getInstance().getLogger().warning("[Token] Player: '" + playerCache.getOfflinePlayer().getName() + "' Token Not Found.");
            getContentMenu().removeSlotMenu(2);
            getContentMenu().removeSlotMenu(6);
            return;
        }
        Items items = new Items(token.getItemStack());
        SlotMenu slotMenu = new SlotMenu(2, items, ActionType.CLOSE_MENU, "");
        slotMenu.setSound(Sound.getSound("on_click_token"));
        getContentMenu().addSlotMenu(slotMenu);
        items = new Items(Cosmetic.getCloneCosmetic(token.getCosmetic()).getItemStack());
        slotMenu = new SlotMenu(6, items, ActionType.REMOVE_TOKEN_ADD_COSMETIC, token);
        slotMenu.setSound(Sound.getSound("on_click_token_result"));
        getContentMenu().addSlotMenu(slotMenu);
    }
}
