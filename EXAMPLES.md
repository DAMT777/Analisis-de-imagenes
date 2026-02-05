# 📖 Ejemplos de Uso - Sistema SACP Dockerizado

Ejemplos prácticos de uso del sistema dockerizado.

---

## 🚀 Escenarios Comunes

### 1. Primer Uso (Setup Completo)

```bash
# Windows (PowerShell)
cd c:\Users\jesus\IdeaProjects\Analisis-de-imagenes

# Crear configuración
Copy-Item .env.example .env
notepad .env  # Editar PGPASSWORD, CLOUDINARY_URL, etc.

# Build con detección automática de GPU
.\scripts\build.ps1

# Iniciar en modo desarrollo
.\scripts\start.ps1 -Dev -Logs

# En otra terminal: verificar
curl http://localhost:8001/health/
```

### 2. Desarrollo Diario

```bash
# Iniciar servicios
make start

# Ver logs
make logs-api

# Hacer cambios en código Python
# Los cambios se reflejan automáticamente (volume mount)

# Reiniciar API si es necesario
docker compose restart python-api

# Detener al final del día
make stop
```

### 3. Testing de GPU

```bash
# Build específico para NVIDIA
.\scripts\build.ps1 -GpuType nvidia

# Iniciar
.\scripts\start.ps1 -GpuType nvidia

# Verificar GPU en uso
docker exec sacp-python-api nvidia-smi

# Test de inferencia
docker exec sacp-python-api python3 -c "
import tensorflow as tf
print('GPUs disponibles:', tf.config.list_physical_devices('GPU'))
print('CUDA disponible:', tf.test.is_built_with_cuda())
print('GPU disponible:', tf.test.is_gpu_available())
"

# Ver uso en tiempo real
docker exec sacp-python-api nvidia-smi -l 1
```

### 4. Comparación CPU vs GPU

```bash
# ===== Test con CPU =====
.\scripts\build.ps1 -GpuType cpu
.\scripts\start.ps1 -GpuType cpu

# Procesar batch
Measure-Command {
    curl -X POST http://localhost:8001/procesar_batch/ `
        -H "Content-Type: application/json" `
        -d '{"folder_path": "C:/ruta/imagenes", "max_images": 10}'
}
# Anotar tiempo

.\scripts\stop.ps1

# ===== Test con GPU NVIDIA =====
.\scripts\build.ps1 -GpuType nvidia
.\scripts\start.ps1 -GpuType nvidia

# Mismo batch
Measure-Command {
    curl -X POST http://localhost:8001/procesar_batch/ `
        -H "Content-Type: application/json" `
        -d '{"folder_path": "C:/ruta/imagenes", "max_images": 10}'
}
# Comparar tiempos
```

---

## 🔧 Comandos API

### Health Check

```bash
# Básico
curl http://localhost:8001/health/

# Con formato JSON bonito (Windows PowerShell)
curl http://localhost:8001/health/ | ConvertFrom-Json | ConvertTo-Json

# Linux/macOS
curl -s http://localhost:8001/health/ | jq .
```

**Respuesta esperada:**
```json
{
  "status": "ok",
  "version": "2.0.0",
  "gpu_available": true,
  "gpu_type": "nvidia",
  "gpu_name": "NVIDIA GeForce RTX 3060",
  "workers": 8
}
```

### Procesar Una Imagen

```bash
# Windows PowerShell
$body = @{
    image_path = "C:\Users\jesus\IdeaProjects\Analisis-de-imagenes\test_image.jpg"
    solo_ojo = $false
} | ConvertTo-Json

curl -X POST http://localhost:8001/procesar/ `
    -H "Content-Type: application/json" `
    -d $body

# Linux/macOS
curl -X POST http://localhost:8001/procesar/ \
    -H "Content-Type: application/json" \
    -d '{
        "image_path": "/app/../test_image.jpg",
        "solo_ojo": false
    }'
```

**Respuesta esperada:**
```json
{
  "calificacion_ojos": 4.25,
  "calificacion_piel": 3.80,
  "processed_image_path": "/app/temp/seg_test_image.jpg",
  "anomalia": false,
  "processing_time_seconds": 0.234
}
```

### Clasificar si es Pez

```bash
curl -X POST http://localhost:8001/es_pez/ `
    -H "Content-Type: application/json" `
    -d '{"image_path": "C:/ruta/imagen.jpg"}'
```

**Respuesta:**
```json
{
  "es_pez": true,
  "image_path": "C:/ruta/imagen.jpg"
}
```

### Procesar Batch

```bash
# PowerShell
$batch = @{
    folder_path = "C:\Users\jesus\IdeaProjects\Analisis-de-imagenes\Artefactos\Clasificación y Dataset\cachamas\pez"
    solo_ojo = $false
    max_images = 10
} | ConvertTo-Json

$response = curl -X POST http://localhost:8001/procesar_batch/ `
    -H "Content-Type: application/json" `
    -d $batch | ConvertFrom-Json

# Ver resultados
$response.batch_results | ForEach-Object {
    Write-Host "Imagen: $($_.image)"
    Write-Host "  Ojos: $($_.result.calificacion_ojos)"
    Write-Host "  Piel: $($_.result.calificacion_piel)"
    Write-Host "  Anomalía: $($_.result.anomalia)"
    Write-Host ""
}

Write-Host "Tiempo total: $($response.processing_time_seconds) segundos"
Write-Host "Velocidad: $($response.images_per_second) img/s"
```

---

## 🗄️ Gestión de Base de Datos

### Conectar a PostgreSQL

```bash
# Desde contenedor
docker exec -it sacp-postgres psql -U postgres -d fish_quality_db

# Comandos útiles en psql:
\dt          # Listar tablas
\d tabla     # Describir tabla
\q           # Salir
```

### Backup

```bash
# Crear backup
make backup-db

# O manualmente
docker exec sacp-postgres pg_dump -U postgres fish_quality_db > backup_$(date +%Y%m%d).sql
```

### Restore

```bash
# Desde backup
cat backup_20260205.sql | docker exec -i sacp-postgres psql -U postgres fish_quality_db

# O con Make
make restore-db
```

### pgAdmin (Modo Desarrollo)

```bash
# Iniciar con pgAdmin
.\scripts\start.ps1 -Dev

# Abrir navegador
start http://localhost:5050

# Login:
#   Email: admin@sacp.local
#   Password: admin (o el configurado en .env)

# Agregar servidor:
#   Host: postgres
#   Port: 5432
#   Database: fish_quality_db
#   Username: postgres
#   Password: (el de .env)
```

---

## 🔍 Debugging

### Ver Logs en Tiempo Real

```bash
# Todos los servicios
make logs

# Solo API Python
docker compose logs -f python-api

# Solo Java App
docker compose logs -f java-app

# Últimas 100 líneas
docker compose logs --tail=100 python-api

# Filtrar errores
docker compose logs python-api | grep -i error
```

### Entrar a Contenedores

```bash
# Python API
docker exec -it sacp-python-api bash

# Dentro del contenedor:
python3 detect_gpu.py
nvidia-smi
ls -la /app
exit

# Java App
docker exec -it sacp-java-app bash

# PostgreSQL
docker exec -it sacp-postgres bash
```

### Test Manual de GPU

```bash
# Ejecutar script Python en contenedor
docker exec sacp-python-api python3 << 'EOF'
import tensorflow as tf
import parallel_config

print("=" * 50)
print("GPU Configuration Test")
print("=" * 50)

# GPU Info
gpu_info = parallel_config.get_gpu_info()
print(f"GPU Type: {gpu_info['type']}")
print(f"GPU Available: {gpu_info['available']}")
print(f"GPU Name: {gpu_info['name']}")

# TensorFlow
gpus = tf.config.list_physical_devices('GPU')
print(f"\nTensorFlow GPUs: {len(gpus)}")
for gpu in gpus:
    print(f"  - {gpu}")

# CUDA
print(f"\nCUDA Available: {tf.test.is_built_with_cuda()}")
print(f"GPU Available: {tf.test.is_gpu_available()}")

print("=" * 50)
EOF
```

### Monitoreo de Recursos

```bash
# Stats en tiempo real
docker stats

# Solo Python API
docker stats sacp-python-api

# Formateo personalizado
docker stats --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}"

# GPU monitoring (NVIDIA)
watch -n 1 docker exec sacp-python-api nvidia-smi
```

---

## 🧪 Testing

### Test de Conectividad

```bash
# Health check
curl http://localhost:8001/health/

# Database
docker exec sacp-postgres pg_isready

# Verificar puertos
netstat -an | findstr "8001 5432"
```

### Test de Endpoints

```bash
# Script de test completo (PowerShell)
$API_URL = "http://localhost:8001"

Write-Host "Testing SACP API..." -ForegroundColor Cyan

# 1. Health
Write-Host "`n1. Health Check..." -ForegroundColor Yellow
$health = curl -s "$API_URL/health/" | ConvertFrom-Json
Write-Host "Status: $($health.status)" -ForegroundColor Green
Write-Host "GPU: $($health.gpu_name)" -ForegroundColor Green

# 2. Root
Write-Host "`n2. Root Endpoint..." -ForegroundColor Yellow
$root = curl -s "$API_URL/" | ConvertFrom-Json
Write-Host "API: $($root.api)" -ForegroundColor Green

# 3. Docs
Write-Host "`n3. Documentation..." -ForegroundColor Yellow
Write-Host "Swagger: $API_URL/docs" -ForegroundColor Cyan
Write-Host "ReDoc: $API_URL/redoc" -ForegroundColor Cyan

Write-Host "`n✓ Tests completados" -ForegroundColor Green
```

---

## 📊 Benchmarks

### Script de Benchmark

```bash
# benchmark.ps1
$IMAGE_PATH = "C:\ruta\imagen_test.jpg"
$ITERATIONS = 10

Write-Host "Benchmark - $ITERATIONS iteraciones" -ForegroundColor Cyan

$times = @()
for ($i = 1; $i -le $ITERATIONS; $i++) {
    $elapsed = Measure-Command {
        $result = curl -X POST http://localhost:8001/procesar/ `
            -H "Content-Type: application/json" `
            -d "{`"image_path`": `"$IMAGE_PATH`"}" `
            -s | ConvertFrom-Json
    }
    
    $seconds = $elapsed.TotalSeconds
    $times += $seconds
    Write-Host "Iteración $i : $($seconds.ToString('F3'))s"
}

$avg = ($times | Measure-Object -Average).Average
$min = ($times | Measure-Object -Minimum).Minimum
$max = ($times | Measure-Object -Maximum).Maximum

Write-Host "`nResultados:" -ForegroundColor Green
Write-Host "  Promedio: $($avg.ToString('F3'))s"
Write-Host "  Mínimo: $($min.ToString('F3'))s"
Write-Host "  Máximo: $($max.ToString('F3'))s"
Write-Host "  Throughput: $((1/$avg).ToString('F2')) img/s"
```

---

## 🔄 Workflows Completos

### Workflow 1: Desarrollo con Hot Reload

```bash
# 1. Iniciar modo dev
make dev

# 2. En otro terminal: ver logs
make logs-api

# 3. Editar código Python (api.py, analyze.py, etc.)
# Los cambios se reflejan automáticamente

# 4. Si necesitas reinstalar dependencias:
docker compose exec python-api pip install nueva-dependencia

# 5. Reiniciar solo si cambias requirements.txt:
docker compose restart python-api

# 6. Test
curl http://localhost:8001/health/
```

### Workflow 2: Deploy Producción

```bash
# 1. Build optimizado
make build-no-cache GPU_TYPE=nvidia

# 2. Test local
make start
make test

# 3. Verificar
make health

# 4. Backup DB antes de deploy
make backup-db

# 5. Deploy (ejemplo)
docker compose -f docker-compose.yml -f docker-compose.nvidia.yml up -d

# 6. Monitoreo
make logs
```

### Workflow 3: Actualización de Código

```bash
# 1. Detener servicios
make stop

# 2. Pull cambios
git pull origin main

# 3. Rebuild solo lo necesario
make build  # O específico: docker compose build python-api

# 4. Iniciar
make start

# 5. Verificar
make health
make logs
```

---

## 🚨 Solución de Problemas Comunes

### Problema: API no responde

```bash
# Verificar logs
docker compose logs python-api

# Verificar puerto
netstat -an | findstr "8001"

# Reiniciar
docker compose restart python-api

# Si persiste, rebuild
docker compose build python-api
docker compose up -d python-api
```

### Problema: GPU no detectada

```bash
# Verificar drivers host
nvidia-smi

# Verificar dentro del contenedor
docker exec sacp-python-api nvidia-smi

# Si no funciona, rebuild con GPU específica
.\scripts\stop.ps1
.\scripts\build.ps1 -GpuType nvidia -NoCacheApi
.\scripts\start.ps1 -GpuType nvidia
```

### Problema: Memoria insuficiente

```bash
# Ver uso actual
docker stats sacp-python-api

# Aumentar límites en docker-compose.yml:
# deploy.resources.limits.memory: 8G

# Aplicar cambios
docker compose up -d python-api
```

---

## 📚 Más Información

- [README.Docker.md](README.Docker.md) - Documentación completa
- [QUICKSTART.md](QUICKSTART.md) - Inicio rápido
- [OPTIMIZATIONS.md](OPTIMIZATIONS.md) - Detalles técnicos
- Swagger UI: http://localhost:8001/docs

---

**¿Dudas?** Revisa los logs: `make logs` o `docker compose logs -f`
