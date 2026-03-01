# 📦 Módulos del Sistema

El ecosistema principal del Equine Verification Backend gira en torno al **Mercado Transaccional de Equinos (Caballos)** respaldado fuertemente por mecanismos de evidencia transparente avalados en un panel de Administración central.

## 1. Módulo JWT / Autenticación (`AuthController`)
Es el punto de entrada principal donde fluye el ingreso al programa.
* **Componentes:** `AuthService`, `JwtTokenProvider`.
* **Roles:** `SELLER` (Vendedor Estándar) y `ADMIN` (El Equipo de Trusting/Soporte).
* Los tokens JWT manejan una vida útil (Expiran en X horas definiéndose en el fichero `.env`). Toda la plataforma está forzada a usar esta barrera de token sin estado.

## 2. Módulo de Vendedores / Usuarios (`UserController`)
Todo aquel humano que desee publicar debe pertenecer como Entidad `User`. 
* Únicamente se pide correo (`email`), contraseña y nombre.
* Un usuario podrá tener N Caballos distintos (Relación _One To Many_) mediante UUID cruzado.

## 3. Módulo Equinos (`HorseController`)
Estructura Inmutable (De sólo Lectura). Antes de que un Vendedor intente Publicar la venta un Caballo en el Marketplace de Anuncios, él debe declarar el elemento que se vende (El Caballo / Horse). 
* **Registros de Raza y Genética:** Contiene Nombre, Raza (`breed`), Edad, y Género (MACHO, HEMBRA, CASTRADO).
* **Limitación y candado anti-fraude:** Parte de la arquitectura fuerza a bloquear los datos bio-históricos del animal **UNA VEZ** se someta formalmente a un proceso posterior de Verificación humana; en ese punto quedan "anclados/locked", de modo que un estafador moderno ya no podrá cambiar en un punto en el futuro una "Raza Falsificada" o nombre.

## 4. Módulo Anuncios del Marketplace (`ListingController`)
Agrupa el proceso general de venta en el mercado. Un `Listing` enlaza "1 Vendedor humano" con "1 Caballo base" para crear un ofrecimiento público al front-end final de ventas.
* **Manejo de Estados Temporales:** El Listing fluye por ciclo de vida finito.
  * Inicia como `DRAFT` (Borrador Editable).
  * Salto a `PENDING_VERIFICATION` (No editable, evaluación requerida).
  * Final en `VERIFIED` (Triunfo, se pinta en verde en la pantalla principal).
  * Desvío a `REJECTED` o `WITHDRAWN`.
* Cuenta con **búsquedas dinámicas**, permitiéndole a un usuario sin autenticar listar solo aquellos `VERIFIED` filtrando por Ubicación, Precio Mínimo/Máximo o si el título hace Match con texto.

## 5. Módulo de Evidencias Documentales (`EvidenceController`)
Alimenta sustancialmente la aprobación de un `Listing`. Es libre de subir por el Seller y se relaciona en cadena (Puede apuntar tanto a un Anuncio `Listing` como directo a la vida general de un Caballo `Horse`).
* **Formatos de Atestación Múltiple (`EvidenceType`):** Imágenes Generales `IMAGE`, Vete-Chequeos médicos (`MEDICAL_RECORD`), Certificaciones Legales de ADN/Raza (`OWNERSHIP_DOC`, `DNA_TEST`) o Audios (`VIDEO`).
* Soportan Subida de estado `PENDING_REVIEW` que el seller puede eliminar antes del salto a auditoría final.

## 6. Módulo de Verificaciones / Panel Administrador (`VerificationController`)
El corazón blindado exclusivo de los Empleados (Roles `ADMIN`). Agrupa una carga masiva paginada mediante `Specification` Dinámico de todos los Listings pendientes. 
* Un Administrador abre un ticket (Reclamo de `Verification` sobre un `Listing`).
* El Administrador puede emitir Veredictos (`APPROVED`, `REJECTED`, `NEEDS_INFO`) con retroalimentación textual al vendedor. Un `APPROVED` sella como verde infinito (Válido 1 Año) el Anuncio principal en base de datos.
