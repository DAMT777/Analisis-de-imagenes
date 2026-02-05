# 🚀 Sistema SACP - Dockerizado

[![Docker](https://img.shields.io/badge/Docker-24.0+-blue.svg)](https://www.docker.com/)
[![GPU](https://img.shields.io/badge/GPU-NVIDIA%20|%20AMD-green.svg)](README.Docker.md)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Python](https://img.shields.io/badge/Python-3.10-blue.svg)](https://www.python.org/)

Sistema de Análisis de Calidad de Pescado completamente dockerizado con **soporte dinámico para GPU** y procesamiento paralelo optimizado.

---

## 🎯 Inicio Rápido (5 minutos)

### Windows (PowerShell)
```powershell
# 1. Configurar
Copy-Item .env.example .env
notepad .env  # Editar credenciales

# 2. Construir
.\scripts\build.ps1

# 3. Iniciar
.\scripts\start.ps1 -Dev
```

### Linux/macOS
```bash
# 1. Configurar
cp .env.example .env
nano .env  # Editar credenciales

# 2. Construir
chmod +x scripts/*.sh
./scripts/build.sh

# 3. Iniciar
./scripts/start.sh --dev
```

### Con Make (Recomendado)
```bash
make init   # Configuración inicial
make build  # Construir imágenes
make dev    # Iniciar en modo desarrollo
```

---

## 📋 Requisitos

- **Docker Desktop** 24.0+ (Windows/Mac) o Docker Engine 24.0+ (Linux)
- **Docker Compose** 2.20+
- **NVIDIA Driver** 525+ (para GPU NVIDIA) + NVIDIA Container Toolkit
- **ROCm** 5.7+ (para GPU AMD)

[Ver guía completa de instalación →](README.Docker.md)

---

## 🏗️ Arquitectura

```
Java App (JavaFX) ←→ Python API (FastAPI + TensorFlow) ←→ PostgreSQL
                              ↓
                        GPU (NVIDIA/AMD/CPU)
```

- **Frontend/Backend Java**: JavaFX con lógica de negocio
- **ML Backend Python**: FastAPI con TensorFlow/Keras para CNN
- **Base de datos**: PostgreSQL 15
- **GPU**: Detección automática NVIDIA/AMD con fallback a CPU
- **Comunicación**: REST API con procesamiento asíncrono

---

## 🎮 Uso de GPU

### Detección Automática
```bash
.\scripts\build.ps1  # Detecta automáticamente
```

### GPU Específica
```powershell
# NVIDIA (RTX/GTX)
.\scripts\build.ps1 -GpuType nvidia
.\scripts\start.ps1 -GpuType nvidia

# AMD (RX)
.\scripts\build.ps1 -GpuType amd

# CPU (sin GPU)
.\scripts\build.ps1 -GpuType cpu
```

### Verificar GPU en uso
```bash
# NVIDIA
docker exec sacp-python-api nvidia-smi

# AMD
docker exec sacp-python-api rocm-smi
```

---

## 📦 Comandos Principales

### Scripts PowerShell (Windows)
```powershell
.\scripts\build.ps1 [-GpuType nvidia|amd|cpu] [-NoCacheApi]
.\scripts\start.ps1 [-Dev] [-Logs]
.\scripts\stop.ps1 [-RemoveVolumes]
```

### Scripts Bash (Linux/macOS)
```bash
./scripts/build.sh [--gpu-type nvidia|amd|cpu]
./scripts/start.sh [--dev] [--logs]
```

### Makefile (Multiplataforma)
```bash
make build          # Construir imágenes
make start          # Iniciar servicios
make dev            # Modo desarrollo (con pgAdmin)
make logs           # Ver logs en tiempo real
make stop           # Detener servicios
make clean          # Limpiar contenedores
make test           # Ejecutar tests
make health         # Verificar estado
```

---

## 🔧 Comandos Docker Compose

```bash
# Iniciar
docker compose up -d

# Con GPU NVIDIA
docker compose -f docker-compose.yml -f docker-compose.nvidia.yml up -d

# Ver logs
docker compose logs -f python-api

# Estado
docker compose ps

# Detener
docker compose down
```

---

## 🌐 URLs de Acceso

- **Python API**: http://localhost:8001
- **API Docs (Swagger)**: http://localhost:8001/docs
- **PostgreSQL**: localhost:5432
- **pgAdmin** (modo dev): http://localhost:5050

---

## 🔍 Troubleshooting Rápido

```bash
# Ver logs de error
docker compose logs python-api | grep ERROR

# Reiniciar servicio específico
docker compose restart python-api

# Verificar conectividad
curl http://localhost:8001/health/

# Entrar al contenedor
docker exec -it sacp-python-api bash

# Ver uso de recursos
docker stats
```

---

## 📊 Optimizaciones Implementadas

### Procesamiento Paralelo
- ✅ Multi-threading para I/O
- ✅ Batch processing en GPU
- ✅ Async/await en FastAPI
- ✅ Connection pooling (HikariCP)

### GPU
- ✅ Detección automática NVIDIA/AMD
- ✅ Memory growth dinámico
- ✅ TensorRT support (NVIDIA)
- ✅ Fallback inteligente a CPU

### Docker
- ✅ Multi-stage builds
- ✅ Layer caching optimizado
- ✅ Health checks
- ✅ Resource limits configurables

---

## 📁 Estructura del Proyecto

```
.
├── docker-compose.yml          # Orquestación principal
├── docker-compose.nvidia.yml   # Override NVIDIA
├── docker-compose.amd.yml      # Override AMD
├── Dockerfile.python           # API Python
├── Dockerfile.java             # App JavaFX
├── scripts/
│   ├── build.ps1              # Build Windows
│   ├── start.ps1              # Start Windows
│   ├── build.sh               # Build Linux/macOS
│   └── start.sh               # Start Linux/macOS
├── python/
│   ├── api_optimized.py       # API optimizada
│   ├── parallel_config.py     # Config paralelo
│   └── ...
└── Makefile                   # Comandos Make
```

---

## 🔐 Configuración (.env)

```env
# GPU
GPU_TYPE=auto                    # auto, nvidia, amd, cpu

# Database
PGHOST=postgres
PGDATABASE=fish_quality_db
PGUSER=postgres
PGPASSWORD=tu_password_seguro

# Cloudinary
CLOUDINARY_URL=cloudinary://...

# Performance
WORKERS=2                        # FastAPI workers
JAVA_OPTS=-Xmx2g -Xms512m
```

---

## 📚 Documentación Completa

- [**README.Docker.md**](README.Docker.md) - Guía detallada de Dockerización
- [**README.md**](README.md) - Documentación original del proyecto

---

## 🆘 Comandos de Emergencia

```bash
# Limpiar todo y empezar de nuevo
docker compose down -v
docker system prune -a
make init
make build
make start

# Backup de base de datos
make backup-db

# Ver estadísticas de rendimiento
docker stats --no-stream
```

---

## 🚀 Próximos Pasos

1. **Iniciar servicios**: `make dev`
2. **Verificar health**: `make health`
3. **Ver logs**: `make logs`
4. **Ejecutar Java app**: Conectará automáticamente a Python API

---

## 📝 Changelog

### v2.0.0 (2026-02-05)
- ✅ Dockerización completa
- ✅ Soporte GPU dinámico (NVIDIA/AMD)
- ✅ Procesamiento paralelo optimizado
- ✅ Scripts automatizados (PowerShell/Bash)
- ✅ API FastAPI mejorada
- ✅ Health checks y monitoring

---

**¿Necesitas ayuda?** Revisa [README.Docker.md](README.Docker.md) o los logs con `make logs`
