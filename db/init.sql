-- ============================================================
-- Script de Inicialización de Base de Datos
-- Sistema SACP - PostgreSQL
-- ============================================================

\c fish_quality_db;

-- Verificar versión
SELECT version();

-- Extensiones útiles
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- Configuración de performance
ALTER SYSTEM SET shared_buffers = '256MB';
ALTER SYSTEM SET effective_cache_size = '1GB';
ALTER SYSTEM SET maintenance_work_mem = '64MB';
ALTER SYSTEM SET checkpoint_completion_target = 0.9;
ALTER SYSTEM SET wal_buffers = '16MB';
ALTER SYSTEM SET default_statistics_target = 100;
ALTER SYSTEM SET random_page_cost = 1.1;
ALTER SYSTEM SET effective_io_concurrency = 200;
ALTER SYSTEM SET work_mem = '16MB';
ALTER SYSTEM SET min_wal_size = '1GB';
ALTER SYSTEM SET max_wal_size = '4GB';

-- Pool de conexiones
ALTER SYSTEM SET max_connections = 100;

COMMENT ON DATABASE fish_quality_db IS 'Sistema de Análisis de Calidad de Pescado';

-- Reiniciar para aplicar cambios (requiere permisos de superusuario)
-- SELECT pg_reload_conf();
