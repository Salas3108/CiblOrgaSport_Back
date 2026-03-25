param(
    [int]$Iterations = 100,
    [int]$DelayMs = 200,
    [int]$DurationSeconds = 180
)

$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $PSScriptRoot
$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$outDir = Join-Path $repoRoot "logs/eco-benchmark/$timestamp"
$null = New-Item -ItemType Directory -Force -Path $outDir

$collection = Get-ChildItem -Path (Join-Path $repoRoot "postman/collections") -Filter "CiblOrgaSport*.postman_collection.json" |
    Select-Object -First 1 -ExpandProperty FullName
$statsFile = Join-Path $outDir "docker-stats.csv"
$newmanReport = Join-Path $outDir "newman-report.json"
$newmanStdout = Join-Path $outDir "newman-output.txt"

if (-not (Get-Command newman -ErrorAction SilentlyContinue)) {
    throw "Newman n'est pas installe. Installe-le avec: npm install -g newman"
}

if (-not (Test-Path $collection)) {
    throw "Collection introuvable: $collection"
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

Write-Host "[2/3] Lancement du scenario Newman..."
$newmanArgs = @(
    "run", $collection,
    "--iteration-count", $Iterations,
    "--delay-request", $DelayMs,
    "--reporters", "cli,json",
    "--reporter-json-export", $newmanReport
)

newman @newmanArgs | Tee-Object -FilePath $newmanStdout

Write-Host "[3/3] Finalisation de la capture docker stats..."
Receive-Job -Job $statsJob -Wait | Out-Null
Remove-Job -Job $statsJob

Write-Host "Benchmark termine. Sorties:"
Write-Host "- $newmanReport"
Write-Host "- $newmanStdout"
Write-Host "- $statsFile"
