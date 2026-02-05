# ============================================================
# Script de Inicio para Sistema SACP
# ============================================================
# Uso: .\scripts\start.ps1 [-GpuType nvidia|amd|cpu] [-Dev]

param(
    [ValidateSet('auto', 'nvidia', 'amd', 'cpu')]
    [string]$GpuType = 'auto',
    
    [switch]$Dev = $false,
    
    [switch]$Build = $false,
    
    [switch]$Logs = $false,
    
    [switch]$Help
)

if ($Help) {
    Write-Host @"
Start Script para Sistema SACP

OPCIONES:
  -GpuType <tipo>     Tipo de GPU: auto, nvidia, amd, cpu (default: auto)
  -Dev                Modo desarrollo (incluye pgAdmin)
  -Build              Reconstruir antes de iniciar
  -Logs               Mostrar logs después de iniciar
  -Help               Mostrar esta ayuda

EJEMPLOS:
  .\scripts\start.ps1
  .\scripts\start.ps1 -GpuType nvidia -Dev
  .\scripts\start.ps1 -Build -Logs
"@
    exit 0
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   Iniciando Sistema SACP" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Detectar GPU si es necesario
if ($GpuType -eq 'auto') {
    Write-Host "Detectando GPU..." -ForegroundColor Yellow
    # Reutilizar lógica de build.ps1
    if (Test-Path "scripts\build.ps1") {
        $detected = & .\scripts\build.ps1 -GpuType auto -NoBuild 2>&1 | Select-String "seleccionado: (\w+)"
        if ($detected) {
            $GpuType = $detected.Matches.Groups[1].Value
        } else {
            $GpuType = 'cpu'
        }
    } else {
        $GpuType = 'cpu'
    }
}

Write-Host "GPU Type: $GpuType" -ForegroundColor Cyan
Write-Host ""

# Configurar archivos compose
$composeFiles = @("-f", "docker-compose.yml")
switch ($GpuType) {
    "nvidia" { $composeFiles += @("-f", "docker-compose.nvidia.yml") }
    "amd"    { $composeFiles += @("-f", "docker-compose.amd.yml") }
    "cpu"    { $composeFiles += @("-f", "docker-compose.cpu.yml") }
}

# Build si se solicita
if ($Build) {
    Write-Host "Reconstruyendo imágenes..." -ForegroundColor Yellow
    & docker compose $composeFiles build
    Write-Host ""
}

# Perfil de desarrollo
$profile = @()
if ($Dev) {
    $profile = @("--profile", "dev")
    Write-Host "Modo desarrollo activado (incluye pgAdmin)" -ForegroundColor Yellow
}

# Iniciar servicios
Write-Host "Iniciando contenedores..." -ForegroundColor Cyan
$startCmd = @("compose") + $composeFiles + @("up", "-d") + $profile
& docker $startCmd

if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Error iniciando servicios" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "   Sistema Iniciado" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

# Mostrar estado
Write-Host "Estado de servicios:" -ForegroundColor Cyan
& docker compose $composeFiles ps

Write-Host ""
Write-Host "URLs de acceso:" -ForegroundColor Cyan
Write-Host "  Python API: http://localhost:8001/health/" -ForegroundColor White
Write-Host "  PostgreSQL: localhost:5432" -ForegroundColor White
if ($Dev) {
    Write-Host "  pgAdmin:    http://localhost:5050" -ForegroundColor White
}

Write-Host ""
Write-Host "Comandos útiles:" -ForegroundColor Cyan
Write-Host "  Ver logs:     docker compose $($composeFiles -join ' ') logs -f" -ForegroundColor Gray
Write-Host "  Detener:      docker compose $($composeFiles -join ' ') down" -ForegroundColor Gray
Write-Host "  Reiniciar:    docker compose $($composeFiles -join ' ') restart" -ForegroundColor Gray

if ($Logs) {
    Write-Host ""
    Write-Host "Mostrando logs (Ctrl+C para salir)..." -ForegroundColor Yellow
    & docker compose $composeFiles logs -f
}
