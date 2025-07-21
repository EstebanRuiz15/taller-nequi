package co.com.nequi.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import io.swagger.v3.oas.models.parameters.PathParameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("🏗️ Taller Nequi - Clean Architecture API")
                .description("""
                    **API REST reactiva** construida con **Spring Boot WebFlux** que implementa los principios de **Clean Architecture**.
                    
                    ### ✨ Funcionalidades Principales:
                    - **Gestión de Usuarios**: CRUD completo de usuarios con validaciones
                    - **Cache Inteligente**: Sistema de cache con Redis para optimizar consultas  
                    - **Mensajería Asíncrona**: Envío de eventos via SQS cuando se crean usuarios
                    - **Servicios Externos**: Integración con APIs externas para obtener datos de usuarios
                    - **Manejo de Errores**: Sistema robusto de manejo de excepciones con respuestas estructuradas
                    
                    ### 🛠️ Stack Tecnológico:
                    - **Framework**: Spring Boot 3 + WebFlux (Reactivo)
                    - **Base de Datos**: PostgreSQL con R2DBC
                    - **Cache**: Redis
                    - **Mensajería**: AWS SQS (LocalStack)
                    - **NoSQL**: DynamoDB (LocalStack)
                    
                    ### 🚀 Cómo Probar la API:
                    1. Asegúrate de que la infraestructura esté corriendo: `./start-infrastructure.sh`
                    2. Ejecuta la aplicación: `./gradlew bootRun`
                    3. Usa los endpoints documentados abajo
                    """)
                .version("1.0.0")
                .contact(new Contact()
                    .name("Equipo Taller Nequi")
                    .email("taller@nequi.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("🖥️ Servidor Local de Desarrollo")))
            .tags(List.of(
                new Tag()
                    .name("👥 Gestión de Usuarios")
                    .description("API para gestión completa de usuarios con cache inteligente y eventos asíncronos")))
            .paths(createPaths());
    }

    private io.swagger.v3.oas.models.Paths createPaths() {
        io.swagger.v3.oas.models.Paths paths = new io.swagger.v3.oas.models.Paths();
        
        // POST /users - Crear Usuario
        paths.addPathItem("/users", new PathItem()
            .post(new Operation()
                .tags(List.of("👥 Gestión de Usuarios"))
                .summary("🆕 Crear Usuario")
                .description("""
                    **Crea un nuevo usuario** obteniendo los datos desde un servicio externo.
                    
                    ### 🔄 Flujo de Ejecución:
                    1. 🔍 Verifica si el usuario ya existe en la base de datos
                    2. 🌐 Si no existe, consulta los datos del servicio externo
                    3. 💾 Guarda el usuario en PostgreSQL
                    4. 📨 Envía evento de creación a SQS
                    5. 🗂️ Actualiza el cache en Redis
                    
                    ### ⚠️ Validaciones:
                    - El ID debe ser un número entero positivo
                    - El usuario no debe existir previamente
                    - El servicio externo debe retornar datos válidos
                    
                    ### 📝 Ejemplo de uso:
                    ```bash
                    curl -X POST "http://localhost:8080/users?id=1"
                    ```
                    """)
                .operationId("createUser")
                .addParametersItem(new QueryParameter()
                    .name("id")
                    .description("🔢 **ID único del usuario** a crear. Debe ser un número entero positivo.")
                    .required(true)
                    .schema(new Schema<>().type("integer").minimum(BigDecimal.valueOf(1)).example(1)))
                .responses(new ApiResponses()
                    .addApiResponse("200", new ApiResponse()
                        .description("✅ Usuario creado exitosamente")
                        .content(new Content()
                            .addMediaType("application/json", new MediaType()
                                .schema(createUserSchema()))))
                    .addApiResponse("400", new ApiResponse()
                        .description("❌ Solicitud inválida - ID no válido o faltante")
                        .content(new Content()
                            .addMediaType("application/json", new MediaType()
                                .schema(createErrorSchema())
                                .example("""
                                    {
                                        "error": "VALIDATION_ERROR",
                                        "message": "El id debe ser un número positivo",
                                        "timestamp": "2025-07-21T18:00:00Z",
                                        "path": "/users"
                                    }
                                    """))))
                    .addApiResponse("409", new ApiResponse()
                        .description("🔄 Conflicto - El usuario ya existe")
                        .content(new Content()
                            .addMediaType("application/json", new MediaType()
                                .schema(createErrorSchema())
                                .example("""
                                    {
                                        "error": "USER_CONFLICT",
                                        "message": "El usuario ya existe",
                                        "timestamp": "2025-07-21T18:00:00Z",
                                        "path": "/users"
                                    }
                                    """))))
                    .addApiResponse("500", new ApiResponse()
                        .description("💥 Error interno del servidor")
                        .content(new Content()
                            .addMediaType("application/json", new MediaType()
                                .schema(createErrorSchema()))))))
            .get(new Operation()
                .tags(List.of("👥 Gestión de Usuarios"))
                .summary("📋 Listar Todos los Usuarios")
                .description("""
                    **Obtiene la lista completa** de todos los usuarios registrados.
                    
                    ### 📊 Información Retornada:
                    - Lista de todos los usuarios en formato JSON
                    - Datos completos: ID, nombre, apellido, email
                    - Sin paginación (para propósitos del taller)
                    
                    ### ⚡ Rendimiento:
                    - Consulta directa a PostgreSQL
                    - Sin cache (datos siempre actualizados)
                    
                    ### 📝 Ejemplo de uso:
                    ```bash
                    curl "http://localhost:8080/users"
                    ```
                    """)
                .operationId("getAllUsers")
                .responses(new ApiResponses()
                    .addApiResponse("200", new ApiResponse()
                        .description("✅ Lista de usuarios obtenida exitosamente")
                        .content(new Content()
                            .addMediaType("application/json", new MediaType()
                                .schema(new ArraySchema().items(createUserSchema()))
                                .example("""
                                    [
                                        {
                                            "id": 1,
                                            "firstName": "John",
                                            "lastName": "Doe",
                                            "email": "john.doe@example.com"
                                        },
                                        {
                                            "id": 2,
                                            "firstName": "Jane",
                                            "lastName": "Smith",
                                            "email": "jane.smith@example.com"
                                        }
                                    ]
                                    """))))
                    .addApiResponse("500", new ApiResponse()
                        .description("💥 Error interno del servidor")
                        .content(new Content()
                            .addMediaType("application/json", new MediaType()
                                .schema(createErrorSchema())))))));

        // GET /users/{id} - Obtener Usuario por ID
        paths.addPathItem("/users/{id}", new PathItem()
            .get(new Operation()
                .tags(List.of("👥 Gestión de Usuarios"))
                .summary("👤 Obtener Usuario por ID")
                .description("""
                    **Obtiene un usuario específico** utilizando estrategia de cache inteligente.
                    
                    ### 🚀 Estrategia de Cache:
                    1. 🗂️ Primero busca en **Redis** (cache)
                    2. 💾 Si no está en cache, consulta **PostgreSQL**
                    3. 🔄 Guarda el resultado en cache con TTL de 5 minutos
                    
                    ### ⚡ Optimización:
                    - **Cache Hit**: Respuesta en ~1ms
                    - **Cache Miss**: Respuesta en ~50ms + actualización de cache
                    
                    ### 📝 Ejemplo de uso:
                    ```bash
                    curl "http://localhost:8080/users/1"
                    ```
                    """)
                .operationId("getUserById")
                .addParametersItem(new PathParameter()
                    .name("id")
                    .description("🔢 **ID único del usuario** a obtener")
                    .required(true)
                    .schema(new Schema<>().type("integer").example(1)))
                .responses(new ApiResponses()
                    .addApiResponse("200", new ApiResponse()
                        .description("✅ Usuario encontrado exitosamente")
                        .content(new Content()
                            .addMediaType("application/json", new MediaType()
                                .schema(createUserSchema()))))
                    .addApiResponse("400", new ApiResponse()
                        .description("❌ ID inválido - Debe ser un número entero")
                        .content(new Content()
                            .addMediaType("application/json", new MediaType()
                                .schema(createErrorSchema()))))
                    .addApiResponse("404", new ApiResponse()
                        .description("🔍 Usuario no encontrado")
                        .content(new Content()
                            .addMediaType("application/json", new MediaType()
                                .schema(createErrorSchema())
                                .example("""
                                    {
                                        "error": "USER_NOT_FOUND",
                                        "message": "El usuario no existe",
                                        "timestamp": "2025-07-21T18:00:00Z",
                                        "path": "/users/999"
                                    }
                                    """)))))));

        // GET /users/search - Buscar Usuarios por Nombre
        paths.addPathItem("/users/search", new PathItem()
            .get(new Operation()
                .tags(List.of("👥 Gestión de Usuarios"))
                .summary("🔍 Buscar Usuarios por Nombre")
                .description("""
                    **Busca usuarios por nombre** utilizando cache inteligente para optimizar búsquedas frecuentes.
                    
                    ### 🎯 Funcionalidad:
                    - Búsqueda **case-insensitive** por nombre
                    - Coincidencia parcial en nombre y apellido
                    - Cache de resultados por 5 minutos
                    
                    ### 🚀 Estrategia de Cache:
                    1. 🗂️ Busca en cache: `"user:name:{nombre}"`
                    2. 💾 Si no está, consulta PostgreSQL con LIKE
                    3. 🔄 Guarda resultados en cache
                    
                    ### 📝 Ejemplos de Búsqueda:
                    - `"john"` → encuentra "John Doe", "Johnny Smith"
                    - `"doe"` → encuentra "John Doe", "Jane Doe"
                    
                    ### 📝 Ejemplo de uso:
                    ```bash
                    curl "http://localhost:8080/users/search?name=john"
                    ```
                    """)
                .operationId("searchUsersByName")
                .addParametersItem(new QueryParameter()
                    .name("name")
                    .description("🔤 **Nombre a buscar**. Búsqueda case-insensitive que coincide con nombre y apellido.")
                    .required(true)
                    .schema(new Schema<>().type("string").example("john")))
                .responses(new ApiResponses()
                    .addApiResponse("200", new ApiResponse()
                        .description("✅ Búsqueda completada exitosamente")
                        .content(new Content()
                            .addMediaType("application/json", new MediaType()
                                .schema(new ArraySchema().items(createUserSchema()))
                                .example("""
                                    [
                                        {
                                            "id": 1,
                                            "firstName": "John",
                                            "lastName": "Doe",
                                            "email": "john.doe@example.com"
                                        }
                                    ]
                                    """))))
                    .addApiResponse("400", new ApiResponse()
                        .description("❌ Parámetro de búsqueda inválido")
                        .content(new Content()
                            .addMediaType("application/json", new MediaType()
                                .schema(createErrorSchema()))))
                    .addApiResponse("404", new ApiResponse()
                        .description("🔍 No se encontraron usuarios")
                        .content(new Content()
                            .addMediaType("application/json", new MediaType()
                                .schema(createErrorSchema())
                                .example("""
                                    {
                                        "error": "USER_NOT_FOUND",
                                        "message": "No se encontraron usuarios con ese nombre",
                                        "timestamp": "2025-07-21T18:00:00Z",
                                        "path": "/users/search"
                                    }
                                    """)))))));

        return paths;
    }

    private Schema<?> createUserSchema() {
        return new Schema<>()
            .type("object")
            .description("👤 **Modelo de Usuario** - Representa un usuario del sistema con información básica de contacto")
            .addProperty("id", new Schema<>()
                .type("integer")
                .description("🔢 **Identificador único** del usuario")
                .example(1)
                .minimum(BigDecimal.valueOf(1)))
            .addProperty("firstName", new Schema<>()
                .type("string")
                .description("📝 **Nombre** del usuario")
                .example("John")
                .minLength(1)
                .maxLength(50))
            .addProperty("lastName", new Schema<>()
                .type("string")
                .description("📝 **Apellido** del usuario")
                .example("Doe")
                .minLength(1)
                .maxLength(50))
            .addProperty("email", new Schema<>()
                .type("string")
                .format("email")
                .description("📧 **Correo electrónico** del usuario")
                .example("john.doe@example.com"))
            .example("""
                {
                    "id": 1,
                    "firstName": "John",
                    "lastName": "Doe", 
                    "email": "john.doe@example.com"
                }
                """);
    }

    private Schema<?> createErrorSchema() {
        return new Schema<>()
            .type("object")
            .description("⚠️ **Modelo de Error** - Respuesta estándar para errores de la API")
            .addProperty("error", new Schema<>()
                .type("string")
                .description("🏷️ **Código de error** específico")
                .example("VALIDATION_ERROR"))
            .addProperty("message", new Schema<>()
                .type("string")
                .description("📝 **Mensaje descriptivo** del error")
                .example("El id debe ser un número positivo"))
            .addProperty("timestamp", new Schema<>()
                .type("string")
                .format("date-time")
                .description("⏰ **Timestamp** de cuándo ocurrió el error")
                .example("2025-07-21T18:00:00Z"))
            .addProperty("path", new Schema<>()
                .type("string")
                .description("🛣️ **Ruta** donde ocurrió el error")
                .example("/users"));
    }
}
