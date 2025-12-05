package com.mud.game.system;

import com.mud.game.entity.Player;
import com.mud.game.entity.NPC;
import com.mud.game.entity.Item;
import java.util.List;
import java.util.Scanner;
import com.mud.game.system.RandomUtil;

public class BattleEngine {
    private Scanner scanner;
    
    public BattleEngine() {
        this.scanner = new Scanner(System.in);
    }
    
    public void startBattle(Player player, NPC enemy) {
        System.out.println("\n⚔️  战斗开始！你遇到了 " + enemy.getName() + "！");
        
        // 战斗主循环
        while (player.getHp() > 0 && enemy.getHp() > 0) {
            displayBattleStatus(player, enemy);
            
            // 玩家回合 - 选择行动
            int action = getPlayerAction();
            
            switch (action) {
                case 1: // 攻击
                    playerAttack(player, enemy);
                    break;
                    
                case 2: // 防御
                    playerDefend(player);
                    break;
                    
                case 3: // 使用物品
                    if (!useItem(player)) {
                        // 如果物品使用失败，跳过敌人回合
                        continue;
                    }
                    break;
                    
                case 4: // 逃跑
                    if (tryEscape(player, enemy)) {
                        return; // 成功逃跑，结束战斗
                    } else {
                        System.out.println("逃跑失败！");
                        // 逃跑失败，敌人获得额外攻击机会
                    }
                    break;
            }
            
            // 检查敌人是否存活
            if (enemy.getHp() <= 0) {
                break;
            }
            
            // 敌人回合
            System.out.println("\n" + enemy.getName() + "的回合：");
            enemyAttack(enemy, player);
            
            if (player.getHp() <= 0) {
                break;
            }
            
            // 回合间隔
            try {
                Thread.sleep(1500); // 1.5秒延迟增加紧张感
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // 战斗结果处理
        if (player.getHp() > 0) {
            handleVictory(player, enemy);
        } else {
            handleDefeat(player);
        }
    }
    
    private void displayBattleStatus(Player player, NPC enemy) {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.printf("║ 🗡️  战斗状态 - %s VS %s  ║%n", 
                         player.getName(), enemy.getName());
        System.out.println("╠════════════════════════════════════╣");
        
        // 显示玩家状态
        String playerHpBar = createHpBar(player.getHp(), player.getMaxHp(), 20);
        System.out.printf("║ 👤 你: %-20s ║%n", player.getName());
        System.out.printf("║    HP: [%s] %3d/%3d    ║%n", playerHpBar, player.getHp(), player.getMaxHp());
        System.out.printf("║    ATK: %-3d  DEF: %-3d          ║%n", player.getAtk(), player.getDef());
        System.out.println("╠════════════════════════════════════╣");
        
        // 显示敌人状态
        String enemyHpBar = createHpBar(enemy.getHp(), 50, 20); // 假设敌人最大HP为50
        System.out.printf("║ 👹 敌人: %-18s ║%n", enemy.getName());
        System.out.printf("║    HP: [%s] %3d/50     ║%n", enemyHpBar, enemy.getHp());
        System.out.printf("║    ATK: %-3d                        ║%n", enemy.getAtk());
        System.out.println("╚════════════════════════════════════╝");
    }
    
    private String createHpBar(int current, int max, int length) {
        double percentage = (double) current / max;
        int filled = (int) (length * percentage);
        int empty = length - filled;
        
        StringBuilder bar = new StringBuilder();
        bar.append("█".repeat(Math.max(0, filled)));
        bar.append("░".repeat(Math.max(0, empty)));
        
        return bar.toString();
    }
    
    private int getPlayerAction() {
        while (true) {
            System.out.println("\n请选择你的行动：");
            System.out.println("1. 攻击");
            System.out.println("2. 防御");
            System.out.println("3. 使用物品");
            System.out.println("4. 逃跑");
            System.out.print("> ");
            
            try {
                String input = scanner.nextLine().trim();
                int choice = Integer.parseInt(input);
                
                if (choice >= 1 && choice <= 4) {
                    return choice;
                } else {
                    System.out.println("请输入1-4之间的数字！");
                }
            } catch (NumberFormatException e) {
                System.out.println("请输入有效的数字！");
            }
        }
    }
    
    private void playerDefend(Player player) {
        System.out.println("你采取了防御姿态！");
        player.setDef(player.getDef() + 2); // 临时提升防御
        System.out.println("防御力临时提升！");
        
        // 下一回合开始时恢复原始防御力
        new Thread(() -> {
            try {
                Thread.sleep(2000); // 2秒后恢复
                player.setDef(player.getDef() - 2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    private boolean useItem(Player player) {
        List<Item> items = player.getBackpack();
        if (items.isEmpty()) {
            System.out.println("背包是空的！");
            return false;
        }
        
        System.out.println("\n=== 可用物品 ===");
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + items.get(i).getName() + " - " + items.get(i).getDescription());
        }
        System.out.println("0. 取消");
        System.out.print("选择要使用的物品：");
        
        try {
            String input = scanner.nextLine().trim();
            int choice = Integer.parseInt(input);
            
            if (choice == 0) {
                return false;
            }
            
            if (choice > 0 && choice <= items.size()) {
                Item selectedItem = items.get(choice - 1);
                return useItemInBattle(player, selectedItem);
            } else {
                System.out.println("无效的选择！");
                return false;
            }
        } catch (NumberFormatException e) {
            System.out.println("请输入有效的数字！");
            return false;
        }
    }
    
    private boolean useItemInBattle(Player player, Item item) {
        switch (item.getType()) {
            case MEDICINE:
                // 治疗物品
                System.out.println("\n💊 使用物品：" + item.getName());
                System.out.println("✨ " + item.getDescription());
                
                int healAmount = (int)Math.round(item.getEffect());
                int oldHp = player.getHp();
                int newHp = Math.min(player.getHp() + healAmount, player.getMaxHp());
                player.setHp(newHp);
                
                System.out.println("❤️ 生命值恢复：" + oldHp + " → " + newHp + " (+" + healAmount + ")");
                
                // 治疗特效
                System.out.print("🌟 治疗特效：");
                for (int i = 0; i < 5; i++) {
                    System.out.print("✨ ");
                    try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                }
                System.out.println();
                
                player.getBackpack().remove(item);
                return true;
                
            case WEAPON:
                // 武器（临时提升攻击力）
                System.out.println("\n⚔️ 装备武器：" + item.getName());
                System.out.println("🔥 " + item.getDescription());
                
                int oldAtk = player.getAtk();
                player.setAtk(player.getAtk() + (int)Math.round(item.getEffect()));
                
                System.out.println("💪 攻击力提升：" + oldAtk + " → " + player.getAtk() + " (+" + item.getEffect() + ")");
                
                // 武器特效
                System.out.print("⚡ 武器特效：");
                for (int i = 0; i < 5; i++) {
                    System.out.print("💥 ");
                    try { Thread.sleep(150); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                }
                System.out.println();
                
                // 武器使用后不消耗，可以重复使用
                return true;
                
            default:
                System.out.println("❌ 这个物品在战斗中无法使用！");
                return false;
        }
    }
    
    private void playerAttack(Player player, NPC enemy) {
        System.out.println("\n⚔️ 你的回合：");
        
        // 攻击动画效果
        String[] attackEffects = {
            "🗡️ 你挥舞武器冲向敌人！",
            "⚡ 一道寒光闪过！",
            "💥 命中目标！"
        };
        
        for (String effect : attackEffects) {
            System.out.println(effect);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        player.performAttack(enemy);
        
        if (enemy.getHp() > 0) {
            System.out.println("🩸 " + enemy.getName() + "剩余生命值：" + enemy.getHp());
        }
    }
    
    private void enemyAttack(NPC enemy, Player player) {
        System.out.println("\n👹 " + enemy.getName() + "的回合：");
        
        // 敌人攻击选择（简单AI）
            int attackType = RandomUtil.nextInt(100);
        
        if (attackType < 70) { // 70% 概率普通攻击
            System.out.println("🐺 " + enemy.getName() + "扑了过来！");
            try { Thread.sleep(300); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            
            int baseDamage = Math.max(1, enemy.getAtk() - 3);
            int randomFactor = RandomUtil.nextInt(1, 6); // 1-5的随机因子
            int damage = baseDamage + randomFactor;
            
            // 考虑玩家防御
            damage = Math.max(1, damage - player.getDef() / 2);
            
            player.setHp(player.getHp() - damage);
            System.out.println("💥 " + enemy.getName() + "对你造成了" + damage + "点伤害！");
            
        } else if (attackType < 85) { // 15% 概率强力攻击
            System.out.println("⚠️ " + enemy.getName() + "开始蓄力...");
            try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            System.out.println("🔥 强力攻击！");
            
            int damage = Math.max(2, (enemy.getAtk() * 3 / 2) - player.getDef() / 2);
            player.setHp(player.getHp() - damage);
            System.out.println("💥 " + enemy.getName() + "的强力攻击对你造成了" + damage + "点伤害！");
            
        } else { // 15% 概率特殊效果
            System.out.println("✨ " + enemy.getName() + "正在聚集能量...");
            try { Thread.sleep(700); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            System.out.println("⚡ 特殊技能释放！");
            
            // 特殊效果：降低玩家防御
            int defenseReduction = 2;
            player.setDef(Math.max(0, player.getDef() - defenseReduction));
            System.out.println("🛡️ 你的防御力降低了" + defenseReduction + "点！");
        }
        
        if (player.getHp() > 0) {
            System.out.println("❤️ 你剩余生命值：" + player.getHp() + "/" + player.getMaxHp());
        }
    }
    
    private void handleVictory(Player player, NPC enemy) {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║           🎉 胜利！🎉             ║");
        System.out.println("╠════════════════════════════════════╣");
        System.out.printf("║ 你击败了 %s！%n", enemy.getName());
        
        // 奖励（基于敌人攻击力计算）
        int expGain = enemy.getAtk() * 2;
        int moneyGain = enemy.getAtk() * 1;
        
        player.gainExperience(expGain);
        player.setMoney(player.getMoney() + moneyGain);
        
        System.out.println("╠════════════════════════════════════╣");
        System.out.printf("║ 💰 获得金币：%d%n", moneyGain);
        System.out.printf("║ ⭐ 获得经验：%d%n", expGain);
        System.out.printf("║ 💎 总金币：%d%n", player.getMoney());
        System.out.printf("║ 📊 总经验：%d%n", player.getExperience());
        System.out.println("╚════════════════════════════════════╝");
        
        // 敌人死亡处理
        enemy.setAlive(false);
        enemy.setHp(0);
        
        // 更新任务进度
        player.updateTaskProgress("kill", enemy.getName(), 1);
        
        // 胜利音效（模拟）
        System.out.println("\n🎵 胜利的音乐响起...");
    }
    
    private void handleDefeat(Player player) {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║           💀 失败！💀             ║");
        System.out.println("╠════════════════════════════════════╣");
        System.out.println("║ 你被敌人击败了！                 ║");
        System.out.println("║ 不要气馁，重新振作起来！         ║");
        System.out.println("╚════════════════════════════════════╝");
        System.out.println("\n🎵 悲伤的音乐响起...");
        System.out.println("\n游戏结束！");
        System.exit(0);
    }
    
    // 逃跑机制
    public boolean tryEscape(Player player, NPC enemy) {
        System.out.println("你尝试逃跑...");
        
        // 基于玩家和敌人的属性计算逃跑成功率
        int escapeChance = 50; // 基础逃跑率50%
        escapeChance += (player.getDef() - enemy.getAtk()) * 2; // 防御优势增加逃跑率
        escapeChance = Math.max(20, Math.min(80, escapeChance)); // 限制在20%-80%之间
        
        if (RandomUtil.isTriggered(escapeChance / 100.0)) {
            System.out.println("逃跑成功！");
            return true;
        } else {
            System.out.println("逃跑失败！" + enemy.getName() + "追上了你！");
            enemyAttack(enemy, player);
            return false;
        }
    }
}