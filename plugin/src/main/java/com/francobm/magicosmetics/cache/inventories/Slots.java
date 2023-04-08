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

    public String isSlot(int slot){
        if(slot >= 0 && slot < 9) {
            if(!isNum1()) {
                setNum1(true);
                if(MagicCosmetics.getInstance().getMessages().contains("edge.row-1")){
                    String space = MagicCosmetics.getInstance().getMessages().getString("edge.row-1");
                    if(MagicCosmetics.getInstance().isItemsAdder()){
                        space = MagicCosmetics.getInstance().getItemsAdder().replaceFontImages(space);
                    }
                    if(MagicCosmetics.getInstance().isOraxen()){
                        space = MagicCosmetics.getInstance().getOraxen().replaceFontImages(space);
                    }
                    return space;
                }
                return "";
            }
        }
        if(slot >= 9 && slot < 18) {
            if(!isNum2()) {
                setNum2(true);
                if(MagicCosmetics.getInstance().getMessages().contains("edge.row-2")){
                    String space = MagicCosmetics.getInstance().getMessages().getString("edge.row-2");
                    if(MagicCosmetics.getInstance().isItemsAdder()){
                        space = MagicCosmetics.getInstance().getItemsAdder().replaceFontImages(space);
                    }
                    if(MagicCosmetics.getInstance().isOraxen()){
                        space = MagicCosmetics.getInstance().getOraxen().replaceFontImages(space);
                    }
                    return space;
                }
                return "";
            }
        }
        if(slot >= 18 && slot < 27) {
            if(!isNum3()) {
                setNum3(true);
                if(MagicCosmetics.getInstance().getMessages().contains("edge.row-3")){
                    String space = MagicCosmetics.getInstance().getMessages().getString("edge.row-3");
                    if(MagicCosmetics.getInstance().isItemsAdder()){
                        space = MagicCosmetics.getInstance().getItemsAdder().replaceFontImages(space);
                    }
                    if(MagicCosmetics.getInstance().isOraxen()){
                        space = MagicCosmetics.getInstance().getOraxen().replaceFontImages(space);
                    }
                    return space;
                }
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
                if(MagicCosmetics.getInstance().getMessages().contains("edge.row-4")){
                    String space = MagicCosmetics.getInstance().getMessages().getString("edge.row-4");
                    if(MagicCosmetics.getInstance().isItemsAdder()){
                        space = MagicCosmetics.getInstance().getItemsAdder().replaceFontImages(space);
                    }
                    if(MagicCosmetics.getInstance().isOraxen()){
                        space = MagicCosmetics.getInstance().getOraxen().replaceFontImages(space);
                    }
                    return space;
                }
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
                if(MagicCosmetics.getInstance().getMessages().contains("edge.row-5")){
                    String space = MagicCosmetics.getInstance().getMessages().getString("edge.row-5");
                    if(MagicCosmetics.getInstance().isItemsAdder()){
                        space = MagicCosmetics.getInstance().getItemsAdder().replaceFontImages(space);
                    }
                    if(MagicCosmetics.getInstance().isOraxen()){
                        space = MagicCosmetics.getInstance().getOraxen().replaceFontImages(space);
                    }
                    return space;
                }
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
                if(MagicCosmetics.getInstance().getMessages().contains("edge.row-6")){
                    String space = MagicCosmetics.getInstance().getMessages().getString("edge.row-6");
                    if(MagicCosmetics.getInstance().isItemsAdder()){
                        space = MagicCosmetics.getInstance().getItemsAdder().replaceFontImages(space);
                    }
                    if(MagicCosmetics.getInstance().isOraxen()){
                        space = MagicCosmetics.getInstance().getOraxen().replaceFontImages(space);
                    }
                    return space;
                }
                return "";
            }
        }
        return "";
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
