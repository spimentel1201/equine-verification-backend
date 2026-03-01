# 🗄️ Diccionario y Diagrama de Base de Datos

Equine Verification Backend mantiene el modelo Relacional sobre motor **PostgreSQL** para prever la escalabilidad en producción. La estructura de Foreign Keys bloquea la insersión de falsos positivos en el servidor. Todas las claves primarias (`Primary Keys`) son universales representadas por `UUID` V4.

```mermaid
erDiagram
    users {
        UUID id PK
        VARCHAR email "Unique"
        VARCHAR password
        VARCHAR firstName
        VARCHAR lastName
        VARCHAR companyName
        VARCHAR phone
        VARCHAR role "SELLER|ADMIN"
        BOOLEAN active
        TIMESTAMP createdAt
    }

    horses {
        UUID id PK
        VARCHAR name
        VARCHAR breed
        INTEGER age
        VARCHAR gender "MALE|FEMALE|GELDING"
        UUID ownerId FK "References users"
        TIMESTAMP createdAt
    }

    listings {
        UUID id PK
        VARCHAR title
        TEXT description
        DECIMAL price
        VARCHAR location
        VARCHAR status "DRAFT|PENDING_VERIFICATION|VERIFIED|REJECTED|WITHDRAWN"
        UUID horseId FK "References horses"
        UUID sellerId FK "References users"
        TIMESTAMP createdAt
        TIMESTAMP updatedAt
    }

    evidences {
        UUID id PK
        VARCHAR type "MEDICAL_RECORD|OWNERSHIP_DOC|DNA_TEST|IMAGE|VIDEO|OTHER"
        VARCHAR status "PENDING_REVIEW|REJECTED|VERIFIED"
        VARCHAR fileUrl
        TEXT description
        TEXT metadata
        UUID listingId FK "References listings (nullable)"
        UUID horseId FK "References horses (nullable)"
        UUID uploaderId FK "References users"
        TIMESTAMP uploadedAt
    }

    verifications {
        UUID id PK
        VARCHAR target "LISTING|HORSE|USER"
        UUID targetId
        VARCHAR status "PENDING|IN_PROGRESS|APPROVED|REJECTED|NEEDS_INFO"
        TEXT notes
        UUID verifierId FK "References users (nullable)"
        TIMESTAMP validUntil
        TIMESTAMP createdAt
        TIMESTAMP updatedAt
    }

    refresh_tokens {
        UUID id PK
        UUID userId FK "References users"
        VARCHAR token "Unique indexed"
        TIMESTAMP expiryDate
    }

    users ||--o{ horses : "owns (One-to-Many)"
    users ||--o{ listings : "creates"
    horses ||--o{ listings : "listed in"
    users ||--o{ evidences : "uploads"
    listings ||--o{ evidences : "has"
    horses ||--o{ evidences : "has (standalone)"
    users ||--o{ verifications : "audited by (Admin)"
    users ||--o{ refresh_tokens : "owns"

```

## Notas Técnicas del Esquema JPA
* **Cascadas:** En H2 y PostgreSQL nos apalancamos en persistencia huérfana (Orphan Removal). Eliminar un usuario forzará borrado en cascada (CascadeType.ALL) de `refresh_tokens`.
* **Polimorfismo mitigado:** La tabla `verifications` no asocia por ForeignKey un objeto fuerte (Listing, etc), sino que enruta flexiblemente con el texto del `target` y su `targetId`. Esto le permite a futuro verificar a caballos mismos que no tengan venta temporal o inclusive un usuario general.
* **Tiempos Base (`Auditing`):** A nivel tabla todas las Entidades operan con los timestamps provistos por Hibernate de Creación `createdAt` (`@CreationTimestamp`) y de Actalización `updatedAt` (`@UpdateTimestamp`).
