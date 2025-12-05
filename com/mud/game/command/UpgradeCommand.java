package com.mud.game.command;

import com.mud.game.entity.Player;
import com.mud.game.entity.Equipment;
import com.mud.game.system.EquipmentManager;
import java.util.List;

/**
 * 装备强化命令
 * 处理装备强化、修复、查看等功能
 */
public class UpgradeCommand implements Command {
    private EquipmentManager equipmentManager;
    
    public UpgradeCommand(EquipmentManager equipmentManager) {
        this.equipmentManager = equipmentManager;
    }
    
    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            showUpgradeHelp();
            return;
        }
        
        String subCommand = args[1].toLowerCase();
        
        switch (subCommand) {
            case "list":
            case "列表":
                showPlayerEquipment(player);
                break;
                
            case "upgrade":
            case "强化":
                handleUpgrade(player, args);
                break;
                
            case "repair":
            case "修复":
                handleRepair(player, args);
                break;
                
            case "info":
            case "信息":
                showEquipmentInfo(player, args);
                break;
                
            default:
                System.out.println("❌ 未知子命令：" + subCommand);
                showUpgradeHelp();
                break;
        }
    }
    
    /**
     * 显示强化帮助
     */
    private void showUpgradeHelp() {
        System.out.println("\n=== ⚒️ 装备强化系统 ===");
        System.out.println("使用方法：");
        System.out.println("  upgrade list     - 查看背包装备");
        System.out.println("  upgrade upgrade <编号> - 强化指定装备");
        System.out.println("  upgrade repair <编号>  - 修复指定装备");
        System.out.println("  upgrade info <编号>   - 查看装备详细信息");
        System.out.println("\n示例：");
        System.out.println("  upgrade list     - 列出所有装备");
        System.out.println("  upgrade upgrade 1   - 强化第1个装备");
        System.out.println("  upgrade repair 2    - 修复第2个装备");
    }
    
    /**
     * 显示玩家装备
     */
    private void showPlayerEquipment(Player player) {
        List<Equipment> equipmentList = equipmentManager.getPlayerEquipmentList(player);
        
        if (equipmentList.isEmpty()) {
            System.out.println("❌ 背包中没有装备！");
            System.out.println("💡 提示：去铁匠铺购买装备，或者在战斗中获得装备。");
            return;
        }
        
        System.out.println("\n=== 🎒 装备列表 ===");
        for (int i = 0; i < equipmentList.size(); i++) {
            Equipment equip = equipmentList.get(i);
            System.out.printf("%d. %s [Lv.%d %s] - %s\n", 
                           (i + 1), equip.getName(), equip.getLevel(), 
                           equip.getGrade().getDisplayName(), equip.getEffectDescription());
            System.out.printf("   耐久: %d/%d | 升级费用: %d | 成功率: %.1f%%\n", 
                           equip.getDurability(), equip.getMaxDurability(),
                           equip.getUpgradeCost(), equip.getSuccessRate() * 100);
        }
        
        System.out.println("\n💰 当前金币：" + player.getMoney());
    }
    
    /**
     * 处理装备强化
     */
    private void handleUpgrade(Player player, String[] args) {
        if (args.length < 3) {
            System.out.println("❌ 请指定要强化的装备编号！");
            System.out.println("使用方法：upgrade upgrade <编号>");
            return;
        }
        
        try {
            int equipmentIndex = Integer.parseInt(args[2]) - 1;
            Equipment equipment = equipmentManager.getPlayerEquipmentByIndex(player, equipmentIndex);
            
            if (equipment == null) {
                System.out.println("❌ 无效的装备编号！");
                return;
            }
            
            System.out.println("\n=== ⚒️ 装备强化 ===");
            System.out.println("准备强化：" + equipment.getName());
            System.out.println("当前等级：" + equipment.getLevel());
            System.out.println("升级费用：" + equipment.getUpgradeCost() + " 金币");
            System.out.println("成功率：" + (equipment.getSuccessRate() * 100) + "%");
            System.out.println("耐久度：" + equipment.getDurability() + "/" + equipment.getMaxDurability());
            
            if (equipment.getDurability() <= 0) {
                System.out.println("❌ 装备耐久度不足，请先修复！");
                return;
            }
            
            //System.out.print("\n是否继续强化？(y/n): ");
            
            // 由于无法直接读取用户输入，这里直接执行强化
            var result = equipmentManager.upgradeEquipment(equipment, player);
            System.out.println(result.getMessage());
            
            if (result.isSuccess()) {
                System.out.println("🎉 强化成功！");
            } else {
                System.out.println("💔 强化失败！");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("❌ 请输入有效的装备编号！");
        }
    }
    
    /**
     * 处理装备修复
     */
    private void handleRepair(Player player, String[] args) {
        if (args.length < 3) {
            System.out.println("❌ 请指定要修复的装备编号！");
            System.out.println("使用方法：upgrade repair <编号>");
            return;
        }
        
        try {
            int equipmentIndex = Integer.parseInt(args[2]) - 1;
            Equipment equipment = equipmentManager.getPlayerEquipmentByIndex(player, equipmentIndex);
            
            if (equipment == null) {
                System.out.println("❌ 无效的装备编号！");
                return;
            }
            
            var result = equipmentManager.repairEquipment(equipment, player);
            System.out.println(result.getMessage());
            
        } catch (NumberFormatException e) {
            System.out.println("❌ 请输入有效的装备编号！");
        }
    }
    
    /**
     * 显示装备详细信息
     */
    private void showEquipmentInfo(Player player, String[] args) {
        if (args.length < 3) {
            System.out.println("❌ 请指定要查看的装备编号！");
            System.out.println("使用方法：upgrade info <编号>");
            return;
        }
        
        try {
            int equipmentIndex = Integer.parseInt(args[2]) - 1;
            Equipment equipment = equipmentManager.getPlayerEquipmentByIndex(player, equipmentIndex);
            
            if (equipment == null) {
                System.out.println("❌ 无效的装备编号！");
                return;
            }
            
            System.out.println("\n=== 📋 装备信息 ===");
            System.out.println(equipment.getDetailedInfo());
            
        } catch (NumberFormatException e) {
            System.out.println("❌ 请输入有效的装备编号！");
        }
    }
    
    @Override
    public String getDescription() {
        return "装备强化系统 - 强化、修复、查看装备";
    }
    
    @Override
    public String getUsage() {
        return "upgrade <子命令> [参数]";
    }
}