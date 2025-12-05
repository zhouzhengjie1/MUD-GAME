package com.mud.game.command;

import com.mud.game.entity.Player;

public class NPCInfoCommand implements Command {
    @Override
    public void execute(Player player, String[] args) {
        if (player.getCurrentRoom().getNpc() != null) {
            player.getCurrentRoom().getNpc().showNPCInfo();
        } else {
            System.out.println("这里没有NPC。");
        }
    }
    
    @Override
    public String getDescription() {
        return "查看当前房间NPC的详细信息";
    }
    
    @Override
    public String getUsage() {
        return "npcinfo";
    }
}