# Product Backlog — TiendaQ

**Proyecto:** TiendaQ - Sistema de Comercio Electrónico Universitario
**Organización:** Fundación Universitaria Konrad Lorenz (FUKL) - Club K-Forge
**Versión:** 1.0
**Fecha:** Abril 2026
**Estado:** En revisión

---

## Tabla de Contenidos

- [Contexto y Decisiones Arquitectónicas](#contexto-y-decisiones-arquitectónicas)
- [Equipo y Roles](#equipo-y-roles)
- [Convenciones del backlog](#convenciones-del-backlog)
- [Épicas e Historias de Usuario](#épicas-e-historias-de-usuario)
  - [Épica 1 — Configuración Base del Proyecto](#épica-1--configuración-base-del-proyecto)
  - [Épica 2 — Autenticación y Autorización](#épica-2--autenticación-y-autorización)
  - [Épica 3 — Gestión de Productos](#épica-3--gestión-de-productos)
  - [Épica 4 — Catálogo y Búsqueda](#épica-4--catálogo-y-búsqueda)
  - [Épica 5 — Carrito de Compras](#épica-5--carrito-de-compras)
  - [Épica 6 — Checkout y Facturación](#épica-6--checkout-y-facturación)
  - [Épica 7 — Gestión de Inventario](#épica-7--gestión-de-inventario)
  - [Épica 8 — Gestión de Usuarios](#épica-8--gestión-de-usuarios)
  - [Épica 9 — Perfil de Usuario](#épica-9--perfil-de-usuario)
  - [Épica 10 — Reportes y Estadísticas](#épica-10--reportes-y-estadísticas)
  - [Épica 11 — Deuda Técnica](#épica-11--deuda-técnica)
- [Resumen General](#resumen-general)
- [Propuesta de Sprints](#propuesta-de-sprints)
- [Registro de Cambios](#registro-de-cambios)

---

## Contexto y Decisiones Arquitectónicas

### Arquitectura definida

El proyecto TiendaQ utilizará una arquitectura **monolítica** con una única base de datos PostgreSQL. Esta decisión fue tomada considerando el tamaño del equipo (4 personas) y el alcance académico del proyecto. Una arquitectura de microservicios queda fuera del alcance actual pero puede considerarse en futuras iteraciones.

```
[ Angular 21 SPA ] --HTTP/JSON--> [ Spring Boot 4.0 API ] --JDBC--> [ PostgreSQL ]
```

### Stack tecnológico confirmado

| Capa | Tecnología | Versión |
|------|-----------|---------|
| Backend | Java + Spring Boot | 25 / 4.0.0 |
| Frontend | Angular | 21.2.x |
| Base de datos | PostgreSQL | 15+ |
| Build backend | Maven | 3.9+ |
| Package manager | pnpm + Bun | — |

### Restricciones conocidas

- Las contraseñas deben almacenarse con hash BCrypt (actualmente están en texto plano en los datos de prueba).
- El IVA aplicado es del 19% según la legislación colombiana.
- El pago es simulado: se registra el método pero no se procesa externamente.
- La entidad `DetalleFactura` existe en la base de datos pero **no tiene clase JPA** todavía.
- Ningún endpoint tiene seguridad activa actualmente.

---

## Equipo y Roles

| Rol Scrum | Nombre | Responsabilidad técnica |
|-----------|--------|------------------------|
| Product Owner | Brian Steven Vargas Clavijo | Definición de requerimientos y prioridades |
| Scrum Master / Dev - Frontend | Juan Camilo Prieto Mestizo | Coordinación de sprints y retrospectivas |
| Dev — BD | Nicoll Alejandra Duran Quintero | Esquema PostgreSQL, modelo de datos, migraciones |
| Dev — Backend | Miguel Angel Quintin Acero | API REST, lógica de negocio, seguridad |

> **Nota:** En este backlog, "Frontend" hace referencia al desarrollador encargado de Angular, "Backend" al desarrollador encargado de la API Spring Boot, y "BD" al desarrollador encargado del esquema PostgreSQL y las migraciones. Cuando una historia requiere coordinación se indica explícitamente.

---

## Convenciones del backlog

### Tipos de items

Cada item del backlog se clasifica con un prefijo según su naturaleza:

| Prefijo | Tipo | Qué es | Quién recibe valor |
|---------|------|--------|--------------------|
| `HU-` | Historia de Usuario | Funcionalidad visible al usuario final. Sigue el formato "Como X, quiero Y, para Z". | Cliente, Empleado o Administrador |
| `TT-` | Tarea Técnica | Cambio interno (refactor, fix de modelo, validación) sin valor visible directo. | Equipo de desarrollo (calidad/correctitud) |
| `EN-` | Enabler / Infraestructura | Habilita features futuras: CI/CD, configuración, observabilidad, scaffolding. | Operación + futuras HUs |
| `MX-` | Mixto | Combina feature visible con requisito técnico/legal. | Múltiple |
| `SP-` | Spike (no usado actualmente) | Investigación o prueba de concepto con timebox. | Equipo (decisión técnica) |

**Regla de planeación:** Las TTs y ENs **fundacionales** (bloqueantes) se hacen ANTES que cualquier HU. Las TTs **acompañantes** se ejecutan junto con su HU asociada en el mismo sprint. Las ENs y TTs **independientes** se interleavean según capacidad.

**Regla de estimación:** TODOS los items (HU/TT/EN/MX) se estiman en planning poker con la misma escala Fibonacci. Los puntos miden complejidad+esfuerzo, no valor de negocio.

### Formato de Historias de Usuario

Cada HU sigue el formato estándar de Scrum:

> *Como **[actor]**, quiero **[acción]**, para **[beneficio/valor]**.*

### Niveles de priorización MoSCoW

| Etiqueta | Significado |
|----------|-------------|
| 🔴 Must have | Bloqueante. Sin esto el sistema no puede funcionar. |
| 🟡 Should have | Muy importante. Debe estar en los primeros sprints. |
| 🟠 Could have | Necesario pero puede esperar a que lo crítico esté listo. |
| 🟢 Won't have | Deseable. Se aborda si hay tiempo disponible. |

### Estados posibles de una historia

| Estado | Descripción |
|--------|------------|
| 📋 Por hacer | Aún no se ha iniciado |
| 🔄 En curso | Alguien está trabajando en ella actualmente |
| 👁️ En revisión | Terminada, esperando revisión del equipo |
| ✅ Hecho | Completada y aceptada por el Product Owner |

---

## Épicas e Historias de Usuario

---

### Épica 1 — Configuración Base del Proyecto

**Descripción:** Establecer la infraestructura técnica mínima antes de desarrollar cualquier funcionalidad. Sin esta base, el resto del equipo no puede trabajar de forma segura ni coordinada.

---

#### EN-001 — Estructura de carpetas y convenciones del proyecto

| Campo | Detalle |
|-------|---------|
| **Historia** | Como equipo, quiero tener definidas las convenciones de código y la estructura de carpetas del proyecto, para que todos trabajemos de forma coherente sin pisarnos. |
| **Responsable** | Todos (revisar AGENTS.md y la guia org-level CONTRIBUTING) |
| **Prioridad** | 🔴 Must have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | — |
| **Criterios de aceptación** | - Las convenciones de commits (Conventional Commits) están documentadas y todos las siguen. <br>- La estructura de ramas Git Flow está activa (`main`, `develop`, `feature/*`). <br>- El equipo acordó cómo nombrar branches, clases y métodos. |
| **Notas** | La guia de contribucion vive en el repo org-level [`K-Forge/.github/blob/main/CONTRIBUTING.md`](https://github.com/K-Forge/.github/blob/main/CONTRIBUTING.md). El `AGENTS.md` local complementa con contexto especifico de TiendaQ. |

---

#### TT-002 — Implementar DTOs para todas las entidades del Backend

| Campo | Detalle |
|-------|---------|
| **Historia** | Como desarrollador Backend, quiero crear DTOs (objetos de transferencia de datos) para cada entidad, para que la API nunca exponga directamente las entidades JPA ni datos sensibles como contraseñas. |
| **Responsable** | Backend |
| **Prioridad** | 🔴 Must have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RNF-019 |
| **Criterios de aceptación** | - Existe un DTO de request y uno de response para cada entidad (Usuario, Producto, Carrito, etc.). <br>- Ningún endpoint retorna directamente una entidad JPA. <br>- El campo `contrasena` nunca aparece en ninguna respuesta de la API. |
| **Notas** | Actualmente todos los controllers exponen entidades JPA directamente. Esto es un problema de seguridad crítico. |

---

#### EN-003 — Configurar Spring Security con JWT

| Campo | Detalle |
|-------|---------|
| **Historia** | Como desarrollador Backend, quiero configurar Spring Security con autenticación JWT, para que todos los endpoints estén protegidos y solo los usuarios autorizados puedan acceder. |
| **Responsable** | Backend |
| **Prioridad** | 🔴 Must have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-002, RF-003, RF-004 |
| **Criterios de aceptación** | - Existe un `SecurityFilterChain` configurado. <br>- Los endpoints públicos (login, registro) no requieren token. <br>- Los endpoints protegidos retornan 401 si no hay token válido. <br>- Los endpoints de administrador retornan 403 si el rol no es suficiente. |
| **Notas** | La dependencia de Spring Security ya está en el `pom.xml`, pero sin configuración activa. |

---

#### TT-004 — Implementar manejo global de errores en el Backend

| Campo | Detalle |
|-------|---------|
| **Historia** | Como desarrollador Backend, quiero un manejador global de excepciones, para que todos los errores de la API tengan un formato JSON consistente y no expongan detalles internos del sistema. |
| **Responsable** | Backend |
| **Prioridad** | 🔴 Must Have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-058, RF-059 |
| **Criterios de aceptación** | - Existe una clase `@ControllerAdvice` que captura excepciones. <br>- Todos los errores retornan JSON con: `timestamp`, `status`, `error`, `message`, `path`. <br>- Ningún error expone stack traces ni nombres de tablas de la base de datos. |
| **Notas** | El paquete `exception/` existe en el proyecto pero está vacío. |

---

#### TT-005 — Corregir mapeo JPA (epica, partida en sub-items)

> Este item se partio en TT-005a, TT-005b y TT-005c por tamano (probable >13 pts). El item original TT-005 queda como referencia de la epica completa.

#### TT-005a — Migrar tipos monetarios a BigDecimal

| Campo | Detalle |
|-------|---------|
| **Historia** | Como desarrollador de BD, quiero migrar todos los campos monetarios de `double` a `BigDecimal`, para eliminar errores de redondeo en calculos de IVA y totales de factura. |
| **Responsable** | BD + Backend |
| **Prioridad** | 🔴 Must Have (Sprint 1 — bloqueante absoluto) |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | — |
| **Criterios de aceptación** | - Cambiar `double` → `BigDecimal` en `Producto.precioUnitario`, `ItemsCarrito.subtotal`, `Factura.subtotal`, `Factura.iva`, `Factura.total`, `IntentoPago.monto`, `DetalleFactura.precioUnitario`, `DetalleFactura.subtotal`. <br>- Configurar Jackson `ObjectMapper` para no usar notacion cientifica al serializar. <br>- Constructores de `BigDecimal` siempre con `String` (no `double`) para evitar perdida de precision. <br>- Migracion Flyway que convierte columnas `DOUBLE PRECISION` → `NUMERIC(15,2)`. |
| **Notas** | Bloqueante absoluto antes de cualquier HU que toque dinero. Ver ADR-0004. |

---

#### TT-005b — Eliminar @Inheritance(JOINED) y migrar Cliente/Empleado a perfiles 1:1

| Campo | Detalle |
|-------|---------|
| **Historia** | Como desarrollador de BD, quiero refactorizar `Cliente` y `Empleado` como entidades independientes con FK `id_usuario UNIQUE`, para alinear el modelo JPA con el esquema PostgreSQL real y permitir que un Usuario tenga ambos perfiles si fuera necesario. |
| **Responsable** | BD + Backend |
| **Prioridad** | 🔴 Must Have (Sprint 2 candidato) |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | — |
| **Criterios de aceptación** | - Eliminar `@Inheritance(JOINED)` de `Usuario`. <br>- `Cliente` y `Empleado` quedan como `@Entity` independientes con `@Id` propio o `@MapsId` referenciando `Usuario.id`. <br>- Use case de registro crea Usuario + perfil correspondiente en transaccion atomica. <br>- Repositorios y casos de uso afectados se actualizan. |
| **Notas** | `@Inheritance(JOINED)` esta roto porque el SQL no comparte PK con Usuario. Ver ADR-0006. |

---

#### TT-005c — Anotar enums + agregar timestamps + indices en FKs

| Campo | Detalle |
|-------|---------|
| **Historia** | Como desarrollador de BD, quiero anotar correctamente enums, timestamps de auditoria e indices en FKs, para que Hibernate genere consultas eficientes y mapee a tipos PostgreSQL nativos. |
| **Responsable** | BD + Backend |
| **Prioridad** | 🔴 Must Have (Sprint 2 candidato) |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | — |
| **Criterios de aceptación** | - Anotar todos los enums con `@JdbcTypeCode(SqlTypes.NAMED_ENUM)` para que mapeen al tipo PostgreSQL nativo. <br>- Agregar `@CreationTimestamp` / `@UpdateTimestamp` en campos `created_at` / `updated_at` con `DEFAULT CURRENT_TIMESTAMP` en SQL. <br>- Agregar `@Index` en `@Table` para todas las FKs frecuentes (categoria_id, cliente_id, producto_id, factura_id, etc.). <br>- Verificar que `TipoDocumento` Java (CC, TI, CE, PASAPORTE, NIT, RUT, OTRO) coincida exactamente con el enum PostgreSQL. |
| **Notas** | Sin `@JdbcTypeCode`, Hibernate convierte enums a VARCHAR perdiendo el tipo nativo. Sin indices en FKs, Postgres hace seq-scan en joins frecuentes. |

---

#### EN-006 — Configurar la estructura base del Frontend Angular (Atomic Design + Signals)

| Campo | Detalle |
|-------|---------|
| **Historia** | Como desarrollador Frontend, quiero estructurar el proyecto Angular 21 con Atomic Design, Signals y PrimeNG, para tener una base solida, escalable y coherente con el design system de K-Forge. |
| **Responsable** | Frontend |
| **Prioridad** | 🔴 Must Have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-072 |
| **Criterios de aceptación** | - Estructura de carpetas Atomic Design: `atoms/`, `molecules/`, `organisms/`, `templates/`, `pages/`. <br>- Patron Container/Presentational: los containers viven en `pages/`, los presentacionales en `organisms/` y `molecules/`. <br>- `AuthService` con Signal store de sesion (sin NgRx). <br>- `authInterceptor` que inyecta el JWT en cada peticion, captura 401 e intenta refresh antes de fallar. <br>- `authGuard` y `roleGuard` para proteger rutas segun autenticacion y rol. <br>- PrimeNG instalado con tema custom que usa los design tokens de K-Forge (negro/amarillo en `_tokens.scss`). <br>- Lazy loading configurado por feature en `app.routes.ts`. <br>- `environment.ts` con la URL base de la API por ambiente. |
| **Notas** | Signals reemplaza NgRx para el MVP (10% del codigo, misma reactividad). PrimeNG proporciona los componentes UI; los design tokens de K-Forge personalizan el tema sin reescribir componentes. |

---

### Épica 2 — Autenticación y Autorización

**Descripción:** Todo lo relacionado con el ingreso al sistema, la identificación del usuario y el control de acceso según su rol.

**¿Por qué va después de la configuración base?** Porque sin DTOs y sin Spring Security configurado, implementar el login no tendría sentido: los datos quedarían expuestos y cualquiera podría acceder a todo.

---

#### HU-007 — Registro de nuevo cliente

| Campo | Detalle |
|-------|---------|
| **Historia** | Como usuario nuevo, quiero registrarme con mis datos personales (nombre, apellido, documento, teléfono, correo, dirección y contraseña), para crear mi cuenta y poder hacer compras. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🔴 Must Have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-001 |
| **Criterios de aceptación** | - El endpoint `POST /api/auth/registro` crea un Usuario y un Cliente en una sola transacción. <br>- La contraseña se almacena con hash BCrypt (nunca en texto plano). <br>- Si el correo, documento o teléfono ya existen, retorna 409 con mensaje claro. <br>- El formulario de Angular valida los campos en tiempo real antes de enviar. <br>- La respuesta nunca incluye la contraseña. |
| **Notas** | Actualmente la creación de usuario y cliente son operaciones separadas y las contraseñas no se hashean. |

---

#### HU-008 — Inicio de sesión

| Campo | Detalle |
|-------|---------|
| **Historia** | Como usuario registrado, quiero iniciar sesión con mi correo y contraseña, para acceder al sistema con las funciones que corresponden a mi rol. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🔴 Must Have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-002 |
| **Criterios de aceptación** | - El endpoint `POST /api/auth/login` retorna un token JWT válido al ingresar credenciales correctas. <br>- El token contiene el ID del usuario, su correo y su rol. <br>- Si las credenciales son incorrectas, retorna 401 con mensaje genérico (no indicar si el error es en el correo o en la contraseña, por seguridad). <br>- El frontend almacena el token de forma segura y redirige al usuario según su rol. |
| **Notas** | —  |

---

#### TT-009 — Validación automática del token JWT

| Campo | Detalle |
|-------|---------|
| **Historia** | Como sistema, quiero validar el token JWT en cada petición a un endpoint protegido, para garantizar que solo usuarios autenticados accedan a los recursos. |
| **Responsable** | Backend |
| **Prioridad** | 🔴 Must Have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-003 |
| **Criterios de aceptación** | - Las peticiones sin token retornan 401. <br>- Las peticiones con token expirado retornan 401. <br>- Las peticiones con token valido pero rol insuficiente retornan 403. <br>- El access token expira en **15 minutos**. <br>- El refresh token expira en **7 dias** y se rota en cada uso (el anterior queda invalidado). <br>- Existe el endpoint `POST /api/auth/refresh` que recibe el refresh token y retorna un nuevo par access/refresh. <br>- Los refresh tokens se almacenan hasheados en tabla `refresh_token(id, usuario_id, token_hash, expires_at, used_at)`. |
| **Notas** | 24h de expiracion es un antipatron de seguridad: cualquier token robado tiene acceso durante un dia completo. 15min + refresh rotation minimiza la ventana de exposicion. Ver ADR-0002. |

---

#### TT-010 — Autorización por roles

| Campo | Detalle |
|-------|---------|
| **Historia** | Como sistema, quiero restringir el acceso a cada endpoint según el rol del usuario autenticado, para que un cliente no pueda hacer lo que solo puede hacer un empleado o administrador. |
| **Responsable** | Backend |
| **Prioridad** | 🔴 Must Have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-004 |
| **Criterios de aceptación** | - Los endpoints de gestión de productos y stock solo son accesibles por VENDEDOR y ADMINISTRADOR. <br>- Los endpoints de gestión de usuarios solo son accesibles por ADMINISTRADOR. <br>- Los endpoints de carrito, perfil e historial son accesibles por CLIENTE. <br>- El rol ADMINISTRADOR hereda los permisos de VENDEDOR. |
| **Notas** | — |

---

#### HU-011 — Cierre de sesión

| Campo | Detalle |
|-------|---------|
| **Historia** | Como usuario autenticado, quiero poder cerrar sesión, para que mi sesión quede invalidada y nadie más pueda usarla desde el mismo dispositivo. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟡 Should have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-005 |
| **Criterios de aceptación** | - El endpoint `POST /api/auth/logout` recibe el access token y el refresh token. <br>- El `jti` (JWT ID) del access token se registra en la tabla `revoked_token(jti, user_id, expires_at)`. <br>- El refresh token se marca como usado en la tabla `refresh_token`. <br>- Peticiones posteriores con ese access token retornan 401 (el filtro JWT verifica contra `revoked_token`). <br>- Un job `@Scheduled` limpia automaticamente los registros de `revoked_token` cuyo `expires_at` ya paso, para no crecer indefinidamente. <br>- El frontend elimina ambos tokens del almacenamiento local y redirige al login. |
| **Notas** | El RNF-013 (stateless) y RF-005 (logout efectivo) son contradictorios si no existe la tabla de blacklist. La tabla `revoked_token` resuelve la contradiccion: el sistema es stateless por defecto pero invalida tokens explicitamente cuando es necesario. Ver ADR-0002. |

---

#### HU-012 — Recuperación de contraseña

| Campo | Detalle |
|-------|---------|
| **Historia** | Como usuario, quiero recuperar mi contraseña si la olvidé, para poder volver a acceder sin necesitar al administrador. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟡 Should Have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-006 |
| **Criterios de aceptación** | - El endpoint `POST /api/auth/recuperar` acepta un correo y genera un token temporal. <br>- El token temporal se registra en consola o en un endpoint dedicado (no se envía correo real). <br>- Con el token temporal el usuario puede establecer una nueva contraseña hasheada. |
| **Notas** | El envío real de correos está fuera del alcance del proyecto. |

---

### Épica 3 — Gestión de Productos

**Descripción:** Todo lo que los empleados necesitan para administrar el catálogo de productos: crear, editar, eliminar y listar.

---

#### HU-013 — Crear un producto

| Campo | Detalle |
|-------|---------|
| **Historia** | Como empleado, quiero crear un producto con nombre, categoría, precio e imagen (URL), para que esté disponible en el catálogo de la tienda. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🔴 Must Have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-008 |
| **Criterios de aceptación** | - El endpoint `POST /api/productos` solo es accesible por VENDEDOR y ADMINISTRADOR. <br>- El producto requiere: nombre, categoría (enum), precio unitario positivo. <br>- La imagen es una URL externa opcional. <br>- El nombre debe ser único dentro de la misma categoría. <br>- El formulario de Angular valida todos los campos antes de enviar. |
| **Notas** | La entidad `Producto` actualmente no tiene campo `imagen`. Debe añadirse (ver TT-044). |

---

#### HU-014 — Editar un producto existente

| Campo | Detalle |
|-------|---------|
| **Historia** | Como empleado, quiero editar los datos de un producto existente, para corregir errores o actualizar el precio o la imagen. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🔴 Must Have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-011 |
| **Criterios de aceptación** | - El endpoint `PUT /api/productos/{id}` actualiza nombre, categoría, precio e imagen. <br>- Si el producto no existe, retorna 404. <br>- La validación de unicidad de nombre por categoría se mantiene al editar. |
| **Notas** | — |

---

#### HU-015 — Eliminar un producto

| Campo | Detalle |
|-------|---------|
| **Historia** | Como empleado, quiero eliminar un producto que no tiene stock ni compras asociadas, para mantener el catálogo limpio y actualizado. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟡 Should Have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-012 |
| **Criterios de aceptación** | - Si el producto tiene stock o está en facturas, el endpoint retorna 409 con un mensaje descriptivo. <br>- Si se puede eliminar, retorna 204 (sin contenido). <br>- El frontend muestra un diálogo de confirmación antes de eliminar. |
| **Notas** | — |

---

#### HU-016 — Listar productos con paginación y filtros

| Campo | Detalle |
|-------|---------|
| **Historia** | Como cualquier usuario, quiero ver la lista de productos con paginación, filtros por categoría y búsqueda por nombre, para encontrar lo que necesito sin ver todo el catálogo de golpe. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🔴 Must have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-010, RF-013 |
| **Criterios de aceptación** | - El endpoint `GET /api/productos` soporta los parámetros: `page`, `size`, `sortBy`, `direction`. <br>- El endpoint `GET /api/productos/categoria/{categoria}` filtra por categoría. <br>- La respuesta incluye metadatos de paginación: `totalElements`, `totalPages`, `currentPage`. <br>- El frontend muestra los productos en una grilla con controles de paginación. |
| **Notas** | Actualmente el endpoint no soporta paginación ni `Pageable`. |

---

### Épica 4 — Catálogo y Búsqueda

**Descripción:** Las funcionalidades de exploración del catálogo orientadas al cliente final: buscar por nombre, filtrar por precio y ver el detalle de un producto.

---

#### HU-017 — Búsqueda de productos por nombre parcial

| Campo | Detalle |
|-------|---------|
| **Historia** | Como cliente, quiero buscar productos escribiendo solo parte del nombre, para encontrar lo que busco sin tener que escribirlo completo. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟡 Should have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-014 |
| **Criterios de aceptación** | - El endpoint acepta un parámetro `nombre` y hace búsqueda ILIKE (insensible a mayúsculas). <br>- Si no hay resultados, retorna lista vacía (no error). <br>- El parámetro debe tener al menos 2 caracteres. <br>- El frontend tiene un campo de búsqueda que se activa al escribir. |
| **Notas** | Requiere agregar `findByNombreContainingIgnoreCase` al repositorio. |

---

#### HU-018 — Filtro de productos por rango de precio

| Campo | Detalle |
|-------|---------|
| **Historia** | Como cliente, quiero filtrar productos por rango de precio (mínimo y máximo), para ver solo los que puedo pagar. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟡 Should have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-015 |
| **Criterios de aceptación** | - El endpoint acepta parámetros `precioMin` y `precioMax` (ambos opcionales). <br>- Si solo se proporciona `precioMin`, filtra productos con precio ≥ precioMin. <br>- Si solo se proporciona `precioMax`, filtra productos con precio ≤ precioMax. <br>- El frontend tiene controles de rango de precio. |
| **Notas** | — |

---

#### HU-019 — Ver detalle de un producto

| Campo | Detalle |
|-------|---------|
| **Historia** | Como cliente, quiero ver el detalle completo de un producto (nombre, categoría, precio, imagen y stock disponible), para decidir si lo compro. |
| **Responsable** | Frontend |
| **Prioridad** | 🔴 Must Have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-017 |
| **Criterios de aceptación** | - La pantalla de detalle consume el endpoint `GET /api/productos/{id}`. <br>- Si el stock es 0, se muestra la etiqueta "Agotado". <br>- Hay un botón para agregar al carrito (deshabilitado si está agotado). <br>- La imagen se muestra si existe URL; si no, se muestra un placeholder. |
| **Notas** | — |

---

### Épica 5 — Carrito de Compras

**Descripción:** El núcleo del e-commerce. El cliente agrega productos, los gestiona y el sistema mantiene la coherencia del inventario y los estados del carrito.

**¿Por qué es crítico?** Porque sin carrito no hay compras, y sin compras no hay facturas. Todo el flujo económico del sistema depende de esta épica.

---

#### HU-020 — Agregar un producto al carrito

| Campo | Detalle |
|-------|---------|
| **Historia** | Como cliente, quiero agregar un producto a mi carrito indicando la cantidad deseada, para ir seleccionando lo que quiero comprar. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🔴 Must have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-019 |
| **Criterios de aceptación** | - Si el cliente no tiene carrito activo, el sistema lo crea automáticamente con estado VACÍO. <br>- Si el producto ya está en el carrito, se suma la cantidad. <br>- Si el stock es insuficiente, retorna 409 con la cantidad disponible. <br>- El precio unitario se registra al momento de agregar (no cambia aunque el producto se actualice después). <br>- El estado del carrito cambia a CON_PRODUCTOS. |
| **Notas** | — |

---

#### HU-021 — Modificar la cantidad de un producto en el carrito

| Campo | Detalle |
|-------|---------|
| **Historia** | Como cliente, quiero cambiar la cantidad de un producto en mi carrito, para ajustar mi pedido antes de pagar. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🔴 Must Have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-020 |
| **Criterios de aceptación** | - Si la nueva cantidad es válida, se actualiza el item. <br>- Si la cantidad es 0, el item se elimina. <br>- Si el carrito queda vacío, el estado cambia a VACÍO. <br>- El sistema valida que el stock sea suficiente para la nueva cantidad. |
| **Notas** | — |

---

#### HU-022 — Eliminar un producto del carrito

| Campo | Detalle |
|-------|---------|
| **Historia** | Como cliente, quiero eliminar un producto específico de mi carrito, para cambiar de opinión sobre ese artículo. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🔴 Must have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-021 |
| **Criterios de aceptación** | - El item se elimina del carrito. <br>- Si el carrito queda vacío, el estado cambia a VACÍO. <br>- El frontend muestra confirmación antes de eliminar. |
| **Notas** | — |

---

#### HU-023 — Ver el carrito con totales calculados

| Campo | Detalle |
|-------|---------|
| **Historia** | Como cliente, quiero ver mi carrito con la lista de productos, el subtotal, el IVA (19%) y el total, para saber exactamente cuánto voy a pagar. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🔴 Must Have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-023 |
| **Criterios de aceptación** | - La respuesta incluye: lista de items, subtotal, IVA (19% del subtotal), total. <br>- Si el cliente no tiene carrito activo, retorna estructura vacía (no error). <br>- El carrito es accesible desde cualquier pantalla (componente lateral o modal). |
| **Notas** | — |

---

#### TT-024 — Validación de stock en tiempo real

| Campo | Detalle |
|-------|---------|
| **Historia** | Como sistema, quiero validar el stock disponible cada vez que se agrega o modifica un item en el carrito, para no permitir vender más de lo que hay en inventario. |
| **Responsable** | Backend |
| **Prioridad** | 🔴 Must have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-024 |
| **Criterios de aceptación** | - Si el stock es insuficiente, la operación se rechaza con 409 indicando la cantidad disponible. <br>- La validación ocurre tanto al agregar como al modificar la cantidad. |
| **Notas** | — |

---

#### TT-025 — Máquina de estados del carrito

| Campo | Detalle |
|-------|---------|
| **Historia** | Como sistema, quiero que el carrito siga una máquina de estados definida, para que el flujo de compra sea coherente y no se pueda saltar pasos. |
| **Responsable** | Backend |
| **Prioridad** | 🔴 Must have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-025 |
| **Criterios de aceptación** | - Las transiciones válidas son: VACÍO → CON_PRODUCTOS, CON_PRODUCTOS → VACÍO, CON_PRODUCTOS → EN_PROCESO_DE_PAGO, EN_PROCESO_DE_PAGO → PAGO_PENDIENTE, PAGO_PENDIENTE → PAGO_EXITOSO, EN_PROCESO_DE_PAGO → CON_PRODUCTOS. <br>- Cualquier otra transición retorna 400 con mensaje descriptivo. |
| **Notas** | — |

---

### Épica 6 — Checkout y Facturación

**Descripción:** El proceso de pago y la generación de la factura. Este es el momento en que el carrito se convierte en una compra real.

---

#### HU-026 — Iniciar el proceso de checkout

| Campo | Detalle |
|-------|---------|
| **Historia** | Como cliente, quiero iniciar el proceso de pago desde mi carrito, para formalizar mi compra. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🔴 Must have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-026 |
| **Criterios de aceptación** | - El sistema re-valida el stock de todos los productos del carrito al iniciar el checkout. <br>- Si hay stock insuficiente en algún producto, retorna 409 con el detalle. <br>- Si todo está bien, el estado del carrito cambia a EN_PROCESO_DE_PAGO. <br>- El frontend muestra un resumen del carrito antes de confirmar. |
| **Notas** | — |

---

#### HU-027 — Seleccionar método de pago

| Campo | Detalle |
|-------|---------|
| **Historia** | Como cliente, quiero seleccionar el método de pago que prefiero (PSE, tarjeta de crédito, tarjeta débito, efectivo o transferencia), para completar mi compra. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🔴 Must have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-027 |
| **Criterios de aceptación** | - Los métodos disponibles son los definidos en el enum `MetodoPago`. <br>- Al seleccionar, el estado del carrito cambia a PAGO_PENDIENTE. <br>- El método seleccionado queda registrado para la factura. |
| **Notas** | No se realiza procesamiento real del pago. Solo se registra el método. |

---

#### HU-028 — Generar factura al confirmar el pago

| Campo | Detalle |
|-------|---------|
| **Historia** | Como sistema, quiero generar una factura completa con el detalle de los productos comprados cuando el cliente confirma el pago, para tener un registro permanente e inmutable de la transacción. |
| **Responsable** | Backend |
| **Prioridad** | 🔴 Must have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-028 |
| **Criterios de aceptación** | - Se crea un registro en `Factura` con: fecha, subtotal, IVA (19%), total, método de pago, referencia al cliente, al empleado/sistema y al carrito. <br>- Se crean registros en `DetalleFactura` con el precio y cantidad de cada producto al momento de la compra. <br>- El estado del carrito cambia a PAGO_EXITOSO. <br>- Todo ocurre en una sola transacción atómica (si algo falla, todo se revierte). |
| **Notas** | Depende de TT-043 (entidad `DetalleFactura`). |

---

#### TT-029 — Descontar stock al facturar

| Campo | Detalle |
|-------|---------|
| **Historia** | Como sistema, quiero descontar automáticamente el stock de cada producto vendido al generar la factura, para que el inventario siempre refleje la realidad. |
| **Responsable** | Backend |
| **Prioridad** | 🔴 Must have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-032 |
| **Criterios de aceptación** | - El descuento de stock es parte de la misma transacción que genera la factura. <br>- Si el stock de algún producto es insuficiente en el momento exacto de facturar, toda la operación se revierte (rollback). <br>- El stock resultante nunca puede ser negativo. |
| **Notas** | — |

---

#### HU-030 — Cancelar el proceso de checkout

| Campo | Detalle |
|-------|---------|
| **Historia** | Como cliente, quiero cancelar el proceso de pago si aún no lo he confirmado, para volver a mi carrito y seguir comprando o modificar mi pedido. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟡 Should have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-029 |
| **Criterios de aceptación** | - Solo se puede cancelar si el carrito está en estado EN_PROCESO_DE_PAGO. <br>- Al cancelar, el estado vuelve a CON_PRODUCTOS. <br>- Los items del carrito permanecen intactos. |
| **Notas** | — |

---

#### HU-031 — Ver historial de facturas

| Campo | Detalle |
|-------|---------|
| **Historia** | Como cliente, quiero ver mi historial de compras con el detalle de cada factura, para llevar control de todo lo que he comprado. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟡 Should have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-030, RF-031 |
| **Criterios de aceptación** | - La lista está paginada y ordenada por fecha descendente (más reciente primero). <br>- Cada factura muestra: número, fecha, total y método de pago. <br>- Al hacer clic en una factura, se ve el detalle completo con productos, cantidades y precios. <br>- Un cliente solo puede ver sus propias facturas. |
| **Notas** | — |

---

### Épica 7 — Gestión de Inventario

**Descripción:** Control de existencias de los productos por parte de los empleados.

---

#### HU-032 — Registrar ingreso de stock

| Campo | Detalle |
|-------|---------|
| **Historia** | Como empleado, quiero registrar un ingreso de stock para un producto indicando la cantidad y la fecha, para actualizar el inventario cuando llegue mercancía. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🔴 Must have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-034 |
| **Criterios de aceptación** | - El endpoint `POST /api/stock` crea un registro de stock asociado al producto. <br>- La cantidad debe ser un entero positivo. <br>- Si el producto no existe, retorna 404. <br>- El frontend tiene un formulario para ingresar la cantidad. |
| **Notas** | — |

---

#### HU-033 — Ver inventario con alertas de stock bajo

| Campo | Detalle |
|-------|---------|
| **Historia** | Como empleado, quiero ver el inventario general con una alerta visual para los productos con poco stock, para saber cuándo debo pedir más mercancía. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟡 Should have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-036, RF-037 |
| **Criterios de aceptación** | - Los productos con stock ≤ 5 unidades se marcan como "stock bajo". <br>- Existe un endpoint dedicado `GET /api/stock/bajo` que retorna solo los productos con stock bajo. <br>- El frontend muestra un indicador visual destacado (color rojo o etiqueta) para estos productos. |
| **Notas** | El umbral de 5 unidades debe ser configurable en el futuro. |

---

### Épica 8 — Gestión de Usuarios

**Descripción:** El administrador tiene control total sobre las cuentas del sistema: puede crear empleados, editar datos y desactivar cuentas.

---

#### HU-034 — Listar usuarios con búsqueda y paginación

| Campo | Detalle |
|-------|---------|
| **Historia** | Como administrador, quiero ver la lista de todos los usuarios del sistema con paginación y búsqueda por nombre, correo o documento, para tener control sobre quién tiene acceso. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟡 Should have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-039, RF-045 |
| **Criterios de aceptación** | - El endpoint retorna la lista paginada de usuarios sin incluir contraseñas. <br>- Se puede buscar por documento (exacto) o por correo (parcial). <br>- Se puede filtrar por tipo de usuario (REGISTRADO, SIN_REGISTRAR). |
| **Notas** | — |

---

#### HU-035 — Crear cuenta de empleado

| Campo | Detalle |
|-------|---------|
| **Historia** | Como administrador, quiero crear cuentas de empleados (vendedor o administrador), para darles acceso al sistema con los permisos correspondientes. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟡 Should have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-041 |
| **Criterios de aceptación** | - Se crea el Usuario y el Empleado en una sola transacción. <br>- La contraseña se hashea con BCrypt. <br>- El tipo de empleado puede ser VENDEDOR o ADMINISTRADOR. |
| **Notas** | — |

---

#### HU-036 — Editar datos de un usuario

| Campo | Detalle |
|-------|---------|
| **Historia** | Como administrador, quiero editar los datos de cualquier usuario (nombre, apellido, teléfono, dirección, correo), para corregir información incorrecta. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟡 Should have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-042 |
| **Criterios de aceptación** | - No se puede modificar el tipo ni número de documento. <br>- Si se cambia el correo, se valida que no exista en otro usuario. <br>- Si se proporciona nueva contraseña, se hashea; si no, la existente no cambia. |
| **Notas** | — |

---

#### HU-037 — Eliminar o desactivar una cuenta de usuario

| Campo | Detalle |
|-------|---------|
| **Historia** | Como administrador, quiero desactivar o eliminar una cuenta de usuario que ya no la necesita, para mantener el sistema ordenado sin perder el historial de compras. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟠 Could have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-043 |
| **Criterios de aceptación** | - **Usuario**: soft-delete con campo `deleted_at TIMESTAMP NULL`. Se anula la visibilidad pero se preserva el historial de facturas. El usuario no puede iniciar sesion una vez eliminado. <br>- **Producto**: soft-delete con `deleted_at`. Deja de aparecer en el catalogo pero las facturas existentes conservan la referencia. <br>- **Carrito**: hard-delete solo si esta vacio y tiene mas de 30 dias de abandono. Un carrito con items no se elimina fisicamente. <br>- Las entidades con soft-delete usan `@SQLDelete` + `@Where(clause = "deleted_at IS NULL")` en Hibernate para que las queries normales no retornen registros eliminados. <br>- El frontend muestra confirmacion antes de desactivar y aclara que el historial se preserva. |
| **Notas** | El soft-delete tambien sirve como registro de auditoria basico: `deleted_at` indica cuando fue desactivado. Se complementa con `audit_log` (TT-066) para tener el `deleted_by`. Ver ADR-0007. |

---

### Épica 9 — Perfil de Usuario

**Descripción:** Cada usuario puede ver y actualizar su propia información de perfil.

---

#### HU-038 — Ver y editar perfil propio

| Campo | Detalle |
|-------|---------|
| **Historia** | Como usuario autenticado, quiero ver mi información de perfil y poder editar mi teléfono y dirección, para mantener mis datos de contacto actualizados. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟡 Should have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-046, RF-047 |
| **Criterios de aceptación** | - El endpoint `GET /api/perfil` retorna los datos del usuario autenticado sin incluir la contraseña. <br>- Solo se pueden editar: teléfono y dirección (no nombre, apellido ni documento). <br>- El cambio de correo requiere verificar la contraseña actual. |
| **Notas** | — |

---

#### HU-039 — Cambiar contraseña

| Campo | Detalle |
|-------|---------|
| **Historia** | Como usuario autenticado, quiero cambiar mi contraseña verificando la actual, para mantener mi cuenta segura. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟡 Should have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-048 |
| **Criterios de aceptación** | - El usuario debe proveer la contraseña actual (verificación con BCrypt). <br>- La nueva contraseña debe tener mínimo 8 caracteres, una mayúscula y un número. <br>- La nueva contraseña se almacena hasheada con BCrypt. |
| **Notas** | — |

---

### Épica 10 — Reportes y Estadísticas

**Descripción:** Información agregada para que el administrador pueda tomar decisiones de negocio.

**¿Por qué va al final?** Porque los reportes dependen de que existan ventas, facturas e inventario registrados. No tiene sentido construirlos primero.

---

#### HU-040 — Reporte de ventas por período

| Campo | Detalle |
|-------|---------|
| **Historia** | Como administrador, quiero ver un reporte de ventas para un rango de fechas, con el total vendido, el IVA recaudado y el número de facturas, para evaluar el desempeño del negocio. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟠 Could have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-049 |
| **Criterios de aceptación** | - El reporte acepta `fechaDesde` y `fechaHasta` como parámetros. <br>- Retorna: número total de facturas, monto total sin IVA, IVA total, total con IVA y promedio por factura. |
| **Notas** | — |

---

#### HU-041 — Ranking de productos más vendidos

| Campo | Detalle |
|-------|---------|
| **Historia** | Como administrador, quiero ver un ranking de los productos más vendidos en un período, para identificar cuáles tienen más demanda. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟠 Could have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-050 |
| **Criterios de aceptación** | - El ranking muestra: nombre del producto, categoría, cantidad vendida y monto generado. <br>- Por defecto muestra el top 10. El límite es configurable. <br>- Se puede filtrar por rango de fechas. |
| **Notas** | — |

---

#### HU-042 — Dashboard de indicadores clave (KPIs)

| Campo | Detalle |
|-------|---------|
| **Historia** | Como administrador, quiero ver un dashboard con los indicadores más importantes del negocio, para tener una visión general rápida sin necesidad de buscar en múltiples reportes. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟠 Could have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-054 |
| **Criterios de aceptación** | - El dashboard muestra: ventas del día, semana y mes, número de clientes registrados, cantidad de productos activos, productos agotados y valor total del inventario. |
| **Notas** | — |

---

### Épica 11 — Deuda Técnica

**Descripción:** Problemas identificados en el código y la base de datos actuales que deben resolverse para que el sistema funcione correctamente. Se llaman "deuda técnica" porque son cosas que quedaron pendientes del desarrollo inicial.

---

#### TT-043 — Crear la entidad JPA `DetalleFactura`

| Campo | Detalle |
|-------|---------|
| **Historia** | Como desarrollador Backend, quiero crear la clase JPA `DetalleFactura` que actualmente falta, para poder registrar el detalle de productos en cada factura generada. |
| **Responsable** | Backend + BD |
| **Prioridad** | 🔴 Must have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-028 |
| **Criterios de aceptación** | - Existe la clase `DetalleFactura.java` en el paquete `model`. <br>- Tiene clave compuesta (idFactura + idProducto) con `@IdClass` o `@EmbeddedId`. <br>- Los campos `cantidad` y `precioUnitario` tienen sus anotaciones de validación. <br>- El esquema SQL ya existe en `SCRIPTS_POSTGRES.sql`; solo falta la entidad Java. |
| **Notas** | La tabla `DetalleFactura` ya existe en la base de datos. |

---

#### TT-044 — Agregar campo `imagen` a la entidad `Producto`

| Campo | Detalle |
|-------|---------|
| **Historia** | Como desarrollador Backend, quiero agregar el campo `imagen` (URL) a la entidad Producto, para que los clientes puedan ver una foto del artículo en el catálogo. |
| **Responsable** | Backend + BD |
| **Prioridad** | 🟡 Should have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-008 |
| **Criterios de aceptación** | - La entidad `Producto` tiene un campo `imagenUrl` de tipo `String`, opcional (nullable). <br>- El campo aparece en el DTO de respuesta de producto. <br>- La columna se agrega via migracion Flyway `V2__add_producto_imagen.sql` (NO via `ddl-auto=update`). |
| **Notas** | `ddl-auto=update` debe estar deshabilitado tras EN-058 (Flyway). Toda modificacion de schema usa migraciones Flyway versionadas. |

---

#### TT-045 — Alinear el enum `TipoDocumento` entre Java y PostgreSQL

| Campo | Detalle |
|-------|---------|
| **Historia** | Como desarrollador de BD, quiero asegurar que los valores del enum `TipoDocumento` en Java y en PostgreSQL sean exactamente los mismos, para evitar errores al insertar o leer usuarios. |
| **Responsable** | BD |
| **Prioridad** | 🔴 Must have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | — |
| **Criterios de aceptación** | - El enum PostgreSQL `tipo_documento_enum` incluye todos los valores del enum Java: CC, TI, CE, PASAPORTE, NIT, RUT, OTRO. <br>- O bien el enum Java se reduce a solo los valores que tiene PostgreSQL. <br>- El equipo decide cuál de las dos opciones aplicar y lo documenta. |
| **Notas** | Actualmente Java tiene 7 valores y PostgreSQL solo tiene 4. Es una inconsistencia que causará errores en producción. |

---

### Épica 12 — Integración con Pasarela de Pagos

**Descripción:** Conectar el checkout con Wompi sandbox para procesar pagos reales en un entorno de prueba. Incluye el flujo completo: iniciar intento, redirigir al usuario, recibir webhook de confirmacion, reconciliar con la factura y aislar la API externa via anticorrupcion layer.

**¿Por qué es necesaria?** El plan define un MVP academico con pagos reales en sandbox. Sin esta epica el checkout solo simula el pago localmente, lo que no cumple el objetivo de aprender integracion con pasarelas reales.

---

#### HU-046 — Iniciar un intento de pago con Wompi

| Campo | Detalle |
|-------|---------|
| **Historia** | Como cliente, quiero iniciar el pago de mi carrito a traves de Wompi sandbox, para completar mi compra con un metodo de pago real. |
| **Responsable** | Backend |
| **Prioridad** | 🟡 Should have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-027 |
| **Criterios de aceptación** | - El endpoint `POST /api/pagos/iniciar` crea un registro `IntentoPago` en estado PENDIENTE. <br>- Retorna la URL de redireccion de Wompi sandbox para que el frontend redirija al usuario. <br>- El `IntentoPago` tiene: idCarrito, monto, moneda (COP), referencia unica y timestamp. |
| **Notas** | Wompi sandbox no requiere tarjeta real. Ver ADR-0003. |

---

#### TT-047 — Recibir webhook de confirmacion de Wompi

| Campo | Detalle |
|-------|---------|
| **Historia** | Como sistema, quiero recibir el webhook de Wompi cuando el pago se confirma o rechaza, para actualizar el estado del intento de pago de forma automatica. |
| **Responsable** | Backend |
| **Prioridad** | 🟡 Should have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-027 |
| **Criterios de aceptación** | - El endpoint `POST /api/pagos/webhook` es publico pero valida la firma HMAC de Wompi. <br>- Si el evento es APPROVED, el `IntentoPago` pasa a APROBADO y se dispara la generacion de factura. <br>- Si el evento es DECLINED o ERROR, el `IntentoPago` pasa a RECHAZADO y el carrito vuelve a CON_PRODUCTOS. <br>- El webhook es idempotente: registros duplicados del mismo `transaction_id` se ignoran (tabla `processed_webhook(transaction_id UNIQUE)`). |
| **Notas** | Sin idempotencia, Wompi puede enviar el mismo evento mas de una vez y se generaria una factura duplicada. |

---

#### TT-048 — Reconciliar IntentoPago con la Factura

| Campo | Detalle |
|-------|---------|
| **Historia** | Como sistema, quiero vincular el `IntentoPago` aprobado con la `Factura` generada, para tener trazabilidad completa del ciclo de vida de un pago. |
| **Responsable** | Backend |
| **Prioridad** | 🟠 Could have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-027 |
| **Criterios de aceptación** | - La `Factura` tiene un campo `intentoPagoId` que referencia al `IntentoPago`. <br>- La transaccion de Flyway crea la factura y actualiza el `IntentoPago` atomicamente. <br>- El endpoint `GET /api/facturas/{id}` incluye el `intentoPagoId` en la respuesta. |
| **Notas** | — |

---

#### HU-049 — Manejar pagos rechazados o fallidos

| Campo | Detalle |
|-------|---------|
| **Historia** | Como cliente, quiero recibir retroalimentacion clara cuando mi pago es rechazado, para poder intentarlo nuevamente o elegir otro metodo. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟡 Should have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-027 |
| **Criterios de aceptación** | - Si el pago es rechazado, el carrito vuelve a estado CON_PRODUCTOS. <br>- El frontend muestra el motivo del rechazo (INSUFFICIENT_FUNDS, INVALID_CARD, etc.). <br>- El cliente puede reintentar el pago sin perder los items del carrito. |
| **Notas** | — |

---

#### TT-050 — Capa anticorrupcion para Wompi (ACL)

| Campo | Detalle |
|-------|---------|
| **Historia** | Como desarrollador Backend, quiero aislar la integracion con Wompi detras de un puerto y un adaptador, para poder cambiar de pasarela en el futuro sin modificar la logica de dominio. |
| **Responsable** | Backend |
| **Prioridad** | 🟠 Could have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | — |
| **Criterios de aceptación** | - Existe una interfaz `PasarelaPort` en el paquete de dominio con metodos `iniciarPago` y `validarWebhook`. <br>- La implementacion `WompiAdapter` esta en el paquete `infrastructure/pago`. <br>- Los tests del dominio usan un mock de `PasarelaPort`, no la clase de Wompi directamente. |
| **Notas** | Patron Hexagonal. Ver ADR-0001 y ADR-0003. |

---

### Épica 13 — DevOps e Infraestructura

**Descripcion:** Automatizar el ciclo de vida del proyecto: contenerizacion, migraciones versionadas, perfiles de ambiente, CI/CD y observabilidad basica.

**¿Por que es necesaria?** Sin esta epica el proyecto solo funciona en la maquina de quien lo desarrollo. Los nuevos integrantes no pueden levantar el entorno en menos de 30 minutos, y cualquier cambio de esquema puede romper la base de datos silenciosamente.

---

#### EN-055 — Dockerfiles para backend y frontend

| Campo | Detalle |
|-------|---------|
| **Historia** | Como desarrollador, quiero que el backend y el frontend tengan Dockerfiles, para poder ejecutar el proyecto en cualquier maquina sin instalar Java, Maven o Node manualmente. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟠 Could have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | — |
| **Criterios de aceptación** | - `app/backend/tiendaq/Dockerfile` construye la imagen del backend (multi-stage: build Maven + runtime JRE). <br>- `app/frontend/Dockerfile` construye la imagen del frontend (multi-stage: build Angular + Nginx). <br>- Ambas imagenes construyen sin errores con `docker build`. |
| **Notas** | — |

---

#### EN-056 — docker-compose para entorno local completo

| Campo | Detalle |
|-------|---------|
| **Historia** | Como desarrollador nuevo, quiero levantar todo el entorno local con un solo comando, para empezar a contribuir en menos de 30 minutos. |
| **Responsable** | Backend + BD |
| **Prioridad** | 🟠 Could have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | — |
| **Criterios de aceptación** | - `docker-compose.yml` levanta: backend, frontend, PostgreSQL y Mailhog (SMTP local). <br>- `docker compose up` levanta todo sin configuracion adicional. <br>- Las variables de entorno estan en `.env.example` con valores por defecto para local. |
| **Notas** | — |

---

#### EN-057 — Pipeline CI/CD con GitHub Actions

| Campo | Detalle |
|-------|---------|
| **Historia** | Como equipo, quiero que cada PR ejecute automaticamente los tests y el build, para detectar errores antes de mergear a develop. |
| **Responsable** | Backend |
| **Prioridad** | 🟠 Could have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | — |
| **Criterios de aceptación** | - Existe `.github/workflows/ci.yml` con jobs: lint → test → build. <br>- Los tests usan Testcontainers (PostgreSQL real, no H2 embebido). <br>- El pipeline falla si alguna prueba falla. <br>- El pipeline pasa en menos de 5 minutos. |
| **Notas** | — |

---

#### EN-058 — Migrar esquema a Flyway

| Campo | Detalle |
|-------|---------|
| **Historia** | Como desarrollador de BD, quiero versionar el esquema con Flyway, para que cualquier cambio de base de datos sea reproducible, reversible y auditable. |
| **Responsable** | BD + Backend |
| **Prioridad** | 🔴 Must have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | — |
| **Criterios de aceptación** | - Existe `app/backend/tiendaq/src/main/resources/db/migration/V1__init.sql` con el esquema completo. <br>- `spring.jpa.hibernate.ddl-auto=none` en todos los perfiles. <br>- Al arrancar la aplicacion, Flyway ejecuta las migraciones pendientes automaticamente. <br>- `SCRIPTS_POSTGRES.sql` sigue siendo la fuente de referencia humana pero no se ejecuta en tiempo de arranque. |
| **Notas** | Ver ADR-0005. Sin Flyway, los cambios de esquema pueden perderse o romperse entre integrantes. |

---

#### EN-059 — Perfiles de ambiente (local/dev/staging)

| Campo | Detalle |
|-------|---------|
| **Historia** | Como desarrollador, quiero tener perfiles de ambiente separados, para que las credenciales de produccion nunca aparezcan en el codigo ni en el repositorio. |
| **Responsable** | Backend |
| **Prioridad** | 🟡 Should have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | — |
| **Criterios de aceptación** | - Existen `application-local.yml`, `application-dev.yml` y `application-staging.yml`. <br>- Las credenciales sensibles se leen de variables de entorno (`DB_USER`, `DB_PASSWORD`, `JWT_SECRET`, `WOMPI_KEY`). <br>- `.env.example` documenta todas las variables requeridas con valores de ejemplo seguros. |
| **Notas** | — |

---

#### EN-060 — Despliegue en staging (Render/Railway)

| Campo | Detalle |
|-------|---------|
| **Historia** | Como equipo, quiero un ambiente de staging accesible por URL publica, para poder demostrar el proyecto sin necesidad de correrlo localmente. |
| **Responsable** | Backend |
| **Prioridad** | 🟠 Could have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | — |
| **Criterios de aceptación** | - El backend esta desplegado en Render o Railway (free tier). <br>- La base de datos PostgreSQL esta provisionada en el mismo proveedor. <br>- La URL publica responde en `GET /actuator/health` con `{"status":"UP"}`. |
| **Notas** | Sin backups. No es produccion real. |

#### HU-061 — Confirmacion de pago en pantalla del cliente

| Campo | Detalle |
|-------|---------|
| **Historia** | Como cliente, quiero ver el resultado de mi pago luego de ser redirigido desde Wompi, para saber si mi compra fue exitosa o si debo intentarlo de nuevo. |
| **Responsable** | Frontend + Backend |
| **Prioridad** | 🟡 Should have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-027 |
| **Criterios de aceptación** | - Al volver de Wompi, el frontend consulta `GET /api/pagos/{id}` para obtener el estado del intento. <br>- Si APROBADO: pantalla de exito con resumen de factura y boton a historial. <br>- Si RECHAZADO: pantalla de error con motivo y boton de reintentar. <br>- La pantalla no depende solo de los parametros de URL (que pueden ser manipulados): siempre consulta el backend. |
| **Notas** | El webhook (TT-047) es el que realmente confirma el pago; esta US solo muestra el resultado al usuario. |

---


#### MX-051 — Captura basica de consentimiento al registro (educativo)

| Campo | Detalle |
|-------|---------|
| **Historia** | Como estudiante que aprende sobre Habeas Data, quiero implementar un checkbox de consentimiento al registrarse, para experimentar con los requisitos basicos de la Ley 1581/2012 en un contexto academico. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟠 Could have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | — |
| **Criterios de aceptación** | - El formulario de registro incluye un checkbox "Acepto la politica de privacidad" (obligatorio). <br>- El backend registra `consentimiento_fecha` y `consentimiento_ip` en el usuario. <br>- El endpoint de registro rechaza con 400 si el campo no es `true`. |
| **Notas** | Solo educativo. El proyecto no saldra a produccion real. Las obligaciones formales completas de la ley (anonimizacion, exportacion) quedan fuera del alcance del MVP. |

---


#### EN-062 — Observabilidad basica con Actuator

| Campo | Detalle |
|-------|---------|
| **Historia** | Como desarrollador, quiero que el backend exponga endpoints de salud y metricas, para poder monitorear el estado del sistema sin acceder directamente al servidor. |
| **Responsable** | Backend |
| **Prioridad** | 🟡 Should have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | — |
| **Criterios de aceptación** | - `GET /actuator/health` retorna `{"status":"UP"}` cuando todo esta bien. <br>- `GET /actuator/info` retorna version y nombre del proyecto. <br>- Los logs se emiten en formato JSON (Logback) con nivel configurable por ambiente. <br>- Los endpoints de actuator estan protegidos y solo son accesibles con rol ADMINISTRADOR (excepto `/health`). |
| **Notas** | Sin Grafana, Prometheus ni Loki para el MVP. |

---


#### EN-063 — Documentacion de la API con OpenAPI y Swagger UI

| Campo | Detalle |
|-------|---------|
| **Historia** | Como desarrollador, quiero que la API este documentada automaticamente con OpenAPI, para que cualquier integrante pueda explorar y probar los endpoints sin necesidad de Postman. |
| **Responsable** | Backend |
| **Prioridad** | 🟡 Should have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RNF-020 |
| **Criterios de aceptación** | - La dependencia `springdoc-openapi-starter-webmvc-ui` esta en el `pom.xml`. <br>- `GET /swagger-ui.html` muestra la UI de Swagger con todos los endpoints documentados. <br>- `GET /v3/api-docs` retorna el YAML de OpenAPI. <br>- El archivo `docs/api/openapi.yaml` esta commiteado y se actualiza con cada cambio de la API. |
| **Notas** | El archivo commiteado es la fuente de verdad para generar colecciones de Postman. |

---


#### EN-064 — Rate limit en endpoints criticos con Bucket4j

| Campo | Detalle |
|-------|---------|
| **Historia** | Como sistema, quiero limitar la cantidad de intentos en los endpoints de autenticacion y checkout, para prevenir ataques de fuerza bruta y abuso del servicio. |
| **Responsable** | Backend |
| **Prioridad** | 🟡 Should have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | — |
| **Criterios de aceptación** | - Limite de 5 intentos de login por IP en 15 minutos (retorna 429 al exceder). <br>- Limite de 3 registros por IP por hora. <br>- Limite de 10 inicios de checkout por IP por hora. <br>- El rate limit se implementa como filtro Spring (Bucket4j) en la cadena: `CorsFilter → RateLimitFilter → JwtAuthFilter`. |
| **Notas** | Ver ADR del rate limit en `docs/adr/`. |

---


#### HU-065 — Notificaciones por correo al confirmar factura

| Campo | Detalle |
|-------|---------|
| **Historia** | Como cliente, quiero recibir un correo con el resumen de mi factura al completar una compra, para tener constancia del pedido sin necesitar acceder a la aplicacion. |
| **Responsable** | Backend |
| **Prioridad** | 🟠 Could have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | — |
| **Criterios de aceptación** | - Al generar la factura, el sistema envia un correo al cliente con: numero de factura, fecha, total y lista de productos. <br>- El envio se realiza de forma asincrona (no bloquea la respuesta al cliente). <br>- En ambiente local, el correo se captura con Mailhog (en docker-compose). <br>- En staging se usa Gmail SMTP (`kforge.dev@gmail.com` + App Password). <br>- La implementacion usa un puerto `NotificacionPort` con un adaptador `GmailSmtpAdapter` intercambiable. |
| **Notas** | Patron Hexagonal: el dominio no conoce Gmail. El adaptador se configura por ambiente. |

---


#### TT-066 — Auditoria de acciones criticas

| Campo | Detalle |
|-------|---------|
| **Historia** | Como administrador, quiero que las acciones criticas (crear usuario, eliminar producto, generar factura) queden registradas en un log de auditoria, para poder rastrear quien hizo que y cuando. |
| **Responsable** | Backend |
| **Prioridad** | 🟠 Could have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | — |
| **Criterios de aceptación** | - Existe la tabla `audit_log(id, usuario_id, accion, entidad, entidad_id, antes, despues, fecha)`. <br>- Las acciones CREATE, UPDATE, DELETE en Usuario, Producto, Factura y StockEntry quedan registradas. <br>- El campo `antes` y `despues` guardan el estado en JSON. <br>- El registro es inmutable: no hay endpoint de eliminacion de audit_log. |
| **Notas** | La auditoria no reemplaza a los logs del sistema; los complementa con contexto de negocio. |

---


#### TT-067 — Reservas de stock durante el checkout

| Campo | Detalle |
|-------|---------|
| **Historia** | Como sistema, quiero reservar el stock de los productos cuando un cliente inicia el checkout, para evitar que dos clientes compren el mismo articulo al mismo tiempo. |
| **Responsable** | Backend |
| **Prioridad** | 🟡 Should have |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | RF-032 |
| **Criterios de aceptación** | - Al iniciar checkout, se crean registros en `stock_reservation(id, producto_id, carrito_id, cantidad, expires_at)`. <br>- El `expires_at` se fija a 10 minutos desde la creacion. <br>- Un job `@Scheduled` libera las reservas expiradas cada minuto. <br>- Al confirmar la factura, las reservas se convierten en descuento real de stock. <br>- Si no hay stock disponible (considerando reservas activas de otros carritos), el checkout retorna 409. |
| **Notas** | Sin reservas hay race condition: dos clientes pueden confirmar el pago del mismo ultimo articulo. Ver ADR-0010. |

---


#### TT-068 — Refactor a Hexagonal+DDD (epica, partida en sub-items)

> Este item se partio en TT-068a, TT-068b y TT-068c por tamano (probable >21 pts: 7 bounded contexts). El item original TT-068 queda como referencia de la epica completa.

#### TT-068a — Estructura hexagonal base + bounded context Identidad

| Campo | Detalle |
|-------|---------|
| **Historia** | Como equipo de desarrollo, quiero crear la estructura hexagonal base y migrar el bounded context Identidad, para establecer la convencion arquitectonica que seguiran los demas contextos. |
| **Responsable** | Backend |
| **Prioridad** | 🔴 Must have (Sprint 1 — bloqueante absoluto) |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | — |
| **Criterios de aceptación** | - Crear estructura `com.tiendaq.<contexto>.{domain,application,infrastructure}`. <br>- Migrar Usuario, Cliente, Empleado a `com.tiendaq.identidad`. <br>- Crear puertos: `UsuarioRepositoryPort`, `TokenPort`, `NotificacionPort`. <br>- Tests de dominio se ejecutan sin Spring Boot. <br>- `domain/` no tiene dependencias de Spring ni JPA. |
| **Notas** | Ver ADR-0001. Bloqueante absoluto antes de cualquier otro context migration o HU. |

---

#### TT-068b — Migrar Catalogo + Inventario

| Campo | Detalle |
|-------|---------|
| **Historia** | Como equipo de desarrollo, quiero migrar los bounded contexts Catalogo e Inventario a la estructura hexagonal, para aislar la logica de productos y stock del resto del sistema. |
| **Responsable** | Backend |
| **Prioridad** | 🔴 Must have (Sprint 2 candidato) |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | — |
| **Criterios de aceptación** | - Mover Producto, Categoria a `com.tiendaq.catalogo`. <br>- Mover StockLevel, StockEntry, StockReservation a `com.tiendaq.inventario`. <br>- Crear puertos `ProductoRepositoryPort`, `StockReservationPort`. <br>- Use cases de creacion/actualizacion/consulta de productos quedan en `application/`. |
| **Notas** | Depende de TT-068a. |

---

#### TT-068c — Migrar Carrito + Pedidos + Pagos + Reportes

| Campo | Detalle |
|-------|---------|
| **Historia** | Como equipo de desarrollo, quiero migrar los bounded contexts restantes (Carrito, Pedidos, Pagos, Reportes) a la estructura hexagonal, para completar el refactor arquitectonico. |
| **Responsable** | Backend |
| **Prioridad** | 🔴 Must have (Sprint 2 o 3 candidato) |
| **Estado** | 📋 Por hacer |
| **RF relacionado** | — |
| **Criterios de aceptación** | - Mover Carrito + ItemCarrito a `com.tiendaq.carrito`. <br>- Mover Factura + DetalleFactura a `com.tiendaq.pedidos`. <br>- Mover IntentoPago + WompiPagoAdapter a `com.tiendaq.pagos` (con `PagoPort` en domain). <br>- Crear `com.tiendaq.reportes` (read-models, sin aggregate propio). |
| **Notas** | Depende de TT-068a y TT-068b. Ultimo paso del refactor. |

---

## Resumen General

### Por épica

| Épica | Historias | Must have 🔴 | Should have 🟡 | Could have 🟠 |
|-------|-----------|------------|---------|---------|
| 1 — Configuración base | 6 | 6 | 0 | 0 |
| 2 — Autenticación | 6 | 4 | 2 | 0 |
| 3 — Gestión de productos | 4 | 3 | 1 | 0 |
| 4 — Catálogo y búsqueda | 3 | 1 | 2 | 0 |
| 5 — Carrito de compras | 6 | 6 | 0 | 0 |
| 6 — Checkout y facturación | 6 | 4 | 2 | 0 |
| 7 — Inventario | 2 | 1 | 1 | 0 |
| 8 — Gestión de usuarios | 4 | 0 | 3 | 1 |
| 9 — Perfil de usuario | 2 | 0 | 2 | 0 |
| 10 — Reportes | 3 | 0 | 0 | 3 |
| 11 — Deuda técnica | 3 | 2 | 1 | 0 |
| **Total** | **45** | **27** | **14** | **4** |

### Por responsable

| Responsable | Historias asignadas |
|-------------|---------------------|
| Backend | 18 historias solo / compartidas |
| Frontend | 15 historias solo / compartidas |
| BD | 4 historias solo / compartidas |
| Todos | 1 historia |

---

## Propuesta de Sprints

> **Estado:** PENDIENTE DE PLANNING POKER. La asignacion de USs a sprints **no esta hecha**. Solo Sprint 1 esta fijado (USs fundacionales, no negociables). Sprints 2-8 se asignan **despues** de la sesion de planning poker conjunta.
>
> **Por que no estan asignados:** Estimar capacidad de sprint requiere conocer puntos por US. Los puntos se obtienen del planning poker. Asignar USs a sprints sin estimates concretos lleva a sobrecargar o subcargar sprints. Es lo opuesto a planeacion agil.

### Capacity teorica del sprint

| Parametro | Valor |
|-----------|-------|
| Duracion | 2 semanas |
| Devs activos | 3 (Brian, Camilo, Aleja, Miguel — Brian PO con dedicacion menor) |
| Horas/dev/semana | 10 h |
| **Capacity bruta** | **60 h/sprint** |

Capacity bruta no es lo mismo que capacity efectiva. La capacity real se ajusta cada sprint segun:
- Velocity observada en el sprint anterior (puntos completados / capacity bruta).
- Imprevistos (parciales, semana de exposiciones, etc.).
- Compromiso real del equipo en planning.

**Primer sprint:** sin velocity historica. Se compromete conservador (~70% de capacity bruta = ~42 h reales) para no sobreprometer.

---

### Sprint 1 — Fundamentos de codigo (FIJO, no requiere poker)

**Objetivo:** Base tecnica irrenunciable antes de que el equipo pueda construir features. Sin esto, cada PR puede romper el esquema de otro.

**Por que va fijo sin planning poker:** Estas USs son must-have de fundacion. No pueden saltarse, no pueden esperar, no compiten con otras prioridades. Son criterio de entrada para que cualquier feature posterior tenga sentido.

| ID | Historia | Responsable | Estimacion (pts) |
|----|----------|-------------|-----------------|
| EN-058 | Migrar esquema a Flyway | BD + Backend | A estimar en poker |
| TT-068 | Refactorizar a arquitectura Hexagonal | Backend | A estimar en poker |
| TT-005 | Corregir mapeo JPA (BigDecimal, JOINED, enums, indices) | BD + Backend | A estimar en poker |
| TT-002 | Implementar DTOs para todas las entidades | Backend | A estimar en poker |

**Nota:** Los puntos de estas USs igualmente se estiman en el planning poker para calibrar la escala del equipo y obtener una primera medicion de velocity al cierre del Sprint 1.

---

### Sprints 2-8 — Pool de USs sin asignar

**Listas separadas por prioridad MoSCoW.** Asignar a sprints despues del planning poker.

#### Pool Must have (asignar primero)

EN-001, EN-003, TT-004, EN-006, HU-007, HU-008, TT-009, TT-010, HU-011, HU-013, HU-014, HU-015, HU-016, HU-019, HU-020, HU-022, HU-023, TT-024, TT-025, HU-026, HU-027, HU-028, TT-029, HU-031, TT-043, TT-044, TT-045, HU-046, TT-047, EN-055, TT-067, EN-059.

#### Pool Should have (asignar despues de cubrir Must)

HU-012, HU-017, HU-018, HU-021, HU-030, HU-032, HU-033, HU-034, HU-036, HU-037, HU-038, HU-039, HU-040, HU-041, HU-042, EN-062, EN-063.

#### Pool Could have (asignar segun capacidad)

HU-035, TT-048, HU-049, TT-050, MX-051, EN-056, EN-057, EN-060, HU-061, EN-064, HU-065, TT-066.

---

## Como hacer planning poker correctamente

### Preparacion (antes de la sesion)

1. **Confirmar Sprint 1.** USs TT-002, TT-005, EN-058, TT-068 quedan en Sprint 1 sin discusion. Solo se estiman puntos.
2. **Acordar la escala de Fibonacci.** Convencion del equipo: 1, 2, 3, 5, 8, 13, 21. Mas de 21 = US demasiado grande, hay que partirla.
3. **Definir referencia.** Elegir 1 US "ancla" que el equipo entienda completamente y asignarle un valor (ej. EN-001 = 3 pts). Todas las demas se comparan contra ella.
4. **Tener BACKLOG.md a mano.** Cada miembro lee la US (descripcion + criterios de aceptacion) antes de votar.
5. **Cada miembro vota en privado.** App de poker (Planning Poker Online, Scrum Poker, etc.) o cartas fisicas. Nada de "yo voto X" en voz alta primero — sesga al resto.

### Durante la sesion

1. **Lectura del Product Owner.** Brian lee la US. Resuelve dudas de scope.
2. **Discusion tecnica corta.** Maximo 3 minutos. Cada uno comenta riesgos, dependencias, complejidad.
3. **Voto simultaneo.** Todos revelan sus cartas a la vez.
4. **Si hay consenso (todos misma carta o adyacentes):** se toma la mediana o se acuerda el numero. Pasar a siguiente US.
5. **Si hay divergencia (ej. uno vota 2, otro vota 13):** los extremos explican su razonamiento. Re-vote. Si persiste, se elige el numero mas alto (mas conservador, mas seguro).
6. **Registrar puntos en BACKLOG.md.** Llenar la columna "Estimacion (pts)" de cada US estimada.

### Despues de la sesion — asignar a sprints

1. **Calcular puntos por sprint** = capacity bruta (60 h) × factor de conversion. Sin velocity historica, asumir factor conservador: **30-40 pts/sprint** para Sprint 2 (primer sprint con USs ya estimadas).
2. **Llenar Sprint 2** con items Must have hasta llegar al limite de puntos. Respetar dependencias (item con `dep: HU-XXX` o `dep: TT-XXX` requiere que la dependencia este en Sprint anterior o mismo).
3. **Repetir para Sprints 3-8** con el resto del Pool Must have, luego Pool Should have, finalmente Pool Could have segun quepa.
4. **Re-evaluar cada sprint.** Al cerrar Sprint N, calcular velocity real (pts completados / pts comprometidos). Ajustar capacity de Sprint N+1.

### Reglas de oro

- No mover USs de Sprint en curso a otro Sprint sin retro. Si no caben, se sacan al backlog.
- US con mas de 13 pts → partirla. No ejecutar USs gigantes en un solo sprint.
- Si un dev no entiende la US lo suficiente para votar → la US no esta lista, no se estima. Volver al refinement.
- Capacity efectiva real ≠ capacity bruta. Esperar 30-50% menos en los primeros sprints hasta calibrar.

---


## Registro de Cambios

| Versión | Fecha | Autor | Cambio |
|---------|-------|-------|--------|
| 1.0 | Abril 2026 | K-Forge | Creación inicial del backlog |

---

> **Cómo usar este documento:**
> 1. El Product Owner revisa y ajusta las prioridades antes de cada Sprint.
> 2. El Scrum Master conduce el Sprint Planning tomando las historias de mayor prioridad.
> 3. Cada desarrollador actualiza el estado de sus historias a medida que avanza.
> 4. Al finalizar cada Sprint se actualiza el `Registro de Cambios`.
>
> **Referencia:** Para el detalle técnico de cada funcionalidad, consultar `docs/REQUIREMENTS.md` (SRS) y `docs/DESIGN.md` (SDD).
