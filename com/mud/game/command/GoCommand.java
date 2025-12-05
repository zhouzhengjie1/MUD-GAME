package com.mud.game.command;

import com.mud.game.entity.*;

public class GoCommand implements Command {
    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            System.out.println("请指定方向。例如：go east 或 go 东");
            return;
        }
        
        String directionStr = args[1];
        try {
            // 尝试直接匹配英文枚举名称
            Direction direction = Direction.valueOf(directionStr.toUpperCase());
            player.move(direction);
        } catch (IllegalArgumentException e) {
            // 如果英文匹配失败，尝试匹配中文名称
            Direction direction = findDirectionByChineseName(directionStr);
            if (direction != null) {
                player.move(direction);
            } else {
                System.out.println("无效的方向。可用方向：east(东), south(南), west(西), north(北), up(上), down(下), northeast(东北), northwest(西北), southeast(东南), southwest(西南)");
            }
        }
    }
    
    /**
     * 根据中文名称查找方向枚举
     */
    private Direction findDirectionByChineseName(String chineseName) {
        for (Direction dir : Direction.values()) {
            if (dir.getChineseName().equals(chineseName)) {
                return dir;
            }
        }
        return null;
    }
    
    @Override
    public String getDescription() {
        return "向指定方向移动";
    }
    
    @Override
    public String getUsage() {
        return "go <方向>";
    }
}