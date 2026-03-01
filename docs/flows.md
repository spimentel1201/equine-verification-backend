# 🔄 Flujos del Sistema (Diagramas de Secuencia)

La operación del portal confiere secuencias robustas y atómicas a través del manejo de Tokens Bearer de SpringBoot.

## 1. Login y Generación de Token
El protocolo obligatorio para todos los `User` (Sellers o Admins). Mapeo sobre Endpoints abriertos `/api/auth`.

```mermaid
sequenceDiagram
    participant FrontEnd
    participant AuthController
    participant SpringSecurity (FilterChain)
    participant Database

    FrontEnd->>AuthController: POST /api/auth/login {email, password}
    AuthController->>SpringSecurity: attemptAuthentication()
    SpringSecurity->>Database: finByEmail(email)
    Database-->>SpringSecurity: User Entity (Password Hashed)
    SpringSecurity->>SpringSecurity: matches(raw, hashed)
    alt Contraseña Correcta
        SpringSecurity-->>AuthController: authSuccess
        AuthController->>AuthController: Generar JWT (HS512)
        AuthController-->>FrontEnd: Devuelve Token {accessToken, refreshToken}
    else Autenticación Fallida
        SpringSecurity-->>AuthController: authFailed
        AuthController-->>FrontEnd: 401 Unauthorized "Bad credentials"
    end
```

## 2. Creación de Anuncio y Petición de Verificación (Rol Seller)
Este flujo visualiza el paso crítico desde insertar en `DRAFT` hasta someterse a Inspección.

```mermaid
sequenceDiagram
    actor Seller
    participant API
    participant DB

    Seller->>API: POST /api/horses {name, breed, age}
    API-->>DB: INSERT horse returning ID
    DB-->>API: Horse ID
    API-->>Seller: 201 Created (Horse)

    Seller->>API: POST /api/listings {title, price, horseId}
    API-->>DB: INSERT listing status=DRAFT
    DB-->>API: Listing ID
    API-->>Seller: 201 Created (Listing Draft)

    Seller->>API: POST /api/evidences {fileUrl, listingId, TYPE}
    API-->>DB: INSERT evidence status=PENDING_REVIEW
    API-->>Seller: 201 Created Evidence

    Seller->>API: POST /api/verifications/request {listingId}
    API-->>DB: SELECT listing
    API->>API: Checks listing=DRAFT/REJECTED
    API-->>DB: UPDATE listing status=PENDING_VERIFICATION
    API-->>DB: INSERT verification status=PENDING
    API-->>Seller: 201 Request Submitted
```

## 3. Revisión del Administrador (Rol Admin)
Intervención y resolución sobre Anuncios congelados.

```mermaid
sequenceDiagram
    actor Admin
    participant FrontendAdmin
    participant API
    participant DB

    Admin->>FrontendAdmin: Click "Ver Casos Pendientes"
    FrontendAdmin->>API: GET /api/verifications?status=PENDING
    API-->>DB: SELECT WHERE status=PENDING + pagination
    DB-->>API: Page<VerificationResponse>
    API-->>FrontendAdmin: Lista Paginada

    Admin->>FrontendAdmin: Click Evaluar (ID=1)
    FrontendAdmin->>API: GET /api/evidences?listingId=100
    API-->>FrontendAdmin: [Foto, PDF Coggins...]

    Admin->>FrontendAdmin: Emitir Veredicto (APPROVED)
    FrontendAdmin->>API: PUT /api/verifications/1/review {status=APPROVED, notes="OK"}
    API-->>DB: SELECT verification + listing
    API-->>DB: UPDATE verification setVerifier(Admin), status=APPROVED
    API-->>DB: UPDATE listing status=VERIFIED
    API-->>FrontendAdmin: 200 OK (Verification)
```
