package com.francobm.customcosmetics.cache.inventories.menus;

import com.francobm.customcosmetics.cache.*;
import com.francobm.customcosmetics.cache.inventories.*;
import com.francobm.customcosmetics.cache.items.Items;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.*;

public class ColoredMenu extends PaginatedMenu {

    private Color color;
    private Cosmetic cosmetic;

    public ColoredMenu(String id, ContentMenu contentMenu, int startSlot, int endSlot, int backSlot, int nextSlot, int pagesSlot, List<Integer> slotsUnavailable) {
        super(id, contentMenu, startSlot, endSlot, backSlot, nextSlot, pagesSlot, slotsUnavailable);
    }

    public ColoredMenu(String id, ContentMenu contentMenu) {
        super(id, contentMenu);
    }

    public ColoredMenu(PlayerCache playerCache, Menu menu, Color color, Cosmetic cosmetic) {
        super(playerCache, menu);
        this.color = color;
        this.cosmetic = cosmetic;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        SlotMenu slotMenu = getContentMenu().getSlotMenuBySlot(slot);
        if(slotMenu == null) return;
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
            slotMenu.playSound(player);
            if(((index + 1) >= color.getSecondaryColors().size())){
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
        setup();
        if(!color.getSecondaryColors().isEmpty()) {
            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if (index >= color.getSecondaryColors().size()) break;
                org.bukkit.Color dyeColor = color.getSecondaryColors().get(index);
                int slot = (getStartSlot() + i);
                if (dyeColor == null) continue;
                while(slotsUnavailable.contains(slot)){
                    slot++;
                }
                Cosmetic cosmetic = Cosmetic.getCloneCosmetic(this.cosmetic.getId());
                cosmetic.setColor(dyeColor);
                Items items = new Items(getPage()+index+"_colored", Items.getItem("color-template").colorItem(dyeColor));
                //CustomCosmetics.getInstance().getLogger().info("Cosmetic ID: " + cosmetic.getId() + " Color: " + cosmetic.getColor().asRGB());
                Items previewItem = new Items(cosmetic.getItemColor());
                SlotMenu preview = new SlotMenu(16, previewItem, ActionType.PREVIEW_ITEM, cosmetic);
                preview.setSound(Sound.getSound("on_click_cosmetic_preview"));
                if(i == 0){
                    getContentMenu().addSlotMenu(preview);
                }
                SlotMenu slotMenu = new SlotMenu(slot, items, ActionType.ADD_ITEM_MENU, preview);
                slotMenu.setSound(Sound.getSound("on_click_item_colored"));
                getContentMenu().addSlotMenu(slotMenu);
                setItemInPaginatedMenu(slotMenu, getPage(), index, "_colored");
            }
        }
        for(SlotMenu slotMenu : getContentMenu().getSlotMenu().values()){
            setItemInPaginatedMenu(slotMenu, -1, -1, "_colored");
        }
    }

    private void setup(){
        Items items = new Items(cosmetic.getItemStack());
        SlotMenu slotMenu = new SlotMenu(10, items, ActionType.CLOSE_MENU, "");
        getContentMenu().addSlotMenu(slotMenu);

        items = new Items("color1", Items.getItem("color-template").colorItem(Color.getColor("color1")));
        slotMenu = new SlotMenu(3, items, ActionType.OPEN_MENU, getId()+"|"+items.getId()+"|"+cosmetic.getId());
        slotMenu.setSound(Sound.getSound("on_click_item_colored"));
        getContentMenu().addSlotMenu(slotMenu);

        items = new Items("color2", Items.getItem("color-template").colorItem(Color.getColor("color2")));
        slotMenu = new SlotMenu(4, items, ActionType.OPEN_MENU, getId()+"|"+items.getId()+"|"+cosmetic.getId());
        slotMenu.setSound(Sound.getSound("on_click_item_colored"));
        getContentMenu().addSlotMenu(slotMenu);

        items = new Items("color3", Items.getItem("color-template").colorItem(Color.getColor("color3")));
        slotMenu = new SlotMenu(5, items, ActionType.OPEN_MENU, getId()+"|"+items.getId()+"|"+cosmetic.getId());
        slotMenu.setSound(Sound.getSound("on_click_item_colored"));
        getContentMenu().addSlotMenu(slotMenu);

        items = new Items("color4", Items.getItem("color-template").colorItem(Color.getColor("color4")));
        slotMenu = new SlotMenu(12, items, ActionType.OPEN_MENU, getId()+"|"+items.getId()+"|"+cosmetic.getId());
        slotMenu.setSound(Sound.getSound("on_click_item_colored"));
        getContentMenu().addSlotMenu(slotMenu);

        items = new Items("color5", Items.getItem("color-template").colorItem(Color.getColor("color5")));
        slotMenu = new SlotMenu(13, items, ActionType.OPEN_MENU, getId()+"|"+items.getId()+"|"+cosmetic.getId());
        slotMenu.setSound(Sound.getSound("on_click_item_colored"));
        getContentMenu().addSlotMenu(slotMenu);

        items = new Items("color6", Items.getItem("color-template").colorItem(Color.getColor("color6")));
        slotMenu = new SlotMenu(14, items, ActionType.OPEN_MENU, getId()+"|"+items.getId()+"|"+cosmetic.getId());
        slotMenu.setSound(Sound.getSound("on_click_item_colored"));
        getContentMenu().addSlotMenu(slotMenu);

        items = new Items("color7", Items.getItem("color-template").colorItem(Color.getColor("color7")));
        slotMenu = new SlotMenu(21, items, ActionType.OPEN_MENU, getId()+"|"+items.getId()+"|"+cosmetic.getId());
        slotMenu.setSound(Sound.getSound("on_click_item_colored"));
        getContentMenu().addSlotMenu(slotMenu);

        items = new Items("color8", Items.getItem("color-template").colorItem(Color.getColor("color8")));
        slotMenu = new SlotMenu(22, items, ActionType.OPEN_MENU, getId()+"|"+items.getId()+"|"+cosmetic.getId());
        slotMenu.setSound(Sound.getSound("on_click_item_colored"));
        getContentMenu().addSlotMenu(slotMenu);

        items = new Items("color9", Items.getItem("color-template").colorItem(Color.getColor("color9")));
        slotMenu = new SlotMenu(23, items, ActionType.OPEN_MENU, getId()+"|"+items.getId()+"|"+cosmetic.getId());
        slotMenu.setSound(Sound.getSound("on_click_item_colored"));
        getContentMenu().addSlotMenu(slotMenu);
    }

}
