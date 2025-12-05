package com.mud.game.command;

import com.mud.game.entity.Player;

public class TakeCommand implements Command {
    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            System.out.println("请指定要拾取的物品。例如：take sword");
            return;
        }
        
        String itemName = args[1];
        player.takeItem(itemName);
    }
    
    @Override
    public String getDescription() {
        return "拾取房间中的物品";
    }
    
    @Override
    public String getUsage() {
        return "take <物品名称>";
    }
}