package com.mud.game.command;

import com.mud.game.entity.Player;

public class TalkCommand implements Command {
    @Override
    public void execute(Player player, String[] args) {
        player.talkToNPC();
    }
    
    @Override
    public String getDescription() {
        return "与当前房间的NPC对话";
    }
    
    @Override
    public String getUsage() {
        return "talk";
    }
}