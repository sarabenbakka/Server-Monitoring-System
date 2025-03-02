# Server Monitoring System

A Java-based server monitoring application that provides real-time monitoring and visualization of server performance metrics using JavaFX.

## Features

- Real-time server metrics monitoring
- Interactive GUI with multiple visualization types:
  - CPU usage line chart
  - Disk usage pie chart
  - Memory usage bar chart
  - Response time monitoring
  - Active connections tracking
- Email alert system for critical events
- Filterable server data table
- RMI (Remote Method Invocation) for server-client communication

## Technologies Used

- Java
- JavaFX for GUI
- RMI for remote communication
- Maven for dependency management
- JavaMail API for email notifications

## Prerequisites

- Java 8 or higher
- Maven
- IDE (IntelliJ IDEA recommended)

## Installation

1. Clone the repository:
```bash
git clone [your-repository-url]
```

2. Navigate to the project directory:
```bash
cd Monitoring-project
```

3. Build the project using Maven:
```bash
mvn clean install
```

## Usage

1. Start the Monitoring Server:
```bash
java -jar target/monitoring-server.jar
```

2. Launch the Monitoring GUI:
```bash
java -jar target/monitoring-gui.jar
```

3. Enter the server details in the GUI to begin monitoring

## Project Structure

- `src/main/java/com/monitoring/`
  - `MonitoringGUI.java` - Main GUI application
  - `MonitoringServer.java` - Server implementation
  - `MonitoringGUIManager.java` - GUI management logic
  - `ServerData.java` - Data model for server metrics
  - `GUICallback.java` - Callback interface for GUI updates
  - `MonitoringServerCallback.java` - Server callback interface

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details

## Contact

Your Name - [your-email@example.com]

Project Link: [https://github.com/yourusername/Monitoring-project]
