package com.mud.game.command;

import com.mud.game.entity.Player;

public class BackpackCommand implements Command {
    @Override
    public void execute(Player player, String[] args) {
        player.showBackpack();
    }
    
    @Override
    public String getDescription() {
        return "查看背包物品";
    }
    
    @Override
    public String getUsage() {
        return "backpack";
    }
}