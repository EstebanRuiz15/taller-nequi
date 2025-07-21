# 🏗️ Taller Nequi - Clean Architecture API

## 📖 Descripción

**Taller Nequi** es una API REST reactiva construida con **Spring Boot WebFlux** que implementa los principios de **Clean Architecture**. La aplicación gestiona usuarios a través de endpoints RESTful y utiliza múltiples tecnologías para demostrar un stack completo de desarrollo.

### ✨ Funcionalidades Principales

- **Gestión de Usuarios**: CRUD completo de usuarios con validaciones
- **Cache Inteligente**: Sistema de cache con Redis para optimizar consultas
- **Mensajería Asíncrona**: Envío de eventos via SQS cuando se crean usuarios
- **Servicios Externos**: Integración con APIs externas para obtener datos de usuarios
- **Manejo de Errores**: Sistema robusto de manejo de excepciones con respuestas estructuradas

---

## 🏛️ Arquitectura del Proyecto

La aplicación sigue los principios de **Clean Architecture** con una separación clara de responsabilidades:

```
📦 taller-nequi/
├── 🎯 applications/          # Configuración principal
│   └── app-service/          # Aplicación Spring Boot
├── 🏢 domain/               # Lógica de negocio
│   ├── model/               # Entidades y gateways
│   └── usecase/             # Casos de uso y lógica de dominio
├── 🔧 infrastructure/       # Adaptadores e implementaciones
│   ├── driven-adapters/     # Adaptadores externos
│   │   ├── r2dbc-postgresql/ # Base de datos
│   │   ├── redis/           # Cache
│   │   ├── web-client/      # Cliente HTTP
│   │   └── sqs-sender/      # Mensajería
│   └── entry-points/        # Puntos de entrada
│       ├── reactive-web/    # API REST
│       └── sqs-listener/    # Procesador de mensajes
└── 🐳 Infraestructura
    ├── docker-compose.yml   # Servicios contenerizados
    └── scripts/             # Scripts de inicialización
```

### 🌐 Endpoints Disponibles

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/users?id={id}` | Crear usuario obteniendo datos de servicio externo |
| `GET` | `/users/{id}` | Obtener usuario por ID |
| `GET` | `/users` | Listar todos los usuarios |
| `GET` | `/users/search?name={name}` | Buscar usuarios por nombre |

### 🛠️ Stack Tecnológico

- **Framework**: Spring Boot 3 + WebFlux (Reactivo)
- **Base de Datos**: PostgreSQL con R2DBC
- **Cache**: Redis
- **Mensajería**: AWS SQS (LocalStack)
- **NoSQL**: DynamoDB (LocalStack)
- **Containerización**: Docker & Docker Compose

---

## 🚀 Configuración y Ejecución Local

### 📋 Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:

- **Java 17+** ☕
- **Docker Desktop** 🐳
- **AWS CLI** ⚡ (se instala automáticamente via brew si no está presente, pero se debe tener brew instalado)
- **Git** 📦

### 🔧 Pasos para Ejecutar Localmente

### Clonar el repositorio
```bash
git clone https://github.com/EstebanRuiz15/taller-nequi.git
cd taller-nequi
```

#### 🔑 **Dar Permisos de Ejecución a los Scripts** (Solo la primera vez)

```bash
chmod +x *.sh
```

> **Nota**: Los scripts ya incluyen permisos de ejecución, pero si encuentras errores de permisos, ejecuta el comando anterior.

#### 1️⃣ **Levantar la Infraestructura**

```bash
./start-infrastructure.sh
```

**¿Qué hace este script?**
- 📥 Descarga las imágenes Docker necesarias (PostgreSQL, Redis, LocalStack)
- 🚀 Levanta los contenedores con `docker-compose`
- ⏳ Espera a que todos los servicios estén listos
- 🔧 Configura automáticamente SQS y DynamoDB en LocalStack
- ✅ Crea la base de datos `tallerdb` 

**Servicios desplegados:**
- **PostgreSQL**: `localhost:5432` (Base de datos principal)
- **Redis**: `localhost:6379` (Sistema de cache)
- **LocalStack**: `localhost:4566` (SQS + DynamoDB simulados)

#### 2️⃣ **Ejecutar la Aplicación**

```bash
./gradlew bootRun
```

**¿Qué hace este comando?**
- 🔄 Compila la aplicación
- 🌐 Inicia el servidor en `http://localhost:8080`
- 🔗 Se conecta automáticamente a todos los servicios de infraestructura

#### 📚 **Acceder a la Documentación Swagger**

Una vez que la aplicación esté ejecutándose, puedes acceder a la documentación interactiva de la API:

**🌐 Swagger UI:**
```
http://localhost:8080/swagger-ui.html
```

**🎯 Características de Swagger UI:**
- **📖 Documentación completa** de todos los endpoints
- **🧪 Interfaz interactiva** para probar los endpoints
- **💡 Ejemplos de requests y responses**
- **🔧 Validaciones y esquemas** de datos
- **📝 Instrucciones detalladas** de cada operación

**💡 Tip:** Swagger UI se abre automáticamente en tu navegador y te permite probar todos los endpoints directamente desde la interfaz web, si no se abre navega manualmente a la url anterior dada.


#### 3️⃣ **Verificar el Estado** (Opcional)

```bash
./check-infrastructure.sh
```

**¿Qué hace este script?**
- 🔍 Verifica que todos los contenedores estén corriendo
- ✅ Prueba la conectividad a PostgreSQL, Redis y LocalStack
- 📊 Muestra el estado de SQS y DynamoDB

#### 4️⃣ **Detener la Infraestructura**

```bash
./stop-infrastructure.sh
```

**¿Qué hace este script?**
- 🛑 Detiene todos los contenedores de forma ordenada
- 💾 Preserva los datos en volúmenes Docker
- 📝 Muestra opciones para limpiar completamente si es necesario

---

#### 4️⃣ **Eliminar la Infraestructura**

```bash
./clean-infrastructure.sh
```

**¿Qué hace este script?**
- 🛑 Detiene todos los contenedores de forma ordenada
- 💾 Elimina todos los contenedores y su información

---

## 🧪 Testing y Cobertura

El proyecto incluye **17 tests** que validan Entry Points, Adapters e Integración siguiendo Clean Architecture.

### 🚀 **Ejecutar Tests Completos**

```bash
# Ejecutar todos los tests (Entry Points + Adapters + Integración) con cobertura
./gradlew :app-service:test :r2dbc-postgresql:test :reactive-web:test jacocoTestReport
```

### � **Ver Cobertura de Cada Módulo**

```bash
# Entry Points 
open infrastructure/entry-points/reactive-web/build/reports/jacocoHtml/index.html

# Adapters 
open infrastructure/driven-adapters/r2dbc-postgresql/build/reports/jacocoHtml/index.html

# Integration 
open infrastructure/driven-adapters/r2dbc-postgresql/build/reports/jacocoHtml/index.html
```

### 📋 **Resumen**

| Módulo | Tests | Valida |
|--------|--------|--------|
| **reactive-web** | 7 tests | Entry Points (API REST) |
| **r2dbc-postgresql** | 6 tests | Adapters (Base de datos) |
| **app-service** | 4 tests | Integration (End-to-End) |

---
## 🧪 Pruebas de los endpoint de la API

### Crear Usuario
```bash
curl -X POST "http://localhost:8080/users?id=1"
```

### Obtener Usuario por ID
```bash
curl "http://localhost:8080/users/1"
```

### Listar Todos los Usuarios
```bash
curl "http://localhost:8080/users"
```

### Buscar por Nombre
```bash
curl "http://localhost:8080/users/search?name=John"
```

---

## 🔍 Monitoreo y Debugging

### 📊 **Consultar Base de Datos PostgreSQL**

```bash
# Conectar a PostgreSQL
psql -h localhost -p 5432 -U nequi_user -d tallerdb

# Ver todos los usuarios
SELECT * FROM users;

# Salir
\q
```

### 🗂️ **Consultar Cache de Redis**

```bash
# Ver todas las keys en Redis
docker exec -it taller-nequi-redis redis-cli KEYS "*"

# Ver contenido de una key específica (usuario por ID)
docker exec -it brave_mendeleev redis-cli GET "\"user:1\""

# Ver contenido de usuarios por nombre
docker exec -it taller-nequi-redis redis-cli GET "user:name:george"
```

### 📡 **Consultar DynamoDB (LocalStack)**

```bash
# Ver datos en tabla DynamoDB
aws --endpoint-url=http://localhost:4566 dynamodb scan --table-name tallerNequiUsers

# Listar todas las tablas
aws --endpoint-url=http://localhost:4566 dynamodb list-tables
```

### 📨 **Consultar SQS (LocalStack)**

```bash
# Listar colas SQS
aws --endpoint-url=http://localhost:4566 sqs list-queues

# Ver mensajes en cola (si los hay)
aws --endpoint-url=http://localhost:4566 sqs receive-message --queue-url http://localhost:4566/000000000000/tallerNequiQueue
```

---

## 🎯 Flujo de la Aplicación

1. **Crear Usuario**: `POST /users?id=X` 
   - 🔍 Busca en base de datos local
   - 🌐 Si no existe, consulta servicio externo
   - 💾 Guarda en PostgreSQL
   - 📨 Envía evento a SQS
   - 🗂️ Actualiza cache en Redis

2. **Consultar Usuario**: `GET /users/{id}`
   - 🚀 Primero busca en cache (Redis)
   - 💾 Si no está en cache, consulta PostgreSQL
   - 🗂️ Guarda resultado en cache para futuras consultas

3. **Búsqueda por Nombre**: `GET /users/search?name=X`
   - 🗂️ Busca en cache por nombre
   - 💾 Si no está cacheado, consulta base de datos
   - 🚀 Almacena resultados en cache

---

## 🐳 Arquitectura de Contenedores

El proyecto utiliza Docker Compose para orquestar los siguientes servicios:

- **PostgreSQL 15**: Base de datos principal con datos persistentes
- **Redis 7**: Sistema de cache en memoria  
- **LocalStack**: Simulación local de servicios AWS (SQS + DynamoDB)

Todos los datos se persisten en volúmenes Docker, por lo que sobreviven a reinicios de contenedores.

---

## � Troubleshooting

### 🚫 **Error: Permission denied al ejecutar scripts**

Si obtienes un error como `./start-infrastructure.sh: Permission denied`, ejecuta:

```bash
chmod +x *.sh
```

### 🐳 **Error: Docker no está ejecutándose**

Asegúrate de que Docker Desktop esté ejecutándose:
- **macOS**: Abre Docker Desktop desde Applications
- **Windows**: Abre Docker Desktop desde el menú inicio
- **Linux**: `sudo systemctl start docker`

### ⚡ **Error: AWS CLI no encontrado**

El script intentará instalar AWS CLI automáticamente via Homebrew. Si falla:

```bash
# En macOS
brew install awscli

# En otros sistemas, sigue las instrucciones oficiales de AWS
```

### 🔌 **Error de conexión a servicios**

Si los servicios no responden, verifica que estén corriendo:

```bash
docker ps
```

Deberías ver containers con nombres que contengan `postgres`, `redis`, y `localstack`.

---

## �📚 Recursos Adicionales

- **Logs de la aplicación**: Visibles en la consola donde ejecutas `./gradlew bootRun`
- **Health Check**: `http://localhost:8080/actuator/health`
- **Métricas**: `http://localhost:8080/actuator/prometheus`


