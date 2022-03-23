package com.francobm.magicosmetics.cache.inventories;

import com.francobm.magicosmetics.cache.PlayerCache;

import java.util.ArrayList;
import java.util.List;

public abstract class PaginatedMenu extends Menu {

    protected int page = 0;
    //9 * size
    protected int maxItemsPerPage;
    protected int startSlot;
    protected int endSlot;

    protected int pagesSlot;

    protected int backSlot;
    protected int nextSlot;
    //9 * size
    //slots no available
    protected List<Integer> slotsUnavailable;
    //slots no available
    protected int index = 0;

    public PaginatedMenu(String id, ContentMenu contentMenu) {
        super(id, contentMenu);
        this.startSlot = 0;
        this.endSlot = 0;
        this.pagesSlot = 0;
        this.backSlot = 0;
        this.nextSlot = 0;
        this.maxItemsPerPage = 0;
        this.slotsUnavailable = new ArrayList<>();
    }

    public PaginatedMenu(PlayerCache playerCache, Menu menu) {
        super(playerCache, menu);
        PaginatedMenu paginatedMenu = (PaginatedMenu) menu;
        this.startSlot = paginatedMenu.getStartSlot();
        this.endSlot = paginatedMenu.getEndSlot();
        this.pagesSlot = paginatedMenu.getPagesSlot();
        this.backSlot = paginatedMenu.getBackSlot();
        this.nextSlot = paginatedMenu.getNextSlot();
        this.maxItemsPerPage = paginatedMenu.getMaxItemsPerPage();
        this.slotsUnavailable = paginatedMenu.getSlotsUnavailable();
    }

    public PaginatedMenu(String id, ContentMenu contentMenu, int startSlot, int endSlot, int backSlot, int nextSlot, int pagesSlot, List<Integer> slotsUnavailable){
        super(id, contentMenu);
        this.startSlot = startSlot;
        this.endSlot = endSlot;
        this.pagesSlot = pagesSlot;
        this.backSlot = backSlot;
        this.nextSlot = nextSlot;
        this.maxItemsPerPage = ((endSlot - startSlot) + 1);
        this.slotsUnavailable = slotsUnavailable;
    }

    public List<Integer> getSlotsUnavailable() {
        return slotsUnavailable;
    }

    public void setSlotsUnavailable(List<Integer> slotsUnavailable) {
        this.slotsUnavailable = slotsUnavailable;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getMaxItemsPerPage() {
        return maxItemsPerPage;
    }

    public int getStartSlot() {
        return startSlot;
    }

    public int getEndSlot() {
        return endSlot;
    }

    public int getBackSlot() {
        return backSlot;
    }

    public int getNextSlot() {
        return nextSlot;
    }

    public int getPagesSlot() {
        return pagesSlot;
    }

    @Override
    public String toString() {
        return "PaginatedMenu{" +
                "id='" + id + '\'' +
                ", playerCache=" + playerCache +
                ", contentMenu=" + contentMenu +
                ", page=" + page +
                ", maxItemsPerPage=" + maxItemsPerPage +
                ", startSlot=" + startSlot +
                ", endSlot=" + endSlot +
                ", pagesSlot=" + pagesSlot +
                ", backSlot=" + backSlot +
                ", nextSlot=" + nextSlot +
                ", slotsUnavailable=" + slotsUnavailable +
                ", index=" + index +
                '}';
    }
}
