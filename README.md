# Trabajo Práctico Clinica odontológica

## Microservicios y APIS escalables

### Profesor: Jorge Pereyra

### Integrantes: Martin Pardo (0127073) y Juan Figueroa (0134068)

---

## 📋 Descripción del Proyecto

Este proyecto es una API REST desarrollada con Spring Boot para la gestión de una clínica odontológica. El sistema permite administrar pacientes, odontólogos y turnos médicos, proporcionando una solución completa para la gestión de citas y registros de una clínica dental.

La aplicación implementa autenticación mediante JWT (JSON Web Tokens) y sigue las mejores prácticas de arquitectura en capas, separando claramente la lógica de negocio, acceso a datos y presentación.

---

## 🛠️ Tecnologías Utilizadas

- **Spring Boot 3.5.7**: Framework principal para el desarrollo de la aplicación
- **Java 21**: Lenguaje de programación
- **Spring Data JPA**: Para la persistencia de datos
- **Spring Security**: Para la autenticación y autorización
- **JWT (Java Web Tokens)**: Para la autenticación stateless
- **H2 Database**: Base de datos en memoria para desarrollo y testing
- **Lombok**: Para reducir el código boilerplate
- **Maven**: Gestor de dependencias y construcción del proyecto
- **Log4j**: Para el manejo de logs

---

## 📁 Estructura del Proyecto

```
MartinPardo/
├── src/
│   ├── main/
│   │   ├── java/com/clinicaOdontologica/MartinPardo/
│   │   │   ├── controller/      # Controladores REST
│   │   │   ├── service/         # Lógica de negocio
│   │   │   ├── repository/      # Acceso a datos (JPA Repositories)
│   │   │   ├── model/           # Entidades del dominio
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── exception/       # Manejo de excepciones
│   │   │   └── security/        # Configuración de seguridad
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/          # Archivos estáticos (HTML, CSS, JS)
│   └── test/                    # Tests unitarios e integración
└── pom.xml
```

---

## 🗄️ Modelo de Datos

El sistema gestiona las siguientes entidades principales:

### **Paciente**
- ID (Long)
- Nombre (String)
- Apellido (String)
- Número de Contacto (Integer)
- Fecha de Ingreso (LocalDate)
- Email (String, único)
- Domicilio (relación OneToOne con Domicilio)

### **Odontólogo**
- ID (Long)
- Nombre (String)
- Apellido (String)
- Matrícula (String, único y obligatorio)

### **Turno**
- ID (Long)
- Paciente (relación ManyToOne con Paciente)
- Odontólogo (relación ManyToOne con Odontólogo)
- Fecha (LocalDate)

### **Domicilio**
- ID (Long)
- Calle (String)
- Número (Integer)
- Localidad (String)
- Provincia (String)

### **Usuario**
- ID (Long)
- Nombre (String)
- Apellido (String)
- Email (String, único)
- Password (String)
- UsuarioRol (Enum: ADMIN, USER)

---

## 🔌 Endpoints de la API

### **Autenticación**

- **POST** `/auth/login`: Autenticación de usuario
  - Body: `{ "username": "email@example.com", "password": "password" }`
  - Retorna: JWT token

- **POST** `/auth/logout`: Cierre de sesión

### **Pacientes**

- **POST** `/pacientes`: Crear un nuevo paciente
- **GET** `/pacientes`: Listar todos los pacientes
- **GET** `/pacientes/{id}`: Obtener paciente por ID
- **GET** `/pacientes/email/{email}`: Obtener paciente por email
- **PUT** `/pacientes/{id}`: Actualizar paciente
- **DELETE** `/pacientes/{id}`: Eliminar paciente

### **Odontólogos**

- **POST** `/odontologos`: Crear un nuevo odontólogo
- **GET** `/odontologos`: Listar todos los odontólogos
- **GET** `/odontologos/{id}`: Obtener odontólogo por ID
- **GET** `/odontologos/matricula/{matricula}`: Obtener odontólogo por matrícula
- **PUT** `/odontologos/{id}`: Actualizar odontólogo
- **DELETE** `/odontologos/{id}`: Eliminar odontólogo

### **Turnos**

- **POST** `/turnos`: Crear un nuevo turno
  - Body: `{ "fecha": "2024-01-15", "pacienteId": 1, "odontologoId": 1 }`
- **GET** `/turnos`: Listar todos los turnos
- **GET** `/turnos/{id}`: Obtener turno por ID
- **PUT** `/turnos/{id}`: Actualizar turno
- **DELETE** `/turnos/{id}`: Eliminar turno

---

## 🔐 Autenticación y Seguridad

El sistema implementa autenticación basada en JWT:

1. **Login**: El usuario se autentica enviando email y password a `/auth/login`
2. **Token JWT**: Se genera un token JWT válido por 1 hora (3600000 ms)
3. **Autorización**: Las peticiones a los endpoints protegidos requieren el header:
   ```
   Authorization: Bearer <token>
   ```

### Roles de Usuario
- **ADMIN**: Administrador con acceso completo
- **USER**: Usuario estándar

---

## 🚀 Cómo Ejecutar el Proyecto

### Requisitos Previos
- Java 21 o superior
- Maven 3.6+ (o usar el wrapper incluido: `mvnw`)

### Pasos para Ejecutar

1. **Clonar el repositorio**:
   ```bash
   git clone <url-del-repositorio>
   cd proyecto_clinica_odontologica
   ```

2. **Navegar al directorio del proyecto**:
   ```bash
   cd MartinPardo
   ```

3. **Compilar el proyecto**:
   ```bash
   ./mvnw clean install
   ```
   O en Windows:
   ```bash
   mvnw.cmd clean install
   ```

4. **Ejecutar la aplicación**:
   ```bash
   ./mvnw spring-boot:run
   ```
   O en Windows:
   ```bash
   mvnw.cmd spring-boot:run
   ```

5. **Acceder a la aplicación**:
   - API REST: `http://localhost:8080`
   - Consola H2: `http://localhost:8080/h2-console`
     - JDBC URL: `jdbc:h2:mem:~/clinicaFeliz`
     - Username: `sa`
     - Password: `sa`

---

## 🧪 Testing

El proyecto incluye tests unitarios e integración:

- **Tests Unitarios**: Para servicios y controladores
- **Tests de Integración**: Para verificar el flujo completo de la aplicación

Para ejecutar los tests:
```bash
./mvnw test
```

Los reportes de tests se generan en: `target/surefire-reports/`

---

## 📊 Base de Datos

La aplicación utiliza H2 Database, una base de datos en memoria que:

- Se crea automáticamente al iniciar la aplicación
- Se elimina al detener la aplicación (configuración `create-drop`)
- Es ideal para desarrollo y testing

### Configuración (application.properties)
```properties
spring.datasource.url=jdbc:h2:mem:~/clinicaFeliz
spring.datasource.username=sa
spring.datasource.password=sa
spring.jpa.hibernate.ddl-auto=create-drop
```

---

## 🎯 Características Principales

1. **CRUD Completo**: Operaciones completas de creación, lectura, actualización y eliminación para todas las entidades
2. **Validaciones**: Validación de datos de entrada y prevención de duplicados
3. **Manejo de Excepciones**: Sistema centralizado de manejo de excepciones con respuestas HTTP apropiadas
4. **Seguridad JWT**: Autenticación stateless con tokens JWT
5. **Arquitectura en Capas**: Separación clara de responsabilidades
6. **Documentación Postman**: Colección de Postman incluida para probar los endpoints

---

## 📝 Excepciones Personalizadas

El sistema maneja las siguientes excepciones:

- `ResourceNotFoundException`: Recurso no encontrado (404)
- `DuplicateResourceException`: Recurso duplicado (409)
- `ValidationException`: Error de validación (400)
- `TurnoConflictException`: Conflicto al crear turno (409)
- `BadRequestException`: Solicitud inválida (400)

---

## 📦 Dependencias Principales

Ver `pom.xml` para la lista completa. Las principales incluyen:

- `spring-boot-starter-web`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-security`
- `h2` (base de datos)
- `lombok`
- `jjwt` (JWT)
- `log4j`

---

## 👥 Notas del Proyecto

Este trabajo práctico fue desarrollado como parte de la materia "Microservicios y APIS escalables" y demuestra:

- Arquitectura REST
- Implementación de seguridad con JWT
- Persistencia de datos con JPA
- Buenas prácticas de desarrollo Java/Spring Boot
- Testing unitario e integración
- Manejo de excepciones
- Validaciones de negocio

---

## 📄 Licencia

Este proyecto es un trabajo práctico académico.

---

## 📧 Contacto

Para consultas sobre el proyecto, contactar a los integrantes del equipo.

---

*Última actualización: Enero 2024*

