package com.francobm.magicosmetics.api;

public enum SprayKeys {
    SHIFT_F,
    SHIFT_E,
    SHIFT_Q,
    SHIFT_LC,
    SHIFT_RC,
    SHIFT_JUMP,
    API;

    public boolean isKey(final SprayKeys key) {
        return this == key;
    }
}
