package org.example;

import java.util.ArrayList;
import java.util.List;

public class TaskGroup {
    private String name;
    private List<ComputationTask> tasks;
    private boolean isRunning;

    public TaskGroup(String name) {
        this.name = name;
        this.tasks = new ArrayList<>();
        this.isRunning = false;
    }

    public void addTask(ComputationTask task) {
        tasks.add(task);
    }

    public String getName() {
        return name;
    }

    public List<ComputationTask> getTasks() {
        return tasks;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }
}