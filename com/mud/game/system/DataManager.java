package com.mud.game.system;

import com.mud.game.entity.*;
import java.io.*;
import java.util.*;

public class DataManager {
    private static final String SAVE_DIR = "saves/";
    private static final String SAVE_FILE_EXTENSION = ".dat";
    
    public DataManager() {
        // 确保保存目录存在
        File saveDir = new File(SAVE_DIR);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
    }
    
    public boolean saveGame(Player player, String saveName) {
        try {
            String fileName = SAVE_DIR + saveName + SAVE_FILE_EXTENSION;
            
            // 创建保存数据对象
            SaveData saveData = new SaveData();
            saveData.setPlayer(player);
            saveData.setSaveTime(new Date());
            saveData.setGameVersion("1.0");
            
            // 序列化保存数据
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
                oos.writeObject(saveData);
                return true;
            }
        } catch (IOException e) {
            System.err.println("保存游戏失败: " + e.getMessage());
            return false;
        }
    }
    
    public Player loadGame(String saveName) {
        try {
            String fileName = SAVE_DIR + saveName + SAVE_FILE_EXTENSION;
            File saveFile = new File(fileName);
            
            if (!saveFile.exists()) {
                System.out.println("存档文件不存在！");
                return null;
            }
            
            // 反序列化保存数据
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile))) {
                SaveData saveData = (SaveData) ois.readObject();
                return saveData.getPlayer();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("加载游戏失败: " + e.getMessage());
            return null;
        }
    }
    
    public List<String> getAvailableSaves() {
        List<String> saves = new ArrayList<>();
        File saveDir = new File(SAVE_DIR);
        
        if (saveDir.exists() && saveDir.isDirectory()) {
            File[] files = saveDir.listFiles((dir, name) -> name.endsWith(SAVE_FILE_EXTENSION));
            if (files != null) {
                for (File file : files) {
                    String fileName = file.getName();
                    String saveName = fileName.substring(0, fileName.length() - SAVE_FILE_EXTENSION.length());
                    saves.add(saveName);
                }
            }
        }
        
        return saves;
    }
    
    public boolean deleteSave(String saveName) {
        try {
            String fileName = SAVE_DIR + saveName + SAVE_FILE_EXTENSION;
            File saveFile = new File(fileName);
            
            if (saveFile.exists()) {
                return saveFile.delete();
            }
            return false;
        } catch (Exception e) {
            System.err.println("删除存档失败: " + e.getMessage());
            return false;
        }
    }
    
    public boolean showSaveInfo(String saveName) {
        try {
            String fileName = SAVE_DIR + saveName + SAVE_FILE_EXTENSION;
            File saveFile = new File(fileName);
            
            if (!saveFile.exists()) {
                System.out.println("存档文件不存在！");
                return false;
            }
            
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile))) {
                SaveData saveData = (SaveData) ois.readObject();
                
                System.out.println("=== 存档信息 ===");
                System.out.println("存档名称: " + saveName);
                System.out.println("保存时间: " + saveData.getSaveTime());
                System.out.println("游戏版本: " + saveData.getGameVersion());
                System.out.println("角色名称: " + saveData.getPlayer().getName());
                System.out.println("角色等级: " + saveData.getPlayer().getLevel());
                System.out.println("当前位置: " + saveData.getPlayer().getCurrentRoomName());
                System.out.println("拥有金币: " + saveData.getPlayer().getMoney());
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("读取存档信息失败: " + e.getMessage());
            return false;
        }
        return true;
    }
    
    // 内部类用于保存数据
    private static class SaveData implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private Player player;
        private Date saveTime;
        private String gameVersion;
        
        public Player getPlayer() { return player; }
        public void setPlayer(Player player) { this.player = player; }
        
        public Date getSaveTime() { return saveTime; }
        public void setSaveTime(Date saveTime) { this.saveTime = saveTime; }
        
        public String getGameVersion() { return gameVersion; }
        public void setGameVersion(String gameVersion) { this.gameVersion = gameVersion; }
    }
}