package com.francobm.magicosmetics.cache.inventories.menus;

import com.francobm.magicosmetics.cache.Cosmetic;
import com.francobm.magicosmetics.cache.PlayerCache;
import com.francobm.magicosmetics.cache.Sound;
import com.francobm.magicosmetics.cache.Token;
import com.francobm.magicosmetics.cache.inventories.ActionType;
import com.francobm.magicosmetics.cache.inventories.ContentMenu;
import com.francobm.magicosmetics.cache.inventories.Menu;
import com.francobm.magicosmetics.cache.inventories.SlotMenu;
import com.francobm.magicosmetics.cache.items.Items;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

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
            getContentMenu().removeSlotMenu(getContentMenu().getPreviewSlot());
            getContentMenu().removeSlotMenu(getContentMenu().getResultSlot());
            return;
        }
        Items items = new Items(token.getItemStack());
        items.addPlaceHolder(playerCache.getOfflinePlayer().getPlayer());
        SlotMenu slotMenu = new SlotMenu(getContentMenu().getPreviewSlot(), items, "", ActionType.CLOSE_MENU);
        slotMenu.setSound(Sound.getSound("on_click_token"));
        getContentMenu().addSlotMenu(slotMenu);
        items = new Items(Cosmetic.getCloneCosmetic(token.getCosmetic()).getItemStack());
        slotMenu = new SlotMenu(getContentMenu().getResultSlot(), items, token, ActionType.REMOVE_TOKEN_ADD_COSMETIC);
        slotMenu.setSound(Sound.getSound("on_click_token_result"));
        getContentMenu().addSlotMenu(slotMenu);
    }
}
