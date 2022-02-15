package com.francobm.customcosmetics.cache.inventories;

import com.francobm.customcosmetics.CustomCosmetics;
import com.francobm.customcosmetics.cache.*;
import com.francobm.customcosmetics.cache.items.Items;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.List;

public class SlotMenu {
    private final int slot;
    private final Items items;
    private final List<ActionType> actionType;
    private final List<String> commands;
    private final String menu;
    private final SlotMenu slotMenu;
    private final Cosmetic cosmetic;
    private final Token token;
    private Sound sound;

    public SlotMenu(int slot, Items items, ActionType actionType, List<String> commands, String menu, SlotMenu slotMenu, Cosmetic cosmetic, Token token, Sound sound) {
        this.slot = slot;
        this.items = items;
        this.actionType = new ArrayList<>();
        this.actionType.add(actionType);
        this.commands = commands;
        this.menu = menu;
        this.slotMenu = slotMenu;
        this.cosmetic = cosmetic;
        this.token = token;
        this.sound = sound;
    }

    public SlotMenu(int slot, Items items, ActionType actionType, List<String> commands, String menu) {
        this.slot = slot;
        this.items = items;
        this.actionType = new ArrayList<>();
        this.actionType.add(actionType);
        this.commands = commands;
        this.menu = menu;
        this.slotMenu = null;
        this.cosmetic = null;
        this.token = null;
        this.sound = null;
    }

    public SlotMenu(int slot, Items items, ActionType actionType, List<String> commands, String menu, Sound sound) {
        this.slot = slot;
        this.items = items;
        this.actionType = new ArrayList<>();
        this.actionType.add(actionType);
        this.commands = commands;
        this.menu = menu;
        this.slotMenu = null;
        this.cosmetic = null;
        this.token = null;
        this.sound = sound;
    }

    public SlotMenu(int slot, Items items, ActionType actionType, List<String> commands) {
        this.slot = slot;
        this.items = items;
        this.actionType = new ArrayList<>();
        this.actionType.add(actionType);
        this.commands = commands;
        this.menu = "";
        this.slotMenu = null;
        this.cosmetic = null;
        this.token = null;
        this.sound = null;
    }

    public SlotMenu(int slot, Items items, ActionType actionType, String menu) {
        this.slot = slot;
        this.items = items;
        this.actionType = new ArrayList<>();
        this.actionType.add(actionType);
        this.commands = new ArrayList<>();
        this.menu = menu;
        this.slotMenu = null;
        this.cosmetic = null;
        this.token = null;
        this.sound = null;
    }

    public SlotMenu(int slot, Items items, ActionType actionType, SlotMenu slotMenu) {
        this.slot = slot;
        this.items = items;
        this.actionType = new ArrayList<>();
        this.actionType.add(actionType);
        this.commands = new ArrayList<>();
        this.menu = "";
        this.slotMenu = slotMenu;
        this.cosmetic = null;
        this.token = null;
        this.sound = null;
    }

    public SlotMenu(int slot, Items items, ActionType actionType, Cosmetic cosmetic) {
        this.slot = slot;
        this.items = items;
        this.actionType = new ArrayList<>();
        this.actionType.add(actionType);
        this.commands = new ArrayList<>();
        this.menu = "";
        this.slotMenu = null;
        this.cosmetic = cosmetic;
        this.token = null;
        this.sound = null;
    }

    public SlotMenu(int slot, Items items, ActionType actionType, Token token) {
        this.slot = slot;
        this.items = items;
        this.actionType = new ArrayList<>();
        this.actionType.add(actionType);
        this.commands = new ArrayList<>();
        this.menu = "";
        this.slotMenu = null;
        this.cosmetic = null;
        this.token = token;
        this.sound = null;
    }

    public int getSlot() {
        return slot;
    }

    public Items getItems() {
        return items;
    }

    public List<ActionType> getActionType() {
        return actionType;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void action(Player player){
        playSound(player);
        for(ActionType actionType : actionType){
            switch (actionType){
                case OPEN_MENU:
                    openMenu(player);
                    break;
                case CLOSE_MENU:
                    closeMenu(player);
                    break;
                case PLAYER_COMMAND:
                case CONSOLE_COMMAND:
                case COMMAND:
                    runCommands(player);
                    break;
                case ADD_ITEM_MENU:
                    addItemMenu(player);
                    break;
                case PREVIEW_ITEM:
                    previewItem(player);
                    break;
                case REMOVE_TOKEN_ADD_COSMETIC:
                    removeTokenAddCosmetic(player);
                    break;
            }
        }
    }

    public void action(Player player, ActionType actionType){
        playSound(player);
        switch (actionType){
            case OPEN_MENU:
                openMenu(player);
                break;
            case CLOSE_MENU:
                closeMenu(player);
                break;
            case PLAYER_COMMAND:
            case CONSOLE_COMMAND:
            case COMMAND:
                runCommands(player);
                break;
            case ADD_ITEM_MENU:
                addItemMenu(player);
                break;
            case PREVIEW_ITEM:
                previewItem(player);
                break;
            case REMOVE_TOKEN_ADD_COSMETIC:
                removeTokenAddCosmetic(player);
                break;
        }
    }

    private void runCommands(Player player){
        for(String command : commands){
            command = command.replace("%player%", player.getName());
            for(ActionType actionType : actionType){
                switch (actionType){
                    case COMMAND:
                    case CONSOLE_COMMAND:
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                        CustomCosmetics.getInstance().getLogger().info("Comando: " + command);
                        closeMenu(player);
                        break;
                    case PLAYER_COMMAND:
                        Bukkit.dispatchCommand(player, command);
                        CustomCosmetics.getInstance().getLogger().info("Comando: " + command);
                        closeMenu(player);
                        break;
                }
            }
        }
    }

    private void removeTokenAddCosmetic(Player player){
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        if(playerCache.removeTokenInPlayer()){
            CustomCosmetics.getInstance().getCosmeticsManager().addCosmetic(player, token.getCosmetic());
        }
        closeMenu(player);
    }

    private void previewItem(Player player){
        PlayerCache playerCache = PlayerCache.getPlayer(player);
        if(playerCache.getCosmeticById(cosmetic.getId()) != null) {
            playerCache.removeCosmetic(cosmetic.getId());
            playerCache.addCosmetic(cosmetic);
            playerCache.setCosmetic(cosmetic);
        }
        playerCache.setPreviewCosmetic(cosmetic);
        closeMenu(player);
    }

    private void addItemMenu(Player player){
        InventoryHolder holder = player.getOpenInventory().getTopInventory().getHolder();
        if(holder instanceof Menu){
            Menu menu = (Menu) holder;
            menu.getContentMenu().addSlotMenu(slotMenu);
            menu.setItemInMenu(slotMenu);
        }
    }

    public void playSound(Player player){
        if(sound == null) {
            CustomCosmetics.getInstance().getLogger().warning("Sound is null!");
            return;
        }
        CustomCosmetics.getInstance().getVersion().sendSound(player, sound);
    }

    private void openMenu(Player player){
        String[] split = menu.split("\\|");
        if(split.length > 1){
            Color color = Color.getColor(split[1]);
            Cosmetic cosmetic = Cosmetic.getCloneCosmetic(split[2]);
            if(color == null){
                CustomCosmetics.getInstance().getLogger().info("Color Null");
                return;
            }
            if(cosmetic == null){
                CustomCosmetics.getInstance().getLogger().info("Cosmetic Null");
                return;
            }
            CustomCosmetics.getInstance().getCosmeticsManager().openMenuColor(player, split[0], color, cosmetic);
            return;
        }
        CustomCosmetics.getInstance().getCosmeticsManager().openMenu(player, this.menu);
    }

    public void setSound(Sound sound) {
        this.sound = sound;
    }

    public Sound getSound() {
        return sound;
    }

    private void closeMenu(Player player){
        player.closeInventory();
    }

    public String getMenu() {
        return menu;
    }


}
