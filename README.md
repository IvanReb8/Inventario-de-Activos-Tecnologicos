# Inventario de Activos Tecnológicos

Este repositorio contiene dos proyectos relacionados que conforman una solución completa de gestión de activos tecnológicos:

- `catalogs/`: backend Spring Boot 3.5.14 con API REST, JWT y acceso a MariaDB.
- `catalogs-frontend/`: frontend Angular 21.2.1 que consume la API de inventario.

## Descargar el repositorio

1. Clona el repositorio desde GitHub:

   ```bash
   git clone https://github.com/IvanReb8/Inventario-de-Activos-Tecnologicos.git
   ```

   Si no tienes Git instalado, [descárgalo aquí](https://git-scm.com).

2. Entra en el directorio del repositorio:

   ```bash
   cd Inventario-de-Activos-Tecnologicos
   ```

3. Verifica que ambas carpetas estén presentes:

   ```bash
   ls -la
   ```

   Deberías ver las carpetas `catalogs/` y `catalogs-frontend/`.

## Requisitos previos

- Java 17
- Maven (opcional, el proyecto incluye `mvnw.cmd`)
- Node.js y npm
- MariaDB o MySQL compatible

## Ejecutar el backend

1. Abre una terminal en la carpeta `catalogs/`.
2. Ejecuta el backend usando el wrapper de Maven:

   ```powershell
   .\mvnw.cmd clean spring-boot:run
   ```

3. Accede a la API en:

   - `http://localhost:8080`

### Configuración de la base de datos

La configuración actual está en `catalogs/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mariadb://localhost:3307/catalog_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
```

Asegúrate de que la base de datos `catalog_db` exista y que el usuario tenga permisos de lectura/escritura.

### Inicializar datos de prueba

El archivo `script.sql` incluido en la raíz del repositorio contiene datos de ejemplo para categorías y activos.

1. Crea la base de datos si no existe:

   ```sql
   CREATE DATABASE IF NOT EXISTS catalog_db;
   ```

2. Carga el script en MariaDB/MySQL:

   ```sql
   USE catalog_db;
   SOURCE script.sql;
   ```

   > Si tu cliente no admite `SOURCE`, copia y pega el contenido de `script.sql`.

## Ejecutar el frontend

1. Abre una terminal en `catalogs-frontend/`.
2. Instala dependencias:

   ```bash
   npm install
   ```

3. Inicia el servidor de desarrollo:

   ```bash
   npm start
   ```

4. Abre en el navegador:

   - `http://localhost:4200`

## Credenciales de acceso

- Administrador:
  - Usuario: `admin`
  - Contraseña: `admin`

- Usuario normal:
  - Usuario: `user`
  - Contraseña: `user`

## Endpoints más importantes

### Autenticación
- `POST /api/v1/auth/login`
  - Body: `{ "username": "admin", "password": "admin" }`

### Categorías
- `GET /api/v1/categories`

### Activos tecnológicos
- `GET /api/v1/assets`
  - Permite filtros, paginación y ordenamiento con `sortBy` / `sortDir`.
- `POST /api/v1/assets`
- `PUT /api/v1/assets/{id}`
- `PATCH /api/v1/assets/{id}/status`
- `GET /api/v1/assets/export`

## Características clave

- Autenticación JWT con roles `ADMIN` y `USER`.
- Gestión de categorías y activos tecnológicos.
- Validación de activos con número de serie único.
- Registro de activos con folio automático por categoría.
- Exportación a ZIP en memoria con Excel y archivo de auditoría.
- Interfaz Angular con filtros, paginación, creación/edición y descarga de reportes.

## Comandos útiles

### Backend

- Compilar:
  ```powershell
  .\mvnw.cmd clean package
  ```

### Frontend

- Compilar:
  ```bash
  npm run build
  ```

## Estructura principal

- `catalogs/`
  - `pom.xml`: dependencias Maven
  - `src/main/java`: código Java
  - `src/main/resources/application.properties`: configuración

- `catalogs-frontend/`
  - `package.json`: dependencias y scripts npm
  - `src/`: código fuente Angular

---
