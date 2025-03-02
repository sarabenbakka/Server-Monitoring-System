package com.monitoring;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MonitoringGUI extends Application {
    private TableView<ServerData> table = new TableView<>();
    private ObservableList<ServerData> data = FXCollections.observableArrayList();
    private LineChart<Number, Number> cpuChart;
    private PieChart diskChart;
    private BarChart<String, Number> memoryChart;
    private LineChart<Number, Number> responseChart;
    private PieChart connectionsChart;
    
    private XYChart.Series<Number, Number> cpuSeries = new XYChart.Series<>();
    private XYChart.Series<String, Number> memorySeries = new XYChart.Series<>();
    private XYChart.Series<Number, Number> responseSeries = new XYChart.Series<>();
    
    private Map<String, PieChart.Data> diskData = new HashMap<>();
    private Map<String, PieChart.Data> connectionsData = new HashMap<>();
    
    private Label alertLabel = new Label();
    private TextField filterField = new TextField();
    private TextField emailField = new TextField();
    private long startTime = System.currentTimeMillis();

    @Override
    public void start(Stage primaryStage) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            MonitoringServer server = (MonitoringServer) registry.lookup("MonitoringServer");
            server.registerCallback(new GUICallbackImpl(this));
            System.out.println("Interface enregistrée auprès du serveur de monitoring");

            BorderPane root = new BorderPane();
            
            // Panneau du haut avec les contrôles
            VBox topControls = new VBox(10);
            topControls.setPadding(new Insets(10));
            topControls.setStyle("-fx-background-color: #f0f0f0;");

            // Première ligne de contrôles
            HBox thresholdControls = new HBox(10);
            thresholdControls.setAlignment(Pos.CENTER_LEFT);
            
            Spinner<Integer> cpuThreshold = new Spinner<>(0, 100, 80, 5);
            Spinner<Integer> memoryThreshold = new Spinner<>(0, 100, 80, 5);
            cpuThreshold.setMaxWidth(80);
            memoryThreshold.setMaxWidth(80);
            
            Label cpuLabel = createStyledLabel("Seuil CPU:");
            Label memoryLabel = createStyledLabel("Seuil Mémoire:");
            
            thresholdControls.getChildren().addAll(cpuLabel, cpuThreshold, memoryLabel, memoryThreshold);

            // Deuxième ligne avec email et boutons
            HBox emailAndButtons = new HBox(10);
            emailAndButtons.setAlignment(Pos.CENTER_LEFT);
            
            Label emailLabel = createStyledLabel("Email pour alertes:");
            emailField.setPrefWidth(200);
            Button sendButton = new Button("Envoyer");
            sendButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            
            // Test d'envoi d'email lors du clic
            sendButton.setOnAction(e -> {
                String email = emailField.getText();
                if (email != null && !email.isEmpty()) {
                    EmailNotifier.getInstance().setEmailTo(email);
                    // Test d'envoi immédiat
                    try {
                        sendTestEmail(email);
                        showAlert("Succès", "Email de test envoyé avec succès!", Alert.AlertType.INFORMATION);
                    } catch (Exception ex) {
                        showAlert("Erreur", "Erreur d'envoi: " + ex.getMessage(), Alert.AlertType.ERROR);
                    }
                }
            });

            Button refreshButton = createStyledButton("Actualiser", "#2196F3");
            Button clearButton = createStyledButton("Effacer Alertes", "#FF9800");
            Button exportButton = createStyledButton("Export PDF", "#9C27B0");
            
            Label searchLabel = createStyledLabel("Filtrer:");
            filterField.setPrefWidth(150);
            
            emailAndButtons.getChildren().addAll(
                emailLabel, emailField, sendButton,
                searchLabel, filterField,
                refreshButton, clearButton, exportButton
            );

            // Ajout des gestionnaires d'événements pour les boutons
            refreshButton.setOnAction(e -> refreshData());
            clearButton.setOnAction(e -> clearData());
            exportButton.setOnAction(e -> exportToPDF());
            filterField.textProperty().addListener((observable, oldValue, newValue) -> filterData());

            topControls.getChildren().addAll(thresholdControls, emailAndButtons);
            
            // Configuration du tableau et des graphiques dans un SplitPane
            SplitPane centerPane = new SplitPane();
            
            // Panneau de gauche (tableau)
            VBox tablePane = new VBox(5);
            tablePane.setPadding(new Insets(5));
            table.setPrefHeight(600);
            table.setStyle("-fx-selection-bar: #2196F3; -fx-selection-bar-non-focused: #90CAF9;");
            
            TableColumn<ServerData, String> idCol = new TableColumn<>("Serveur");
            TableColumn<ServerData, Number> cpuCol = new TableColumn<>("CPU (%)");
            TableColumn<ServerData, Number> memoryCol = new TableColumn<>("Mémoire (%)");
            TableColumn<ServerData, Number> diskCol = new TableColumn<>("Disque (%)");
            TableColumn<ServerData, Number> responseCol = new TableColumn<>("Temps de réponse (ms)");
            TableColumn<ServerData, Number> connectionsCol = new TableColumn<>("Connexions");
            
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            cpuCol.setCellValueFactory(new PropertyValueFactory<>("cpuUsage"));
            memoryCol.setCellValueFactory(new PropertyValueFactory<>("memoryUsage"));
            diskCol.setCellValueFactory(new PropertyValueFactory<>("diskUsage"));
            responseCol.setCellValueFactory(new PropertyValueFactory<>("responseTime"));
            connectionsCol.setCellValueFactory(new PropertyValueFactory<>("activeConnections"));
            
            idCol.setPrefWidth(100);
            cpuCol.setPrefWidth(80);
            memoryCol.setPrefWidth(80);
            diskCol.setPrefWidth(80);
            responseCol.setPrefWidth(120);
            connectionsCol.setPrefWidth(80);
            
            cpuCol.setCellFactory(col -> new TableCell<ServerData, Number>() {
                @Override
                protected void updateItem(Number item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(String.format("%.1f%%", item.doubleValue()));
                        if (item.doubleValue() > cpuThreshold.getValue()) {
                            setStyle("-fx-background-color: #ffcdd2;");
                        } else {
                            setStyle("-fx-background-color: #c8e6c9;");
                        }
                    }
                }
            });
            
            memoryCol.setCellFactory(col -> new TableCell<ServerData, Number>() {
                @Override
                protected void updateItem(Number item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(String.format("%.1f%%", item.doubleValue()));
                        if (item.doubleValue() > memoryThreshold.getValue()) {
                            setStyle("-fx-background-color: #ffcdd2;");
                        } else {
                            setStyle("-fx-background-color: #c8e6c9;");
                        }
                    }
                }
            });
            
            table.getColumns().addAll(idCol, cpuCol, memoryCol, diskCol, responseCol, connectionsCol);
            table.setItems(data);
            tablePane.getChildren().add(table);
            
            // Panneau de droite (graphiques)
            VBox chartsBox = new VBox(10);
            chartsBox.setPadding(new Insets(5));
            chartsBox.setStyle("-fx-background-color: white;");
            
            cpuChart = createLineChart("Utilisation CPU", "Temps (s)", "CPU (%)", cpuSeries);
            cpuChart.setPrefHeight(200);

            memoryChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
            memoryChart.setTitle("Utilisation Mémoire par Serveur");
            memoryChart.setPrefHeight(250);
            memoryChart.getXAxis().setLabel("Serveurs");
            memoryChart.getYAxis().setLabel("Mémoire (%)");

            diskChart = new PieChart();
            diskChart.setTitle("Utilisation Disque");
            diskChart.setPrefHeight(250);
            diskChart.setLabelsVisible(true);

            responseChart = createLineChart("Temps de réponse", "Temps (s)", "ms", responseSeries);
            responseChart.setPrefHeight(200);

            connectionsChart = new PieChart();
            connectionsChart.setTitle("Répartition des Connexions");
            connectionsChart.setPrefHeight(250);
            connectionsChart.setLabelsVisible(true);

            GridPane chartsGrid = new GridPane();
            chartsGrid.setHgap(10);
            chartsGrid.setVgap(10);
            chartsGrid.add(cpuChart, 0, 0);
            chartsGrid.add(memoryChart, 1, 0);
            chartsGrid.add(diskChart, 0, 1);
            chartsGrid.add(responseChart, 1, 1);
            chartsGrid.add(connectionsChart, 0, 2, 2, 1);

            chartsBox.getChildren().add(chartsGrid);

            centerPane.getItems().addAll(tablePane, chartsBox);
            centerPane.setDividerPositions(0.4); // 40% pour le tableau, 60% pour les graphiques

            root.setTop(topControls);
            root.setCenter(centerPane);

            Scene scene = new Scene(root, 1200, 800);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LineChart<Number, Number> createLineChart(String title, String xLabel, String yLabel, XYChart.Series<Number, Number> series) {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(xLabel);
        yAxis.setLabel(yLabel);
        
        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(title);
        chart.getData().add(series);
        chart.setCreateSymbols(false);
        return chart;
    }

    private class GUICallbackImpl extends UnicastRemoteObject implements GUICallback {
        private final MonitoringGUI gui;

        public GUICallbackImpl(MonitoringGUI gui) throws RemoteException {
            super();
            this.gui = gui;
        }

        @Override
        public void updateGUI(ServerInfo info) throws RemoteException {
            Platform.runLater(() -> {
                // Mise à jour du tableau
                updateTableData(info);

                // Mise à jour des graphiques
                double timeInSeconds = (System.currentTimeMillis() - gui.startTime) / 1000.0;
                
                // Mise à jour CPU (LineChart)
                updateLineChart(cpuSeries, timeInSeconds, info.getCpuUsage());
                
                // Mise à jour Mémoire (BarChart)
                updateBarChart(info);
                
                // Mise à jour Disque (PieChart)
                updatePieChart(diskChart, diskData, info.getId(), info.getDiskUsage(), "Disque");
                
                // Mise à jour Temps de réponse (LineChart)
                updateLineChart(responseSeries, timeInSeconds, info.getResponseTime());
                
                // Mise à jour Connexions (PieChart)
                updatePieChart(connectionsChart, connectionsData, info.getId(), 
                             info.getActiveConnections(), "Connexions");
            });
        }
    }

    private void updateTableData(ServerInfo info) {
        boolean found = false;
        for (ServerData sd : data) {
            if (sd.getId().equals(info.getId())) {
                sd.setCpuUsage(info.getCpuUsage());
                sd.setMemoryUsage(info.getMemoryUsage());
                sd.setDiskUsage(info.getDiskUsage());
                sd.setResponseTime(info.getResponseTime());
                sd.setActiveConnections(info.getActiveConnections());
                found = true;
                break;
            }
        }
        if (!found) {
            data.add(new ServerData(info.getId(), info.getCpuUsage(), info.getMemoryUsage(),
                    info.getDiskUsage(), info.getResponseTime(), info.getActiveConnections()));
        }
        table.refresh();
    }

    private void updateLineChart(XYChart.Series<Number, Number> series, double time, double value) {
        series.getData().add(new XYChart.Data<>(time, value));
        if (series.getData().size() > 50) {
            series.getData().remove(0);
        }
    }

    private void updateBarChart(ServerInfo info) {
        boolean found = false;
        for (XYChart.Series<String, Number> series : memoryChart.getData()) {
            if (series.getName().equals(info.getId())) {
                series.getData().clear();
                series.getData().add(new XYChart.Data<>(info.getId(), info.getMemoryUsage()));
                found = true;
                break;
            }
        }
        if (!found) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(info.getId());
            series.getData().add(new XYChart.Data<>(info.getId(), info.getMemoryUsage()));
            memoryChart.getData().add(series);
        }
    }

    private void updatePieChart(PieChart chart, Map<String, PieChart.Data> dataMap, 
                              String id, double value, String metric) {
        if (!dataMap.containsKey(id)) {
            PieChart.Data slice = new PieChart.Data(id + " " + metric, value);
            dataMap.put(id, slice);
            chart.getData().add(slice);
        } else {
            dataMap.get(id).setPieValue(value);
        }
    }

    private void sendTestEmail(String to) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("benbakkasara@gmail.com", "Ffef606e2c@#");
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("benbakkasara@gmail.com"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject("Test de notification - Monitoring Serveur");
        message.setText("Ceci est un email de test pour vérifier la configuration des notifications.");

        Transport.send(message);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.show();
        });
    }

    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");
        return label;
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white;", color));
        return button;
    }

    private void refreshData() {
        table.refresh();
        cpuChart.layout();
        memoryChart.layout();
        diskChart.layout();
        responseChart.layout();
        connectionsChart.layout();
    }

    private void clearData() {
        data.clear();
        cpuSeries.getData().clear();
        memorySeries.getData().clear();
        diskData.clear();
        responseSeries.getData().clear();
        connectionsData.clear();
        
        // Effacer les données des graphiques
        diskChart.getData().clear();
        memoryChart.getData().clear();
        connectionsChart.getData().clear();
        
        // Rafraîchir l'interface
        refreshData();
    }

    private void filterData() {
        String filter = filterField.getText().toLowerCase();
        if (filter.isEmpty()) {
            table.setItems(data);
        } else {
            ObservableList<ServerData> filteredData = FXCollections.observableArrayList(
                data.filtered(server -> server.getId().toLowerCase().contains(filter))
            );
            table.setItems(filteredData);
        }
    }

    private void exportToPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le rapport PDF");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf")
        );
        fileChooser.setInitialFileName("rapport_monitoring.pdf");
        
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                PDFExporter.exportToPDF(data, ((File) file).getAbsolutePath());
                showAlert("Succès", "Le rapport a été exporté avec succès vers " + file.getName(), Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de l'export du PDF: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
