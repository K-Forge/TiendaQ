<!--
  AGENT.md -- Contexto del proyecto para asistentes de IA
  Ultima actualizacion: 2026-03-16

  IMPORTANTE: Mantener este archivo actualizado cada vez que se modifique
  la arquitectura, se agreguen endpoints, se cambien dependencias o se
  avance en el desarrollo. Un agente de IA que lea SOLO este archivo
  debe poder entender el proyecto completo.
-->

# TiendaQ -- Contexto para Agentes de IA

## Descripcion del proyecto

TiendaQ es una aplicacion de comercio electronico desarrollada como proyecto academico universitario. El sistema permite a clientes navegar un catalogo de productos, gestionar un carrito de compras, realizar el proceso de checkout con facturacion, y a empleados/administradores gestionar inventario, usuarios y reportes.

El backend es una API REST construida con Spring Boot 4.0 y Java 25. El frontend es una SPA con Angular 21. La base de datos es PostgreSQL 15+.

El proyecto se encuentra en fase temprana de desarrollo: la estructura base esta creada (entidades, repositorios, servicios y controllers CRUD) pero no hay logica de negocio, seguridad, validacion ni manejo de errores implementados. El frontend es un scaffold vacio.

---

## Stack tecnologico

| Componente         | Tecnologia              | Version       |
| ------------------ | ----------------------- | ------------- |
| Lenguaje backend   | Java                    | 25            |
| Framework backend  | Spring Boot             | 4.0.0         |
| ORM                | Spring Data JPA / Hibernate | (via starter) |
| Seguridad          | Spring Security + OAuth2 Client | (via starter, sin configurar) |
| Validacion         | Jakarta Validation      | (via starter, sin usar) |
| Monitoreo          | Spring Actuator         | (via starter) |
| Generacion codigo  | Lombok                  | (via dependency) |
| Base de datos      | PostgreSQL              | 15+           |
| Driver JDBC        | PostgreSQL Driver       | (via starter) |
| Framework frontend | Angular                 | 21.2.0        |
| CLI frontend       | Angular CLI             | 21.2.2        |
| Lenguaje frontend  | TypeScript              | ~5.9.2        |
| Estilos            | SCSS                    | (via Angular) |
| Reactividad        | RxJS                    | ~7.8.0        |
| Package manager    | Bun                     | 1.3.8         |
| Formatter          | Prettier                | ^3.8.1        |
| Build backend      | Maven                   | (wrapper)     |

---

## Arquitectura

Arquitectura monolitica con separacion frontend/backend:

```
Angular SPA (puerto 4200)  --->  Spring Boot REST API (puerto 8080)  --->  PostgreSQL (puerto 5432)
```

El backend sigue una arquitectura de capas:

```
Controller (REST)  --->  Service (logica de negocio)  --->  Repository (JPA)  --->  PostgreSQL
```

Todas las rutas de la API estan bajo el prefijo `/api/`. Los controllers reciben y devuelven entidades JPA directamente (no hay DTOs implementados todavia). No hay seguridad configurada.

El frontend es una SPA Angular con componentes standalone y SCSS. No tiene SSR. Actualmente es un scaffold vacio sin logica.

---

## Estructura del proyecto

```
TiendaK/                          # Directorio del repositorio (el proyecto se llama TiendaQ)
  .agent/
    skills/                       # Skills y configuracion para agentes de IA
      .gitkeep
  .gitignore
  package.json                    # Metadata del workspace (Bun 1.3.8)
  README.md
  CONTRIBUTING.md                 # Convenciones de Git, codigo y documentacion
  AGENT.md                        # Este archivo
  CODE_OF_CONDUCT.md
  SECURITY.md
  LICENSE
  docs/
    REQUIREMENTS.md               # SRS: 72 RF + 22 RNF, 11 modulos
    DESIGN.md                     # SDD: arquitectura, diagramas, API design
    PROGRESS.md                   # Estado actual del desarrollo
    DATABASE.md                   # Documentacion del esquema y scripts
  scripts/
    start-back.sh                 # Script para iniciar el backend
    start-front.sh                # Script para iniciar el frontend
  app/
    backend/
      postman/
        .gitkeep                  # Para colecciones Postman (vacio)
      tiendaq/
        pom.xml                   # Maven: Java 25, Spring Boot 4.0.0
        src/main/java/com/tiendaq/api/
          TiendaQApplication.java # Clase principal
          controller/             # 8 controllers REST
          service/                # 8 servicios
          repository/             # 8 repositorios JPA
          model/                  # 8 entidades JPA
            enums/                # 6 enums
          config/                 # VACIO -- falta SecurityConfig, CorsConfig
          dto/                    # VACIO -- faltan DTOs de request/response
          exception/              # VACIO -- falta GlobalExceptionHandler
        src/main/resources/
          application.properties  # Configuracion de BD y JPA
        src/test/java/com/tiendaq/api/
          TiendaQApplicationTests.java  # Unico test (contexto Spring)
    database/
      SCRIPTS_POSTGRES.sql        # DDL: 9 tablas, 6 enums, FKs, constraints
      SCRIPTS_MYSQL_LEGACY.sql    # Historico: esquema MySQL original (NO usar)
      INSERTS.sql                 # Datos de prueba (seed data)
      DELETE.sql                  # Limpieza con TRUNCATE CASCADE
    frontend/
      package.json                # Angular 21.2.0, TS 5.9.2
      angular.json
      tsconfig.json
      src/app/
        app.ts                    # Componente raiz (generado)
        app.html                  # Template raiz (generado)
        app.scss                  # Estilos raiz
        app.routes.ts             # Rutas (vacio)
        app.config.ts             # Configuracion de la app
```

---

## Modulos del sistema

Definidos en el SRS (`docs/REQUIREMENTS.md`). 72 requerimientos funcionales organizados en 11 modulos:

| N | Modulo                         | Requerimientos | Descripcion |
| - | ------------------------------ | -------------- | ----------- |
| 1 | Autenticacion y autorizacion   | RF-001 a RF-007 | Registro, login, JWT, roles, logout, recuperacion de contrasena |
| 2 | Gestion de productos           | RF-008 a RF-013 | CRUD de productos para empleados/admin |
| 3 | Catalogo y busqueda            | RF-014 a RF-017 | Busqueda, filtros, paginacion, detalle de producto |
| 4 | Carrito de compras             | RF-018 a RF-025 | Agregar/modificar/eliminar items, maquina de estados, validacion de stock |
| 5 | Checkout y facturacion         | RF-026 a RF-033 | Proceso de pago, generacion de factura con detalle, IVA, descuento de stock |
| 6 | Gestion de inventario          | RF-034 a RF-038 | Registrar ingresos, consultar stock, alertas de stock bajo |
| 7 | Gestion de usuarios            | RF-039 a RF-045 | Listar, buscar, crear, actualizar, desactivar usuarios |
| 8 | Perfil de usuario              | RF-046 a RF-048 | Consultar perfil, actualizar datos, cambiar contrasena |
| 9 | Reportes y estadisticas        | RF-049 a RF-054 | Ventas por periodo, productos mas vendidos, inventario, dashboard KPIs |
| 10 | Validacion y errores          | RF-055 a RF-059 | Validacion de entradas, mensajes de error, formato estandarizado |
| 11 | Frontend                      | RF-060 a RF-072 | Paginas de registro, login, catalogo, carrito, checkout, gestion, admin, responsividad |

---

## Entidades de dominio

| Entidad        | Clase JPA        | Tabla SQL         | PK                        | Notas |
| -------------- | ---------------- | ----------------- | ------------------------- | ----- |
| Usuario        | `Usuario.java`   | `Usuario`         | `idUsuario` (IDENTITY)    | Clase base. `@Inheritance(JOINED)`. Campos: nombre, apellido, tipoDocumento, documento, telefono, correo, direccion, contrasena, tipoUsuario. |
| Cliente        | `Cliente.java`   | `Cliente`         | Hereda de Usuario         | `extends Usuario`. `@PrimaryKeyJoinColumn(name="idUsuario")`. Campo adicional: idCliente. |
| Empleado       | `Empleado.java`  | `Empleado`        | Hereda de Usuario         | `extends Usuario`. `@PrimaryKeyJoinColumn(name="idUsuario")`. Campo adicional: idEmpleado, tipoEmpleado (enum). |
| Producto       | `Producto.java`  | `Producto`        | `idProducto` (IDENTITY)   | Campos: categoria (enum), nombre, precioUnitario. Sin campo de imagen (pendiente). |
| Stock          | `Stock.java`     | `Stock`           | `idStock` (IDENTITY)      | Campos: fechaIngreso, stock (cantidad), producto (ManyToOne). |
| Carrito        | `Carrito.java`   | `CarritoCompra`   | `idcarritocompra` (IDENTITY) | Campos: fechaCreacion, estado (enum), usuario (ManyToOne). |
| Items          | `Items.java`     | `ItemCarrito`     | Compuesta: carrito + producto | `@IdClass(Items.ItemId)`. Campos: carrito (ManyToOne), producto (ManyToOne), cantidad, precioUnitario. |
| Factura        | `Factura.java`   | `Factura`         | `idFactura` (IDENTITY)    | Campos: fechaCompra, totalCompra, metodoPago (enum), iva, empleado (ManyToOne), cliente (ManyToOne), carrito (ManyToOne). |
| DetalleFactura | No existe en JPA | `DetalleFactura`  | (definida en SQL)         | La tabla existe en SCRIPTS_POSTGRES.sql pero NO hay entidad JPA. Pendiente de crear. |

---

## Endpoints de la API

Todos bajo el prefijo `/api/`. 33 endpoints en 8 controllers.

### CarritoController (`/api/carritos`)

| Metodo | Ruta                         | Descripcion |
| ------ | ---------------------------- | ----------- |
| GET    | `/api/carritos/usuario/{idUsuario}` | Listar carritos por usuario |
| GET    | `/api/carritos/{id}`         | Buscar carrito por ID |
| POST   | `/api/carritos`              | Crear carrito |
| PUT    | `/api/carritos/{id}`         | Actualizar carrito |
| DELETE | `/api/carritos/{id}`         | Eliminar carrito |

### ClienteController (`/api/clientes`)

| Metodo | Ruta                         | Descripcion |
| ------ | ---------------------------- | ----------- |
| GET    | `/api/clientes/{idUsuario}`  | Buscar cliente por ID de usuario |
| POST   | `/api/clientes`              | Crear cliente |
| PUT    | `/api/clientes/{idUsuario}`  | Actualizar cliente |

### EmpleadoController (`/api/empleados`)

| Metodo | Ruta                              | Descripcion |
| ------ | --------------------------------- | ----------- |
| GET    | `/api/empleados/{id}`             | Buscar empleado por ID |
| GET    | `/api/empleados/usuario/{idUsuario}` | Buscar empleado por ID de usuario |

### FacturaController (`/api/facturas`)

| Metodo | Ruta                              | Descripcion |
| ------ | --------------------------------- | ----------- |
| GET    | `/api/facturas/cliente/{idCliente}` | Listar facturas por cliente |
| GET    | `/api/facturas/{id}`              | Buscar factura por ID |
| POST   | `/api/facturas`                   | Crear factura |

### ItemController (`/api/items`)

| Metodo | Ruta                                    | Descripcion |
| ------ | --------------------------------------- | ----------- |
| GET    | `/api/items/carrito/{idCarrito}`        | Listar items por carrito |
| POST   | `/api/items`                            | Crear item |
| PUT    | `/api/items`                            | Actualizar item (sin ID en path) |
| DELETE | `/api/items/{idCarrito}/{idProducto}`   | Eliminar item por clave compuesta |

### ProductoController (`/api/productos`)

| Metodo | Ruta                                  | Descripcion |
| ------ | ------------------------------------- | ----------- |
| GET    | `/api/productos`                      | Listar todos los productos |
| GET    | `/api/productos/{id}`                 | Buscar producto por ID |
| GET    | `/api/productos/categoria/{categoria}` | Buscar productos por categoria |
| POST   | `/api/productos`                      | Crear producto |
| PUT    | `/api/productos/{id}`                 | Actualizar producto |
| DELETE | `/api/productos/{id}`                 | Eliminar producto |

### StockController (`/api/stock`)

| Metodo | Ruta                                | Descripcion |
| ------ | ----------------------------------- | ----------- |
| GET    | `/api/stock/producto/{idProducto}`  | Listar stock por producto |
| GET    | `/api/stock/{id}`                   | Buscar registro de stock por ID |
| POST   | `/api/stock`                        | Crear registro de stock |
| PUT    | `/api/stock/{id}`                   | Actualizar registro de stock |
| DELETE | `/api/stock/{id}`                   | Eliminar registro de stock |

### UsuarioController (`/api/usuarios`)

| Metodo | Ruta                    | Descripcion |
| ------ | ----------------------- | ----------- |
| GET    | `/api/usuarios`         | Listar todos los usuarios |
| GET    | `/api/usuarios/{id}`    | Buscar usuario por ID |
| POST   | `/api/usuarios`         | Crear usuario |
| PUT    | `/api/usuarios/{id}`    | Actualizar usuario |
| DELETE | `/api/usuarios/{id}`    | Eliminar usuario |

---

## Enums

| Enum           | Valores                                                     | Uso |
| -------------- | ----------------------------------------------------------- | --- |
| Categoria      | `ROPA`, `ACCESORIOS`, `LIBRERIA`, `PAPELERIA`              | Clasificacion de productos |
| Estado         | `VACIO`, `CON_PRODUCTOS`, `EN_PROCESO_DE_PAGO`, `PAGO_PENDIENTE`, `PAGO_EXITOSO` | Estado del carrito de compras |
| MetodoPago     | `PSE`, `TARJETA_CREDITO`, `TARJETA_DEBITO`, `EFECTIVO`, `TRANSFERENCIA` | Metodo de pago en factura |
| TipoDocumento  | `CC`, `TI`, `CE`, `PASAPORTE`, `NIT`, `RUT`, `OTRO`       | Tipo de documento del usuario |
| TipoEmpleado   | `ADMINISTRADOR`, `VENDEDOR`                                 | Rol del empleado |
| TipoUsuario    | `REGISTRADO`, `SIN_REGISTRAR`                               | Estado de registro del usuario |

---

## Base de datos

- **Motor:** PostgreSQL 15+
- **Nombre de la base de datos:** `tiendaq`
- **URL:** `jdbc:postgresql://localhost:5432/tiendaq`
- **Credenciales:** Variables de entorno con valores por defecto:
  - `DB_USER` (default: `postgres`)
  - `DB_PASSWORD` (default: vacio)
- **Estrategia DDL:** `spring.jpa.hibernate.ddl-auto=update` (Hibernate sincroniza el esquema automaticamente)
- **SQL logging:** Habilitado (`spring.jpa.show-sql=true`)

### Scripts SQL (`app/database/`)

| Script                  | Proposito |
| ----------------------- | --------- |
| `SCRIPTS_POSTGRES.sql`  | DDL principal: 9 tablas, 6 tipos ENUM, foreign keys, constraints. Usar este. |
| `SCRIPTS_MYSQL_LEGACY.sql` | Historico: esquema MySQL original de una version anterior. NO usar. |
| `INSERTS.sql`           | Datos de prueba: usuarios, clientes, empleados, productos, stock, carritos, items, facturas. |
| `DELETE.sql`            | Limpieza completa con TRUNCATE CASCADE en orden correcto. |

---

## Estado actual del desarrollo

| Area           | Progreso | Estado |
| -------------- | -------- | ------ |
| Backend (API)  | 20%      | CRUD basico sin seguridad, sin logica de negocio, sin validacion, sin DTOs |
| Frontend       | 2%       | Scaffold Angular vacio, sin componentes ni rutas |
| Base de datos  | 85%      | Esquema completo con datos de prueba |
| Documentacion  | 70%      | SRS y SDD completos |

### Requerimientos funcionales

- **Total definidos:** 72 RF + 22 RNF
- **Completamente implementados:** 0
- **Parcialmente implementados:** 6 (solo CRUD basico, sin la logica que especifica el RF)
  - Gestion de productos: RF-008, RF-009, RF-010 (CRUD existe, falta imagen, paginacion, busqueda)
  - Gestion de inventario: RF-034 (CRUD de stock existe, falta logica)
  - Gestion de usuarios: RF-039, RF-040 (listado y busqueda basica por ID)
- **Pendientes:** 66

### Lo que NO esta implementado (critico)

- Spring Security (sin filtro, sin JWT, sin autenticacion)
- Hasheo de contrasenas (BCrypt)
- DTOs (los controllers exponen entidades JPA directamente, incluyendo contrasenas)
- Validacion de entradas (Jakarta Validation no se usa a pesar de estar en pom.xml)
- Manejo global de errores (`@ControllerAdvice`)
- Configuracion de CORS
- Entidad DetalleFactura (existe en SQL, no en JPA)
- Campo imagen en Producto
- Logica de negocio en todos los servicios

---

## Convenciones

### Git

- **Branching:** Git Flow con ramas `main`, `develop`, `feature/*`, `chore/*`, `bugfix/*`, `test/*`, `hotfix/*`, `release/*`
- **Commits:** Conventional Commits en ingles, minusculas, sin punto final
  - Formato: `type: short message`
  - Tipos: `feat`, `fix`, `chore`, `release`, `hotfix`, `docs`, `refactor`, `test`
- **Versionado:** SemVer con soporte para pre-release (alpha, beta, rc)

### Codigo

**Backend (Java):**
- Indentacion: 4 espacios
- Clases: PascalCase
- Metodos y variables: camelCase
- Paquetes: minusculas (`com.tiendaq.api`)

**Frontend (Angular/TypeScript):**
- Formatter: Prettier (ancho 100, comillas simples)
- Indentacion: 2 espacios
- EditorConfig configurado
- Componentes standalone (sin NgModules)
- Estilos en SCSS

### Documentacion

- Idioma: espanol
- Sin emojis
- Estilo formal/academico
- Referencias a requerimientos usando IDs (RF-001, RNF-005, etc.)

---

## Archivos clave

| Archivo | Proposito |
| ------- | --------- |
| `app/backend/tiendaq/pom.xml` | Dependencias y versiones del backend (Java 25, Spring Boot 4.0.0) |
| `app/backend/tiendaq/src/main/resources/application.properties` | Configuracion de BD, JPA y aplicacion |
| `app/frontend/package.json` | Dependencias del frontend (Angular 21.2.0, TS 5.9.2) |
| `docs/REQUIREMENTS.md` | SRS completo: 72 RF + 22 RNF con matriz de trazabilidad |
| `docs/DESIGN.md` | SDD: arquitectura, diagramas ER, diagramas de secuencia, API design |
| `docs/PROGRESS.md` | Estado detallado del desarrollo con tareas pendientes priorizadas |
| `docs/DATABASE.md` | Documentacion del esquema y scripts SQL |
| `CONTRIBUTING.md` | Convenciones de Git, codigo y documentacion |
| `app/database/SCRIPTS_POSTGRES.sql` | DDL principal del esquema PostgreSQL |
| `app/database/INSERTS.sql` | Datos de prueba (seed data) |
| `scripts/start-back.sh` | Script para iniciar el servidor backend |
| `scripts/start-front.sh` | Script para iniciar el servidor frontend |

---

## Proximos pasos prioritarios

Orden recomendado para continuar el desarrollo (referencia completa en `docs/PROGRESS.md`):

1. **DTOs de request/response** -- Los controllers NO deben exponer entidades JPA (RNF-019)
2. **Manejo global de errores** -- `@ControllerAdvice` con formato estandarizado (RF-058, RF-059)
3. **Jakarta Validation** -- Anotaciones en DTOs y `@Valid` en controllers (RF-055 a RF-057)
4. **Entidad DetalleFactura + campo imagen en Producto** -- Completar el modelo (RF-008, RF-028)
5. **Spring Security** -- SecurityFilterChain, JWT, BCrypt, roles (RF-002 a RF-004)
6. **Registro de cliente** -- Creacion atomica Usuario + Cliente con hash (RF-001)
7. **Logica de carrito** -- Maquina de estados, validacion de stock (RF-018 a RF-025)
8. **Checkout y facturacion** -- Flujo completo con detalle y descuento de stock (RF-026 a RF-032)
9. **Paginacion y busqueda** -- Pageable en listados, busqueda por nombre y precio (RF-010, RF-014 a RF-016)
10. **CORS** -- Configurar para permitir requests del frontend (RNF-007)

---

## Advertencias y gotchas

1. **Enums de PostgreSQL son case-sensitive.** Los valores en las clases Java (`ROPA`, `ACCESORIOS`, etc.) deben coincidir EXACTAMENTE con los definidos en el esquema PostgreSQL. Si la BD tiene `Ropa` y Java tiene `ROPA`, va a fallar.

2. **Credenciales de BD usan variables de entorno.** En `application.properties`: `${DB_USER:postgres}` y `${DB_PASSWORD:}`. Si no se definen las variables, usa `postgres` sin contrasena. No hay credenciales hardcodeadas.

3. **Lombok puede generar falsos errores en el IDE.** Todas las entidades usan `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`. Si el IDE marca errores en getters/setters, instalar el plugin de Lombok correspondiente (IntelliJ: Lombok plugin; VS Code: extensiones de Java).

4. **Items (ItemCarrito) usa clave primaria compuesta con `@IdClass`.** La clase `Items` tiene una inner class `ItemId` con `implements Serializable`. Se usa `@IdClass(Items.ItemId.class)`, NO `@EmbeddedId`. Los campos de la PK son las relaciones `carrito` y `producto` (ambos `@Id @ManyToOne`).

5. **Usuario usa `@Inheritance(JOINED)`.** `Cliente` y `Empleado` extienden de `Usuario` con `@PrimaryKeyJoinColumn(name="idUsuario")`. Esto significa que hay 3 tablas separadas (Usuario, Cliente, Empleado) unidas por el ID. Los queries de Cliente/Empleado generan JOINs automaticamente.

6. **SCRIPTS_MYSQL_LEGACY.sql es historico.** Este archivo contiene el esquema MySQL de una version anterior del proyecto. El motor actual es PostgreSQL. Usar SIEMPRE `SCRIPTS_POSTGRES.sql` para el DDL.

7. **El directorio del repositorio se llama "TiendaK" pero el proyecto es "TiendaQ".** El nombre en Git es `TiendaK` por razones historicas. Todo el codigo, paquetes, configuraciones y documentacion usan `tiendaq` (junto, minusculas).

8. **Los passwords en los datos semilla (INSERTS.sql) estan en texto plano intencionalmente.** Es un entorno de desarrollo. Cuando se implemente Spring Security con BCrypt, estos valores deberan reemplazarse por hashes.
