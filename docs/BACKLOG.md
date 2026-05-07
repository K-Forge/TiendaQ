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
- [Convención de Historias de Usuario](#convención-de-historias-de-usuario)
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

## Convención de Historias de Usuario

Cada historia de usuario sigue el formato estándar de Scrum:

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
| 📋 Ready | Aún no se ha iniciado |
| 🔄 In progress | Alguien está trabajando en ella actualmente |
| 👁️ In review | Terminada, esperando revisión del equipo |
| ✅ Done | Completada y aceptada por el Product Owner |

---

## Épicas e Historias de Usuario

---

### Épica 1 — Configuración Base del Proyecto

**Descripción:** Establecer la infraestructura técnica mínima antes de desarrollar cualquier funcionalidad. Sin esta base, el resto del equipo no puede trabajar de forma segura ni coordinada.

---

#### US-001 — Estructura de carpetas y convenciones del proyecto

| Campo | Detalle |
|-------|---------|
| **Historia** | Como equipo, quiero tener definidas las convenciones de código y la estructura de carpetas del proyecto, para que todos trabajemos de forma coherente sin pisarnos. |
| **Responsable** | Todos (revisar AGENTS.md y la guia org-level CONTRIBUTING) |
| **Prioridad** | 🔴 Must have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | — |
| **Criterios de aceptación** | - Las convenciones de commits (Conventional Commits) están documentadas y todos las siguen. <br>- La estructura de ramas Git Flow está activa (`main`, `develop`, `feature/*`). <br>- El equipo acordó cómo nombrar branches, clases y métodos. |
| **Notas** | La guia de contribucion vive en el repo org-level [`K-Forge/.github/blob/main/CONTRIBUTING.md`](https://github.com/K-Forge/.github/blob/main/CONTRIBUTING.md). El `AGENTS.md` local complementa con contexto especifico de TiendaQ. |

---

#### US-002 — Implementar DTOs para todas las entidades del Backend

| Campo | Detalle |
|-------|---------|
| **Historia** | Como desarrollador Backend, quiero crear DTOs (objetos de transferencia de datos) para cada entidad, para que la API nunca exponga directamente las entidades JPA ni datos sensibles como contraseñas. |
| **Responsable** | Backend |
| **Prioridad** | 🔴 Must have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RNF-019 |
| **Criterios de aceptación** | - Existe un DTO de request y uno de response para cada entidad (Usuario, Producto, Carrito, etc.). <br>- Ningún endpoint retorna directamente una entidad JPA. <br>- El campo `contrasena` nunca aparece en ninguna respuesta de la API. |
| **Notas** | Actualmente todos los controllers exponen entidades JPA directamente. Esto es un problema de seguridad crítico. |

---

#### US-003 — Configurar Spring Security con JWT

| Campo | Detalle |
|-------|---------|
| **Historia** | Como desarrollador Backend, quiero configurar Spring Security con autenticación JWT, para que todos los endpoints estén protegidos y solo los usuarios autorizados puedan acceder. |
| **Responsable** | Backend |
| **Prioridad** | 🔴 Must have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-002, RF-003, RF-004 |
| **Criterios de aceptación** | - Existe un `SecurityFilterChain` configurado. <br>- Los endpoints públicos (login, registro) no requieren token. <br>- Los endpoints protegidos retornan 401 si no hay token válido. <br>- Los endpoints de administrador retornan 403 si el rol no es suficiente. |
| **Notas** | La dependencia de Spring Security ya está en el `pom.xml`, pero sin configuración activa. |

---

#### US-004 — Implementar manejo global de errores en el Backend

| Campo | Detalle |
|-------|---------|
| **Historia** | Como desarrollador Backend, quiero un manejador global de excepciones, para que todos los errores de la API tengan un formato JSON consistente y no expongan detalles internos del sistema. |
| **Responsable** | Backend |
| **Prioridad** | 🔴 Must Have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-058, RF-059 |
| **Criterios de aceptación** | - Existe una clase `@ControllerAdvice` que captura excepciones. <br>- Todos los errores retornan JSON con: `timestamp`, `status`, `error`, `message`, `path`. <br>- Ningún error expone stack traces ni nombres de tablas de la base de datos. |
| **Notas** | El paquete `exception/` existe en el proyecto pero está vacío. |

---

#### US-005 — Verificar y sincronizar el esquema de BD con las entidades JPA

| Campo | Detalle |
|-------|---------|
| **Historia** | Como desarrollador de BD, quiero verificar que el esquema PostgreSQL esté perfectamente alineado con las entidades JPA, para evitar errores de mapeo al ejecutar la aplicación. |
| **Responsable** | BD |
| **Prioridad** | 🔴 Must Have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | — |
| **Criterios de aceptación** | - Los tipos ENUM en PostgreSQL coinciden exactamente con los enums de Java. <br>- Los nombres de columnas en SQL coinciden con los mapeados en `@Column`. <br>- El enum `TipoDocumento` en Java (que tiene NIT, RUT, OTRO) está alineado con el tipo PostgreSQL (que actualmente no los tiene). |
| **Notas** | Inconsistencia detectada: `TipoDocumento` en Java tiene valores que no están en el ENUM de PostgreSQL. |

---

#### US-006 — Configurar la estructura base del Frontend Angular

| Campo | Detalle |
|-------|---------|
| **Historia** | Como desarrollador Frontend, quiero estructurar el proyecto Angular con las carpetas, servicios base e interceptores HTTP necesarios, para tener una base sólida sobre la que construir todas las pantallas. |
| **Responsable** | Frontend |
| **Prioridad** | 🔴 Must Have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-072 |
| **Criterios de aceptación** | - Existen las carpetas: `pages/`, `components/`, `services/`, `guards/`, `interceptors/`, `models/`. <br>- Hay un interceptor HTTP que agrega el token JWT a todas las peticiones. <br>- Hay un interceptor de errores que captura respuestas 401 y 403. <br>- Las rutas base están configuradas en `app.routes.ts`. <br>- Existe un ambiente (`environment.ts`) con la URL base de la API. |
| **Notas** | Actualmente `app.routes.ts` está vacío y no hay ninguna estructura de carpetas definida. |

---

### Épica 2 — Autenticación y Autorización

**Descripción:** Todo lo relacionado con el ingreso al sistema, la identificación del usuario y el control de acceso según su rol.

**¿Por qué va después de la configuración base?** Porque sin DTOs y sin Spring Security configurado, implementar el login no tendría sentido: los datos quedarían expuestos y cualquiera podría acceder a todo.

---

#### US-007 — Registro de nuevo cliente

| Campo | Detalle |
|-------|---------|
| **Historia** | Como usuario nuevo, quiero registrarme con mis datos personales (nombre, apellido, documento, teléfono, correo, dirección y contraseña), para crear mi cuenta y poder hacer compras. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🔴 Must Have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-001 |
| **Criterios de aceptación** | - El endpoint `POST /api/auth/registro` crea un Usuario y un Cliente en una sola transacción. <br>- La contraseña se almacena con hash BCrypt (nunca en texto plano). <br>- Si el correo, documento o teléfono ya existen, retorna 409 con mensaje claro. <br>- El formulario de Angular valida los campos en tiempo real antes de enviar. <br>- La respuesta nunca incluye la contraseña. |
| **Notas** | Actualmente la creación de usuario y cliente son operaciones separadas y las contraseñas no se hashean. |

---

#### US-008 — Inicio de sesión

| Campo | Detalle |
|-------|---------|
| **Historia** | Como usuario registrado, quiero iniciar sesión con mi correo y contraseña, para acceder al sistema con las funciones que corresponden a mi rol. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🔴 Must Have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-002 |
| **Criterios de aceptación** | - El endpoint `POST /api/auth/login` retorna un token JWT válido al ingresar credenciales correctas. <br>- El token contiene el ID del usuario, su correo y su rol. <br>- Si las credenciales son incorrectas, retorna 401 con mensaje genérico (no indicar si el error es en el correo o en la contraseña, por seguridad). <br>- El frontend almacena el token de forma segura y redirige al usuario según su rol. |
| **Notas** | —  |

---

#### US-009 — Validación automática del token JWT

| Campo | Detalle |
|-------|---------|
| **Historia** | Como sistema, quiero validar el token JWT en cada petición a un endpoint protegido, para garantizar que solo usuarios autenticados accedan a los recursos. |
| **Responsable** | Backend |
| **Prioridad** | 🔴 Must Have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-003 |
| **Criterios de aceptación** | - Las peticiones sin token retornan 401. <br>- Las peticiones con token expirado retornan 401. <br>- Las peticiones con token válido pero rol insuficiente retornan 403. <br>- Los tokens tienen una expiración configurable (por defecto 24 horas). |
| **Notas** | — |

---

#### US-010 — Autorización por roles

| Campo | Detalle |
|-------|---------|
| **Historia** | Como sistema, quiero restringir el acceso a cada endpoint según el rol del usuario autenticado, para que un cliente no pueda hacer lo que solo puede hacer un empleado o administrador. |
| **Responsable** | Backend |
| **Prioridad** | 🔴 Must Have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-004 |
| **Criterios de aceptación** | - Los endpoints de gestión de productos y stock solo son accesibles por VENDEDOR y ADMINISTRADOR. <br>- Los endpoints de gestión de usuarios solo son accesibles por ADMINISTRADOR. <br>- Los endpoints de carrito, perfil e historial son accesibles por CLIENTE. <br>- El rol ADMINISTRADOR hereda los permisos de VENDEDOR. |
| **Notas** | — |

---

#### US-011 — Cierre de sesión

| Campo | Detalle |
|-------|---------|
| **Historia** | Como usuario autenticado, quiero poder cerrar sesión, para que mi sesión quede invalidada y nadie más pueda usarla desde el mismo dispositivo. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟡 Should have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-005 |
| **Criterios de aceptación** | - Al hacer logout, el token queda invalidado. <br>- Peticiones posteriores con ese token retornan 401. <br>- El frontend elimina el token almacenado y redirige al login. |
| **Notas** | — |

---

#### US-012 — Recuperación de contraseña

| Campo | Detalle |
|-------|---------|
| **Historia** | Como usuario, quiero recuperar mi contraseña si la olvidé, para poder volver a acceder sin necesitar al administrador. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟡 Should Have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-006 |
| **Criterios de aceptación** | - El endpoint `POST /api/auth/recuperar` acepta un correo y genera un token temporal. <br>- El token temporal se registra en consola o en un endpoint dedicado (no se envía correo real). <br>- Con el token temporal el usuario puede establecer una nueva contraseña hasheada. |
| **Notas** | El envío real de correos está fuera del alcance del proyecto. |

---

### Épica 3 — Gestión de Productos

**Descripción:** Todo lo que los empleados necesitan para administrar el catálogo de productos: crear, editar, eliminar y listar.

---

#### US-013 — Crear un producto

| Campo | Detalle |
|-------|---------|
| **Historia** | Como empleado, quiero crear un producto con nombre, categoría, precio e imagen (URL), para que esté disponible en el catálogo de la tienda. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🔴 Must Have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-008 |
| **Criterios de aceptación** | - El endpoint `POST /api/productos` solo es accesible por VENDEDOR y ADMINISTRADOR. <br>- El producto requiere: nombre, categoría (enum), precio unitario positivo. <br>- La imagen es una URL externa opcional. <br>- El nombre debe ser único dentro de la misma categoría. <br>- El formulario de Angular valida todos los campos antes de enviar. |
| **Notas** | La entidad `Producto` actualmente no tiene campo `imagen`. Debe añadirse (ver US-044). |

---

#### US-014 — Editar un producto existente

| Campo | Detalle |
|-------|---------|
| **Historia** | Como empleado, quiero editar los datos de un producto existente, para corregir errores o actualizar el precio o la imagen. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🔴 Must Have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-011 |
| **Criterios de aceptación** | - El endpoint `PUT /api/productos/{id}` actualiza nombre, categoría, precio e imagen. <br>- Si el producto no existe, retorna 404. <br>- La validación de unicidad de nombre por categoría se mantiene al editar. |
| **Notas** | — |

---

#### US-015 — Eliminar un producto

| Campo | Detalle |
|-------|---------|
| **Historia** | Como empleado, quiero eliminar un producto que no tiene stock ni compras asociadas, para mantener el catálogo limpio y actualizado. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟡 Should Have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-012 |
| **Criterios de aceptación** | - Si el producto tiene stock o está en facturas, el endpoint retorna 409 con un mensaje descriptivo. <br>- Si se puede eliminar, retorna 204 (sin contenido). <br>- El frontend muestra un diálogo de confirmación antes de eliminar. |
| **Notas** | — |

---

#### US-016 — Listar productos con paginación y filtros

| Campo | Detalle |
|-------|---------|
| **Historia** | Como cualquier usuario, quiero ver la lista de productos con paginación, filtros por categoría y búsqueda por nombre, para encontrar lo que necesito sin ver todo el catálogo de golpe. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🔴 Must have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-010, RF-013 |
| **Criterios de aceptación** | - El endpoint `GET /api/productos` soporta los parámetros: `page`, `size`, `sortBy`, `direction`. <br>- El endpoint `GET /api/productos/categoria/{categoria}` filtra por categoría. <br>- La respuesta incluye metadatos de paginación: `totalElements`, `totalPages`, `currentPage`. <br>- El frontend muestra los productos en una grilla con controles de paginación. |
| **Notas** | Actualmente el endpoint no soporta paginación ni `Pageable`. |

---

### Épica 4 — Catálogo y Búsqueda

**Descripción:** Las funcionalidades de exploración del catálogo orientadas al cliente final: buscar por nombre, filtrar por precio y ver el detalle de un producto.

---

#### US-017 — Búsqueda de productos por nombre parcial

| Campo | Detalle |
|-------|---------|
| **Historia** | Como cliente, quiero buscar productos escribiendo solo parte del nombre, para encontrar lo que busco sin tener que escribirlo completo. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟡 Should have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-014 |
| **Criterios de aceptación** | - El endpoint acepta un parámetro `nombre` y hace búsqueda ILIKE (insensible a mayúsculas). <br>- Si no hay resultados, retorna lista vacía (no error). <br>- El parámetro debe tener al menos 2 caracteres. <br>- El frontend tiene un campo de búsqueda que se activa al escribir. |
| **Notas** | Requiere agregar `findByNombreContainingIgnoreCase` al repositorio. |

---

#### US-018 — Filtro de productos por rango de precio

| Campo | Detalle |
|-------|---------|
| **Historia** | Como cliente, quiero filtrar productos por rango de precio (mínimo y máximo), para ver solo los que puedo pagar. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟡 Should have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-015 |
| **Criterios de aceptación** | - El endpoint acepta parámetros `precioMin` y `precioMax` (ambos opcionales). <br>- Si solo se proporciona `precioMin`, filtra productos con precio ≥ precioMin. <br>- Si solo se proporciona `precioMax`, filtra productos con precio ≤ precioMax. <br>- El frontend tiene controles de rango de precio. |
| **Notas** | — |

---

#### US-019 — Ver detalle de un producto

| Campo | Detalle |
|-------|---------|
| **Historia** | Como cliente, quiero ver el detalle completo de un producto (nombre, categoría, precio, imagen y stock disponible), para decidir si lo compro. |
| **Responsable** | Frontend |
| **Prioridad** | 🔴 Must Have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-017 |
| **Criterios de aceptación** | - La pantalla de detalle consume el endpoint `GET /api/productos/{id}`. <br>- Si el stock es 0, se muestra la etiqueta "Agotado". <br>- Hay un botón para agregar al carrito (deshabilitado si está agotado). <br>- La imagen se muestra si existe URL; si no, se muestra un placeholder. |
| **Notas** | — |

---

### Épica 5 — Carrito de Compras

**Descripción:** El núcleo del e-commerce. El cliente agrega productos, los gestiona y el sistema mantiene la coherencia del inventario y los estados del carrito.

**¿Por qué es crítico?** Porque sin carrito no hay compras, y sin compras no hay facturas. Todo el flujo económico del sistema depende de esta épica.

---

#### US-020 — Agregar un producto al carrito

| Campo | Detalle |
|-------|---------|
| **Historia** | Como cliente, quiero agregar un producto a mi carrito indicando la cantidad deseada, para ir seleccionando lo que quiero comprar. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🔴 Must have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-019 |
| **Criterios de aceptación** | - Si el cliente no tiene carrito activo, el sistema lo crea automáticamente con estado VACÍO. <br>- Si el producto ya está en el carrito, se suma la cantidad. <br>- Si el stock es insuficiente, retorna 409 con la cantidad disponible. <br>- El precio unitario se registra al momento de agregar (no cambia aunque el producto se actualice después). <br>- El estado del carrito cambia a CON_PRODUCTOS. |
| **Notas** | — |

---

#### US-021 — Modificar la cantidad de un producto en el carrito

| Campo | Detalle |
|-------|---------|
| **Historia** | Como cliente, quiero cambiar la cantidad de un producto en mi carrito, para ajustar mi pedido antes de pagar. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🔴 Must Have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-020 |
| **Criterios de aceptación** | - Si la nueva cantidad es válida, se actualiza el item. <br>- Si la cantidad es 0, el item se elimina. <br>- Si el carrito queda vacío, el estado cambia a VACÍO. <br>- El sistema valida que el stock sea suficiente para la nueva cantidad. |
| **Notas** | — |

---

#### US-022 — Eliminar un producto del carrito

| Campo | Detalle |
|-------|---------|
| **Historia** | Como cliente, quiero eliminar un producto específico de mi carrito, para cambiar de opinión sobre ese artículo. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🔴 Must have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-021 |
| **Criterios de aceptación** | - El item se elimina del carrito. <br>- Si el carrito queda vacío, el estado cambia a VACÍO. <br>- El frontend muestra confirmación antes de eliminar. |
| **Notas** | — |

---

#### US-023 — Ver el carrito con totales calculados

| Campo | Detalle |
|-------|---------|
| **Historia** | Como cliente, quiero ver mi carrito con la lista de productos, el subtotal, el IVA (19%) y el total, para saber exactamente cuánto voy a pagar. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🔴 Must Have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-023 |
| **Criterios de aceptación** | - La respuesta incluye: lista de items, subtotal, IVA (19% del subtotal), total. <br>- Si el cliente no tiene carrito activo, retorna estructura vacía (no error). <br>- El carrito es accesible desde cualquier pantalla (componente lateral o modal). |
| **Notas** | — |

---

#### US-024 — Validación de stock en tiempo real

| Campo | Detalle |
|-------|---------|
| **Historia** | Como sistema, quiero validar el stock disponible cada vez que se agrega o modifica un item en el carrito, para no permitir vender más de lo que hay en inventario. |
| **Responsable** | Backend |
| **Prioridad** | 🔴 Must have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-024 |
| **Criterios de aceptación** | - Si el stock es insuficiente, la operación se rechaza con 409 indicando la cantidad disponible. <br>- La validación ocurre tanto al agregar como al modificar la cantidad. |
| **Notas** | — |

---

#### US-025 — Máquina de estados del carrito

| Campo | Detalle |
|-------|---------|
| **Historia** | Como sistema, quiero que el carrito siga una máquina de estados definida, para que el flujo de compra sea coherente y no se pueda saltar pasos. |
| **Responsable** | Backend |
| **Prioridad** | 🔴 Must have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-025 |
| **Criterios de aceptación** | - Las transiciones válidas son: VACÍO → CON_PRODUCTOS, CON_PRODUCTOS → VACÍO, CON_PRODUCTOS → EN_PROCESO_DE_PAGO, EN_PROCESO_DE_PAGO → PAGO_PENDIENTE, PAGO_PENDIENTE → PAGO_EXITOSO, EN_PROCESO_DE_PAGO → CON_PRODUCTOS. <br>- Cualquier otra transición retorna 400 con mensaje descriptivo. |
| **Notas** | — |

---

### Épica 6 — Checkout y Facturación

**Descripción:** El proceso de pago y la generación de la factura. Este es el momento en que el carrito se convierte en una compra real.

---

#### US-026 — Iniciar el proceso de checkout

| Campo | Detalle |
|-------|---------|
| **Historia** | Como cliente, quiero iniciar el proceso de pago desde mi carrito, para formalizar mi compra. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🔴 Must have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-026 |
| **Criterios de aceptación** | - El sistema re-valida el stock de todos los productos del carrito al iniciar el checkout. <br>- Si hay stock insuficiente en algún producto, retorna 409 con el detalle. <br>- Si todo está bien, el estado del carrito cambia a EN_PROCESO_DE_PAGO. <br>- El frontend muestra un resumen del carrito antes de confirmar. |
| **Notas** | — |

---

#### US-027 — Seleccionar método de pago

| Campo | Detalle |
|-------|---------|
| **Historia** | Como cliente, quiero seleccionar el método de pago que prefiero (PSE, tarjeta de crédito, tarjeta débito, efectivo o transferencia), para completar mi compra. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🔴 Must have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-027 |
| **Criterios de aceptación** | - Los métodos disponibles son los definidos en el enum `MetodoPago`. <br>- Al seleccionar, el estado del carrito cambia a PAGO_PENDIENTE. <br>- El método seleccionado queda registrado para la factura. |
| **Notas** | No se realiza procesamiento real del pago. Solo se registra el método. |

---

#### US-028 — Generar factura al confirmar el pago

| Campo | Detalle |
|-------|---------|
| **Historia** | Como sistema, quiero generar una factura completa con el detalle de los productos comprados cuando el cliente confirma el pago, para tener un registro permanente e inmutable de la transacción. |
| **Responsable** | Backend |
| **Prioridad** | 🔴 Must have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-028 |
| **Criterios de aceptación** | - Se crea un registro en `Factura` con: fecha, subtotal, IVA (19%), total, método de pago, referencia al cliente, al empleado/sistema y al carrito. <br>- Se crean registros en `DetalleFactura` con el precio y cantidad de cada producto al momento de la compra. <br>- El estado del carrito cambia a PAGO_EXITOSO. <br>- Todo ocurre en una sola transacción atómica (si algo falla, todo se revierte). |
| **Notas** | Depende de US-044 (entidad `DetalleFactura`). |

---

#### US-029 — Descontar stock al facturar

| Campo | Detalle |
|-------|---------|
| **Historia** | Como sistema, quiero descontar automáticamente el stock de cada producto vendido al generar la factura, para que el inventario siempre refleje la realidad. |
| **Responsable** | Backend |
| **Prioridad** | 🔴 Must have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-032 |
| **Criterios de aceptación** | - El descuento de stock es parte de la misma transacción que genera la factura. <br>- Si el stock de algún producto es insuficiente en el momento exacto de facturar, toda la operación se revierte (rollback). <br>- El stock resultante nunca puede ser negativo. |
| **Notas** | — |

---

#### US-030 — Cancelar el proceso de checkout

| Campo | Detalle |
|-------|---------|
| **Historia** | Como cliente, quiero cancelar el proceso de pago si aún no lo he confirmado, para volver a mi carrito y seguir comprando o modificar mi pedido. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟡 Should have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-029 |
| **Criterios de aceptación** | - Solo se puede cancelar si el carrito está en estado EN_PROCESO_DE_PAGO. <br>- Al cancelar, el estado vuelve a CON_PRODUCTOS. <br>- Los items del carrito permanecen intactos. |
| **Notas** | — |

---

#### US-031 — Ver historial de facturas

| Campo | Detalle |
|-------|---------|
| **Historia** | Como cliente, quiero ver mi historial de compras con el detalle de cada factura, para llevar control de todo lo que he comprado. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟡 Should have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-030, RF-031 |
| **Criterios de aceptación** | - La lista está paginada y ordenada por fecha descendente (más reciente primero). <br>- Cada factura muestra: número, fecha, total y método de pago. <br>- Al hacer clic en una factura, se ve el detalle completo con productos, cantidades y precios. <br>- Un cliente solo puede ver sus propias facturas. |
| **Notas** | — |

---

### Épica 7 — Gestión de Inventario

**Descripción:** Control de existencias de los productos por parte de los empleados.

---

#### US-032 — Registrar ingreso de stock

| Campo | Detalle |
|-------|---------|
| **Historia** | Como empleado, quiero registrar un ingreso de stock para un producto indicando la cantidad y la fecha, para actualizar el inventario cuando llegue mercancía. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🔴 Must have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-034 |
| **Criterios de aceptación** | - El endpoint `POST /api/stock` crea un registro de stock asociado al producto. <br>- La cantidad debe ser un entero positivo. <br>- Si el producto no existe, retorna 404. <br>- El frontend tiene un formulario para ingresar la cantidad. |
| **Notas** | — |

---

#### US-033 — Ver inventario con alertas de stock bajo

| Campo | Detalle |
|-------|---------|
| **Historia** | Como empleado, quiero ver el inventario general con una alerta visual para los productos con poco stock, para saber cuándo debo pedir más mercancía. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟡 Should have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-036, RF-037 |
| **Criterios de aceptación** | - Los productos con stock ≤ 5 unidades se marcan como "stock bajo". <br>- Existe un endpoint dedicado `GET /api/stock/bajo` que retorna solo los productos con stock bajo. <br>- El frontend muestra un indicador visual destacado (color rojo o etiqueta) para estos productos. |
| **Notas** | El umbral de 5 unidades debe ser configurable en el futuro. |

---

### Épica 8 — Gestión de Usuarios

**Descripción:** El administrador tiene control total sobre las cuentas del sistema: puede crear empleados, editar datos y desactivar cuentas.

---

#### US-034 — Listar usuarios con búsqueda y paginación

| Campo | Detalle |
|-------|---------|
| **Historia** | Como administrador, quiero ver la lista de todos los usuarios del sistema con paginación y búsqueda por nombre, correo o documento, para tener control sobre quién tiene acceso. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟡 Should have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-039, RF-045 |
| **Criterios de aceptación** | - El endpoint retorna la lista paginada de usuarios sin incluir contraseñas. <br>- Se puede buscar por documento (exacto) o por correo (parcial). <br>- Se puede filtrar por tipo de usuario (REGISTRADO, SIN_REGISTRAR). |
| **Notas** | — |

---

#### US-035 — Crear cuenta de empleado

| Campo | Detalle |
|-------|---------|
| **Historia** | Como administrador, quiero crear cuentas de empleados (vendedor o administrador), para darles acceso al sistema con los permisos correspondientes. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟡 Should have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-041 |
| **Criterios de aceptación** | - Se crea el Usuario y el Empleado en una sola transacción. <br>- La contraseña se hashea con BCrypt. <br>- El tipo de empleado puede ser VENDEDOR o ADMINISTRADOR. |
| **Notas** | — |

---

#### US-036 — Editar datos de un usuario

| Campo | Detalle |
|-------|---------|
| **Historia** | Como administrador, quiero editar los datos de cualquier usuario (nombre, apellido, teléfono, dirección, correo), para corregir información incorrecta. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟡 Should have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-042 |
| **Criterios de aceptación** | - No se puede modificar el tipo ni número de documento. <br>- Si se cambia el correo, se valida que no exista en otro usuario. <br>- Si se proporciona nueva contraseña, se hashea; si no, la existente no cambia. |
| **Notas** | — |

---

#### US-037 — Eliminar o desactivar una cuenta de usuario

| Campo | Detalle |
|-------|---------|
| **Historia** | Como administrador, quiero eliminar o desactivar una cuenta de usuario que ya no la necesita, para mantener la base de datos limpia y ordenada. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟠 Could have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-043 |
| **Criterios de aceptación** | - Si el usuario tiene facturas, no se puede eliminar y retorna 409. <br>- Si no tiene facturas, se elimina junto con sus registros de cliente/empleado y carritos no finalizados. <br>- El frontend pide confirmación antes de eliminar. |
| **Notas** | Evaluar si implementar eliminación lógica (soft delete) o física. |

---

### Épica 9 — Perfil de Usuario

**Descripción:** Cada usuario puede ver y actualizar su propia información de perfil.

---

#### US-038 — Ver y editar perfil propio

| Campo | Detalle |
|-------|---------|
| **Historia** | Como usuario autenticado, quiero ver mi información de perfil y poder editar mi teléfono y dirección, para mantener mis datos de contacto actualizados. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟡 Should have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-046, RF-047 |
| **Criterios de aceptación** | - El endpoint `GET /api/perfil` retorna los datos del usuario autenticado sin incluir la contraseña. <br>- Solo se pueden editar: teléfono y dirección (no nombre, apellido ni documento). <br>- El cambio de correo requiere verificar la contraseña actual. |
| **Notas** | — |

---

#### US-039 — Cambiar contraseña

| Campo | Detalle |
|-------|---------|
| **Historia** | Como usuario autenticado, quiero cambiar mi contraseña verificando la actual, para mantener mi cuenta segura. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟡 Should have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-048 |
| **Criterios de aceptación** | - El usuario debe proveer la contraseña actual (verificación con BCrypt). <br>- La nueva contraseña debe tener mínimo 8 caracteres, una mayúscula y un número. <br>- La nueva contraseña se almacena hasheada con BCrypt. |
| **Notas** | — |

---

### Épica 10 — Reportes y Estadísticas

**Descripción:** Información agregada para que el administrador pueda tomar decisiones de negocio.

**¿Por qué va al final?** Porque los reportes dependen de que existan ventas, facturas e inventario registrados. No tiene sentido construirlos primero.

---

#### US-040 — Reporte de ventas por período

| Campo | Detalle |
|-------|---------|
| **Historia** | Como administrador, quiero ver un reporte de ventas para un rango de fechas, con el total vendido, el IVA recaudado y el número de facturas, para evaluar el desempeño del negocio. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟠 Could have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-049 |
| **Criterios de aceptación** | - El reporte acepta `fechaDesde` y `fechaHasta` como parámetros. <br>- Retorna: número total de facturas, monto total sin IVA, IVA total, total con IVA y promedio por factura. |
| **Notas** | — |

---

#### US-041 — Ranking de productos más vendidos

| Campo | Detalle |
|-------|---------|
| **Historia** | Como administrador, quiero ver un ranking de los productos más vendidos en un período, para identificar cuáles tienen más demanda. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟠 Could have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-050 |
| **Criterios de aceptación** | - El ranking muestra: nombre del producto, categoría, cantidad vendida y monto generado. <br>- Por defecto muestra el top 10. El límite es configurable. <br>- Se puede filtrar por rango de fechas. |
| **Notas** | — |

---

#### US-042 — Dashboard de indicadores clave (KPIs)

| Campo | Detalle |
|-------|---------|
| **Historia** | Como administrador, quiero ver un dashboard con los indicadores más importantes del negocio, para tener una visión general rápida sin necesidad de buscar en múltiples reportes. |
| **Responsable** | Backend + Frontend |
| **Prioridad** | 🟠 Could have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-054 |
| **Criterios de aceptación** | - El dashboard muestra: ventas del día, semana y mes, número de clientes registrados, cantidad de productos activos, productos agotados y valor total del inventario. |
| **Notas** | — |

---

### Épica 11 — Deuda Técnica

**Descripción:** Problemas identificados en el código y la base de datos actuales que deben resolverse para que el sistema funcione correctamente. Se llaman "deuda técnica" porque son cosas que quedaron pendientes del desarrollo inicial.

---

#### US-043 — Crear la entidad JPA `DetalleFactura`

| Campo | Detalle |
|-------|---------|
| **Historia** | Como desarrollador Backend, quiero crear la clase JPA `DetalleFactura` que actualmente falta, para poder registrar el detalle de productos en cada factura generada. |
| **Responsable** | Backend + BD |
| **Prioridad** | 🔴 Must have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-028 |
| **Criterios de aceptación** | - Existe la clase `DetalleFactura.java` en el paquete `model`. <br>- Tiene clave compuesta (idFactura + idProducto) con `@IdClass` o `@EmbeddedId`. <br>- Los campos `cantidad` y `precioUnitario` tienen sus anotaciones de validación. <br>- El esquema SQL ya existe en `SCRIPTS_POSTGRES.sql`; solo falta la entidad Java. |
| **Notas** | La tabla `DetalleFactura` ya existe en la base de datos. |

---

#### US-044 — Agregar campo `imagen` a la entidad `Producto`

| Campo | Detalle |
|-------|---------|
| **Historia** | Como desarrollador Backend, quiero agregar el campo `imagen` (URL) a la entidad Producto, para que los clientes puedan ver una foto del artículo en el catálogo. |
| **Responsable** | Backend + BD |
| **Prioridad** | 🟡 Should have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | RF-008 |
| **Criterios de aceptación** | - La entidad `Producto` tiene un campo `imagenUrl` de tipo `String`, opcional (nullable). <br>- El campo aparece en el DTO de respuesta de producto. <br>- El esquema PostgreSQL se actualiza con `ALTER TABLE` o a través del `ddl-auto=update` de Hibernate. |
| **Notas** | Usar `ddl-auto=update` permite que Hibernate añada la columna automáticamente, pero verificar que no cause problemas. |

---

#### US-045 — Alinear el enum `TipoDocumento` entre Java y PostgreSQL

| Campo | Detalle |
|-------|---------|
| **Historia** | Como desarrollador de BD, quiero asegurar que los valores del enum `TipoDocumento` en Java y en PostgreSQL sean exactamente los mismos, para evitar errores al insertar o leer usuarios. |
| **Responsable** | BD |
| **Prioridad** | 🔴 Must have |
| **Estado** | 📋 Backlog |
| **RF relacionado** | — |
| **Criterios de aceptación** | - El enum PostgreSQL `tipo_documento_enum` incluye todos los valores del enum Java: CC, TI, CE, PASAPORTE, NIT, RUT, OTRO. <br>- O bien el enum Java se reduce a solo los valores que tiene PostgreSQL. <br>- El equipo decide cuál de las dos opciones aplicar y lo documenta. |
| **Notas** | Actualmente Java tiene 7 valores y PostgreSQL solo tiene 4. Es una inconsistencia que causará errores en producción. |

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

> Cada Sprint tiene una duración sugerida de **2 semanas**. El Product Owner puede ajustar el alcance de cada sprint según la velocidad del equipo.

### Sprint 1 — Base técnica (US-001 a US-006, US-043, US-045)

**Objetivo:** Dejar el proyecto con la infraestructura técnica mínima para que todos puedan trabajar sin bloquearse entre sí.

| ID | Historia | Responsable |
|----|----------|-------------|
| US-001 | Estructura de carpetas y convenciones | Todos |
| US-002 | Implementar DTOs en Backend | Backend |
| US-003 | Configurar Spring Security con JWT | Backend |
| US-004 | Manejador global de errores | Backend |
| US-005 | Sincronizar esquema BD con JPA | BD |
| US-006 | Estructura base del Frontend Angular | Frontend |
| US-043 | Crear entidad JPA DetalleFactura | Backend + BD |
| US-045 | Alinear enum TipoDocumento | BD |

---

### Sprint 2 — Autenticación (US-007 a US-012)

**Objetivo:** Que cualquier usuario pueda registrarse, iniciar sesión y que el sistema controle el acceso según su rol.

| ID | Historia | Responsable |
|----|----------|-------------|
| US-007 | Registro de nuevo cliente | Backend + Frontend |
| US-008 | Inicio de sesión | Backend + Frontend |
| US-009 | Validación automática del token JWT | Backend |
| US-010 | Autorización por roles | Backend |
| US-011 | Cierre de sesión | Backend + Frontend |
| US-012 | Recuperación de contraseña | Backend + Frontend |

---

### Sprint 3 — Productos y catálogo (US-013 a US-019, US-044)

**Objetivo:** Los empleados pueden gestionar el catálogo y los clientes pueden explorar los productos.

| ID | Historia | Responsable |
|----|----------|-------------|
| US-013 | Crear un producto | Backend + Frontend |
| US-014 | Editar un producto | Backend + Frontend |
| US-015 | Eliminar un producto | Backend + Frontend |
| US-016 | Listar productos con paginación y filtros | Backend + Frontend |
| US-017 | Búsqueda por nombre parcial | Backend + Frontend |
| US-018 | Filtro por rango de precio | Backend + Frontend |
| US-019 | Ver detalle de un producto | Frontend |
| US-044 | Agregar campo imagen a Producto | Backend + BD |

---

### Sprint 4 — Carrito de compras (US-020 a US-025)

**Objetivo:** El cliente puede agregar productos, gestionarlos y ver su carrito con los totales calculados.

| ID | Historia | Responsable |
|----|----------|-------------|
| US-020 | Agregar producto al carrito | Backend + Frontend |
| US-021 | Modificar cantidad en el carrito | Backend + Frontend |
| US-022 | Eliminar producto del carrito | Backend + Frontend |
| US-023 | Ver carrito con totales | Backend + Frontend |
| US-024 | Validación de stock en tiempo real | Backend |
| US-025 | Máquina de estados del carrito | Backend |

---

### Sprint 5 — Checkout y facturación (US-026 a US-031)

**Objetivo:** El cliente puede completar su compra y recibir una factura. El inventario se actualiza automáticamente.

| ID | Historia | Responsable |
|----|----------|-------------|
| US-026 | Iniciar proceso de checkout | Backend + Frontend |
| US-027 | Seleccionar método de pago | Backend + Frontend |
| US-028 | Generar factura | Backend |
| US-029 | Descontar stock al facturar | Backend |
| US-030 | Cancelar proceso de checkout | Backend + Frontend |
| US-031 | Ver historial de facturas | Backend + Frontend |

---

### Sprint 6 — Inventario, usuarios y perfil (US-032 a US-039)

**Objetivo:** Los empleados gestionan el inventario, el administrador gestiona usuarios y cada usuario puede ver su perfil.

| ID | Historia | Responsable |
|----|----------|-------------|
| US-032 | Registrar ingreso de stock | Backend + Frontend |
| US-033 | Ver inventario con alertas de stock bajo | Backend + Frontend |
| US-034 | Listar usuarios con búsqueda y paginación | Backend + Frontend |
| US-035 | Crear cuenta de empleado | Backend + Frontend |
| US-036 | Editar datos de usuario | Backend + Frontend |
| US-037 | Eliminar o desactivar usuario | Backend + Frontend |
| US-038 | Ver y editar perfil propio | Backend + Frontend |
| US-039 | Cambiar contraseña | Backend + Frontend |

---

### Sprint 7 — Reportes y estadísticas (US-040 a US-042)

**Objetivo:** El administrador tiene acceso a información agregada para tomar decisiones de negocio.

| ID | Historia | Responsable |
|----|----------|-------------|
| US-040 | Reporte de ventas por período | Backend + Frontend |
| US-041 | Ranking de productos más vendidos | Backend + Frontend |
| US-042 | Dashboard de KPIs | Backend + Frontend |

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