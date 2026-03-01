# 📚 Equine Verification Backend - Documentación Oficial

Bienvenido a la carpeta de documentación del proyecto. Este directorio contiene los diagramas, manuales de arquitectura técnica y diagramas de estados y secuencias que describen el panorama general de este Marketplace de caballos verificados.

## 🗂️ Índice de Contenidos

Este repositorio de documentación ha sido fragmentado en 4 pilares fundamentales para facilitar su lectura:

1. **[Arquitectura y Seguridad (`architecture.md`)](architecture.md)**
   Estructura general de directorios, patrón Modelo-Servicio-Controlador usado, mitigación de N+1 Queries, paginación base y configuración de OWASP Top 10 que blinda el código.

2. **[Módulos y Dominio (`modules.md`)](modules.md)**
   Reglas de negocio detalladas por cada actor de los procesos. Trata las limitantes lógicas como bloqueos biológicos a Perfiles de Caballos (Iceboxing temporal de propiedades) e interrelaciones Auth.

3. **[Flujos y Secuencias (`flows.md`)](flows.md)**
   Diagramas de secuencia renderizados que explican mediante vectores e hilos asíncronos el Login, Petición de un Review por parte de Sellers y las acciones de Aprobación realizadas por el panel web Administrador.

4. **[Diagrama de Base de Datos (`database.md`)](database.md)**
   Diccionario de base de datos relacional (Entity Relationship Diagram). Demuestra físicamente cómo funcionan las `Foreign Keys` y las `UUID`. Tipos de dato en las columnas e insersiones condicionales huérfanas bajo JPA e Hibernate.

---

### Mantenimiento de la Documentación
> Cualquier modificación sobre `Controllers`, o alteración del Esquema Entidad-Relación (`src/main/java/com/horsetrust/models/entities/*`) debería verse obligatoriamente reflejada en un _Pull Request_ que modifique simétricamente uno de los ficheros descritos arriba.

_Escrito y Generado bajo estándar de Clean Code, Spring Boot 3.x - 2026_
