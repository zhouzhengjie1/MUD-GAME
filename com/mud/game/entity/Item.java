package com.mud.game.entity;

import java.io.Serializable;

public class Item implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private double effect;
    private ItemType type;
    
    public Item(String name, String description, double effect, ItemType type) {
        this.name = name;
        this.description = description;
        this.effect = effect;
        this.type = type;
    }
    
    public void use(Player player) {
        switch (type) {
            case MEDICINE:
                player.setHp(player.getHp() + (int)Math.round(effect));
                System.out.printf("使用了%s，恢复了%.2f点生命值！\n", name, effect);
                break;
            case WEAPON:
                player.setAtk(player.getAtk() + (int)Math.round(effect));
                System.out.printf("装备了%s，攻击力提升了%.2f点！\n", name, effect);
                break;
            case ARMOR:
                player.setDef(player.getDef() + (int)Math.round(effect));
                System.out.printf("装备了%s，防御力提升了%.2f点！\n", name, effect);
                break;
            case CLUE:
                System.out.println("查看了线索：" + description);
                break;
            default:
                System.out.println("使用了" + name + "，但似乎没有什么效果。");
                break;
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
    
    public double getEffect() {
        return effect;
    }
    
    public void setEffect(double effect) {
        this.effect = effect;
    }
    
    public ItemType getType() {
        return type;
    }
    
    public void setType(ItemType type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        return name + " (" + type + ") - " + description;
    }
}