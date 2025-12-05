package com.mud.game.system;

import java.util.Random;
import java.util.List;
import java.util.Arrays;

/**
 * 随机工具类
 * 封装游戏中所有的随机行为操作，提供统一的随机数生成和随机选择功能
 */
public class RandomUtil {
    
    // 单例Random实例，避免重复创建Random对象
    private static final Random RANDOM = new Random();
    
    /**
     * 获取[0,1)之间的随机浮点数
     * 替代Math.random()
     */
    public static double nextDouble() {
        return RANDOM.nextDouble();
    }
    
    /**
     * 获取[0,bound)之间的随机整数
     * @param bound 上限（不包含）
     */
    public static int nextInt(int bound) {
        return RANDOM.nextInt(bound);
    }
    
    /**
     * 获取[min,max)之间的随机整数
     * @param min 最小值（包含）
     * @param max 最大值（不包含）
     */
    public static int nextInt(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("最小值必须小于最大值");
        }
        return min + RANDOM.nextInt(max - min);
    }
    
    /**
     * 随机选择数组中的一个元素
     * @param array 元素数组
     * @return 随机选中的元素，如果数组为空返回null
     */
    public static <T> T randomElement(T[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        return array[RANDOM.nextInt(array.length)];
    }
    
    /**
     * 随机选择列表中的一个元素
     * @param list 元素列表
     * @return 随机选中的元素，如果列表为空返回null
     */
    public static <T> T randomElement(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(RANDOM.nextInt(list.size()));
    }
    
    /**
     * 根据概率判断是否触发某事件
     * @param probability 触发概率，范围[0,1]
     * @return 是否触发
     */
    public static boolean isTriggered(double probability) {
        if (probability < 0 || probability > 1) {
            throw new IllegalArgumentException("概率必须在0到1之间");
        }
        return RANDOM.nextDouble() < probability;
    }
    
    /**
     * 根据权重随机选择索引
     * @param weights 权重数组，长度不能为0
     * @return 选中的索引
     */
    public static int randomByWeight(double[] weights) {
        if (weights == null || weights.length == 0) {
            throw new IllegalArgumentException("权重数组不能为空");
        }
        
        // 计算总权重
        double totalWeight = Arrays.stream(weights).sum();
        if (totalWeight <= 0) {
            throw new IllegalArgumentException("权重总和必须大于0");
        }
        
        // 随机生成一个0到总权重之间的值
        double randomValue = RANDOM.nextDouble() * totalWeight;
        
        // 根据权重选择索引
        double currentWeight = 0;
        for (int i = 0; i < weights.length; i++) {
            currentWeight += weights[i];
            if (randomValue < currentWeight) {
                return i;
            }
        }
        
        // 理论上不应该到达这里，但为了安全返回最后一个索引
        return weights.length - 1;
    }
    
    /**
     * 获取随机的布尔值
     * @return true或false
     */
    public static boolean nextBoolean() {
        return RANDOM.nextBoolean();
    }
    
    /**
     * 设置随机数种子，用于测试时复现特定随机结果
     * @param seed 随机数种子
     */
    public static void setSeed(long seed) {
        RANDOM.setSeed(seed);
    }
}