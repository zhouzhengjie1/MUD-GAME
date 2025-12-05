package com.mud.game.command;

import com.mud.game.entity.Player;
import com.mud.game.system.EquipmentManager;
import java.util.*;

public class CommandParser {
    private Map<String, Command> commands;
    private EquipmentManager equipmentManager;
    
    public CommandParser() {
        commands = new HashMap<>();
        this.equipmentManager = new EquipmentManager();
        initializeCommands();
    }
    
    private void initializeCommands() {
        commands.put("go", new GoCommand());
        commands.put("take", new TakeCommand());
        commands.put("use", new UseCommand());
        commands.put("attack", new AttackCommand());
        commands.put("talk", new TalkCommand());
        commands.put("status", new StatusCommand());
        commands.put("backpack", new BackpackCommand());
        commands.put("task", new TaskCommand());
        commands.put("accept", new AcceptTaskCommand());
        commands.put("save", new SaveCommand());
        commands.put("load", new LoadCommand());
        commands.put("help", new HelpCommand());
        commands.put("quit", new QuitCommand());
        commands.put("exit", new QuitCommand()); // 别名
        commands.put("npcinfo", new NPCInfoCommand());
        commands.put("upgrade", new UpgradeCommand(equipmentManager));
        //commands.put("强化", new UpgradeCommand(equipmentManager)); // 中文别名
        commands.put("unequip", new UnequipCommand());
    }
    
    public void parseAndExecute(String input, Player player) {
        if (input == null || input.trim().isEmpty()) {
            return;
        }
        
        String[] parts = input.trim().toLowerCase().split("\\s+");
        String commandName = parts[0];
        
        Command command = commands.get(commandName);
        if (command != null) {
            try {
                command.execute(player, parts);
            } catch (Exception e) {
                System.out.println("执行命令时出错：" + e.getMessage());
            }
        } else {
            System.out.println("未知命令：" + commandName);
            System.out.println("输入 'help' 查看可用命令。");
        }
    }
    
    public void showAvailableCommands() {
        System.out.println("\n=== 可用命令 ===");
        for (Map.Entry<String, Command> entry : commands.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue().getDescription());
        }
    }
    
    public Command getCommand(String name) {
        return commands.get(name);
    }
}