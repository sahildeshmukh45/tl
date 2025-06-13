# TeamLogger Troubleshooting Guide

## 🚨 Common Issues and Solutions

### Java-Related Issues

#### Issue: "java: command not found"
**Symptoms**: Cannot run Java applications
**Solution**:
```bash
# Check if Java is installed
java -version

# If not found, install Java 17+
# Windows: Download from adoptium.net
# Linux: sudo apt install openjdk-17-jdk
# macOS: brew install openjdk@17

# Set JAVA_HOME
# Windows
setx JAVA_HOME "C:\Program Files\Eclipse Adoptium\jdk-17.0.x-hotspot"

# Linux/macOS
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
```

#### Issue: "UnsupportedClassVersionError"
**Symptoms**: Application fails to start with version error
**Solution**:
- Ensure Java 17+ is installed
- Check JAVA_HOME points to correct version
- Restart terminal/command prompt

### Database Issues

#### Issue: "Connection refused" or "Cannot connect to database"
**Symptoms**: Backend fails to start, database connection errors
**Solutions**:

**For MySQL**:
```bash
# Check if MySQL is running
# Windows
net start mysql

# Linux
sudo systemctl status mysql
sudo systemctl start mysql

# macOS
brew services start mysql
```

**For H2**:
- H2 runs in-memory, no external setup needed
- Check application.properties for correct H2 configuration

#### Issue: "Access denied for user"
**Symptoms**: Database authentication errors
**Solution**:
```sql
-- Create user and grant privileges
CREATE USER 'teamlogger_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON teamlogger.* TO 'teamlogger_user'@'localhost';
FLUSH PRIVILEGES;
```

### Email Configuration Issues

#### Issue: "Authentication failed" for Gmail
**Symptoms**: Email sending fails with authentication error
**Solution**:
1. Enable 2-factor authentication in Google account
2. Generate App Password:
   - Google Account → Security → 2-Step Verification → App passwords
   - Generate password for "Mail"
3. Use App Password in application.properties

#### Issue: "Connection timeout" for SMTP
**Symptoms**: Email timeout errors
**Solution**:
```properties
# Check SMTP settings
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.auth=true
```

### Cloudinary Issues

#### Issue: "Invalid credentials" for Cloudinary
**Symptoms**: Screenshot upload fails
**Solution**:
1. Verify credentials in Cloudinary dashboard
2. Check application.properties:
   ```properties
   cloudinary.cloud-name=your-cloud-name
   cloudinary.api-key=your-api-key
   cloudinary.api-secret=your-api-secret
   ```

#### Issue: "Upload failed" for screenshots
**Symptoms**: Screenshots not uploading
**Solution**:
- Check internet connection
- Verify Cloudinary account is active
- Check file size limits

### Network and Port Issues

#### Issue: "Port 8080 already in use"
**Symptoms**: Backend fails to start
**Solution**:
```bash
# Find process using port
# Windows
netstat -ano | findstr :8080

# Linux/macOS
lsof -i :8080

# Kill process
# Windows
taskkill /PID <process_id> /F

# Linux/macOS
kill -9 <process_id>

# Or change port in application.properties
server.port=8081
```

#### Issue: "Connection refused" between frontend and backend
**Symptoms**: Frontend cannot connect to backend
**Solution**:
1. Ensure backend is running
2. Check API URL in frontend configuration
3. Verify CORS settings
4. Check firewall settings

### Application Startup Issues

#### Issue: "Bean creation failed"
**Symptoms**: Spring Boot fails to start
**Solution**:
1. Check application.properties syntax
2. Verify all required properties are set
3. Check database connection
4. Review startup logs

#### Issue: "ClassNotFoundException"
**Symptoms**: Missing dependencies
**Solution**:
1. Ensure all JAR files are present
2. Check classpath configuration
3. Verify Maven dependencies

### Frontend Issues

#### Issue: JavaFX application not starting
**Symptoms**: Frontend fails to launch
**Solution**:
1. Ensure JavaFX is included in JAR
2. Check Java version compatibility
3. Verify frontend configuration

#### Issue: "Cannot connect to backend"
**Symptoms**: Frontend shows connection errors
**Solution**:
1. Start backend first
2. Check API URL in frontend-config.properties
3. Verify backend is accessible at configured URL

### Performance Issues

#### Issue: "OutOfMemoryError"
**Symptoms**: Application crashes with memory error
**Solution**:
```bash
# Increase heap size
java -Xmx2g -jar teamlogger-backend.jar
java -Xmx1g -jar teamlogger-frontend.jar
```

#### Issue: Slow application response
**Symptoms**: Application is sluggish
**Solution**:
1. Check database performance
2. Monitor memory usage
3. Optimize queries
4. Enable caching

### Security Issues

#### Issue: "JWT token expired"
**Symptoms**: Users get logged out frequently
**Solution**:
```properties
# Increase JWT expiration time
jwt.expiration=86400000  # 24 hours
jwt.refresh-expiration=604800000  # 7 days
```

#### Issue: "Invalid JWT token"
**Symptoms**: Authentication failures
**Solution**:
1. Check JWT secret in configuration
2. Ensure consistent secret across restarts
3. Clear browser cache/cookies

## 🔍 Diagnostic Commands

### Check System Status
```bash
# Java version
java -version

# Memory usage
# Windows
wmic OS get FreePhysicalMemory,TotalVisibleMemorySize

# Linux
free -h

# Disk space
# Windows
dir

# Linux
df -h
```

### Check Application Status
```bash
# Check if application is running
# Windows
netstat -ano | findstr :8080

# Linux
ps aux | grep java
```

### Check Logs
```bash
# Backend logs
tail -f logs/teamlogger.log

# Frontend logs
tail -f logs/frontend.log
```

## 📊 Monitoring and Health Checks

### Backend Health Check
- URL: http://localhost:8080/actuator/health
- Should return: `{"status":"UP"}`

### Database Health Check
- H2 Console: http://localhost:8080/h2-console
- Test connection with configured credentials

### Email Test
- Check application logs for email sending attempts
- Verify SMTP settings

## 🛠️ Recovery Procedures

### Complete Reset
1. Stop all applications
2. Clear logs: `rm -rf logs/*`
3. Restart applications
4. Check health endpoints

### Database Reset
```sql
-- Drop and recreate database
DROP DATABASE teamlogger;
CREATE DATABASE teamlogger;
```

### Configuration Reset
1. Backup current configuration
2. Restore default configuration files
3. Reconfigure step by step

## 📞 Getting Help

### Information to Provide
When seeking help, include:
1. Operating system and version
2. Java version
3. Error messages and logs
4. Configuration files (remove sensitive data)
5. Steps to reproduce the issue

### Log Locations
- Backend: `logs/teamlogger.log`
- Frontend: `logs/frontend.log`
- System: Check OS-specific log locations

### Contact Information
- Create issue in repository
- Include all diagnostic information
- Provide error screenshots if applicable

---

**Remember**: Most issues can be resolved by checking logs and verifying configuration! 