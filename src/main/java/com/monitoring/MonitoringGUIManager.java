package com.monitoring;

import javafx.application.Platform;

public class MonitoringGUIManager {
    private static MonitoringGUIManager instance;
    private MonitoringGUI gui;

    // Constructeur privé pour empêcher l'instanciation directe
    private MonitoringGUIManager() {}

    // Méthode pour obtenir l'instance unique
    public static MonitoringGUIManager getInstance() {
        if (instance == null) {
            instance = new MonitoringGUIManager();
        }
        return instance;
    }

    // Méthode pour démarrer l'interface graphique
    public void startGUI() {
        new Thread(() -> {
            MonitoringGUI.launch(MonitoringGUI.class);
        }).start();
    }

    // Méthode pour obtenir l'instance de MonitoringGUI
    public MonitoringGUI getGUI() {
        return gui;
    }

    // Méthode pour définir l'instance de MonitoringGUI
    public void setGUI(MonitoringGUI gui) {
        this.gui = gui;
    }
}
