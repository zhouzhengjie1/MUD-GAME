package com.mud.game.command;

import com.mud.game.entity.Player;

public class TaskCommand implements Command {
    @Override
    public void execute(Player player, String[] args) {
        player.showTasks();
    }
    
    @Override
    public String getDescription() {
        return "查看任务列表";
    }
    
    @Override
    public String getUsage() {
        return "task";
    }
}