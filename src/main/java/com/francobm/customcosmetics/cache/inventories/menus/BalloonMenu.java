package com.francobm.customcosmetics.cache.inventories.menus;

import com.francobm.customcosmetics.CustomCosmetics;
import com.francobm.customcosmetics.cache.Cosmetic;
import com.francobm.customcosmetics.cache.CosmeticType;
import com.francobm.customcosmetics.cache.PlayerCache;
import com.francobm.customcosmetics.cache.Sound;
import com.francobm.customcosmetics.cache.inventories.*;
import com.francobm.customcosmetics.cache.items.Items;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Collections;
import java.util.List;

public class BalloonMenu extends PaginatedMenu {

    public BalloonMenu(String id, ContentMenu contentMenu, int startSlot, int endSlot, int backSlot, int nextSlot, int pagesSlot, List<Integer> slotsUnavailable) {
        super(id, contentMenu, startSlot, endSlot, backSlot, nextSlot, pagesSlot, slotsUnavailable);
    }

    public BalloonMenu(String id, ContentMenu contentMenu) {
        super(id, contentMenu);
    }

    public BalloonMenu(PlayerCache playerCache, Menu menu) {
        super(playerCache, menu);
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        SlotMenu slotMenu = getContentMenu().getSlotMenuBySlot(slot);
        if(slotMenu == null) return;
        if(slotMenu.getActionType().size() == 2){
            if(event.getClick() == ClickType.SHIFT_LEFT){
                slotMenu.action(player, slotMenu.getActionType().get(1));
                return;
            }
            slotMenu.action(player, slotMenu.getActionType().get(0));
            return;
        }
        if(slotMenu.getSlot() == getBackSlot()){
            slotMenu.playSound(player);
            if(page == 0){
                //player.sendMessage(CustomCosmetics.getInstance().prefix + CustomCosmetics.getInstance().getMessages().getString("first-page"));
                return;
            }
            page = page - 1;
            open();
            return;
        }

        if(slotMenu.getSlot() == getNextSlot()){
            List<Cosmetic> cosmetics = Cosmetic.getCosmeticsByType(CosmeticType.BALLOON);
            slotMenu.playSound(player);
            if(((index + 1) >= cosmetics.size())){
                //player.sendMessage(CustomCosmetics.getInstance().prefix + CustomCosmetics.getInstance().getMessages().getString("last-page"));
                return;
            }
            page = page + 1;
            open();
            return;
        }
        slotMenu.action(player);
    }

    @Override
    public void setItems() {
        if(getBackSlot() != -1) {
            SlotMenu s = new SlotMenu(getBackSlot(), Items.getItem("back-button-template"), ActionType.OPEN_MENU, id);
            s.setSound(Sound.getSound("on_click_back_page"));
            getContentMenu().addSlotMenu(s);
        }
        if(getNextSlot() != -1){
            SlotMenu s = new SlotMenu(getNextSlot(), Items.getItem("next-button-template"), ActionType.OPEN_MENU, id);
            s.setSound(Sound.getSound("on_click_next_page"));
            getContentMenu().addSlotMenu(s);
        }
        if(getPagesSlot() != -1) {
            getContentMenu().addSlotMenu(new SlotMenu(getPagesSlot(), new Items(Items.getItem("pages-template").addVariableItem("%pages%", page + 1)), ActionType.CLOSE_MENU, id));
        }
        List<Cosmetic> cosmetics = Cosmetic.getCosmeticsByType(CosmeticType.BALLOON);
        if(!cosmetics.isEmpty()) {
            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if (index >= cosmetics.size()) break;
                Cosmetic cosmetic = cosmetics.get(index);
                int slot = (getStartSlot() + i);
                if (cosmetic == null) continue;
                while(slotsUnavailable.contains(slot)){
                    slot++;
                }
                Items items = new Items(getPage()+index+"_balloon", Items.getItem("balloon-template").copyItem(playerCache, cosmetic, cosmetic.getItemStack()));
                items.addVariable("%name%", cosmetic.getName()).addVariable("%available%", playerCache.getCosmeticById(cosmetic.getId()) != null ? CustomCosmetics.getInstance().getMessages().getString("available") : CustomCosmetics.getInstance().getMessages().getString("unavailable")).addVariable("%type%", cosmetic.getCosmeticType());
                SlotMenu slotMenu;
                if(playerCache.getBalloon() != null){
                    if(playerCache.getBalloon().getId().equalsIgnoreCase(cosmetic.getId())){
                        slotMenu = new SlotMenu(slot, items, ActionType.PLAYER_COMMAND, Collections.singletonList("cosmetics unset " + cosmetic.getId()));
                    }else{
                        if(cosmetic.isColored()){
                            slotMenu = new SlotMenu(slot, items, ActionType.OPEN_MENU, Collections.singletonList("cosmetics unuse " + cosmetic.getId()),"colored|color1|"+cosmetic.getId());
                        }else{
                            slotMenu = new SlotMenu(slot, items, ActionType.PREVIEW_ITEM, cosmetic);
                        }
                        slotMenu.getActionType().add(ActionType.PLAYER_COMMAND);
                    }
                }else{
                    if(cosmetic.isColored()){
                        slotMenu = new SlotMenu(slot, items, ActionType.OPEN_MENU, Collections.singletonList("cosmetics unuse " + cosmetic.getId()),"colored|color1|"+cosmetic.getId());
                    }else{
                        slotMenu = new SlotMenu(slot, items, ActionType.PREVIEW_ITEM, cosmetic);
                    }
                    slotMenu.getActionType().add(ActionType.PLAYER_COMMAND);
                }
                slotMenu.setSound(Sound.getSound("on_click_cosmetic"));
                getContentMenu().addSlotMenu(slotMenu);
                setItemInPaginatedMenu(slotMenu, getPage(), index, "_balloon");
            }
        }
        for(SlotMenu slotMenu : getContentMenu().getSlotMenu().values()){
            setItemInPaginatedMenu(slotMenu, -1, -1, "_balloon");
        }
    }

}
