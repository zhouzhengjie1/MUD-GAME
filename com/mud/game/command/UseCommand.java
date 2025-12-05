package com.mud.game.command;

import com.mud.game.entity.Player;

public class UseCommand implements Command {
    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            System.out.println("请指定要使用的物品。例如：use potion");
            return;
        }
        
        String itemName = args[1];
        player.useItem(itemName);
    }
    
    @Override
    public String getDescription() {
        return "使用背包中的物品";
    }
    
    @Override
    public String getUsage() {
        return "use <物品名称>";
    }
}