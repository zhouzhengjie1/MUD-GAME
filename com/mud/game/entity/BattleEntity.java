package com.mud.game.entity;

import java.util.List;

public interface BattleEntity {
    String getName();
    int getHp();
    void setHp(int hp);
    int getMaxHp();
    void setMaxHp(int maxHp);
    int getAtk();
    void setAtk(int atk);
    int getDef();
    void setDef(int def);
    
    // 临时属性
    int getTempAtk();
    void setTempAtk(int tempAtk);
    int getTempDef();
    void setTempDef(int tempDef);
    double getDodgeRate();
    void setDodgeRate(double dodgeRate);
    double getCritRate();
    void setCritRate(double critRate);
    double getCritDamage();
    void setCritDamage(double critDamage);
    String getElement();
    void setElement(String element);
    
    // 状态效果
    List<StatusEffect> getStatusEffects();
    void addStatusEffect(StatusEffect effect);
    void removeStatusEffect(StatusEffect effect);
    void clearStatusEffects();
    boolean hasStatus(BattleStatus status);
    
    // 战斗方法
    void takeDamage(int damage);
    boolean isAlive();
    void setAlive(boolean alive);
}