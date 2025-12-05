package com.mud.game.entity;

import java.io.Serializable;
import java.util.*;
import com.mud.game.system.EnhancedBattleEngine;
import com.mud.game.system.TaskManager;
import com.mud.game.system.RandomUtil;
import java.util.Random;

public class Player implements Serializable, BattleEntity {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private int hp;
    private int maxHp;
    private int atk;
    private int def;
    private int reputation;
    private int level;
    private int experience;
    private int money;
    private String currentRoomName;
    private List<Item> backpack;
    private Room currentRoom;
    private List<Task> tasks;
    private GameDifficulty difficulty; // 游戏难度
    
    // 战斗相关属性
    private String element;
    private double dodgeRate;
    private double critRate;
    private double critDamage;
    private int tempAtk;
    private int tempDef;
    private List<StatusEffect> statusEffects;
    private boolean isAlive;
    
    // 用于保存NPC血量状态的映射
    private Map<String, Integer> npcHealthMap;
    
    // 用于游戏内加载存档的临时属性（不序列化）
    private transient boolean loadRequested;
    private transient String loadSaveName;
    private transient TaskManager taskManager;
    
    public Player(String name, int hp, int atk, int def, int level, int experience, int money, GameDifficulty difficulty) {
        this.name = name;
        this.difficulty = difficulty;
        
        // 根据难度调整属性
        this.maxHp = difficulty.getPlayerHpByDifficulty(hp);
        this.hp = this.maxHp;
        this.atk = difficulty.getPlayerAtkByDifficulty(atk);
        this.def = difficulty.getPlayerDefByDifficulty(def);
        
        this.level = level;
        this.experience = experience;
        this.money = money;
        this.reputation = 0;
        this.backpack = new ArrayList<>();
        this.currentRoom = null;
        this.currentRoomName = "";
        this.tasks = new ArrayList<>();
        this.loadRequested = false;
        this.loadSaveName = null;
        this.taskManager = null;
        
        // 初始化战斗属性
        this.element = "无";
        this.dodgeRate = 0.05; // 5%基础闪避率
        this.critRate = 0.1;   // 10%基础暴击率
        this.critDamage = 1.5; // 1.5倍暴击伤害
        this.tempAtk = 0;
        this.tempDef = 0;
        this.statusEffects = new ArrayList<>();
        this.isAlive = true;
        this.npcHealthMap = new HashMap<>();
    }
    
    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }
    
    // 简化构造函数，用于测试，默认普通难度
    public Player(String name) {
        this(name, 100, 10, 5, 1, 0, 100, GameDifficulty.NORMAL);
    }
    
    public boolean takeItem(String itemName) {
        Item item = currentRoom.findItem(itemName);
        if (item != null) {
            if (currentRoom.removeItem(item)) {
                backpack.add(item);
                System.out.println("拾取了" + item.getName() + "。");
                // 自动更新收集类任务进度
                updateTaskProgress("collect", item.getName(), 1);
                return true;
            }
        }
        System.out.println("这里没有" + itemName + "。");
        return false;
    }
    
    public boolean useItem(String itemName) {
        for (Item item : new ArrayList<>(backpack)) { // 创建副本以避免并发修改异常
            if (item.getName().equalsIgnoreCase(itemName)) {
                // 特殊处理Equipment类型的物品
                if (item instanceof Equipment) {
                    Equipment equipment = (Equipment) item;
                    if (!equipment.isEquipped()) {
                        equipment.use(this);
                        equipment.setEquipped(true);
                        System.out.println("✅ " + itemName + " 已成功装备！");
                        // 装备不会从背包中移除
                    } else {
                        System.out.println("❌ " + itemName + " 已经装备了！");
                        System.out.println("请使用 'unequip " + itemName + "' 命x令来卸下装备。");
                    }
                } else {
                    // 普通物品的处理逻辑
                    item.use(this);
                    // 移除消耗品
                    if (item.getType() == ItemType.MEDICINE) {
                        backpack.remove(item);
                    }
                    // 对于普通武器和护甲，仍保持原有逻辑
                    else if (item.getType() == ItemType.WEAPON || 
                             item.getType() == ItemType.ARMOR) {
                        backpack.remove(item);
                    }
                }
                return true;
            }
        }
        System.out.println("背包里没有" + itemName + "。");
        return false;
    }
    
    /**
     * 卸下已装备的装备
     * @param itemName 装备名称
     * @return 是否成功卸下
     */
    /**
     * 显示当前已装备的所有装备
     */
    public void showEquippedItems() {
        List<Equipment> equippedItems = new ArrayList<>();
        
        // 遍历背包找出所有已装备的Equipment
        for (Item item : backpack) {
            if (item instanceof Equipment && ((Equipment) item).isEquipped()) {
                equippedItems.add((Equipment) item);
            }
        }
        
        if (equippedItems.isEmpty()) {
            System.out.println("📦 当前没有装备任何物品。");
            return;
        }
        
        System.out.println("🛡️ 当前已装备的物品：");
        System.out.println("======================");
        
        // 分类显示武器和护甲
        System.out.println("⚔️ 武器：");
        boolean hasWeapon = false;
        for (Equipment equip : equippedItems) {
            if (equip.getType() == ItemType.WEAPON) {
                hasWeapon = true;
                System.out.println("  " + equip.getName() + " (" + equip.getEffect() + "攻击力)");
                System.out.println("    品质：" + equip.getGrade().getDisplayName());
                System.out.println("    等级：" + equip.getLevel() + "/" + equip.getMaxLevel());
            }
        }
        if (!hasWeapon) {
            System.out.println("  无");
        }
        
        System.out.println("\n🛡️ 护甲：");
        boolean hasArmor = false;
        for (Equipment equip : equippedItems) {
            if (equip.getType() == ItemType.ARMOR) {
                hasArmor = true;
                System.out.println("  " + equip.getName() + " (" + equip.getEffect() + "防御力)");
                System.out.println("    品质：" + equip.getGrade().getDisplayName());
                System.out.println("    等级：" + equip.getLevel() + "/" + equip.getMaxLevel());
            }
        }
        if (!hasArmor) {
            System.out.println("  无");
        }
        
        System.out.println("======================");
        System.out.println("使用 'unequip <装备名>' 可以卸下装备。");
    }
    
    public boolean unequipItem(String itemName) {
        for (Item item : backpack) {
            if (item instanceof Equipment && item.getName().equalsIgnoreCase(itemName)) {
                Equipment equipment = (Equipment) item;
                if (equipment.isEquipped()) {
                    // 移除装备效果
                    if (equipment.getType() == ItemType.WEAPON) {
                        setAtk(getAtk() - (int)Math.round(equipment.getEffect()));
                    } else if (equipment.getType() == ItemType.ARMOR) {
                        setDef(getDef() - (int)Math.round(equipment.getEffect()));
                    }
                    
                    equipment.setEquipped(false);
                    System.out.println("✅ 已成功卸下 " + itemName + "！");
                    System.out.println(equipment.getEffectDescription().replace("+", "-"));
                    return true;
                } else {
                    System.out.println("❌ " + itemName + " 尚未装备！");
                    return true;
                }
            }
        }
        System.out.println("背包里没有" + itemName + "。");
        return false;
    }
    
    public void move(Direction direction) {
        Room currentRoom = this.currentRoom;
        Room newRoom = null;
        
        if (currentRoom == null) {
            System.out.println("你现在不在任何房间中。");
            return;
        }
        
        newRoom = currentRoom.getExit(direction);
        
        if (newRoom == null) {
            System.out.println("那个方向没有房间。");
        } else {
            this.currentRoom = newRoom;
            System.out.println("你移动到了 " + newRoom.getName() + "。");
            System.out.println(newRoom.getDescription());
            
            // 显示房间中的NPC
            if (newRoom.getNpc() != null) {
                System.out.println("你看到了 " + newRoom.getNpc().getName() + "。");
            }
            
            // 更新任务进度（探索）
            updateTaskProgress("explore", currentRoom.getName(), 1);
            
            // 触发可能的任务
            if (taskManager != null) {
                taskManager.checkTaskTriggers(this, "enter", this.currentRoom.getName());
            }
        }
    }
    
    public void attack(NPC npc) {
        if (!npc.isAlive()) {
            System.out.println(npc.getName() + "已经死亡。");
            return;
        }
        
        // 使用增强版战斗引擎进行回合制战斗
        EnhancedBattleEngine battleEngine = new EnhancedBattleEngine();
        battleEngine.startBattle(this, npc);
    }
    
    // 单次攻击方法，用于回合制战斗系统
    public int performAttack(NPC npc) {
        if (!npc.isAlive()) {
            return 0;
        }
        
        // 计算伤害，包含随机性和暴击机制
        int baseDamage = Math.max(1, this.atk - 5);
        int randomFactor = RandomUtil.nextInt(1, 11); // 1-10的随机因子
        int damage = baseDamage + randomFactor;
        
        // 暴击判定（10%概率）
        if (RandomUtil.isTriggered(0.1)) {
            damage = (int)(damage * 1.5); // 1.5倍暴击伤害
            System.out.println("暴击！");
        }
        
        // 调用新版本的takeDamage方法，传递Player参数
        npc.takeDamage(damage, this);
        System.out.println("你对" + npc.getName() + "造成了" + damage + "点伤害！");
        
        return damage;
    }
    
    public void talkToNPC() {
        NPC npc = currentRoom.getNpc();
        if (npc != null && npc.isAlive()) {
            npc.talk(this);
            if (!npc.isHostile()) {
                updateTaskProgress("talk", npc.getName(), 1);

                // 检查NPC是否有可用任务
                // if (!npc.getAvailableTasks().isEmpty()) {
                //     System.out.println("\n【任务提示】");
                //     System.out.println(npc.getName() + "似乎有任务要交给你...");
                //     System.out.println("输入 'task' 查看可用任务");
                // }
                if (taskManager != null) {
                    taskManager.checkTaskTriggers(this, "talk", npc.getName());
                }
            }
        } else {
            System.out.println("这里没有可以对话的人。");
        }
    }
    
    public void updateTaskProgress(String targetType, String targetName, int amount) {
        for (Task task : tasks) {
            if (task.getStatus() == TaskStatus.IN_PROGRESS) {
                task.updateProgress(targetType, targetName, amount);
                task.checkComplete(this);
            }
        }
    }
    
    public void showStatus() {
        System.out.println("\n=== 角色状态 ===");
        System.out.println("姓名：" + name);
        System.out.println("等级：" + level);
        System.out.println("生命值：" + hp + "/" + maxHp);
        System.out.println("攻击力：" + atk);
        System.out.println("防御力：" + def);
        System.out.println("侠义值：" + reputation);
        System.out.println("经验值：" + experience);
        System.out.println("金币：" + money);
        
        // 添加当前位置的详细描述
        System.out.println("\n=== 当前位置 ===");
        if (currentRoom != null) {
            System.out.println("位置名称：" + currentRoom.getName());
            System.out.println(currentRoom.getExitDescription());
            System.out.println(currentRoom.getItemsDescription());
            // 显示房间中的NPC信息
            if (currentRoom.getNpc() != null && currentRoom.getNpc().isAlive()) {
                System.out.println("NPC：" + currentRoom.getNpc().getName() + " - " + currentRoom.getNpc().getDescription());
            } else {
                System.out.println("NPC：无");
            }
        } else {
            System.out.println("位置名称：" + currentRoomName);
            System.out.println("详细信息：无法获取当前位置的详细信息");
        }
    }
    
    public void showBackpack() {
        System.out.println("\n=== 背包 ===");
        if (backpack.isEmpty()) {
            System.out.println("背包是空的。");
        } else {
            for (Item item : backpack) {
                System.out.println("- " + item);
            }
        }
    }
    
    public void showTasks() {
        System.out.println("\n=== 任务列表 ===");
        if (tasks.isEmpty()) {
            System.out.println("暂无任务。");
        } else {
            for (Task task : tasks) {
                if (task.getStatus() != TaskStatus.NOT_ACCEPTED) {
                    System.out.println(task);
                }
            }
        }
    }
    
    public void acceptTask(String taskName) {
        Task targetTask = null;
        
        // 查找任务
        for (Task task : tasks) {
            if (task.getName().equals(taskName)) {
                targetTask = task;
                break;
            }
        }
        
        if (targetTask == null) {
            System.out.println("❌ 未找到任务: " + taskName);
            return;
        }
        
        if (targetTask.getStatus() != TaskStatus.NOT_ACCEPTED) {
            System.out.println("❌ 任务 " + taskName + " 已经" + targetTask.getStatus() + "，无法接受。");
            return;
        }
        
        // 接受任务
        targetTask.acceptTask();
        System.out.println("✅ 成功接受任务: " + taskName);
        System.out.println("任务目标: " + targetTask.getObjective());
        
        // 显示任务提示
        if (targetTask.isMainTask()) {
            System.out.println("💡 这是主线任务，建议优先完成。");
        }
    }
    
    public void showAvailableTasks() {
        boolean hasAvailableTasks = false;
        
        System.out.println("\n=== 可接受的任务 ===");
        for (Task task : tasks) {
            if (task.getStatus() == TaskStatus.NOT_ACCEPTED) {
                System.out.println("任务名称: " + task.getName());
                System.out.println("任务描述: " + task.getDescription());
                if (task.isMainTask()) {
                    System.out.println("任务类型: 主线任务");
                } else {
                    System.out.println("任务类型: 支线任务");
                }
                System.out.println("-------------------");
                hasAvailableTasks = true;
            }
        }
        
        if (!hasAvailableTasks) {
            System.out.println("当前没有可接受的任务。");
            System.out.println("提示: 与NPC对话或探索新区域可能会触发新任务。");
        } else {
            System.out.println("\n使用方法: accept <任务名称>");
            System.out.println("例如: accept 初来乍到");
        }
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getHp() {
        return hp;
    }
    
    public void setHp(int hp) {
        this.hp = Math.min(hp, maxHp);
        if (this.hp <= 0) {
            this.isAlive = false;
            System.out.println("你死亡了！游戏结束！");
            System.exit(0);
        }
    }
    
    public int getMaxHp() {
        return maxHp;
    }
    
    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }
    
    public int getAtk() {
        return atk;
    }
    
    public void setAtk(int atk) {
        this.atk = atk;
    }
    
    public int getDef() {
        return def;
    }
    
    public void setDef(int def) {
        this.def = def;
    }
    
    public GameDifficulty getDifficulty() {
        return difficulty;
    }
    
    public void setDifficulty(GameDifficulty difficulty) {
        this.difficulty = difficulty;
        // 根据新难度重新调整属性
        this.maxHp = difficulty.getPlayerHpByDifficulty(100); // 基础生命值100
        this.hp = Math.min(this.hp, this.maxHp); // 确保当前生命值不超过最大值
        this.atk = difficulty.getPlayerAtkByDifficulty(10);   // 基础攻击力10
        this.def = difficulty.getPlayerDefByDifficulty(5);    // 基础防御力5
    }
    
    public int getReputation() {
        return reputation;
    }
    
    public void setReputation(int reputation) {
        this.reputation = reputation;
    }
    
    public List<Item> getBackpack() {
        return backpack;
    }
    
    public void setBackpack(List<Item> backpack) {
        this.backpack = backpack;
    }
    
    public Room getCurrentRoom() {
        return currentRoom;
    }
    
    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }
    
    public List<Task> getTasks() {
        return tasks;
    }
    
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
    
    public int getLevel() {
        return level;
    }
    
    public void setLevel(int level) {
        this.level = level;
    }
    
    public int getExperience() {
        return experience;
    }
    
    public void setExperience(int experience) {
        this.experience = experience;
    }
    
    public int getMoney() {
        return money;
    }
    
    public void setMoney(int money) {
        this.money = money;
    }
    
    public String getCurrentRoomName() {
        return currentRoom != null ? currentRoom.getName() : currentRoomName;
    }
    
    public void setCurrentRoomName(String currentRoomName) {
        this.currentRoomName = currentRoomName;
    }
    
    public void gainExperience(int exp) {
        this.experience += exp;
        System.out.println("获得经验值：" + exp);
        
        // 检查升级
        int expNeeded = level * 100;
        while (experience >= expNeeded) {
            experience -= expNeeded;
            level++;
            
            // 基础升级属性加成
            int hpGain = 10;
            int atkGain = 2;
            int defGain = 1;
            
            // 根据难度调整升级加成
            if (difficulty != null) {
   // 根据难度调整升级加成
            maxHp += difficulty.getPlayerHpByDifficulty(hpGain);
            atk += difficulty.getPlayerAtkByDifficulty(atkGain);
            def += difficulty.getPlayerDefByDifficulty(defGain);
            } else {
                maxHp += hpGain;
                atk += atkGain;
                def += defGain;
            }
            
            hp = maxHp;
            System.out.println("恭喜升级！当前等级：" + level);
            expNeeded = level * 100;
        }
    }
    
    public void gainMoney(int amount) {
        this.money += amount;
        System.out.println("获得金币：" + amount);
    }
    
    public void gainExp(int exp) {
        gainExperience(exp);
    }
    
    public void gainGold(int gold) {
        gainMoney(gold);
    }
    
    public Map<String, Integer> getItems() {
        Map<String, Integer> itemMap = new HashMap<>();
        for (Item item : backpack) {
            String name = item.getName();
            itemMap.put(name, itemMap.getOrDefault(name, 0) + 1);
        }
        return itemMap;
    }
    
    public void addItem(String itemName, int count) {
        // 简化实现：创建基础物品
        for (int i = 0; i < count; i++) {
            Item item = new Item(itemName, "基础物品", 0, ItemType.OTHER);
            backpack.add(item);
        }
    }
    
    public void addItem(Item item) {
        backpack.add(item);
        System.out.println("获得物品：" + item.getName());
    }
    
    // 用于游戏内加载存档的方法
    public boolean isLoadRequested() {
        return loadRequested;
    }
    
    public void setLoadRequested(boolean loadRequested) {
        this.loadRequested = loadRequested;
    }
    
    public String getLoadSaveName() {
        return loadSaveName;
    }
    
    public void setLoadSaveName(String loadSaveName) {
        this.loadSaveName = loadSaveName;
    }
    
    // BattleEntity接口实现
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
    public double getDodgeRate() {
        return dodgeRate;
    }
    
    @Override
    public void setDodgeRate(double dodgeRate) {
        this.dodgeRate = Math.max(0.0, Math.min(0.9, dodgeRate));
    }
    
    @Override
    public double getCritRate() {
        return critRate;
    }
    
    @Override
    public void setCritRate(double critRate) {
        this.critRate = Math.max(0.0, Math.min(1.0, critRate));
    }
    
    @Override
    public double getCritDamage() {
        return critDamage;
    }
    
    @Override
    public void setCritDamage(double critDamage) {
        this.critDamage = Math.max(1.0, Math.min(3.0, critDamage));
    }
    
    @Override
    public String getElement() {
        return element;
    }
    
    @Override
    public void setElement(String element) {
        this.element = element;
    }
    
    @Override
    public List<StatusEffect> getStatusEffects() {
        return statusEffects;
    }
    
    @Override
    public void addStatusEffect(StatusEffect effect) {
        statusEffects.add(effect);
        System.out.println("✨ " + name + "获得了" + effect.getStatus().getName() + "效果！");
    }
    
    @Override
    public void removeStatusEffect(StatusEffect effect) {
        statusEffects.remove(effect);
        System.out.println("⏰ " + name + "的" + effect.getStatus().getName() + "效果消失了！");
    }
    
    @Override
    public void clearStatusEffects() {
        statusEffects.clear();
        System.out.println("🧹 " + name + "的所有状态效果被清除了！");
    }
    
    @Override
    public boolean hasStatus(BattleStatus status) {
        for (StatusEffect effect : statusEffects) {
            if (effect.getStatus() == status) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void takeDamage(int damage) {
        setHp(getHp() - damage);
        System.out.println("💔 " + name + "受到了" + damage + "点伤害！");
    }
    
    @Override
    public boolean isAlive() {
        return isAlive && hp > 0;
    }
    
    @Override
    public void setAlive(boolean alive) {
        this.isAlive = alive;
    }
    
    // NPC血量状态管理相关方法
    public Map<String, Integer> getNpcHealthMap() {
        return npcHealthMap;
    }
    
    public void setNpcHealthMap(Map<String, Integer> npcHealthMap) {
        this.npcHealthMap = npcHealthMap;
    }
    
    public void updateNpcHealth(String npcName, int health) {
        npcHealthMap.put(npcName, health);
    }
    
    public Integer getNpcHealth(String npcName) {
        return npcHealthMap.get(npcName);
    }
}