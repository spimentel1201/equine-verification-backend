# 🏗️ Arquitectura del Sistema

El proyecto **Equine Verification Backend** está diseñado bajo los principios de **Clean Architecture** y sigue el patrón arquitectónico tradicional **MVC modificado (Modelo - Servicio - Controlador)**, ideal para la construcción escalable y predecible de software en el ecosistema Spring Boot.

## 🛠️ Stack Tecnológico

1. **Lenguaje y Entorno:** Java 17 LTS / Spring Boot 3.x
2. **Framework Web:** Spring Web MVC
3. **Persistencia (ORM):** Spring Data JPA (Hibernate)
4. **Bases de Datos:** PostgreSQL (Producción/Staging) y H2 Database (Entorno Desarrollo / Tests).
5. **Seguridad:** Spring Security con Tokens JWT (JSON Web Tokens). Manejador Stateless, sin sesiones almacenadas del lado del servidor.
6. **Autenticación:** Proveedor BCryptPasswordEncoder (Hash de contraseñas de ida y múltiples validaciones de Auth).
7. **Documentación Automática de API:** OpenAPI 3 / Swagger-UI.
8. **Manejo de Variables de Entorno:** dotenv-java (Archivos `.env`).
9. **Media y Nube Externa:** Integración Java SDK con Cloudinary para subidas/descarte mediante flujos `multipart/form-data`.

## 🧱 Estructura de Paquetes (Patrón de Capas)

El diseño arquitectónico de código está encapsulado y separado por áreas de responsabilidad dentro de `src/main/java/com/horsetrust`.

### 1. `api/controllers/` (Capa Externa / Presentación)
Los controladores REST definen y mapean todos los Endpoints (`@RestController`). 
* Reciben las llamadas HTTP, delegan la lógica completa a `services/`.
* Se encargan única y exclusivamente de transformar la data web (JSON) a DTOs usando validación con `@Valid`.
* Mapean la configuración de Swagger para exponer la estructura al frontend.

### 2. `api/dto/` (Objetos de Transferencia de Datos)
Patrón Data Transfer Object. Los `Record` de Java inmutables se utilizan para intercambiar objetos al exterior y nunca exponer estructuras internas de la BD (Entidades DB).
* *Ej.* `CreateListingRequest`, `VerificationResponse`, `PageResponse`.

### 3. `services/` (Capa Principal / Lógica de Negocios)
Contiene las sentencias `@Service` ricas en lógica empresarial. Gestiona microservicios adaptables y centraliza lógicas comunes.
* **Componentes de Integración:** Emplea envoltorios (Wrappers) limpios (ej. `CloudinaryService`) que aíslan las bibliotecas foráneas. Si el día de mañana se migra Amazon S3, el resto del código del sistema no padece refactorización ni dolores gracias a este aislamiento interno.
* Gestiona todo mediante `@Transactional` (Commit or Rollback en cascada preventivo).
* Transforma modelos de Base de datos (Entities) a respuestas (DTOs) para exponer al controlador de manera acotada.
* Contiene validación exhaustiva de lógicas ("No puedes aprobar un Listing sin revisar"). Las fallas de negocio arrojan excepciones encapsuladas.

### 4. `repositories/` y `repositories/specifications` (Capa de Acceso a Datos / Infra)
Toda interacción y extracción de datos. 
* Los repositorios heredan `JpaRepository` y `JpaSpecificationExecutor`.
* Almacena lógicas de `Join Fetching` (`@EntityGraph`) para prevencion contra latencia del llamado N+1 Query.
* Las clases de `specifications/` habilitan la magia de la paginación de búsquedas dinámicas.

### 5. `models/` (Capa Central / Dominio)
Estructura y representación matemática de las variables operadas.
* `entities/`: Representación uno-a-uno a las tablas SQL de JPA. 
* `enums/`: Limitantes finitos de constantes para evitar errores ortográficos (Type Safety).

### 6. `security/` e `common/exception/` (Capa Transversal / Seguridad y Filtros)
* `security/`: Implementación de Auth principal. Autentica al Header entrante "Bearer: [Token]". Restringe Endpoints basándose en si quien pregunta tiene Rol 'ADMIN' o 'SELLER'. Implementación rigurosa a normas OWASP-Top-10 con configuración estricta de CORS.
* `common/exception/`: Define nuestra jerarquía personalizada de Errores (404, 401, 403, 400). Una clase Global `GlobalExceptionHandler` unifica intercepciones no planeadas evitando fuga de StackTraces al usuario final.

---

## 🔒 Mecanismos y Patrón OWASP
Toda la API está blindada en `SecurityConfig`:
- **Previene Click-Jacking**: Bloqueo estricto del encabezado `X-Frame-Options: SAMEORIGIN`.
- **Previene Cross-Site Scripting (XSS)**: Activación de mitigación `X-XSS-Protection`.
- **Enforcement HTTPS**: Se transmite obligatoriamente SSL por HSTS en Producción.
- **Detección MIME**: Deniega cualquier forzado de inyección de recursos cruzados por `X-Content-Type-Options: nosniff`.
- **CORS Validado**: Ningún host cruzado puede comunicarse a la API sino están explícitamente listados en la lista blanca con `SetAllowedOrigins` del `.env`.

---

## 📦 Paginación Avanzada (`PageResponse<T>`)
Todo listado grande de datos (búsqueda de Verificaciones o Caballos) viene encapsulado en una Respuesta Estándar Paginal que ayuda al FrontEnd en el dibujo de Tablas y Listas Infinitas.
```json
{
  "content": [
    { ... }
  ],
  "pageNo": 0,
  "pageSize": 10,
  "totalElements": 25,
  "totalPages": 3,
  "last": false
}
```
Todos los repositorios aceptan en el Endpoint los parámetros inyectables como URL Params de `@PageableDefault` (Por ejemplo: `?page=0&size=50`).
