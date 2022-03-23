package com.francobm.magicosmetics.cache.inventories;

import com.francobm.magicosmetics.MagicCosmetics;

public class Slots {
    private int min;
    private int max;
    private boolean num1;
    private boolean num2;
    private boolean num3;
    private boolean num4;
    private boolean num5;
    private boolean num6;

    public Slots(){
        this.min = 0;
        this.max = 0;
        this.num1 = false;
        this.num2 = false;
        this.num3 = false;
        this.num4 = false;
        this.num5 = false;
        this.num6 = false;
    }

    public void resetSlots(){
        this.min = 0;
        this.max = 0;
        this.num1 = false;
        this.num2 = false;
        this.num3 = false;
        this.num4 = false;
        this.num5 = false;
        this.num6 = false;
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    public String getSlot(int slot){
        if(slot >= 0 && slot < 9) {
            return "";
        }
        if(slot >= 9 && slot < 18) {
            return "";
        }
        if(slot >= 18 && slot < 27) {
            return "쌥";
        }
        if(slot >= 27 && slot < 36) {
            return "쌦";
        }
        if(slot >= 36 && slot < 45) {
            return "쌧";
        }
        if(slot >= 45 && slot < 54) {
            return "";
        }
        return "-1";
    }

    public String isSlot(int slot){
        if(slot >= 0 && slot < 9) {
            if(!isNum1()) {
                setNum1(true);
                return "";
            }
        }
        if(slot >= 9 && slot < 18) {
            if(!isNum2()) {
                setNum2(true);
                return "";
            }
        }
        if(slot >= 18 && slot < 27) {
            if(!isNum3()) {
                setNum3(true);
                String space = MagicCosmetics.getInstance().getMessages().getString("edge.space-1");
                if(MagicCosmetics.getInstance().isItemsAdder()){
                    space = MagicCosmetics.getInstance().getItemsAdder().replaceFontImages(space);
                }
                if(MagicCosmetics.getInstance().isOraxen()){
                    space = MagicCosmetics.getInstance().getOraxen().replaceFontImages(space);
                }
                return space;
            }
        }
        if(slot >= 27 && slot < 36) {
            if(!isNum4()) {
                setNum4(true);
                String space = MagicCosmetics.getInstance().getMessages().getString("edge.space-2");
                if(MagicCosmetics.getInstance().isItemsAdder()){
                    space = MagicCosmetics.getInstance().getItemsAdder().replaceFontImages(space);
                }
                if(MagicCosmetics.getInstance().isOraxen()){
                    space = MagicCosmetics.getInstance().getOraxen().replaceFontImages(space);
                }
                return space;
            }
        }
        if(slot >= 36 && slot < 45) {
            if(!isNum5()) {
                setNum5(true);
                String space = MagicCosmetics.getInstance().getMessages().getString("edge.space-3");
                if(MagicCosmetics.getInstance().isItemsAdder()){
                    space = MagicCosmetics.getInstance().getItemsAdder().replaceFontImages(space);
                }
                if(MagicCosmetics.getInstance().isOraxen()){
                    space = MagicCosmetics.getInstance().getOraxen().replaceFontImages(space);
                }
                return space;
            }
        }
        if(slot >= 45 && slot < 54) {
            if(!isNum6()) {
                setNum6(true);
                return "";
            }
        }
        return "";
    }

    public String isSecondaryColored(String primary, int slot){
        if(primary.startsWith("\uF80B\uF80B\uF801")){
            switch (slot){
                case 37:
                    return "\uF80A\uF809\uF807\uF804\uF801쌍";
                case 38:
                    return "\uF806\uF809\uF809\uF801쌍";
                case 39:
                    return "\uF806\uF808\uF807쌍";
                case 40:
                    return "\uF804\uF804\uF804쌍";
                case 41:
                    return "\uF804\uF804\uF804     \uF801\uF804쌍";
                case 42:
                    return "\uF804\uF804\uF804          \uF801\uF802\uF804쌍";
                case 43:
                    return "\uF804\uF804\uF804              \uF801\uF804쌍";
            }
        }
        if(primary.startsWith("\uF80A\uF80A\uF80A\uF807\uF807\uF801")){
            switch (slot){
                case 37:
                    return "\uF80A\uF809\uF807\uF804\uF809\uF804\uF804\uF804쌍";
                case 38:
                    return "\uF80A\uF809\uF807\uF804\uF801쌍";
                case 39:
                    return "\uF806\uF809\uF809\uF801쌍";
                case 40:
                    return "\uF806\uF808\uF807쌍";
                case 41:
                    return "\uF804\uF804\uF804쌍";
                case 42:
                    return "\uF804\uF804\uF804     \uF801\uF804쌍";
                case 43:
                    return "\uF804\uF804\uF804          \uF801\uF802\uF804쌍";
            }
        }
        if(primary.startsWith("\uF80A\uF80A\uF807\uF807\uF807\uF807\uF801")){
            switch (slot){
                case 37:
                    return "\uF80A\uF809\uF807\uF809\uF809\uF806쌍";
                case 38:
                    return "\uF80A\uF809\uF807\uF809\uF804\uF804\uF804\uF804쌍";
                case 39:
                    return "\uF80A\uF809\uF807\uF804\uF801쌍";
                case 40:
                    return "\uF806\uF809\uF809\uF801쌍";
                case 41:
                    return "\uF806\uF808\uF807쌍";
                case 42:
                    return "\uF804\uF804\uF804쌍";
                case 43:
                    return "\uF804\uF804\uF804     \uF801\uF804쌍";
            }
        }
        return "null";
    }

    public String isPrimaryColored(int slot){
        switch (slot){
            case 3:
                return "\uF80B\uF80B\uF801쌉";
            case 4:
                return "\uF80A\uF80A\uF80A\uF807\uF807\uF801쌉";
            case 5:
                return "\uF80A\uF80A\uF807\uF807\uF807\uF807\uF801쌉";
            case 12:
                return "\uF80B\uF80B\uF801쌊";
            case 13:
                return "\uF80A\uF80A\uF80A\uF807\uF807\uF801쌊";
            case 14:
                return "\uF80A\uF80A\uF807\uF807\uF807\uF807\uF801쌊";
            case 21:
                return "\uF80B\uF80B\uF801쌋";
            case 22:
                return "\uF80A\uF80A\uF80A\uF807\uF807\uF801쌋";
            case 23:
                return "\uF80A\uF80A\uF807\uF807\uF807\uF807\uF801쌋";
        }
        return "null";
    }

    public boolean isNum1() {
        return num1;
    }

    public void setNum1(boolean num1) {
        this.num1 = num1;
    }

    public boolean isNum2() {
        return num2;
    }

    public void setNum2(boolean num2) {
        this.num2 = num2;
    }

    public boolean isNum3() {
        return num3;
    }

    public void setNum3(boolean num3) {
        this.num3 = num3;
    }

    public boolean isNum4() {
        return num4;
    }

    public void setNum4(boolean num4) {
        this.num4 = num4;
    }

    public boolean isNum5() {
        return num5;
    }

    public void setNum5(boolean num5) {
        this.num5 = num5;
    }

    public boolean isNum6() {
        return num6;
    }

    public void setNum6(boolean num6) {
        this.num6 = num6;
    }
}
