package com.mud.game.command;

import com.mud.game.entity.Player;

public class QuitCommand implements Command {
    @Override
    public void execute(Player player, String[] args) {
        System.out.println("感谢游玩！再见！");
        System.exit(0);
    }
    
    @Override
    public String getDescription() {
        return "退出游戏";
    }
    
    @Override
    public String getUsage() {
        return "quit";
    }
}