# Makefile para Sistema SACP
# Comandos simplificados para gestión del proyecto

.PHONY: help build start stop restart logs clean test dev prod

# Variables
GPU_TYPE ?= auto
COMPOSE_FILES = -f docker-compose.yml

# Detectar GPU y añadir override
ifeq ($(GPU_TYPE),nvidia)
	COMPOSE_FILES += -f docker-compose.nvidia.yml
else ifeq ($(GPU_TYPE),amd)
	COMPOSE_FILES += -f docker-compose.amd.yml
else ifeq ($(GPU_TYPE),cpu)
	COMPOSE_FILES += -f docker-compose.cpu.yml
endif

# ============================================================
# Ayuda
# ============================================================
help:
	@echo "Sistema de Análisis de Calidad de Pescado (SACP)"
	@echo ""
	@echo "Comandos disponibles:"
	@echo "  make build          - Construir imágenes Docker"
	@echo "  make start          - Iniciar servicios"
	@echo "  make stop           - Detener servicios"
	@echo "  make restart        - Reiniciar servicios"
	@echo "  make logs           - Ver logs en tiempo real"
	@echo "  make clean          - Limpiar contenedores y volúmenes"
	@echo "  make test           - Ejecutar tests"
	@echo "  make dev            - Modo desarrollo (con pgAdmin)"
	@echo "  make prod           - Modo producción"
	@echo ""
	@echo "Variables:"
	@echo "  GPU_TYPE=nvidia     - Usar GPU NVIDIA"
	@echo "  GPU_TYPE=amd        - Usar GPU AMD"
	@echo "  GPU_TYPE=cpu        - Solo CPU"
	@echo ""
	@echo "Ejemplos:"
	@echo "  make build GPU_TYPE=nvidia"
	@echo "  make start GPU_TYPE=amd"
	@echo "  make dev"

# ============================================================
# Construcción
# ============================================================
build:
	@echo "Construyendo imágenes Docker (GPU: $(GPU_TYPE))..."
	docker compose $(COMPOSE_FILES) build --build-arg GPU_TYPE=$(GPU_TYPE)

build-no-cache:
	@echo "Construyendo sin cache..."
	docker compose $(COMPOSE_FILES) build --no-cache --build-arg GPU_TYPE=$(GPU_TYPE)

# ============================================================
# Gestión de servicios
# ============================================================
start:
	@echo "Iniciando servicios..."
	docker compose $(COMPOSE_FILES) up -d
	@echo "Esperando a que servicios estén listos..."
	@sleep 5
	@make status

stop:
	@echo "Deteniendo servicios..."
	docker compose $(COMPOSE_FILES) down

restart:
	@echo "Reiniciando servicios..."
	docker compose $(COMPOSE_FILES) restart

status:
	@echo "Estado de servicios:"
	docker compose $(COMPOSE_FILES) ps

# ============================================================
# Logs
# ============================================================
logs:
	docker compose $(COMPOSE_FILES) logs -f

logs-api:
	docker compose $(COMPOSE_FILES) logs -f python-api

logs-app:
	docker compose $(COMPOSE_FILES) logs -f java-app

logs-db:
	docker compose $(COMPOSE_FILES) logs -f postgres

# ============================================================
# Desarrollo
# ============================================================
dev:
	@echo "Iniciando en modo desarrollo..."
	docker compose $(COMPOSE_FILES) --profile dev up -d
	@echo ""
	@echo "Servicios disponibles:"
	@echo "  - Python API: http://localhost:8001"
	@echo "  - pgAdmin: http://localhost:5050"
	@make status

shell-api:
	docker exec -it sacp-python-api bash

shell-app:
	docker exec -it sacp-java-app bash

shell-db:
	docker exec -it sacp-postgres psql -U postgres -d fish_quality_db

# ============================================================
# Testing
# ============================================================
test:
	@echo "Ejecutando tests..."
	docker compose $(COMPOSE_FILES) exec python-api pytest -v

test-api:
	@echo "Test de conectividad API..."
	@curl -f http://localhost:8001/health/ || echo "API no disponible"

# ============================================================
# Limpieza
# ============================================================
clean:
	@echo "Limpiando contenedores y redes..."
	docker compose $(COMPOSE_FILES) down

clean-all:
	@echo "⚠️  ADVERTENCIA: Esto eliminará TODOS los datos persistentes"
	@read -p "¿Continuar? [y/N] " confirm && [ "$$confirm" = "y" ]
	docker compose $(COMPOSE_FILES) down -v
	docker system prune -f

clean-images:
	@echo "Eliminando imágenes..."
	docker compose $(COMPOSE_FILES) down --rmi local

# ============================================================
# Producción
# ============================================================
prod:
	@echo "Iniciando en modo producción..."
	docker compose $(COMPOSE_FILES) up -d
	@make status

prod-logs:
	docker compose $(COMPOSE_FILES) logs -f --tail=100

# ============================================================
# Utilidades
# ============================================================
health:
	@echo "Verificando salud de servicios..."
	@curl -s http://localhost:8001/health/ | jq . || echo "❌ API no responde"
	@docker exec sacp-postgres pg_isready && echo "✅ PostgreSQL OK" || echo "❌ PostgreSQL no responde"

stats:
	docker stats --no-stream

backup-db:
	@echo "Creando backup de base de datos..."
	@mkdir -p backups
	docker exec sacp-postgres pg_dump -U postgres fish_quality_db > backups/backup_$$(date +%Y%m%d_%H%M%S).sql
	@echo "Backup creado en backups/"

restore-db:
	@echo "Restaurar base de datos..."
	@read -p "Archivo de backup: " file && \
	docker exec -i sacp-postgres psql -U postgres fish_quality_db < $$file

# ============================================================
# Setup inicial
# ============================================================
init:
	@echo "Configuración inicial del proyecto..."
	@if [ ! -f .env ]; then \
		cp .env.example .env; \
		echo "✅ Archivo .env creado"; \
		echo "⚠️  EDITA .env antes de continuar"; \
	else \
		echo "✅ .env ya existe"; \
	fi
	@mkdir -p python/temp
	@mkdir -p db
	@echo "✅ Directorios creados"
	@echo ""
	@echo "Siguiente paso: make build"
