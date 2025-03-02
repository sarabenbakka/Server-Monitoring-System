package com.monitoring;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GUICallback extends Remote {
    void updateGUI(ServerInfo info) throws RemoteException;
}
