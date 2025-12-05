package com.mud.game.system;

import com.mud.game.entity.Player;
import com.mud.game.entity.Room;
import com.mud.game.entity.Item;
import com.mud.game.entity.ItemType;
import com.mud.game.entity.Equipment;
import com.mud.game.entity.EquipmentGrade;
import com.mud.game.entity.Task;
import com.mud.game.entity.NPC;
import com.mud.game.MudGame;
import java.util.Scanner;
import java.util.Map;

/**
 * 游戏启动器
 * 提供新游戏、加载存档、存档管理等功能
 */
public class GameLauncher {
    private Scanner scanner;
    private GameLoader gameLoader;
    private MapManager mapManager;
    private TaskManager taskManager;
    
    public GameLauncher() {
        this.scanner = new Scanner(System.in);
        this.gameLoader = new GameLoader();
        this.mapManager = new MapManager();
        this.taskManager = new TaskManager();
    }
    
    /**
     * 显示主菜单并处理用户选择
     * @return 玩家对象，如果退出返回null
     */
    public Player showMainMenu() {
        while (true) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("           欢迎来到武侠世界 RPG");
            System.out.println("=".repeat(50));
            System.out.println("1. 开始新游戏");
            System.out.println("2. 加载存档");
            System.out.println("3. 存档管理");
            System.out.println("4. 退出游戏");
            System.out.println("=".repeat(50));
            
            System.out.print("请选择操作：");
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    return startNewGame();
                case "2":
                    Player loadedPlayer = gameLoader.showLoadMenu();
                    if (loadedPlayer != null) {
                        return restoreLoadedGame(loadedPlayer);
                    }
                    break;
                case "3":
                    gameLoader.showSaveManagementMenu();
                    break;
                case "4":
                    System.out.println("感谢游玩，再见！");
                    return null;
                default:
                    System.out.println("无效的选择，请重新输入！");
            }
        }
    }
    
    /**
     * 开始新游戏
     */
    private Player startNewGame() {
        System.out.println("\n=== 创建新角色 ===");
        
        System.out.print("请输入角色姓名：");
        String playerName = scanner.nextLine().trim();
        
        if (playerName.isEmpty()) {
            playerName = "无名侠士";
        }
        
        // 创建玩家
        // 使用默认普通难度创建玩家
        Player player = new Player(playerName, 100, 10, 5, 1, 0, 100, com.mud.game.entity.GameDifficulty.NORMAL);
        player.setMoney(100);
        player.setLevel(1);
        player.setExperience(0);
        player.setReputation(0);
        
        // 初始化游戏世界
        initializeGameWorld(player);
        
        System.out.println("\n🎉 角色创建成功！");
        System.out.println("欢迎 " + playerName + " 来到武侠世界！");
        
        return player;
    }
    
    /**
     * 恢复已加载的游戏
     * 重新建立玩家与游戏世界的连接
     */
    private Player restoreLoadedGame(Player loadedPlayer) {
        System.out.println("\n正在恢复游戏世界...");
        
        // 重新初始化地图
        // mapManager.initializeMap(); // 这个方法在MapManager中是私有的
        
        // 重新建立玩家与房间的连接
        String currentRoomName = loadedPlayer.getCurrentRoomName();
        Room currentRoom = mapManager.getRoom(currentRoomName);
        
        if (currentRoom != null) {
            loadedPlayer.setCurrentRoom(currentRoom);
            
            // 恢复NPC的血量状态
            restoreNpcHealth(loadedPlayer);
            
            System.out.println("✅ 游戏世界恢复完成！");
            
            // 显示欢迎信息
            System.out.println("\n🎮 欢迎回来，" + loadedPlayer.getName() + "！");
            System.out.println("你当前的位置是：" + currentRoomName);
            
            return loadedPlayer;
        } else {
            System.out.println("⚠️ 警告：无法找到存档中的位置，将重新开始...");
            // 如果找不到原来的位置，重新开始
            initializeGameWorld(loadedPlayer);
            return loadedPlayer;
        }
    }
    
    /**
     * 恢复NPC的血量状态
     */
    private void restoreNpcHealth(Player player) {
        Map<String, Integer> npcHealthMap = player.getNpcHealthMap();
        if (npcHealthMap != null && !npcHealthMap.isEmpty()) {
            System.out.println("🔄 正在恢复NPC状态...");
            
            // 遍历所有房间中的NPC，恢复血量
            for (Room room : mapManager.getRooms()) {
                NPC npc = room.getNpc();
                if (npc != null && npcHealthMap.containsKey(npc.getName())) {
                    Integer savedHp = npcHealthMap.get(npc.getName());
                    if (savedHp != null) {
                        // 设置NPC的血量
                        npc.setHp(savedHp);
                        if (savedHp <= 0) {
                            npc.setAlive(false);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 初始化游戏世界
     */
    private void initializeGameWorld(Player player) {
        // 初始化地图
        // mapManager.initializeMap(); // 这个方法在MapManager中是私有的
        
        // 设置玩家起始位置
        Room startRoom = mapManager.getStartRoom();
        player.setCurrentRoom(startRoom);
        player.setCurrentRoomName(startRoom.getName());
        
        // 初始化任务系统
        // taskManager.initializeTasks(); // 这个方法在TaskManager中是私有的
        
        // 给玩家一些初始物品
        giveStarterItems(player);
        
        // 添加初始任务
        addStarterTasks(player);
    }
    
    /**
     * 给予新手物品
     */
    private void giveStarterItems(Player player) {
        // 给玩家一些初始物品
        Item starterSword = new Equipment("铁剑", "新手铁剑，攻击力+5", 5, ItemType.WEAPON, EquipmentGrade.COMMON, 3);
        Item starterPotion = new Item("生命药水", "恢复生命值20点", 20, ItemType.MEDICINE);
        
        // 添加到玩家背包
        player.addItem(starterSword);
        player.addItem(starterPotion);
        
        // 自动使用武器效果
        starterSword.use(player);
        System.out.println("已获得新手装备：新手剑、新手护甲、治疗药水！");
    }
    
    /**
     * 添加新手任务
     */
    private void addStarterTasks(Player player) {
        // 这里可以添加一些初始任务
        System.out.println("新手任务已准备就绪！");
    }
    
    /**
     * 显示游戏介绍
     */
    public void showGameIntro() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                    游戏介绍");
        System.out.println("=".repeat(60));
        System.out.println("这是一个基于文本的武侠RPG游戏。");
        System.out.println("你可以：");
        System.out.println("• 探索不同的地点和场景");
        System.out.println("• 与NPC对话和战斗");
        System.out.println("• 收集和使用各种物品");
        System.out.println("• 完成任务获得奖励");
        System.out.println("• 保存和加载游戏进度");
        System.out.println("=".repeat(60));
        System.out.println("输入 'help' 查看可用命令");
        System.out.println("=".repeat(60));
    }
    
    /**
     * 获取扫描器（用于主游戏循环）
     */
    public Scanner getScanner() {
        return scanner;
    }
    
    /**
     * 获取地图管理器
     */
    public MapManager getMapManager() {
        return mapManager;
    }
    
    /**
     * 获取任务管理器
     */
    public TaskManager getTaskManager() {
        return taskManager;
    }
    
    /**
     * 主方法 - 游戏入口点
     */
    public static void main(String[] args) {
        GameLauncher launcher = new GameLauncher();
        Player player = launcher.showMainMenu();
        
        if (player != null) {
            // 显示游戏介绍
            launcher.showGameIntro();
            
            // 创建并启动游戏主循环
            MudGame game = new MudGame();
            game.start();
        }
        
        System.out.println("游戏结束，再见！");
    }
    
    /**
     * 关闭资源
     */
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}