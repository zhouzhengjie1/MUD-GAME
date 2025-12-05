package com.mud.game.command;

import com.mud.game.entity.Player;

public interface Command {
    void execute(Player player, String[] args);
    String getDescription();
    String getUsage();
}