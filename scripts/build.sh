#!/bin/bash
# ============================================================
# Script de Build para Linux/macOS
# ============================================================

set -e

GPU_TYPE="auto"
NO_BUILD=false
NO_CACHE_API=false
NO_CACHE_APP=false

# Colores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

usage() {
    echo "Build Script para Sistema SACP"
    echo ""
    echo "OPCIONES:"
    echo "  -g, --gpu-type <tipo>   Tipo de GPU: auto, nvidia, amd, cpu (default: auto)"
    echo "  -n, --no-build          Solo detectar GPU, no construir"
    echo "  --no-cache-api          Reconstruir API Python sin cache"
    echo "  --no-cache-app          Reconstruir aplicación Java sin cache"
    echo "  -h, --help              Mostrar esta ayuda"
    echo ""
    echo "EJEMPLOS:"
    echo "  ./scripts/build.sh"
    echo "  ./scripts/build.sh --gpu-type nvidia"
    echo "  ./scripts/build.sh --no-cache-api"
    exit 0
}

# Parse arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -g|--gpu-type)
            GPU_TYPE="$2"
            shift 2
            ;;
        -n|--no-build)
            NO_BUILD=true
            shift
            ;;
        --no-cache-api)
            NO_CACHE_API=true
            shift
            ;;
        --no-cache-app)
            NO_CACHE_APP=true
            shift
            ;;
        -h|--help)
            usage
            ;;
        *)
            echo "Opción desconocida: $1"
            usage
            ;;
    esac
done

echo -e "${CYAN}========================================${NC}"
echo -e "${CYAN}   Build Sistema SACP${NC}"
echo -e "${CYAN}========================================${NC}"
echo ""

# Detectar GPU
detect_gpu() {
    echo -e "${YELLOW}Detectando hardware GPU...${NC}"
    
    # Verificar NVIDIA
    if command -v nvidia-smi &> /dev/null; then
        if nvidia-smi &> /dev/null; then
            echo -e "${GREEN}✓ GPU NVIDIA detectada${NC}"
            nvidia-smi --query-gpu=name,driver_version --format=csv,noheader | head -1
            echo "nvidia"
            return
        fi
    fi
    
    # Verificar AMD
    if command -v rocm-smi &> /dev/null; then
        if rocm-smi &> /dev/null; then
            echo -e "${GREEN}✓ GPU AMD detectada${NC}"
            echo "amd"
            return
        fi
    fi
    
    # Verificar lspci
    if command -v lspci &> /dev/null; then
        GPU_INFO=$(lspci | grep -i 'vga\|3d\|display')
        if echo "$GPU_INFO" | grep -iq nvidia; then
            echo -e "${GREEN}✓ GPU NVIDIA encontrada (nvidia-smi no disponible)${NC}"
            echo "$GPU_INFO"
            echo "nvidia"
            return
        elif echo "$GPU_INFO" | grep -iq amd; then
            echo -e "${GREEN}✓ GPU AMD encontrada (rocm-smi no disponible)${NC}"
            echo "$GPU_INFO"
            echo "amd"
            return
        fi
    fi
    
    echo -e "${YELLOW}⚠ No se detectó GPU compatible, usando CPU${NC}"
    echo "cpu"
}

if [ "$GPU_TYPE" = "auto" ]; then
    GPU_TYPE=$(detect_gpu)
    echo ""
fi

echo -e "${CYAN}Tipo de GPU seleccionado: $GPU_TYPE${NC}"
echo ""

if [ "$NO_BUILD" = true ]; then
    echo -e "${YELLOW}Modo detección únicamente (-n activado)${NC}"
    exit 0
fi

# Verificar Docker
echo -e "${YELLOW}Verificando Docker...${NC}"
if ! command -v docker &> /dev/null; then
    echo -e "${RED}✗ Docker no está instalado${NC}"
    exit 1
fi
echo -e "${GREEN}✓ $(docker --version)${NC}"

if ! docker compose version &> /dev/null; then
    echo -e "${RED}✗ Docker Compose no está disponible${NC}"
    exit 1
fi
echo -e "${GREEN}✓ $(docker compose version)${NC}"
echo ""

# Verificar archivos
echo -e "${YELLOW}Verificando archivos...${NC}"
REQUIRED_FILES=("docker-compose.yml" "Dockerfile.python" "Dockerfile.java" "python/api.py" "pom.xml")
MISSING=false
for file in "${REQUIRED_FILES[@]}"; do
    if [ ! -f "$file" ]; then
        echo -e "${RED}✗ Falta: $file${NC}"
        MISSING=true
    fi
done

if [ "$MISSING" = true ]; then
    exit 1
fi
echo -e "${GREEN}✓ Archivos verificados${NC}"
echo ""

# Configurar .env
if [ ! -f ".env" ]; then
    echo -e "${YELLOW}⚠ Archivo .env no encontrado${NC}"
    if [ -f ".env.example" ]; then
        cp .env.example .env
        echo -e "${YELLOW}✓ Archivo .env creado. EDÍTALO antes de continuar.${NC}"
        echo ""
        read -p "Presiona Enter cuando hayas configurado .env..."
    else
        echo -e "${RED}✗ No se encontró .env.example${NC}"
        exit 1
    fi
fi

# Construir imágenes
echo -e "${CYAN}========================================${NC}"
echo -e "${CYAN}   Construyendo Imágenes Docker${NC}"
echo -e "${CYAN}========================================${NC}"
echo ""

COMPOSE_FILES="-f docker-compose.yml"
case $GPU_TYPE in
    nvidia) COMPOSE_FILES="$COMPOSE_FILES -f docker-compose.nvidia.yml" ;;
    amd)    COMPOSE_FILES="$COMPOSE_FILES -f docker-compose.amd.yml" ;;
    cpu)    COMPOSE_FILES="$COMPOSE_FILES -f docker-compose.cpu.yml" ;;
esac

# Build Python API
echo -e "${CYAN}Construyendo Python API...${NC}"
BUILD_ARGS="compose $COMPOSE_FILES build"
if [ "$NO_CACHE_API" = true ]; then
    BUILD_ARGS="$BUILD_ARGS --no-cache"
fi
BUILD_ARGS="$BUILD_ARGS --build-arg GPU_TYPE=$GPU_TYPE python-api"

docker $BUILD_ARGS
echo -e "${GREEN}✓ Python API construida${NC}"
echo ""

# Build Java App
echo -e "${CYAN}Construyendo Java App...${NC}"
BUILD_ARGS="compose $COMPOSE_FILES build"
if [ "$NO_CACHE_APP" = true ]; then
    BUILD_ARGS="$BUILD_ARGS --no-cache"
fi
BUILD_ARGS="$BUILD_ARGS java-app"

docker $BUILD_ARGS
echo -e "${GREEN}✓ Java App construida${NC}"
echo ""

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}   Build Completado Exitosamente${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo -e "${CYAN}Para iniciar los servicios:${NC}"
echo -e "  ${NC}./scripts/start.sh${NC}"
echo ""
echo -e "${CYAN}O manualmente:${NC}"
echo -e "  ${NC}docker compose $COMPOSE_FILES up -d${NC}"
