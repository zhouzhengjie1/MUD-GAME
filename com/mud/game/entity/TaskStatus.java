package com.mud.game.entity;

public enum TaskStatus {
    NOT_ACCEPTED("未接取"),
    IN_PROGRESS("进行中"),
    COMPLETED("已完成");
    
    private final String chineseName;
    
    TaskStatus(String chineseName) {
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