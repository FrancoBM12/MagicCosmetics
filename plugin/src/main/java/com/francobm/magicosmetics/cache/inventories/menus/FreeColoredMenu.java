package com.francobm.magicosmetics.cache.inventories.menus;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.cache.Color;
import com.francobm.magicosmetics.cache.PlayerData;
import com.francobm.magicosmetics.cache.SecondaryColor;
import com.francobm.magicosmetics.cache.Sound;
import com.francobm.magicosmetics.cache.inventories.*;
import com.francobm.magicosmetics.cache.items.Items;
import com.francobm.magicosmetics.utils.Utils;
import com.francobm.magicosmetics.utils.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class FreeColoredMenu extends PaginatedMenu {

    private Color color;
    private SecondaryColor secondaryColor;
    private ItemStack itemStack;
    private Items containItem;
    private List<String> unavailableColors;

    public FreeColoredMenu(String id, ContentMenu contentMenu, int startSlot, int endSlot, Set<Integer> backSlot, Set<Integer> nextSlot, int pagesSlot, List<Integer> slotsUnavailable, Items containItem, List<String> unavailableColors) {
        super(id, contentMenu, startSlot, endSlot, backSlot, nextSlot, pagesSlot, slotsUnavailable);
        this.containItem = containItem;
        this.unavailableColors = unavailableColors;
    }

    public FreeColoredMenu(String id, ContentMenu contentMenu) {
        super(id, contentMenu);
    }

    public FreeColoredMenu(PlayerData playerData, Menu menu, Color color) {
        super(playerData, menu);
        this.color = color;
        this.containItem = ((FreeColoredMenu)menu).getContainItem();
        this.secondaryColor = color.getSecondaryColors().get(0);
    }

    public FreeColoredMenu getClone(PlayerData playerData, Color color, ItemStack itemStack) {
        FreeColoredMenu freeColoredMenu = new FreeColoredMenu(this.getId(), this.getContentMenu().getClone(), this.getStartSlot(), this.getEndSlot(), this.getBackSlot(), this.getNextSlot(), this.getPagesSlot(), this.getSlotsUnavailable(), this.getContainItem(), this.getUnavailableColors());
        freeColoredMenu.playerData = playerData;
        freeColoredMenu.setColor(color);
        freeColoredMenu.itemStack = itemStack;
        freeColoredMenu.secondaryColor = color.getSecondaryColors().get(0);
        return freeColoredMenu;
    }

    public FreeColoredMenu getClone(PlayerData playerData, Color color) {
        FreeColoredMenu freeColoredMenu = new FreeColoredMenu(this.getId(), this.getContentMenu().getClone(), this.getStartSlot(), this.getEndSlot(), this.getBackSlot(), this.getNextSlot(), this.getPagesSlot(), this.getSlotsUnavailable(), this.getContainItem(), this.getUnavailableColors());
        freeColoredMenu.playerData = playerData;
        freeColoredMenu.setColor(color);
        freeColoredMenu.secondaryColor = color.getSecondaryColors().get(0);
        return freeColoredMenu;
    }


    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if(event.getClickedInventory() == null) return;
        int slot = event.getSlot();
        if(event.getClickedInventory().getType() == InventoryType.PLAYER){
            if(event.getClick() != ClickType.LEFT) {
                event.setCancelled(true);
                return;
            }
            return;
        }
        if(event.getClick() != ClickType.LEFT) {
            event.setCancelled(true);
            return;
        }
        if (getContentMenu().getPreviewSlot() == slot) {
            if(event.getCursor().getType() != XMaterial.AIR.parseMaterial()) {
                if(containItem != null) {
                    if (containItem.isColored(event.getCursor())) {
                        this.itemStack = event.getCursor().clone();
                        SlotMenu slotMenu1 = new SlotMenu(getContentMenu().getPreviewSlot(), new Items(itemStack), "");
                        getContentMenu().addSlotMenu(slotMenu1);
                        setResultItem();
                        return;
                    }
                    event.setCancelled(true);
                    return;
                }
                if(Utils.isDyeable(event.getCursor()) && color.hasPermission(player)) {
                    this.itemStack = event.getCursor().clone();
                    SlotMenu slotMenu1 = new SlotMenu(getContentMenu().getPreviewSlot(), new Items(itemStack), "");
                    getContentMenu().addSlotMenu(slotMenu1);
                    setResultItem();
                    return;
                }
                event.setCancelled(true);
                return;
            }
            if(event.getCurrentItem() == null) return;
            this.itemStack = null;
            getContentMenu().removeSlotMenu(getContentMenu().getPreviewSlot());
            getContentMenu().removeSlotMenu(getContentMenu().getResultSlot());
            event.getClickedInventory().setItem(getContentMenu().getResultSlot(), XMaterial.AIR.parseItem());
            return;
        }
        if (getContentMenu().getResultSlot() == slot) {
            if (event.getCurrentItem() == null) {
                event.setCancelled(true);
                return;
            }
            this.itemStack = null;
            getContentMenu().removeSlotMenu(getContentMenu().getPreviewSlot());
            getContentMenu().removeSlotMenu(getContentMenu().getResultSlot());
            event.getClickedInventory().setItem(getContentMenu().getPreviewSlot(), XMaterial.AIR.parseItem());
            return;
        }

        event.setCancelled(true);
        if(event.getCurrentItem() == null) return;
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
            setSecondaryColor((SecondaryColor) null);
            setItems();
            return;
        }
        if(getNextSlot().contains(slotMenu.getSlot())){
            slotMenu.playSound(player);
            if(((index + 1) >= color.getSecondaryColors().size())){
                //player.sendMessage(CustomCosmetics.getInstance().prefix + CustomCosmetics.getInstance().getMessages().getString("last-page"));
                return;
            }
            page = page + 1;
            setSecondaryColor((SecondaryColor) null);
            setItems();
            return;
        }
        slotMenu.action(player);
    }

    public void setResultItem(){
        if(itemStack == null) return;
        Items resultItem = new Items(itemStack.clone()).coloredItem(secondaryColor.getColor());
        SlotMenu result = new SlotMenu(getContentMenu().getResultSlot(), resultItem, "");
        result.setSound(Sound.getSound("on_click_cosmetic_preview"));
        getContentMenu().addSlotMenu(result);
        setItemInMenu(result);
        //
        for (SlotMenu slotMenu : getContentMenu().getSlotMenu().values()) {
            if (!slotMenu.getItems().getId().endsWith("_colored")) continue;
            org.bukkit.Color color = slotMenu.getSlotMenu().getItems().getDyeColor();
            slotMenu.getSlotMenu().setItems(new Items(itemStack.clone()).coloredItem(color));
            /*SlotMenu sm = new SlotMenu(slotMenu.getSlot(), slotMenu.getItems(), slotMenu.getSlotMenu(), ActionType.ADD_ITEM_MENU);
            slotMenu.setSound(Sound.getSound("on_click_item_colored"));
            getContentMenu().addSlotMenu(sm);*/
        }
    }

    @Override
    public void setItems() {
        resetItems(Arrays.asList(getContentMenu().getPreviewSlot(), getContentMenu().getResultSlot()));
        getContentMenu().getSlots().resetSlots();
        if(!getBackSlot().isEmpty()) {
            SlotMenu s;
            for(int slot : getBackSlot()) {
                if(page == 0){
                    s = new SlotMenu(slot, Items.getItem("back-button-cancel-template"), id, ActionType.REFRESH);
                }else{
                    s = new SlotMenu(slot, Items.getItem("back-button-template"), id, ActionType.REFRESH);
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
                SecondaryColor dyeColor = color.getSecondaryColors().get(index);
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
                Items items = new Items(getPage()+index+"_colored", Items.getItem("color-template").colorItem(playerData.getOfflinePlayer().getPlayer(), dyeColor, secondaryColor));
                items.addPlaceHolder(playerData.getOfflinePlayer().getPlayer());
                if(dyeColor.getColor().asRGB() == secondaryColor.getColor().asRGB()){
                    //title.append(getContentMenu().getSlots().isSecondaryColored(sPrimaryColor, slot));
                    title.append(selected[i]);
                }
                Items resultItem;
                if(itemStack == null){
                    resultItem = new Items(XMaterial.AIR.parseItem()).coloredItem(dyeColor.getColor());
                }else {
                    resultItem = new Items(itemStack.clone()).coloredItem(dyeColor.getColor());
                }
                SlotMenu result = new SlotMenu(getContentMenu().getResultSlot(), resultItem, "");
                result.setSound(Sound.getSound("on_click_cosmetic_preview"));
                if (i == 0) {
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
                    s = new SlotMenu(slot, Items.getItem("next-button-cancel-template"), id, ActionType.REFRESH);
                    //player.sendMessage(CustomCosmetics.getInstance().prefix + CustomCosmetics.getInstance().getMessages().getString("last-page"));
                }else {
                    s = new SlotMenu(slot, Items.getItem("next-button-template"), id, ActionType.REFRESH);
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
        Items items;
        SlotMenu slotMenu;
        for(Color color : Color.colors.values()){
            if(unavailableColors.contains(color.getId())) continue;
            if(color.isPrimaryItem()){
                items = new Items(color.getId(), Items.getItem("color-template").copyItem(color, this.color));
            }else {
                items = new Items(color.getId(), Items.getItem("color-template").colorItem(color, this.color));
            }
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
            slotMenu = new SlotMenu(color.getSlot(), items, getId() + "|" + items.getId(), ActionType.OPEN_MENU);
            slotMenu.setSound(Sound.getSound("on_click_item_colored"));
            getContentMenu().addSlotMenu(slotMenu);
        }
        return title;
    }

    public void setSecondaryColor(org.bukkit.Color secondaryColor) {
        this.secondaryColor = new SecondaryColor(secondaryColor);
    }

    public void setSecondaryColor(SecondaryColor secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public Items getContainItem() {
        return containItem;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public SecondaryColor getSecondaryColor() {
        return secondaryColor;
    }

    public String[] getSelectedList(){
        return color.getRow().getSelected().split(",");
    }

    public void returnItem(){
        if(this.itemStack == null) return;
        Player player = playerData.getOfflinePlayer().getPlayer();
        if(player == null) return;
        if(player.getInventory().firstEmpty() == -1){
            player.getWorld().dropItem(player.getLocation(), this.itemStack);
            this.itemStack = null;
            getContentMenu().removeSlotMenu(getContentMenu().getPreviewSlot());
            getContentMenu().removeSlotMenu(getContentMenu().getResultSlot());
            return;
        }
        player.getInventory().addItem(itemStack);
        this.itemStack = null;
        getContentMenu().removeSlotMenu(getContentMenu().getPreviewSlot());
        getContentMenu().removeSlotMenu(getContentMenu().getResultSlot());
    }

    public List<String> getUnavailableColors() {
        return unavailableColors;
    }
}
