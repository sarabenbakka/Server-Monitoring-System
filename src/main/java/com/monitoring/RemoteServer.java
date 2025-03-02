package com.monitoring;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

public class RemoteServer {
    public static void main(String[] args) {
        try {
            // Se connecter au registre RMI existant
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            System.out.println("Connecté au registre RMI sur le port 1099");

            // Obtenir la référence du serveur de monitoring
            MonitoringServer monitoringServer = (MonitoringServer) registry.lookup("MonitoringServer");
            System.out.println("Référence du serveur de monitoring obtenue");

            // Liste des villes marocaines
            final String[] CITIES = {
                "Casablanca", "Rabat", "Fès", "Marrakech", "Agadir",
                "Tanger", "Oujda", "Kénitra", "Tétouan", "Meknès",
                "El Jadida", "Mohammedia", "Safi", "Essaouira", "Taza",
                "Nador", "Béni Mellal", "Khouribga", "Ouarzazate", "Larache"
            };

            // Simuler plusieurs serveurs distants
            for (int i = 0; i < CITIES.length; i++) {
                final String city = CITIES[i];
                new Thread(() -> {
                    Random random = new Random();
                    while (true) {
                        try {
                            // Générer des valeurs aléatoires
                            double cpuUsage = 40 + random.nextDouble() * 60;      // Entre 40% et 100%
                            double memoryUsage = 30 + random.nextDouble() * 70;   // Entre 30% et 100%
                            double diskUsage = 20 + random.nextDouble() * 60;     // Entre 20% et 80%
                            double responseTime = 50 + random.nextDouble() * 450;  // Entre 50ms et 500ms
                            int activeConnections = random.nextInt(1000);         // Entre 0 et 1000 connexions

                            // Créer et envoyer les informations du serveur
                            ServerInfo info = new ServerInfo(city, cpuUsage, memoryUsage, 
                                                          diskUsage, responseTime, activeConnections);
                            monitoringServer.updatePerformance(info);
                            System.out.println("Données envoyées pour le serveur: " + city + 
                                             " (CPU: " + String.format("%.1f", cpuUsage) + "%, " +
                                             "Mémoire: " + String.format("%.1f", memoryUsage) + "%, " +
                                             "Disque: " + String.format("%.1f", diskUsage) + "%, " +
                                             "Temps de réponse: " + String.format("%.0f", responseTime) + "ms, " +
                                             "Connexions: " + activeConnections + ")");

                            Thread.sleep(5000 + random.nextInt(5000));
                        } catch (Exception e) {
                            System.err.println("Erreur pour le serveur " + city + ": " + e.getMessage());
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }
                    }
                }).start();
                
                Thread.sleep(100);
            }

            System.out.println("Simulation des serveurs démarrée pour " + CITIES.length + " villes");
        } catch (Exception e) {
            System.err.println("Erreur serveur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
