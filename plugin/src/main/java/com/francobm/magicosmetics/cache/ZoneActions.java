package com.francobm.magicosmetics.cache;

import com.francobm.magicosmetics.listeners.ZoneListener;

public class ZoneActions {

    private boolean enabled;
    private final ZoneAction onEnter;
    private final ZoneAction onExit;

    private final ZoneListener zoneListener;

    public ZoneActions(ZoneAction onEnter, ZoneAction onExit) {
        this.onEnter = onEnter;
        this.onExit = onExit;
        this.enabled = false;
        this.zoneListener = new ZoneListener();
    }

    public ZoneAction getOnEnter() {
        return onEnter;
    }

    public ZoneAction getOnExit() {
        return onExit;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ZoneListener getZoneListener() {
        return zoneListener;
    }
}
