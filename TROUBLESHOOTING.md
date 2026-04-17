# Troubleshooting & FAQ Guide

## Quick Fixes

### Port Already in Use

**Problem:** "Address already in use: 8080"

**Solution:**
```powershell
# Find what's using port 8080
netstat -ano | findstr :8080

# Kill the process (replace PID with actual)
taskkill /PID 12345 /F

# Or use different port:
# Edit application.properties: server.port=8081
```

### Frontend Routes Not Loading (404)

**Problem:** Clicking sidebar links shows error or blank page

**Solution:**
```powershell
# 1. Verify router/index.js is correct
# Check that all routes are in the children array, not duplicate paths

# 2. Clear browser cache
# Dev Tools → Application → Clear site data

# 3. Restart frontend
# Ctrl+C to stop npm run dev
# npm run dev

# 4. Check console for errors
# Press F12 → Console tab → Look for red errors
```

### Backend Not Starting

**Problem:** `mvn spring-boot:run` fails or hangs

**Solution:**
```powershell
# 1. Check Java version
java -version  # Should be 21+

# 2. Clean and rebuild
cd app-back
mvn clean install

# 3. Check for errors in console
# Look for: "NoClassDefFoundError", "ClassNotFoundException"

# 4. Make sure PostgreSQL is running
psql -U postgres --version

# 5. Check database connection
# Edit application.properties if needed
# Restart backend
```

### Database Connection Failed

**Problem:** "Connection refused" or "FATAL: role "postgres" does not exist"

**Solution:**
```powershell
# 1. Start PostgreSQL
# Windows: Services → PostgreSQL → Start
# Or: pg_ctl -D "C:\Program Files\PostgreSQL\14\data" start

# 2. Verify connection
psql -U postgres -h localhost

# 3. Create database
createdb -U postgres supply_chain_db

# 4. Update application.properties
# spring.datasource.url=jdbc:postgresql://localhost:5432/supply_chain_db
# spring.datasource.username=postgres
# spring.datasource.password=your_password

# 5. Restart backend
```

### Python Module Not Found

**Problem:** "No module named 'ocr_module'" or similar

**Solution:**
```powershell
# 1. Verify Python is installed
python --version  # Should be 3.9+

# 2. Check Python modules exist
# Check folder: app-back\src\main\resources\python\

# 3. Install required dependencies
pip install python-docx numpy scipy scikit-learn

# 4. Verify modules work standalone
python app-back\src\main\resources\python\ocr_module.py

# 5. Check PythonIntegration.java PYTHON_ENV setting
# Should be "python" (or "python3" on Mac/Linux)
```

### Anomaly Detection Returns Invalid Results

**Problem:** Anomaly score is null or always "LOW"

**Solution:**
```powershell
# 1. Check Python subprocess execution
# Add logging to PythonIntegration.java:
# System.out.println("Executing: " + String.join(" ", command));

# 2. Test Python module directly
python -c "from anomaly_detection_module import *; detect([100,102,98,105,500])"

# 3. Verify JSON serialization
# Ensure anomaly_detection_module.py returns valid JSON

# 4. Check REST response
# POST to /api/analytics/anomalies/detect with sample data
# Check backend logs for Python output
```

### JWT Token Expired

**Problem:** "401 Unauthorized" on API calls

**Solution:**
```powershell
# 1. Get new token
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" `
  -Method Post `
  -ContentType "application/json" `
  -Body '{"username":"admin","password":"password"}'

$newToken = ($response.Content | ConvertFrom-Json).token

# 2. Use new token in Authorization header
$headers = @{"Authorization"="Bearer $newToken"}

# 3. Check token expiration in JwtTokenProvider
# Default: 24 hours
```

### Duplicate Role Error on Startup

**Problem:** "Duplicate entry for key 'role.UK_8360wq86txfrmtruylo2b61yp9'"

**Solution:**
```powershell
# 1. Clear database manually
psql -U postgres

\connect supply_chain_db
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
GRANT ALL ON SCHEMA public TO postgres;

# 2. Or drop and recreate database
dropdb -U postgres supply_chain_db
createdb -U postgres supply_chain_db

# 3. Restart backend (will auto-create tables)
mvn spring-boot:run
```

### Maven Plugin Errors

**Problem:** "Plugin execution not covered by lifecycle configuration"

**Solution:**
```powershell
# 1. Update pom.xml
# Add to build/plugins:
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-compiler-plugin</artifactId>
  <version>3.11.0</version>
  <configuration>
    <source>21</source>
    <target>21</target>
  </configuration>
</plugin>

# 2. Rebuild
mvn clean install

# 3. Or ignore warning in IDE
# Right-click error → mark as resolved
```

### Frontend Component Not Displaying

**Problem:** Vue component shows blank page

**Solution:**
```powershell
# 1. Check browser console (F12)
# Look for:
# - "Cannot import..."
# - "undefined component"

# 2. Verify component is exported
# Check: app-front\...\YourComponent.vue
# Should end with: export default { ... }

# 3. Verify route exists
# Check: app-front\...\router\index.js
# Should have: path: 'your-path', component: () => import('...')

# 4. Check service calls
# Verify API endpoint exists on backend
# Check Network tab to see actual API calls

# 5. Restart frontend
npm run dev
```

### CORS Errors

**Problem:** "Access to XMLHttpRequest... blocked by CORS policy"

**Solution:**
```powershell
# Backend CorsConfig should already be configured

# 1. Verify CorsConfig.java exists
# Location: app-back\src\main\java\com\esb\appback\config\CorsConfig.java

# 2. Check configuration:
# Should allow: http://localhost:5173, localhost:3000, etc.

# 3. If still getting CORS error:
# Check browser console for exact origin

# 4. Update CorsConfig.java
# Add origin: config.addAllowedOrigin("http://localhost:5173")

# 5. Rebuild backend
mvn clean compile
```

### High Memory Usage

**Problem:** "java.lang.OutOfMemoryError: Java heap space"

**Solution:**
```powershell
# Increase heap size
# Set environment variable:
set MAVEN_OPTS=-Xmx2048m -Xms1024m

# Or edit pom.xml:
<plugin>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-maven-plugin</artifactId>
  <configuration>
    <jvmArguments>-Xmx2048m -Xms1024m</jvmArguments>
  </configuration>
</plugin>

# Restart backend
mvn spring-boot:run
```

### Slow API Responses

**Problem:** API calls take 10+ seconds

**Solution:**
```powershell
# 1. Check database queries
# Add logging to Service classes
# Check Query execution time in logs

# 2. Enable query logging
# Add to application.properties:
spring.jpa.properties.hibernate.generate_statistics=true
logging.level.org.hibernate=DEBUG

# 3. Profile the request
# Backend logs should show query time

# 4. Optimize queries
# Add indexes on frequently queried columns
# See DatabaseInit.java for schema

# 5. Check Python module performance
# Time Python subprocess:
# measure-command { python script.py }
```

---

## Common API Errors

### 400 Bad Request

```json
{
  "error": "Invalid request body",
  "message": "Field 'name' is required"
}
```

**Solution:** Check JSON format and required fields

### 401 Unauthorized

```json
{
  "error": "Unauthorized",
  "message": "Invalid or expired token"
}
```

**Solution:** Login again to get new token, include in Authorization header

### 404 Not Found

```json
{
  "error": "Resource not found",
  "message": "Supplier with id 999 not found"
}
```

**Solution:** Check entity ID exists, verify API endpoint path

### 409 Conflict

```json
{
  "error": "Conflict",
  "message": "Order already in APPROVED status, cannot modify"
}
```

**Solution:** Check entity state before operation, respect workflow rules

### 500 Internal Server Error

```json
{
  "error": "Internal server error",
  "message": "An unexpected error occurred"
}
```

**Solution:** Check backend logs, verify database connection, restart backend

---

## Testing Checklist

### Before Going Live

- [ ] Backend compiles without errors: `mvn clean compile`
- [ ] Backend builds JAR: `mvn clean package`
- [ ] PostgreSQL running and accessible
- [ ] Frontend npm install succeeds: `npm install`
- [ ] Frontend dev server starts: `npm run dev`
- [ ] Can login with admin/password
- [ ] Dashboard loads without 404
- [ ] Sidebar navigation works
- [ ] At least one API endpoint working
- [ ] Python modules execute successfully
- [ ] Database has 25 tables created
- [ ] Audit logs recording actions

### Performance Verification

- [ ] Page loads in < 2 seconds
- [ ] API responses in < 500ms (without Python ML)
- [ ] Database queries use indexes
- [ ] No console errors in browser F12
- [ ] Browser Network tab shows no failed requests
- [ ] Backend logs show successful table creation

### Security Verification

- [ ] API requires JWT token for all operations
- [ ] Login endpoint works correctly
- [ ] Tokens expire after 24 hours
- [ ] CORS is properly configured
- [ ] Passwords not logged anywhere
- [ ] Database credentials in application.properties only

---

## FAQ

### Q: How do I reset everything?

A:
```powershell
# 1. Drop database
dropdb -U postgres supply_chain_db
createdb -U postgres supply_chain_db

# 2. Clean Maven
cd app-back
mvn clean

# 3. Clear Node cache
cd ../app-front/consommables
rm -r node_modules
npm install

# 4. Clear Python cache (if needed)
rm -r app-back/src/main/resources/python/__pycache__

# 5. Restart everything
```

### Q: How do I update a service?

A:
```powershell
# 1. Edit Service class
# File: app-back/src/main/java/com/esb/appback/service/YourService.java

# 2. Add your business logic

# 3. Recompile
mvn clean compile

# 4. Restart backend
# Ctrl+C to stop
# mvn spring-boot:run

# 5. Test via API
```

### Q: How do I add a new Vue component?

A:
```powershell
# 1. Create component file
# File: app-front/consommables/src/views/YourComponent.vue

# 2. Add route in router/index.js
# Add to children array:
{
  path: 'your-path',
  component: () => import('../views/YourComponent.vue')
}

# 3. Add sidebar menu item in AdminDashboard.vue
# Add to sidebarItems array

# 4. Restart frontend
# Ctrl+C to stop
# npm run dev
```

### Q: How do I test Python integration?

A:
```powershell
# 1. Test Python module directly
cd app-back/src/main/resources/python
python anomaly_detection_module.py

# 2. Test via Java
# Add test in AppBackApplicationTests.java
# Call PythonIntegration bean

# 3. Test via API
# POST to /api/analytics/anomalies/detect
# Check response and backend logs
```

### Q: How do I change database from PostgreSQL to MySQL?

A:
```powershell
# 1. Update pom.xml
# Remove postgresql dependency
# Add mysql-connector-java

# 2. Update application.properties
# spring.datasource.url=jdbc:mysql://localhost:3306/supply_chain_db
# spring.datasource.username=root
# spring.datasource.password=password
# spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect

# 3. Update jdbc driver
# Check pom.xml for correct version

# 4. Rebuild and restart
```

### Q: How do I deploy this to production?

A:
```powershell
# 1. Build JAR
cd app-back
mvn clean package -DskipTests

# 2. Build frontend
cd ../app-front/consommables
npm run build

# 3. Move to server
# Copy JAR: app-back/target/app-back-0.0.1-SNAPSHOT.jar
# Copy frontend: app-front/consommables/dist/

# 4. Configure production database
# Update application.properties with production DB

# 5. Run JAR
java -jar app-back-0.0.1-SNAPSHOT.jar

# 6. Serve frontend files (nginx, Apache, etc)
```

### Q: How do I monitor application logs?

A:
```powershell
# Backend logs (while running)
# Watch console during: mvn spring-boot:run
# Or tail log file:
# Get-Content -Path "logs/application.log" -Tail 50 -Wait

# Frontend logs
# Open: http://localhost:5173
# Open Developer Tools: F12 → Console

# Database logs
# Check PostgreSQL logs folder:
# C:\Program Files\PostgreSQL\14\data\log\

# Python subprocess logs
# Add to PythonIntegration.java:
# System.out.println("Python output: " + result);
```

### Q: Can I run everything on Docker?

A:
```powershell
# Yes, but requires Docker Compose setup
# Would need:
# - Dockerfile for backend
# - Dockerfile for frontend
# - docker-compose.yml for orchestration
# - PostgreSQL container

# This is advanced - use local setup first
```

### Q: How do I make the system faster?

A:
```powershell
# 1. Add database indexes
# Edit DatabaseInit.java, add @Index annotations

# 2. Enable caching
# Add spring-data-redis dependency
# Configure Redis in application.properties

# 3. Optimize Python modules
# Use numpy vectorization instead of loops
# Cache model predictions

# 4. Add pagination to API responses
# Implement PageRequest in services

# 5. Use HTTP caching headers
# Add browser cache headers to responses
```

### Q: How do I scale this to multiple servers?

A:
```powershell
# Advanced setup requiring:
# 1. Load balancer (nginx, HAProxy)
# 2. Shared database (already PostgreSQL)
# 3. Redis for session cache
# 4. File sharing for uploads
# 5. Queue system for long tasks (RabbitMQ, etc)

# This is production-grade - plan accordingly
```

---

**Still stuck? Check:**
1. Browser console (F12 → Console)
2. Backend logs (check for errors)
3. Database connection (psql test)
4. Network tab (check API calls)
5. Port conflicts (netstat)

**Need help?**
- Review COMPLETE_SETUP_GUIDE.md
- Check QUICK_START.md for commands
- Look at existing working code for patterns
- Test individual components in isolation
