# ============================================================
# Script de Detención para Sistema SACP
# ============================================================

param(
    [switch]$RemoveVolumes = $false,
    [switch]$RemoveImages = $false,
    [switch]$Help
)

if ($Help) {
    Write-Host @"
Stop Script para Sistema SACP

OPCIONES:
  -RemoveVolumes      Eliminar volúmenes persistentes (⚠ BORRA DATOS)
  -RemoveImages       Eliminar imágenes Docker
  -Help               Mostrar esta ayuda

EJEMPLOS:
  .\scripts\stop.ps1
  .\scripts\stop.ps1 -RemoveVolumes
  .\scripts\stop.ps1 -RemoveVolumes -RemoveImages
"@
    exit 0
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   Deteniendo Sistema SACP" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$downArgs = @("compose", "-f", "docker-compose.yml", "down")

if ($RemoveVolumes) {
    Write-Host "⚠ Se eliminarán los volúmenes persistentes (DATOS DE BASE DE DATOS)" -ForegroundColor Yellow
    $confirm = Read-Host "¿Estás seguro? (y/N)"
    if ($confirm -eq 'y' -or $confirm -eq 'Y') {
        $downArgs += "-v"
    } else {
        Write-Host "Operación cancelada" -ForegroundColor Yellow
        exit 0
    }
}

if ($RemoveImages) {
    $downArgs += "--rmi", "local"
}

& docker $downArgs

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "✓ Sistema detenido correctamente" -ForegroundColor Green
} else {
    Write-Host ""
    Write-Host "✗ Error deteniendo sistema" -ForegroundColor Red
    exit 1
}
