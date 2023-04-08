package com.francobm.magicosmetics.cache.inventories;

import com.francobm.magicosmetics.cache.PlayerData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class PaginatedMenu extends Menu {

    protected int page = 0;
    //9 * size
    protected List<Integer> maxItemsPerPage;
    protected int startSlot;
    protected int endSlot;

    protected int pagesSlot;

    protected Set<Integer> backSlot;
    protected Set<Integer> nextSlot;
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
        this.backSlot = new HashSet<>();
        this.nextSlot = new HashSet<>();
        this.maxItemsPerPage = new ArrayList<>();
        this.slotsUnavailable = new ArrayList<>();
    }

    public PaginatedMenu(PlayerData playerData, Menu menu) {
        super(playerData, menu);
        PaginatedMenu paginatedMenu = (PaginatedMenu) menu;
        this.startSlot = paginatedMenu.getStartSlot();
        this.endSlot = paginatedMenu.getEndSlot();
        this.pagesSlot = paginatedMenu.getPagesSlot();
        this.backSlot = paginatedMenu.getBackSlot();
        this.nextSlot = paginatedMenu.getNextSlot();
        this.maxItemsPerPage = paginatedMenu.getMaxItemsPerPageList();
        this.slotsUnavailable = paginatedMenu.getSlotsUnavailable();
    }

    public PaginatedMenu(String id, ContentMenu contentMenu, int startSlot, int endSlot, Set<Integer> backSlot, Set<Integer> nextSlot, int pagesSlot, List<Integer> slotsUnavailable){
        super(id, contentMenu);
        this.startSlot = startSlot;
        this.endSlot = endSlot;
        this.pagesSlot = pagesSlot;
        this.backSlot = backSlot;
        this.nextSlot = nextSlot;
        this.slotsUnavailable = slotsUnavailable;
        this.maxItemsPerPage = new ArrayList<>();
        for(int i = startSlot; i <= endSlot; i++) {
            if(slotsUnavailable.contains(i)) continue;
            maxItemsPerPage.add(i);
        }
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
        return maxItemsPerPage.size();
    }
    public List<Integer> getMaxItemsPerPageList() {
        return maxItemsPerPage;
    }

    public int getStartSlot() {
        return startSlot;
    }

    public int getEndSlot() {
        return endSlot;
    }

    public Set<Integer> getBackSlot() {
        return backSlot;
    }

    public Set<Integer> getNextSlot() {
        return nextSlot;
    }

    public int getPagesSlot() {
        return pagesSlot;
    }

    @Override
    public String toString() {
        return "PaginatedMenu{" +
                "id='" + id + '\'' +
                ", playerCache=" + playerData +
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
