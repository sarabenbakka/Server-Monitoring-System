package com.monitoring;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javafx.application.Platform;
import java.util.ArrayList;
import java.util.List;

public class MonitoringServerImpl extends UnicastRemoteObject implements MonitoringServer {
    private List<GUICallback> callbacks = new ArrayList<>();

    public MonitoringServerImpl() throws RemoteException {
        super();
    }

    @Override
    public void registerCallback(GUICallback callback) throws RemoteException {
        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
            System.out.println("Nouveau callback enregistré");
        }
    }

    @Override
    public void updatePerformance(ServerInfo info) throws RemoteException {
        System.out.println("Données reçues pour le serveur: " + info.getId());
        
        List<GUICallback> toRemove = new ArrayList<>();
        
        for (GUICallback callback : callbacks) {
            try {
                callback.updateGUI(info);
            } catch (Exception e) {
                System.err.println("Erreur lors de la mise à jour via callback: " + e.getMessage());
                toRemove.add(callback);
            }
        }
        
        // Supprimer les callbacks défectueux
        callbacks.removeAll(toRemove);
    }
}
