package com.francobm.magicosmetics.cache.inventories;

import com.francobm.magicosmetics.cache.Panel;
import com.francobm.magicosmetics.files.FileCreator;
import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.cache.PlayerCache;
import com.francobm.magicosmetics.cache.Sound;
import com.francobm.magicosmetics.cache.inventories.menus.*;
import com.francobm.magicosmetics.cache.items.Items;
import com.francobm.magicosmetics.utils.XMaterial;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class Menu implements InventoryHolder {
    public static Map<String, Menu> inventories = new HashMap<>();
    private static final Map<String, Panel> panels = new HashMap<>();
    protected final String id;
    protected final PlayerCache playerCache;
    protected final ContentMenu contentMenu;
    private String permission;

    public Menu(String id, ContentMenu contentMenu){
        this.id = id;
        this.contentMenu = contentMenu;
        this.playerCache = null;
        this.permission = "";
        MagicCosmetics.getInstance().getLogger().info("Menu named: '" + id + "' registered.");
    }

    public Menu(PlayerCache playerCache, Menu menu) {
        this.id = menu.id;
        this.contentMenu = new ContentMenu(menu.getContentMenu().getTitle(), menu.getContentMenu().getSize(), menu.getContentMenu().getInventoryType(), menu.getContentMenu().getSlotMenu(), menu.getContentMenu().getPreviewSlot(), menu.getContentMenu().getResultSlot());
        this.permission = menu.permission;
        this.playerCache = playerCache;
    }

    public static Panel getPanel(String id){
        return panels.get(id);
    }

    public void open(){
        if(MagicCosmetics.getInstance().isPlaceholderAPI()){
            getContentMenu().createInventory(this, MagicCosmetics.getInstance().getPlaceholderAPI().setPlaceholders(playerCache.getOfflinePlayer().getPlayer(), getContentMenu().getTitle()));
        }else {
            getContentMenu().createInventory(this);
        }
        if(playerCache == null) return;
        playerCache.getOfflinePlayer().getPlayer().openInventory(getInventory());
        setItems();
    }

    public static void loadMenus(){
        inventories.clear();
        panels.clear();
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        FileCreator menu = plugin.getMenus();
        for(String key : menu.getConfigurationSection("menus.panels").getKeys(false)){
            String character = menu.getString("menus.panels." + key);
            if(plugin.isItemsAdder()){
                character = plugin.getItemsAdder().replaceFontImageWithoutColor(character);
            }
            if(plugin.isOraxen()) {
                character = plugin.getOraxen().replaceFontImages(character);
            }
            panels.put(key, new Panel(key, character));
        }
        for(String key : menu.getConfigurationSection("menus").getKeys(false)){
            if(!menu.contains("menus." + key + ".title")) continue;
            String perm = "";
            String title = "";
            int size = 0;
            InventoryType inventoryType = null;
            Map<Integer, SlotMenu> slotMenus = new HashMap<>();

            int startSlot = 0;
            int endSlot = 0;
            int pagesSlot = 0;
            int backButtonSlot = 0;
            int nextButtonSlot = 0;
            int previewSlot = 0;
            int resultSlot = 0;
            List<Integer> slotsUnavailable = new ArrayList<>();
            Items containItem = null;
            boolean drag = false;
            if(menu.contains("menus." + key + ".permission")){
                perm = menu.getString("menus." + key + ".permission");
            }
            if(menu.contains("menus." + key + ".title")){
                title = menu.getString("menus." + key + ".title");
                if(plugin.isItemsAdder()){
                    title = plugin.getItemsAdder().replaceFontImages(title);
                }
                if(plugin.isOraxen()){
                    title = plugin.getOraxen().replaceFontImages(title);
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
            if(inventoryType == null) continue;
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
            if(menu.contains("menus." + key + ".preview-slot")){
                previewSlot = menu.getInt("menus." + key + ".preview-slot");
            }
            if(menu.contains("menus." + key + ".result-slot")){
                resultSlot = menu.getInt("menus." + key + ".result-slot");
            }
            if(menu.contains("menus." + key + ".result-slot")){
                resultSlot = menu.getInt("menus." + key + ".result-slot");
            }
            if(menu.contains("menus." + key + ".contains-item")){
                containItem = Items.getItem(menu.getString("menus." + key + ".contains-item"));
            }
            if(menu.contains("menus." + key + ".drag")){
                drag = menu.getBoolean("menus." + key + ".drag");
            }
            //
            for(String slot : menu.getConfigurationSection("menus." + key).getKeys(false)){
                if(!menu.contains("menus." + key + "." + slot + ".slot")) continue;
                int itemSlot = 0;
                Items item = null;
                List<ActionType> actionType = new ArrayList<>();
                Sound sound = null;
                List<String> commands = new ArrayList<>();
                String open_menu = "";
                String permission = "";
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
                        actionType.add(ActionType.valueOf(type.toUpperCase()));
                    }catch (IllegalArgumentException exception){
                        plugin.getLogger().info("Menu id '" + key + "' with slot '" + slot + "' Action " + type + " Not Found");
                    }
                }
                if(menu.contains("menus." + key + "." + slot + ".action.types")) {
                    List<String> types = menu.getStringListWF("menus." + key + "." + slot + ".action.types");
                    for(String type : types){
                        try{
                            actionType.add(ActionType.valueOf(type.toUpperCase()));
                        }catch (IllegalArgumentException exception){
                            plugin.getLogger().info("Menu id '" + key + "' with slot '" + slot + "' Action " + type + " Not Found");
                        }
                    }
                }
                if(menu.contains("menus." + key + "." + slot + ".action.commands")) {
                    commands = menu.getStringList("menus." + key + "." + slot + ".action.commands");
                }
                if(menu.contains("menus." + key + "." + slot + ".action.menu")) {
                    open_menu = menu.getString("menus." + key + "." + slot + ".action.menu");
                }
                if(menu.contains("menus." + key + "." + slot + ".permission")) {
                    permission = menu.getString("menus." + key + "." + slot + ".permission");
                }
                if(menu.contains("menus." + key + "." + slot + ".sound")) {
                    String s = menu.getString("menus." + key + "." + slot + ".sound");
                    sound = Sound.getSound(s);
                }
                slotMenus.put(itemSlot, new SlotMenu(itemSlot, item, commands, open_menu, sound, permission, actionType));
            }
            ContentMenu contentMenu = new ContentMenu(title, size, inventoryType, slotMenus, previewSlot, resultSlot);
            switch (inventoryType){
                case HAT:
                    HatMenu hatMenu = new HatMenu(key, contentMenu, startSlot, endSlot, backButtonSlot, nextButtonSlot, pagesSlot, slotsUnavailable);
                    hatMenu.setPermission(perm);
                    inventories.put(key, hatMenu);
                    break;
                case BAG:
                    BagMenu bagMenu = new BagMenu(key, contentMenu, startSlot, endSlot, backButtonSlot, nextButtonSlot, pagesSlot, slotsUnavailable);
                    bagMenu.setPermission(perm);
                    inventories.put(key, bagMenu);
                    break;
                case WALKING_STICK:
                    WStickMenu wStickMenu = new WStickMenu(key, contentMenu, startSlot, endSlot, backButtonSlot, nextButtonSlot, pagesSlot, slotsUnavailable);
                    wStickMenu.setPermission(perm);
                    inventories.put(key, wStickMenu);
                    break;
                case BALLOON:
                    BalloonMenu balloonMenu = new BalloonMenu(key, contentMenu, startSlot, endSlot, backButtonSlot, nextButtonSlot, pagesSlot, slotsUnavailable);
                    balloonMenu.setPermission(perm);
                    inventories.put(key, balloonMenu);
                    break;
                case SPRAY:
                    SprayMenu sprayMenu = new SprayMenu(key, contentMenu, startSlot, endSlot, backButtonSlot, nextButtonSlot, pagesSlot, slotsUnavailable);
                    sprayMenu.setPermission(perm);
                    inventories.put(key, sprayMenu);
                    break;
                case FREE:
                    FreeMenu freeMenu = new FreeMenu(key, contentMenu);
                    freeMenu.setPermission(perm);
                    inventories.put(key, freeMenu);
                    break;
                case COLORED:
                    ColoredMenu coloredMenu = new ColoredMenu(key, contentMenu, startSlot, endSlot, backButtonSlot, nextButtonSlot, pagesSlot, slotsUnavailable);
                    coloredMenu.setPermission(perm);
                    inventories.put(key, coloredMenu);
                    break;
                case FREE_COLORED:
                    FreeColoredMenu freeColoredMenu = new FreeColoredMenu(key, contentMenu, startSlot, endSlot, backButtonSlot, nextButtonSlot, pagesSlot, slotsUnavailable, containItem);
                    freeColoredMenu.setPermission(perm);
                    inventories.put(key, freeColoredMenu);
                    break;
                case TOKEN:
                    TokenMenu tokenMenu = new TokenMenu(key, contentMenu, drag);
                    tokenMenu.setPermission(perm);
                    inventories.put(key, tokenMenu);
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
            MagicCosmetics.getInstance().getLogger().info("Slot: " + slotMenu.getSlot() + " is Null!");
            return;
        }
        Items items = slotMenu.getItems();
        items.addPlaceHolder(playerCache.getOfflinePlayer().getPlayer());
        slotMenu.setItems(items);
        contentMenu.getInventory().setItem(slotMenu.getSlot(), slotMenu.getItems().getItemStack());
    }

    public void resetItems(List<Integer> ignoredSlots){
        for(SlotMenu slots : getContentMenu().getSlotMenu().values()){
            if(ignoredSlots.contains(slots.getSlot())) continue;
            contentMenu.getInventory().setItem(slots.getSlot(), XMaterial.AIR.parseItem());
        }
        contentMenu.resetSlotMenu(ignoredSlots);
    }

    public void setItemInPaginatedMenu(SlotMenu slotMenu, int page, int index, String endsWith){
        if(contentMenu == null) return;
        if(slotMenu.getItems() == null){
            MagicCosmetics.getInstance().getLogger().info("Slot: " + slotMenu.getSlot() + " is Null!");
            return;
        }
        if(!slotMenu.getItems().getId().endsWith(endsWith)){
            setItemInMenu(slotMenu);
            return;
        }
        if(slotMenu.getItems().getItemStack() == null){
            MagicCosmetics.getInstance().getLogger().info("Slot: " + slotMenu.getSlot() + " is Null!");
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

    public String getPanel(int slot){
        int panelId = 0;
        if(slot >= 0 && slot < 9) {
            panelId = 1;
        }
        if(slot >= 9 && slot < 18) {
            panelId = 2;
        }
        if(slot >= 18 && slot < 27) {
            panelId = 3;
        }
        if(slot >= 27 && slot < 36) {
            panelId = 4;
        }
        if(slot >= 36 && slot < 45) {
            panelId = 5;
        }
        if(slot >= 45 && slot < 54) {
            panelId = 6;
        }
        Panel panel = getPanel(String.valueOf(panelId));
        if(panel == null) return "";
        return panel.getCharacter();
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}