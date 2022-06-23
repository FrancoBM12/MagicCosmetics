package com.francobm.magicosmetics.cache.inventories.menus;

import com.francobm.magicosmetics.MagicCosmetics;
import com.francobm.magicosmetics.cache.Cosmetic;
import com.francobm.magicosmetics.cache.PlayerCache;
import com.francobm.magicosmetics.cache.Sound;
import com.francobm.magicosmetics.cache.Token;
import com.francobm.magicosmetics.cache.inventories.ActionType;
import com.francobm.magicosmetics.cache.inventories.ContentMenu;
import com.francobm.magicosmetics.cache.inventories.Menu;
import com.francobm.magicosmetics.cache.inventories.SlotMenu;
import com.francobm.magicosmetics.cache.items.Items;
import com.francobm.magicosmetics.utils.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class TokenMenu extends Menu {

    private boolean drag;
    private ItemStack itemStack;

    public TokenMenu(String id, ContentMenu contentMenu, boolean drag) {
        super(id, contentMenu);
        this.drag = drag;
    }


    public TokenMenu(PlayerCache playerCache, Menu menu) {
        super(playerCache, menu);
        this.drag = ((TokenMenu)menu).isDrag();
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if(isDrag()){
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
                    Token token = Token.getTokenByItem(event.getCursor());
                    if(token == null){
                        event.setCancelled(true);
                        return;
                    }
                    itemStack = event.getCursor().clone();
                    Items items = new Items(token.getItemStack().clone());
                    items.addPlaceHolder(playerCache.getOfflinePlayer().getPlayer());
                    SlotMenu slotMenu = new SlotMenu(getContentMenu().getPreviewSlot(), items, "");
                    getContentMenu().addSlotMenu(slotMenu);

                    items = new Items(Cosmetic.getCloneCosmetic(token.getCosmetic()).getItemStack());
                    slotMenu = new SlotMenu(getContentMenu().getResultSlot(), items, token);
                    getContentMenu().addSlotMenu(slotMenu);
                    setItemInMenu(slotMenu);
                    return;
                }
                if(event.getCurrentItem() == null) return;
                itemStack = null;
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
                event.setCancelled(true);
                SlotMenu slotMenu = getContentMenu().getSlotMenuBySlot(slot);
                if(slotMenu == null) return;
                Token token = slotMenu.getToken();
                boolean redeem = Token.removeToken(player, itemStack);
                if(!redeem) return;
                if(itemStack.getAmount() > token.getItemStack().getAmount()){
                    ItemStack newItem = token.getItemStack().clone();
                    newItem.setAmount(itemStack.getAmount() - token.getItemStack().getAmount());
                    player.getInventory().addItem(newItem);
                }
                itemStack = null;
                getContentMenu().removeSlotMenu(getContentMenu().getPreviewSlot());
                getContentMenu().removeSlotMenu(getContentMenu().getResultSlot());
                event.getClickedInventory().setItem(getContentMenu().getPreviewSlot(), XMaterial.AIR.parseItem());
                event.getClickedInventory().setItem(getContentMenu().getResultSlot(), XMaterial.AIR.parseItem());
                MagicCosmetics.getInstance().getCosmeticsManager().changeCosmetic(player, token.getCosmetic());
                return;
            }
            event.setCancelled(true);
            return;
        }
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
        if(isDrag()) return;
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

    public void returnItem(){
        if(this.itemStack == null) return;
        Player player = playerCache.getOfflinePlayer().getPlayer();
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

    public void setDrag(boolean drag) {
        this.drag = drag;
    }

    public boolean isDrag() {
        return drag;
    }
}
