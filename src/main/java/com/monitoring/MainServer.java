package com.monitoring;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MainServer {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            System.out.println("Registre RMI créé sur le port 1099");

            MonitoringServerImpl server = new MonitoringServerImpl();
            
            registry.rebind("MonitoringServer", server);
            System.out.println("Serveur de monitoring enregistré");
            
            System.out.println("Serveur principal prêt à recevoir les connexions...");
        } catch (Exception e) {
            System.err.println("Erreur serveur principal: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
