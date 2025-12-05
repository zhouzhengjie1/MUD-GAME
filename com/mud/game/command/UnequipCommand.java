package com.mud.game.command;

import com.mud.game.entity.Player;

public class UnequipCommand implements Command {
    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            // 无参数时显示当前装备的所有装备
            player.showEquippedItems();
            return;
        }
        
        // 组合装备名称参数（支持多词名称）
        StringBuilder itemNameBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (i > 1) itemNameBuilder.append(" ");
            itemNameBuilder.append(args[i]);
        }
        String itemName = itemNameBuilder.toString();
        
        player.unequipItem(itemName);
    }
    
    @Override
    public String getDescription() {
        return "卸下已装备的物品";
    }
    
    @Override
    public String getUsage() {
        return "unequip <装备名称>";
    }
}