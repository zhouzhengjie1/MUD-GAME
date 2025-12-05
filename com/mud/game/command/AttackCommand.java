package com.mud.game.command;

import com.mud.game.entity.Player;

public class AttackCommand implements Command {
    @Override
    public void execute(Player player, String[] args) {
        if (player.getCurrentRoom().getNpc() == null) {
            System.out.println("这里没有可以攻击的目标。");
            return;
        }
        
        player.attack(player.getCurrentRoom().getNpc());
    }
    
    @Override
    public String getDescription() {
        return "攻击当前房间的NPC";
    }
    
    @Override
    public String getUsage() {
        return "attack";
    }
}