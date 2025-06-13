# TeamLogger Installation Guide

## 📋 Prerequisites

Before installing TeamLogger, ensure you have the following:

### Required Software
- **Java 17 or higher** - [Download OpenJDK 17](https://adoptium.net/) or [Oracle JDK 17](https://www.oracle.com/java/technologies/downloads/)
- **MySQL 8.0+** (for production) or use H2 (included)
- **Email service** (Gmail, Outlook, etc.)
- **Cloudinary account** (for screenshot storage)

### System Requirements
- **RAM**: Minimum 4GB, Recommended 8GB+
- **Storage**: Minimum 2GB free space
- **OS**: Windows 10+, macOS 10.15+, or Linux
- **Network**: Internet connection for email and Cloudinary

## 🚀 Installation Steps

### Step 1: Install Java

#### Windows
1. Download OpenJDK 17 from [adoptium.net](https://adoptium.net/)
2. Run the installer and follow the setup wizard
3. Set JAVA_HOME environment variable:
   ```cmd
   setx JAVA_HOME "C:\Program Files\Eclipse Adoptium\jdk-17.0.x-hotspot"
   setx PATH "%PATH%;%JAVA_HOME%\bin"
   ```

#### macOS
```bash
# Using Homebrew
brew install openjdk@17

# Set JAVA_HOME
echo 'export JAVA_HOME=/opt/homebrew/opt/openjdk@17' >> ~/.zshrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.zshrc
source ~/.zshrc
```

#### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install openjdk-17-jdk

# Set JAVA_HOME
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
source ~/.bashrc
```

### Step 2: Verify Java Installation
```bash
java -version
javac -version
echo $JAVA_HOME  # Should show Java installation path
```

### Step 3: Database Setup

#### Option A: MySQL (Recommended for Production)
1. Install MySQL 8.0+
2. Create database and user:
   ```sql
   CREATE DATABASE teamlogger;
   CREATE USER 'teamlogger_user'@'localhost' IDENTIFIED BY 'your_password';
   GRANT ALL PRIVILEGES ON teamlogger.* TO 'teamlogger_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

#### Option B: H2 (Development/Testing)
- No setup required, H2 is included with the application

### Step 4: Email Configuration

#### Gmail Setup
1. Enable 2-factor authentication in your Google account
2. Generate an App Password:
   - Go to Google Account settings
   - Security → 2-Step Verification → App passwords
   - Generate password for "Mail"
3. Use the generated password in configuration

#### Outlook Setup
1. Use your email and password
2. Enable "Less secure app access" if needed

### Step 5: Cloudinary Setup
1. Create account at [cloudinary.com](https://cloudinary.com)
2. Get your credentials from Dashboard:
   - Cloud name
   - API Key
   - API Secret

### Step 6: Configure Application

1. **Copy configuration files:**
   ```bash
   cp application-prod.properties application.properties
   cp frontend-config.properties frontend.properties
   ```

2. **Edit `application.properties`:**
   - Update database credentials
   - Add email settings
   - Configure Cloudinary
   - Set JWT secret

3. **Edit `frontend-config.properties`:**
   - Update backend API URL
   - Configure UI settings

### Step 7: Run Applications

#### Start Backend
```bash
java -jar teamlogger-backend.jar
```

#### Start Frontend
```bash
java -jar teamlogger-frontend.jar
```

## 🔧 Configuration Details

### Backend Configuration (`application.properties`)
```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/teamlogger
spring.datasource.username=teamlogger_user
spring.datasource.password=your_password

# Email
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password

# Cloudinary
cloudinary.cloud-name=your-cloud-name
cloudinary.api-key=your-api-key
cloudinary.api-secret=your-api-secret

# JWT
jwt.secret=your-secret-key-here
```

### Frontend Configuration (`frontend-config.properties`)
```properties
# Backend API
api.base-url=http://localhost:8080/api

# Application
app.title=TeamLogger
app.version=1.0.0
```

## 🌐 Access Application

- **Backend API**: http://localhost:8080
- **Frontend**: http://localhost:8080 (if served by backend)
- **H2 Console**: http://localhost:8080/h2-console (development only)

## 👤 Default Users

- **Admin**: admin@teamlogger.com / admin123
- **User**: user@teamlogger.com / user123

**⚠️ Important**: Change default passwords immediately after first login!

## 🔒 Security Setup

1. **Change default passwords**
2. **Update JWT secret** with a strong random string
3. **Configure HTTPS** for production
4. **Set up firewall rules**
5. **Enable database encryption**

## 📊 Monitoring

- **Health Check**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/metrics
- **Logs**: Check `logs/teamlogger.log`

## 🚨 Troubleshooting

### Common Issues

1. **Java not found**
   - Verify JAVA_HOME is set correctly
   - Restart terminal/command prompt

2. **Database connection failed**
   - Check MySQL service is running
   - Verify credentials in application.properties

3. **Email not working**
   - Check SMTP settings
   - Verify app password for Gmail

4. **Port already in use**
   - Change port in application.properties
   - Kill process using the port

### Logs Location
- Backend: `logs/teamlogger.log`
- Frontend: `logs/frontend.log`

## 📞 Support

For additional help:
- Check `TROUBLESHOOTING.md`
- Review application logs
- Contact development team

---

**Installation Complete!** 🎉

Your TeamLogger application is now ready to use. 