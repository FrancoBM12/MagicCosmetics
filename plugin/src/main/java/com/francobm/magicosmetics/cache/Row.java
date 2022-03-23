package com.francobm.magicosmetics.cache;

public class Row {
    private final String row;
    private final String character;
    private final String selected;

    public Row(String row, String character, String selected) {
        this.row = row;
        this.character = character;
        this.selected = selected;
    }

    public String getRow() {
        return row;
    }

    public String getCharacter() {
        return character;
    }

    public String getSelected() {
        return selected;
    }
}
