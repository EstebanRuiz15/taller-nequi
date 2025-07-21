#!/bin/bash

# Script para detener la infraestructura
echo "🛑 Deteniendo servicios de infraestructura..."

# Mostrar estado actual antes de detener
echo "📊 Estado actual de los servicios:"
docker-compose ps

echo ""
echo "🔄 Deteniendo contenedores..."
docker-compose stop

echo "✅ Servicios detenidos."
echo ""
echo "� Los datos se han preservado en volúmenes Docker."
echo ""
echo "�💡 Opciones disponibles:"
echo "   Para reiniciar:           ./start-infrastructure.sh"
echo "   Para verificar estado:    ./check-infrastructure.sh"
echo "   Para limpiar todo:        ./clean-infrastructure.sh"
echo "   Para eliminar contenedores solamente: docker-compose down"
echo "   Para eliminar datos también:          docker-compose down -v"
