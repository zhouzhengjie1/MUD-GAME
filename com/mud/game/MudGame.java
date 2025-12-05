package com.mud.game;

import com.mud.game.entity.*;
import com.mud.game.command.*;
import com.mud.game.system.*;
import java.util.Scanner;
import java.util.List;
import java.util.Map;

public class MudGame {
    private Player player;
    private CommandParser commandParser;
    private MapManager mapManager;
    private BattleEngine battleEngine;
    private TaskManager taskManager;
    private Scanner scanner;
    private boolean gameRunning;
    private boolean loadRequested;
    private String loadSaveName;
    
    public MudGame() {
        this.scanner = new Scanner(System.in);
        this.commandParser = new CommandParser();
        // 暂时使用默认难度创建地图管理器，后面会更新为玩家选择的难度
        this.mapManager = new MapManager();
        this.battleEngine = new BattleEngine();
        this.taskManager = new TaskManager();
        this.gameRunning = false;
        this.loadRequested = false;
        this.loadSaveName = null;
    }
    
    public void start() {
        System.out.println("=== 欢迎来到文字冒险世界 ===");
        System.out.println("这是一个充满冒险和挑战的世界...");
        System.out.println();
        
        // 创建玩家
        createPlayer();
        
        // 初始化游戏
        initializeGame();
        
        // 显示欢迎信息
        showWelcomeMessage();
        
        // 开始游戏循环
        gameLoop();
    }
    
    private void createPlayer() {
        System.out.print("请输入你的角色名称：");
        String playerName = scanner.nextLine().trim();
        
        if (playerName.isEmpty()) {
            playerName = "冒险者";
        }
        
        // 选择游戏难度
        GameDifficulty difficulty = selectDifficulty();
        
        // 根据难度创建玩家
        this.player = new Player(playerName, 100, 10, 5, 1, 0, 100, difficulty);
        
        // 显示难度信息
        System.out.println("\n=== 难度选择 ===");
        System.out.println("你选择了【" + difficulty.getChineseName() + "】难度");
        System.out.println("玩家属性调整：生命值×" + difficulty.getPlayerHpMultiplier() + ", 攻击力×" + difficulty.getPlayerAtkMultiplier() + ", 防御力×" + difficulty.getPlayerDefMultiplier());
        System.out.println("NPC属性调整：生命值×" + difficulty.getNpcHpMultiplier() + ", 攻击力×" + difficulty.getNpcAtkMultiplier() + ", 防御力×" + difficulty.getNpcDefMultiplier());
        System.out.println("奖励倍数：×" + difficulty.getRewardMultiplier());
        System.out.println();
        
        System.out.println("欢迎，" + playerName + "！你的冒险即将开始...");
        System.out.println();
    }
    
    private GameDifficulty selectDifficulty() {
        System.out.println("\n=== 选择游戏难度 ===");
        
        // 获取所有可用难度选项
        Map<String, GameDifficulty> difficultyOptions = GameDifficulty.getDifficultyOptions();
        
        // 显示难度选项
        for (Map.Entry<String, GameDifficulty> entry : difficultyOptions.entrySet()) {
            GameDifficulty diff = entry.getValue();
            System.out.println(entry.getKey() + ". " + diff.getChineseName());
            System.out.println("   难度描述：" + diff.getDescription());
            System.out.println("   玩家属性：" + diff.getPlayerStatsDescription());
            System.out.println("   NPC属性：" + diff.getNpcStatsDescription());
            System.out.println();
        }
        
        // 让玩家选择难度
        while (true) {
            System.out.print("请选择难度 (输入选项编号): ");
            String input = scanner.nextLine().trim();
            
            if (difficultyOptions.containsKey(input)) {
                return difficultyOptions.get(input);
            } else {
                System.out.println("无效的选择，请输入正确的选项编号！");
            }
        }
    }
    
    private void initializeGame() {

        // 使用玩家选择的难度重新创建地图管理器
        this.mapManager = new MapManager(player.getDifficulty());
        
        // 设置玩家起始位置
        Room startRoom = mapManager.getStartRoom();
        player.setCurrentRoom(startRoom);
        player.setCurrentRoomName(startRoom.getName());
        
        // 给玩家一些初始物品
        player.addItem(new Equipment("新手剑", "一把基础的长剑", 5, ItemType.WEAPON, EquipmentGrade.COMMON, 3));
        player.addItem(new Equipment("新手护甲", "一套基础的护甲", 3, ItemType.ARMOR, EquipmentGrade.COMMON, 3));
        player.addItem(new Item("治疗药水", "可以恢复生命的药水", 20, ItemType.MEDICINE));
        
        System.out.println("你获得了初始装备：新手剑、新手护甲、治疗药水");
        
        // 将任务管理器中的任务添加到玩家的任务列表
        List<Task> allAvailableTasks = taskManager.getAllTasks();
        for (Task task : allAvailableTasks) {
            // 创建任务的副本，避免多个玩家共享同一个任务实例
            Task taskCopy = new Task(
                task.getName(),
                task.getDescription(),
                task.getObjective(),
                task.getReward(),
                task.isMainTask()
            );
            player.getTasks().add(taskCopy);
        }
        
        // 为玩家设置任务管理器引用
        player.setTaskManager(taskManager);
        
        System.out.println("任务系统已初始化，" + allAvailableTasks.size() + "个任务可用！");
        System.out.println();
    }
    
    private void showWelcomeMessage() {
        System.out.println("=== 游戏开始 ===");
        player.showStatus();
        System.out.println();
        player.getCurrentRoom().enterRoom();
        System.out.println();
        
        // 检查并触发初始任务
        taskManager.checkTaskTriggers(player, "enter", player.getCurrentRoom().getName());
        
        System.out.println("输入 'help' 查看可用命令，输入 'quit' 退出游戏");
        System.out.println();
    }
    
    private void gameLoop() {
        gameRunning = true;
        
        while (gameRunning) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) {
                continue;
            }
            
            // 解析并执行命令
            commandParser.parseAndExecute(input, player);
            
            // 检查是否需要加载存档
            if (player.isLoadRequested()) {
                String saveName = player.getLoadSaveName();
                player.setLoadRequested(false);
                player.setLoadSaveName(null);
                
                if (loadGame(saveName)) {
                    System.out.println("存档加载成功！继续游戏...");
                    continue;
                } else {
                    System.out.println("存档加载失败，继续当前游戏...");
                }
            }
            
            // 检查游戏状态
            checkGameState();
            
            System.out.println();
        }
        
        // 游戏结束
        System.out.println("感谢游玩！再见！");
        scanner.close();
    }
    
    private void checkGameState() {
        // 检查玩家是否存活
        if (player.getHp() <= 0) {
            System.out.println("\n=== 游戏结束 ===");
            System.out.println("你的角色已经死亡！");
            System.out.println("最终等级：" + player.getLevel());
            System.out.println("获得经验：" + player.getExperience());
            gameRunning = false;
            return;
        }
        
        // 检查是否完成所有主线任务
        boolean allMainTasksCompleted = true;
        for (Task task : player.getTasks()) {
            if (task.isMainTask() && task.getStatus() != TaskStatus.COMPLETED) {
                allMainTasksCompleted = false;
                break;
            }
        }
        
        if (allMainTasksCompleted) {
            System.out.println("\n=== 恭喜！ ===");
            System.out.println("你已经完成了所有主线任务！");
            System.out.println("最终等级：" + player.getLevel());
            System.out.println("获得经验：" + player.getExperience());
            System.out.println("你成为了真正的英雄！");
            gameRunning = false;
        }
    }
    
    public void setPlayer(Player player) {
        this.player = player;
    }
    
    // 在游戏内加载存档的方法
    public void requestLoadGame(String saveName) {
        this.loadRequested = true;
        this.loadSaveName = saveName;
    }
    
    private boolean loadGame(String saveName) {
        DataManager dataManager = new DataManager();
        
        System.out.println("正在加载存档: " + saveName);
        
        // 尝试加载玩家数据
        Player loadedPlayer = dataManager.loadGame(saveName);
        if (loadedPlayer == null) {
            System.out.println("加载存档失败：无法读取存档文件");
            return false;
        }
        
        // 更新当前玩家对象
        this.player = loadedPlayer;
        
        // 重新初始化相关管理器
        this.mapManager = new MapManager(player.getDifficulty());
        this.battleEngine = new BattleEngine();
        this.taskManager = new TaskManager();
        
        // 设置玩家当前房间
        Room currentRoom = mapManager.getRoomByName(player.getCurrentRoomName());
        if (currentRoom != null) {
            player.setCurrentRoom(currentRoom);
        }
        
        System.out.println("存档加载成功！");
        System.out.println("欢迎回来，" + player.getName() + "！");
        System.out.println();
        
        // 显示当前状态
        player.showStatus();
        System.out.println();
        player.getCurrentRoom().enterRoom();
        
        return true;
    }

    public static void main(String[] args) {
        MudGame game = new MudGame();
        game.start();
    }
}