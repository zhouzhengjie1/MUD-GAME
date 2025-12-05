package com.mud.game.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * 游戏难度枚举类
 * 定义不同难度级别及对应的属性调整系数
 */
public enum GameDifficulty {
    // 简单难度：玩家属性不降低，NPC属性略微降低
    EASY("简单", 1.0, 1.0, 1.0, 0.8, 0.8, 0.8, 0.8, 1.5),
    
    // 普通难度：玩家属性轻微降低，NPC属性不变
    NORMAL("普通", 0.9, 0.9, 0.9, 1.0, 1.0, 1.0, 1.0, 1.0),
    
    // 困难难度：玩家属性明显降低，NPC属性明显提升
    HARD("困难", 0.7, 0.7, 0.8, 1.3, 1.3, 1.2, 1.2, 0.8),
    
    // 噩梦难度：玩家属性大幅降低，NPC属性大幅提升
    NIGHTMARE("噩梦", 0.5, 0.5, 0.6, 1.8, 1.8, 1.5, 1.5, 0.6);
    
    private final String name;           // 难度名称
    private final double playerHpFactor; // 玩家生命值系数
    private final double playerAtkFactor; // 玩家攻击力系数
    private final double playerDefFactor; // 玩家防御力系数
    private final double npcAtkFactor;   // NPC攻击力系数
    private final double npcHpFactor;    // NPC生命值系数
    private final double npcDefFactor;   // NPC防御力系数
    private final double npcDodgeCritFactor; // NPC闪避和暴击系数
    private final double rewardFactor;   // 奖励系数（经验、金币等）
    
    /**
     * 构造函数
     * @param name 难度名称
     * @param playerHpFactor 玩家生命值系数
     * @param playerAtkFactor 玩家攻击力系数
     * @param playerDefFactor 玩家防御力系数
     * @param npcAtkFactor NPC攻击力系数
     * @param npcHpFactor NPC生命值系数
     * @param npcDefFactor NPC防御力系数
     * @param npcDodgeCritFactor NPC闪避和暴击系数
     * @param rewardFactor 奖励系数
     */
    GameDifficulty(String name, double playerHpFactor, double playerAtkFactor, 
                  double playerDefFactor, double npcAtkFactor, double npcHpFactor,
                  double npcDefFactor, double npcDodgeCritFactor, double rewardFactor) {
        this.name = name;
        this.playerHpFactor = playerHpFactor;
        this.playerAtkFactor = playerAtkFactor;
        this.playerDefFactor = playerDefFactor;
        this.npcAtkFactor = npcAtkFactor;
        this.npcHpFactor = npcHpFactor;
        this.npcDefFactor = npcDefFactor;
        this.npcDodgeCritFactor = npcDodgeCritFactor;
        this.rewardFactor = rewardFactor;
    }
    
    /**
     * 根据难度获取玩家调整后的生命值
     * @param baseHp 基础生命值
     * @return 调整后的生命值
     */
    public int getAdjustedPlayerHp(int baseHp) {
        return (int) (baseHp * playerHpFactor);
    }
    
    /**
     * 根据难度获取玩家调整后的攻击力
     * @param baseAtk 基础攻击力
     * @return 调整后的攻击力
     */
    public int getAdjustedPlayerAtk(int baseAtk) {
        return (int) (baseAtk * playerAtkFactor);
    }
    
    /**
     * 根据难度获取玩家调整后的防御力
     * @param baseDef 基础防御力
     * @return 调整后的防御力
     */
    public int getAdjustedPlayerDef(int baseDef) {
        return (int) (baseDef * playerDefFactor);
    }
    
    // 兼容方法：根据难度获取玩家生命值
    public int getPlayerHpByDifficulty(int baseHp) {
        return getAdjustedPlayerHp(baseHp);
    }
    
    // 兼容方法：根据难度获取玩家攻击力
    public int getPlayerAtkByDifficulty(int baseAtk) {
        return getAdjustedPlayerAtk(baseAtk);
    }
    
    // 兼容方法：根据难度获取玩家防御力
    public int getPlayerDefByDifficulty(int baseDef) {
        return getAdjustedPlayerDef(baseDef);
    }
    
    /**
     * 获取NPC生命值系数
     * @return NPC生命值系数
     */
    public double getNpcHpFactor() {
        return npcHpFactor;
    }
    
    /**
     * 获取NPC攻击力系数
     * @return NPC攻击力系数
     */
    public double getNpcAtkFactor() {
        return npcAtkFactor;
    }
    
    /**
     * 获取NPC防御力系数
     * @return NPC防御力系数
     */
    public double getNpcDefFactor() {
        return npcDefFactor;
    }
    
    /**
     * 获取NPC闪避和暴击系数
     * @return NPC闪避和暴击系数
     */
    public double getNpcDodgeCritFactor() {
        return npcDodgeCritFactor;
    }
    
    /**
     * 获取奖励系数
     * @return 奖励系数
     */
    public double getRewardFactor() {
        return rewardFactor;
    }
    
    /**
     * 根据难度获取NPC调整后的防御力
     * @param baseDef 基础防御力
     * @return 调整后的防御力
     */
    public int getAdjustedNpcDef(int baseDef) {
        return (int) (baseDef * npcDefFactor);
    }
    
    /**
     * 根据难度获取NPC调整后的攻击力
     * @param baseAtk 基础攻击力
     * @return 调整后的攻击力
     */
    public int getAdjustedNpcAtk(int baseAtk) {
        return (int) (baseAtk * npcAtkFactor);
    }
    
    /**
     * 根据难度获取NPC调整后的生命值
     * @param baseHp 基础生命值
     * @return 调整后的生命值
     */
    public int getAdjustedNpcHp(int baseHp) {
        return (int) (baseHp * npcHpFactor);
    }
    
    /**
     * 获取难度名称
     * @return 难度名称
     */
    public String getName() {
        return name;
    }
    
    /**
     * 获取中文难度名称（兼容方法）
     * @return 中文难度名称
     */
    public String getChineseName() {
        return name;
    }
    
    /**
     * 获取难度描述
     * @return 难度描述
     */
    public String getDescription() {
        switch (this) {
            case EASY:
                return "适合新手玩家的难度，敌人较弱，资源丰富";
            case NORMAL:
                return "平衡的游戏体验，适合大多数玩家";
            case HARD:
                return "挑战性较高，敌人更强，资源更稀缺";
            case NIGHTMARE:
                return "极高难度，敌人非常强大，对玩家技术要求极高";
            default:
                return "未知难度";
        }
    }
    
    /**
     * 获取玩家属性调整描述
     * @return 玩家属性调整描述
     */
    public String getPlayerStatsDescription() {
        return "生命值×" + playerHpFactor + ", 攻击力×" + playerAtkFactor + ", 防御力×" + playerDefFactor;
    }
    
    /**
     * 获取NPC属性调整描述
     * @return NPC属性调整描述
     */
    public String getNpcStatsDescription() {
        return "生命值×" + npcHpFactor + ", 攻击力×" + npcAtkFactor + ", 防御力×" + npcDefFactor;
    }
    
    /**
     * 获取玩家生命值系数（兼容方法）
     * @return 玩家生命值系数
     */
    public double getPlayerHpMultiplier() {
        return playerHpFactor;
    }
    
    /**
     * 获取玩家攻击力系数（兼容方法）
     * @return 玩家攻击力系数
     */
    public double getPlayerAtkMultiplier() {
        return playerAtkFactor;
    }
    
    /**
     * 获取玩家防御力系数（兼容方法）
     * @return 玩家防御力系数
     */
    public double getPlayerDefMultiplier() {
        return playerDefFactor;
    }
    
    /**
     * 获取NPC生命值系数（兼容方法）
     * @return NPC生命值系数
     */
    public double getNpcHpMultiplier() {
        return npcHpFactor;
    }
    
    /**
     * 获取NPC攻击力系数（兼容方法）
     * @return NPC攻击力系数
     */
    public double getNpcAtkMultiplier() {
        return npcAtkFactor;
    }
    
    /**
     * 获取NPC防御力系数（兼容方法）
     * @return NPC防御力系数
     */
    public double getNpcDefMultiplier() {
        return npcDefFactor;
    }
    
    /**
     * 获取奖励系数（兼容方法）
     * @return 奖励系数
     */
    public double getRewardMultiplier() {
        return rewardFactor;
    }
    
    /**
     * 获取所有难度选项（兼容MudGame中的调用）
     * @return 难度选项映射
     */
    public static Map<String, GameDifficulty> getDifficultyOptions() {
        Map<String, GameDifficulty> options = new HashMap<>();
        options.put("1", GameDifficulty.EASY);
        options.put("2", GameDifficulty.NORMAL);
        options.put("3", GameDifficulty.HARD);
        options.put("4", GameDifficulty.NIGHTMARE);
        return options;
    }
    
    /**
     * 根据输入的数字获取对应的难度
     * @param choice 玩家选择的数字
     * @return 对应的难度枚举，如果无效则返回普通难度
     */
    public static GameDifficulty getDifficultyByChoice(int choice) {
        switch (choice) {
            case 1:
                return EASY;
            case 2:
                return NORMAL;
            case 3:
                return HARD;
            case 4:
                return NIGHTMARE;
            default:
                return NORMAL; // 默认普通难度
        }
    }
    
    /**
     * 显示所有难度选项
     */
    public static void showDifficultyOptions() {
        System.out.println("=== 选择游戏难度 ===");
        System.out.println("1. 简单 - 适合新手，敌人较弱");
        System.out.println("2. 普通 - 平衡的游戏体验");
        System.out.println("3. 困难 - 挑战性更高，敌人更强");
        System.out.println("4. 噩梦 - 极其困难，敌人非常强大");
        System.out.print("请输入选择 (1-4，默认2): ");
    }
}