package com.mud.game.entity;

import java.io.Serializable;

public class Reward implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int experience;
    private int reputation;
    private Item item;
    private String description;
    
    public Reward(int experience, int reputation, Item item, String description) {
        this.experience = experience;
        this.reputation = reputation;
        this.item = item;
        this.description = description;
    }
    
    public void giveReward(Player player) {
        System.out.println("获得奖励：" + description);
        
        if (experience > 0) {
            System.out.println("获得经验值：" + experience);
        }
        
        if (reputation != 0) {
            player.setReputation(player.getReputation() + reputation);
            if (reputation > 0) {
                System.out.println("侠义值提升：" + reputation);
            } else {
                System.out.println("侠义值降低：" + Math.abs(reputation));
            }
        }
        
        if (item != null) {
            player.getBackpack().add(item);
            System.out.println("获得物品：" + item.getName());
        }
    }
    
    public int getExperience() {
        return experience;
    }
    
    public void setExperience(int experience) {
        this.experience = experience;
    }
    
    public int getReputation() {
        return reputation;
    }
    
    public void setReputation(int reputation) {
        this.reputation = reputation;
    }
    
    public Item getItem() {
        return item;
    }
    
    public void setItem(Item item) {
        this.item = item;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}