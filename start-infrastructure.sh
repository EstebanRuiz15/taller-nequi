#!/bin/bash

# Script para levantar la infraestructura con Docker Compose
echo "🚀 Iniciando servicios de infraestructura..."

# Verificar si Docker está corriendo
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker no está corriendo. Por favor inicia Docker Desktop."
    exit 1
fi

# Verificar si AWS CLI está instalado
if ! command -v aws &> /dev/null; then
    echo "⚠️  AWS CLI no está instalado. Instalando via brew..."
    if command -v brew &> /dev/null; then
        brew install awscli
    else
        echo "❌ Brew no está disponible. Por favor instala AWS CLI manualmente."
        echo "   Instrucciones: https://docs.aws.amazon.com/cli/latest/userguide/install-cliv2.html"
        exit 1
    fi
fi

# Bajar imágenes necesarias
echo "📥 Descargando imágenes de Docker..."
docker pull postgres:15-alpine
docker pull redis:7-alpine
docker pull localstack/localstack:latest

# Crear la red si no existe
docker network create taller-network 2>/dev/null || true

# Levantar los servicios
echo "🏗️  Levantando servicios..."
docker-compose up -d

# Esperar a que PostgreSQL esté listo
echo "⏳ Esperando a que PostgreSQL esté listo..."
until docker exec taller-nequi-postgres pg_isready -U nequi_user -d tallerdb; do
  echo "PostgreSQL no está listo aún... esperando..."
  sleep 2
done

echo "✅ PostgreSQL está listo!"

# Esperar a que Redis esté listo
echo "⏳ Esperando a que Redis esté listo..."
until docker exec taller-nequi-redis redis-cli ping | grep -q PONG; do
  echo "Redis no está listo aún... esperando..."
  sleep 2
done

echo "✅ Redis está listo!"

# Esperar a que LocalStack esté listo
echo "⏳ Esperando a que LocalStack esté listo..."
timeout=120 
elapsed=0
while ! curl -s http://localhost:4566/_localstack/health > /dev/null 2>&1; do
  if [ $elapsed -ge $timeout ]; then
    echo "❌ LocalStack tardó demasiado en estar listo. Verificando logs..."
    docker logs taller-nequi-localstack --tail 10
    echo "⚠️  Continuando sin LocalStack..."
    break
  fi
  echo "LocalStack no está listo aún... esperando... ($elapsed/$timeout segundos)"
  sleep 5
  elapsed=$((elapsed + 5))
done

if curl -s http://localhost:4566/_localstack/health > /dev/null 2>&1; then
  echo "✅ LocalStack está listo!"
else
  echo "⚠️  LocalStack no respondió, pero continuamos..."
fi

# Configurar LocalStack - SQS
echo "🔧 Configurando SQS en LocalStack..."
aws --endpoint-url=http://localhost:4566 --no-cli-pager sqs create-queue --queue-name tallerNequiQueue --region us-east-1 || echo "Queue ya existe"

# Configurar LocalStack - DynamoDB
echo "🔧 Configurando DynamoDB en LocalStack..."
aws --endpoint-url=http://localhost:4566 --no-cli-pager dynamodb create-table \
  --table-name tallerNequiUsers \
  --attribute-definitions AttributeName=id,AttributeType=N \
  --key-schema AttributeName=id,KeyType=HASH \
  --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1 \
  --region us-east-1 || echo "Tabla ya existe"

# Mostrar el estado de los servicios
echo "📊 Estado de los servicios:"
docker-compose ps

echo ""
echo "🎉 ¡Infraestructura lista!"
echo "📍 Servicios disponibles:"
echo "   • PostgreSQL: localhost:5432"
echo "   • Redis: localhost:6379"
echo "   • LocalStack (SQS/DynamoDB): localhost:4566"
echo ""
echo "🔗 Conexión a PostgreSQL:"
echo "   Host: localhost"
echo "   Port: 5432"
echo "   Database: tallerdb"
echo "   Username: nequi_user"
echo "   Password: nequi_password"
echo "   Schema: public"
echo ""
echo "🔗 Servicios de LocalStack:"
echo "   SQS Queue: tallerNequiQueue"
echo "   DynamoDB Table: tallerNequiUsers"
echo "   Endpoint: http://localhost:4566"
echo ""
echo "🛠️  Comandos útiles:"
echo "   Ver colas SQS: aws --endpoint-url=http://localhost:4566 --no-cli-pager sqs list-queues"
echo "   Ver tablas DynamoDB: aws --endpoint-url=http://localhost:4566 --no-cli-pager dynamodb list-tables"
echo "   Conectar a PostgreSQL: psql -h localhost -p 5432 -U nequi_user -d tallerdb"
