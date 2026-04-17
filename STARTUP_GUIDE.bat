@echo off
REM ETAP Asset Management System - Complete Startup Guide (Windows)
REM This guide covers running the entire stack

setlocal enabledelayedexpansion

echo ==================================
echo ETAP System - Complete Startup
echo ==================================
echo.

REM Colors simulation for Windows
set "RED=[91m"
set "GREEN=[92m"
set "YELLOW=[93m"
set "NC=[0m"

REM Step 1: Database Setup
echo [STEP 1] Database Setup
echo ==================================================
echo.
echo PostgreSQL Command:
echo   In PowerShell or CMD, run:
echo   createdb -U postgres etap_db
echo.
echo Then update: app-back\src\main\resources\application.properties
echo   spring.datasource.url=jdbc:postgresql://localhost:5432/etap_db
echo   spring.datasource.username=postgres
echo   spring.datasource.password=your_password
echo.
pause

REM Step 2: Python Setup
echo [STEP 2] Python ML Modules Setup
echo ==================================================
echo.
echo Run these commands in PowerShell:
echo   pip install python-docx
echo.
echo   cd c:\Users\amrou\OneDrive\Desktop\PFE\app
echo.
echo Verify Python modules exist:
echo   dir *.py
echo.
echo Expected files:
echo   - ocr_module.py
echo   - anomaly_detection_module.py
echo   - forecasting_module.py
echo   - stock_optimization_module.py
echo.
pause

REM Step 3: Backend Build & Start
echo [STEP 3] Backend Build ^& Start
echo ==================================================
echo.
echo Open a NEW PowerShell/CMD and run:
echo.
echo   cd c:\Users\amrou\OneDrive\Desktop\PFE\app\app-back
echo   mvn clean package -DskipTests
echo   mvn spring-boot:run
echo.
echo Backend should start at: http://localhost:8080
echo.
echo Check logs for:
echo   - "Started AppBackApplication"
echo   - "Hibernate: create table" messages
echo.
pause

REM Step 4: Frontend Build & Start
echo [STEP 4] Frontend Build ^& Start
echo ==================================================
echo.
echo Open ANOTHER NEW PowerShell/CMD and run:
echo.
echo   cd c:\Users\amrou\OneDrive\Desktop\PFE\app\app-front\consommables
echo   npm install
echo   npm run dev
echo.
echo Frontend should start at: http://localhost:5173
echo.
pause

REM Step 5: Access Application
echo [STEP 5] Access Application
echo ==================================================
echo.
echo Open your browser and go to:
echo   http://localhost:5173
echo.
echo Login Credentials:
echo   Username: admin
echo   Password: password
echo.
echo Then navigate to:
echo   http://localhost:5173/admin/dashboard/overview
echo.
pause

REM Testing
echo [STEP 6] Test Backend API
echo ==================================================
echo.
echo First, get a JWT token by logging in on http://localhost:5173
echo Copy the token from localStorage (open DevTools F12)
echo.
echo Then test the API in PowerShell:
echo.
echo 1. Get all suppliers:
echo   $headers = @{ 'Authorization' = 'Bearer YOUR_TOKEN' }
echo   Invoke-RestMethod -Uri http://localhost:8080/api/supply-chain/suppliers `
echo     -Headers $headers
echo.
echo 2. Create a supplier:
echo   $body = @{
echo     name = "Test Supplier"
echo     email = "test@example.com"
echo     phone = "+213 555 123456"
echo     address = "Algiers"
echo     rating = 4.5
echo   } ^| ConvertTo-Json
echo.
echo   Invoke-RestMethod -Uri http://localhost:8080/api/supply-chain/suppliers `
echo     -Method POST `
echo     -Headers $headers `
echo     -ContentType application/json `
echo     -Body $body
echo.
echo 3. Test Anomaly Detection:
echo   $body = @{
echo     consumableId = 1
echo     values = @(100, 102, 98, 105, 103)
echo     currentValue = 500
echo   } ^| ConvertTo-Json
echo.
echo   Invoke-RestMethod -Uri http://localhost:8080/api/analytics/anomalies/detect `
echo     -Method POST `
echo     -Headers $headers `
echo     -ContentType application/json `
echo     -Body $body
echo.
pause

echo ==================================
echo Startup complete!
echo ==================================
echo.
echo System Status:
echo   ^✓ Backend:  http://localhost:8080
echo   ^✓ Frontend: http://localhost:5173
echo   ^✓ Python:   Integrated
echo.
