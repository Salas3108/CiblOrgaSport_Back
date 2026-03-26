param(
    [int]$Vus = 10,
    [string]$RampUp = "30s",
    [string]$Steady = "90s",
    [string]$RampDown = "30s",
    [int]$DurationSeconds = 180,
    [string]$AuthBase = "http://localhost:8080",
    [string]$AuthUsername = "admin3",
    [string]$AuthPassword = "123456",
    [string]$EventBase = "http://localhost:8084",
    [string]$ParticipantsBase = "http://localhost:8087",
    [string]$ResultatsBase = "http://localhost:8088",
    [string]$AbonnementBase = "http://localhost:8082",
    [string]$BilletterieBase = "http://localhost:8081",
    [string]$IncidentBase = "http://localhost:8083",
    [string]$LieuBase = "http://localhost:8090",
    [string]$NotificationsBase = "http://localhost:8089",
    [string]$AnalyticsBase = "http://localhost:8091",
    [string]$GeolocationBase = "http://localhost:8092",
    [string]$VolunteerBase = "http://localhost:8093",
    [string]$PrometheusWriteUrl = "http://localhost:9090/api/v1/write"
)

$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $PSScriptRoot
$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$outDir = Join-Path $repoRoot "logs/k6-benchmark/$timestamp"
$null = New-Item -ItemType Directory -Force -Path $outDir

$k6Script = Join-Path $repoRoot "scripts/k6/main.js"
$statsFile = Join-Path $outDir "docker-stats.csv"
$k6Summary = Join-Path $outDir "k6-summary.json"
$k6Metrics = Join-Path $outDir "k6-metrics.json"
$k6Stdout = Join-Path $outDir "k6-output.txt"

if (-not (Get-Command k6 -ErrorAction SilentlyContinue)) {
    throw "k6 n'est pas installe. Installe-le depuis https://grafana.com/docs/k6/latest/set-up/install-k6/"
}

if (-not (Test-Path $k6Script)) {
    throw "Script k6 introuvable: $k6Script"
}

Write-Host "[1/3] Capture docker stats pendant $DurationSeconds sec..."
$statsJob = Start-Job -ScriptBlock {
    param($file, $seconds)
    docker stats --no-stream --format "table {{.Name}},{{.CPUPerc}},{{.MemUsage}},{{.NetIO}},{{.BlockIO}},{{.PIDs}}" | Out-File -FilePath $file -Encoding utf8
    $end = (Get-Date).AddSeconds($seconds)
    while ((Get-Date) -lt $end) {
        docker stats --no-stream --format "{{.Name}},{{.CPUPerc}},{{.MemUsage}},{{.NetIO}},{{.BlockIO}},{{.PIDs}}" | Add-Content -Path $file -Encoding utf8
        Start-Sleep -Seconds 2
    }
} -ArgumentList $statsFile, $DurationSeconds

Write-Host "[2/3] Lancement du scenario k6..."
$env:VUS = $Vus.ToString()
$env:RAMP_UP = $RampUp
$env:STEADY = $Steady
$env:RAMP_DOWN = $RampDown
$env:AUTH_BASE = $AuthBase
$env:AUTH_USERNAME = $AuthUsername
$env:AUTH_PASSWORD = $AuthPassword
$env:EVENT_BASE = $EventBase
$env:PARTICIPANTS_BASE = $ParticipantsBase
$env:RESULTATS_BASE = $ResultatsBase
$env:ABONNEMENT_BASE = $AbonnementBase
$env:BILLETTERIE_BASE = $BilletterieBase
$env:INCIDENT_BASE = $IncidentBase
$env:LIEU_BASE = $LieuBase
$env:NOTIFICATIONS_BASE = $NotificationsBase
$env:ANALYTICS_BASE = $AnalyticsBase
$env:GEOLOCATION_BASE = $GeolocationBase
$env:VOLUNTEER_BASE = $VolunteerBase
$env:K6_PROMETHEUS_RW_TREND_STATS = "p(90),p(95),p(99),min,max,avg,med"
$env:K6_PROMETHEUS_RW_STALE_MARKERS = "true"

$k6Args = @(
    "run",
    "--summary-export", $k6Summary,
    "--out", "experimental-prometheus-rw=$PrometheusWriteUrl",
    "--out", "json=$k6Metrics",
    $k6Script
)

k6 @k6Args | Tee-Object -FilePath $k6Stdout

Write-Host "[3/3] Finalisation de la capture docker stats..."
Receive-Job -Job $statsJob -Wait | Out-Null
Remove-Job -Job $statsJob

Write-Host "Benchmark k6 termine. Sorties:"
Write-Host "- $k6Summary"
Write-Host "- $k6Metrics"
Write-Host "- $k6Stdout"
Write-Host "- $statsFile"
