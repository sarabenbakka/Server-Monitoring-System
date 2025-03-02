package com.monitoring;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MonitoringServerCallback extends Remote {
    void updateServerInfo(ServerInfo info) throws RemoteException;
}
