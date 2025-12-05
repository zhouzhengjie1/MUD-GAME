package com.mud.game.system;

import com.mud.game.entity.*;
import com.mud.game.ui.BattleAnimation;
import com.mud.game.system.RandomUtil;
import java.util.*;

public class EnhancedBattleEngine {
    private Scanner scanner;
    private List<NPC> enemies;
    private Map<NPC, Integer> enemyTargets; // 记录每个敌人攻击的目标
    
    // 战斗状态
    private Map<BattleEntity, List<StatusEffect>> activeEffects;
    
    public EnhancedBattleEngine() {
        this.scanner = new Scanner(System.in);
        this.enemies = new ArrayList<>();
        this.enemyTargets = new HashMap<>();
        this.activeEffects = new HashMap<>();
    }
    
    // 开始多敌人战斗
    public void startMultiBattle(Player player, List<NPC> enemyList) {
        this.enemies = new ArrayList<>(enemyList);
        this.enemyTargets.clear();
        this.activeEffects.clear();
        
        // 播放战斗开始动画
        BattleAnimation.playBattleStartAnimation(
            enemies.stream().map(NPC::getName).toList()
        );
        
        // 初始化敌人目标
        for (NPC enemy : enemies) {
            enemyTargets.put(enemy, 0); // 0表示攻击玩家
        }
        
        int turn = 1;
        while (player.isAlive() && hasAliveEnemies()) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("🎮 第 " + turn + " 回合");
            System.out.println("=".repeat(60));
            
            displayEnhancedBattleStatus(player);
            
            // 处理状态效果
            processStatusEffects(player);
            processEnemiesStatusEffects();
            
            if (!player.isAlive()) break;
            
            // 检查玩家是否被眩晕
            if (player.hasStatus(BattleStatus.STUN)) {
                System.out.println("\n😵 你处于眩晕状态，无法行动！");
            } else {
                // 玩家回合
                if (!playerTurn(player)) {
                    break; // 玩家逃跑或死亡
                }
            }
            
            if (!player.isAlive() || !hasAliveEnemies()) break;
            
            // 敌人回合
            enemiesTurn(player);
            
            // 回合结束处理
            endTurn();
            turn++;
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // 战斗结果
        if (player.isAlive()) {
            handleVictory(player);
        } else {
            handleDefeat(player);
        }
    }
    
    // 单敌人战斗（兼容原有接口）
    public void startBattle(Player player, NPC enemy) {
        List<NPC> enemyList = new ArrayList<>();
        enemyList.add(enemy);
        startMultiBattle(player, enemyList);
    }
    
    // 玩家回合
    private boolean playerTurn(Player player) {
        System.out.println("\n🎯 你的回合，请选择行动：");
        System.out.println("1. ⚔️ 攻击");
        System.out.println("2. 🛡️ 防御");
        System.out.println("3. 💊 使用物品");
        System.out.println("4. 🏃 逃跑");
        System.out.println("5. 🎯 选择目标");
        
        int action = getPlayerChoice(1, 5);
        
        switch (action) {
            case 1:
                return playerAttack(player);
            case 2:
                playerDefend(player);
                return true;
            case 3:
                return useItem(player);
            case 4:
                return tryEscape(player);
            case 5:
                selectTarget(player);
                return playerTurn(player); // 重新选择行动
            default:
                return true;
        }
    }
    
    // 玩家攻击
    private boolean playerAttack(Player player) {
        System.out.println("\n⚔️ 选择攻击目标：");
        displayAliveEnemies();
        
        int targetIndex = getPlayerChoice(1, getAliveEnemiesCount()) - 1;
        NPC target = getAliveEnemy(targetIndex);
        
        if (target == null) {
            System.out.println("❌ 无效目标！");
            return true;
        }
        
        // 闪避判定
        if (RandomUtil.nextDouble() < target.getDodgeRate()) {
            BattleAnimation.playDodgeAnimation(player.getName(), target.getName());
            return true;
        }
        
        // 暴击判定
        boolean isCrit = RandomUtil.nextDouble() < player.getCritRate();
        double damageMultiplier = isCrit ? player.getCritDamage() : 1.0;
        
        // 基础伤害计算
        int baseDamage = Math.max(1, player.getAtk() + player.getTempAtk() - target.getDef() - target.getTempDef());
        int damage = (int)(baseDamage * damageMultiplier);
        
        // 属性克制
        double elementModifier = calculateElementAdvantage(player.getElement(), target.getElement());
        damage = (int)(damage * elementModifier);
        
        // 执行攻击
        target.takeDamage(damage);
        
        // 显示攻击动画和效果
        BattleAnimation.playAttackAnimation(player.getName(), target.getName());
        BattleAnimation.showDamageNumber(target.getName(), damage, isCrit);
        
        if (isCrit) {
            BattleAnimation.playCriticalAnimation(player.getName(), target.getName());
        }
        
        if (elementModifier > 1.2) {
            System.out.println("🔥 属性克制！伤害提升！");
        } else if (elementModifier < 0.8) {
            System.out.println("❄️ 属性被克制！伤害降低！");
        }
        
        // 添加随机状态效果（5%概率）
        if (RandomUtil.isTriggered(0.05)) {
            applyRandomStatusEffect(target);
        }
        
        return true;
    }
    
    // 使用物品
    private boolean useItem(Player player) {
        if (player.getBackpack().isEmpty()) {
            System.out.println("❌ 背包是空的！");
            return true;
        }
        
        System.out.println("\n💊 选择要使用的物品：");
        List<Item> items = player.getBackpack();
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + items.get(i).getName() + " - " + items.get(i).getDescription());
        }
        System.out.println((items.size() + 1) + ". 返回");
        
        int choice = getPlayerChoice(1, items.size() + 1);
        if (choice == items.size() + 1) {
            return true;
        }
        
        Item item = items.get(choice - 1);
        return applyItemEffect(player, item);
    }
    
    // 应用物品效果
    private boolean applyItemEffect(Player player, Item item) {
        switch (item.getType()) {
            case MEDICINE:
                int oldHp = player.getHp();
                int newHp = Math.min(player.getHp() + (int)Math.round(item.getEffect()), player.getMaxHp());
                player.setHp(newHp);
                System.out.println("💚 使用了" + item.getName() + "，生命值：" + oldHp + " → " + newHp + " (+" + item.getEffect() + ")");
                player.getBackpack().remove(item);
                return true;
                
            case BUFF_POTION:
                StatusEffect buffEffect = new StatusEffect(BattleStatus.ATTACK_BOOST, (int)Math.round(item.getEffect() / 5));
                player.addStatusEffect(buffEffect);
                System.out.println("⚔️ 使用了" + item.getName() + "，获得攻击强化效果！");
                player.getBackpack().remove(item);
                return true;
                
            case ANTIDOTE:
                // 清除负面状态
                player.getStatusEffects().removeIf(effect -> !effect.getStatus().isPositive());
                System.out.println("💊 使用了" + item.getName() + "，清除了所有负面状态！");
                player.getBackpack().remove(item);
                return true;
                
            case POISON:
                System.out.println("☠️ 选择要施毒的目标：");
                displayAliveEnemies();
                int targetIndex = getPlayerChoice(1, getAliveEnemiesCount()) - 1;
                NPC target = getAliveEnemy(targetIndex);
                if (target != null) {
                    StatusEffect poisonEffect = new StatusEffect(BattleStatus.POISON, (int)Math.round(item.getEffect() / 3));
                    target.addStatusEffect(poisonEffect);
                    System.out.println("☠️ " + target.getName() + "中毒了！");
                    player.getBackpack().remove(item);
                }
                return true;
                
            default:
                System.out.println("❌ " + item.getName() + "无法在战斗中使用！");
                return false;
        }
    }
    
    // 逃跑
    private boolean tryEscape(Player player) {
        System.out.println("\n🏃 尝试逃跑...");
        
        // 基础逃跑成功率：50% + 玩家等级 * 2% - 敌人平均等级 * 3%
        double escapeChance = 0.5 + (player.getLevel() * 0.02) - (getAverageEnemyLevel() * 0.03);
        escapeChance = Math.max(0.1, Math.min(0.9, escapeChance)); // 限制在10%-90%之间
        
        if (RandomUtil.isTriggered(escapeChance)) {
            System.out.println("💨 成功逃跑！");
            return false; // 结束战斗
        } else {
            System.out.println("❌ 逃跑失败！敌人追了上来！");
            // 逃跑失败，敌人获得额外攻击机会
            return true;
        }
    }
    
    // 防御
    private void playerDefend(Player player) {
        StatusEffect defendEffect = new StatusEffect(BattleStatus.DEFENSE_BOOST, 2, 1);
        player.addStatusEffect(defendEffect);
        System.out.println("🛡️ 你采取了防御姿态，防御力临时提升！");
    }
    
    // 选择目标
    private void selectTarget(Player player) {
        System.out.println("\n🎯 选择要集中攻击的目标：");
        displayAliveEnemies();
        
        int targetIndex = getPlayerChoice(1, getAliveEnemiesCount()) - 1;
        NPC target = getAliveEnemy(targetIndex);
        
        if (target != null) {
            System.out.println("🎯 你将集中攻击" + target.getName() + "！");
            // 这里可以添加集中攻击的buff效果
        }
    }
    
    // 处理状态效果
    private void processStatusEffects(BattleEntity entity) {
        List<StatusEffect> effects = entity.getStatusEffects();
        Iterator<StatusEffect> iterator = effects.iterator();
        
        while (iterator.hasNext()) {
            StatusEffect effect = iterator.next();
            effect.applyEffect(entity);
            
            if (!effect.isActive()) {
                System.out.println("⏰ " + entity.getName() + "的" + effect.getStatus().getName() + "效果结束了");
                iterator.remove();
            }
        }
    }
    
    private void processStatusEffects(Player player) {
        processStatusEffects((BattleEntity) player);
    }
    
    private void processEnemiesStatusEffects() {
        for (NPC enemy : enemies) {
            if (enemy.isAlive()) {
                processStatusEffects(enemy);
            }
        }
    }
    
    // 回合结束
    private void endTurn() {
        // 减少所有状态效果的剩余回合
        for (BattleEntity entity : activeEffects.keySet()) {
            List<StatusEffect> effects = entity.getStatusEffects();
            Iterator<StatusEffect> iterator = effects.iterator();
            
            while (iterator.hasNext()) {
                StatusEffect effect = iterator.next();
                effect.endTurn();
                
                if (!effect.isActive()) {
                    System.out.println("⏰ " + entity.getName() + "的" + effect.getStatus().getName() + "效果结束了");
                    iterator.remove();
                }
            }
        }
    }
    
    // 显示增强战斗状态
    private void displayEnhancedBattleStatus(Player player) {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.printf("║ 👤 玩家: %-20s 等级: %-3d 元素: %-3s   ║%n", 
                         player.getName(), player.getLevel(), player.getElement());
        
        // 显示状态效果
        if (!player.getStatusEffects().isEmpty()) {
            System.out.print("║ 状态效果: ");
            for (StatusEffect effect : player.getStatusEffects()) {
                System.out.print(effect.getDescription() + " ");
            }
            System.out.println("║");
        }
        
        String playerHpBar = createHpBar(player.getHp(), player.getMaxHp(), 25);
        System.out.printf("║ HP: [%s] %3d/%3d    ATK: %-3d  DEF: %-3d  闪避: %3.0f%% ║%n", 
                         playerHpBar, player.getHp(), player.getMaxHp(),
                         player.getAtk() + player.getTempAtk(), player.getDef() + player.getTempDef(),
                         player.getDodgeRate() * 100);
        
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        
        // 显示敌人状态
        System.out.println("║ 👹 敌人状态:                                                ║");
        for (int i = 0; i < enemies.size(); i++) {
            NPC enemy = enemies.get(i);
            if (enemy.isAlive()) {
                String enemyHpBar = createHpBar(enemy.getHp(), enemy.getMaxHp(), 20);
                System.out.printf("║ %d. %-15s HP: [%s] %3d/%3d 元素: %-3s ║%n", 
                                 i + 1, enemy.getName(), enemyHpBar, enemy.getHp(), enemy.getMaxHp(),
                                 enemy.getElement());
                
                // 显示敌人状态效果
                if (!enemy.getStatusEffects().isEmpty()) {
                    System.out.print("║    状态: ");
                    for (StatusEffect effect : enemy.getStatusEffects()) {
                        System.out.print(effect.getDescription() + " ");
                    }
                    System.out.println("║");
                }
            }
        }
        
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
    }
    
    // 创建血条
    private String createHpBar(int current, int max, int length) {
        int filled = (int)((double)current / max * length);
        StringBuilder bar = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            if (i < filled) {
                bar.append("█");
            } else {
                bar.append("░");
            }
        }
        
        return bar.toString();
    }
    
    // 获取玩家选择
    private int getPlayerChoice(int min, int max) {
        while (true) {
            System.out.print("请选择 (" + min + "-" + max + "): ");
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice >= min && choice <= max) {
                    return choice;
                }
                System.out.println("❌ 无效选择，请重新输入！");
            } catch (NumberFormatException e) {
                System.out.println("❌ 请输入数字！");
            }
        }
    }
    
    // 显示存活的敌人
    private void displayAliveEnemies() {
        int index = 1;
        for (NPC enemy : enemies) {
            if (enemy.isAlive()) {
                System.out.println(index + ". " + enemy.getName() + " (HP: " + enemy.getHp() + "/" + enemy.getMaxHp() + ")");
                index++;
            }
        }
    }
    
    // 获取存活的敌人数量
    private int getAliveEnemiesCount() {
        int count = 0;
        for (NPC enemy : enemies) {
            if (enemy.isAlive()) {
                count++;
            }
        }
        return count;
    }
    
    // 获取存活的敌人
    private NPC getAliveEnemy(int index) {
        int currentIndex = 0;
        for (NPC enemy : enemies) {
            if (enemy.isAlive()) {
                if (currentIndex == index) {
                    return enemy;
                }
                currentIndex++;
            }
        }
        return null;
    }
    
    // 检查是否有存活的敌人
    private boolean hasAliveEnemies() {
        for (NPC enemy : enemies) {
            if (enemy.isAlive()) {
                return true;
            }
        }
        return false;
    }
    
    // 获取敌人平均等级
    private double getAverageEnemyLevel() {
        if (enemies.isEmpty()) return 1;
        
        int totalLevel = 0;
        int aliveCount = 0;
        for (NPC enemy : enemies) {
            if (enemy.isAlive()) {
                totalLevel += enemy.getLevel();
                aliveCount++;
            }
        }
        return aliveCount > 0 ? (double)totalLevel / aliveCount : 1;
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
    
    // 计算魔法伤害
    public int calculateMagicDamage(Player player, NPC target, String element, boolean isCrit) {
        double damageMultiplier = isCrit ? player.getCritDamage() : 1.0;
        
        // 基础魔法伤害（略高于普通攻击）
        int baseDamage = Math.max(1, (player.getAtk() + player.getTempAtk()) * 3 / 2 - target.getDef() - target.getTempDef());
        int damage = (int)(baseDamage * damageMultiplier);
        
        // 元素克制
        double elementModifier = calculateElementAdvantage(element, target.getElement());
        damage = (int)(damage * elementModifier);
        
        return Math.max(1, damage);
    }
    
    // 计算普通伤害（用于外部调用）
    public int calculateDamage(Player player, NPC target, boolean isCrit) {
        double damageMultiplier = isCrit ? player.getCritDamage() : 1.0;
        int baseDamage = Math.max(1, player.getAtk() + player.getTempAtk() - target.getDef() - target.getTempDef());
        return (int)(baseDamage * damageMultiplier);
    }
    
    // 计算敌人对玩家的伤害
    public int calculateDamage(NPC enemy, Player target, boolean isCrit) {
        double damageMultiplier = isCrit ? 1.5 : 1.0; // 敌人基础暴击倍率
        int baseDamage = Math.max(1, enemy.getAtk() + enemy.getTempAtk() - target.getDef() - target.getTempDef());
        return (int)(baseDamage * damageMultiplier);
    }
    
    // 使用物品（外部调用接口）
    public boolean useItem(Player player, String itemName) {
        // 在背包中查找物品
        Item itemToUse = null;
        for (Item item : player.getBackpack()) {
            if (item.getName().equals(itemName)) {
                itemToUse = item;
                break;
            }
        }
        
        if (itemToUse == null) {
            System.out.println("❌ 背包中没有" + itemName + "！");
            return false;
        }
        
        return applyItemEffect(player, itemToUse);
    }
    
    // 应用随机状态效果
    private void applyRandomStatusEffect(NPC target) {
        BattleStatus[] possibleEffects = {
            BattleStatus.POISON, BattleStatus.BLEED, BattleStatus.BURN
        };
        
        BattleStatus randomEffect = RandomUtil.randomElement(possibleEffects);
        StatusEffect effect = new StatusEffect(randomEffect, 1);
        target.addStatusEffect(effect);
        
        System.out.println("✨ " + target.getName() + "获得了" + randomEffect.getName() + "效果！");
    }
    
    // 敌人回合
    private void enemiesTurn(Player player) {
        for (NPC enemy : enemies) {
            if (enemy.isAlive() && !enemy.hasStatus(BattleStatus.STUN)) {
                enemyAttack(enemy, player);
            } else if (enemy.hasStatus(BattleStatus.STUN)) {
                System.out.println("\n😵 " + enemy.getName() + "处于眩晕状态，无法行动！");
            }
        }
    }
    
    // 敌人攻击
    private void enemyAttack(NPC enemy, Player player) {
        System.out.println("\n👹 " + enemy.getName() + "的回合：");
        
        // 敌人AI选择攻击类型
        int attackType = RandomUtil.nextInt(100);
        
        if (attackType < 60) { // 60% 普通攻击
            performNormalEnemyAttack(enemy, player);
        } else if (attackType < 85) { // 25% 强力攻击
            performStrongEnemyAttack(enemy, player);
        } else { // 15% 特殊技能
            performSpecialEnemyAttack(enemy, player);
        }
    }
    
    // 普通敌人攻击
    private void performNormalEnemyAttack(NPC enemy, Player player) {
        // 闪避判定
        if (RandomUtil.nextDouble() < player.getDodgeRate()) {
            System.out.println("💨 " + player.getName() + "闪避了" + enemy.getName() + "的攻击！");
            return;
        }
        
        // 暴击判定
        boolean isCrit = RandomUtil.nextDouble() < enemy.getCritRate();
        double damageMultiplier = isCrit ? enemy.getCritDamage() : 1.0;
        
        int baseDamage = Math.max(1, enemy.getAtk() - player.getDef() - player.getTempDef());
        int damage = (int)(baseDamage * damageMultiplier);
        
        player.setHp(player.getHp() - damage);
        
        BattleAnimation.playAttackAnimation(enemy.getName(), player.getName());
        BattleAnimation.showDamageNumber(player.getName(), damage, isCrit);
        
        if (isCrit) {
            BattleAnimation.playCriticalAnimation(enemy.getName(), player.getName());
        }
    }
    
    // 强力敌人攻击
    private void performStrongEnemyAttack(NPC enemy, Player player) {
        System.out.println("⚠️ " + enemy.getName() + "开始蓄力...");
        
        // 闪避判定（降低闪避率）
        if (RandomUtil.nextDouble() < player.getDodgeRate() * 0.7) {
            System.out.println("💨 " + player.getName() + "勉强闪避了强力攻击！");
            return;
        }
        
        int damage = Math.max(2, (enemy.getAtk() * 3 / 2) - player.getDef() - player.getTempDef());
        player.setHp(player.getHp() - damage);
        
        System.out.println("🔥 " + enemy.getName() + "的强力攻击对你造成了" + damage + "点伤害！");
    }
    
    // 特殊敌人攻击
    private void performSpecialEnemyAttack(NPC enemy, Player player) {
        System.out.println("✨ " + enemy.getName() + "正在聚集能量...");
        
        int specialType = RandomUtil.nextInt(3);
        switch (specialType) {
            case 0: // 降低防御
                int defenseReduction = 3;
                player.setTempDef(Math.max(-10, player.getTempDef() - defenseReduction));
                System.out.println("⚡ 特殊技能：你的防御力降低了" + defenseReduction + "点！");
                break;
                
            case 1: // 添加负面状态
                BattleStatus[] debuffs = {BattleStatus.POISON, BattleStatus.BLEED, BattleStatus.WEAK};
                BattleStatus randomDebuff = RandomUtil.randomElement(debuffs);
                StatusEffect debuffEffect = new StatusEffect(randomDebuff, 1);
                player.addStatusEffect(debuffEffect);
                System.out.println("⚡ 特殊技能：你获得了" + randomDebuff.getName() + "效果！");
                break;
                
            case 2: // 百分比伤害
                int percentageDamage = player.getHp() / 5; // 20%最大生命值
                player.setHp(player.getHp() - percentageDamage);
                System.out.println("⚡ 特殊技能：" + enemy.getName() + "造成了你最大生命值20%的伤害（" + percentageDamage + "点）！");
                break;
        }
    }
    
    // 处理胜利
    private void handleVictory(Player player) {
        BattleAnimation.playVictoryAnimation();
        
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                        🎉 胜利！🎉                         ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        
        int totalExp = 0;
        int totalMoney = 0;
        
        for (NPC enemy : enemies) {
            if (!enemy.isAlive()) {
                totalExp += enemy.getLevel() * 15 + enemy.getAtk() * 2;
                totalMoney += enemy.getLevel() * 10 + enemy.getAtk() * 1;
                System.out.printf("║ 击败了 %-15s 获得经验: %-5d 金币: %-5d ║%n", 
                                 enemy.getName(), enemy.getLevel() * 15 + enemy.getAtk() * 2, 
                                 enemy.getLevel() * 10 + enemy.getAtk() * 1);
            }
        }
        
        player.gainExperience(totalExp);
        player.setMoney(player.getMoney() + totalMoney);
        
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.printf("║ 总计获得: 经验值 %-8d 金币 %-8d                  ║%n", totalExp, totalMoney);
        System.out.printf("║ 当前状态: 等级 %-3d 总经验 %-8d 总金币 %-8d ║%n", 
                         player.getLevel(), player.getExperience(), player.getMoney());
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        
        // 更新任务进度
        for (NPC enemy : enemies) {
            if (!enemy.isAlive()) {
                player.updateTaskProgress("kill", enemy.getName(), 1);
            }
        }
        
        System.out.println("\n🎵 胜利的音乐响起...");
    }
    
    // 处理失败
    private void handleDefeat(Player player) {
        BattleAnimation.playDefeatAnimation();
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                        💀 失败！💀                          ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.println("║ 你被敌人击败了！                                           ║");
        System.out.println("║ 失去了一半的金币...                                        ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        
        // 惩罚：失去一半金币
        int lostMoney = player.getMoney() / 2;
        player.setMoney(player.getMoney() - lostMoney);
        
        // 恢复一些生命值避免游戏结束
        player.setHp(Math.max(1, player.getMaxHp() / 10));
        
        System.out.println("💸 失去了 " + lostMoney + " 金币...");
        System.out.println("❤️ 你在村庄中醒来，生命值恢复到了 " + player.getHp() + "/" + player.getMaxHp());
    }
}