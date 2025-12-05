package com.mud.game.command;

import com.mud.game.entity.Player;
import com.mud.game.system.DataManager;

public class SaveCommand implements Command {
    private DataManager dataManager;
    
    public SaveCommand() {
        this.dataManager = new DataManager();
    }
    
    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            System.out.println("请输入存档名称！");
            System.out.println("使用方法: save <存档名称>");
            return;
        }
        
        String saveName = args[1];
        boolean success = dataManager.saveGame(player, saveName);
        
        if (success) {
            System.out.println("游戏已保存为: " + saveName);
        } else {
            System.out.println("保存游戏失败！");
        }
    }
    
    @Override
    public String getDescription() {
        return "保存当前游戏进度";
    }
    
    @Override
    public String getUsage() {
        return "save <存档名称> - 保存游戏到指定存档";
    }
}