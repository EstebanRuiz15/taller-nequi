#!/bin/bash

# Script para verificar el estado de la infraestructura
echo "🔍 Verificando estado de la infraestructura..."

# Verificar Docker Compose
echo ""
echo "📊 Estado de contenedores:"
docker-compose ps

echo ""
echo "🔧 Verificando conectividad de servicios:"

# Verificar PostgreSQL
echo -n "PostgreSQL: "
if docker exec taller-nequi-postgres pg_isready -U nequi_user -d tallerdb >/dev/null 2>&1; then
    echo "✅ Activo"
else
    echo "❌ No disponible"
fi

# Verificar Redis
echo -n "Redis: "
if docker exec taller-nequi-redis redis-cli ping | grep -q PONG 2>/dev/null; then
    echo "✅ Activo"
else
    echo "❌ No disponible"
fi

# Verificar LocalStack
echo -n "LocalStack: "
if curl -s http://localhost:4566/_localstack/health >/dev/null 2>&1; then
    echo "✅ Activo"
else
    echo "❌ No disponible"
fi

# Verificar SQS
echo -n "SQS Queue: "
if aws --endpoint-url=http://localhost:4566 sqs list-queues 2>/dev/null | grep -q tallerNequiQueue; then
    echo "✅ tallerNequiQueue existe"
else
    echo "❌ tallerNequiQueue no encontrada"
fi

# Verificar DynamoDB
echo -n "DynamoDB Table: "
if aws --endpoint-url=http://localhost:4566 dynamodb list-tables 2>/dev/null | grep -q tallerNequiUsers; then
    echo "✅ tallerNequiUsers existe"
else
    echo "❌ tallerNequiUsers no encontrada"
fi

echo ""
echo "📋 Información de conexión:"
echo "   PostgreSQL: localhost:5432 (nequi_user/nequi_password)"
echo "   Redis: localhost:6379"
echo "   LocalStack: localhost:4566"

echo ""
echo "🛠️  Comandos de prueba rápida:"
echo "   Conectar a PostgreSQL: psql -h localhost -p 5432 -U nequi_user -d tallerdb"
echo "   Conectar a Redis: redis-cli -h localhost -p 6379"
echo "   Ver colas SQS: aws --endpoint-url=http://localhost:4566 sqs list-queues"
echo "   Ver tablas DynamoDB: aws --endpoint-url=http://localhost:4566 dynamodb list-tables"
