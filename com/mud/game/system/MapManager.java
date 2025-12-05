package com.mud.game.system;

import com.mud.game.entity.*;
import java.util.*;

public class MapManager {
    private Map<String, Room> rooms;
    private Room startRoom;
    private Room currentRoom;
    private GameDifficulty difficulty; // 游戏难度
    
    public MapManager() {
        this(GameDifficulty.NORMAL); // 默认使用普通难度
    }
    
    public MapManager(GameDifficulty difficulty) {
        this.rooms = new HashMap<>();
        this.difficulty = difficulty;
        initializeMap();
    }
    
    private void initializeMap() {
        // 创建基础房间
        Room entrance = new Room("村口", "你来到了一个小村庄的入口，村口有一块石碑，上面刻着\"桃源村\"。");
        Room villageSquare = new Room("村中心", "这里是村庄的中心，有几个村民在聊天，中央有一口古井。");
        Room inn = new Room("客栈", "一家简陋的客栈，老板正在柜台后面打盹。");
        Room blacksmith = new Room("铁匠铺", "铁匠铺里传来叮叮当当的打铁声，铁匠正在忙碌地工作。");
        Room forest = new Room("森林", "一片茂密的森林，阳光透过树叶洒下斑驳的光影。");
        Room cave = new Room("山洞", "一个阴暗的山洞，里面传来奇怪的声音。");
        Room mountain = new Room("山顶", "山顶风景优美，可以俯瞰整个村庄。");
        
        // 扩展新区域
        Room eastRoad = new Room("东郊小路", "一条通往东方的小径，路边开满了野花。");
        Room westRoad = new Room("西郊小路", "一条通往西方的蜿蜒小路，远处可以看到一片湖泊。");
        Room lake = new Room("镜湖", "一个宁静的湖泊，湖水清澈如镜，湖边有一座小亭子。");
        Room pavilion = new Room("湖心亭", "建在湖中心的小亭子，是文人墨客喜欢的地方。");
        Room bambooGrove = new Room("竹林", "一片幽静的竹林，微风吹过发出沙沙的声响。");
        Room temple = new Room("古庙", "一座古老的寺庙，香火鼎盛，有很多信徒前来参拜。");
        Room templeBack = new Room("后山", "寺庙后面的山路，通向一片神秘的区域。");
        Room mysticGrove = new Room("神秘林地", "一片充满神秘气息的林地，据说有灵兽出没。");
        Room merchantTent = new Room("商人营地", "几个旅行商人的临时营地，有各种珍稀物品出售。");
        Room trainingGround = new Room("练武场", "一个开阔的练武场地，地面平整，适合练习武艺。");
        Room herbGarden = new Room("药园", "种植各种草药的园子，空气中弥漫着药草的香味。");
        Room abandonedHouse = new Room("废弃小屋", "一间废弃的小屋，看起来已经很久没人居住了。");
        Room secretPath = new Room("密道", "一条隐蔽的地下通道，通向未知的地方。");
        Room undergroundHall = new Room("地下大厅", "一个宽敞的地下大厅，墙上刻着古老的符文。");
        Room treasureRoom = new Room("宝藏室", "一个藏宝室，里面可能藏着珍贵的宝物。");
        
        // 添加物品到基础房间
        entrance.addItem(new Item("石碑", "刻着\"桃源村\"三个大字的石碑", 0, ItemType.OTHER));
        villageSquare.addItem(new Item("古井", "村中心的古井，据说有神奇的力量", 0, ItemType.OTHER));
        inn.addItem(new Item("酒壶", "客栈老板的酒壶", 10, ItemType.MEDICINE));
        blacksmith.addItem(new Equipment("铁剑", "铁匠打造的基础铁剑", 5, ItemType.WEAPON, EquipmentGrade.COMMON, 3));
        forest.addItem(new Item("草药", "森林中采到的草药", 20, ItemType.MEDICINE));
        forest.addItem(new Item("木材", "森林中的木材", 0, ItemType.OTHER));
        cave.addItem(new Item("矿石", "山洞中的稀有矿石", 0, ItemType.OTHER));
        
        // 添加扩展区域的物品
        lake.addItem(new Item("湖水", "清澈的湖水", 0, ItemType.OTHER));
        lake.addItem(new Item("荷花", "湖中的荷花", 5, ItemType.MEDICINE));
        pavilion.addItem(new Item("石桌", "亭中的石桌，上面刻着棋盘", 0, ItemType.OTHER));
        pavilion.addItem(new Item("诗集", "文人墨客留下的诗集", 15, ItemType.OTHER));
        bambooGrove.addItem(new Item("竹笋", "新鲜的竹笋", 8, ItemType.MEDICINE));
        bambooGrove.addItem(new Item("竹竿", "结实的竹竿", 0, ItemType.OTHER));
        temple.addItem(new Item("香烛", "供奉用的香烛", 3, ItemType.OTHER));
        temple.addItem(new Item("佛珠", "开过光的佛珠", 25, ItemType.OTHER));
        templeBack.addItem(new Item("山果", "野生的山果", 12, ItemType.MEDICINE));
        mysticGrove.addItem(new Item("灵草", "散发着微光的灵草", 30, ItemType.MEDICINE));
        mysticGrove.addItem(new Item("灵石", "蕴含灵力的石头", 40, ItemType.OTHER));
        merchantTent.addItem(new Item("商人的背包", "商人的行囊", 0, ItemType.OTHER));
        trainingGround.addItem(new Item("木人桩", "练习用的木人桩", 0, ItemType.OTHER));
        trainingGround.addItem(new Item("石锁", "练武用的石锁", 0, ItemType.OTHER));
        herbGarden.addItem(new Item("人参", "珍贵的人参", 50, ItemType.MEDICINE));
        herbGarden.addItem(new Item("灵芝", "稀有的灵芝", 45, ItemType.MEDICINE));
        herbGarden.addItem(new Item("当归", "常用的药材", 18, ItemType.MEDICINE));
        abandonedHouse.addItem(new Item("旧书", "泛黄的古书", 20, ItemType.OTHER));
        abandonedHouse.addItem(new Item("铜镜", "古老的铜镜", 35, ItemType.OTHER));
        secretPath.addItem(new Item("火把", "照明的火把", 5, ItemType.OTHER));
        undergroundHall.addItem(new Item("符文石", "刻有符文的石头", 60, ItemType.OTHER));
        treasureRoom.addItem(new Item("宝箱", "神秘的宝箱", 100, ItemType.OTHER));
        
        // 添加NPC到房间 - 增强任务功能
        NPC villager = new NPC("村民", 50, 5, new String[]{
            "欢迎来到桃源村！",
            "最近村里不太平，山上有怪物出没。",
            "铁匠铺有好武器，你可以去看看。"
        }, false, "村民", "友善", difficulty);
        villager.addTask("village_intro");
        villager.addTaskDialogue("village_intro", "我们村子最近遇到了一些麻烦，如果你能帮忙就太好了。");
        villageSquare.setNpc(villager);
        
        NPC innkeeper = new NPC("客栈老板", 40, 3, new String[]{
            "客官，要来点什么？",
            "本店的酒可是远近闻名的。",
            "最近生意不好，大家都被山上的怪物吓坏了。"
        }, false, "商人", "精明", difficulty);
        innkeeper.addTask("inn_protection");
        innkeeper.addTaskDialogue("inn_protection", "如果你能清理森林里的野狼，我可以给你一些好酒和金币作为报酬。");
        inn.setNpc(innkeeper);
        
        NPC blacksmithNpc = new NPC("铁匠", 60, 8, new String[]{
            "想要武器吗？我这里有最好的。",
            "山上的怪物越来越猖狂了。",
            "如果你能帮我清理山洞里的怪物，我会给你打造一把好剑。"
        }, false, "铁匠", "豪爽", difficulty);
        blacksmithNpc.addTask("cave_monster");
        blacksmithNpc.addTaskDialogue("cave_monster", "山洞里的哥布林偷走了我的珍贵矿石，如果你能帮我取回来，我会给你打造一把传说中的宝剑！");
        blacksmith.setNpc(blacksmithNpc);
        
        // 添加新的NPC角色
        NPC hunter = new NPC("老猎人", 55, 12, new String[]{
            "我在这片森林打猎已经几十年了。",
            "最近森林里的动物行为很异常。",
            "山顶上似乎有什么东西在作祟。"
        }, false, "猎人", "经验丰富", difficulty);
        hunter.addTask("forest_investigation");
        hunter.addTaskDialogue("forest_investigation", "森林深处有一股邪恶的气息，我需要有人去调查一下。");
        forest.setNpc(hunter);
        
        NPC hermit = new NPC("隐士", 35, 6, new String[]{
            "山顶的景色很美，但最近有些不祥的预感。",
            "我在这里修行多年，感受到了黑暗的威胁。",
            "年轻人，你愿意帮助这片大地吗？"
        }, false, "修行者", "神秘", difficulty);
        hermit.addTask("mountain_cleansing");
        hermit.addTaskDialogue("mountain_cleansing", "山顶被邪恶力量污染了，需要净化仪式来恢复平衡。");
        mountain.setNpc(hermit);
        
        NPC wolf = new NPC("野狼", 30, 8, new String[]{
            "嗷呜~~~"
        }, true, "野兽", "凶猛", difficulty);
        forest.setNpc(wolf);
        
        NPC goblin = new NPC("哥布林", 40, 10, new String[]{
            "嘿嘿嘿，又一个送死的！",
            "这是我的地盘，滚出去！"
        }, true, "怪物", "邪恶", difficulty);
        cave.setNpc(goblin);
        
        // 扩展区域的新NPC
        NPC merchant = new NPC("旅行商人", 45, 7, new String[]{
            "走过路过不要错过，我这里有最好的货物！",
            "我从远方带来了珍贵的物品。",
            "如果你有金币，我们可以做笔交易。"
        }, false, "商人", "精明", difficulty);
        merchant.addTask("merchant_trade");
        merchant.addTaskDialogue("merchant_trade", "如果你能帮我收集一些稀有的灵石，我可以给你一些特殊的装备。");
        merchantTent.setNpc(merchant);
        
        NPC monk = new NPC("老和尚", 65, 9, new String[]{
            "阿弥陀佛，施主有礼了。",
            "这座古庙已经存在了几百年。",
            "后山有些不寻常的气息，施主要小心。"
        }, false, "僧人", "慈悲", difficulty);
        monk.addTask("temple_ritual");
        monk.addTaskDialogue("temple_ritual", "寺庙的香火最近不旺，如果你能帮忙采集一些草药，我们可以为你祈福。");
        temple.setNpc(monk);
        
        NPC herbalist = new NPC("药师", 50, 6, new String[]{
            "药草是大自然的馈赠。",
            "我种植这些草药已经几十年了。",
            "有些稀有的草药只能在特殊的地方找到。"
        }, false, "药师", "博学", difficulty);
        herbalist.addTask("rare_herbs");
        herbalist.addTaskDialogue("rare_herbs", "我需要一些神秘林地里的灵草来配制特殊的药剂，你能帮我采集吗？");
        herbGarden.setNpc(herbalist);
        
        NPC martialArtist = new NPC("武师", 70, 15, new String[]{
            "练武之道，贵在坚持。",
            "我看你骨骼精奇，是个练武的好材料。",
            "如果你能通过我的考验，我可以教你一些招式。"
        }, false, "武师", "严厉", difficulty);
        martialArtist.addTask("martial_test");
        martialArtist.addTaskDialogue("martial_test", "想要学习真正的武艺，需要先证明你的实力，去击败竹林里的盗贼吧。");
        trainingGround.setNpc(martialArtist);
        
        NPC scholar = new NPC("书生", 35, 4, new String[]{
            "湖心亭的风景真是美不胜收。",
            "我在这里寻找灵感写诗。",
            "最近湖边有些奇怪的事情发生。"
        }, false, "书生", "文雅", difficulty);
        scholar.addTask("lake_mystery");
        scholar.addTaskDialogue("lake_mystery", "湖心亭最近晚上会出现奇怪的光芒，你能帮我去调查一下吗？");
        pavilion.setNpc(scholar);
        
        NPC hermitWoman = new NPC("女隐士", 40, 8, new String[]{
            "这片竹林是我的清修之地。",
            "远离尘嚣，方能心境平和。",
            "竹林深处有些不干净的东西，你要小心。"
        }, false, "隐士", "神秘", difficulty);
        hermitWoman.addTask("bamboo_cleansing");
        hermitWoman.addTaskDialogue("bamboo_cleansing", "竹林里最近出现了一些盗贼，扰乱了这里的宁静，你能帮我驱赶他们吗？");
        bambooGrove.setNpc(hermitWoman);
        
        NPC bandit = new NPC("竹林盗贼", 45, 11, new String[]{
            "此路是我开，此树是我栽！",
            "要想从此过，留下买路财！",
            "兄弟们，上！"
        }, true, "盗贼", "贪婪", difficulty);
        bambooGrove.setNpc(bandit);
        
        NPC ghost = new NPC("怨灵", 55, 13, new String[]{
            "还我命来~~~",
            "为什么...为什么是我...",
            "离开这里，否则你将遭受同样的命运！"
        }, true, "怨灵", "怨恨", difficulty);
        abandonedHouse.setNpc(ghost);
        
        NPC guardian = new NPC("守护灵", 80, 18, new String[]{
            "这里是神圣之地，不容侵犯！",
            "只有通过考验的人，才能获得宝藏。",
            "证明你的勇气和智慧吧！"
        }, true, "守护灵", "威严", difficulty);
        undergroundHall.setNpc(guardian);
        
        // 连接基础房间
        entrance.connectRoom(Direction.EAST, villageSquare);
        villageSquare.connectRoom(Direction.WEST, entrance);
        
        villageSquare.connectRoom(Direction.NORTH, inn);
        villageSquare.connectRoom(Direction.SOUTH, blacksmith);
        villageSquare.connectRoom(Direction.EAST, forest);
        
        inn.connectRoom(Direction.SOUTH, villageSquare);
        blacksmith.connectRoom(Direction.NORTH, villageSquare);
        forest.connectRoom(Direction.WEST, villageSquare);
        forest.connectRoom(Direction.EAST, cave);
        forest.connectRoom(Direction.UP, mountain);
        
        cave.connectRoom(Direction.WEST, forest);
        mountain.connectRoom(Direction.DOWN, forest);
        
        // 连接扩展区域
        villageSquare.connectRoom(Direction.NORTHEAST, eastRoad);
        villageSquare.connectRoom(Direction.NORTHWEST, westRoad);
        
        eastRoad.connectRoom(Direction.SOUTHWEST, villageSquare);
        eastRoad.connectRoom(Direction.EAST, temple);
        eastRoad.connectRoom(Direction.NORTH, trainingGround);
        
        westRoad.connectRoom(Direction.SOUTHEAST, villageSquare);
        westRoad.connectRoom(Direction.WEST, lake);
        westRoad.connectRoom(Direction.NORTH, bambooGrove);
        
        lake.connectRoom(Direction.EAST, westRoad);
        lake.connectRoom(Direction.NORTH, pavilion);
        
        pavilion.connectRoom(Direction.SOUTH, lake);
        
        temple.connectRoom(Direction.WEST, eastRoad);
        temple.connectRoom(Direction.NORTH, templeBack);
        temple.connectRoom(Direction.EAST, herbGarden);
        
        templeBack.connectRoom(Direction.SOUTH, temple);
        templeBack.connectRoom(Direction.UP, mysticGrove);
        
        mysticGrove.connectRoom(Direction.DOWN, templeBack);
        mysticGrove.connectRoom(Direction.EAST, merchantTent);
        
        bambooGrove.connectRoom(Direction.SOUTH, westRoad);
        bambooGrove.connectRoom(Direction.EAST, abandonedHouse);
        bambooGrove.connectRoom(Direction.UP, mountain);
        
        abandonedHouse.connectRoom(Direction.WEST, bambooGrove);
        abandonedHouse.connectRoom(Direction.DOWN, secretPath);
        
        secretPath.connectRoom(Direction.UP, abandonedHouse);
        secretPath.connectRoom(Direction.EAST, undergroundHall);
        
        undergroundHall.connectRoom(Direction.WEST, secretPath);
        undergroundHall.connectRoom(Direction.NORTH, treasureRoom);
        
        treasureRoom.connectRoom(Direction.SOUTH, undergroundHall);
        
        trainingGround.connectRoom(Direction.SOUTH, eastRoad);
        
        herbGarden.connectRoom(Direction.WEST, temple);
        
        merchantTent.connectRoom(Direction.WEST, mysticGrove);
        
        // 添加到地图管理器
        rooms.put("村口", entrance);
        rooms.put("村中心", villageSquare);
        rooms.put("客栈", inn);
        rooms.put("铁匠铺", blacksmith);
        rooms.put("森林", forest);
        rooms.put("山洞", cave);
        rooms.put("山顶", mountain);
        
        // 添加扩展区域到地图管理器
        rooms.put("东郊小路", eastRoad);
        rooms.put("西郊小路", westRoad);
        rooms.put("镜湖", lake);
        rooms.put("湖心亭", pavilion);
        rooms.put("古庙", temple);
        rooms.put("庙后小径", templeBack);
        rooms.put("竹林", bambooGrove);
        rooms.put("废弃小屋", abandonedHouse);
        rooms.put("秘密通道", secretPath);
        rooms.put("地下大厅", undergroundHall);
        rooms.put("宝藏室", treasureRoom);
        rooms.put("训练场", trainingGround);
        rooms.put("药草园", herbGarden);
        rooms.put("神秘树林", mysticGrove);
        rooms.put("商人帐篷", merchantTent);
        
        // 设置初始房间
        startRoom = entrance;
        currentRoom = entrance;
    }
    
    public Room getRoom(String name) {
        return rooms.get(name);
    }
    
    public Room getRoomByName(String name) {
        return rooms.get(name);
    }
    
    public Room getStartRoom() {
        return startRoom;
    }
    
    public void showMapInfo() {
        System.out.println("\n=== 地图信息 ===");
        System.out.println("当前地图包含以下区域：");
        for (String roomName : rooms.keySet()) {
            System.out.println("- " + roomName);
        }
    }
    
    public void setDifficulty(GameDifficulty difficulty) {
        this.difficulty = difficulty;
        // 重新初始化地图，使NPC使用新的难度设置
        initializeMap();
    }
    
    /**
     * 获取所有房间的集合
     * @return 所有房间的集合
     */
    public Collection<Room> getRooms() {
        return rooms.values();
    }
    
    public GameDifficulty getDifficulty() {
        return difficulty;
    }
}