# Multi-stage Dockerfile para aplicación JavaFX

# ============================================================
# STAGE 1: Build
# ============================================================
FROM maven:3.9-eclipse-temurin-21 as builder

WORKDIR /build

# Copiar archivos de configuración Maven
COPY pom.xml .
COPY .env* ./

# Descargar dependencias (cacheado)
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar aplicación
RUN mvn clean package -DskipTests -B

# ============================================================
# STAGE 2: Runtime
# ============================================================
FROM eclipse-temurin:21-jre

# Instalar dependencias para JavaFX y X11
RUN apt-get update && apt-get install -y --no-install-recommends \
    libgl1 \
    libgtk-3-0 \
    libxtst6 \
    libxrender1 \
    libxi6 \
    x11-apps \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copiar JAR compilado
COPY --from=builder /build/target/*.jar app.jar

# Copiar recursos
COPY --from=builder /build/target/classes ./classes
COPY .env* ./

# Variables de entorno
ENV PYTHON_API_URL=http://python-api:8001 \
    JAVA_OPTS="-Xmx2g -Xms512m" \
    DISPLAY=:0

# Script de entrada
COPY <<'EOF' /app/entrypoint.sh
#!/bin/bash
set -e

echo "=========================================="
echo "  Sistema de Análisis de Calidad (Java)"
echo "=========================================="

# Esperar a que API Python esté disponible
echo "Esperando API Python en ${PYTHON_API_URL}..."
timeout=60
elapsed=0
until curl -sf "${PYTHON_API_URL}/health/" > /dev/null 2>&1; do
    if [ $elapsed -ge $timeout ]; then
        echo "⚠ TIMEOUT: API Python no disponible después de ${timeout}s"
        echo "Continuando de todas formas..."
        break
    fi
    echo "Reintentando en 2s... ($elapsed/${timeout}s)"
    sleep 2
    elapsed=$((elapsed + 2))
done

echo "✓ API Python disponible"
echo "Iniciando aplicación JavaFX..."

# Ejecutar aplicación
exec java ${JAVA_OPTS} -jar app.jar "$@"
EOF

RUN chmod +x /app/entrypoint.sh

# Para desarrollo: exponer puerto debug
EXPOSE 5005

ENTRYPOINT ["/app/entrypoint.sh"]
