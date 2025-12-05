package com.mud.game.system;

import com.mud.game.entity.Player;
import com.mud.game.entity.Room;
import java.util.List;
import java.util.Scanner;

/**
 * 游戏存档加载器
 * 提供完整的存档管理和加载功能
 */
public class GameLoader {
    private DataManager dataManager;
    private Scanner scanner;
    
    public GameLoader() {
        this.dataManager = new DataManager();
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * 显示存档选择界面并加载选中的存档
     * @return 加载的玩家对象，如果取消或失败返回null
     */
    public Player showLoadMenu() {
        System.out.println("\n=== 加载游戏 ===");
        
        // 获取可用存档
        List<String> saves = dataManager.getAvailableSaves();
        
        if (saves.isEmpty()) {
            System.out.println("暂无可用存档！");
            System.out.println("请先创建新游戏或保存当前进度。");
            return null;
        }
        
        // 显示存档列表
        System.out.println("可用存档：");
        for (int i = 0; i < saves.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + saves.get(i));
        }
        System.out.println("  0. 取消加载");
        
        // 选择存档
        String saveName = selectSave(saves);
        if (saveName == null) {
            return null;
        }
        
        // 显示存档详情
        System.out.println("\n正在读取存档信息...");
        dataManager.showSaveInfo(saveName);
        
        // 确认加载
        if (!confirmLoad()) {
            System.out.println("加载已取消。");
            return null;
        }
        
        // 加载存档
        return loadGame(saveName);
    }
    
    /**
     * 选择存档
     */
    private String selectSave(List<String> saves) {
        System.out.print("\n请选择要加载的存档（输入编号或名称）：");
        String input = scanner.nextLine().trim();
        
        // 取消加载
        if (input.equals("0")) {
            return null;
        }
        
        // 尝试解析为数字
        try {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < saves.size()) {
                return saves.get(index);
            } else {
                System.out.println("无效的编号！");
                return selectSave(saves); // 递归重新选择
            }
        } catch (NumberFormatException e) {
            // 如果不是数字，检查是否是有效的存档名称
            if (saves.contains(input)) {
                return input;
            } else {
                System.out.println("存档不存在！");
                return selectSave(saves); // 递归重新选择
            }
        }
    }
    
    /**
     * 确认加载
     */
    private boolean confirmLoad() {
        System.out.print("\n确认加载此存档？(y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        return confirm.equals("y") || confirm.equals("yes");
    }
    
    /**
     * 加载游戏存档
     */
    private Player loadGame(String saveName) {
        System.out.println("\n正在加载存档...");
        
        Player player = dataManager.loadGame(saveName);
        
        if (player != null) {
            System.out.println("✅ 存档加载成功！");
            
            // 显示加载的角色信息
            showLoadedPlayerInfo(player);
            
            // 重新建立房间连接
            restoreGameState(player);
            
            return player;
        } else {
            System.out.println("❌ 存档加载失败！");
            return null;
        }
    }
    
    /**
     * 显示加载的玩家信息
     */
    private void showLoadedPlayerInfo(Player player) {
        System.out.println("\n=== 角色信息 ===");
        player.showStatus();
        
        System.out.println("\n=== 背包物品 ===");
        player.showBackpack();
        
        System.out.println("\n=== 任务状态 ===");
        player.showTasks();
    }
    
    /**
     * 恢复游戏状态
     * 重新建立玩家与房间的连接关系
     */
    private void restoreGameState(Player player) {
        // 这里需要重新建立玩家与游戏世界的连接
        // 由于房间和地图信息需要在主游戏中重新初始化
        // 我们只保留玩家的基本状态和物品
        
        System.out.println("\n=== 游戏状态恢复 ===");
        System.out.println("位置：" + player.getCurrentRoomName());
        
        // 注意：实际的房间对象需要在主游戏中重新设置
        // 这里只是显示信息，告诉玩家加载成功
        System.out.println("游戏状态已恢复，可以开始冒险了！");
    }
    
    /**
     * 显示存档管理菜单
     */
    public void showSaveManagementMenu() {
        while (true) {
            System.out.println("\n=== 存档管理 ===");
            System.out.println("1. 查看所有存档");
            System.out.println("2. 查看存档详情");
            System.out.println("3. 删除存档");
            System.out.println("4. 返回主菜单");
            
            System.out.print("请选择操作：");
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    showAllSaves();
                    break;
                case "2":
                    showSaveDetails();
                    break;
                case "3":
                    deleteSave();
                    break;
                case "4":
                    return;
                default:
                    System.out.println("无效的选择！");
            }
        }
    }
    
    /**
     * 显示所有存档
     */
    private void showAllSaves() {
        List<String> saves = dataManager.getAvailableSaves();
        
        if (saves.isEmpty()) {
            System.out.println("暂无存档！");
            return;
        }
        
        System.out.println("\n=== 所有存档 ===");
        for (int i = 0; i < saves.size(); i++) {
            System.out.println((i + 1) + ". " + saves.get(i));
        }
    }
    
    /**
     * 显示存档详情
     */
    private void showSaveDetails() {
        List<String> saves = dataManager.getAvailableSaves();
        
        if (saves.isEmpty()) {
            System.out.println("暂无存档！");
            return;
        }
        
        showAllSaves();
        System.out.print("请输入要查看的存档编号或名称：");
        String input = scanner.nextLine().trim();
        
        String saveName;
        try {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < saves.size()) {
                saveName = saves.get(index);
            } else {
                System.out.println("无效的编号！");
                return;
            }
        } catch (NumberFormatException e) {
            if (saves.contains(input)) {
                saveName = input;
            } else {
                System.out.println("存档不存在！");
                return;
            }
        }
        
        dataManager.showSaveInfo(saveName);
    }
    
    /**
     * 删除存档
     */
    private void deleteSave() {
        List<String> saves = dataManager.getAvailableSaves();
        
        if (saves.isEmpty()) {
            System.out.println("暂无存档！");
            return;
        }
        
        showAllSaves();
        System.out.print("请输入要删除的存档编号或名称：");
        String input = scanner.nextLine().trim();
        
        String saveName;
        try {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < saves.size()) {
                saveName = saves.get(index);
            } else {
                System.out.println("无效的编号！");
                return;
            }
        } catch (NumberFormatException e) {
            if (saves.contains(input)) {
                saveName = input;
            } else {
                System.out.println("存档不存在！");
                return;
            }
        }
        
        System.out.print("确认删除存档 '" + saveName + "'？(y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if (confirm.equals("y") || confirm.equals("yes")) {
            if (dataManager.deleteSave(saveName)) {
                System.out.println("✅ 存档已删除！");
            } else {
                System.out.println("❌ 删除失败！");
            }
        } else {
            System.out.println("删除已取消。");
        }
    }
    
    /**
     * 获取数据管理器（用于测试）
     */
    public DataManager getDataManager() {
        return dataManager;
    }
}