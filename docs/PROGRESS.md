# TiendaQ -- Progreso del Desarrollo

**Ultima actualizacion:** Marzo 2026

---

## Estado general

| Area           | Estado                   | Progreso |
| -------------- | ------------------------ | -------- |
| Backend (API)  | CRUD basico sin seguridad | 20%      |
| Frontend       | Scaffold sin logica       | 2%       |
| Base de datos  | Esquema y datos de prueba | 85%      |
| Documentacion  | SRS y SDD completos       | 70%      |

**Nota:** Los porcentajes reflejan el avance respecto a los 72 requerimientos funcionales y 22 requerimientos no funcionales definidos en REQUIREMENTS.md. El backend tiene la estructura de capas creada pero carece de logica de negocio, seguridad, validacion y manejo de errores. El frontend es un scaffold vacio.

---

## Backend (Spring Boot 4.0)

Proyecto Maven con Java 25. Dependencias declaradas en `pom.xml`: Spring Data JPA, Spring Security, OAuth2 Client, Jakarta Validation, Spring Actuator, Lombok.

Ruta base del codigo: `app/backend/tiendaq/src/main/java/com/tiendaq/api/`

### Capa de modelo

9 entidades JPA + 6 enums.

| Entidad    | Archivo          | Estado    | Notas |
| ---------- | ---------------- | --------- | ----- |
| Usuario    | `model/Usuario.java`   | Creada | Entidad JPA basica. Sin anotaciones de validacion. |
| Cliente    | `model/Cliente.java`   | Creada | Relacion con Usuario. |
| Empleado   | `model/Empleado.java`  | Creada | Relacion con Usuario. |
| Producto   | `model/Producto.java`  | Creada | Campos: id, categoria (enum), nombre, precioUnitario. Sin campo de imagen. |
| Stock      | `model/Stock.java`     | Creada | Relacion con Producto. |
| Carrito    | `model/Carrito.java`   | Creada | Relacion con Usuario. |
| Items      | `model/Items.java`     | Creada | Clave compuesta (idCarrito, idProducto). |
| Factura    | `model/Factura.java`   | Creada | Relacion con Cliente y Empleado. |
| DetalleFactura | No existe        | Pendiente | Requerido por RF-028. No hay entidad ni tabla JPA para detalle de factura. |

| Enum           | Archivo                    |
| -------------- | -------------------------- |
| Categoria      | `model/enums/Categoria.java`     |
| Estado         | `model/enums/Estado.java`        |
| MetodoPago     | `model/enums/MetodoPago.java`    |
| TipoDocumento  | `model/enums/TipoDocumento.java` |
| TipoEmpleado   | `model/enums/TipoEmpleado.java`  |
| TipoUsuario    | `model/enums/TipoUsuario.java`   |

**Problemas detectados en la capa de modelo:**

- Ninguna entidad tiene anotaciones de Jakarta Validation (`@NotNull`, `@NotBlank`, `@Email`, `@Size`, etc.) a pesar de que la dependencia `spring-boot-starter-validation` esta en el `pom.xml`.
- La entidad `Producto` no tiene campo para URL de imagen (requerido por RF-008).
- No existe la entidad `DetalleFactura` (requerida por RF-028 y el esquema de base de datos).

### Capa de repositorio

8 interfaces que extienden `JpaRepository`.

| Repositorio         | Archivo                           | Estado | Notas |
| ------------------- | --------------------------------- | ------ | ----- |
| UsuarioRepository   | `repository/UsuarioRepository.java`   | Creado | Solo metodos heredados de JpaRepository. |
| ClienteRepository   | `repository/ClienteRepository.java`   | Creado | Idem. |
| EmpleadoRepository  | `repository/EmpleadoRepository.java`  | Creado | Idem. |
| ProductoRepository  | `repository/ProductoRepository.java`  | Creado | Query method `findByCategoria`. |
| StockRepository     | `repository/StockRepository.java`     | Creado | Query method `findByProductoIdProducto`. |
| CarritoRepository   | `repository/CarritoRepository.java`   | Creado | Query method `findByUsuarioIdUsuario`. |
| ItemRepository      | `repository/ItemRepository.java`      | Creado | Query method `findByIdIdCarrito`. |
| FacturaRepository   | `repository/FacturaRepository.java`   | Creado | Query method `findByClienteIdCliente`. |

**Pendiente en repositorios:** No hay queries personalizadas para busqueda por nombre (ILIKE), rango de precio, paginacion con `Pageable`, ni consultas de reportes.

### Capa de servicio

8 servicios. Todos siguen el mismo patron: metodos `save`, `findById`, `findAll`/`findByX`, `deleteById`. Ninguno contiene logica de negocio real.

| Servicio         | Archivo                       | Logica de negocio | Notas |
| ---------------- | ----------------------------- | ----------------- | ----- |
| UsuarioService   | `service/UsuarioService.java`   | No | CRUD puro: listarTodos, buscarPorId, crear, actualizar, eliminar. |
| ClienteService   | `service/ClienteService.java`   | No | buscarPorUsuario, crear, actualizar. |
| EmpleadoService  | `service/EmpleadoService.java`  | No | buscarPorId, buscarPorUsuario. |
| ProductoService  | `service/ProductoService.java`  | No | CRUD puro + buscarPorCategoria. |
| StockService     | `service/StockService.java`     | No | CRUD puro + listarPorProducto. |
| CarritoService   | `service/CarritoService.java`   | No | CRUD puro + buscarPorUsuario. Sin maquina de estados (RF-025). |
| ItemService      | `service/ItemService.java`      | No | CRUD puro + buscarPorCarrito. Sin validacion de stock (RF-024). |
| FacturaService   | `service/FacturaService.java`   | No | Solo buscarPorCliente, buscarPorId, crear. Sin calculo de IVA, sin descuento de stock, sin generacion de detalle. |

**Problemas criticos en la capa de servicio:**

- Ninguno hashea contrasenas con BCrypt (RF-001, RNF-005).
- No hay validacion de stock al agregar items al carrito (RF-024).
- No hay maquina de estados del carrito (RF-025).
- No hay proceso de checkout ni generacion de facturas con detalle (RF-026 a RF-032).
- No hay calculo de IVA ni descuento transaccional de stock (RF-028, RF-032).
- No hay logica de registro de cliente (creacion atomica de Usuario + Cliente, RF-001).

### Capa de controlador

8 controllers REST. Todos exponen entidades JPA directamente (sin DTOs). Ninguno usa `@Valid`. Ningun endpoint esta protegido por roles.

| Controller          | Ruta base         | Endpoints                                  | Estado | Notas |
| ------------------- | ----------------- | ------------------------------------------ | ------ | ----- |
| UsuarioController   | `/api/usuarios`   | GET /, GET /{id}, POST, PUT /{id}, DELETE /{id} | CRUD basico | Sin paginacion. Expone entidad completa (incluida contrasena). |
| ClienteController   | `/api/clientes`   | GET /{idUsuario}, POST, PUT /{idUsuario}   | CRUD parcial | Sin DELETE. |
| EmpleadoController  | `/api/empleados`  | GET /{id}, GET /usuario/{idUsuario}        | Solo lectura | Sin crear, actualizar ni eliminar. |
| ProductoController  | `/api/productos`  | GET /, GET /{id}, GET /categoria/{cat}, POST, PUT /{id}, DELETE /{id} | CRUD basico | Sin paginacion, sin busqueda por nombre, sin filtro por precio. |
| StockController     | `/api/stock`      | GET /producto/{idProd}, GET /{id}, POST, PUT /{id}, DELETE /{id} | CRUD basico | Sin endpoint de stock total calculado ni alerta de stock bajo. |
| CarritoController   | `/api/carritos`   | GET /usuario/{idUsr}, GET /{id}, POST, PUT /{id}, DELETE /{id} | CRUD basico | Sin flujo de checkout, sin transiciones de estado. |
| ItemController      | `/api/items`      | GET /carrito/{idCarrito}, POST, PUT, DELETE /{idCarrito}/{idProducto} | CRUD basico | Sin validacion de stock. PUT no recibe ID en path. |
| FacturaController   | `/api/facturas`   | GET /cliente/{idCli}, GET /{id}, POST      | CRUD parcial | Sin generacion real de factura. Sin detalle. Sin listado para empleados. |

### Paquetes vacios

Los siguientes paquetes fueron creados pero no contienen ninguna clase:

- `config/` -- Se esperan: SecurityConfig, CorsConfig, JwtConfig
- `dto/` -- Se esperan: DTOs de request/response para cada entidad
- `exception/` -- Se esperan: GlobalExceptionHandler, excepciones personalizadas (ResourceNotFoundException, BusinessRuleException, etc.)

### Pendiente en backend

Listado de lo que NO esta implementado, organizado por prioridad y modulo del SRS.

**Prioridad critica (bloquea la funcionalidad principal):**

- Spring Security: No hay SecurityFilterChain, ni filtro JWT, ni configuracion de autenticacion (RF-002, RF-003, RF-004)
- JWT: No hay generacion, validacion ni renovacion de tokens (RF-002, RF-003, RF-007)
- BCrypt: Las contrasenas no se hashean al crear usuarios (RF-001, RNF-005)
- DTOs: Todos los controllers exponen entidades JPA directamente, incluyendo contrasenas (RNF-009, RNF-019)
- Validacion: No se usan anotaciones de Jakarta Validation en ningun punto (RF-055, RF-056, RNF-010)
- Manejo de errores: No existe `@ControllerAdvice` ni excepciones personalizadas (RF-058, RF-059)
- CORS: No hay configuracion de CORS (RNF-007)
- Entidad DetalleFactura: No existe (RF-028)
- Campo imagen en Producto: No existe (RF-008)

**Prioridad alta (logica de negocio del core):**

- Registro de cliente: Creacion atomica de Usuario + Cliente (RF-001)
- Maquina de estados del carrito (RF-025)
- Validacion de stock en tiempo real al modificar carrito (RF-024)
- Flujo de checkout completo: iniciar, confirmar pago, generar factura (RF-026, RF-027, RF-028)
- Generacion de factura con detalle y calculo de IVA al 19% (RF-028)
- Descuento transaccional de stock al facturar (RF-032)
- Paginacion y ordenamiento en todos los listados (RF-010, RNF-003, RNF-014)
- Busqueda de productos por nombre, rango de precio, filtros combinados (RF-014, RF-015, RF-016)
- Autorizacion basada en roles en cada endpoint (RF-004)

**Prioridad media:**

- Cierre de sesion / invalidacion de token (RF-005)
- Recuperacion de contrasena (RF-006)
- Cancelar checkout (RF-029)
- Listado de facturas para empleados con filtros (RF-033)
- Alerta de stock bajo (RF-037)
- Buscar usuarios por documento o correo (RF-045)
- Perfil de usuario: consultar, actualizar, cambiar contrasena (RF-046, RF-047, RF-048)
- Documentacion OpenAPI / Swagger (RNF-020)

**Prioridad baja:**

- Renovacion de token JWT (RF-007)
- Correccion de registros de stock (RF-038)
- Cambio de rol de empleado (RF-044)
- Todos los reportes: ventas por periodo, productos mas vendidos, resumen de inventario, ventas por categoria, ventas por empleado, dashboard KPIs (RF-049 a RF-054)

**Testing:**

- Existe un unico archivo de test: `TiendaQApplicationTests.java` (test de contexto generado por Spring Initializr).
- No hay tests unitarios de servicios.
- No hay tests de integracion de controllers.

---

## Frontend (Angular 21)

Proyecto generado con Angular CLI 21. Ruta: `app/frontend/`

### Estado actual

El proyecto es un scaffold vacio. Los unicos archivos en `src/app/` son los generados por defecto:

| Archivo        | Proposito |
| -------------- | --------- |
| `app.ts`       | Componente raiz (generado) |
| `app.html`     | Template raiz (generado) |
| `app.scss`     | Estilos raiz (vacio o minimal) |
| `app.routes.ts`| Configuracion de rutas (vacia) |
| `app.config.ts`| Configuracion de la app (generado) |

### Pendiente en frontend (por modulo del SRS)

**Infraestructura:**

- Estructura de carpetas (pages/, components/, services/, guards/, interceptors/, models/)
- Servicio HTTP base con interceptor para JWT (Authorization header)
- Interceptor de errores HTTP (manejo global)
- Configuracion de rutas con lazy loading
- Gestion de estado (Angular Signals o RxJS)
- Almacenamiento seguro de token JWT
- Configuracion de ambiente (API base URL)

**Modulo de autenticacion (RF-060, RF-061, RF-072):**

- Pagina de registro con validacion en tiempo real
- Pagina de login
- Route guards por rol (AuthGuard, RoleGuard)
- Redireccion segun rol al autenticarse

**Modulo de catalogo (RF-062):**

- Pagina de catalogo con grilla/lista, busqueda, filtros, paginacion
- Pagina de detalle de producto (RF-017)

**Modulo de carrito (RF-063, RF-064):**

- Componente de carrito (sidebar o modal)
- Flujo de checkout en pasos

**Modulo de historial (RF-065):**

- Pagina de historial de facturas con detalle

**Modulo de gestion (empleados) (RF-066, RF-067):**

- Panel de gestion de productos (CRUD)
- Panel de gestion de inventario

**Modulo de administracion (RF-068, RF-069):**

- Panel de administracion de usuarios
- Dashboard de reportes y KPIs

**Transversales (RF-070, RF-071):**

- Diseno responsivo
- Estados de carga (spinners, skeletons)
- Mensajes de error amigables

---

## Base de datos

Motor: PostgreSQL 15+. Ruta de scripts: `app/database/`

| Script               | Lineas | Estado    | Contenido |
| -------------------- | ------ | --------- | --------- |
| SCRIPTS_POSTGRES.sql | 138    | Completo  | Esquema DDL: 9 tablas, 6 tipos ENUM, foreign keys, constraints. |
| SCRIPTS.sql          | 101    | Referencia | Esquema MySQL equivalente (no se usa en el proyecto). |
| INSERTS.sql          | 436    | Completo  | Datos de prueba: usuarios, clientes, empleados, productos, stock, carritos, items, facturas. |
| DELETE.sql           | 26     | Completo  | Script de limpieza con TRUNCATE CASCADE. |

**Lo que esta bien:**

- Esquema normalizado con 9 tablas incluyendo DetalleFactura
- 6 tipos ENUM definidos (Categoria, Estado, MetodoPago, TipoDocumento, TipoEmpleado, TipoUsuario)
- Integridad referencial con foreign keys
- Datos de prueba suficientes para desarrollo

**Pendiente en base de datos:**

- Indices para columnas de busqueda frecuente (nombre de producto, correo de usuario, fechas de factura)
- El modelo JPA no tiene entidad DetalleFactura aunque la tabla existe en el esquema
- Verificar que el modelo JPA coincide exactamente con el esquema PostgreSQL (campos, tipos, constraints)

---

## Documentacion

| Documento        | Archivo             | Estado    | Notas |
| ---------------- | ------------------- | --------- | ----- |
| SRS              | `docs/REQUIREMENTS.md` | Completo | 72 RF + 22 RNF + matriz de trazabilidad. Version 2.0. |
| SDD              | `docs/DESIGN.md`       | Completo | Documento de diseno tecnico. Version 2.0. |
| Base de datos    | `docs/DATABASE.md`     | Completo | Documentacion del esquema y scripts. |
| Progreso         | `docs/PROGRESS.md`     | Este documento | Se actualiza conforme avanza el desarrollo. |

**Pendiente en documentacion:**

- Documentacion de API (Swagger / OpenAPI) -- depende de implementar DTOs y anotaciones
- Guia de contribucion / setup local para nuevos desarrolladores
- Diccionario de datos actualizado si se modifica el esquema

---

## Cobertura de requerimientos funcionales

De los 72 requerimientos funcionales del SRS, el estado actual es:

| Modulo                       | RFs       | Implementados | Parcial | Pendientes |
| ---------------------------- | --------- | ------------- | ------- | ---------- |
| Autenticacion y autorizacion | RF-001-007 | 0            | 0       | 7          |
| Gestion de productos         | RF-008-013 | 0            | 3*      | 3          |
| Catalogo y busqueda          | RF-014-017 | 0            | 0       | 4          |
| Carrito de compras           | RF-018-025 | 0            | 0       | 8          |
| Checkout y facturacion       | RF-026-033 | 0            | 0       | 8          |
| Gestion de inventario        | RF-034-038 | 0            | 1*      | 4          |
| Gestion de usuarios          | RF-039-045 | 0            | 2*      | 5          |
| Perfil de usuario            | RF-046-048 | 0            | 0       | 3          |
| Reportes y estadisticas      | RF-049-054 | 0            | 0       | 6          |
| Validacion y errores         | RF-055-059 | 0            | 0       | 5          |
| Frontend                     | RF-060-072 | 0            | 0       | 13         |
| **Total**                    | **72**    | **0**         | **6**   | **66**     |

*Parcial significa que existe un endpoint CRUD basico que cubre parte de la funcionalidad, pero sin la logica de negocio, validacion, seguridad ni DTOs que el RF especifica.

**Conclusion:** Ningun requerimiento funcional esta completamente implementado segun las especificaciones del SRS. Lo que existe es infraestructura base (entidades, repositorios, servicios y controllers CRUD) que servira como punto de partida.

---

## Proximos pasos

Orden de prioridad recomendado para el equipo de desarrollo:

| Prioridad | Tarea | Area | RFs relacionados |
| --------- | ----- | ---- | ---------------- |
| 1 | Crear DTOs de request/response para todas las entidades. Los controllers NO deben exponer entidades JPA. | Backend | RNF-019 |
| 2 | Implementar `@ControllerAdvice` con manejo global de excepciones y formato estandarizado de errores. | Backend | RF-058, RF-059 |
| 3 | Agregar anotaciones de Jakarta Validation en los DTOs y `@Valid` en los controllers. | Backend | RF-055, RF-056, RF-057 |
| 4 | Crear entidad `DetalleFactura` en JPA y agregar campo `imagen` a `Producto`. | Backend | RF-008, RF-028 |
| 5 | Configurar Spring Security: SecurityFilterChain, filtro JWT, autenticacion con BCrypt, autorizacion por roles. | Backend | RF-002, RF-003, RF-004 |
| 6 | Implementar registro de cliente (creacion atomica Usuario + Cliente con contrasena hasheada). | Backend | RF-001 |
| 7 | Implementar logica de carrito: maquina de estados, validacion de stock, agregar/modificar/eliminar items. | Backend | RF-018-025 |
| 8 | Implementar flujo de checkout: iniciar, confirmar pago, generar factura con detalle, descontar stock. | Backend | RF-026-032 |
| 9 | Agregar paginacion (`Pageable`) y busqueda (nombre, precio, filtros combinados) a productos. | Backend | RF-010, RF-014-016 |
| 10 | Configurar CORS para permitir solicitudes del frontend. | Backend | RNF-007 |
| 11 | Estructurar frontend: carpetas, rutas, servicio HTTP con interceptor JWT, guards. | Frontend | RF-072 |
| 12 | Implementar login y registro en frontend. | Frontend | RF-060, RF-061 |
| 13 | Implementar catalogo de productos con busqueda y filtros. | Frontend | RF-062 |
| 14 | Implementar carrito y flujo de checkout en frontend. | Frontend | RF-063, RF-064 |
| 15 | Implementar paneles de gestion (productos, inventario, usuarios) para empleados y administradores. | Frontend | RF-066-068 |
| 16 | Reportes y dashboard (backend + frontend). | Ambos | RF-049-054, RF-069 |
| 17 | Tests unitarios de servicios y tests de integracion de controllers. | Backend | -- |

---

Este documento refleja el estado real del proyecto al momento de la ultima actualizacion. Cada tarea pendiente referencia los IDs de requerimientos funcionales del SRS (REQUIREMENTS.md) para mantener trazabilidad.
