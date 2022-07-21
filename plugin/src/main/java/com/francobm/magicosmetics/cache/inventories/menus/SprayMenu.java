package com.francobm.magicosmetics.cache.inventories.menus;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.api.CosmeticType;
import com.francobm.magicosmetics.cache.Cosmetic;
import com.francobm.magicosmetics.cache.PlayerCache;
import com.francobm.magicosmetics.cache.Sound;
import com.francobm.magicosmetics.cache.inventories.*;
import com.francobm.magicosmetics.cache.items.Items;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Collections;
import java.util.List;

public class SprayMenu extends PaginatedMenu {

    public SprayMenu(String id, ContentMenu contentMenu, int startSlot, int endSlot, int backSlot, int nextSlot, int pagesSlot, List<Integer> slotsUnavailable) {
        super(id, contentMenu, startSlot, endSlot, backSlot, nextSlot, pagesSlot, slotsUnavailable);
    }

    public SprayMenu(String id, ContentMenu contentMenu) {
        super(id, contentMenu);
    }

    public SprayMenu(PlayerCache playerCache, Menu menu) {
        super(playerCache, menu);
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        SlotMenu slotMenu = getContentMenu().getSlotMenuBySlot(slot);
        if(slotMenu == null) return;
        if(slotMenu.getItems().getId().endsWith("_spray")) {
            if (slotMenu.getActionType().size() == 2) {
                if (event.getClick() == ClickType.SHIFT_LEFT) {
                    slotMenu.action(player, slotMenu.getActionType().get(1));
                    setItems();
                    return;
                }
                slotMenu.action(player, slotMenu.getActionType().get(0));
                return;
            }
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
            List<Cosmetic> cosmetics = Cosmetic.getCosmeticsUnHideByType(CosmeticType.SPRAY);
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
        MagicCosmetics plugin = MagicCosmetics.getInstance();
        getContentMenu().getSlots().resetSlots();
        StringBuilder title = new StringBuilder();
        title.append(getContentMenu().getTitle());
        List<Cosmetic> cosmetics = Cosmetic.getCosmeticsUnHideByType(CosmeticType.SPRAY);
        if(getBackSlot() != -1) {
            SlotMenu s;
            if(page == 0){
                s = new SlotMenu(getBackSlot(), Items.getItem("back-button-cancel-template"), id, ActionType.OPEN_MENU);
            }else{
                s = new SlotMenu(getBackSlot(), Items.getItem("back-button-template"), id, ActionType.OPEN_MENU);
            }
            s.setSound(Sound.getSound("on_click_back_page"));
            getContentMenu().addSlotMenu(s);
        }
        if(getPagesSlot() != -1) {
            getContentMenu().addSlotMenu(new SlotMenu(getPagesSlot(), new Items(Items.getItem("pages-template").addVariableItem("%pages%", page + 1)), id, ActionType.CLOSE_MENU));
        }
        if(!cosmetics.isEmpty()) {
            int a = 0;
            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if (index >= cosmetics.size()) break;
                Cosmetic cosmetic = cosmetics.get(index);
                int slot = (getStartSlot() + i + a);
                if (cosmetic == null) continue;
                while(slotsUnavailable.contains(slot)){
                    slot++;
                    a++;
                }
                title.append(getContentMenu().getSlots().isSlot(slot));
                Items items = new Items(getPage()+index+"_spray", Items.getItem("spray-template").copyItem(playerCache, cosmetic, cosmetic.getItemStack()));
                SlotMenu slotMenu;
                items.addVariable("%equip%", playerCache.getEquip(cosmetic.getId()) != null ? MagicCosmetics.getInstance().getMessages().getString("equip") : MagicCosmetics.getInstance().getMessages().getString("unequip"));
                items.addPlaceHolder(playerCache.getOfflinePlayer().getPlayer());
                if(plugin.isPermissions()){
                    items.addVariable("%name%", cosmetic.getName()).addVariable("%available%", cosmetic.hasPermission(playerCache.getOfflinePlayer().getPlayer()) ? MagicCosmetics.getInstance().getMessages().getString("available") : MagicCosmetics.getInstance().getMessages().getString("unavailable")).addVariable("%type%", cosmetic.getCosmeticType());
                    if(cosmetic.hasPermission(playerCache.getOfflinePlayer().getPlayer())){
                        title.append(playerCache.getEquip(cosmetic.getId()) != null ? plugin.equip : plugin.ava);
                    }else{
                        title.append(plugin.unAva);
                    }
                }else {
                    items.addVariable("%name%", cosmetic.getName()).addVariable("%available%", playerCache.getCosmeticById(cosmetic.getId()) != null ? MagicCosmetics.getInstance().getMessages().getString("available") : MagicCosmetics.getInstance().getMessages().getString("unavailable")).addVariable("%type%", cosmetic.getCosmeticType());
                    if (playerCache.getCosmeticById(cosmetic.getId()) != null) {
                        title.append(playerCache.getEquip(cosmetic.getId()) != null ? plugin.equip : plugin.ava);
                    } else {
                        title.append(plugin.unAva);
                    }
                }
                title.append(getPanel(slot));
                if(playerCache.getSpray() != null){
                    if(playerCache.getSpray().getId().equalsIgnoreCase(cosmetic.getId())){
                        slotMenu = new SlotMenu(slot, items, Collections.singletonList("magiccos unset " + cosmetic.getId()), ActionType.PLAYER_COMMAND);
                    }else{
                        if(cosmetic.isColored()){
                            slotMenu = new SlotMenu(slot, items, Collections.singletonList("magiccos unuse " + cosmetic.getId()),"colored|color1|"+cosmetic.getId(), ActionType.OPEN_MENU, ActionType.PLAYER_COMMAND);
                        }else{
                            slotMenu = new SlotMenu(slot, items, cosmetic, ActionType.PREVIEW_ITEM, ActionType.PLAYER_COMMAND);
                            slotMenu.getCommands().add("magiccos unuse " + cosmetic.getId());
                        }
                    }
                }else{
                    if(cosmetic.isColored()){
                        slotMenu = new SlotMenu(slot, items, Collections.singletonList("magiccos unuse " + cosmetic.getId()),"colored|color1|"+cosmetic.getId(), ActionType.OPEN_MENU, ActionType.PLAYER_COMMAND);
                    }else{
                        slotMenu = new SlotMenu(slot, items, cosmetic, ActionType.PREVIEW_ITEM, ActionType.PLAYER_COMMAND);
                        slotMenu.getCommands().add("magiccos unuse " + cosmetic.getId());
                    }
                }
                slotMenu.setSound(Sound.getSound("on_click_cosmetic"));
                getContentMenu().addSlotMenu(slotMenu);
                setItemInPaginatedMenu(slotMenu, getPage(), index, "_spray");
            }
        }
        if(getNextSlot() != -1){
            SlotMenu s;
            if(((index + 1) >= cosmetics.size())){
                s = new SlotMenu(getNextSlot(), Items.getItem("next-button-cancel-template"), id, ActionType.OPEN_MENU);
                //player.sendMessage(CustomCosmetics.getInstance().prefix + CustomCosmetics.getInstance().getMessages().getString("last-page"));
            }else {
                s = new SlotMenu(getNextSlot(), Items.getItem("next-button-template"), id, ActionType.OPEN_MENU);
            }
            s.setSound(Sound.getSound("on_click_next_page"));
            getContentMenu().addSlotMenu(s);
        }
        for(SlotMenu slotMenu : getContentMenu().getSlotMenu().values()){
            setItemInPaginatedMenu(slotMenu, -1, -1, "_spray");
        }
        MagicCosmetics.getInstance().getVersion().updateTitle(playerCache.getOfflinePlayer().getPlayer(), title.toString());
    }

}
