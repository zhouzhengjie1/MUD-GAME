package com.mud.game.command;

import com.mud.game.entity.Player;

public class HelpCommand implements Command {
    @Override
    public void execute(Player player, String[] args) {
        System.out.println("\n=== 可用命令 ===");
        System.out.println("go <方向>    - 向指定方向移动");
        System.out.println("take <物品>  - 拾取房间中的物品");
        System.out.println("use <物品>   - 使用背包中的物品");
        System.out.println("unequip <装备> - 卸下已装备的物品");
        System.out.println("attack       - 攻击当前房间的NPC");
        System.out.println("talk         - 与当前房间的NPC对话");
        System.out.println("npcinfo      - 查看当前房间NPC的详细信息");
        System.out.println("status       - 查看角色状态");
        System.out.println("backpack     - 查看背包物品");
        System.out.println("task         - 查看任务列表");
        System.out.println("accept [任务] - 接受任务或查看可接受的任务");
        System.out.println("save <名称>  - 保存游戏进度");
        System.out.println("load <名称>  - 加载已保存的游戏");
        System.out.println("upgrade list - 查看装备列表");
        System.out.println("upgrade upgrade <编号> - 强化装备");
        System.out.println("upgrade repair <编号> - 修复装备");
        System.out.println("upgrade info <编号> - 查看装备详细信息");
        System.out.println("help         - 显示此帮助信息");
        System.out.println("quit         - 退出游戏");
    }
    
    @Override
    public String getDescription() {
        return "显示帮助信息";
    }
    
    @Override
    public String getUsage() {
        return "help";
    }
}