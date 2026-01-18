# CiblOrgaSport Services - Windows Execution Guide

## Quick Start (Windows)

### Prerequisites
Before running the scripts, ensure you have installed:

1. **Java 17+**
   ```powershell
   java -version
   ```
   Should show Java 17 or higher

2. **Maven 3.8+**
   ```powershell
   mvn -version
   ```
   Should show Maven 3.8 or higher

3. **Docker & Docker Compose**
   ```powershell
   docker --version
   docker-compose --version
   ```

4. **Git Bash or WSL (Optional, for .sh scripts)**
   - If you want to run the bash scripts on Windows

---

## How to Start All Services on Windows

### Option 1: Using PowerShell (Recommended for Windows) ⭐

#### Step 1: Open PowerShell as Administrator
- Right-click PowerShell → Select "Run as Administrator"
- Navigate to your project directory:
  ```powershell
  cd d:\MIAGE\Glop\CiblOrgaSport_Back
  ```

#### Step 2: Run the Startup Script
```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\start-all-services.ps1
```

**What this does:**
- Starts PostgreSQL database (Docker)
- Builds all 6 services (auth, event, billetterie, incident, abonnement, gateway)
- Starts each service on its configured port
- Displays real-time status with color-coded output
- Shows all service URLs when complete

#### Expected Output:
```
═══════════════════════════════════════════════════════════
🚀 Démarrage des services CiblOrgaSport (Spring Boot)
═══════════════════════════════════════════════════════════

🗄️  Starting PostgreSQL database...
✅ PostgreSQL started

📦 Building: auth-service...
✅ Build successful: auth-service
🚀 Starting: auth-service on port 8081
✅ auth-service started (PID: 12345, Port: 8081) | Log: logs\auth-service.log

[... more services ...]

📊 Service Status:

  ✅ auth-service: Running (Port 8081)
  ✅ event-service: Running (Port 8082)
  ✅ billetterie: Running (Port 8083)
  ✅ incident-service: Running (Port 8084)
  ✅ abonnement-service: Running (Port 8085)
  ✅ gateway: Running (Port 8080)

🎉 All services started successfully!

📋 Service URLs:
  🔐 auth-service: http://localhost:8081
  🎪 event-service: http://localhost:8082
  🎫 billetterie: http://localhost:8083
  🚨 incident-service: http://localhost:8084
  🎓 abonnement-service: http://localhost:8085
  🌐 gateway: http://localhost:8080

📋 Logs directory: d:\MIAGE\Glop\CiblOrgaSport_Back\logs
🛑 To stop: powershell -ExecutionPolicy Bypass -File .\scripts\stop-all-services.ps1
═══════════════════════════════════════════════════════════
```

---

### Option 2: Using Git Bash or WSL

If you have Git Bash installed:

```bash
cd d:\MIAGE\Glop\CiblOrgaSport_Back
bash ./scripts/start-all-services.sh
```

---

## How to Stop All Services

### Using PowerShell:
```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\stop-all-services.ps1
```

### Using Git Bash:
```bash
bash ./scripts/stop-all-services.sh
```

---

## Service Ports

| Service | Port | URL | Purpose |
|---------|------|-----|---------|
| Gateway | 8080 | http://localhost:8080 | API Gateway (entry point) |
| Auth Service | 8081 | http://localhost:8081 | Authentication & User Management |
| Event Service | 8082 | http://localhost:8082 | Events & Competitions |
| Billetterie | 8083 | http://localhost:8083 | Ticket Management |
| Incident Service | 8084 | http://localhost:8084 | Incident Reporting |
| Abonnement Service | 8085 | http://localhost:8085 | User Subscriptions |
| PostgreSQL | 5432 | localhost:5432 | Database |
| PgAdmin | 8082 | http://localhost:8082 | Database UI (same as Event Service port warning) |

---

## Troubleshooting

### ❌ "Service failed to start"

**Check the logs:**
```powershell
# View specific service logs
type logs\auth-service.log

# Or use tail-like command
Get-Content logs\auth-service.log -Tail 50
```

**Common causes:**
- Port already in use: Check what's running on the port
  ```powershell
  netstat -ano | findstr :8081
  ```
- Build failure: Check Maven is installed correctly
  ```powershell
  mvn --version
  ```
- Insufficient permissions: Run PowerShell as Administrator

---

### ❌ "PostgreSQL connection refused"

**Check Docker:**
```powershell
docker ps
docker logs postgres
```

**Restart PostgreSQL:**
```powershell
docker-compose down
docker-compose up -d postgres
Start-Sleep -Seconds 5
```

---

### ❌ "Port 8080/8081/etc already in use"

**Find what's using the port:**
```powershell
netstat -ano | findstr :8080
# Returns something like: TCP  0.0.0.0:8080  0.0.0.0:0  LISTENING  12345

# Kill the process
taskkill /PID 12345 /F
```

---

### ❌ "Java not found"

**Install Java 17:**
1. Download from: https://www.oracle.com/java/technologies/downloads/#java17
2. Run the installer
3. Restart PowerShell and verify:
   ```powershell
   java -version
   ```

---

### ❌ "Maven not found"

**Install Maven:**
1. Download from: https://maven.apache.org/download.cgi
2. Extract to a folder (e.g., `C:\Maven`)
3. Add to PATH:
   ```powershell
   [Environment]::SetEnvironmentVariable("PATH", $env:PATH + ";C:\Maven\bin", "User")
   ```
4. Restart PowerShell and verify:
   ```powershell
   mvn --version
   ```

---

## Viewing Logs

### View all logs directory:
```powershell
Get-ChildItem logs\
```

### Watch a service log in real-time:
```powershell
Get-Content logs\auth-service.log -Wait
```

### Search for errors in logs:
```powershell
Select-String "ERROR" logs\*.log
```

---

## Testing the Services

### Using Postman:
1. Import the collection:
   - Open Postman
   - File → Import → Select `postman\CiblOrgaSport.postman_collection.json`

2. Test an endpoint:
   - Select a request (e.g., "Login")
   - Click "Send"

### Using curl (PowerShell):
```powershell
# Test gateway health
curl http://localhost:8080/actuator/health

# Test auth service
curl http://localhost:8081/api/auth/login -Method POST -ContentType "application/json" `
  -Body '{"username":"test","password":"test"}'
```

---

## Tips & Best Practices

### 💡 Keep logs visible while testing
Open a second PowerShell window to watch logs:
```powershell
Get-Content logs\billetterie.log -Wait
```

### 💡 Quick restart a single service
```powershell
# Stop
taskkill /F /IM java.exe  # Kills all Java processes

# Or stop specific service
$pid = Get-Content logs\auth-service.pid
Stop-Process -Id $pid -Force

# Then rebuild and start manually:
cd auth-service
mvn clean package -DskipTests
java -jar target/auth-service-1.0-SNAPSHOT.jar --server.port=8081
```

### 💡 Verify all services are listening:
```powershell
$ports = 8080,8081,8082,8083,8084,8085
foreach ($port in $ports) {
  $check = Test-NetConnection localhost -Port $port -WarningAction SilentlyContinue
  Write-Host "Port $port : $($check.TcpTestSucceeded)"
}
```

---

## Database Access

### Using PgAdmin UI:
1. Open: http://localhost:8082
2. Login: 
   - Email: `admin@admin.com`
   - Password: `password`
3. Explore the database

### Using Command Line:
```bash
# Connect to PostgreSQL
psql -h localhost -U admin -d glop -W
# When prompted, enter password: password

# List tables
\dt

# Exit
\q
```

---

## Need Help?

1. **Check logs:** `type logs\[service-name].log`
2. **Restart services:** Run stop script, wait 5 seconds, then start script
3. **Clean rebuild:** Delete all JAR files and rebuild:
   ```powershell
   Get-ChildItem -Path "**/target/*.jar" -Recurse | Remove-Item -Force
   powershell -ExecutionPolicy Bypass -File .\scripts\start-all-services.ps1
   ```

---

**Last Updated:** January 2026  
**OS:** Windows 10/11  
**Java:** 17+  
**Maven:** 3.8+
