# 文字冒险游戏 - 详细文档

## 游戏概述
这是一个基于Java开发的文字冒险游戏，玩家在游戏中扮演一名冒险者，通过探索地图、与NPC对话、完成任务、与怪物战斗等方式进行游戏。游戏采用回合制战斗系统，拥有完整的任务系统、装备系统、存档系统等核心功能。

## 核心实体类

### Player类
玩家角色的核心类，实现了Serializable和BattleEntity接口。

**主要属性：**
- `name`：玩家名称
- `hp/maxHp`：生命值/最大生命值
- `atk`：攻击力
- `def`：防御力
- `reputation`：侠义值
- `level/experience`：等级/经验值
- `money`：金币
- `currentRoom/currentRoomName`：当前所在房间
- `backpack`：背包物品列表
- `tasks`：任务列表
- 战斗相关属性：`element`(元素属性)、`dodgeRate`(闪避率)、`critRate`(暴击率)、`critDamage`(暴击伤害)等

**主要方法：**
- `takeItem(String itemName)`：拾取房间中的物品
- `move(Direction direction)`：向指定方向移动
- `attack(NPC npc)`：攻击NPC
- `talkToNPC()`：与当前房间的NPC对话
- `showStatus()`：显示角色状态
- `acceptTask(String taskName)`：接受任务
- `updateTaskProgress(String targetType, String targetName, int amount)`：更新任务进度
- `showTasks()`：显示任务列表

### NPC类
非玩家角色类，实现了Serializable和BattleEntity接口。

**主要属性：**
- `name`：NPC名称
- `hp/maxHp`：生命值/最大生命值
- `atk/def`：攻击力/防御力
- `level`：等级
- `element`：元素属性
- `dodgeRate/critRate/critDamage`：战斗属性
- `dialogue`：对话内容数组
- `isHostile`：是否敌对
- `isAlive`：是否存活
- `availableTasks`：可提供的任务列表
- `taskDialogues`：任务相关对话
- `profession/personality`：职业/性格
- `relationship`：与玩家的关系值

**主要方法：**
- `talk(Player player)`：与玩家对话
- `attack(Player player)`：攻击玩家
- `showNPCInfo()`：显示NPC信息

### Room类
游戏中的房间类，实现了Serializable接口。

**主要属性：**
- `name`：房间名称
- `description`：房间描述
- `exits`：出口方向映射（Direction到Room的映射）
- `items`：房间中的物品列表
- `npc`：房间中的NPC
- `isVisited`：是否已访问

**主要方法：**
- `enterRoom()`：进入房间时显示信息
- `connectRoom(Direction direction, Room room)`：连接其他房间
- `getExit(Direction direction)`：获取指定方向的出口
- `getExitDescription()`：获取出口描述

### Item类
游戏中的物品类。

**主要属性：**
- `name`：物品名称
- `description`：物品描述
- `effect`：物品效果值
- `type`：物品类型（MEDICINE, WEAPON, CLUE, OTHER等）

**主要方法：**
- `use(Player player)`：使用物品，根据类型产生不同效果

### Task类
任务类，实现了Serializable接口。

**主要属性：**
- `name`：任务名称
- `description`：任务描述
- `status`：任务状态（NOT_ACCEPTED, IN_PROGRESS, COMPLETED）
- `objective`：任务目标
- `reward`：任务奖励
- `isMainTask`：是否为主线任务

**主要方法：**
- `checkComplete(Player player)`：检查任务是否完成
- `updateProgress(String targetType, String targetName, int amount)`：更新任务进度
- `acceptTask()`：接受任务

### TaskObjective类
任务目标类，实现了Serializable接口。

**主要属性：**
- `description`：目标描述
- `targetCount`：目标总数
- `currentCount`：当前进度数
- `targetType`：目标类型（"kill", "collect", "talk", "explore"）
- `targetName`：目标名称

**主要方法：**
- `isCompleted()`：检查目标是否完成
- `updateProgress(int amount)`：更新进度

### TaskStatus枚举
任务状态枚举类。

**枚举值：**
- `NOT_ACCEPTED`：未接取
- `IN_PROGRESS`：进行中
- `COMPLETED`：已完成

### Direction枚举
方向枚举类，定义了游戏中的10个方向：东、南、西、北、上、下、东北、西北、东南、西南。

## 游戏系统

### MudGame类
游戏主类，负责游戏的初始化和主循环。

**主要属性：**
- `player`：玩家对象
- `commandParser`：命令解析器
- `mapManager`：地图管理器
- `battleEngine`：战斗引擎
- `taskManager`：任务管理器
- `scanner`：输入扫描器
- `gameRunning`：游戏运行状态

**主要方法：**
- `start()`：开始游戏
- `createPlayer()`：创建玩家
- `initializeGame()`：初始化游戏
- `showWelcomeMessage()`：显示欢迎信息
- `gameLoop()`：游戏主循环
- `checkGameState()`：检查游戏状态
- `loadGame(String saveName)`：加载游戏存档

### MapManager类
地图管理器，负责管理游戏中的所有房间和地图连接。

**主要属性：**
- `rooms`：所有房间的映射
- `startRoom`：起始房间
- `currentRoom`：当前房间

**主要方法：**
- `initializeMap()`：初始化地图
- `getStartRoom()`：获取起始房间
- `getRoomByName(String name)`：根据名称获取房间
- `connectRoom(Room from, Direction direction, Room to)`：连接两个房间

### BattleEngine类
战斗引擎的基础实现。

**主要方法：**
- `startBattle(Player player, NPC enemy)`：开始战斗
- `displayBattleStatus(Player player, NPC enemy)`：显示战斗状态
- `getPlayerAction()`：获取玩家战斗动作选择
- `playerAttack(Player player, NPC enemy)`：玩家攻击
- `playerDefend(Player player)`：玩家防御
- `useItem(Player player)`：使用物品
- `tryEscape(Player player, NPC enemy)`：尝试逃跑

### EnhancedBattleEngine类
增强版战斗引擎，提供更复杂的战斗功能。

**主要属性：**
- `random`：随机数生成器
- `scanner`：输入扫描器
- `enemies`：敌人列表
- `enemyTargets`：敌人目标映射
- `activeEffects`：活跃状态效果映射

**主要方法：**
- `startBattle(Player player, NPC enemy)`：开始战斗
- 提供更丰富的战斗机制，包括状态效果系统

### TaskManager类
任务管理器，负责管理游戏中的所有任务。

**主要属性：**
- `allTasks`：所有任务列表
- `activeTasks`：活跃任务列表
- `taskMap`：任务映射

**主要方法：**
- `initializeTasks()`：初始化任务
- `checkTaskTriggers(Player player, String triggerType, String targetName)`：检查任务触发条件
- `updateTaskProgress(Player player, String targetType, String targetName, int amount)`：更新任务进度
- `checkFollowUpTasks(Player player, Task completedTask)`：检查后续任务
- `showTaskProgress()`：显示任务进度
- `getMainTasks()`：获取主线任务
- `getSideTasks()`：获取支线任务

### EquipmentManager类
装备管理器，负责装备的强化、修复和商店功能。

**主要属性：**
- `equipmentTemplates`：装备模板映射
- `playerEquipment`：玩家装备映射

## 命令系统

### Command接口
所有命令类的接口，定义了命令的基本方法。

**方法：**
- `execute(Player player, String[] args)`：执行命令
- `getDescription()`：获取命令描述
- `getUsage()`：获取命令用法

### CommandParser类
命令解析器，负责解析玩家输入的命令并执行相应的命令类。

### 主要命令类

1. **HelpCommand**：显示帮助信息
2. **QuitCommand**：退出游戏
3. **TaskCommand**：查看任务列表
4. **AcceptTaskCommand**：接受任务
5. **StatusCommand**：查看角色状态
6. **BackpackCommand**：查看背包物品
7. **GoCommand**：向指定方向移动
8. **TakeCommand**：拾取物品
9. **UseCommand**：使用物品
10. **AttackCommand**：攻击NPC
11. **TalkCommand**：与NPC对话

## 游戏功能模块

### 任务系统
- 支持主线任务和支线任务
- 任务状态：未接取、进行中、已完成
- 任务目标类型：击杀怪物、收集物品、与NPC对话、探索地点
- 任务触发机制：通过与NPC对话、进入特定区域等方式触发
- 任务奖励：经验值、金币、装备、物品等
- 连锁任务系统：完成一个任务后可能触发新的任务

### 战斗系统
- 回合制战斗机制
- 玩家可选择的动作：攻击、防御、使用物品、逃跑
- 战斗属性：生命值、攻击力、防御力、闪避率、暴击率、暴击伤害
- 元素属性系统：火、水、土、风、雷、光、暗，存在属性克制关系
- 状态效果系统：增强版战斗引擎支持各种状态效果

### 地图系统
- 基于房间的地图结构
- 支持10个方向的移动：东、南、西、北、上、下、东北、西北、东南、西南
- 房间包含物品、NPC、出口等元素
- 房间描述和探索功能

### 物品系统
- 物品类型：武器、药水、线索、其他
- 物品使用效果：恢复生命、提升属性、显示信息等
- 背包管理功能

### 存档系统
- 游戏进度保存和加载功能
- 支持多个存档槽位
- 保存玩家状态、物品、任务进度等信息
- NPC血量状态保存和恢复：当玩家保存游戏后，所有NPC的当前血量状态会被正确记录，并在游戏重新加载时准确恢复

## 游戏流程

1. **游戏启动**：通过GameLauncher启动游戏，显示主菜单
2. **角色创建**：玩家输入角色名称，系统创建新角色
3. **游戏初始化**：设置玩家起始位置、给予初始物品、加载初始任务
4. **游戏循环**：
   - 显示当前房间信息
   - 接收玩家命令输入
   - 解析并执行命令
   - 更新游戏状态
   - 检查任务触发条件
5. **游戏结束条件**：
   - 玩家生命值归0
   - 完成所有主线任务
   - 玩家主动退出

## 游戏地图结构

游戏世界包含多个区域，主要包括：

- **桃源村**：
  - 村口（起始位置）
  - 村中心
  - 客栈
  - 铁匠铺
- **野外区域**：
  - 森林
  - 山洞
  - 山顶
  - 东郊小路
  - 西郊小路
  - 镜湖
  - 湖心亭
  - 竹林

这些区域通过各种方向连接，玩家可以自由探索。

## 游戏特色

1. **丰富的任务系统**：包含主线任务和多个支线任务，任务之间存在关联性
2. **策略性战斗**：基于属性和元素克制的战斗系统，需要玩家制定策略
3. **开放探索**：玩家可以自由探索游戏世界，发现隐藏的秘密
4. **NPC交互**：与各种NPC对话，建立关系，获取任务
5. **物品收集与使用**：收集各种物品，合理使用以增强实力

## 扩展功能

游戏代码结构设计良好，支持以下扩展：

1. 增加新的地图区域和房间
2. 添加更多类型的NPC和怪物
3. 扩展任务系统，增加更复杂的任务链
4. 增加新的装备和物品
5. 增强战斗系统，添加更多技能和状态效果
6. 实现多人游戏功能
