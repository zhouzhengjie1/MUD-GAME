package com.mud.game.entity;

import java.io.Serializable;

public class TaskObjective implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String description;
    private int targetCount;
    private int currentCount;
    private String targetType; // "kill", "collect", "talk", "explore"
    private String targetName;
    
    public TaskObjective(String description, String targetType, String targetName, int targetCount) {
        this.description = description;
        this.targetType = targetType;
        this.targetName = targetName;
        this.targetCount = targetCount;
        this.currentCount = 0;
    }
    
    public boolean isCompleted() {
        return currentCount >= targetCount;
    }
    
    public void updateProgress(int amount) {
        currentCount += amount;
        if (currentCount > targetCount) {
            currentCount = targetCount;
        }
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getTargetCount() {
        return targetCount;
    }
    
    public void setTargetCount(int targetCount) {
        this.targetCount = targetCount;
    }
    
    public int getCurrentCount() {
        return currentCount;
    }
    
    public void setCurrentCount(int currentCount) {
        this.currentCount = currentCount;
    }
    
    public String getTargetType() {
        return targetType;
    }
    
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }
    
    public String getTargetName() {
        return targetName;
    }
    
    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }
    
    @Override
    public String toString() {
        return description + " (" + currentCount + "/" + targetCount + ")";
    }
}