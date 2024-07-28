package com.francobm.magicosmetics.cache.cosmetics.store;

import com.francobm.magicosmetics.api.Cosmetic;

import java.util.Map;

public interface ICosmeticStore {

    Map<String, Cosmetic> getCosmetics();

    boolean hasPermission();
}
