package com.mud.game.ui;

import com.mud.game.entity.*;
import com.mud.game.system.EnhancedBattleEngine;
import com.mud.game.system.RandomUtil;
import java.util.*;

/**
 * 增强版战斗用户界面
 * 集成动画效果和视觉特效
 */
public class EnhancedBattleUI {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    
    private EnhancedBattleEngine battleEngine;
    private Player player;
    private List<NPC> enemies;
    private boolean inBattle;
    
    public EnhancedBattleUI(Player player, List<NPC> enemies) {
        this.player = player;
        this.enemies = enemies;
        this.battleEngine = new EnhancedBattleEngine();
        this.inBattle = true;
    }
    
    /**
     * 开始增强版战斗界面
     */
    public void startBattle() {
        BattleAnimation.playBattleStartAnimation(
            enemies.stream().map(NPC::getName).toList()
        );
        
        while (inBattle && player.isAlive() && enemies.stream().anyMatch(NPC::isAlive)) {
            displayBattleField();
            playerTurn();
            
            if (!player.isAlive() || enemies.stream().noneMatch(NPC::isAlive)) {
                break;
            }
            
            enemyTurn();
            
            // 每回合结束时的状态效果处理
            processEndOfTurnEffects();
            
            BattleAnimation.pause(1000);
        }
        
        endBattle();
    }
    
    /**
     * 显示战斗场景
     */
    private void displayBattleField() {
        BattleAnimation.clearScreen();
        
        System.out.println(CYAN + "=".repeat(80) + RESET);
        System.out.println(CYAN + "⚔️  战斗场景  ⚔️" + RESET);
        System.out.println(CYAN + "=".repeat(80) + RESET);
        
        // 显示玩家状态
        System.out.println(GREEN + "\n【我方】" + RESET);
        displayEntityStatus(player);
        
        // 显示敌人状态
        System.out.println(RED + "\n【敌方】" + RESET);
        for (int i = 0; i < enemies.size(); i++) {
            NPC enemy = enemies.get(i);
            if (enemy.isAlive()) {
                System.out.print((i + 1) + ". ");
                displayEntityStatus(enemy);
            }
        }
        
        // 显示状态效果
        displayStatusEffects();
        
        System.out.println(CYAN + "\n" + "=".repeat(80) + RESET);
    }
    
    /**
     * 显示实体状态
     */
    private void displayEntityStatus(BattleEntity entity) {
        String name = entity.getName();
        int hp = entity.getHp();
        int maxHp = entity.getMaxHp();
        
        // 计算生命值百分比
        double hpPercentage = (double) hp / maxHp;
        int hpBars = (int) (hpPercentage * 20);
        
        // 创建生命条
        String hpBar = "❤️".repeat(hpBars) + "🖤".repeat(20 - hpBars);
        
        // 显示状态信息
        System.out.printf("%-15s Lv.%-3d HP:[%-20s] %3d/%-3d ATK:%-3d DEF:%-3d%n",
            name, 
            (entity instanceof Player) ? ((Player) entity).getLevel() : ((NPC) entity).getLevel(),
            hpBar, hp, maxHp, 
            entity.getAtk() + entity.getTempAtk(), 
            entity.getDef() + entity.getTempDef()
        );
        
        // 显示元素属性
        if (entity.getElement() != null && !entity.getElement().equals("无")) {
            System.out.printf("                元素: %s | 闪避: %.0f%% | 暴击: %.0f%%%n",
                entity.getElement(),
                entity.getDodgeRate() * 100,
                entity.getCritRate() * 100
            );
        }
    }
    
    /**
     * 显示状态效果
     */
    private void displayStatusEffects() {
        System.out.println(YELLOW + "\n【状态效果】" + RESET);
        
        // 玩家状态效果
        List<StatusEffect> playerEffects = player.getStatusEffects();
        if (!playerEffects.isEmpty()) {
            System.out.print(GREEN + player.getName() + "：" + RESET);
            for (StatusEffect effect : playerEffects) {
                if (effect.isActive()) {
                    System.out.printf("[%s %d回合] ", 
                        effect.getStatus().getName(), 
                        effect.getRemainingTurns()
                    );
                }
            }
            System.out.println();
        }
        
        // 敌人状态效果
        for (NPC enemy : enemies) {
            if (enemy.isAlive() && !enemy.getStatusEffects().isEmpty()) {
                System.out.print(RED + enemy.getName() + "：" + RESET);
                for (StatusEffect effect : enemy.getStatusEffects()) {
                    if (effect.isActive()) {
                        System.out.printf("[%s %d回合] ", 
                            effect.getStatus().getName(), 
                            effect.getRemainingTurns()
                        );
                    }
                }
                System.out.println();
            }
        }
    }
    
    /**
     * 玩家回合
     */
    private void playerTurn() {
        System.out.println(BLUE + "\n【你的回合】" + RESET);
        System.out.println("1. 普通攻击");
        System.out.println("2. 魔法攻击");
        System.out.println("3. 使用物品");
        System.out.println("4. 查看状态");
        System.out.println("5. 逃跑");
        System.out.print("选择行动：");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            
            switch (choice) {
                case 1:
                    performNormalAttack();
                    break;
                case 2:
                    performMagicAttack();
                    break;
                case 3:
                    useItem();
                    break;
                case 4:
                    showDetailedStatus();
                    break;
                case 5:
                    attemptEscape();
                    break;
                default:
                    System.out.println(RED + "无效选择！" + RESET);
            }
        } catch (NumberFormatException e) {
            System.out.println(RED + "请输入有效数字！" + RESET);
        }
    }
    
    /**
     * 执行普通攻击
     */
    private void performNormalAttack() {
        NPC target = selectTarget();
        if (target != null && target.isAlive()) {
            // 播放攻击动画
            BattleAnimation.playAttackAnimation(player.getName(), target.getName());
            
            // 执行攻击
            boolean isCrit = RandomUtil.nextDouble() < player.getCritRate();
            int damage = battleEngine.calculateDamage(player, target, isCrit);
            
            if (RandomUtil.nextDouble() < target.getDodgeRate()) {
                BattleAnimation.playDodgeAnimation(player.getName(), target.getName());
            } else {
                target.takeDamage(damage);
                BattleAnimation.showDamageNumber(target.getName(), damage, isCrit);
                
                if (isCrit) {
                    BattleAnimation.playCriticalAnimation(player.getName(), target.getName());
                }
                
                if (!target.isAlive()) {
                    System.out.println(GREEN + target.getName() + " 被击败了！" + RESET);
                }
            }
        }
    }
    
    /**
     * 执行魔法攻击
     */
    private void performMagicAttack() {
        System.out.println(PURPLE + "\n选择元素魔法：" + RESET);
        System.out.println("1. 火系魔法 (高伤害)");
        System.out.println("2. 冰系魔法 (减速效果)");
        System.out.println("3. 雷系魔法 (高暴击)");
        System.out.println("4. 毒系魔法 (持续伤害)");
        System.out.print("选择魔法类型：");
        
        try {
            int magicChoice = Integer.parseInt(scanner.nextLine());
            String element = switch (magicChoice) {
                case 1 -> "fire";
                case 2 -> "ice";
                case 3 -> "lightning";
                case 4 -> "poison";
                default -> "fire";
            };
            
            NPC target = selectTarget();
            if (target != null && target.isAlive()) {
                BattleAnimation.playMagicAnimation(player.getName(), target.getName(), element);
                
                boolean isCrit = RandomUtil.nextDouble() < (player.getCritRate() + 0.2);
                int damage = battleEngine.calculateMagicDamage(player, target, element, isCrit);
                
                target.takeDamage(damage);
                BattleAnimation.showElementalEffect(element, target.getName());
                BattleAnimation.showDamageNumber(target.getName(), damage, isCrit);
                
                if (!target.isAlive()) {
                    System.out.println(GREEN + target.getName() + " 被击败了！" + RESET);
                }
            }
        } catch (NumberFormatException e) {
            System.out.println(RED + "请输入有效数字！" + RESET);
        }
    }
    
    /**
     * 选择目标
     */
    private NPC selectTarget() {
        List<NPC> aliveEnemies = enemies.stream()
            .filter(NPC::isAlive)
            .toList();
            
        if (aliveEnemies.isEmpty()) {
            return null;
        }
        
        if (aliveEnemies.size() == 1) {
            return aliveEnemies.get(0);
        }
        
        System.out.println(YELLOW + "\n选择目标：" + RESET);
        for (int i = 0; i < aliveEnemies.size(); i++) {
            System.out.println((i + 1) + ". " + aliveEnemies.get(i).getName());
        }
        System.out.print("选择目标编号：");
        
        try {
            int targetChoice = Integer.parseInt(scanner.nextLine()) - 1;
            if (targetChoice >= 0 && targetChoice < aliveEnemies.size()) {
                return aliveEnemies.get(targetChoice);
            }
        } catch (NumberFormatException e) {
            System.out.println(RED + "请输入有效数字！" + RESET);
        }
        
        return aliveEnemies.get(0); // 默认选择第一个
    }
    
    /**
     * 使用物品
     */
    private void useItem() {
        System.out.println(YELLOW + "\n【物品栏】" + RESET);
        Map<String, Integer> items = player.getItems();
        
        if (items.isEmpty()) {
            System.out.println(RED + "背包为空！" + RESET);
            return;
        }
        
        List<String> itemList = new ArrayList<>(items.keySet());
        for (int i = 0; i < itemList.size(); i++) {
            String item = itemList.get(i);
            System.out.println((i + 1) + ". " + item + " (x" + items.get(item) + ")");
        }
        System.out.println("0. 返回");
        System.out.print("选择要使用的物品：");
        
        try {
            int itemChoice = Integer.parseInt(scanner.nextLine());
            if (itemChoice == 0) return;
            
            if (itemChoice > 0 && itemChoice <= itemList.size()) {
                String selectedItem = itemList.get(itemChoice - 1);
                battleEngine.useItem(player, selectedItem);
                
                // 播放使用物品动画
                if (selectedItem.contains("药") || selectedItem.contains("治疗")) {
                    BattleAnimation.playHealAnimation(player.getName(), player.getName(), 50);
                }
            }
        } catch (NumberFormatException e) {
            System.out.println(RED + "请输入有效数字！" + RESET);
        }
    }
    
    /**
     * 显示详细状态
     */
    private void showDetailedStatus() {
        System.out.println(BLUE + "\n【详细状态】" + RESET);
        System.out.println("角色：" + player.getName());
        System.out.println("等级：" + player.getLevel());
        System.out.println("生命值：" + player.getHp() + "/" + player.getMaxHp());
        System.out.println("攻击力：" + player.getAtk() + " (临时+" + player.getTempAtk() + ")");
        System.out.println("防御力：" + player.getDef() + " (临时+" + player.getTempDef() + ")");
        System.out.println("元素：" + player.getElement());
        System.out.println("闪避率：" + (player.getDodgeRate() * 100) + "%");
        System.out.println("暴击率：" + (player.getCritRate() * 100) + "%");
        System.out.println("暴击伤害：" + (player.getCritDamage() * 100) + "%");
        
        System.out.print("\n按回车键继续...");
        scanner.nextLine();
    }
    
    /**
     * 尝试逃跑
     */
    private void attemptEscape() {
        System.out.println(YELLOW + "\n尝试逃跑..." + RESET);
        
        if (RandomUtil.isTriggered(0.5)) {
            System.out.println(GREEN + "逃跑成功！" + RESET);
            inBattle = false;
        } else {
            System.out.println(RED + "逃跑失败！" + RESET);
        }
        
        BattleAnimation.pause(1000);
    }
    
    /**
     * 敌人回合
     */
    private void enemyTurn() {
        System.out.println(RED + "\n【敌人回合】" + RESET);
        
        for (NPC enemy : enemies) {
            if (enemy.isAlive()) {
                BattleAnimation.pause(800);
                
                // 敌人AI选择行动
            if (RandomUtil.isTriggered(0.8)) { // 80%概率攻击
                    performEnemyAttack(enemy);
                } else {
                    System.out.println(enemy.getName() + " 正在准备...");
                }
            }
        }
    }
    
    /**
     * 执行敌人攻击
     */
    private void performEnemyAttack(NPC enemy) {
        BattleAnimation.playAttackAnimation(enemy.getName(), player.getName());
        
        boolean isCrit = RandomUtil.isTriggered(0.1); // 敌人10%暴击率
        int damage = battleEngine.calculateDamage(enemy, player, isCrit);
        
        if (RandomUtil.nextDouble() < player.getDodgeRate()) {
            BattleAnimation.playDodgeAnimation(enemy.getName(), player.getName());
        } else {
            player.takeDamage(damage);
            BattleAnimation.showDamageNumber(player.getName(), damage, isCrit);
            
            if (isCrit) {
                System.out.println(RED + "暴击！" + RESET);
            }
            
            if (!player.isAlive()) {
                System.out.println(RED + player.getName() + " 被击败了！" + RESET);
            }
        }
    }
    
    /**
     * 处理回合结束效果
     */
    private void processEndOfTurnEffects() {
        // 处理玩家状态效果
        List<StatusEffect> playerEffects = new ArrayList<>(player.getStatusEffects());
        for (StatusEffect effect : playerEffects) {
            if (effect.isActive()) {
                effect.endTurn();
                
                if (effect.getStatus().getName().contains("毒")) {
                    BattleAnimation.playStatusEffectAnimation(player.getName(), "poison", false);
                }
                
                if (!effect.isActive()) {
                    player.removeStatusEffect(effect);
                    System.out.println(YELLOW + player.getName() + " 的 " + effect.getStatus().getName() + " 效果结束了" + RESET);
                }
            }
        }
        
        // 处理敌人状态效果
        for (NPC enemy : enemies) {
            if (enemy.isAlive()) {
                List<StatusEffect> enemyEffects = new ArrayList<>(enemy.getStatusEffects());
                for (StatusEffect effect : enemyEffects) {
                    if (effect.isActive()) {
                        effect.endTurn();
                        
                        if (!effect.isActive()) {
                            enemy.removeStatusEffect(effect);
                            System.out.println(YELLOW + enemy.getName() + " 的 " + effect.getStatus().getName() + " 效果结束了" + RESET);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 结束战斗
     */
    private void endBattle() {
        if (player.isAlive() && enemies.stream().noneMatch(NPC::isAlive)) {
            BattleAnimation.playVictoryAnimation();
            
            // 计算奖励
            int totalExp = enemies.stream().mapToInt(NPC::getExpReward).sum();
            int totalGold = enemies.stream().mapToInt(NPC::getGoldReward).sum();
            
            System.out.println(GREEN + "获得经验值：" + totalExp + RESET);
            System.out.println(GREEN + "获得金币：" + totalGold + RESET);
            
            player.gainExp(totalExp);
            player.gainGold(totalGold);
            
        } else if (!player.isAlive()) {
            BattleAnimation.playDefeatAnimation();
            System.out.println(RED + "你被击败了！游戏结束..." + RESET);
        }
        
        // 重置临时属性
        player.setTempAtk(0);
        player.setTempDef(0);
        
        System.out.print("\n按回车键继续...");
        scanner.nextLine();
    }
}