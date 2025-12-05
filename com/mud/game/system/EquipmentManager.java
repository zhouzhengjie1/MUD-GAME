package com.mud.game.system;

import com.mud.game.entity.*;
import java.util.*;

/**
 * 装备强化系统管理器
 * 管理装备的强化、修复、商店等功能
 */
public class EquipmentManager {
    private Map<String, Equipment> equipmentTemplates;  // 装备模板
    private Map<String, Equipment> playerEquipment;   // 玩家装备
    
    public EquipmentManager() {
        this.equipmentTemplates = new HashMap<>();
        this.playerEquipment = new HashMap<>();
        initializeEquipmentTemplates();
    }
    
    /**
     * 初始化装备模板
     */
    private void initializeEquipmentTemplates() {
        // 武器类装备
        addEquipmentTemplate(new Equipment("铁剑", "基础铁剑，适合新手使用", 5, ItemType.WEAPON, EquipmentGrade.COMMON, 10));
        addEquipmentTemplate(new Equipment("钢剑", "精制钢剑，更加锋利", 8, ItemType.WEAPON, EquipmentGrade.UNCOMMON, 10));
        addEquipmentTemplate(new Equipment("银剑", "银制长剑，蕴含魔法力量", 12, ItemType.WEAPON, EquipmentGrade.RARE, 10));
        addEquipmentTemplate(new Equipment("炎之剑", "火焰附魔的宝剑", 18, ItemType.WEAPON, EquipmentGrade.EPIC, 10));
        addEquipmentTemplate(new Equipment("龙牙剑", "用巨龙牙齿打造的传说武器", 25, ItemType.WEAPON, EquipmentGrade.LEGENDARY, 10));
        addEquipmentTemplate(new Equipment("神使之刃", "神话级别的神器", 35, ItemType.WEAPON, EquipmentGrade.MYTHIC, 10));
        
        // 护甲类装备
        addEquipmentTemplate(new Equipment("布衣", "基础布衣，提供少量防护", 3, ItemType.ARMOR, EquipmentGrade.COMMON, 10));
        addEquipmentTemplate(new Equipment("皮甲", "结实皮甲，更好的防护", 6, ItemType.ARMOR, EquipmentGrade.UNCOMMON, 10));
        addEquipmentTemplate(new Equipment("链甲", "金属链甲，优秀的防护", 10, ItemType.ARMOR, EquipmentGrade.RARE, 10));
        addEquipmentTemplate(new Equipment("板甲", "全身板甲，极强的防护", 15, ItemType.ARMOR, EquipmentGrade.EPIC, 10));
        addEquipmentTemplate(new Equipment("龙鳞甲", "巨龙鳞片制作的传说护甲", 22, ItemType.ARMOR, EquipmentGrade.LEGENDARY, 10));
        addEquipmentTemplate(new Equipment("神圣战甲", "神话级别的圣甲", 30, ItemType.ARMOR, EquipmentGrade.MYTHIC, 10));
    }
    
    /**
     * 添加装备模板
     */
    private void addEquipmentTemplate(Equipment equipment) {
        equipmentTemplates.put(equipment.getName(), equipment);
    }
    
    /**
     * 创建装备实例（随机品质）
     */
    public Equipment createEquipment(String name) {
        Equipment template = equipmentTemplates.get(name);
        if (template == null) {
            return null;
        }
        
        // 随机生成品质
        EquipmentGrade randomGrade = EquipmentGrade.getRandomGrade();
        
        return new Equipment(
            template.getName(),
            template.getDescription(),
            template.getBaseEffect(),
            template.getType(),
            randomGrade,
            template.getMaxLevel()
        );
    }
    
    /**
     * 创建装备实例（指定品质）
     */
    public Equipment createEquipment(String name, EquipmentGrade grade) {
        Equipment template = equipmentTemplates.get(name);
        if (template == null) {
            return null;
        }
        
        return new Equipment(
            template.getName(),
            template.getDescription(),
            template.getBaseEffect(),
            template.getType(),
            grade,
            template.getMaxLevel()
        );
    }
    
    /**
     * 强化装备
     */
    public UpgradeResult upgradeEquipment(Equipment equipment, Player player) {
        // 检查金币
        if (player.getMoney() < equipment.getUpgradeCost()) {
            return new UpgradeResult(false, 
                String.format("❌ 金币不足！需要 %d 金币，当前只有 %d 金币", 
                    equipment.getUpgradeCost(), player.getMoney()));
        }
        
        // 检查等级上限
        if (equipment.getLevel() >= equipment.getMaxLevel()) {
            return new UpgradeResult(false, "❌ 装备已达到最高等级！");
        }
        
        // 检查耐久度
        if (equipment.getDurability() <= 0) {
            return new UpgradeResult(false, "❌ 装备耐久度不足，请先修复！");
        }
        
        // 扣除金币
        player.setMoney(player.getMoney() - equipment.getUpgradeCost());
        
        // 执行强化
        return equipment.upgrade(player);
    }
    
    /**
     * 修复装备
     */
    public RepairResult repairEquipment(Equipment equipment, Player player) {
        int repairCost = equipment.getRepairCost();
        
        // 检查金币
        if (player.getMoney() < repairCost) {
            return new RepairResult(false, 
                String.format("❌ 金币不足！修复需要 %d 金币，当前只有 %d 金币", 
                    repairCost, player.getMoney()));
        }
        
        // 检查是否需要修复
        if (equipment.getDurability() >= equipment.getMaxDurability()) {
            return new RepairResult(false, "❌ 装备耐久度已满，无需修复！");
        }
        
        // 扣除金币并修复
        player.setMoney(player.getMoney() - repairCost);
        return equipment.repair(player);
    }
    
    /**
     * 显示装备商店
     */
    public void showEquipmentShop(Player player) {
        System.out.println("\n=== ⚒️ 铁匠铺 ===");
        System.out.println("欢迎来到铁匠铺！这里有各种装备和强化服务。");
        System.out.println("当前金币：" + player.getMoney() + "\n");
        
        System.out.println("【可购买装备】");
        List<Equipment> shopEquipment = getShopEquipment();
        
        for (int i = 0; i < shopEquipment.size(); i++) {
            Equipment equip = shopEquipment.get(i);
            int price = calculateEquipmentPrice(equip);
            System.out.printf("%d. %s - %d 金币\n", 
                           (i + 1), equip.getDetailedInfo(), price);
        }
        
        System.out.println("\n【强化服务】");
        System.out.println("U. 强化装备 - 提升装备等级（费用根据装备而定）");
        System.out.println("R. 修复装备 - 恢复装备耐久度（费用根据耐久损失而定）");
        System.out.println("I. 查看背包装备");
        System.out.println("Q. 离开铁匠铺");
        System.out.print("\n请选择：");
    }
    
    /**
     * 获取商店装备列表
     */
    private List<Equipment> getShopEquipment() {
        List<Equipment> shopList = new ArrayList<>();
        
        // 基础装备（普通品质）
        shopList.add(createEquipment("铁剑", EquipmentGrade.COMMON));
        shopList.add(createEquipment("布衣", EquipmentGrade.COMMON));
        shopList.add(createEquipment("钢剑", EquipmentGrade.UNCOMMON));
        shopList.add(createEquipment("皮甲", EquipmentGrade.UNCOMMON));
        
        // 随机高级装备（小概率出现）
        if (RandomUtil.isTriggered(0.3)) {
            shopList.add(createEquipment("银剑", EquipmentGrade.RARE));
        }
        if (RandomUtil.isTriggered(0.2)) {
            shopList.add(createEquipment("链甲", EquipmentGrade.RARE));
        }
        if (RandomUtil.isTriggered(0.1)) {
            shopList.add(createEquipment("炎之剑", EquipmentGrade.EPIC));
        }
        
        return shopList;
    }
    
    /**
     * 计算装备价格
     */
    private int calculateEquipmentPrice(Equipment equipment) {
        double basePrice = equipment.getBaseEffect() * 50;
        double gradeMultiplier = equipment.getGrade().getEffectMultiplier();
        return (int) (basePrice * gradeMultiplier);
    }
    
    /**
     * 购买装备
     */
    public boolean buyEquipment(Equipment equipment, Player player) {
        int price = calculateEquipmentPrice(equipment);
        
        if (player.getMoney() < price) {
            System.out.println("❌ 金币不足！需要 " + price + " 金币");
            return false;
        }
        
        player.setMoney(player.getMoney() - price);
        player.addItem(equipment);
        System.out.println("✅ 成功购买 " + equipment.getName() + "！");
        return true;
    }
    
    /**
     * 显示玩家所有装备
     */
    public void showPlayerEquipment(Player player) {
        System.out.println("\n=== 🎒 装备背包 ===");
        
        List<Equipment> playerEquipments = new ArrayList<>();
        for (Item item : player.getBackpack()) {
            if (item instanceof Equipment) {
                playerEquipments.add((Equipment) item);
            }
        }
        
        if (playerEquipments.isEmpty()) {
            System.out.println("背包中没有装备！");
            return;
        }
        
        for (int i = 0; i < playerEquipments.size(); i++) {
            Equipment equip = playerEquipments.get(i);
            System.out.println((i + 1) + ". " + equip.getDetailedInfo());
        }
    }
    
    /**
     * 获取玩家装备列表
     */
    public List<Equipment> getPlayerEquipmentList(Player player) {
        List<Equipment> equipmentList = new ArrayList<>();
        for (Item item : player.getBackpack()) {
            if (item instanceof Equipment) {
                equipmentList.add((Equipment) item);
            }
        }
        return equipmentList;
    }
    
    /**
     * 通过索引获取玩家装备
     */
    public Equipment getPlayerEquipmentByIndex(Player player, int index) {
        List<Equipment> equipmentList = getPlayerEquipmentList(player);
        if (index >= 0 && index < equipmentList.size()) {
            return equipmentList.get(index);
        }
        return null;
    }
}