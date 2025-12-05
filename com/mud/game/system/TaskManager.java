package com.mud.game.system;

import com.mud.game.entity.*;
import java.util.*;

import javax.sound.midi.SysexMessage;

public class TaskManager {
    private List<Task> allTasks;
    private List<Task> activeTasks;
    private Map<String, Task> taskMap;
    
    public TaskManager() {
        this.allTasks = new ArrayList<>();
        this.activeTasks = new ArrayList<>();
        this.taskMap = new HashMap<>();
        initializeTasks();
    }
    
    private void initializeTasks() {
        // 主线任务
        TaskObjective mainObjective1 = new TaskObjective("探索村庄，了解基本情况", "explore", "村中心", 1);
        Reward mainReward1 = new Reward(50, 5, null, "完成村庄探索");
        Task mainTask1 = new Task("初来乍到", "作为新来的冒险者，你需要先了解这个村庄的情况", mainObjective1, mainReward1, true);
        
        TaskObjective mainObjective2 = new TaskObjective("击败森林中的野狼", "kill", "野狼", 1);
        Reward mainReward2 = new Reward(100, 10, new Equipment("狼牙项链", "野狼的牙齿制成的项链，增加攻击力", 3, ItemType.WEAPON, EquipmentGrade.UNCOMMON, 3), "击败野狼的奖励");
        Task mainTask2 = new Task("森林的威胁", "村庄附近的森林里有野狼出没，威胁着村民的安全", mainObjective2, mainReward2, true);
        
        TaskObjective mainObjective3 = new TaskObjective("击败山洞中的哥布林", "kill", "哥布林", 1);
        Reward mainReward3 = new Reward(200, 20, new Equipment("精铁剑", "铁匠精心打造的精铁剑", 8, ItemType.WEAPON, EquipmentGrade.RARE, 5), "击败哥布林的奖励");
        Task mainTask3 = new Task("山洞的邪恶", "山洞中的哥布林是更大的威胁，必须清除", mainObjective3, mainReward3, true);
        
        // 支线任务
        TaskObjective sideObjective1 = new TaskObjective("与村民对话", "talk", "村民", 1);
        Reward sideReward1 = new Reward(25, 2, new Item("草药", "村民给你的草药", 15, ItemType.MEDICINE), "村民的好感");
        Task sideTask1 = new Task("村民的忧虑", "村民似乎有什么烦恼，去和他聊聊", sideObjective1, sideReward1, false);
        
        TaskObjective sideObjective2 = new TaskObjective("收集5个木材", "collect", "木材", 5);
        Reward sideReward2 = new Reward(75, 3, new Item("金币", "村民给你的报酬", 0, ItemType.OTHER), "收集木材的报酬");
        Task sideTask2 = new Task("收集木材", "铁匠需要一些木材来修理工具", sideObjective2, sideReward2, false);
        
        TaskObjective sideObjective3 = new TaskObjective("收集3个草药", "collect", "草药", 3);
        Reward sideReward3 = new Reward(60, 5, new Item("治疗药水", "客栈老板特制的治疗药水", 30, ItemType.MEDICINE), "收集草药的奖励");
        Task sideTask3 = new Task("采集草药", "客栈老板需要一些草药来制作药剂", sideObjective3, sideReward3, false);
        
        // 新增丰富的任务
        TaskObjective newObjective1 = new TaskObjective("调查森林异常", "explore", "森林", 1);
        Reward newReward1 = new Reward(120, 8, new Equipment("猎人徽章", "老猎人给你的徽章，增加防御力", 5, ItemType.ARMOR, EquipmentGrade.UNCOMMON, 3), "调查森林的奖励");
        Task newTask1 = new Task("森林异变", "森林中的动物行为异常，需要调查原因", newObjective1, newReward1, false);
        
        TaskObjective newObjective2 = new TaskObjective("净化山顶", "explore", "山顶", 1);
        Reward newReward2 = new Reward(150, 15, new Equipment("净化法杖", "隐士给你的法杖，可以净化邪恶", 12, ItemType.WEAPON, EquipmentGrade.EPIC, 8), "净化山顶的奖励");
        Task newTask2 = new Task("山顶净化", "山顶被邪恶力量污染，需要帮助隐士进行净化仪式", newObjective2, newReward2, false);
        
        TaskObjective newObjective3 = new TaskObjective("保护客栈", "kill", "野狼", 2);
        Reward newReward3 = new Reward(100, 12, new Item("特制美酒", "客栈老板特制的陈年美酒", 25, ItemType.MEDICINE), "保护客栈的奖励");
        Task newTask3 = new Task("客栈护卫", "客栈老板担心野狼威胁客栈安全，需要你清理森林中的野狼", newObjective3, newReward3, false);
        
        TaskObjective newObjective4 = new TaskObjective("寻找矿石", "collect", "矿石", 1);
        Reward newReward4 = new Reward(80, 10, new Equipment("精钢护甲", "铁匠用珍贵矿石打造的护甲", 10, ItemType.ARMOR, EquipmentGrade.RARE, 5), "寻找矿石的奖励");
        Task newTask4 = new Task("珍贵矿石", "铁匠需要山洞中的珍贵矿石来打造高级装备", newObjective4, newReward4, false);
        
        // 扩展地图相关任务
        TaskObjective templeObjective = new TaskObjective("参拜古庙", "explore", "古庙", 1);
        Reward templeReward = new Reward(90, 7, new Item("祈福符", "老和尚给你的祈福符，增加幸运值", 0, ItemType.OTHER), "参拜古庙的奖励");
        Task templeTask = new Task("古庙祈福", "古庙的老和尚邀请你参拜，据说能获得佛祖庇佑", templeObjective, templeReward, false);
        
        TaskObjective tradeObjective = new TaskObjective("与商人交易", "talk", "旅行商人", 1);
        Reward tradeReward = new Reward(110, 9, new Item("商人的货物", "旅行商人给你的特殊商品", 0, ItemType.OTHER), "与商人交易的奖励");
        Task tradeTask = new Task("商人的请求", "旅行商人需要一些稀有物品，愿意用珍贵商品交换", tradeObjective, tradeReward, false);
        
        TaskObjective herbObjective = new TaskObjective("采集稀有草药", "collect", "草药", 5);
        Reward herbReward = new Reward(130, 11, new Item("药师的药剂", "药师特制的强效药剂", 40, ItemType.MEDICINE), "采集草药的奖励");
        Task herbTask = new Task("稀有草药", "药师需要一些稀有草药来配制特殊药剂", herbObjective, herbReward, false);
        
        TaskObjective martialObjective = new TaskObjective("通过武师考验", "kill", "竹林盗贼", 1);
        Reward martialReward = new Reward(160, 14, new Item("武师秘籍", "武师传授的秘籍，学会新技能", 0, ItemType.OTHER), "通过考验的奖励");
        Task martialTask = new Task("武师考验", "武师要求你先证明自己的实力，击败竹林中的盗贼", martialObjective, martialReward, false);
        
        TaskObjective lakeObjective = new TaskObjective("调查湖心亭", "explore", "湖心亭", 1);
        Reward lakeReward = new Reward(95, 6, new Item("书生的诗集", "书生给你的诗集，增加文化修养", 0, ItemType.OTHER), "调查湖心亭的奖励");
        Task lakeTask = new Task("湖心亭之谜", "湖心亭最近出现奇怪的光芒，书生希望你能调查", lakeObjective, lakeReward, false);
        
        TaskObjective bambooObjective = new TaskObjective("驱赶竹林盗贼", "kill", "竹林盗贼", 1);
        Reward bambooReward = new Reward(140, 12, new Item("隐士的信物", "女隐士给你的信物，可以进入神秘地点", 0, ItemType.OTHER), "驱赶盗贼的奖励");
        Task bambooTask = new Task("竹林清修", "竹林中的盗贼扰乱了隐士的清修，需要你帮忙驱赶", bambooObjective, bambooReward, false);
        
        TaskObjective ghostObjective = new TaskObjective("净化废弃小屋", "kill", "怨灵", 1);
        Reward ghostReward = new Reward(170, 16, new Item("净化宝珠", "净化怨灵后获得的宝珠，具有神秘力量", 15, ItemType.OTHER), "净化废弃小屋的奖励");
        Task ghostTask = new Task("怨灵净化", "废弃小屋中的怨灵需要被净化，才能获得安宁", ghostObjective, ghostReward, false);
        
        TaskObjective guardianObjective = new TaskObjective("击败守护灵", "kill", "守护灵", 1);
        Reward guardianReward = new Reward(200, 20, new Item("宝藏钥匙", "守护灵守护的宝藏钥匙", 0, ItemType.OTHER), "击败守护灵的奖励");
        Task guardianTask = new Task("宝藏守护者", "地下大厅的守护灵守护着珍贵的宝藏，只有击败它才能获得", guardianObjective, guardianReward, false);
        
        // 连锁任务
        TaskObjective chainObjective1 = new TaskObjective("与隐士对话", "talk", "隐士", 1);
        Reward chainReward1 = new Reward(50, 5, null, "隐士的智慧");
        Task chainTask1 = new Task("隐士的指引", "山顶的隐士似乎知道一些秘密，去和他谈谈", chainObjective1, chainReward1, false);
        
        TaskObjective chainObjective2 = new TaskObjective("收集净化材料", "collect", "草药", 5);
        Reward chainReward2 = new Reward(100, 8, new Item("净化粉末", "隐士给你的净化材料", 0, ItemType.OTHER), "收集净化材料");
        Task chainTask2 = new Task("净化材料", "隐士需要一些特殊的草药来制作净化药剂", chainObjective2, chainReward2, false);
        
        // 添加所有任务
        allTasks.add(mainTask1);
        allTasks.add(mainTask2);
        allTasks.add(mainTask3);
        allTasks.add(sideTask1);
        allTasks.add(sideTask2);
        allTasks.add(sideTask3);
        allTasks.add(newTask1);
        allTasks.add(newTask2);
        allTasks.add(newTask3);
        allTasks.add(newTask4);
        allTasks.add(chainTask1);
        allTasks.add(chainTask2);
        
        // 添加扩展地图任务
        allTasks.add(templeTask);
        allTasks.add(tradeTask);
        allTasks.add(herbTask);
        allTasks.add(martialTask);
        allTasks.add(lakeTask);
        allTasks.add(bambooTask);
        allTasks.add(ghostTask);
        allTasks.add(guardianTask);
        
        // 添加到任务映射
        for (Task task : allTasks) {
            taskMap.put(task.getName(), task);
        }
    }
    
    public void checkTaskTriggers(Player player, String triggerType, String targetName) {
        //for (Task task : allTasks) {
        for (Task task : player.getTasks()) {
            if (task.getStatus() == TaskStatus.NOT_ACCEPTED) {
                // 检查是否应该触发任务
                boolean shouldTrigger = false;
                
                switch (task.getName()) {
                    case "初来乍到":
                        shouldTrigger = triggerType.equals("enter") && targetName.equals("村中心");
                        break;
                    case "森林的威胁":
                        shouldTrigger = player.getCurrentRoom() != null && player.getCurrentRoom().getName().equals("森林");
                        break;
                    case "山洞的邪恶":
                        shouldTrigger = player.getCurrentRoom() != null && player.getCurrentRoom().getName().equals("山洞");
                        break;
                    case "村民的忧虑":
                        shouldTrigger = triggerType.equals("talk") && targetName.equals("村民");
                        break;
                    case "收集木材":
                        shouldTrigger = triggerType.equals("talk") && targetName.equals("铁匠");
                        break;
                    case "采集草药":
                        shouldTrigger = triggerType.equals("talk") && targetName.equals("客栈老板");
                        break;
                    // 新增任务触发条件
                    case "森林异变":
                        shouldTrigger = triggerType.equals("talk") && targetName.equals("老猎人");
                        break;
                    case "山顶净化":
                        shouldTrigger = triggerType.equals("talk") && targetName.equals("隐士");
                        break;
                    case "客栈护卫":
                        shouldTrigger = triggerType.equals("talk") && targetName.equals("客栈老板");
                        break;
                    case "珍贵矿石":
                        shouldTrigger = triggerType.equals("talk") && targetName.equals("铁匠");
                        break;
                    case "隐士的指引":
                        shouldTrigger = triggerType.equals("talk") && targetName.equals("隐士");
                        break;
                    case "净化材料":
                        shouldTrigger = triggerType.equals("talk") && targetName.equals("隐士");
                        break;
                    // 扩展地图任务触发条件
                    case "古庙祈福":
                        shouldTrigger = triggerType.equals("talk") && targetName.equals("老和尚");
                        break;
                    case "商人的请求":
                        shouldTrigger = triggerType.equals("talk") && targetName.equals("旅行商人");
                        break;
                    case "稀有草药":
                        shouldTrigger = triggerType.equals("talk") && targetName.equals("药师");
                        break;
                    case "武师考验":
                        shouldTrigger = triggerType.equals("talk") && targetName.equals("武师");
                        break;
                    case "湖心亭之谜":
                        shouldTrigger = triggerType.equals("talk") && targetName.equals("书生");
                        break;
                    case "竹林清修":
                        shouldTrigger = triggerType.equals("talk") && targetName.equals("女隐士");
                        break;
                    case "怨灵净化":
                        shouldTrigger = triggerType.equals("talk") && targetName.equals("女隐士");
                        break;
                    case "宝藏守护者":
                        shouldTrigger = triggerType.equals("talk") && targetName.equals("守护灵");
                        break;
                }

                if (shouldTrigger&&task.getStatus()==TaskStatus.NOT_ACCEPTED) {

                    player.acceptTask(task.getName());
                    // 使用新的acceptTask方法接受任务
                    
                    //task.acceptTask();
                }
            }
        }
    }
    
    /**
     * 接受任务
     * @param player 玩家对象
     * @param task 任务对象
     */
    public void acceptTask(Player player, Task task) {
        // 先检查任务是否已经存在于玩家的任务列表中
        boolean taskExists = false;
        for (Task existingTask : player.getTasks()) {
            if (existingTask.getName().equals(task.getName())) {
                taskExists = true;
                break;
            }
        }
        
        // 如果任务不存在于玩家任务列表中，创建副本并添加
        if (!taskExists) {
            // 创建任务目标的副本
            TaskObjective originalObjective = task.getObjective();
            TaskObjective objectiveCopy = new TaskObjective(
                originalObjective.getDescription(),
                originalObjective.getTargetType(),
                originalObjective.getTargetName(),
                originalObjective.getTargetCount()
            );
            
            // 创建奖励的副本（如果奖励不为null）
            Reward originalReward = task.getReward();
            Reward rewardCopy = null;
            if (originalReward != null) {
                rewardCopy = new Reward(
                    originalReward.getExperience(),
                    originalReward.getReputation(),
                    originalReward.getItem(),
                    originalReward.getDescription()
                );
            }
            
            // 创建任务副本
            Task taskCopy = new Task(
                task.getName(),
                task.getDescription(),
                objectiveCopy,
                rewardCopy,
                task.isMainTask()
            );
            
            // 将任务副本添加到玩家任务列表
            player.getTasks().add(taskCopy);
        }
        
        // 使用任务名称调用Player类的acceptTask方法
        player.acceptTask(task.getName());
        
        // 将已接受的任务添加到activeTasks列表
        for (Task playerTask : player.getTasks()) {
            if (playerTask.getName().equals(task.getName()) && 
                playerTask.getStatus() == TaskStatus.IN_PROGRESS && 
                !activeTasks.contains(playerTask)) {
                activeTasks.add(playerTask);
                break;
            }
        }
    }
    
    /**
     * 通过任务名称接受任务
     * @param player 玩家对象
     * @param taskName 任务名称
     */
    public void acceptTask(Player player, String taskName) {
        // 获取任务对象
        Task task = getTask(taskName);
        if (task != null) {
            // 调用重载方法
            acceptTask(player, task);
        } else {
            System.out.println("找不到任务：" + taskName);
        }
    }
    
    public void updateTaskProgress(Player player, String targetType, String targetName, int amount) {
        // 调用Player的任务更新方法来处理任务进度
        player.updateTaskProgress(targetType, targetName, amount);
        
        // 同步更新activeTasks列表，移除已完成的任务
        // 创建副本以避免迭代时修改异常
        List<Task> completedTasks = new ArrayList<>();
        for (Task task : activeTasks) {
            if (task.getStatus() == TaskStatus.COMPLETED) {
                completedTasks.add(task);
            }
        }
        
        // 从activeTasks中移除已完成的任务并触发后续任务
        for (Task completedTask : completedTasks) {
            activeTasks.remove(completedTask);
            checkFollowUpTasks(player, completedTask);
        }
    }
    
    private void checkFollowUpTasks(Player player, Task completedTask) {
        // 根据完成的任务触发后续任务
        switch (completedTask.getName()) {
            case "初来乍到":
                checkTaskTriggers(player, "enter", "森林");
                break;
            case "森林的威胁":
                checkTaskTriggers(player, "enter", "山洞");
                break;
            case "村民的忧虑":
                // 村民任务完成后，可能会触发其他任务
                break;
            case "森林异变":
                // 完成森林调查后，隐士会出现新的对话
                checkTaskTriggers(player, "talk", "隐士");
                break;
            case "隐士的指引":
                // 隐士指引完成后，触发净化材料任务
                checkTaskTriggers(player, "talk", "隐士");
                break;
            case "净化材料":
                // 收集完净化材料后，触发山顶净化任务
                checkTaskTriggers(player, "talk", "隐士");
                break;
            case "客栈护卫":
                // 完成客栈护卫后，客栈老板关系改善
                if (player.getCurrentRoom().getNpc() != null && player.getCurrentRoom().getNpc().getName().equals("客栈老板")) {
                    player.getCurrentRoom().getNpc().modifyRelationship(20);
                }
                break;
            case "珍贵矿石":
                // 完成珍贵矿石任务后，铁匠关系改善
                if (player.getCurrentRoom().getNpc() != null && player.getCurrentRoom().getNpc().getName().equals("铁匠")) {
                    player.getCurrentRoom().getNpc().modifyRelationship(15);
                }
                break;
            // 扩展地图任务后续
            case "竹林清修":
                // 完成竹林清修后，触发怨灵净化任务
                checkTaskTriggers(player, "talk", "女隐士");
                break;
            case "怨灵净化":
                // 完成怨灵净化后，触发宝藏守护者任务
                checkTaskTriggers(player, "talk", "守护灵");
                break;
            case "武师考验":
                // 完成武师考验后，武师关系改善
                if (player.getCurrentRoom().getNpc() != null && player.getCurrentRoom().getNpc().getName().equals("武师")) {
                    player.getCurrentRoom().getNpc().modifyRelationship(25);
                }
                break;
            case "古庙祈福":
                // 完成古庙祈福后，老和尚关系改善
                if (player.getCurrentRoom().getNpc() != null && player.getCurrentRoom().getNpc().getName().equals("老和尚")) {
                    player.getCurrentRoom().getNpc().modifyRelationship(20);
                }
                break;
            case "商人的请求":
                // 完成商人的请求后，商人关系改善
                if (player.getCurrentRoom().getNpc() != null && player.getCurrentRoom().getNpc().getName().equals("旅行商人")) {
                    player.getCurrentRoom().getNpc().modifyRelationship(15);
                }
                break;
        }
    }
    
    public Task getTask(String taskName) {
        return taskMap.get(taskName);
    }
    
    public List<Task> getAllTasks() {
        return new ArrayList<>(allTasks);
    }
    
    public List<Task> getActiveTasks() {
        return new ArrayList<>(activeTasks);
    }
    
    public List<Task> getMainTasks() {
        List<Task> mainTasks = new ArrayList<>();
        for (Task task : allTasks) {
            if (task.isMainTask()) {
                mainTasks.add(task);
            }
        }
        return mainTasks;
    }
    
    public List<Task> getSideTasks() {
        List<Task> sideTasks = new ArrayList<>();
        for (Task task : allTasks) {
            if (!task.isMainTask()) {
                sideTasks.add(task);
            }
        }
        return sideTasks;
    }
    
    public void showTaskProgress() {
        System.out.println("\n=== 任务进度 ===");
        
        if (activeTasks.isEmpty()) {
            System.out.println("暂无进行中的任务。");
        } else {
            System.out.println("进行中的任务：");
            for (Task task : activeTasks) {
                System.out.println(task);
            }
        }
        
        // 显示已完成的任务
        int completedCount = 0;
        for (Task task : allTasks) {
            if (task.getStatus() == TaskStatus.COMPLETED) {
                completedCount++;
            }
        }
        
        System.out.println("已完成任务：" + completedCount + "/" + allTasks.size());
    }
}