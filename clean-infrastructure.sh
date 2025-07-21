#!/bin/bash

# Script para limpiar completamente la infraestructura
echo "🧹 Limpiando infraestructura..."

# Preguntar confirmación
read -p "¿Estás seguro de que quieres eliminar TODOS los datos? (y/N): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "❌ Operación cancelada"
    exit 1
fi

echo "🛑 Deteniendo y eliminando contenedores..."
docker-compose down

echo "🗑️  Eliminando volúmenes (datos)..."
docker-compose down -v

echo "🧽 Eliminando imágenes locales (opcional)..."
read -p "¿Quieres eliminar también las imágenes Docker descargadas? (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    docker rmi postgres:15-alpine redis:7-alpine localstack/localstack:latest 2>/dev/null || true
fi

echo "🌐 Eliminando red..."
docker network rm taller-network 2>/dev/null || true

echo "✅ Limpieza completada."
echo ""
echo "💡 Para volver a iniciar la infraestructura, ejecuta:"
echo "   ./start-infrastructure.sh"
