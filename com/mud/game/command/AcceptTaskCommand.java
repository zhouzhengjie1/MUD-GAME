package com.mud.game.command;

import com.mud.game.entity.Player;

public class AcceptTaskCommand implements Command {
    
    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            player.showAvailableTasks();
            return;
        }
        
        // 组合任务名称参数
        StringBuilder taskNameBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (i > 1) taskNameBuilder.append(" ");
            taskNameBuilder.append(args[i]);
        }
        String taskName = taskNameBuilder.toString();
        
        player.acceptTask(taskName);
    }
    
    @Override
    public String getDescription() {
        return "接受任务";
    }
    
    @Override
    public String getUsage() {
        return "accept [任务名称] - 接受指定任务，或使用 'accept' 查看可接受的任务列表";
    }
}