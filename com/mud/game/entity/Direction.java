package com.mud.game.entity;

public enum Direction {
    EAST("东"),
    SOUTH("南"),
    WEST("西"),
    NORTH("北"),
    UP("上"),
    DOWN("下"),
    NORTHEAST("东北"),
    NORTHWEST("西北"),
    SOUTHEAST("东南"),
    SOUTHWEST("西南");
    
    private final String chineseName;
    
    Direction(String chineseName) {
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