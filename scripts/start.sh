#!/bin/bash
# ============================================================
# Script de Inicio para Linux/macOS
# ============================================================

set -e

GPU_TYPE="auto"
DEV=false
BUILD=false
LOGS=false

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

usage() {
    echo "Start Script para Sistema SACP"
    echo ""
    echo "OPCIONES:"
    echo "  -g, --gpu-type <tipo>   Tipo de GPU: auto, nvidia, amd, cpu (default: auto)"
    echo "  -d, --dev               Modo desarrollo (incluye pgAdmin)"
    echo "  -b, --build             Reconstruir antes de iniciar"
    echo "  -l, --logs              Mostrar logs después de iniciar"
    echo "  -h, --help              Mostrar esta ayuda"
    exit 0
}

while [[ $# -gt 0 ]]; do
    case $1 in
        -g|--gpu-type) GPU_TYPE="$2"; shift 2 ;;
        -d|--dev) DEV=true; shift ;;
        -b|--build) BUILD=true; shift ;;
        -l|--logs) LOGS=true; shift ;;
        -h|--help) usage ;;
        *) echo "Opción desconocida: $1"; usage ;;
    esac
done

echo -e "${CYAN}========================================${NC}"
echo -e "${CYAN}   Iniciando Sistema SACP${NC}"
echo -e "${CYAN}========================================${NC}"
echo ""

# Detectar GPU
if [ "$GPU_TYPE" = "auto" ]; then
    echo -e "${YELLOW}Detectando GPU...${NC}"
    if [ -f "scripts/build.sh" ]; then
        GPU_TYPE=$(./scripts/build.sh -n 2>&1 | grep "seleccionado:" | awk '{print $NF}')
        [ -z "$GPU_TYPE" ] && GPU_TYPE="cpu"
    else
        GPU_TYPE="cpu"
    fi
fi

echo -e "${CYAN}GPU Type: $GPU_TYPE${NC}"
echo ""

COMPOSE_FILES="-f docker-compose.yml"
case $GPU_TYPE in
    nvidia) COMPOSE_FILES="$COMPOSE_FILES -f docker-compose.nvidia.yml" ;;
    amd)    COMPOSE_FILES="$COMPOSE_FILES -f docker-compose.amd.yml" ;;
    cpu)    COMPOSE_FILES="$COMPOSE_FILES -f docker-compose.cpu.yml" ;;
esac

if [ "$BUILD" = true ]; then
    echo -e "${YELLOW}Reconstruyendo imágenes...${NC}"
    docker compose $COMPOSE_FILES build
    echo ""
fi

PROFILE=""
if [ "$DEV" = true ]; then
    PROFILE="--profile dev"
    echo -e "${YELLOW}Modo desarrollo activado (incluye pgAdmin)${NC}"
fi

echo -e "${CYAN}Iniciando contenedores...${NC}"
docker compose $COMPOSE_FILES up -d $PROFILE

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}   Sistema Iniciado${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

echo -e "${CYAN}Estado de servicios:${NC}"
docker compose $COMPOSE_FILES ps

echo ""
echo -e "${CYAN}URLs de acceso:${NC}"
echo -e "  Python API: http://localhost:8001/health/"
echo -e "  PostgreSQL: localhost:5432"
[ "$DEV" = true ] && echo -e "  pgAdmin:    http://localhost:5050"

echo ""
echo -e "${CYAN}Comandos útiles:${NC}"
echo -e "  Ver logs:     docker compose $COMPOSE_FILES logs -f"
echo -e "  Detener:      docker compose $COMPOSE_FILES down"
echo -e "  Reiniciar:    docker compose $COMPOSE_FILES restart"

if [ "$LOGS" = true ]; then
    echo ""
    echo -e "${YELLOW}Mostrando logs (Ctrl+C para salir)...${NC}"
    docker compose $COMPOSE_FILES logs -f
fi
