package com.mud.game.command;

import com.mud.game.entity.Player;
import com.mud.game.system.DataManager;

public class LoadCommand implements Command {
    private DataManager dataManager;
    
    public LoadCommand() {
        this.dataManager = new DataManager();
    }
    
    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            System.out.println("请输入存档名称！");
            System.out.println("可用存档：");
            
            java.util.List<String> saves = dataManager.getAvailableSaves();
            if (saves.isEmpty()) {
                System.out.println("  暂无存档");
            } else {
                for (String save : saves) {
                    System.out.println("  " + save);
                }
            }
            System.out.println("使用方法: load <存档名称>");
            return;
        }
        
        String saveName = args[1];
        
        // 显示存档信息
        System.out.println("正在加载存档...");
        if(!dataManager.showSaveInfo(saveName))return;
        System.out.println();
        
        System.out.print("确认加载此存档？(y/n): ");
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if (!confirm.equals("y") && !confirm.equals("yes")) {
            System.out.println("加载已取消。");
            return;
        }
        
        // 设置加载请求标记，将在游戏循环中处理
        player.setLoadRequested(true);
        player.setLoadSaveName(saveName);
        System.out.println("存档加载将在当前命令执行完成后进行...");
    }
    
    @Override
    public String getDescription() {
        return "加载已保存的游戏进度";
    }
    
    @Override
    public String getUsage() {
        return "load <存档名称> - 从指定存档加载游戏";
    }
}