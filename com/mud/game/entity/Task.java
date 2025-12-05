package com.mud.game.entity;

import java.io.Serializable;

public class Task implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private TaskStatus status;
    private TaskObjective objective;
    private Reward reward;
    private boolean isMainTask;
    
    public Task(String name, String description, TaskObjective objective, Reward reward, boolean isMainTask) {
        this.name = name;
        this.description = description;
        this.objective = objective;
        this.reward = reward;
        this.status = TaskStatus.NOT_ACCEPTED;
        this.isMainTask = isMainTask;
    }
    
    public boolean checkComplete(Player player) {
        if (status == TaskStatus.COMPLETED) {
            return true;
        }
        
        if (objective.isCompleted()) {
            status = TaskStatus.COMPLETED;
            System.out.println("任务完成：" + name);
            if (reward != null) {
                reward.giveReward(player);
            }
            return true;
        }
        
        return false;
    }
    
    public void updateProgress(String targetType, String targetName, int amount) {
        if (status != TaskStatus.IN_PROGRESS) {
            return;
        }
        
        if (objective.getTargetType().equals(targetType) && 
            (objective.getTargetName() == null || objective.getTargetName().equals(targetName))) {
            objective.updateProgress(amount);
            System.out.println("任务进度更新：" + objective);
        }
    }
    
    public void acceptTask() {
        if (status == TaskStatus.NOT_ACCEPTED) {
            status = TaskStatus.IN_PROGRESS;
            System.out.println("接受任务：" + name);
            System.out.println("任务描述：" + description);
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
    
    public TaskStatus getStatus() {
        return status;
    }
    
    public void setStatus(TaskStatus status) {
        this.status = status;
    }
    
    public TaskObjective getObjective() {
        return objective;
    }
    
    public void setObjective(TaskObjective objective) {
        this.objective = objective;
    }
    
    public Reward getReward() {
        return reward;
    }
    
    public void setReward(Reward reward) {
        this.reward = reward;
    }
    
    public boolean isMainTask() {
        return isMainTask;
    }
    
    public void setMainTask(boolean mainTask) {
        isMainTask = mainTask;
    }
    
    @Override
    public String toString() {
        return name + " [" + status + "] - " + description + "\n进度：" + objective;
    }
}