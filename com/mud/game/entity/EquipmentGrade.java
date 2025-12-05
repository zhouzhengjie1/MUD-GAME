package com.mud.game.entity;

import com.mud.game.system.RandomUtil;

/**
 * 装备品质枚举
 * 定义装备的品质等级和相应属性
 */
public enum EquipmentGrade {
    COMMON("普通", "⚪", 1.0, 100, 0.9, 50, 2),
    UNCOMMON("优秀", "🟢", 1.2, 150, 0.85, 100, 3),
    RARE("稀有", "🔵", 1.5, 200, 0.8, 200, 5),
    EPIC("史诗", "🟣", 2.0, 300, 0.75, 500, 8),
    LEGENDARY("传说", "🟠", 2.5, 500, 0.7, 1000, 10),
    MYTHIC("神话", "🔴", 3.0, 800, 0.65, 2000, 15);
    
    private final String displayName;     // 显示名称
    private final String icon;           // 图标
    private final double effectMultiplier; // 效果倍率
    private final int baseDurability;    // 基础耐久度
    private final double baseSuccessRate; // 基础强化成功率
    private final int baseUpgradeCost;   // 基础升级费用
    private final int repairCostPerPoint; // 每点耐久修复费用
    
    EquipmentGrade(String displayName, String icon, double effectMultiplier, 
                   int baseDurability, double baseSuccessRate, int baseUpgradeCost,
                   int repairCostPerPoint) {
        this.displayName = displayName;
        this.icon = icon;
        this.effectMultiplier = effectMultiplier;
        this.baseDurability = baseDurability;
        this.baseSuccessRate = baseSuccessRate;
        this.baseUpgradeCost = baseUpgradeCost;
        this.repairCostPerPoint = repairCostPerPoint;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public double getEffectMultiplier() {
        return effectMultiplier;
    }
    
    public int getBaseDurability() {
        return baseDurability;
    }
    
    public double getBaseSuccessRate() {
        return baseSuccessRate;
    }
    
    public int getBaseUpgradeCost() {
        return baseUpgradeCost;
    }
    
    public int getRepairCostPerPoint() {
        return repairCostPerPoint;
    }
    
    /**
     * 获取随机品质（按概率分布）
     */
    public static EquipmentGrade getRandomGrade() {
        double random = RandomUtil.nextDouble();
        if (random < 0.5) return COMMON;      // 50%
        if (random < 0.75) return UNCOMMON;   // 25%
        if (random < 0.9) return RARE;        // 15%
        if (random < 0.97) return EPIC;       // 7%
        if (random < 0.99) return LEGENDARY;  // 2%
        return MYTHIC;                        // 1%
    }
    
    /**
     * 根据品质等级获取品质
     */
    public static EquipmentGrade getGradeByLevel(int level) {
        switch (level) {
            case 1: return UNCOMMON;
            case 2: return RARE;
            case 3: return EPIC;
            case 4: return LEGENDARY;
            case 5: return MYTHIC;
            default: return COMMON;
        }
    }
    
    @Override
    public String toString() {
        return icon + " " + displayName;
    }
}