# ⚡ Quick Reference Card

## 🚀 Start Here (30 seconds)

**Terminal 1 - Backend:**
```powershell
cd app-back
mvn spring-boot:run
# Runs on: http://localhost:8080
```

**Terminal 2 - Frontend:**
```powershell
cd app-front/consommables
npm run dev
# Opens: http://localhost:5173
```

**Terminal 3 - Database:**
```powershell
# Make sure PostgreSQL is running
psql -U postgres
```

Then login: `admin` / `password`

---

## 📍 URLs

| Service | URL | Purpose |
|---------|-----|---------|
| Frontend | http://localhost:5173 | Vue app (login here) |
| Backend API | http://localhost:8080/api | REST endpoints |
| Database | localhost:5432 | PostgreSQL |
| Admin Dashboard | http://localhost:5173/admin | Main interface |

---

## 🔧 Common Commands

### Backend
```powershell
mvn clean compile      # Verify code compiles
mvn clean package      # Build JAR
mvn spring-boot:run    # Start server
```

### Frontend
```powershell
npm install            # Install dependencies
npm run dev           # Start dev server
npm run build         # Build for production
```

### Database
```powershell
psql -U postgres                    # Connect
\connect supply_chain_db            # Select DB
\dt                                 # List tables
SELECT * FROM article;              # Query
```

### Python
```powershell
python --version                        # Check version
python app-back/src/main/resources/python/ocr_module.py  # Test
```

---

## 📚 Documentation Files

| File | Use For |
|------|---------|
| `README.md` | Overview |
| `QUICK_START.md` | 5-min setup ⭐ |
| `COMPLETE_SETUP_GUIDE.md` | Detailed setup |
| `SERVICES_GUIDE.md` | Backend services |
| `API_REFERENCE.md` | API testing |
| `TROUBLESHOOTING.md` | Fix issues |
| `DOCUMENTATION_INDEX.md` | Navigation map |
| `SESSION_SUMMARY.md` | This session |

---

## 🔓 Authentication

```powershell
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" `
  -Method Post `
  -ContentType "application/json" `
  -Body '{"username":"admin","password":"password"}'

$token = ($response.Content | ConvertFrom-Json).token

# Use in header:
$headers = @{"Authorization"="Bearer $token"}
```

---

## 🌐 Sample API Calls

### Create Supplier
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/supply-chain/suppliers" `
  -Method Post `
  -Headers $headers `
  -ContentType "application/json" `
  -Body (@{name="Company Inc"; rating=4.5} | ConvertTo-Json)
```

### Get All Suppliers
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/supply-chain/suppliers" `
  -Method Get `
  -Headers $headers
```

### Detect Anomalies
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/analytics/anomalies/detect" `
  -Method Post `
  -Headers $headers `
  -ContentType "application/json" `
  -Body (@{entityType="CONSUMABLE"; entityId=1; values=@(100,102,98,500)} | ConvertTo-Json)
```

---

## ⚠️ Common Issues

| Issue | Fix |
|-------|-----|
| Port 8080 in use | `netstat -ano \| findstr :8080` then `taskkill /PID xxx /F` |
| 404 Routes | Clear browser cache (Ctrl+Shift+Del) and refresh |
| DB connection failed | Start PostgreSQL service |
| Python module not found | Check path: `app-back/src/main/resources/python/` |
| 401 Unauthorized | Get new JWT token first |
| Build error | Check errors.log or TROUBLESHOOTING.md |

---

## 📊 System Stats

- **Backend:** Spring Boot 4.0.2 (Java 21+)
- **Frontend:** Vue 3 with Vite
- **Database:** PostgreSQL (25 tables)
- **API:** 75+ endpoints
- **ML:** 4 Python modules
- **Auth:** JWT tokens
- **Build:** ✅ SUCCESS

---

## 🎯 Quick Checklist

- [ ] All 3 terminals running (Backend, Frontend, Database)
- [ ] Can access http://localhost:5173
- [ ] Can login with admin/password
- [ ] Dashboard loads
- [ ] Check browser console (F12) - no red errors
- [ ] Test one API endpoint works

---

## 💡 Pro Tips

1. **Pin this file** - Save as bookmark
2. **Keep terminals open** - Don't close while testing
3. **Check logs first** - Before Google searching
4. **Copy/paste examples** - From API_REFERENCE.md
5. **Test one thing** - At a time, systematically

---

## 🆘 Emergency Reset

```powershell
# Kill all ports
taskkill /F /IM node.exe
taskkill /F /IM java.exe

# Reset database
dropdb -U postgres supply_chain_db
createdb -U postgres supply_chain_db

# Clear Node cache
cd app-front/consommables
rm -r node_modules
npm install

# Restart everything
# Terminal 1: mvn spring-boot:run (in app-back)
# Terminal 2: npm run dev (in app-front/consommables)
```

---

## ✉️ File Locations

```
c:\Users\amrou\OneDrive\Desktop\PFE\app\
├── app-back\              ← Backend (Java)
├── app-front\consommables ← Frontend (Vue)
├── QUICK_START.md         ← Use this first!
└── [all docs here]        ← All documentation
```

---

## 🎓 Learning Path

1. **5 min:** Read README.md
2. **5 min:** Run QUICK_START.md
3. **5 min:** Check if it works
4. **10 min:** Read DOCUMENTATION_INDEX.md
5. **15 min:** Pick a guide based on your role
6. **Start coding!**

---

## 📞 Help Resources (In Order)

1. **This card** - Quick reference
2. **TROUBLESHOOTING.md** - Errors & fixes
3. **API_REFERENCE.md** - Code examples
4. **SERVICES_GUIDE.md** - Understanding services
5. **COMPLETE_SETUP_GUIDE.md** - Detailed help
6. **DOCUMENTATION_INDEX.md** - Navigation

---

**Print this page and keep it handy!** 🖨️

Questions? Each documentation file has a purpose - use DOCUMENTATION_INDEX.md to find what you need!
