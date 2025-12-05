package com.mud.game.entity;

public enum BattleStatus {
    // 正面状态
    ATTACK_BOOST("攻击强化", "⚔️", true, 3),
    DEFENSE_BOOST("防御强化", "🛡️", true, 3),
    SPEED_BOOST("速度强化", "💨", true, 3),
    CRIT_BOOST("暴击强化", "💥", true, 3),
    REGENERATION("生命回复", "💚", true, 5),
    
    // 负面状态
    POISON("中毒", "☠️", false, 5),
    BLEED("流血", "🩸", false, 4),
    STUN("眩晕", "😵", false, 2),
    WEAK("虚弱", "😰", false, 3),
    SLOW("减速", "🐌", false, 3),
    BURN("燃烧", "🔥", false, 4),
    FREEZE("冰冻", "❄️", false, 2),
    CURSE("诅咒", "👻", false, 6),
    
    // 特殊状态
    INVINCIBLE("无敌", "✨", true, 1),
    BERSERK("狂暴", "😡", true, 3),
    STEALTH("隐身", "👤", true, 2);
    
    private final String name;
    private final String icon;
    private final boolean isPositive;
    private final int maxTurns;
    
    BattleStatus(String name, String icon, boolean isPositive, int maxTurns) {
        this.name = name;
        this.icon = icon;
        this.isPositive = isPositive;
        this.maxTurns = maxTurns;
    }
    
    public String getName() {
        return name;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public boolean isPositive() {
        return isPositive;
    }
    
    public int getMaxTurns() {
        return maxTurns;
    }
    
    @Override
    public String toString() {
        return icon + " " + name;
    }
}