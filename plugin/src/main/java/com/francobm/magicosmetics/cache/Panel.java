package com.francobm.magicosmetics.cache;

public class Panel {
    private final String id;
    private final String character;

    public Panel(String id, String character) {
        this.id = id;
        this.character = character;
    }

    public String getId() {
        return id;
    }

    public String getCharacter() {
        return character;
    }
}
