package com.monitoring;

import javafx.beans.property.*;

public class ServerData {
    private final StringProperty id;
    private final DoubleProperty cpuUsage;
    private final DoubleProperty memoryUsage;
    private final DoubleProperty diskUsage;
    private final DoubleProperty responseTime;
    private final IntegerProperty activeConnections;

    public ServerData(String id, double cpuUsage, double memoryUsage, 
                     double diskUsage, double responseTime, int activeConnections) {
        this.id = new SimpleStringProperty(id);
        this.cpuUsage = new SimpleDoubleProperty(cpuUsage);
        this.memoryUsage = new SimpleDoubleProperty(memoryUsage);
        this.diskUsage = new SimpleDoubleProperty(diskUsage);
        this.responseTime = new SimpleDoubleProperty(responseTime);
        this.activeConnections = new SimpleIntegerProperty(activeConnections);
    }

    // Getters
    public String getId() { return id.get(); }
    public double getCpuUsage() { return cpuUsage.get(); }
    public double getMemoryUsage() { return memoryUsage.get(); }
    public double getDiskUsage() { return diskUsage.get(); }
    public double getResponseTime() { return responseTime.get(); }
    public int getActiveConnections() { return activeConnections.get(); }

    // Property getters
    public StringProperty idProperty() { return id; }
    public DoubleProperty cpuUsageProperty() { return cpuUsage; }
    public DoubleProperty memoryUsageProperty() { return memoryUsage; }
    public DoubleProperty diskUsageProperty() { return diskUsage; }
    public DoubleProperty responseTimeProperty() { return responseTime; }
    public IntegerProperty activeConnectionsProperty() { return activeConnections; }

    // Setters
    public void setId(String id) { this.id.set(id); }
    public void setCpuUsage(double value) { this.cpuUsage.set(value); }
    public void setMemoryUsage(double value) { this.memoryUsage.set(value); }
    public void setDiskUsage(double value) { this.diskUsage.set(value); }
    public void setResponseTime(double value) { this.responseTime.set(value); }
    public void setActiveConnections(int value) { this.activeConnections.set(value); }
}
