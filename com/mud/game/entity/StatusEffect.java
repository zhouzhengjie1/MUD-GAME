package com.mud.game.entity;

import java.io.Serializable;

public class StatusEffect implements Serializable {
    private static final long serialVersionUID = 1L;
    private BattleStatus status;
    private int remainingTurns;
    private int intensity; // 效果强度
    
    public StatusEffect(BattleStatus status, int intensity) {
        this.status = status;
        this.intensity = intensity;
        this.remainingTurns = status.getMaxTurns();
    }
    
    public StatusEffect(BattleStatus status, int intensity, int duration) {
        this.status = status;
        this.intensity = intensity;
        this.remainingTurns = duration;
    }
    
    // 状态效果生效
    public void applyEffect(BattleEntity target) {
        switch (status) {
            case POISON:
                int poisonDamage = intensity * 2;
                target.setHp(target.getHp() - poisonDamage);
                System.out.println("☠️ " + target.getName() + "受到" + poisonDamage + "点毒伤！");
                break;
                
            case BLEED:
                int bleedDamage = intensity * 3;
                target.setHp(target.getHp() - bleedDamage);
                System.out.println("🩸 " + target.getName() + "流血造成" + bleedDamage + "点伤害！");
                break;
                
            case BURN:
                int burnDamage = intensity * 2;
                target.setHp(target.getHp() - burnDamage);
                System.out.println("🔥 " + target.getName() + "燃烧造成" + burnDamage + "点伤害！");
                break;
                
            case REGENERATION:
                int healAmount = intensity * 3;
                int newHp = Math.min(target.getHp() + healAmount, target.getMaxHp());
                target.setHp(newHp);
                System.out.println("💚 " + target.getName() + "回复了" + healAmount + "点生命！");
                break;
                
            case ATTACK_BOOST:
                target.setTempAtk(target.getTempAtk() + intensity * 2);
                break;
                
            case DEFENSE_BOOST:
                target.setTempDef(target.getTempDef() + intensity * 2);
                break;
                
            case WEAK:
                target.setTempAtk(Math.max(0, target.getTempAtk() - intensity));
                break;
                
            case STUN:
                // 眩晕效果在行动时处理
                System.out.println("😵 " + target.getName() + "处于眩晕状态，无法行动！");
                break;
        }
    }
    
    // 回合结束
    public void endTurn() {
        remainingTurns--;
    }
    
    // 是否还有效果
    public boolean isActive() {
        return remainingTurns > 0;
    }
    
    // 获取状态描述
    public String getDescription() {
        return status.getIcon() + " " + status.getName() + " (" + remainingTurns + "回合)";
    }
    
    // Getter方法
    public BattleStatus getStatus() {
        return status;
    }
    
    public int getRemainingTurns() {
        return remainingTurns;
    }
    
    public int getIntensity() {
        return intensity;
    }
}