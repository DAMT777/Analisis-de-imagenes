# ============================================================
# Script de Build para Sistema SACP
# ============================================================
# Uso: .\scripts\build.ps1 [-GpuType nvidia|amd|cpu] [-NoBuild]

param(
    [ValidateSet('auto', 'nvidia', 'amd', 'cpu')]
    [string]$GpuType = 'auto',
    
    [switch]$NoBuild = $false,
    
    [switch]$NoCacheApi = $false,
    
    [switch]$NoCacheApp = $false,
    
    [switch]$Help
)

if ($Help) {
    Write-Host @"
Build Script para Sistema SACP

OPCIONES:
  -GpuType <tipo>     Tipo de GPU: auto, nvidia, amd, cpu (default: auto)
  -NoBuild            Solo detectar GPU, no construir
  -NoCacheApi         Reconstruir API Python sin cache
  -NoCacheApp         Reconstruir aplicacion Java sin cache
  -Help               Mostrar esta ayuda

EJEMPLOS:
  .\scripts\build.ps1
  .\scripts\build.ps1 -GpuType nvidia
  .\scripts\build.ps1 -NoCacheApi
  .\scripts\build.ps1 -GpuType cpu -NoBuild
"@
    exit 0
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   Build Sistema SACP" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# ============================================================
# Detectar GPU si es auto
# ============================================================
function Detect-GPU {
    Write-Host "Detectando hardware GPU..." -ForegroundColor Yellow
    
    # Verificar NVIDIA
    try {
        $nvidiaOutput = & nvidia-smi 2>&1
        if ($LASTEXITCODE -eq 0) {
            Write-Host "[OK] GPU NVIDIA detectada" -ForegroundColor Green
            $nvidiaOutput | Select-String "CUDA Version" | ForEach-Object {
                Write-Host "  $_" -ForegroundColor Gray
            }
            return "nvidia"
        }
    }
    catch {
        Write-Host "  NVIDIA: No detectada" -ForegroundColor Gray
    }
    
    # Verificar AMD
    try {
        $amdOutput = & rocm-smi 2>&1
        if ($LASTEXITCODE -eq 0) {
            Write-Host "[OK] GPU AMD detectada" -ForegroundColor Green
            return "amd"
        }
    }
    catch {
        Write-Host "  AMD: No detectada" -ForegroundColor Gray
    }
    
    # Verificar mediante WMI (Windows)
    try {
        $gpus = Get-WmiObject Win32_VideoController
        foreach ($gpu in $gpus) {
            $name = $gpu.Name.ToLower()
            if ($name -match "nvidia|geforce|rtx|gtx|quadro") {
                Write-Host "[OK] GPU NVIDIA encontrada: $($gpu.Name)" -ForegroundColor Green
                Write-Host "  (nvidia-smi no disponible, usando deteccion basica)" -ForegroundColor Yellow
                return "nvidia"
            }
            if ($name -match "amd|radeon|rx") {
                Write-Host "[OK] GPU AMD encontrada: $($gpu.Name)" -ForegroundColor Green
                Write-Host "  (rocm-smi no disponible, usando deteccion basica)" -ForegroundColor Yellow
                return "amd"
            }
        }
    }
    catch {
        Write-Host "  No se pudo detectar GPU via WMI" -ForegroundColor Gray
    }
    
    Write-Host "[!] No se detecto GPU compatible, usando CPU" -ForegroundColor Yellow
    return "cpu"
}

if ($GpuType -eq 'auto') {
    $GpuType = Detect-GPU
    Write-Host ""
}

Write-Host "Tipo de GPU seleccionado: $GpuType" -ForegroundColor Cyan
Write-Host ""

if ($NoBuild) {
    Write-Host "Modo deteccion unicamente (-NoBuild activado)" -ForegroundColor Yellow
    exit 0
}

# ============================================================
# Verificar Docker
# ============================================================
Write-Host "Verificando Docker..." -ForegroundColor Yellow
try {
    $dockerVersion = docker --version
    Write-Host "[OK] $dockerVersion" -ForegroundColor Green
}
catch {
    Write-Host "[ERROR] Docker no esta instalado o no esta en PATH" -ForegroundColor Red
    exit 1
}

try {
    $composeVersion = docker compose version
    Write-Host "[OK] $composeVersion" -ForegroundColor Green
}
catch {
    Write-Host "[ERROR] Docker Compose no esta disponible" -ForegroundColor Red
    exit 1
}
Write-Host ""

# ============================================================
# Verificar archivos necesarios
# ============================================================
Write-Host "Verificando archivos..." -ForegroundColor Yellow

$requiredFiles = @(
    "docker-compose.yml",
    "Dockerfile.python",
    "Dockerfile.java",
    "python\api.py",
    "pom.xml"
)

$missingFiles = @()
foreach ($file in $requiredFiles) {
    if (-not (Test-Path $file)) {
        $missingFiles += $file
    }
}

if ($missingFiles.Count -gt 0) {
    Write-Host "[ERROR] Archivos faltantes:" -ForegroundColor Red
    $missingFiles | ForEach-Object { Write-Host "  - $_" -ForegroundColor Red }
    exit 1
}
Write-Host "[OK] Archivos verificados" -ForegroundColor Green
Write-Host ""

# ============================================================
# Configurar variables de entorno
# ============================================================
if (-not (Test-Path ".env")) {
    Write-Host "[!] Archivo .env no encontrado, creando desde .env.example..." -ForegroundColor Yellow
    if (Test-Path ".env.example") {
        Copy-Item ".env.example" ".env"
        Write-Host "[OK] Archivo .env creado. EDITALO antes de continuar." -ForegroundColor Yellow
        Write-Host ""
        Read-Host "Presiona Enter cuando hayas configurado .env"
    }
    else {
        Write-Host "[ERROR] No se encontro .env.example" -ForegroundColor Red
        exit 1
    }
}

# Cargar variables de entorno
Get-Content .env | ForEach-Object {
    if ($_ -match '^([^#][^=]*)=(.*)$') {
        [System.Environment]::SetEnvironmentVariable($matches[1].Trim(), $matches[2].Trim(), 'Process')
    }
}

# ============================================================
# Construir imagenes
# ============================================================
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   Construyendo Imagenes Docker" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$composeFiles = @("-f", "docker-compose.yml")

switch ($GpuType) {
    "nvidia" { $composeFiles += @("-f", "docker-compose.nvidia.yml") }
    "amd"    { $composeFiles += @("-f", "docker-compose.amd.yml") }
    "cpu"    { $composeFiles += @("-f", "docker-compose.cpu.yml") }
}

# Build API Python
Write-Host "Construyendo Python API..." -ForegroundColor Cyan
$buildArgs = @("compose") + $composeFiles + @("build")
if ($NoCacheApi) {
    $buildArgs += "--no-cache"
}
$buildArgs += @("--build-arg", "GPU_TYPE=$GpuType", "python-api")

& docker $buildArgs
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Error construyendo Python API" -ForegroundColor Red
    exit 1
}
Write-Host "[OK] Python API construida" -ForegroundColor Green
Write-Host ""

# Build Java App
Write-Host "Construyendo Java App..." -ForegroundColor Cyan
$buildArgs = @("compose") + $composeFiles + @("build")
if ($NoCacheApp) {
    $buildArgs += "--no-cache"
}
$buildArgs += "java-app"

& docker $buildArgs
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Error construyendo Java App" -ForegroundColor Red
    exit 1
}
Write-Host "[OK] Java App construida" -ForegroundColor Green
Write-Host ""

Write-Host "========================================" -ForegroundColor Green
Write-Host "   Build Completado Exitosamente" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Para iniciar los servicios:" -ForegroundColor Cyan
Write-Host "  .\scripts\start.ps1" -ForegroundColor White
Write-Host ""
Write-Host "O manualmente:" -ForegroundColor Cyan
$startCmd = "docker compose " + ($composeFiles -join " ") + " up -d"
Write-Host "  $startCmd" -ForegroundColor White
