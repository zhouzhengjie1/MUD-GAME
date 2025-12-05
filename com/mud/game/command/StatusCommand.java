package com.mud.game.command;

import com.mud.game.entity.Player;

public class StatusCommand implements Command {
    @Override
    public void execute(Player player, String[] args) {
        player.showStatus();
    }
    
    @Override
    public String getDescription() {
        return "查看角色状态";
    }
    
    @Override
    public String getUsage() {
        return "status";
    }
}