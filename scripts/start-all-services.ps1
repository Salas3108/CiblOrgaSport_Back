# CiblOrgaSport Services Startup Script for Windows
# Usage: powershell -ExecutionPolicy Bypass -File .\start-all-services.ps1

param()

$ErrorActionPreference = "Continue"
$Green = "`e[0;32m"
$Blue = "`e[0;34m"
$Red = "`e[0;31m"
$Yellow = "`e[1;33m"
$NC = "`e[0m"

Write-Host "`n$Blue======================================================$NC"
Write-Host "$Blue[START] CiblOrgaSport Services (Spring Boot)$NC"
Write-Host "$Blue======================================================$NC`n"

$ROOT_DIR = Split-Path -Parent $PSScriptRoot
$LOG_DIR = Join-Path $ROOT_DIR "logs"

if (-not (Test-Path $LOG_DIR)) {
    New-Item -ItemType Directory -Path $LOG_DIR -Force | Out-Null
}

Get-ChildItem -Path $LOG_DIR -Filter "*.pid" -ErrorAction SilentlyContinue | Remove-Item -Force

$services = @(
    @{ name = "auth-service"; port = "8081"; dir = "auth-service" }
    @{ name = "event-service"; port = "8082"; dir = "event-service" }
    @{ name = "billetterie"; port = "8083"; dir = "billetterie" }
    @{ name = "incident-service"; port = "8084"; dir = "incident-service" }
    @{ name = "abonnement-service"; port = "8085"; dir = "abonnement-service" }
    @{ name = "gateway"; port = "8080"; dir = "gateway" }
)

function Get-Port {
    param($dir, $defaultPort)
    $propFile = Join-Path $dir "src\main\resources\application.properties"
    if (Test-Path $propFile) {
        $port = (Select-String -Path $propFile -Pattern '^server\.port' | ForEach-Object { ($_.Line -split '=')[1].Trim() } | Select-Object -Last 1)
        if ($port) { return $port }
    }
    return $defaultPort
}

function Build-Service {
    param($name, $dir)
    Write-Host "$Blue[*] Building: $name$NC"
    Push-Location $dir
    & mvn clean package spring-boot:repackage -DskipTests -q 2>&1 | Out-Null
    $result = $LASTEXITCODE
    Pop-Location
    if ($result -eq 0) {
        Write-Host "$Green[OK] Built: $name$NC"
        return $true
    } else {
        Write-Host "$Red[FAIL] Build failed: $name$NC"
        return $false
    }
}

function Start-Service {
    param($name, $dir, $port)
    if (-not (Build-Service $name $dir)) { return $false }
    
    $jar = Get-ChildItem -Path "$dir\target" -Filter "*.jar" -ErrorAction SilentlyContinue | Where-Object { $_.Name -notmatch '\.original$' } | Select-Object -First 1
    if (-not $jar) {
        Write-Host "$Red[FAIL] JAR not found: $name$NC"
        return $false
    }
    
    $logFile = Join-Path $LOG_DIR "$name.log"
    Write-Host "$Blue[*] Starting: $name on port $port$NC"
    
    $process = Start-Process -FilePath "java" -ArgumentList "-jar", "`"$($jar.FullName)`"", "--server.port=$port" -WorkingDirectory $dir -RedirectStandardOutput $logFile -NoNewWindow -PassThru
    $process.Id | Out-File (Join-Path $LOG_DIR "$name.pid") -Encoding ASCII -Force
    
    Start-Sleep -Milliseconds 500
    if ($null -eq (Get-Process -Id $process.Id -ErrorAction SilentlyContinue)) {
        Write-Host "$Red[FAIL] Failed to start: $name$NC"
        return $false
    }
    
    Write-Host "$Green[OK] Started: $name (PID: $($process.Id), Port: $port)$NC"
    return $true
}

function Check-Service {
    param($name)
    $pidFile = Join-Path $LOG_DIR "$name.pid"
    if (-not (Test-Path $pidFile)) { return $false }
    $processId = Get-Content $pidFile -ErrorAction SilentlyContinue
    return ($null -ne (Get-Process -Id $processId -ErrorAction SilentlyContinue))
}

Write-Host "$Blue[*] Starting PostgreSQL...${NC}"
docker-compose up -d postgres 2>&1 | Out-Null
Start-Sleep -Seconds 5

Write-Host "`n$Blue[*] Starting microservices...${NC}`n"

$jobs = @()
foreach ($svc in $services | Where-Object { $_.name -ne "gateway" }) {
    $dir = Join-Path $ROOT_DIR $svc.dir
    $port = Get-Port $dir $svc.port
    
    $job = Start-Job -ArgumentList $svc.name, $dir, $port, $LOG_DIR -ScriptBlock {
        param($n, $d, $p, $l)
        Push-Location $d
        & mvn clean package spring-boot:repackage -DskipTests -q 2>&1 | Out-Null
        Pop-Location
        
        $jar = Get-ChildItem "$d\target" -Filter "*.jar" -ErrorAction SilentlyContinue | Where-Object { $_.Name -notmatch '\.original$' } | Select-Object -First 1
        if ($jar) {
            $lf = Join-Path $l "$n.log"
            $pf = Join-Path $l "$n.pid"
            $proc = Start-Process -FilePath "java" -ArgumentList "-jar", "`"$($jar.FullName)`"", "--server.port=$p" -WorkingDirectory $d -RedirectStandardOutput $lf -NoNewWindow -PassThru
            $proc.Id | Out-File $pf -Encoding ASCII -Force
            Start-Sleep -Milliseconds 500
            return ($null -ne (Get-Process -Id $proc.Id -ErrorAction SilentlyContinue))
        }
        return $false
    }
    $jobs += $job
}

$jobs | Wait-Job | Out-Null
Write-Host "$Green[OK] Microservices started$NC"

Write-Host "$Yellow[*] Waiting 10s...${NC}"
Start-Sleep -Seconds 10

$gatewayDir = Join-Path $ROOT_DIR "gateway"
$gatewayPort = Get-Port $gatewayDir "8080"
Write-Host "`n$Blue[*] Starting Gateway...${NC}"
Start-Service "gateway" $gatewayDir $gatewayPort | Out-Null

Start-Sleep -Seconds 3

Write-Host "`n$Blue[*] Service Status:$NC`n"
$allOk = $true
foreach ($svc in $services) {
    $dir = Join-Path $ROOT_DIR $svc.dir
    $port = Get-Port $dir $svc.port
    if (Check-Service $svc.name) {
        Write-Host "$Green  [OK] $($svc.name): Running (Port $port)$NC"
    } else {
        Write-Host "$Red  [FAIL] $($svc.name): Failed$NC"
        $allOk = $false
    }
}

Write-Host "`n$Blue======================================================$NC"
if ($allOk) {
    Write-Host "$Green[SUCCESS] All services started successfully!$NC"
    Write-Host "`n$Blue[URLs]:$NC"
    foreach ($svc in $services) {
        Write-Host "$Green  $($svc.name): http://localhost:$($svc.port)$NC"
    }
} else {
    Write-Host "$Yellow[WARNING] Some services failed$NC"
}

Write-Host "$Blue[*] Logs: $LOG_DIR$NC"
Write-Host '$Blue[STOP] powershell -ExecutionPolicy Bypass -File .\scripts\stop-all-services.ps1$NC'
Write-Host "$Blue======================================================$NC`n"
