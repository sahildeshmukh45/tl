# TeamLogger - Team Time Tracking & Management Application

A comprehensive team time tracking and management application built with Java Spring Boot backend and JavaFX frontend.

## 🚀 Features

### User Authentication & Management
- User login/logout with JWT authentication
- Role-based access control (Admin/User)
- Password recovery via email
- User profile management

### Dashboard & Analytics
- Real-time dashboard with productivity statistics
- Time tracking overview
- Project progress visualization
- Team performance metrics

### Time Tracking
- Start/pause/stop timers
- Manual time entry
- Idle detection and break tracking
- Overtime calculation
- Screenshot capture with Cloudinary integration

### Project & Task Management
- Project creation and management
- Task assignment and tracking
- Deadline management
- Priority and status tracking
- Progress monitoring

### Reports & Analytics
- Timesheet generation
- Productivity reports
- Export to Excel, PDF, CSV
- Email report delivery
- Custom date range filtering

### Admin Controls
- User management
- Permission management
- System monitoring
- Activity logs

### Settings & Customization
- Work hours configuration
- Project categories
- Notification settings
- Language and timezone support

## 🛠️ Technology Stack

### Backend
- **Framework**: Spring Boot 3.x
- **Database**: H2 (development), MySQL/PostgreSQL (production)
- **Security**: Spring Security with JWT
- **Email**: JavaMail API
- **File Storage**: Cloudinary
- **Scheduling**: Quartz Scheduler
- **Build Tool**: Maven

### Frontend
- **Framework**: JavaFX 17+
- **HTTP Client**: Java 11+ HTTP Client
- **JSON**: Jackson
- **Charts**: JavaFX Charts
- **Icons**: FontAwesome (via CSS)

## 📁 Project Structure

```
teamlogger/
├── src/                          # Backend Spring Boot application
│   ├── main/java/com/teamlogger/
│   │   ├── config/               # Configuration classes
│   │   ├── controller/           # REST controllers
│   │   ├── dto/                  # Data Transfer Objects
│   │   ├── entity/               # JPA entities
│   │   ├── repository/           # Data access layer
│   │   ├── security/             # Security configuration
│   │   └── service/              # Business logic
│   └── resources/
│       ├── application.properties
│       └── data.sql
├── frontend/                     # JavaFX frontend application
│   ├── src/main/java/com/teamlogger/frontend/
│   │   ├── controller/           # JavaFX controllers
│   │   ├── model/                # Data models
│   │   ├── service/              # API services
│   │   └── view/                 # FXML views
│   └── src/main/resources/
│       ├── fxml/                 # FXML files
│       ├── css/                  # Stylesheets
│       └── images/               # Images and icons
└── README.md
```

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Git

### Backend Setup
1. Clone the repository
2. Navigate to the backend directory
3. Configure `application.properties` with your settings
4. Run: `mvn spring-boot:run`

### Frontend Setup
1. Navigate to the frontend directory
2. Run: `mvn clean javafx:run`

### Database Setup
The application uses H2 in-memory database by default. For production:
1. Update `application.properties` with your database credentials
2. Run the application to auto-create tables

## ⚙️ Configuration

### Required Environment Variables
- `JWT_SECRET`: Secret key for JWT token generation
- `CLOUDINARY_URL`: Cloudinary configuration URL
- `SMTP_HOST`: SMTP server for email notifications
- `SMTP_PORT`: SMTP port
- `SMTP_USERNAME`: Email username
- `SMTP_PASSWORD`: Email password

### Application Properties
Key configuration options in `application.properties`:
- Database connection
- JWT settings
- Email configuration
- Cloudinary settings
- CORS configuration
- Logging levels

## 🔐 Security

- JWT-based authentication
- Role-based authorization
- Password encryption
- CORS configuration
- Input validation

## 📊 API Documentation

### Authentication Endpoints
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout
- `POST /api/auth/refresh` - Refresh JWT token

### Time Tracking Endpoints
- `POST /api/time/start` - Start timer
- `POST /api/time/pause` - Pause timer
- `POST /api/time/stop` - Stop timer
- `GET /api/time/entries` - Get time entries
- `POST /api/time/manual` - Manual time entry

### Dashboard Endpoints
- `GET /api/dashboard/stats` - Dashboard statistics
- `GET /api/dashboard/recent` - Recent activities

### Project Management Endpoints
- `GET /api/projects` - List projects
- `POST /api/projects` - Create project
- `PUT /api/projects/{id}` - Update project
- `DELETE /api/projects/{id}` - Delete project

### Task Management Endpoints
- `GET /api/tasks` - List tasks
- `POST /api/tasks` - Create task
- `PUT /api/tasks/{id}` - Update task
- `DELETE /api/tasks/{id}` - Delete task

### Reports Endpoints
- `GET /api/reports/timesheet` - Generate timesheet
- `GET /api/reports/productivity` - Productivity report
- `POST /api/reports/export` - Export reports

## 🧪 Testing

### Backend Testing
```bash
mvn test
```

### Frontend Testing
```bash
cd frontend
mvn test
```

## 📦 Deployment

### Backend Deployment
1. Build: `mvn clean package`
2. Run: `java -jar target/teamlogger-0.0.1-SNAPSHOT.jar`

### Frontend Deployment
1. Build: `mvn clean package`
2. Run: `java -jar target/frontend-1.0.0.jar`

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Contact the development team

## 🔄 Version History

- **v1.0.0** - Initial release with core features
- User authentication and authorization
- Time tracking functionality
- Project and task management
- Dashboard and reporting
- Screenshot capture
- Email notifications

---

**TeamLogger** - Empowering teams with efficient time tracking and project management. 