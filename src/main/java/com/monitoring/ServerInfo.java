package com.monitoring;

import java.io.Serializable;

public class ServerInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private double cpuUsage;
    private double memoryUsage;
    private double diskUsage;      // Pourcentage d'utilisation du disque
    private double responseTime;    // Temps de réponse en millisecondes
    private int activeConnections;  // Nombre de connexions actives

    public ServerInfo(String id, double cpuUsage, double memoryUsage, double diskUsage, 
                     double responseTime, int activeConnections) {
        this.id = id;
        this.cpuUsage = cpuUsage;
        this.memoryUsage = memoryUsage;
        this.diskUsage = diskUsage;
        this.responseTime = responseTime;
        this.activeConnections = activeConnections;
    }

    // Getters
    public String getId() { return id; }
    public double getCpuUsage() { return cpuUsage; }
    public double getMemoryUsage() { return memoryUsage; }
    public double getDiskUsage() { return diskUsage; }
    public double getResponseTime() { return responseTime; }
    public int getActiveConnections() { return activeConnections; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setCpuUsage(double cpuUsage) { this.cpuUsage = cpuUsage; }
    public void setMemoryUsage(double memoryUsage) { this.memoryUsage = memoryUsage; }
    public void setDiskUsage(double diskUsage) { this.diskUsage = diskUsage; }
    public void setResponseTime(double responseTime) { this.responseTime = responseTime; }
    public void setActiveConnections(int activeConnections) { this.activeConnections = activeConnections; }
}
