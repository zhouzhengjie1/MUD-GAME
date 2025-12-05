package com.mud.game.entity;

/**
 * 装备强化结果
 */
public class UpgradeResult {
    private boolean success;
    private String message;
    
    public UpgradeResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getMessage() {
        return message;
    }
    
    @Override
    public String toString() {
        return (success ? "✅ " : "❌ ") + message;
    }
}