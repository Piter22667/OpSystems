package org.example;

import java.io.Serializable;

public class ComputationTask implements Serializable {
    private double argument;
    private double result;
    private boolean isCompleted;

    public ComputationTask(double argument) {
        this.argument = argument;
        this.isCompleted = false;
    }

    public double getArgument() {
        return argument;
    }

    public double getResult() {
        return result;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setResult(double result) {
        this.result = result;
        this.isCompleted = true;  // Коли встановлюємо результат true, то задача буде вважатись виконаною
    }
}
