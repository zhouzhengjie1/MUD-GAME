package com.mud.game.entity;

import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 可强化装备类
 * 支持装备强化、等级提升、属性成长等功能
 */
public class Equipment extends Item implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int level;              // 装备等级
    private int maxLevel;           // 最高可强化等级
    private int baseEffect;         // 基础效果值
    private int upgradeCost;        // 升级所需金币
    private double successRate;     // 强化成功率
    private int durability;          // 耐久度
    private int maxDurability;      // 最大耐久度
    private EquipmentGrade grade;    // 装备品质
    private boolean isEquipped;    // 是否已装备
    
    public Equipment(String name, String description, int effect, ItemType type, 
                     EquipmentGrade grade, int maxLevel) {
        super(name, description, effect, type);
        this.level = 1;
        this.maxLevel = maxLevel;
        this.baseEffect = effect;
        this.grade = grade;
        this.maxDurability = grade.getBaseDurability();
        this.durability = maxDurability;
        this.isEquipped = false;
        
        // 根据品质和等级计算升级成本
        updateUpgradeCost();
        updateSuccessRate();
        updateEffect();
    }
    
    /**
     * 强化装备
     * @param player 执行强化的玩家
     * @return 强化结果
     */
    public UpgradeResult upgrade(Player player) {
        // 检查等级上限
        if (level >= maxLevel) {
            return new UpgradeResult(false, "❌ 装备已达到最高等级！");
        }
        
        // 检查耐久度
        if (durability <= 0) {
            return new UpgradeResult(false, "❌ 装备耐久度不足，请先修复！");
        }
        
        // 计算成功率
        double successRate = calculateSuccessRate();
        boolean success = java.util.concurrent.ThreadLocalRandom.current().nextDouble() < successRate;
        
        if (success) {
            // 强化成功
            level++;
            durability--;
            updateStats();
            
            return new UpgradeResult(true, 
                String.format("🎉 强化成功！%s 升级到 Lv.%d\n效果：%s\n耐久：%d/%d", 
                    getName(), level, getEffectDescription(),
                    durability, maxDurability));
        } else {
            // 强化失败
            durability = Math.max(0, durability - 2);
            
            return new UpgradeResult(false, 
                String.format("💔 强化失败！%s 强化失败\n耐久：%d/%d\n成功率：%.1f%%", 
                    getName(), durability, maxDurability,
                    successRate * 100));
        }
    }
    
    /**
     * 修复装备
     * @param player 执行修复的玩家
     * @return 修复结果
     */
    public RepairResult repair(Player player) {
        if (durability >= maxDurability) {
            return new RepairResult(false, "❌ 装备耐久度已满，无需修复！");
        }

        durability = maxDurability;

        return new RepairResult(true, 
            String.format("✅ 修复成功！%s 耐久度已恢复至 %d/%d", 
                         getName(), durability, maxDurability));
    }
    
    /**
     * 更新属性
     */
    private void updateStats() {
        updateEffect();
        updateUpgradeCost();
        updateSuccessRate();
    }
    
    /**
     * 更新效果
     */
    private void updateEffect() {
        double newEffect = (baseEffect * grade.getEffectMultiplier() * (1 + (level - 1) * 0.1));
        setEffect(newEffect);
    }
    
    /**
     * 更新升级费用
     */
    private void updateUpgradeCost() {
        upgradeCost = (int)(grade.getBaseUpgradeCost() * Math.pow(1.5, level - 1));
    }
    
    /**
     * 更新成功率
     */
    private void updateSuccessRate() {
        successRate = Math.max(0.1, grade.getBaseSuccessRate() - (level - 1) * 0.05);
    }
    
    /**
     * 计算成功率
     */
    private double calculateSuccessRate() {
        return Math.max(0.1, grade.getBaseSuccessRate() - (level - 1) * 0.05);
    }
    
    /**
     * 获取修复费用
     */
    public int getRepairCost() {
        return (maxDurability - durability) * grade.getRepairCostPerPoint();
    }
    
    /**
     * 获取详细信息
     */
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append(" [Lv.").append(level).append(" ").append(grade.getDisplayName()).append("] (")
          .append(grade.getIcon()).append(")\n");
        sb.append("品质：").append(grade.getIcon()).append(" ").append(grade.getDisplayName()).append("\n");
        sb.append("耐久：").append(durability).append("/").append(maxDurability).append(" | 升级费用：")
          .append(upgradeCost).append(" | 成功率：").append(successRate * 100).append("%\n");
        sb.append("修复费用：").append(getRepairCost()).append(" | 类型：").append(getType().name()).append("\n");
        sb.append("效果：").append(getEffectDescription());
        return sb.toString();
    }
    
    /**
     * 获取效果描述
     */
    public String getEffectDescription() {
        switch (getType()) {
            case WEAPON:
                return "攻击力 +" + getEffect();
            case ARMOR:
                return "防御力 +" + getEffect();
            default:
                return "效果 +" + getEffect();
        }
    }
    
    // Getters and Setters
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    
    public int getMaxLevel() { return maxLevel; }
    public void setMaxLevel(int maxLevel) { this.maxLevel = maxLevel; }
    
    public int getBaseEffect() { return baseEffect; }
    public void setBaseEffect(int baseEffect) { this.baseEffect = baseEffect; }
    
    public int getUpgradeCost() { return upgradeCost; }
    public void setUpgradeCost(int upgradeCost) { this.upgradeCost = upgradeCost; }
    
    public double getSuccessRate() { return successRate; }
    public void setSuccessRate(double successRate) { this.successRate = successRate; }
    
    public int getDurability() { return durability; }
    public void setDurability(int durability) { this.durability = durability; }
    
    public int getMaxDurability() { return maxDurability; }
    public void setMaxDurability(int maxDurability) { this.maxDurability = maxDurability; }
    
    public EquipmentGrade getGrade() { return grade; }
    public void setGrade(EquipmentGrade grade) { this.grade = grade; }
    
    public boolean isEquipped() { return isEquipped; }
    public void setEquipped(boolean equipped) { isEquipped = equipped; }
    
    /**
     * 获取显示名称
     */
    @Override
    public String toString() {
        return String.format("%s [Lv.%d %s] (%s)", getName(), level, grade.getDisplayName(), getEffectDescription());
    }
}