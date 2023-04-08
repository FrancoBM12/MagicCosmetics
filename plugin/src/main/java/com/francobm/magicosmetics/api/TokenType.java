package com.francobm.magicosmetics.api;

public enum TokenType {
    HAT(CosmeticType.HAT),
    BAG(CosmeticType.BAG),
    WALKING_STICK(CosmeticType.WALKING_STICK),
    BALLOON(CosmeticType.BALLOON),
    SPRAY(CosmeticType.SPRAY),
    ALL(null);

    private final CosmeticType cosmeticType;
    TokenType(CosmeticType cosmeticType) {
        this.cosmeticType = cosmeticType;
    }

    public CosmeticType getCosmeticType() {
        return cosmeticType;
    }
}
