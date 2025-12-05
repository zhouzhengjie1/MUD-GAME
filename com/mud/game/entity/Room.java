package com.mud.game.entity;

import java.io.Serializable;
import java.util.*;

public class Room implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private Map<Direction, Room> exits;
    private List<Item> items;
    private NPC npc;
    private boolean isVisited;
    
    public Room(String name, String description) {
        this.name = name;
        this.description = description;
        this.exits = new HashMap<>();
        this.items = new ArrayList<>();
        this.isVisited = false;
    }
    
    public void connectRoom(Direction direction, Room room) {
        exits.put(direction, room);
    }
    
    public Room getExit(Direction direction) {
        return exits.get(direction);
    }
    
    public void addItem(Item item) {
        items.add(item);
    }
    
    public boolean removeItem(Item item) {
        return items.remove(item);
    }
    
    public Item findItem(String itemName) {
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null;
    }
    
    public String getExitDescription() {
        if (exits.isEmpty()) {
            return "这里没有明显的出口。";
        }
        
        StringBuilder sb = new StringBuilder("出口：");
        for (Map.Entry<Direction, Room> entry : exits.entrySet()) {
            Direction dir = entry.getKey();
            // 显示：中文名称(英文代码):目标房间名
            sb.append(dir.getChineseName()).append("(").append(dir.name().toLowerCase()).append("):").append(entry.getValue().getName()).append(" ");
        }
        return sb.toString();
    }
    
    public String getItemsDescription() {
        if (items.isEmpty()) {
            return "这里什么都没有。";
        }
        
        StringBuilder sb = new StringBuilder("物品：");
        for (Item item : items) {
            sb.append(item.getName()).append(" ");
        }
        return sb.toString();
    }
    
    public void enterRoom() {
        isVisited = true;
        System.out.println("\n=== " + name + " ===");
        System.out.println(description);
        System.out.println(getExitDescription());
        System.out.println(getItemsDescription());
        
        if (npc != null && npc.isAlive()) {
            System.out.println("这里有一个" + npc.getName() + "。");
        }
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Map<Direction, Room> getExits() {
        return exits;
    }
    
    public void setExits(Map<Direction, Room> exits) {
        this.exits = exits;
    }
    
    public List<Item> getItems() {
        return items;
    }
    
    public void setItems(List<Item> items) {
        this.items = items;
    }
    
    public NPC getNpc() {
        return npc;
    }
    
    public void setNpc(NPC npc) {
        this.npc = npc;
    }
    
    public boolean isVisited() {
        return isVisited;
    }
    
    public void setVisited(boolean visited) {
        isVisited = visited;
    }
}