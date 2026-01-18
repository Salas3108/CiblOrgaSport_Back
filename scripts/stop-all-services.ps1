# CiblOrgaSport Services Stop Script for Windows (PowerShell)
# Usage: powershell -ExecutionPolicy Bypass -File .\stop-all-services.ps1

$ErrorActionPreference = "Continue"

# Colors
$Green = "`e[0;32m"
$Blue = "`e[0;34m"
$Red = "`e[0;31m"
$Yellow = "`e[1;33m"
$NC = "`e[0m"

Write-Host "`n$Red======================================================$NC"
Write-Host "$Red[STOP] Stopping all CiblOrgaSport services$NC"
Write-Host "$Red======================================================$NC`n"

# Set directories
$ROOT_DIR = Split-Path -Parent $PSScriptRoot
$LOG_DIR = Join-Path $ROOT_DIR "logs"

# Services to stop (reverse order)
$services = @(
    "gateway"
    "incident-service"
    "billetterie"
    "event-service"
    "abonnement-service"
    "auth-service"
)

function Stop-Service {
    param([string]$serviceName)
    
    $pidFile = Join-Path $LOG_DIR "$serviceName.pid"
    
    if (-not (Test-Path $pidFile)) {
        Write-Host "$Red[FAIL] PID file not found for: $serviceName$NC"
        return
    }
    
    try {
        $processId = Get-Content -Path $pidFile -ErrorAction SilentlyContinue
        
        Write-Host "$Blue[*] Stopping: $serviceName (PID: $processId)$NC"
        
        # Check if process exists
        $process = Get-Process -Id $processId -ErrorAction SilentlyContinue
        if ($process) {
            # Graceful stop
            Stop-Process -Id $processId -Force -ErrorAction SilentlyContinue
            Start-Sleep -Milliseconds 500
            
            # Verify stopped
            $process = Get-Process -Id $processId -ErrorAction SilentlyContinue
            if ($process) {
                Write-Host "$Yellow[WARN] Force killing: $serviceName$NC"
                Stop-Process -Id $processId -Force -ErrorAction SilentlyContinue
            }
            else {
                Write-Host "$Green[OK] Stopped: $serviceName$NC"
            }
        }
        else {
            Write-Host "$Red[FAIL] Process not running: $serviceName (PID: $processId)$NC"
        }
    }
    catch {
        Write-Host "$Red[FAIL] Error stopping $serviceName`: $($_.Exception.Message)$NC"
    }
    finally {
        # Remove PID file
        Remove-Item -Path $pidFile -Force -ErrorAction SilentlyContinue
    }
}

# Stop all services
foreach ($service in $services) {
    Stop-Service -serviceName $service
}

# Stop Docker services
Write-Host "`n$Blue[*] Stopping PostgreSQL database...${NC}"
try {
    docker-compose down 2>&1 | Out-Null
    Write-Host "$Green[OK] PostgreSQL and services stopped$NC"
}
catch {
    Write-Host "$Yellow[WARN] Docker compose not available$NC"
}

Write-Host "`n$Green[SUCCESS] All services stopped$NC"
Write-Host "$Blue======================================================$NC`n"
