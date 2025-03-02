package com.monitoring;

public class AlertThresholds {
    private static AlertThresholds instance;
    private double cpuThreshold = 80.0;
    private double memoryThreshold = 80.0;

    private AlertThresholds() {}

    public static AlertThresholds getInstance() {
        if (instance == null) {
            instance = new AlertThresholds();
        }
        return instance;
    }

    public double getCpuThreshold() {
        return cpuThreshold;
    }

    public void setCpuThreshold(double cpuThreshold) {
        this.cpuThreshold = cpuThreshold;
    }

    public double getMemoryThreshold() {
        return memoryThreshold;
    }

    public void setMemoryThreshold(double memoryThreshold) {
        this.memoryThreshold = memoryThreshold;
    }
}
