package com.mud.game.ui;

import java.util.*;
import com.mud.game.system.RandomUtil;

/**
 * 战斗动画和特效显示系统
 * 提供战斗中的各种视觉效果和动画
 */
public class BattleAnimation {
    private static final Map<String, String[]> ANIMATIONS = new HashMap<>();
    private static final Map<String, String> EFFECT_COLORS = new HashMap<>();
    
    static {
        // 初始化动画帧
        ANIMATIONS.put("attack", new String[]{
            "⚔️  ",
            "⚔️⚡", 
            "💥⚔️",
            "💥💥"
        });
        
        ANIMATIONS.put("magic", new String[]{
            "✨  ",
            "✨⭐",
            "⭐✨",
            "🌟💫"
        });
        
        ANIMATIONS.put("heal", new String[]{
            "💚  ",
            "💚✨",
            "💖✨",
            "💗🌟"
        });
        
        ANIMATIONS.put("poison", new String[]{
            "💚  ",
            "💚💀",
            "💀💚",
            "💀💀"
        });
        
        ANIMATIONS.put("critical", new String[]{
            "💥  ",
            "💥⚡",
            "⚡💥",
            "💥💥💥"
        });
        
        ANIMATIONS.put("dodge", new String[]{
            "🏃  ",
            "🏃💨",
            "💨🏃",
            "💨💨"
        });
        
        // 效果颜色映射
        EFFECT_COLORS.put("fire", "\u001B[31m");     // 红色
        EFFECT_COLORS.put("ice", "\u001B[34m");      // 蓝色
        EFFECT_COLORS.put("lightning", "\u001B[33m"); // 黄色
        EFFECT_COLORS.put("poison", "\u001B[32m");   // 绿色
        EFFECT_COLORS.put("heal", "\u001B[35m");     // 紫色
        EFFECT_COLORS.put("physical", "\u001B[37m"); // 白色
        EFFECT_COLORS.put("reset", "\u001B[0m");     // 重置颜色
    }
    
    /**
     * 播放攻击动画
     */
    public static void playAttackAnimation(String attacker, String target) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println(attacker + " 对 " + target + " 发动攻击！");
        
        String[] frames = ANIMATIONS.get("attack");
        for (int i = 0; i < frames.length; i++) {
            System.out.print("\r" + frames[i] + " " + attacker + " → " + target);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println();
    }
    
    /**
     * 播放魔法攻击动画
     */
    public static void playMagicAnimation(String caster, String target, String element) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println(caster + " 施展 " + element + " 魔法攻击 " + target + "！");
        
        String color = EFFECT_COLORS.getOrDefault(element, EFFECT_COLORS.get("reset"));
        String reset = EFFECT_COLORS.get("reset");
        
        String[] frames = ANIMATIONS.get("magic");
        for (int i = 0; i < frames.length; i++) {
            System.out.print("\r" + color + frames[i] + " " + caster + " → " + target + reset);
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println();
    }
    
    /**
     * 播放治疗动画
     */
    public static void playHealAnimation(String healer, String target, int amount) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println(healer + " 治疗 " + target + " 恢复了 " + amount + " 点生命值！");
        
        String color = EFFECT_COLORS.get("heal");
        String reset = EFFECT_COLORS.get("reset");
        
        String[] frames = ANIMATIONS.get("heal");
        for (int i = 0; i < frames.length; i++) {
            System.out.print("\r" + color + frames[i] + " " + target + " +" + amount + " HP" + reset);
            try {
                Thread.sleep(350);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println();
    }
    
    /**
     * 播放暴击动画
     */
    public static void playCriticalAnimation(String attacker, String target) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("💥 暴击！" + attacker + " 对 " + target + " 造成暴击伤害！");
        
        String[] frames = ANIMATIONS.get("critical");
        for (int i = 0; i < frames.length; i++) {
            System.out.print("\r" + frames[i] + " 暴击伤害！");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println();
    }
    
    /**
     * 播放闪避动画
     */
    public static void playDodgeAnimation(String attacker, String target) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println(target + " 闪避了 " + attacker + " 的攻击！");
        
        String[] frames = ANIMATIONS.get("dodge");
        for (int i = 0; i < frames.length; i++) {
            System.out.print("\r" + frames[i] + " 闪避成功！");
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println();
    }
    
    /**
     * 播放状态效果动画
     */
    public static void playStatusEffectAnimation(String target, String effectType, boolean isPositive) {
        System.out.println("\n" + "=".repeat(50));
        
        if ("poison".equals(effectType)) {
            String[] frames = ANIMATIONS.get("poison");
            System.out.println(target + " 受到中毒效果影响！");
            
            String color = EFFECT_COLORS.get("poison");
            String reset = EFFECT_COLORS.get("reset");
            
            for (int i = 0; i < frames.length; i++) {
                System.out.print("\r" + color + frames[i] + " 中毒伤害！" + reset);
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } else {
            String symbol = isPositive ? "✨" : "💀";
            String message = isPositive ? 
                target + " 获得增益效果！" : target + " 受到负面效果影响！";
            
            System.out.println(message);
            for (int i = 0; i < 4; i++) {
                System.out.print("\r" + symbol.repeat(i + 1) + " " + effectType);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        System.out.println();
    }
    
    /**
     * 显示伤害数字特效
     */
    public static void showDamageNumber(String target, int damage, boolean isCritical) {
        String color = isCritical ? "\u001B[31m" : "\u001B[33m"; // 暴击红色，普通黄色
        String reset = EFFECT_COLORS.get("reset");
        
        if (isCritical) {
            System.out.println(color + "💥 " + target + " 受到 " + damage + " 点暴击伤害！" + reset);
        } else {
            System.out.println(color + "⚔️ " + target + " 受到 " + damage + " 点伤害！" + reset);
        }
    }
    
    /**
     * 显示元素特效
     */
    public static void showElementalEffect(String element, String target) {
        String color = EFFECT_COLORS.getOrDefault(element, EFFECT_COLORS.get("reset"));
        String reset = EFFECT_COLORS.get("reset");
        
        Map<String, String> elementSymbols = new HashMap<>();
        elementSymbols.put("fire", "🔥");
        elementSymbols.put("ice", "❄️");
        elementSymbols.put("lightning", "⚡");
        elementSymbols.put("poison", "☠️");
        elementSymbols.put("physical", "💥");
        
        String symbol = elementSymbols.getOrDefault(element, "✨");
        System.out.println(color + symbol + " " + element + " 元素效果对 " + target + " 生效！" + reset);
    }
    
    /**
     * 播放战斗开始动画
     */
    public static void playBattleStartAnimation(List<String> enemies) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("⚔️  战斗开始！⚔️");
        System.out.println("敌人出现：" + String.join(", ", enemies));
        
        for (int i = 0; i < 3; i++) {
            System.out.print("\r准备战斗" + ".".repeat(i + 1));
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("\n" + "=".repeat(60));
    }
    
    /**
     * 播放战斗胜利动画
     */
    public static void playVictoryAnimation() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎉 战斗胜利！🎉");
        
        String[] celebration = {"🎊", "🎉", "⭐", "🏆", "✨"};
        for (int i = 0; i < 10; i++) {
            String frame = RandomUtil.randomElement(celebration);
            System.out.print("\r" + frame.repeat(i + 1) + " 胜利！" + frame.repeat(i + 1));
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("\n" + "=".repeat(60));
    }
    
    /**
     * 播放战斗失败动画
     */
    public static void playDefeatAnimation() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("💀 战斗失败... 💀");
        
        for (int i = 0; i < 5; i++) {
            System.out.print("\r" + "💀".repeat(i + 1) + " 战败... " + "💀".repeat(i + 1));
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("\n" + "=".repeat(60));
    }
    
    /**
     * 显示战斗状态栏
     */
    public static void showBattleStatus(String name, int currentHp, int maxHp, int mp, int maxMp) {
        int hpBars = (int) ((currentHp / (double) maxHp) * 10);
        int mpBars = (int) ((mp / (double) maxMp) * 10);
        
        String hpBar = "❤️".repeat(Math.max(0, hpBars)) + "🖤".repeat(Math.max(0, 10 - hpBars));
        String mpBar = "💙".repeat(Math.max(0, mpBars)) + "🖤".repeat(Math.max(0, 10 - mpBars));
        
        System.out.printf("%-15s HP:[%s] %3d/%-3d MP:[%s] %3d/%-3d%n", 
            name, hpBar, currentHp, maxHp, mpBar, mp, maxMp);
    }
    
    /**
     * 清屏方法
     */
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    
    /**
     * 暂停动画
     */
    public static void pause(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}