package com.francobm.customcosmetics.cache.inventories;

import com.francobm.customcosmetics.CustomCosmetics;
import com.francobm.customcosmetics.cache.PlayerCache;
import com.francobm.customcosmetics.cache.Sound;
import com.francobm.customcosmetics.cache.inventories.menus.*;
import com.francobm.customcosmetics.cache.items.Items;
import com.francobm.customcosmetics.files.FileCreator;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.*;

public abstract class Menu implements InventoryHolder {
    public static Map<String, Menu> inventories = new HashMap<>();
    protected final String id;
    protected final PlayerCache playerCache;
    protected final ContentMenu contentMenu;

    public Menu(String id, ContentMenu contentMenu){
        this.id = id;
        this.contentMenu = contentMenu;
        this.playerCache = null;
        CustomCosmetics.getInstance().getLogger().info("Menu named: '" + id + "' registered.");
    }

    public Menu(PlayerCache playerCache, Menu menu) {
        this.id = menu.id;
        this.contentMenu = new ContentMenu(menu.getContentMenu().getTitle(), menu.getContentMenu().getSize(), menu.getContentMenu().getInventoryType(), menu.getContentMenu().getSlotMenu());
        this.playerCache = playerCache;
    }

    public void open(){
        getContentMenu().createInventory(this);
        setItems();
        if(playerCache == null) return;
        playerCache.getOfflinePlayer().getPlayer().openInventory(getInventory());
    }

    public static void loadMenus(){
        inventories.clear();
        CustomCosmetics plugin = CustomCosmetics.getInstance();
        FileCreator menu = plugin.getMenus();
        for(String key : menu.getConfigurationSection("menus").getKeys(false)){
            String title = "";
            int size = 0;
            InventoryType inventoryType = null;
            Map<Integer, SlotMenu> slotMenus = new HashMap<>();

            int startSlot = 0;
            int endSlot = 0;
            int pagesSlot = 0;
            int backButtonSlot = 0;
            int nextButtonSlot = 0;
            List<Integer> slotsUnavailable = new ArrayList<>();
            if(menu.contains("menus." + key + ".title")){
                title = menu.getString("menus." + key + ".title");
                if(plugin.isItemsAdder()){
                    if(plugin.getItemsAdder().isFontImageWrapper(title)) {
                        title = plugin.getItemsAdder().getFontImageWrapperString(title);
                    }
                }
            }
            if(menu.contains("menus." + key + ".size")){
                size = menu.getInt("menus." + key + ".size");
            }
            if(menu.contains("menus." + key + ".type")){
                String type = menu.getString("menus." + key + ".type");
                try {
                    inventoryType = InventoryType.valueOf(type);
                }catch (IllegalArgumentException exception){
                    plugin.getLogger().info("Menu id '" + key + "' type: " + type + " Not Found.");
                }
            }
            //
            if(menu.contains("menus." + key + ".start-slot")){
                startSlot = menu.getInt("menus." + key + ".start-slot");
            }
            if(menu.contains("menus." + key + ".end-slot")){
                endSlot = menu.getInt("menus." + key + ".end-slot");
            }
            if(menu.contains("menus." + key + ".pages-slot")){
                pagesSlot = menu.getInt("menus." + key + ".pages-slot");
            }
            if(menu.contains("menus." + key + ".back-button-slot")){
                backButtonSlot = menu.getInt("menus." + key + ".back-button-slot");
            }
            if(menu.contains("menus." + key + ".next-button-slot")){
                nextButtonSlot = menu.getInt("menus." + key + ".next-button-slot");
            }
            if(menu.contains("menus." + key + ".unavailable-slots")){
                String format = menu.getString("menus." + key + ".unavailable-slots");
                String[] slots = format.split(",");
                for(String slot : slots){
                    try{
                        slotsUnavailable.add(Integer.parseInt(slot));
                    }catch (NumberFormatException exception){
                        plugin.getLogger().info("Menu id '" + key + "' with Unavailable slot '" + slot + "' Not Number");
                    }
                }
            }
            //
            if(inventoryType == null) return;
            for(String slot : menu.getConfigurationSection("menus." + key).getKeys(false)){
                if(!menu.contains("menus." + key + "." + slot + ".slot")) continue;
                int itemSlot = 0;
                Items item = null;
                ActionType actionType = null;
                Sound sound = null;
                List<String> commands = new ArrayList<>();
                String open_menu = "";
                if(menu.contains("menus." + key + "." + slot + ".slot")){
                    itemSlot = menu.getInt("menus." + key + "." + slot + ".slot");
                }
                if(menu.contains("menus." + key + "." + slot + ".item")){
                    String itemName = menu.getString("menus." + key + "." + slot + ".item");
                    item = Items.getItem(itemName);
                }
                if(menu.contains("menus." + key + "." + slot + ".action.type")) {
                    String type = menu.getString("menus." + key + "." + slot + ".action.type");
                    try{
                        actionType = ActionType.valueOf(type.toUpperCase());
                    }catch (IllegalArgumentException exception){
                        plugin.getLogger().info("Menu id '" + key + "' with slot '" + slot + "' Action " + type + " Not Found");
                    }
                }
                if(menu.contains("menus." + key + "." + slot + ".action.commands")) {
                    commands = menu.getStringList("menus." + key + "." + slot + ".action.commands");
                }
                if(menu.contains("menus." + key + "." + slot + ".action.menu")) {
                    open_menu = menu.getString("menus." + key + "." + slot + ".action.menu");
                }
                if(menu.contains("menus." + key + "." + slot + ".sound")) {
                    String s = menu.getString("menus." + key + "." + slot + ".sound");
                    sound = Sound.getSound(s);
                }
                slotMenus.put(itemSlot, new SlotMenu(itemSlot, item, actionType, commands, open_menu, sound));
            }
            ContentMenu contentMenu = new ContentMenu(title, size, inventoryType, slotMenus);
            switch (inventoryType){
                case HAT:
                    inventories.put(key, new HatMenu(key, contentMenu, startSlot, endSlot, backButtonSlot, nextButtonSlot, pagesSlot, slotsUnavailable));
                    break;
                case BAG:
                    inventories.put(key, new BagMenu(key, contentMenu, startSlot, endSlot, backButtonSlot, nextButtonSlot, pagesSlot, slotsUnavailable));
                    break;
                case WALKING_STICK:
                    inventories.put(key, new WStickMenu(key, contentMenu, startSlot, endSlot, backButtonSlot, nextButtonSlot, pagesSlot, slotsUnavailable));
                    break;
                case BALLOON:
                    inventories.put(key, new BalloonMenu(key, contentMenu, startSlot, endSlot, backButtonSlot, nextButtonSlot, pagesSlot, slotsUnavailable));
                    break;
                case FREE:
                    inventories.put(key, new FreeMenu(key, contentMenu));
                    break;
                case COLORED:
                    inventories.put(key, new ColoredMenu(key, contentMenu, startSlot, endSlot, backButtonSlot, nextButtonSlot, pagesSlot, slotsUnavailable));
                    break;
                case TOKEN:
                    inventories.put(key, new TokenMenu(key, contentMenu));
                    break;
            }
        }
    }

    public String getId() {
        return id;
    }

    public ContentMenu getContentMenu() {
        return contentMenu;
    }

    public abstract void handleMenu(InventoryClickEvent event);

    public abstract void setItems();

    public void setItemInMenu(SlotMenu slotMenu){
        if(contentMenu == null) return;
        if(slotMenu.getItems() == null){
            CustomCosmetics.getInstance().getLogger().info("Slot: " + slotMenu.getSlot() + " is Null!");
            return;
        }
        contentMenu.getInventory().setItem(slotMenu.getSlot(), slotMenu.getItems().getItemStack());
    }

    public void setItemInPaginatedMenu(SlotMenu slotMenu, int page, int index, String endsWith){
        if(contentMenu == null) return;
        if(slotMenu.getItems() == null){
            CustomCosmetics.getInstance().getLogger().info("Slot: " + slotMenu.getSlot() + " is Null!");
            return;
        }
        if(!slotMenu.getItems().getId().endsWith(endsWith)){
            setItemInMenu(slotMenu);
            return;
        }
        if(!slotMenu.getItems().getId().equalsIgnoreCase(page+index+endsWith)) return;

        contentMenu.getInventory().setItem(slotMenu.getSlot(), slotMenu.getItems().getItemStack());
    }

    @Override
    public Inventory getInventory() {
        return contentMenu.getInventory();
    }

    @Override
    public String toString() {
        return "Menu{" +
                "id='" + id + '\'' +
                ", playerCache=" + playerCache +
                ", contentMenu=" + contentMenu +
                '}';
    }
}