# 🐳 Dockerización del Sistema SACP

Sistema completamente dockerizado con **soporte dinámico para GPU** (NVIDIA, AMD) y procesamiento paralelo optimizado.

---

## 📋 Requisitos Previos

### Software Base
- **Docker Desktop** 24.0+ (Windows/Mac) o Docker Engine 24.0+ (Linux)
- **Docker Compose** 2.20+
- **Git** (para clonar el repositorio)

### Para GPU NVIDIA (RTX/GTX)
- **NVIDIA Driver** 525.60.13+ (Linux) o 528.33+ (Windows)
- **NVIDIA Container Toolkit**
  ```bash
  # Linux
  distribution=$(. /etc/os-release;echo $ID$VERSION_ID)
  curl -s -L https://nvidia.github.io/nvidia-docker/gpgkey | sudo apt-key add -
  curl -s -L https://nvidia.github.io/nvidia-docker/$distribution/nvidia-docker.list | sudo tee /etc/apt/sources.list.d/nvidia-docker.list
  sudo apt-get update && sudo apt-get install -y nvidia-container-toolkit
  sudo systemctl restart docker
  ```

### Para GPU AMD (RX)
- **ROCm** 5.7+
- Drivers AMD actualizados
- Docker configurado con soporte ROCm

---

## 🚀 Inicio Rápido

### 1. Configuración Inicial

```powershell
# Clonar repositorio (si no lo has hecho)
cd c:\Users\jesus\IdeaProjects\Analisis-de-imagenes

# Copiar configuración de ejemplo
Copy-Item .env.example .env

# Editar configuración (IMPORTANTE: configurar credenciales DB y Cloudinary)
notepad .env
```

### 2. Detección Automática y Build

```powershell
# Detección automática de GPU y construcción
.\scripts\build.ps1

# O especificar GPU manualmente
.\scripts\build.ps1 -GpuType nvidia  # Para NVIDIA
.\scripts\build.ps1 -GpuType amd     # Para AMD
.\scripts\build.ps1 -GpuType cpu     # Sin GPU
```

### 3. Iniciar Sistema

```powershell
# Inicio simple
.\scripts\start.ps1

# Con modo desarrollo (incluye pgAdmin)
.\scripts\start.ps1 -Dev

# Ver logs en tiempo real
.\scripts\start.ps1 -Logs
```

### 4. Verificar Estado

```powershell
# Ver servicios activos
docker compose ps

# Ver logs
docker compose logs -f python-api
docker compose logs -f java-app

# Verificar API
curl http://localhost:8001/health/
```

---

## 🏗️ Arquitectura Docker

```
┌─────────────────────────────────────────────────┐
│           Docker Network (sacp-network)         │
│                                                 │
│  ┌──────────────┐      ┌───────────────┐      │
│  │  Java App    │─────▶│  Python API   │      │
│  │  (JavaFX)    │ HTTP │  (FastAPI)    │      │
│  │  Port: -     │      │  Port: 8001   │      │
│  └──────┬───────┘      └───────┬───────┘      │
│         │                      │               │
│         │                      │               │
│         │                      ▼               │
│         │              ┌──────────────┐        │
│         └─────────────▶│  PostgreSQL  │        │
│                        │  Port: 5432  │        │
│                        └──────────────┘        │
│                                                 │
│  ┌──────────────┐                              │
│  │   pgAdmin    │ (solo modo dev)              │
│  │  Port: 5050  │                              │
│  └──────────────┘                              │
└─────────────────────────────────────────────────┘

        ▼ GPU Support (dinámico) ▼
┌──────────────────────────────────────┐
│  NVIDIA CUDA  │  AMD ROCm  │  CPU    │
│  (RTX/GTX)    │  (RX)      │         │
└──────────────────────────────────────┘
```

---

## 📁 Estructura de Archivos Docker

```
.
├── Dockerfile.python          # API Python con multi-stage para GPU
├── Dockerfile.java            # Aplicación JavaFX
├── docker-compose.yml         # Orquestación base
├── docker-compose.nvidia.yml  # Override para NVIDIA
├── docker-compose.amd.yml     # Override para AMD
├── docker-compose.cpu.yml     # Override para CPU
├── .dockerignore              # Archivos excluidos del build
├── .env.example               # Plantilla de configuración
└── scripts/
    ├── build.ps1              # Script de construcción
    ├── start.ps1              # Script de inicio
    └── stop.ps1               # Script de detención
```

---

## 🎮 Uso de GPU

### Detección Automática
El sistema detecta automáticamente tu GPU:
```powershell
.\scripts\build.ps1  # Detecta y configura automáticamente
```

### NVIDIA (RTX/GTX)
```powershell
# Build y start específico
.\scripts\build.ps1 -GpuType nvidia
.\scripts\start.ps1 -GpuType nvidia

# O manualmente
docker compose -f docker-compose.yml -f docker-compose.nvidia.yml up -d
```

**Verificar GPU:**
```powershell
docker exec sacp-python-api nvidia-smi
```

### AMD (RX Series)
```powershell
# Build y start específico
.\scripts\build.ps1 -GpuType amd
.\scripts\start.ps1 -GpuType amd

# O manualmente
docker compose -f docker-compose.yml -f docker-compose.amd.yml up -d
```

**Verificar GPU:**
```powershell
docker exec sacp-python-api rocm-smi
```

### CPU (Fallback)
```powershell
.\scripts\build.ps1 -GpuType cpu
.\scripts\start.ps1 -GpuType cpu
```

---

## 🔧 Comandos Útiles

### Gestión de Contenedores
```powershell
# Iniciar
docker compose up -d

# Detener
docker compose down

# Reiniciar
docker compose restart

# Ver logs
docker compose logs -f
docker compose logs -f python-api  # Solo un servicio

# Entrar a un contenedor
docker exec -it sacp-python-api bash
docker exec -it sacp-java-app bash
```

### Desarrollo
```powershell
# Reconstruir sin cache
.\scripts\build.ps1 -NoCacheApi

# Modo desarrollo con pgAdmin
.\scripts\start.ps1 -Dev

# Actualizar solo Python (hot reload)
docker compose restart python-api
```

### Depuración
```powershell
# Ver uso de recursos
docker stats

# Inspeccionar red
docker network inspect sacp-network

# Ver volúmenes
docker volume ls
docker volume inspect sacp-postgres-data

# Limpiar todo (⚠ CUIDADO)
.\scripts\stop.ps1 -RemoveVolumes -RemoveImages
```

---

## 🔍 Troubleshooting

### GPU no detectada
```powershell
# Verificar drivers
nvidia-smi        # NVIDIA
rocm-smi          # AMD

# Verificar Docker GPU support
docker run --rm --gpus all nvidia/cuda:11.8.0-base-ubuntu22.04 nvidia-smi

# Ver logs de inicio
docker compose logs python-api | Select-String "GPU"
```

### Error de conexión Python-Java
```powershell
# Verificar red
docker network inspect sacp-network

# Ping entre contenedores
docker exec sacp-java-app ping python-api

# Verificar API
docker exec sacp-python-api curl http://localhost:8001/health/
```

### Base de datos no conecta
```powershell
# Verificar PostgreSQL
docker compose logs postgres

# Conectar manualmente
docker exec -it sacp-postgres psql -U postgres -d fish_quality_db

# Verificar .env
Get-Content .env | Select-String "PG"
```

### Problemas de memoria
```powershell
# Aumentar límites en docker-compose.yml
# Sección deploy.resources.limits.memory

# Verificar uso actual
docker stats sacp-python-api
```

---

## ⚙️ Configuración Avanzada

### Variables de Entorno (.env)

```env
# GPU Configuration
GPU_TYPE=auto                    # auto, nvidia, amd, cpu
CUDA_VISIBLE_DEVICES=0           # GPU específica (NVIDIA)
HIP_VISIBLE_DEVICES=0            # GPU específica (AMD)

# Performance Tuning
WORKERS=2                        # Workers de FastAPI
OMP_NUM_THREADS=4                # Threads OpenMP (CPU)
TF_GPU_THREAD_COUNT=2            # Threads GPU

# Memory Limits
JAVA_OPTS=-Xmx2g -Xms512m        # JVM heap
MALLOC_ARENA_MAX=2               # Reduce memory fragmentation
```

### Procesamiento Paralelo

El sistema está optimizado para:
- **Batch processing** de imágenes en GPU
- **Multi-threading** en preprocesamiento
- **Async workers** en FastAPI (configurable con `WORKERS`)
- **Connection pooling** en DB (HikariCP)

### Escalabilidad

```yaml
# docker-compose.yml
services:
  python-api:
    deploy:
      replicas: 2  # Múltiples instancias
      resources:
        limits:
          cpus: '4'
          memory: 4G
```

---

## 📊 Monitoreo

### Health Checks
```powershell
# API Python
curl http://localhost:8001/health/

# PostgreSQL
docker exec sacp-postgres pg_isready

# Todos los servicios
docker compose ps
```

### Logs Estructurados
```powershell
# Logs con timestamps
docker compose logs -f --timestamps

# Solo errores
docker compose logs python-api | Select-String "ERROR"

# Últimas 100 líneas
docker compose logs --tail=100
```

---

## 🔒 Seguridad

### Checklist
- [ ] `.env` en `.gitignore`
- [ ] Credenciales DB cambiadas del default
- [ ] Firewall configurado (solo puertos necesarios)
- [ ] Volúmenes con permisos correctos
- [ ] No exponer puertos innecesarios
- [ ] Actualizar imágenes base regularmente

---

## 📦 Producción

### Build para Producción
```powershell
# Build optimizado
docker compose -f docker-compose.yml -f docker-compose.prod.yml build

# Sin cache para asegurar actualización
docker compose build --no-cache
```

### Deployment
```powershell
# Exportar imágenes
docker save sacp-python-api:latest | gzip > sacp-python-api.tar.gz
docker save sacp-java-app:latest | gzip > sacp-java-app.tar.gz

# Importar en servidor
docker load < sacp-python-api.tar.gz
docker load < sacp-java-app.tar.gz
```

---

## 🆘 Soporte

### Limpiar y Reiniciar
```powershell
# Detener todo
.\scripts\stop.ps1

# Limpiar completamente
docker system prune -a --volumes
docker network prune
docker volume prune

# Rebuild desde cero
.\scripts\build.ps1 -NoCacheApi -NoCacheApp
.\scripts\start.ps1
```

### Verificar Instalación
```powershell
# Verificar Docker
docker --version
docker compose version

# Verificar GPU
nvidia-smi  # NVIDIA
rocm-smi    # AMD

# Test completo
docker run --rm --gpus all nvidia/cuda:11.8.0-base-ubuntu22.04 nvidia-smi
```

---

## 📝 Próximos Pasos

1. ✅ Dockerización completada
2. ⏭️ Implementar tests en contenedores
3. ⏭️ CI/CD con GitHub Actions
4. ⏭️ Kubernetes manifests (opcional)
5. ⏭️ Monitoring con Prometheus/Grafana

---

**¿Problemas?** Revisa logs con `docker compose logs -f` o abre un issue en el repositorio.
