# MSA Court Reservation - Authentication Service

## Descripción
Servicio de autenticación y gestión de usuarios para el sistema de reserva de canchas. Incluye registro, login, gestión de roles (ADMIN / USUARIO_FINAL) y control de acceso mediante JWT.

## Características
- ✅ Registro de usuarios con validación de email
- ✅ Autenticación con JWT
- ✅ Gestión de usuarios (CRUD)
- ✅ Sistema de roles: ADMIN y USUARIO_FINAL
- ✅ Encriptación de contraseñas con BCrypt
- ✅ Persistencia en PostgreSQL con JPA/Hibernate
- ✅ Documentación con Swagger/OpenAPI

## Acceso a Swagger UI

Una vez que la aplicación está corriendo, accede a:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

Aquí puedes ver toda la documentación interactiva de los endpoints y probarlos directamente.

## Endpoints

### 1. Registro de Usuario
**POST** `/api/auth/register`

**Request:**
```json
{
  "userName": "Juan Pérez",
  "userMail": "juan@example.com",
  "userPassword": "password123",
  "userRole": "USUARIO_FINAL"
}
```

**Response (201 Created):**
```json
{
  "userId": 1,
  "userName": "Juan Pérez",
  "userMail": "juan@example.com",
  "userRole": "USUARIO_FINAL"
}
```

### 2. Login
**POST** `/api/auth/login`

**Request:**
```json
{
  "userMail": "juan@example.com",
  "userPassword": "password123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "userId": 1,
  "userName": "Juan Pérez",
  "userMail": "juan@example.com",
  "userRole": "USUARIO_FINAL"
}
```

### 3. Obtener Usuario por ID
**GET** `/api/auth/users/{userId}`

**Response (200 OK):**
```json
{
  "userId": 1,
  "userName": "Juan Pérez",
  "userMail": "juan@example.com",
  "userRole": "USUARIO_FINAL"
}
```

### 4. Obtener Todos los Usuarios
**GET** `/api/auth/users`

**Response (200 OK):**
```json
[
  {
    "userId": 1,
    "userName": "Juan Pérez",
    "userMail": "juan@example.com",
    "userRole": "USUARIO_FINAL"
  },
  {
    "userId": 2,
    "userName": "Admin User",
    "userMail": "admin@example.com",
    "userRole": "ADMIN"
  }
]
```

### 5. Actualizar Usuario
**PUT** `/api/auth/users/{userId}`

**Request:**
```json
{
  "userName": "Juan Carlos Pérez",
  "userPassword": "newPassword123"
}
```

**Response (200 OK):**
```json
{
  "userId": 1,
  "userName": "Juan Carlos Pérez",
  "userMail": "juan@example.com",
  "userRole": "USUARIO_FINAL"
}
```

### 6. Eliminar Usuario
**DELETE** `/api/auth/users/{userId}`

**Response (204 No Content)**

### 7. Cambiar Rol de Usuario
**PUT** `/api/auth/users/{userId}/role?newRole=ADMIN`

**Response (200 OK):**
```json
{
  "userId": 1,
  "userName": "Juan Pérez",
  "userMail": "juan@example.com",
  "userRole": "ADMIN"
}
```

## Configuración

### Variables de Entorno (application.yml)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/court_reservation
    username: postgres
    password: postgres

jwt:
  secret: your-secret-key-change-this-in-production
  expiration: 86400000  # 24 horas en ms
```

## Autenticación

Los endpoints protegidos requieren el token JWT en el header:
```
Authorization: Bearer <token>
```

## Roles Disponibles
- **ADMIN**: Acceso completo para gestionar usuarios y roles
- **USUARIO_FINAL**: Usuario con permisos limitados

## Estructura del Proyecto
```
src/main/java/com/courtreservation/authentication/
├── controller/     # UsuariosController - Endpoints REST
├── service/        # UsuariosService - Lógica de negocio
├── model/          # User - Entidad JPA
├── repository/     # UserRepository - Acceso a datos
├── dto/            # DTOs (Request/Response)
├── security/       # JwtTokenProvider, SecurityConfig
```

## Dependencias Clave
- Spring Boot 4.1.0
- Spring Data JPA
- Spring Security
- JWT (JJWT 0.12.3)
- PostgreSQL Driver
- Lombok
