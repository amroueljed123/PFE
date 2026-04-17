# ETAP (Equipment & Technical Asset Planning) System

## Project Overview
ETAP is a comprehensive enterprise asset management and supply chain analytics platform. It facilitates the end-to-end lifecycle of technical assets, from procurement and inventory tracking to advanced consumption analytics and anomaly detection.

### Core Architecture
- **Frontend:** Vue 3 with Vuetify 4 (beta) and Vite. Features a multi-role dashboard system (Admin, Gestionnaire, Responsable, Directeur, User).
- **Backend:** Spring Boot (Java 21) following a RESTful architectural pattern. Uses Spring Security with JWT for authentication and JPA for data persistence.
- **Database:** PostgreSQL for relational data storage.
- **Analytics Engine:** Specialized Python 3.9 modules for machine learning and statistical analysis, integrated into the Java backend via `ProcessBuilder`.
- **OCR Integration:** Tesseract (via tess4j) and Python-based document processing for invoice data extraction.

## Building and Running

### Prerequisites
- Java 21
- Node.js 18+ & npm
- PostgreSQL 14+
- Python 3.9+ with `pip install python-docx`
- Tesseract OCR installed on the system

### Database Setup
1. Start PostgreSQL.
2. Create the database: `createdb -U postgres etap_db`.
3. Configure `app-back/src/main/resources/application.properties` with your credentials.

### Backend (Spring Boot)
```powershell
cd app-back
mvn clean install
mvn spring-boot:run
```
- **Port:** 8080
- **API Base:** `/api`

### Frontend (Vue 3)
```powershell
cd app-front/consommables
npm install
npm run dev
```
- **Port:** 5173
- **Login Credentials (Default):** `admin` / `password`

### Python Modules
Ensure Python is in your PATH. The modules are located in the root directory and are called automatically by the backend.
- `ocr_module.py`
- `anomaly_detection_module.py`
- `forecasting_module.py`
- `stock_optimization_module.py`

## Development Conventions

### Backend
- **Repository Pattern:** Uses Spring Data JPA repositories.
- **Service Layer:** All business logic resides in the `service` package.
- **Python Integration:** Managed by `com.esb.appback.util.PythonIntegration`.
- **Security:** JWT-based. All protected routes require a `Bearer` token in the `Authorization` header.
- **Audit Logging:** Every major operation is logged via `AuditService`.

### Frontend
- **State Management:** Role-based access control managed via `vue-router` guards.
- **API Communication:** Axios-based services in `src/services`.
- **UI Components:** Vuetify 4 components.
- **Routing:** Nested routes for different dashboard roles (Admin, Gestionnaire, etc.).

### Analytics
- **Communication:** Java and Python communicate via JSON over standard I/O.
- **Methods:**
    - **Anomaly Detection:** Z-score and IQR methods.
    - **Forecasting:** Simple Moving Average, Exponential Smoothing, Holt's method.
    - **Optimization:** EOQ (Economic Order Quantity) and ABC analysis.

## Key Files
- `app-back/pom.xml`: Backend dependencies.
- `app-front/consommables/package.json`: Frontend scripts and dependencies.
- `app-back/src/main/resources/application.properties`: System configuration.
- `PythonIntegration.java`: Bridge between Java and Python.
- `COMPLETE_SETUP_GUIDE.md`: Exhaustive setup instructions.
- `API_REFERENCE.md`: Detailed API documentation.
