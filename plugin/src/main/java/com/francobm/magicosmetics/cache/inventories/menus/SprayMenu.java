package com.francobm.magicosmetics.cache.inventories.menus;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.api.CosmeticType;
import com.francobm.magicosmetics.api.Cosmetic;
import com.francobm.magicosmetics.cache.PlayerData;
import com.francobm.magicosmetics.cache.Sound;
import com.francobm.magicosmetics.cache.Token;
import com.francobm.magicosmetics.cache.inventories.*;
import com.francobm.magicosmetics.cache.items.Items;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class SprayMenu extends PaginatedMenu {

    public SprayMenu(String id, ContentMenu contentMenu, int startSlot, int endSlot, Set<Integer> backSlot, Set<Integer> nextSlot, int pagesSlot, List<Integer> slotsUnavailable) {
        super(id, contentMenu, startSlot, endSlot, backSlot, nextSlot, pagesSlot, slotsUnavailable);
    }

    public SprayMenu(String id, ContentMenu contentMenu) {
        super(id, contentMenu);
    }

    public SprayMenu(PlayerData playerData, Menu menu) {
        super(playerData, menu);
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        SlotMenu slotMenu = getContentMenu().getSlotMenuBySlot(slot);
        if(slotMenu == null) return;
        if(slotMenu.getItems().getId().endsWith("_spray")) {
            if(slotMenu.isExchangeable()){
                Cosmetic cosmetic = slotMenu.getTempCosmetic();
                if(cosmetic != null && playerData.hasCosmeticById(cosmetic.getId())){
                    if(event.getClick() == ClickType.SHIFT_LEFT ){
                        if(!slotMenu.action(player, ActionType.REMOVE_COSMETIC_ADD_TOKEN)) return;
                        setItems();
                        return;
                    }
                }
                slotMenu.action(player);
                return;
            }
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
            int cosmetics = Cosmetic.getCosmeticCount(CosmeticType.SPRAY);
            slotMenu.playSound(player);
            if(((index + 1) >= cosmetics)){
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
                Items items = new Items(getPage()+index+"_spray", Items.getItem("spray-template").copyItem(playerData, cosmetic, cosmetic.getItemStack()));
                SlotMenu slotMenu;
                items.addVariable("%equip%", playerData.getEquip(cosmetic.getId()) != null ? MagicCosmetics.getInstance().getMessages().getString("equip") : MagicCosmetics.getInstance().getMessages().getString("unequip"));
                items.addPlaceHolder(playerData.getOfflinePlayer().getPlayer());
                if(plugin.isPermissions()){
                    items.addVariable("%name%", cosmetic.getName()).addVariable("%available%", cosmetic.hasPermission(playerData.getOfflinePlayer().getPlayer()) ? MagicCosmetics.getInstance().getMessages().getString("available") : MagicCosmetics.getInstance().getMessages().getString("unavailable")).addVariable("%type%", cosmetic.getCosmeticType());
                    if(cosmetic.hasPermission(playerData.getOfflinePlayer().getPlayer())){
                        title.append(playerData.getEquip(cosmetic.getId()) != null ? plugin.equip : plugin.ava);
                    }else{
                        title.append(plugin.unAva);
                    }
                }else {
                    items.addVariable("%name%", cosmetic.getName()).addVariable("%available%", playerData.getCosmeticById(cosmetic.getId()) != null ? MagicCosmetics.getInstance().getMessages().getString("available") : MagicCosmetics.getInstance().getMessages().getString("unavailable")).addVariable("%type%", cosmetic.getCosmeticType());
                    if (playerData.getCosmeticById(cosmetic.getId()) != null) {
                        title.append(playerData.getEquip(cosmetic.getId()) != null ? plugin.equip : plugin.ava);
                    } else {
                        title.append(plugin.unAva);
                    }
                }
                title.append(getPanel(slot));
                if(playerData.getSpray() != null){
                    if(playerData.getSpray().getId().equalsIgnoreCase(cosmetic.getId())){
                        slotMenu = new SlotMenu(slot, items, Collections.singletonList("magiccos unset " + cosmetic.getId()), ActionType.PLAYER_COMMAND);
                    }else{
                        if(cosmetic.isColored()){
                            slotMenu = new SlotMenu(slot, items,"colored|color1|"+cosmetic.getId(), ActionType.OPEN_MENU);
                        }else{
                            slotMenu = new SlotMenu(slot, items, cosmetic, ActionType.PREVIEW_ITEM);
                        }
                    }
                }else{
                    if(cosmetic.isColored()){
                        slotMenu = new SlotMenu(slot, items,"colored|color1|"+cosmetic.getId(), ActionType.OPEN_MENU);
                    }else{
                        slotMenu = new SlotMenu(slot, items, cosmetic, ActionType.PREVIEW_ITEM);
                    }
                }
                slotMenu.setSound(Sound.getSound("on_click_cosmetic"));
                slotMenu.setTempCosmetic(cosmetic);
                Token token = Token.getTokenByCosmetic(cosmetic.getId());
                slotMenu.setExchangeable(token != null && token.isExchangeable());
                getContentMenu().addSlotMenu(slotMenu);
                setItemInPaginatedMenu(slotMenu, getPage(), index, "_spray");
            }
        }
        if(!getNextSlot().isEmpty()){
            SlotMenu s;
            for(int slot : getNextSlot()){
                if(((index + 1) >= cosmetics.size())){
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
            setItemInPaginatedMenu(slotMenu, -1, -1, "_spray");
        }
        MagicCosmetics.getInstance().getVersion().updateTitle(playerData.getOfflinePlayer().getPlayer(), title.toString());
    }

}
