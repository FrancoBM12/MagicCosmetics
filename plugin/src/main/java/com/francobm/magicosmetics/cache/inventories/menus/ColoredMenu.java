package com.francobm.magicosmetics.cache.inventories.menus;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.cache.inventories.*;
import com.francobm.magicosmetics.cache.Color;
import com.francobm.magicosmetics.api.Cosmetic;
import com.francobm.magicosmetics.cache.PlayerData;
import com.francobm.magicosmetics.cache.Sound;
import com.francobm.magicosmetics.cache.items.Items;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ColoredMenu extends PaginatedMenu {

    private Color color;
    private org.bukkit.Color secondaryColor;
    private Cosmetic cosmetic;

    public ColoredMenu(String id, ContentMenu contentMenu, int startSlot, int endSlot, Set<Integer> backSlot, Set<Integer> nextSlot, int pagesSlot, List<Integer> slotsUnavailable) {
        super(id, contentMenu, startSlot, endSlot, backSlot, nextSlot, pagesSlot, slotsUnavailable);
    }

    public ColoredMenu(String id, ContentMenu contentMenu) {
        super(id, contentMenu);
    }

    public ColoredMenu(PlayerData playerData, Menu menu, Color color, Cosmetic cosmetic) {
        super(playerData, menu);
        this.color = color;
        this.cosmetic = cosmetic;
        this.secondaryColor = color.getSecondaryColors().get(0);
    }

    public ColoredMenu getClone(PlayerData playerData, Color color, Cosmetic cosmetic) {
        ColoredMenu coloredMenu = new ColoredMenu(this.getId(), this.getContentMenu().getClone(), this.getStartSlot(), this.getEndSlot(), this.getBackSlot(), this.getNextSlot(), this.getPagesSlot(), this.getSlotsUnavailable());
        coloredMenu.playerData = playerData;
        coloredMenu.setColor(color);
        coloredMenu.cosmetic = cosmetic;
        coloredMenu.secondaryColor = color.getSecondaryColors().get(0);
        return coloredMenu;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        SlotMenu slotMenu = getContentMenu().getSlotMenuBySlot(slot);
        if(slotMenu == null) return;
        if(slotMenu.getItems().getId().endsWith("_colored")){
            setSecondaryColor(slotMenu.getItems().getColor());
            setItems();
        }
        if(getBackSlot().contains(slotMenu.getSlot())){
            slotMenu.playSound(player);
            if(page == 0){
                //player.sendMessage(CustomCosmetics.getInstance().prefix + CustomCosmetics.getInstance().getMessages().getString("first-page"));
                return;
            }
            page = page - 1;
            open();
            return;
        }
        if(getNextSlot().contains(slotMenu.getSlot())){
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
        getContentMenu().getSlots().resetSlots();
        if(!getBackSlot().isEmpty()) {
            SlotMenu s;
            for(int slot : getBackSlot()) {
                if(page == 0){
                    s = new SlotMenu(slot, Items.getItem("back-button-cancel-template"), id, ActionType.OPEN_MENU);
                }else{
                    s = new SlotMenu(slot, Items.getItem("back-button-template"), id, ActionType.OPEN_MENU);
                }
                s.setSound(Sound.getSound("on_click_back_page"));
                getContentMenu().addSlotMenu(s);
            }
        }
        if(getPagesSlot() != -1) {
            getContentMenu().addSlotMenu(new SlotMenu(getPagesSlot(), new Items(Items.getItem("pages-template").addVariableItem("%pages%", page + 1)), id, ActionType.CLOSE_MENU));
        }
        String sPrimaryColor = setup();
        StringBuilder title = new StringBuilder();
        title.append(getContentMenu().getTitle());
        title.append(sPrimaryColor);
        String[] selected = getSelectedList();
        if(!color.getSecondaryColors().isEmpty()) {
            int a = 0;
            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if (index >= color.getSecondaryColors().size()) break;
                org.bukkit.Color dyeColor = color.getSecondaryColors().get(index);
                int slot = (getStartSlot() + i + a);
                if (dyeColor == null) continue;
                if(getSecondaryColor() == null) {
                    if (i == 0) {
                        setSecondaryColor(dyeColor);
                    }
                }
                while(slotsUnavailable.contains(slot)){
                    slot++;
                    a++;
                }
                Cosmetic cosmetic = Cosmetic.getCloneCosmetic(this.cosmetic.getId());
                cosmetic.setColor(dyeColor);
                if(!color.hasPermission(playerData.getOfflinePlayer().getPlayer()))
                    cosmetic.setColorBlocked(true);
                Items items = new Items(getPage()+index+"_colored", Items.getItem("color-template").colorItem(dyeColor, secondaryColor));
                items.addPlaceHolder(playerData.getOfflinePlayer().getPlayer());
                if(dyeColor.asRGB() == secondaryColor.asRGB()){
                    //title.append(getContentMenu().getSlots().isSecondaryColored(sPrimaryColor, slot));
                    title.append(selected[i]);
                }
                //CustomCosmetics.getInstance().getLogger().info("Cosmetic ID: " + cosmetic.getId() + " Color: " + cosmetic.getColor().asRGB());
                Items resultItem = new Items(cosmetic.getItemColor());
                SlotMenu result = new SlotMenu(getContentMenu().getResultSlot(), resultItem, cosmetic, ActionType.PREVIEW_ITEM);
                result.setSound(Sound.getSound("on_click_cosmetic_preview"));
                if(i == 0){
                    getContentMenu().addSlotMenu(result);
                }
                SlotMenu slotMenu = new SlotMenu(slot, items, result, ActionType.ADD_ITEM_MENU);
                slotMenu.setSound(Sound.getSound("on_click_item_colored"));
                getContentMenu().addSlotMenu(slotMenu);
                setItemInPaginatedMenu(slotMenu, getPage(), index, "_colored");
            }
        }
        if(!getNextSlot().isEmpty()){
            SlotMenu s;
            for(int slot : getNextSlot()) {
                if(((index + 1) >= color.getSecondaryColors().size())){
                    s = new SlotMenu(slot, Items.getItem("next-button-cancel-template"), id, ActionType.OPEN_MENU);
                    //player.sendMessage(CustomCosmetics.getInstance().prefix + CustomCosmetics.getInstance().getMessages().getString("last-page"));
                }else {
                    s = new SlotMenu(slot, Items.getItem("next-button-template"), id, ActionType.OPEN_MENU);
                }
                s.setSound(Sound.getSound("on_click_next_page"));
                getContentMenu().addSlotMenu(s);
            }
        }
        for(SlotMenu slotMenu : getContentMenu().getSlotMenu().values()){
            setItemInPaginatedMenu(slotMenu, -1, -1, "_colored");
        }

        MagicCosmetics plugin = MagicCosmetics.getInstance();
        if(plugin.isPlaceholderAPI()){
            plugin.getVersion().updateTitle(playerData.getOfflinePlayer().getPlayer(), plugin.getPlaceholderAPI().setPlaceholders(playerData.getOfflinePlayer().getPlayer(), title.toString()));
            return;
        }
        plugin.getVersion().updateTitle(playerData.getOfflinePlayer().getPlayer(), title.toString());
    }

    private String setup(){
        String title = "";
        Items items = new Items(cosmetic.getItemStack());
        items.addPlaceHolder(playerData.getOfflinePlayer().getPlayer());
        SlotMenu slotMenu = new SlotMenu(getContentMenu().getPreviewSlot(), items, "", ActionType.CLOSE_MENU);
        getContentMenu().addSlotMenu(slotMenu);
        for(Color color : Color.colors.values()){
            if(color.isPrimaryItem())
                items = new Items(color.getId(), Items.getItem("color-template").copyItem(color, this.color));
            else
                items = new Items(color.getId(), Items.getItem("color-template").colorItem(color, this.color));
            if(color.getId().equalsIgnoreCase(this.color.getId())){
                title = color.getSelect();
            }
            if(!color.getName().isEmpty()) {
                ItemMeta itemMeta = items.getItemStack().getItemMeta();
                if(itemMeta != null) {
                    itemMeta.setDisplayName(color.getName());
                }
                items.getItemStack().setItemMeta(itemMeta);
            }
            slotMenu = new SlotMenu(color.getSlot(), items, getId()+"|"+items.getId()+"|"+cosmetic.getId(), ActionType.OPEN_MENU);
            slotMenu.setSound(Sound.getSound("on_click_item_colored"));
            getContentMenu().addSlotMenu(slotMenu);
        }
        return title;
    }

    public void setSecondaryColor(org.bukkit.Color secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public Cosmetic getCosmetic() {
        return cosmetic;
    }

    public Color getColor() {
        return color;
    }

    public org.bukkit.Color getSecondaryColor() {
        return secondaryColor;
    }

    public String[] getSelectedList(){
        return color.getRow().getSelected().split(",");
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
