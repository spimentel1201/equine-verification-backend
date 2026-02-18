# HorseTrust Backend

HorseTrust es un marketplace seguro de equinos diseñado para conectar compradores y vendedores verificados, garantizando la integridad de las transacciones y el bienestar animal.

Este repositorio contiene el backend de la aplicación, construido con **Java** y **Spring Boot**.

## 🚀 Tecnologías

*   **Java 17**
*   **Spring Boot 3.4.3**
*   **Spring Data JPA** (Hibernate)
*   **Spring Security**
*   **JWT** (JSON Web Tokens)
*   **H2 Database** (Entorno de desarrollo / memoria)
*   **PostgreSQL** (Entorno de producción)
*   **Maven** (Gestor de dependencias)
*   **Lombok**

## 📋 Requisitos Previos

Tener instalado:

*   [Java Development Kit (JDK) 17](https://www.oracle.com/java/technologies/downloads/#java17) o superior.
*   [Maven](https://maven.apache.org/download.cgi)

## 🛠️ Configuración y Ejecución

### 1. Clonar el repositorio

```bash
git clone <URL_DEL_REPOSITORIO>
cd equine-verification-backend
```

### 2. Configuración de Base de Datos

Por defecto, el proyecto está configurado para usar **H2 Database** en memoria para facilitar el desarrollo. No se requiere configuración adicional.

Si deseas usar **PostgreSQL**, modifica el archivo `src/main/resources/application.properties` descomentando las líneas correspondientes a PostgreSQL y configurando tu usuario/contraseña.

### 3. Compilar el proyecto

```bash
mvn clean compile
```

### 4. Ejecutar la aplicación

Puedes ejecutar la aplicación directamente usando Maven:

```bash
mvn spring-boot:run
```

O empaquetar y ejecutar el JAR:

```bash
mvn clean package
java -jar target/horsetrust-backend-0.0.1-SNAPSHOT.jar
```

La aplicación iniciará en `http://localhost:8080`.

### 5. Acceder a la Consola H2

Si la aplicación está corriendo con el perfil por defecto (H2):
*   **URL:** `http://localhost:8080/h2-console`
*   **JDBC URL:** `jdbc:h2:mem:horsetrustdb`
*   **User Name:** `sa`
*   **Password:** (dejar en blanco)

## 📂 Estructura del Proyecto

```
src/main/java/com/horsetrust/
├── models/
│   ├── entities/       # Entidades JPA (User, Horse, Listing, etc.)
│   └── enums/          # Enumeraciones (UserRole, ListingStatus, etc.)
├── HorseTrustApplication.java  # Clase principal
```