package com.monitoring;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MonitoringServer extends Remote {
    void updatePerformance(ServerInfo info) throws RemoteException;
    void registerCallback(GUICallback callback) throws RemoteException;
}
