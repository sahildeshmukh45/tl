# Team Logger Frontend

A modern JavaFX frontend for the Team Logger application with a clean, attractive interface.

## Features

- **Modern UI Design**: Clean, gradient-based design with smooth animations
- **User Authentication**: Secure login with JWT token management
- **Time Tracking**: Punch in/out functionality with real-time status updates
- **Dashboard**: Overview of work hours, projects, and tasks
- **Responsive Layout**: Adapts to different screen sizes
- **Role-based Access**: Different views for users, managers, and admins

## Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- Backend server running on `http://localhost:8080`

## Building and Running

### Option 1: Using Maven

```bash
# Navigate to frontend directory
cd frontend

# Clean and compile
mvn clean compile

# Run the application
mvn javafx:run
```

### Option 2: Using IDE

1. Import the project into your IDE (IntelliJ IDEA, Eclipse, etc.)
2. Make sure JavaFX is properly configured
3. Run the `TeamLoggerApp` class

### Option 3: Build JAR and Run

```bash
# Build the JAR file
mvn clean package

# Run the JAR
java -jar target/frontend-1.0.0.jar
```

## Project Structure

```
frontend/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/teamlogger/frontend/
│       │       ├── TeamLoggerApp.java          # Main application class
│       │       ├── controller/                 # FXML controllers
│       │       │   ├── LoginController.java
│       │       │   └── DashboardController.java
│       │       └── service/                    # Business logic services
│       │           ├── ApiService.java         # HTTP API communication
│       │           └── AuthService.java        # Authentication management
│       └── resources/
│           ├── css/
│           │   └── styles.css                  # Modern CSS styling
│           └── fxml/                           # FXML layout files
│               ├── LoginView.fxml              # Login screen
│               └── DashboardView.fxml          # Main dashboard
├── pom.xml                                     # Maven configuration
└── README.md                                   # This file
```

## UI Features

### Login Screen
- Clean, centered login form
- Gradient background
- Form validation with visual feedback
- Forgot password functionality
- Remember me option

### Dashboard
- Sidebar navigation with role-based menu items
- Real-time clock display
- Time tracking controls (Punch In/Out/Break)
- Statistics cards showing work hours, projects, tasks
- Recent activity feed
- Notes functionality for time entries

### Styling
- Modern gradient backgrounds
- Card-based layout with shadows
- Smooth hover animations
- Color-coded status indicators
- Responsive design elements

## Configuration

The application connects to the backend API at `http://localhost:8080/api`. To change this:

1. Edit `ApiService.java`
2. Update the `BASE_URL` constant
3. Rebuild the application

## Development

### Adding New Views

1. Create FXML file in `src/main/resources/fxml/`
2. Create corresponding controller in `src/main/java/com/teamlogger/frontend/controller/`
3. Add navigation in `DashboardController.java`
4. Update CSS styles if needed

### Styling

The application uses modern CSS with:
- CSS variables for consistent theming
- Flexbox-like layouts
- Smooth transitions and animations
- Responsive design patterns

## Troubleshooting

### Common Issues

1. **JavaFX not found**: Make sure JavaFX is included in your Java installation or add the JavaFX SDK to your project
2. **Backend connection failed**: Ensure the backend server is running on port 8080
3. **Compilation errors**: Check that you're using Java 21 or higher

### Logs

The application uses SLF4J for logging. Check the console output for detailed error messages and debugging information.

## Future Enhancements

- Screenshot capture functionality
- Advanced time tracking features
- Project and task management views
- Reporting and analytics
- User management for admins
- Settings and preferences
- Offline mode support
- Multi-language support

## License

This project is part of the Team Logger application suite. 