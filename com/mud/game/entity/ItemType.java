package com.mud.game.entity;

public enum ItemType {
    WEAPON("武器"),
    MEDICINE("药品"),
    CLUE("线索"),
    BUFF_POTION("增益药剂"),
    DEBUFF_POTION("减益药剂"),
    POISON("毒药"),
    CONTROL_ITEM("控制道具"),
    ANTIDOTE("解毒剂"),
    SPECIAL("特殊道具"),
    ARMOR("护甲"),
    OTHER("其他");
    
    private final String chineseName;
    
    ItemType(String chineseName) {
        this.chineseName = chineseName;
    }
    
    public String getChineseName() {
        return chineseName;
    }
    
    @Override
    public String toString() {
        return chineseName;
    }
}