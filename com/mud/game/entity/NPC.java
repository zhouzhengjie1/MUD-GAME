package com.mud.game.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.mud.game.system.RandomUtil;

public class NPC implements Serializable, BattleEntity {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private int hp;
    private int maxHp;
    private int atk;
    private int def;
    private int level;
    private String element; // 属性：火、水、土、风、雷、光、暗
    private double dodgeRate; // 闪避率(0-1)
    private double critRate; // 暴击率(0-1)
    private double critDamage; // 暴击伤害倍数
    private int resistance; // 抗性，减少负面效果持续时间
    private String[] dialogue;
    private boolean isHostile;
    private boolean isAlive;
    private GameDifficulty difficulty; // 游戏难度
    
    // 任务相关字段
    private List<String> availableTasks; // 可提供的任务
    private Map<String, String> taskDialogues; // 任务相关对话
    private String profession; // 职业/身份
    private String personality; // 性格特征
    private int relationship; // 与玩家的关系值 (-100 到 100)
    
    // BattleEntity接口字段
    private int tempAtk; // 临时攻击力
    private int tempDef; // 临时防御力
    private List<StatusEffect> statusEffects; // 状态效果列表
    private int expReward; // 经验奖励
    private int goldReward; // 金币奖励
    
    public NPC(String name, int hp, int atk, String[] dialogue, boolean isHostile) {
        this(name, hp, atk, dialogue, isHostile, "村民", "友善", GameDifficulty.NORMAL);
    }
    
    public NPC(String name, int hp, int atk, String[] dialogue, boolean isHostile, GameDifficulty difficulty) {
        this(name, hp, atk, dialogue, isHostile, "村民", "友善", difficulty);
    }
    
    public NPC(String name, int hp, int atk, String[] dialogue, boolean isHostile, String profession, String personality, GameDifficulty difficulty) {
        this.difficulty = difficulty;
        this.name = name;
        
        // 根据难度调整NPC属性（难度越高，NPC属性越强）
        int adjustedHp = (int)(hp * difficulty.getNpcHpFactor());
        this.hp = adjustedHp;
        this.maxHp = adjustedHp;
        
        int adjustedAtk = (int)(atk * difficulty.getNpcAtkFactor());
        this.atk = adjustedAtk;
        
        int adjustedDef = (int)(3 * difficulty.getNpcDefFactor());
        this.def = adjustedDef;
        
        this.level = 1;
        this.element = "无";
        
        // 根据难度调整闪避率和暴击率
        this.dodgeRate = 0.1 * difficulty.getNpcDodgeCritFactor();
        this.critRate = 0.15 * difficulty.getNpcDodgeCritFactor();
        this.critDamage = 1.5;
        
        // 根据难度调整抗性
        this.resistance = (int)(1 * difficulty.getNpcDefFactor());
        
        this.dialogue = dialogue;
        this.isHostile = isHostile;
        this.isAlive = true;
        this.profession = profession;
        this.personality = personality;
        this.relationship = 0;
        this.availableTasks = new ArrayList<>();
        this.taskDialogues = new HashMap<>();
        
        // 初始化BattleEntity字段
        this.tempAtk = 0;
        this.tempDef = 0;
        this.statusEffects = new ArrayList<>();
        
        // 根据难度调整奖励
        int baseExpReward = isHostile ? 15 : 5;
        int baseGoldReward = isHostile ? 10 : 3;
        this.expReward = (int)(baseExpReward * difficulty.getRewardFactor());
        this.goldReward = (int)(baseGoldReward * difficulty.getRewardFactor());
    }
    
    // 增强构造函数，支持更多属性
    public NPC(String name, int hp, int atk, int def, int level, String element, 
             double dodgeRate, double critRate, double critDamage, int resistance,
             String[] dialogue, boolean isHostile) {
        this(name, hp, atk, def, level, element, dodgeRate, critRate, critDamage, resistance, 
             dialogue, isHostile, GameDifficulty.NORMAL);
    }
    
    public NPC(String name, int hp, int atk, int def, int level, String element, 
             double dodgeRate, double critRate, double critDamage, int resistance,
             String[] dialogue, boolean isHostile, GameDifficulty difficulty) {
        this.difficulty = difficulty;
        this.name = name;
        
        // 根据难度调整NPC属性（难度越高，NPC属性越强）
        int adjustedHp = (int)(hp * difficulty.getNpcHpFactor());
        this.hp = adjustedHp;
        this.maxHp = adjustedHp;
        
        int adjustedAtk = (int)(atk * difficulty.getNpcAtkFactor());
        this.atk = adjustedAtk;
        
        int adjustedDef = (int)(def * difficulty.getNpcDefFactor());
        this.def = adjustedDef;
        
        this.level = level;
        this.element = element;
        
        // 根据难度调整闪避率和暴击率
        this.dodgeRate = dodgeRate * difficulty.getNpcDodgeCritFactor();
        this.critRate = critRate * difficulty.getNpcDodgeCritFactor();
        this.critDamage = critDamage;
        
        // 根据难度调整抗性
        this.resistance = (int)(resistance * difficulty.getNpcDefFactor());
        
        this.dialogue = dialogue;
        this.isHostile = isHostile;
        this.isAlive = true;
        
        // 初始化BattleEntity字段
        this.tempAtk = 0;
        this.tempDef = 0;
        this.statusEffects = new ArrayList<>();
        
        // 根据难度调整奖励
        this.expReward = (int)(10 * difficulty.getRewardFactor()); // 默认经验奖励
        this.goldReward = (int)(5 * difficulty.getRewardFactor()); // 默认金币奖励
    }
    
    public void talk(Player player) {
        if (!isAlive) {
            System.out.println(name + "已经死亡，无法对话。");
            return;
        }
        
        if (isHostile) {
            System.out.println(name + "是敌对NPC，无法对话！");
            System.out.println(name + "向你发起了攻击！");
            attack(player);
            return;
        }
        
        // 根据关系值和可用任务选择对话
        String message = getContextualDialogue();
        System.out.println(name + "说：" + message);
        
        // 显示可用任务
        // if (!availableTasks.isEmpty()) {
        //     System.out.println("\n【任务提示】");
        //     System.out.println(name + "似乎有任务要交给你...");
        //     System.out.println("输入 'task' 查看可用任务");
        // }
    }
    
    private String getContextualDialogue() {
        // 根据关系值选择不同的对话
        if (relationship >= 50) {
            return getFriendlyDialogue();
        } else if (relationship <= -20) {
            return getUnfriendlyDialogue();
        } else {
            return getNeutralDialogue();
        }
    }
    
    private String getFriendlyDialogue() {
        String[] friendlyDialogues = {
            "老朋友，见到你真高兴！",
            "有什么需要帮忙的吗？尽管说！",
            "你最近怎么样？我这边有些新消息。",
            "来，咱们聊聊最近发生的事情。"
        };
        return RandomUtil.randomElement(friendlyDialogues);
    }
    
    private String getUnfriendlyDialogue() {
        String[] unfriendlyDialogues = {
            "你来干什么？",
            "我不怎么想和你说话。",
            "哼，又是你。",
            "希望你不是来惹麻烦的。"
        };
        return RandomUtil.randomElement(unfriendlyDialogues);
    }
    
    private String getNeutralDialogue() {
        if (dialogue != null && dialogue.length > 0) {
            return RandomUtil.randomElement(dialogue);
        }
        return "沉默不语。";
    }
    
    public void attack(Player player) {
        if (!isAlive) {
            System.out.println(name + "已经死亡，无法攻击。");
            return;
        }
        
        // 闪避判定
        if (RandomUtil.nextDouble() < player.getDodgeRate()) {
            System.out.println("💨 " + player.getName() + "灵巧地闪避了" + name + "的攻击！");
            return;
        }
        
        // 暴击判定
        boolean isCrit = RandomUtil.nextDouble() < this.critRate;
        double damageMultiplier = isCrit ? this.critDamage : 1.0;
        
        // 基础伤害计算
        int baseDamage = Math.max(1, this.atk - player.getDef() / 2);
        int damage = (int)(baseDamage * damageMultiplier);
        
        // 属性克制计算
        double elementModifier = calculateElementAdvantage(this.element, player.getElement());
        damage = (int)(damage * elementModifier);
        
        player.setHp(player.getHp() - damage);
        
        // 显示攻击效果
        if (isCrit) {
            System.out.println("💥 " + name + "的暴击对你造成了" + damage + "点伤害！");
        } else {
            System.out.println(name + "对你造成了" + damage + "点伤害！");
        }
        
        if (elementModifier > 1.2) {
            System.out.println("🔥 属性克制！伤害大幅提升！");
        } else if (elementModifier < 0.8) {
            System.out.println("❄️ 属性被克制！伤害降低！");
        }
        
        if (player.getHp() <= 0) {
            System.out.println("你被" + name + "击败了！游戏结束！");
            System.exit(0);
        }
    }
    
    public void takeDamage(int damage, Player player) {
        if (!isAlive) {
            System.out.println(name + "已经死亡。");
            return;
        }
        
        this.hp -= damage;
        if (this.hp <= 0) {
            this.hp = 0;
            this.isAlive = false;
            System.out.println(name + "被击败了！");
        } else {
            System.out.println(name + "受到了" + damage + "点伤害，剩余生命值：" + this.hp);
        }
        
        // 更新Player的NPC血量映射
        if (player != null) {
            player.updateNpcHealth(this.name, this.hp);
        }
    }
    
    // 兼容旧版本的takeDamage方法
    public void takeDamage(int damage) {
        takeDamage(damage, null);
    }
    
    // 任务相关方法
    public void addTask(String taskId) {
        if (!availableTasks.contains(taskId)) {
            availableTasks.add(taskId);
        }
    }
    
    public void removeTask(String taskId) {
        availableTasks.remove(taskId);
    }
    
    public List<String> getAvailableTasks() {
        return new ArrayList<>(availableTasks);
    }
    
    public void addTaskDialogue(String taskId, String dialogue) {
        taskDialogues.put(taskId, dialogue);
    }
    
    public String getTaskDialogue(String taskId) {
        return taskDialogues.get(taskId);
    }
    
    public void modifyRelationship(int change) {
        relationship += change;
        relationship = Math.max(-100, Math.min(100, relationship)); // 限制在-100到100之间
        
        if (change > 0) {
            System.out.println(name + "对你的好感度提升了！");
        } else if (change < 0) {
            System.out.println(name + "对你的好感度下降了...");
        }
    }
    
    public int getRelationship() {
        return relationship;
    }
    
    public String getProfession() {
        return profession;
    }
    
    public String getPersonality() {
        return personality;
    }
    
    public String getRelationshipStatus() {
        if (relationship >= 80) return "亲密无间";
        if (relationship >= 50) return "友好";
        if (relationship >= 20) return "友善";
        if (relationship >= -20) return "中立";
        if (relationship >= -50) return "冷淡";
        return "敌对";
    }
    
    public void showNPCInfo() {
        System.out.println("\n=== NPC信息 ===");
        System.out.println("姓名：" + name);
        System.out.println("职业：" + profession);
        System.out.println("性格：" + personality);
        System.out.println("生命：" + hp + "/" + maxHp);
        System.out.println("关系：" + getRelationshipStatus() + " (" + relationship + ")");
        
        if (!availableTasks.isEmpty()) {
            System.out.println("可用任务：" + availableTasks.size() + " 个");
        }
        
        if (isHostile) {
            System.out.println("状态：敌对");
        } else {
            System.out.println("状态：友好");
        }
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getHp() {
        return hp;
    }
    
    public void setHp(int hp) {
        this.hp = hp;
    }
    
    public int getAtk() {
        return atk;
    }
    
    public void setAtk(int atk) {
        this.atk = atk;
    }
    
    public String[] getDialogue() {
        return dialogue;
    }
    
    public void setDialogue(String[] dialogue) {
        this.dialogue = dialogue;
    }
    
    public boolean isHostile() {
        return isHostile;
    }
    
    public void setHostile(boolean hostile) {
        isHostile = hostile;
    }
    
    public boolean isAlive() {
        return isAlive;
    }
    
    public void setAlive(boolean alive) {
        isAlive = alive;
    }
    
    public int getMaxHp() {
        return maxHp;
    }
    
    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }
    
    public int getDef() {
        return def;
    }
    
    public void setDef(int def) {
        this.def = def;
    }
    
    public int getLevel() {
        return level;
    }
    
    public void setLevel(int level) {
        this.level = level;
    }
    
    public String getElement() {
        return element;
    }
    
    public void setElement(String element) {
        this.element = element;
    }
    
    public double getDodgeRate() {
        return dodgeRate;
    }
    
    public void setDodgeRate(double dodgeRate) {
        this.dodgeRate = dodgeRate;
    }
    
    public double getCritRate() {
        return critRate;
    }
    
    public void setCritRate(double critRate) {
        this.critRate = critRate;
    }
    
    public double getCritDamage() {
        return critDamage;
    }
    
    public void setCritDamage(double critDamage) {
        this.critDamage = critDamage;
    }
    
    public int getResistance() {
        return resistance;
    }
    
    public void setResistance(int resistance) {
        this.resistance = resistance;
    }
    
    // BattleEntity接口方法实现
    @Override
    public int getTempAtk() {
        return tempAtk;
    }
    
    @Override
    public void setTempAtk(int tempAtk) {
        this.tempAtk = tempAtk;
    }
    
    @Override
    public int getTempDef() {
        return tempDef;
    }
    
    @Override
    public void setTempDef(int tempDef) {
        this.tempDef = tempDef;
    }
    
    @Override
    public List<StatusEffect> getStatusEffects() {
        return statusEffects;
    }
    
    @Override
    public void addStatusEffect(StatusEffect effect) {
        statusEffects.add(effect);
    }
    
    @Override
    public void removeStatusEffect(StatusEffect effect) {
        statusEffects.remove(effect);
    }
    
    @Override
    public void clearStatusEffects() {
        statusEffects.clear();
    }
    
    @Override
    public boolean hasStatus(BattleStatus status) {
        return statusEffects.stream().anyMatch(effect -> effect.getStatus() == status);
    }
    
    // 经验奖励和金币奖励的getter和setter
    public int getExpReward() {
        return expReward;
    }
    
    public void setExpReward(int expReward) {
        this.expReward = expReward;
    }
    
    public int getGoldReward() {
        return goldReward;
    }
    
    public void setGoldReward(int goldReward) {
        this.goldReward = goldReward;
    }
    
    public String getName() {
        return name;
    }
    
    public String getOccupation() {
        return profession;
    }
    
    public String getDescription() {
        return personality;
    }
    
    public String[] getDialogues() {
        return dialogue;
    }
    
    public GameDifficulty getDifficulty() {
        return difficulty;
    }
    
    public void setDifficulty(GameDifficulty difficulty) {
        this.difficulty = difficulty;
    }
    
    // 属性克制计算
    private double calculateElementAdvantage(String attackerElement, String defenderElement) {
        if (attackerElement.equals("无") || defenderElement.equals("无")) {
            return 1.0;
        }
        
        // 克制关系：火克风，风克雷，雷克水，水克火，土克雷，光暗互克
        switch (attackerElement) {
            case "火":
                return defenderElement.equals("风") ? 1.5 : 
                       defenderElement.equals("水") ? 0.7 : 1.0;
            case "水":
                return defenderElement.equals("火") ? 1.5 : 
                       defenderElement.equals("雷") ? 0.7 : 1.0;
            case "风":
                return defenderElement.equals("雷") ? 1.5 : 
                       defenderElement.equals("火") ? 0.7 : 1.0;
            case "雷":
                return defenderElement.equals("水") ? 1.5 : 
                       defenderElement.equals("风") ? 0.7 : 
                       defenderElement.equals("土") ? 0.5 : 1.0;
            case "土":
                return defenderElement.equals("雷") ? 1.5 : 1.0;
            case "光":
                return defenderElement.equals("暗") ? 2.0 : 1.0;
            case "暗":
                return defenderElement.equals("光") ? 2.0 : 1.0;
            default:
                return 1.0;
        }
    }
}