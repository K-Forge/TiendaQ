# TiendaQ -- Documento de Diseno de Software (SDD)

**Proyecto:** TiendaQ - Sistema de Comercio Electronico Universitario  
**Organizacion:** Fundacion Universitaria Konrad Lorenz (FUKL) - Club K-Forge  
**Version:** 2.0  
**Fecha:** Marzo 2026  
**Estado:** Aprobado  
**Documento de referencia:** REQUIREMENTS.md (SRS v2.0, 72 RF + 22 RNF)

---

## 1. Introduccion

### 1.1 Proposito

Este documento define el Diseno de Software (SDD) para **TiendaQ**, estableciendo la arquitectura del sistema, el modelo de datos, los patrones de diseno aplicados, los diagramas de comportamiento y el diseno de la API REST. El documento sirve como referencia tecnica para la implementacion y como instrumento de evaluacion academica.

El documento esta dirigido a:

- Desarrolladores del equipo K-Forge responsables de la implementacion.
- Docentes y evaluadores academicos que verifican la calidad arquitectonica del sistema.
- Testers que requieren comprender la estructura interna para disenar pruebas.

### 1.2 Alcance

El SDD cubre el diseno completo de TiendaQ en sus tres componentes:

- **Frontend**: SPA en Angular 21 con standalone components, Angular Router y SCSS, comunicandose via HTTP/JSON con el backend.
- **Backend**: API REST en Spring Boot 4.0 con Java 25, implementando una arquitectura en capas (Controller, Service, Repository, Model).
- **Base de datos**: PostgreSQL 15+ como unico motor de persistencia con esquema relacional normalizado.

**Fuera del alcance del diseno:** Infraestructura de despliegue en produccion, integracion con pasarelas de pago reales, servicios de correo electronico transaccional.

### 1.3 Definiciones y acronimos

| Termino | Definicion |
| --- | --- |
| SDD | Documento de Diseno de Software (Software Design Document) |
| SRS | Especificacion de Requerimientos de Software (Software Requirements Specification) |
| C4 Model | Modelo de diagramacion arquitectonica en cuatro niveles: Contexto, Contenedores, Componentes, Codigo |
| JPA | Java Persistence API -- especificacion para mapeo objeto-relacional |
| ORM | Mapeo Objeto-Relacional (Object-Relational Mapping) |
| DTO | Objeto de Transferencia de Datos (Data Transfer Object) |
| CRUD | Operaciones basicas: Crear, Leer, Actualizar, Eliminar |
| JWT | JSON Web Token -- estandar para transmitir informacion de autenticacion |
| SPA | Aplicacion de Pagina Unica (Single Page Application) |
| IVA | Impuesto al Valor Agregado (19% en Colombia) |
| Entidad de dominio | Clase que representa un concepto del negocio y se mapea a una tabla de la base de datos |

---

## 2. Stack tecnologico

La siguiente tabla refleja las tecnologias y versiones reales utilizadas en el proyecto, extraidas de `pom.xml` y `package.json`.

| Capa | Tecnologia | Version | Proposito |
| --- | --- | --- | --- |
| Lenguaje backend | Java | 25 | Lenguaje principal del servidor |
| Framework backend | Spring Boot | 4.0.0 | Framework para la API REST |
| Persistencia | Spring Data JPA (Hibernate) | -- | ORM y acceso a datos |
| Seguridad | Spring Security + OAuth2 Client | -- | Autenticacion y autorizacion |
| Validacion | Spring Validation (Jakarta) | -- | Validacion de DTOs de entrada |
| Monitoreo | Spring Actuator | -- | Health checks y metricas |
| Web | Spring WebMVC | -- | Controladores REST |
| Utilidades | Lombok | -- | Reduccion de boilerplate (getters, setters, constructores) |
| Desarrollo | Spring DevTools | -- | Recarga en caliente durante desarrollo |
| Base de datos | PostgreSQL | 15+ | Motor de persistencia relacional |
| Driver JDBC | PostgreSQL Driver | -- | Conectividad Java-PostgreSQL |
| Build backend | Maven | 3.9+ | Gestion de dependencias y compilacion |
| Lenguaje frontend | TypeScript | ~5.9 | Lenguaje del cliente |
| Framework frontend | Angular | 21.2.0 | Framework de interfaces de usuario |
| CLI frontend | Angular CLI | 21.2.2 | Herramienta de desarrollo y build |
| Routing | Angular Router | -- | Navegacion SPA |
| HTTP Client | Angular HttpClient | -- | Comunicacion con la API REST |
| Estilos | SCSS | -- | Preprocesador de estilos |
| Package manager | pnpm + Bun | -- | pnpm para dependencias, Bun para ejecucion |

---

## 3. Arquitectura del sistema

### 3.1 Diagrama de contexto (C4 -- Nivel 1)

El diagrama de contexto muestra el sistema TiendaQ y sus interacciones con los actores externos. En este nivel, el sistema se presenta como una caja negra.

```mermaid
graph TB
    CLI["Cliente
    (Usuario registrado)"]
    VEN["Vendedor
    (Empleado)"]
    ADM["Administrador
    (Empleado)"]

    SYS["TiendaQ
    Sistema de Comercio Electronico
    ---
    Permite a clientes navegar
    un catalogo, gestionar carritos
    y realizar compras.
    Permite a empleados gestionar
    productos, inventario y facturas."]

    NAV["Navegador Web
    (Chrome, Firefox,
    Safari, Edge)"]

    CLI -->|"Navega catalogo, compra productos, consulta historial"| SYS
    VEN -->|"Gestiona productos, inventario, procesa ventas"| SYS
    ADM -->|"Administra usuarios, empleados, consulta reportes"| SYS
    SYS -->|"Sirve la aplicacion"| NAV
```

### 3.2 Diagrama de contenedores (C4 -- Nivel 2)

El diagrama de contenedores descompone el sistema en sus tres contenedores ejecutables y muestra las tecnologias y protocolos de comunicacion entre ellos.

```mermaid
graph TB
    subgraph Navegador["Navegador Web"]
        SPA["Frontend SPA
        ---
        Angular 21
        Standalone Components
        Angular Router
        Angular HttpClient
        SCSS
        ---
        Interfaz de usuario,
        validacion de formularios,
        gestion de estado local,
        proteccion de rutas"]
    end

    subgraph Servidor["Servidor de Aplicaciones"]
        API["API REST
        ---
        Spring Boot 4.0
        Java 25
        Spring Security
        Spring Data JPA
        Lombok
        ---
        Logica de negocio,
        autenticacion JWT,
        autorizacion por roles,
        validacion de datos"]
    end

    subgraph Datos["Capa de Datos"]
        DB[("PostgreSQL 15+
        ---
        Esquema relacional
        6 enums nativos
        9 tablas
        Integridad referencial
        via foreign keys")]
    end

    SPA -->|"HTTP/JSON
    (API REST)
    Puerto 8080"| API
    API -->|"JDBC
    (Spring Data JPA / Hibernate)
    Puerto 5432"| DB
```

### 3.3 Arquitectura del backend (capas)

El backend implementa una **arquitectura en capas** (Layered Architecture), el patron estandar de Spring Boot. Cada capa tiene una responsabilidad unica y solo puede depender de la capa inmediatamente inferior. Esta restriccion garantiza la separacion de responsabilidades y facilita la testeabilidad.

```mermaid
graph TD
    HTTP["Peticion HTTP
    (Cliente)"]

    subgraph "Capa de Presentacion"
        CTRL["Controller
        @RestController
        ---
        Recibe peticiones HTTP
        Valida entrada (@Valid)
        Delega al Service
        Retorna ResponseEntity"]
    end

    subgraph "Capa de Negocio"
        SVC["Service
        @Service
        ---
        Logica de negocio
        Orquestacion de operaciones
        Transacciones (@Transactional)
        Mapeo Entity-DTO"]
    end

    subgraph "Capa de Acceso a Datos"
        REPO["Repository
        @Repository / JpaRepository
        ---
        Consultas a base de datos
        Metodos derivados de JPA
        Queries personalizados"]
    end

    subgraph "Capa de Dominio"
        MODEL["Model / Entity
        @Entity
        ---
        Entidades JPA
        Mapeo a tablas
        Relaciones entre entidades
        Enums del dominio"]
    end

    subgraph "Capa Transversal"
        CONFIG["Config
        @Configuration
        ---
        Security, CORS"]
        EXCEPTION["Exception Handler
        @ControllerAdvice
        ---
        Manejo global de errores"]
        DTO["DTO
        ---
        Objetos de
        entrada/salida"]
    end

    HTTP --> CTRL
    CTRL --> SVC
    SVC --> REPO
    REPO --> MODEL
    CTRL -.-> DTO
    CTRL -.-> EXCEPTION
    SVC -.-> CONFIG
```

**Reglas de dependencia:**

| Capa | Puede depender de | NO puede depender de |
| --- | --- | --- |
| Controller | Service, DTO, Exception Handler | Repository, Model (directamente) |
| Service | Repository, Model, DTO | Controller |
| Repository | Model | Controller, Service |
| Model / Entity | Enums del dominio | Ninguna otra capa |
| Config | -- | Capas de negocio |

### 3.4 Estructura de paquetes

```
com.tiendaq.api
├── TiendaQApplication.java            -- Clase principal (@SpringBootApplication)
├── config/                             -- Configuraciones de Spring (pendiente)
├── controller/                         -- Controladores REST
│   ├── CarritoController.java
│   ├── ClienteController.java
│   ├── EmpleadoController.java
│   ├── FacturaController.java
│   ├── ItemController.java
│   ├── ProductoController.java
│   ├── StockController.java
│   └── UsuarioController.java
├── dto/                                -- Objetos de transferencia de datos (pendiente)
├── exception/                          -- Manejo global de excepciones (pendiente)
├── model/                              -- Entidades JPA
│   ├── enums/                          -- Enumeraciones del dominio
│   │   ├── Categoria.java
│   │   ├── Estado.java
│   │   ├── MetodoPago.java
│   │   ├── TipoDocumento.java
│   │   ├── TipoEmpleado.java
│   │   └── TipoUsuario.java
│   ├── Carrito.java
│   ├── Cliente.java
│   ├── Empleado.java
│   ├── Factura.java
│   ├── Items.java
│   ├── Producto.java
│   ├── Stock.java
│   └── Usuario.java
├── repository/                         -- Interfaces de acceso a datos
│   ├── CarritoRepository.java
│   ├── ClienteRepository.java
│   ├── EmpleadoRepository.java
│   ├── FacturaRepository.java
│   ├── ItemRepository.java
│   ├── ProductoRepository.java
│   ├── StockRepository.java
│   └── UsuarioRepository.java
└── service/                            -- Logica de negocio
    ├── CarritoService.java
    ├── ClienteService.java
    ├── EmpleadoService.java
    ├── FacturaService.java
    ├── ItemService.java
    ├── ProductoService.java
    ├── StockService.java
    └── UsuarioService.java
```

---

## 4. Modelo de datos

### 4.1 Que son las entidades de dominio

Las **entidades de dominio** son las clases que representan los conceptos fundamentales del negocio. En el contexto de TiendaQ, cada entidad modela un objeto del mundo real del comercio electronico: un usuario, un producto, una factura, etc.

En terminos tecnicos, cada entidad de dominio:

- Se mapea a una **tabla** en la base de datos PostgreSQL mediante anotaciones JPA (`@Entity`, `@Table`).
- Define los **atributos** del concepto de negocio (campos que se convierten en columnas).
- Establece las **relaciones** con otras entidades (`@ManyToOne`, `@OneToMany`, claves foraneas).
- Utiliza **enums** para campos con valores finitos y predefinidos (categorias, estados, roles).

Las entidades son el nucleo del sistema: toda la logica de negocio opera sobre ellas, y todo lo que se persiste en la base de datos pasa por ellas.

### 4.2 Diagrama entidad-relacion

```mermaid
erDiagram
    Usuario {
        int idUsuario PK
        varchar_20 nombre
        varchar_20 apellido
        enum tipoDocumento
        varchar_20 documento UK
        varchar_20 telefono UK
        varchar_70 correo UK
        varchar_100 direccion
        varchar_250 contrasena
        enum tipoUsuario
    }

    Empleado {
        int idEmpleado PK
        enum tipoEmpleado
        int idUsuario FK
    }

    Cliente {
        int idCliente PK
        int idUsuario FK
    }

    Producto {
        int idProducto PK
        enum categoria
        varchar_50 nombreProducto
        numeric_10_2 precioUnitario
    }

    Stock {
        int idStock PK
        int idProducto FK
        timestamp fechaIngreso
        int stock
    }

    CarritoCompra {
        int idCarritoCompra PK
        timestamp fechaCreacion
        enum estado
        int idUsuario FK
    }

    ItemCarrito {
        int idCarritoCompra PK_FK
        int idProducto PK_FK
        int cantidad
        numeric_10_2 precioUnitario
    }

    Factura {
        int idFactura PK
        timestamp fechaCompra
        numeric_10_2 totalCompra
        enum metodoPago
        numeric_10_2 iva
        int idEmpleado FK
        int idCliente FK
        int idCarritoCompra FK
    }

    DetalleFactura {
        int idFactura PK_FK
        int idProducto PK_FK
        int cantidad
        numeric_10_2 precioUnitario
    }

    Usuario ||--o{ Empleado : "es (herencia JOINED)"
    Usuario ||--o{ Cliente : "es (herencia JOINED)"
    Usuario ||--o{ CarritoCompra : "tiene"
    Producto ||--o{ Stock : "tiene registros de"
    Producto ||--o{ ItemCarrito : "esta en"
    Producto ||--o{ DetalleFactura : "aparece en"
    CarritoCompra ||--o{ ItemCarrito : "contiene"
    CarritoCompra ||--o| Factura : "genera"
    Empleado ||--o{ Factura : "procesa"
    Cliente ||--o{ Factura : "compra"
    Factura ||--o{ DetalleFactura : "detalla"
```

### 4.3 Descripcion de entidades

A continuacion se describe cada entidad del dominio con sus campos reales, derivados de las clases JPA del proyecto y del esquema PostgreSQL.

#### 4.3.1 Usuario

Representa a cualquier persona registrada en el sistema. Es la entidad base de la jerarquia de herencia (estrategia `JOINED`): tanto `Empleado` como `Cliente` extienden de `Usuario`.

**Clase JPA:** `com.tiendaq.api.model.Usuario`  
**Tabla:** `Usuario`  
**Estrategia de herencia:** `InheritanceType.JOINED`

| Campo | Tipo Java | Tipo SQL | Restricciones | Descripcion |
| --- | --- | --- | --- | --- |
| idUsuario | int | SERIAL | PK, auto-generado | Identificador unico del usuario |
| nombre | String | VARCHAR(20) | NOT NULL | Nombre del usuario |
| apellido | String | VARCHAR(20) | NOT NULL | Apellido del usuario |
| tipoDocumento | TipoDocumento (enum) | tipo_documento_enum | NOT NULL | Tipo de documento de identidad |
| documento | String | VARCHAR(20) | NOT NULL, UNIQUE | Numero de documento de identidad |
| telefono | String | VARCHAR(20) | NOT NULL, UNIQUE | Numero de telefono |
| correo | String | VARCHAR(70) | NOT NULL, UNIQUE | Correo electronico |
| direccion | String | VARCHAR(100) | NOT NULL | Direccion fisica |
| contrasena | String | VARCHAR(250) | NOT NULL | Contrasena hasheada con BCrypt |
| tipoUsuario | TipoUsuario (enum) | tipo_usuario_enum | NOT NULL | Tipo de usuario: REGISTRADO o SIN_REGISTRAR |

#### 4.3.2 Empleado

Representa a un trabajador del sistema (vendedor o administrador). Hereda todos los campos de `Usuario` via JPA JOINED inheritance, y anade sus campos propios.

**Clase JPA:** `com.tiendaq.api.model.Empleado`  
**Tabla:** `Empleado`  
**Extiende:** `Usuario` (via `@PrimaryKeyJoinColumn(name = "idUsuario")`)

| Campo | Tipo Java | Tipo SQL | Restricciones | Descripcion |
| --- | --- | --- | --- | --- |
| idEmpleado | int | SERIAL | Generado | Identificador especifico de empleado |
| tipoEmpleado | TipoEmpleado (enum) | tipo_empleado_enum | NOT NULL | Rol: ADMINISTRADOR o VENDEDOR |
| idUsuario | int | INT | FK -> Usuario(idUsuario) | Referencia al usuario base (clave de join) |

#### 4.3.3 Cliente

Representa a un comprador registrado. Hereda todos los campos de `Usuario`.

**Clase JPA:** `com.tiendaq.api.model.Cliente`  
**Tabla:** `Cliente`  
**Extiende:** `Usuario` (via `@PrimaryKeyJoinColumn(name = "idUsuario")`)

| Campo | Tipo Java | Tipo SQL | Restricciones | Descripcion |
| --- | --- | --- | --- | --- |
| idCliente | int | SERIAL | Generado | Identificador especifico de cliente |
| idUsuario | int | INT | FK -> Usuario(idUsuario) | Referencia al usuario base (clave de join) |

#### 4.3.4 Producto

Representa un articulo disponible para la venta en el catalogo de TiendaQ.

**Clase JPA:** `com.tiendaq.api.model.Producto`  
**Tabla:** `Producto`

| Campo | Tipo Java | Tipo SQL | Restricciones | Descripcion |
| --- | --- | --- | --- | --- |
| idProducto | int | SERIAL | PK, auto-generado | Identificador unico del producto |
| categoria | Categoria (enum) | categoria_producto_enum | NOT NULL | Categoria del producto |
| nombre | String | VARCHAR(50) | NOT NULL | Nombre del producto (mapeado a `nombreproducto`) |
| precioUnitario | double | NUMERIC(10,2) | NOT NULL | Precio unitario sin IVA |

#### 4.3.5 Stock

Representa un registro de ingreso de inventario para un producto. Cada entrada registra una cantidad y la fecha en que fue ingresada. El stock total de un producto se calcula sumando todos sus registros de stock menos las cantidades vendidas.

**Clase JPA:** `com.tiendaq.api.model.Stock`  
**Tabla:** `Stock`

| Campo | Tipo Java | Tipo SQL | Restricciones | Descripcion |
| --- | --- | --- | --- | --- |
| idStock | int | SERIAL | PK, auto-generado | Identificador del registro de stock |
| fechaIngreso | LocalDateTime | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Fecha y hora del ingreso |
| stock | int | INT | NOT NULL | Cantidad ingresada |
| producto | Producto | INT | FK -> Producto(idProducto), NOT NULL | Producto al que pertenece el ingreso |

#### 4.3.6 CarritoCompra

Representa el carrito de compras de un usuario. Sigue una maquina de estados que controla el ciclo de vida de la compra (ver seccion 5.3).

**Clase JPA:** `com.tiendaq.api.model.Carrito`  
**Tabla:** `CarritoCompra`

| Campo | Tipo Java | Tipo SQL | Restricciones | Descripcion |
| --- | --- | --- | --- | --- |
| idCarrito | int | SERIAL | PK, auto-generado | Identificador del carrito (mapeado a `idcarritocompra`) |
| fechaCreacion | LocalDateTime | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Fecha de creacion del carrito |
| estado | Estado (enum) | estado_carrito_enum | NOT NULL | Estado actual del carrito |
| usuario | Usuario | INT | FK -> Usuario(idUsuario), NOT NULL | Usuario propietario del carrito |

#### 4.3.7 ItemCarrito

Representa un producto dentro de un carrito con su cantidad y precio al momento de agregarlo. Usa una **clave compuesta** (idCarritoCompra + idProducto) que modela la relacion muchos-a-muchos entre CarritoCompra y Producto.

**Clase JPA:** `com.tiendaq.api.model.Items`  
**Tabla:** `ItemCarrito`  
**Clave compuesta:** `Items.ItemId` (idCarrito + idProducto) via `@IdClass`

| Campo | Tipo Java | Tipo SQL | Restricciones | Descripcion |
| --- | --- | --- | --- | --- |
| carrito | Carrito | INT | PK (parcial), FK -> CarritoCompra(idCarritoCompra) | Carrito al que pertenece el item |
| producto | Producto | INT | PK (parcial), FK -> Producto(idProducto) | Producto agregado al carrito |
| cantidad | int | INT | NOT NULL | Cantidad del producto en el carrito |
| precioUnitario | double | NUMERIC(10,2) | NOT NULL | Precio unitario al momento de agregar |

#### 4.3.8 Factura

Representa una transaccion de compra completada. Contiene los totales calculados, el metodo de pago y las referencias al cliente, empleado y carrito que originaron la compra.

**Clase JPA:** `com.tiendaq.api.model.Factura`  
**Tabla:** `Factura`

| Campo | Tipo Java | Tipo SQL | Restricciones | Descripcion |
| --- | --- | --- | --- | --- |
| idFactura | int | SERIAL | PK, auto-generado | Numero de factura |
| fechaCompra | LocalDateTime | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Fecha y hora de la compra |
| totalCompra | double | NUMERIC(10,2) | NOT NULL | Total de la compra (subtotal + IVA) |
| metodoPago | MetodoPago (enum) | metodo_pago_enum | NOT NULL | Metodo de pago utilizado |
| iva | double | NUMERIC(10,2) | NOT NULL | Monto del IVA (19%) |
| empleado | Empleado | INT | FK -> Empleado(idEmpleado), NOT NULL | Empleado que proceso la factura |
| cliente | Cliente | INT | FK -> Cliente(idCliente), NOT NULL | Cliente que realizo la compra |
| carrito | Carrito | INT | FK -> CarritoCompra(idCarritoCompra), NOT NULL | Carrito de origen |

#### 4.3.9 DetalleFactura

Snapshot de cada producto comprado al momento de la facturacion. Preserva el precio y cantidad como datos historicos inmutables. Usa clave compuesta (idFactura + idProducto).

**Tabla SQL:** `DetalleFactura`  
**Nota:** Esta entidad esta definida en el esquema SQL pero aun no tiene clase JPA dedicada en el proyecto; sera implementada como entidad con `@IdClass` o `@EmbeddedId`.

| Campo | Tipo SQL | Restricciones | Descripcion |
| --- | --- | --- | --- |
| idFactura | INT | PK (parcial), FK -> Factura(idFactura) | Factura a la que pertenece |
| idProducto | INT | PK (parcial), FK -> Producto(idProducto) | Producto facturado |
| cantidad | INT | NOT NULL, CHECK (cantidad > 0) | Cantidad vendida |
| precioUnitario | NUMERIC(10,2) | NOT NULL | Precio unitario al momento de la compra |

### 4.4 Enums del dominio

Los enums representan campos con un conjunto finito y cerrado de valores validos. En PostgreSQL se implementan como tipos ENUM nativos; en Java como enumeraciones que se mapean via `@Enumerated(EnumType.STRING)`.

| Enum (Java) | Tipo PostgreSQL | Valores | Uso |
| --- | --- | --- | --- |
| TipoDocumento | tipo_documento_enum | CC, TI, CE, PASAPORTE, NIT, RUT, OTRO | Tipo de documento del usuario |
| TipoUsuario | tipo_usuario_enum | REGISTRADO, SIN_REGISTRAR | Estado de registro del usuario |
| TipoEmpleado | tipo_empleado_enum | ADMINISTRADOR, VENDEDOR | Rol del empleado en el sistema |
| Categoria | categoria_producto_enum | ROPA, ACCESORIOS, LIBRERIA, PAPELERIA | Categoria del producto |
| Estado | estado_carrito_enum | VACIO, CON_PRODUCTOS, EN_PROCESO_DE_PAGO, PAGO_PENDIENTE, PAGO_EXITOSO | Estado del carrito de compras |
| MetodoPago | metodo_pago_enum | PSE, TARJETA_CREDITO, TARJETA_DEBITO, EFECTIVO, TRANSFERENCIA | Forma de pago de la factura |

**Nota:** El enum `TipoDocumento` en Java incluye los valores NIT, RUT y OTRO, que no estan presentes en el tipo PostgreSQL `tipo_documento_enum` (que solo define CC, TI, CE, PASAPORTE). Esta discrepancia debera alinearse durante la implementacion.

---

## 5. Diagramas de comportamiento

### 5.1 Diagramas de casos de uso

#### 5.1.1 Casos de uso: Autenticacion y autorizacion

```mermaid
graph LR
    ANON["Usuario Anonimo"]
    AUTH_USER["Usuario Autenticado"]
    SIS["Sistema"]

    subgraph "Modulo de Autenticacion"
        UC1["Registrarse como cliente
        (RF-001)"]
        UC2["Iniciar sesion
        (RF-002)"]
        UC3["Cerrar sesion
        (RF-005)"]
        UC4["Recuperar contrasena
        (RF-006)"]
        UC5["Renovar token JWT
        (RF-007)"]
        UC6["Validar token JWT
        (RF-003)"]
        UC7["Autorizar por rol
        (RF-004)"]
    end

    ANON --> UC1
    ANON --> UC2
    ANON --> UC4
    AUTH_USER --> UC3
    AUTH_USER --> UC5
    SIS --> UC6
    SIS --> UC7
```

#### 5.1.2 Casos de uso: Compras (catalogo, carrito, checkout)

```mermaid
graph LR
    CLI["Cliente"]
    SIS["Sistema"]

    subgraph "Modulo de Catalogo"
        UC8["Navegar catalogo
        (RF-010)"]
        UC9["Buscar por nombre
        (RF-014)"]
        UC10["Filtrar por categoria
        (RF-013)"]
        UC11["Filtrar por precio
        (RF-015)"]
        UC12["Ver detalle de producto
        (RF-017)"]
    end

    subgraph "Modulo de Carrito"
        UC13["Agregar producto al carrito
        (RF-019)"]
        UC14["Modificar cantidad
        (RF-020)"]
        UC15["Eliminar item
        (RF-021)"]
        UC16["Vaciar carrito
        (RF-022)"]
        UC17["Consultar carrito
        (RF-023)"]
        UC18["Validar stock
        (RF-024)"]
    end

    subgraph "Modulo de Checkout"
        UC19["Iniciar checkout
        (RF-026)"]
        UC20["Seleccionar metodo pago
        (RF-027)"]
        UC21["Confirmar compra
        (RF-028)"]
        UC22["Cancelar checkout
        (RF-029)"]
        UC23["Consultar historial
        (RF-030)"]
    end

    CLI --> UC8
    CLI --> UC9
    CLI --> UC10
    CLI --> UC11
    CLI --> UC12
    CLI --> UC13
    CLI --> UC14
    CLI --> UC15
    CLI --> UC16
    CLI --> UC17
    CLI --> UC19
    CLI --> UC20
    CLI --> UC21
    CLI --> UC22
    CLI --> UC23
    SIS --> UC18
```

#### 5.1.3 Casos de uso: Administracion (usuarios, inventario, reportes)

```mermaid
graph LR
    VEN["Vendedor"]
    ADM["Administrador"]
    SIS["Sistema"]

    subgraph "Gestion de Productos"
        UC24["Crear producto
        (RF-008)"]
        UC25["Actualizar producto
        (RF-011)"]
        UC26["Eliminar producto
        (RF-012)"]
    end

    subgraph "Gestion de Inventario"
        UC27["Registrar ingreso de stock
        (RF-034)"]
        UC28["Consultar stock
        (RF-035)"]
        UC29["Listar inventario
        (RF-036)"]
        UC30["Alerta stock bajo
        (RF-037)"]
    end

    subgraph "Gestion de Usuarios"
        UC31["Listar usuarios
        (RF-039)"]
        UC32["Crear empleado
        (RF-041)"]
        UC33["Actualizar usuario
        (RF-042)"]
        UC34["Eliminar usuario
        (RF-043)"]
        UC35["Cambiar rol
        (RF-044)"]
    end

    subgraph "Reportes"
        UC36["Ventas por periodo
        (RF-049)"]
        UC37["Productos mas vendidos
        (RF-050)"]
        UC38["Resumen inventario
        (RF-051)"]
        UC39["Dashboard KPIs
        (RF-054)"]
    end

    VEN --> UC24
    VEN --> UC25
    VEN --> UC26
    VEN --> UC27
    VEN --> UC28
    VEN --> UC29
    ADM --> UC24
    ADM --> UC25
    ADM --> UC26
    ADM --> UC27
    ADM --> UC28
    ADM --> UC29
    ADM --> UC31
    ADM --> UC32
    ADM --> UC33
    ADM --> UC34
    ADM --> UC35
    ADM --> UC36
    ADM --> UC37
    ADM --> UC38
    ADM --> UC39
    SIS --> UC30
```

### 5.2 Diagramas de secuencia

#### 5.2.1 Inicio de sesion (Login)

```mermaid
sequenceDiagram
    participant C as Cliente (Angular)
    participant CTRL as AuthController
    participant SVC as AuthService
    participant REPO as UsuarioRepository
    participant SEC as Spring Security
    participant DB as PostgreSQL

    C->>CTRL: POST /api/auth/login {correo, contrasena}
    CTRL->>SVC: autenticar(correo, contrasena)
    SVC->>REPO: findByCorreo(correo)
    REPO->>DB: SELECT * FROM Usuario WHERE correo = ?
    DB-->>REPO: Registro de usuario
    REPO-->>SVC: Optional de Usuario

    alt Usuario no encontrado
        SVC-->>CTRL: throw UsuarioNotFoundException
        CTRL-->>C: 401 Unauthorized {message: "Credenciales invalidas"}
    else Usuario encontrado
        SVC->>SEC: BCrypt.matches(contrasena, hash)
        alt Contrasena incorrecta
            SEC-->>SVC: false
            SVC-->>CTRL: throw AuthenticationException
            CTRL-->>C: 401 Unauthorized {message: "Credenciales invalidas"}
        else Contrasena correcta
            SEC-->>SVC: true
            SVC->>SVC: Generar JWT(idUsuario, correo, rol)
            SVC-->>CTRL: TokenResponse(jwt, expiracion)
            CTRL-->>C: 200 OK {token, expiresIn, tipoUsuario}
        end
    end
```

#### 5.2.2 Registro de cliente

```mermaid
sequenceDiagram
    participant C as Cliente (Angular)
    participant CTRL as UsuarioController
    participant SVC as UsuarioService
    participant CSVC as ClienteService
    participant REPO as UsuarioRepository
    participant CREPO as ClienteRepository
    participant DB as PostgreSQL

    C->>CTRL: POST /api/auth/registro {nombre, apellido, tipoDocumento, documento, telefono, correo, direccion, contrasena}
    CTRL->>CTRL: Validar campos (@Valid)

    alt Validacion fallida
        CTRL-->>C: 400 Bad Request {fieldErrors: [...]}
    else Validacion exitosa
        CTRL->>SVC: registrarCliente(datosRegistro)
        SVC->>REPO: existsByCorreo(correo)
        REPO->>DB: SELECT EXISTS(...)
        DB-->>REPO: true/false

        alt Correo duplicado
            SVC-->>CTRL: throw ConflictException
            CTRL-->>C: 409 Conflict {message: "El correo ya esta registrado"}
        else Datos unicos
            SVC->>SVC: BCrypt.hash(contrasena)
            SVC->>REPO: save(usuario con tipoUsuario=REGISTRADO)
            REPO->>DB: INSERT INTO Usuario (...)
            DB-->>REPO: Usuario creado
            SVC->>CSVC: crear(cliente con idUsuario)
            CSVC->>CREPO: save(cliente)
            CREPO->>DB: INSERT INTO Cliente (...)
            DB-->>CREPO: Cliente creado
            SVC-->>CTRL: UsuarioDTO (sin contrasena)
            CTRL-->>C: 201 Created {usuario}
        end
    end
```

#### 5.2.3 Navegar catalogo y buscar productos

```mermaid
sequenceDiagram
    participant C as Cliente (Angular)
    participant CTRL as ProductoController
    participant SVC as ProductoService
    participant REPO as ProductoRepository
    participant DB as PostgreSQL

    Note over C: Carga inicial del catalogo
    C->>CTRL: GET /api/productos?page=0&size=20&sortBy=nombre
    CTRL->>SVC: listarTodos(pageable)
    SVC->>REPO: findAll(pageable)
    REPO->>DB: SELECT * FROM Producto ORDER BY nombreproducto LIMIT 20 OFFSET 0
    DB-->>REPO: Lista paginada
    REPO-->>SVC: Page de Producto
    SVC-->>CTRL: Page de ProductoDTO
    CTRL-->>C: 200 OK {content: [...], totalElements, totalPages}

    Note over C: Busqueda por nombre
    C->>CTRL: GET /api/productos?nombre=cuaderno&page=0
    CTRL->>SVC: buscarPorNombre("cuaderno", pageable)
    SVC->>REPO: findByNombreContainingIgnoreCase("cuaderno", pageable)
    REPO->>DB: SELECT * FROM Producto WHERE LOWER(nombreproducto) LIKE '%cuaderno%'
    DB-->>REPO: Resultados filtrados
    REPO-->>SVC: Page de Producto
    SVC-->>CTRL: Page de ProductoDTO
    CTRL-->>C: 200 OK {content: [...], totalElements, totalPages}

    Note over C: Filtro por categoria
    C->>CTRL: GET /api/productos/categoria/PAPELERIA
    CTRL->>SVC: buscarPorCategoria(PAPELERIA)
    SVC->>REPO: findByCategoria(PAPELERIA)
    REPO->>DB: SELECT * FROM Producto WHERE categoria = 'PAPELERIA'
    DB-->>REPO: Productos filtrados
    REPO-->>SVC: Lista de Producto
    SVC-->>CTRL: Lista de ProductoDTO
    CTRL-->>C: 200 OK [productos]
```

#### 5.2.4 Agregar producto al carrito

```mermaid
sequenceDiagram
    participant C as Cliente (Angular)
    participant CTRL as ItemController
    participant SVC as ItemService
    participant CSVC as CarritoService
    participant SSVC as StockService
    participant REPO as ItemRepository
    participant DB as PostgreSQL

    C->>CTRL: POST /api/items {idCarrito, idProducto, cantidad}
    CTRL->>SVC: agregar(item)

    SVC->>SSVC: consultarStockDisponible(idProducto)
    SSVC->>DB: SELECT SUM(stock) FROM Stock WHERE idProducto = ?
    DB-->>SSVC: stockTotal

    alt Stock insuficiente
        SVC-->>CTRL: throw StockInsuficienteException
        CTRL-->>C: 409 Conflict {message: "Stock insuficiente", disponible: N}
    else Stock suficiente
        SVC->>REPO: findById(idCarrito, idProducto)
        REPO->>DB: SELECT * FROM ItemCarrito WHERE ...

        alt Item ya existe en carrito
            SVC->>SVC: item.cantidad += nuevaCantidad
            SVC->>REPO: save(item actualizado)
            REPO->>DB: UPDATE ItemCarrito SET cantidad = ? WHERE ...
        else Item nuevo
            SVC->>SVC: precioUnitario = producto.getPrecioUnitario()
            SVC->>REPO: save(nuevo item)
            REPO->>DB: INSERT INTO ItemCarrito (...)
        end

        DB-->>REPO: Item guardado
        SVC->>CSVC: actualizarEstado(idCarrito, CON_PRODUCTOS)
        CSVC->>DB: UPDATE CarritoCompra SET estado = 'CON_PRODUCTOS' WHERE ...
        SVC-->>CTRL: Item creado/actualizado
        CTRL-->>C: 200 OK {item}
    end
```

#### 5.2.5 Checkout y generacion de factura

```mermaid
sequenceDiagram
    participant C as Cliente (Angular)
    participant CTRL as FacturaController
    participant FSVC as FacturaService
    participant CSVC as CarritoService
    participant ISVC as ItemService
    participant SSVC as StockService
    participant DB as PostgreSQL

    Note over C: Paso 1 - Iniciar checkout
    C->>CTRL: POST /api/checkout/iniciar {idCarrito}
    CTRL->>CSVC: iniciarCheckout(idCarrito)
    CSVC->>ISVC: obtenerItems(idCarrito)
    ISVC->>DB: SELECT * FROM ItemCarrito WHERE idCarritoCompra = ?
    DB-->>ISVC: Lista de items

    loop Por cada item en el carrito
        CSVC->>SSVC: validarStock(idProducto, cantidad)
    end

    alt Algun producto sin stock
        CSVC-->>CTRL: throw StockInsuficienteException
        CTRL-->>C: 409 Conflict {productosInsuficientes: [...]}
    else Todo con stock
        CSVC->>DB: UPDATE CarritoCompra SET estado = 'EN_PROCESO_DE_PAGO'
        CSVC-->>CTRL: Checkout iniciado
        CTRL-->>C: 200 OK {carritoId, estado: "EN_PROCESO_DE_PAGO"}
    end

    Note over C: Paso 2 - Seleccionar metodo de pago
    C->>CTRL: POST /api/checkout/pago {idCarrito, metodoPago: "TARJETA_CREDITO"}
    CTRL->>CSVC: confirmarPago(idCarrito, metodoPago)
    CSVC->>DB: UPDATE CarritoCompra SET estado = 'PAGO_PENDIENTE'
    CTRL-->>C: 200 OK {estado: "PAGO_PENDIENTE"}

    Note over C: Paso 3 - Confirmar y generar factura
    C->>CTRL: POST /api/checkout/confirmar {idCarrito}
    CTRL->>FSVC: generarFactura(idCarrito)

    FSVC->>FSVC: Calcular subtotal, IVA (19%), total
    FSVC->>DB: INSERT INTO Factura (...)
    DB-->>FSVC: Factura creada

    loop Por cada item
        FSVC->>DB: INSERT INTO DetalleFactura (idFactura, idProducto, cantidad, precioUnitario)
        FSVC->>SSVC: descontarStock(idProducto, cantidad)
        SSVC->>DB: UPDATE Stock ...
    end

    FSVC->>CSVC: actualizarEstado(idCarrito, PAGO_EXITOSO)
    CSVC->>DB: UPDATE CarritoCompra SET estado = 'PAGO_EXITOSO'

    FSVC-->>CTRL: FacturaDTO completa
    CTRL-->>C: 201 Created {factura con detalles}
```

#### 5.2.6 Gestion de inventario (registrar ingreso de stock)

```mermaid
sequenceDiagram
    participant E as Empleado (Angular)
    participant CTRL as StockController
    participant SVC as StockService
    participant PSVC as ProductoService
    participant REPO as StockRepository
    participant DB as PostgreSQL

    E->>CTRL: POST /api/stock {idProducto, stock: 50}
    CTRL->>SVC: registrarIngreso(stockDTO)

    SVC->>PSVC: buscarPorId(idProducto)
    PSVC->>DB: SELECT * FROM Producto WHERE idProducto = ?
    DB-->>PSVC: Producto

    alt Producto no existe
        PSVC-->>SVC: Optional.empty()
        SVC-->>CTRL: throw ResourceNotFoundException
        CTRL-->>E: 404 Not Found {message: "Producto no encontrado"}
    else Producto existe
        SVC->>SVC: stock.setFechaIngreso(LocalDateTime.now())
        SVC->>SVC: stock.setProducto(producto)
        SVC->>REPO: save(stock)
        REPO->>DB: INSERT INTO Stock (idProducto, fechaIngreso, stock) VALUES (?, ?, 50)
        DB-->>REPO: Stock registrado
        REPO-->>SVC: Stock guardado
        SVC-->>CTRL: StockDTO
        CTRL-->>E: 201 Created {idStock, idProducto, fechaIngreso, stock: 50}
    end

    Note over E: Consultar inventario completo
    E->>CTRL: GET /api/stock/producto/{idProducto}
    CTRL->>SVC: listarPorProducto(idProducto)
    SVC->>REPO: findByProductoIdProducto(idProducto)
    REPO->>DB: SELECT * FROM Stock WHERE idProducto = ?
    DB-->>REPO: Historial de ingresos
    SVC-->>CTRL: Lista de StockDTO
    CTRL-->>E: 200 OK [{idStock, fechaIngreso, stock}, ...]
```

### 5.3 Diagramas de estados

#### 5.3.1 Estados del carrito de compras

El carrito sigue una maquina de estados finita con transiciones controladas. Cualquier transicion no listada es invalida y el sistema la rechaza con HTTP 400.

```mermaid
stateDiagram-v2
    [*] --> VACIO : Carrito creado

    VACIO --> CON_PRODUCTOS : Agregar primer item (RF-019)

    CON_PRODUCTOS --> VACIO : Vaciar carrito (RF-022) o eliminar ultimo item (RF-021)
    CON_PRODUCTOS --> EN_PROCESO_DE_PAGO : Iniciar checkout (RF-026)

    EN_PROCESO_DE_PAGO --> CON_PRODUCTOS : Cancelar checkout (RF-029)
    EN_PROCESO_DE_PAGO --> PAGO_PENDIENTE : Confirmar metodo de pago (RF-027)

    PAGO_PENDIENTE --> PAGO_EXITOSO : Pago procesado, factura generada (RF-028)

    PAGO_EXITOSO --> [*] : Carrito finalizado
```

**Transiciones validas:**

| Estado origen | Estado destino | Accion disparadora | RF |
| --- | --- | --- | --- |
| (nuevo) | VACIO | Crear carrito | RF-018 |
| VACIO | CON_PRODUCTOS | Agregar item al carrito | RF-019 |
| CON_PRODUCTOS | VACIO | Vaciar carrito o eliminar ultimo item | RF-021, RF-022 |
| CON_PRODUCTOS | EN_PROCESO_DE_PAGO | Iniciar checkout | RF-026 |
| EN_PROCESO_DE_PAGO | CON_PRODUCTOS | Cancelar checkout | RF-029 |
| EN_PROCESO_DE_PAGO | PAGO_PENDIENTE | Confirmar metodo de pago | RF-027 |
| PAGO_PENDIENTE | PAGO_EXITOSO | Generar factura exitosamente | RF-028 |

#### 5.3.2 Ciclo de vida de una factura

La factura es un registro inmutable una vez creado. Su ciclo de vida esta acoplado al del carrito.

```mermaid
stateDiagram-v2
    [*] --> CarritoEnPago : Carrito en PAGO_PENDIENTE

    CarritoEnPago --> ValidandoStock : Confirmar compra
    ValidandoStock --> CalculandoTotales : Stock verificado OK

    CalculandoTotales --> GenerandoFactura : Subtotal + IVA calculados
    GenerandoFactura --> CreandoDetalles : INSERT Factura

    CreandoDetalles --> DescontandoStock : INSERT DetalleFactura por cada item
    DescontandoStock --> FacturaGenerada : Stock descontado

    FacturaGenerada --> [*] : Factura inmutable

    ValidandoStock --> ErrorStock : Stock insuficiente
    ErrorStock --> [*] : Rollback transaccional
```

---

## 6. Diseno de API REST

### 6.1 Convenciones

La API REST sigue las siguientes convenciones de diseno:

| Aspecto | Convencion |
| --- | --- |
| Base URL | `/api/` |
| Formato | JSON (`Content-Type: application/json`) |
| Nombres de recursos | Sustantivos en plural y minusculas (`/api/productos`, `/api/usuarios`) |
| Identificadores | Segmentos de ruta (`/api/productos/{id}`) |
| Paginacion | Parametros `page` (0-indexed) y `size` (default: 20, max: 100) |
| Ordenamiento | Parametros `sortBy` y `direction` (ASC/DESC) |
| Filtros | Parametros de query (`?categoria=ROPA&nombre=cuaderno`) |
| Autenticacion | Header `Authorization: Bearer {JWT}` |
| Codigos HTTP | 200 (OK), 201 (Created), 204 (No Content), 400 (Bad Request), 401 (Unauthorized), 403 (Forbidden), 404 (Not Found), 409 (Conflict) |
| Errores | Formato estandarizado con timestamp, status, error, message, path (RF-058) |

### 6.2 Mapa de endpoints por modulo

#### 6.2.1 Autenticacion (`/api/auth`)

| Metodo | Ruta | Descripcion | Autenticacion | RF |
| --- | --- | --- | --- | --- |
| POST | `/api/auth/registro` | Registrar nuevo cliente | No | RF-001 |
| POST | `/api/auth/login` | Iniciar sesion, obtener JWT | No | RF-002 |
| POST | `/api/auth/logout` | Cerrar sesion (invalidar token) | Si | RF-005 |
| POST | `/api/auth/recuperar` | Solicitar recuperacion de contrasena | No | RF-006 |
| POST | `/api/auth/renovar` | Renovar token JWT | Si | RF-007 |

#### 6.2.2 Usuarios (`/api/usuarios`)

| Metodo | Ruta | Descripcion | Autenticacion | RF |
| --- | --- | --- | --- | --- |
| GET | `/api/usuarios` | Listar todos los usuarios (paginado) | ADMINISTRADOR | RF-039 |
| GET | `/api/usuarios/{id}` | Consultar usuario por ID | ADMINISTRADOR | RF-040 |
| POST | `/api/usuarios` | Crear usuario | ADMINISTRADOR | RF-041 |
| PUT | `/api/usuarios/{id}` | Actualizar datos de usuario | ADMINISTRADOR | RF-042 |
| DELETE | `/api/usuarios/{id}` | Eliminar usuario | ADMINISTRADOR | RF-043 |

#### 6.2.3 Clientes (`/api/clientes`)

| Metodo | Ruta | Descripcion | Autenticacion | RF |
| --- | --- | --- | --- | --- |
| GET | `/api/clientes/{idUsuario}` | Consultar cliente por ID de usuario | Si | RF-040 |
| POST | `/api/clientes` | Crear registro de cliente | Si | RF-001 |
| PUT | `/api/clientes/{idUsuario}` | Actualizar datos de cliente | Si | RF-042 |

#### 6.2.4 Empleados (`/api/empleados`)

| Metodo | Ruta | Descripcion | Autenticacion | RF |
| --- | --- | --- | --- | --- |
| GET | `/api/empleados/{id}` | Consultar empleado por ID | ADMINISTRADOR | RF-040 |
| GET | `/api/empleados/usuario/{idUsuario}` | Consultar empleado por ID de usuario | ADMINISTRADOR | RF-040 |

#### 6.2.5 Productos (`/api/productos`)

| Metodo | Ruta | Descripcion | Autenticacion | RF |
| --- | --- | --- | --- | --- |
| GET | `/api/productos` | Listar todos los productos (paginado) | No | RF-010 |
| GET | `/api/productos/{id}` | Consultar producto por ID | No | RF-009 |
| GET | `/api/productos/categoria/{categoria}` | Filtrar productos por categoria | No | RF-013 |
| POST | `/api/productos` | Crear producto | VENDEDOR, ADMINISTRADOR | RF-008 |
| PUT | `/api/productos/{id}` | Actualizar producto | VENDEDOR, ADMINISTRADOR | RF-011 |
| DELETE | `/api/productos/{id}` | Eliminar producto | VENDEDOR, ADMINISTRADOR | RF-012 |

#### 6.2.6 Stock (`/api/stock`)

| Metodo | Ruta | Descripcion | Autenticacion | RF |
| --- | --- | --- | --- | --- |
| GET | `/api/stock/producto/{idProducto}` | Listar registros de stock por producto | VENDEDOR, ADMINISTRADOR | RF-035 |
| GET | `/api/stock/{id}` | Consultar registro de stock por ID | VENDEDOR, ADMINISTRADOR | RF-035 |
| POST | `/api/stock` | Registrar ingreso de stock | VENDEDOR, ADMINISTRADOR | RF-034 |
| PUT | `/api/stock/{id}` | Actualizar registro de stock | ADMINISTRADOR | RF-038 |
| DELETE | `/api/stock/{id}` | Eliminar registro de stock | ADMINISTRADOR | RF-038 |

#### 6.2.7 Carritos (`/api/carritos`)

| Metodo | Ruta | Descripcion | Autenticacion | RF |
| --- | --- | --- | --- | --- |
| GET | `/api/carritos/usuario/{idUsuario}` | Listar carritos de un usuario | Si (propietario) | RF-023 |
| GET | `/api/carritos/{id}` | Consultar carrito por ID | Si (propietario) | RF-023 |
| POST | `/api/carritos` | Crear carrito | Si | RF-018 |
| PUT | `/api/carritos/{id}` | Actualizar carrito (estado) | Si (propietario) | RF-025 |
| DELETE | `/api/carritos/{id}` | Eliminar carrito | Si (propietario) | RF-022 |

#### 6.2.8 Items del carrito (`/api/items`)

| Metodo | Ruta | Descripcion | Autenticacion | RF |
| --- | --- | --- | --- | --- |
| GET | `/api/items/carrito/{idCarrito}` | Listar items de un carrito | Si (propietario) | RF-023 |
| POST | `/api/items` | Agregar item al carrito | Si (propietario) | RF-019 |
| PUT | `/api/items` | Actualizar cantidad de item | Si (propietario) | RF-020 |
| DELETE | `/api/items/{idCarrito}/{idProducto}` | Eliminar item del carrito | Si (propietario) | RF-021 |

#### 6.2.9 Facturas (`/api/facturas`)

| Metodo | Ruta | Descripcion | Autenticacion | RF |
| --- | --- | --- | --- | --- |
| GET | `/api/facturas/cliente/{idCliente}` | Listar facturas de un cliente | Si (propietario), VENDEDOR, ADMINISTRADOR | RF-030 |
| GET | `/api/facturas/{id}` | Consultar detalle de factura | Si (propietario), VENDEDOR, ADMINISTRADOR | RF-031 |
| POST | `/api/facturas` | Generar factura | Si | RF-028 |

#### 6.2.10 Checkout (`/api/checkout`) -- Pendiente de implementacion

| Metodo | Ruta | Descripcion | Autenticacion | RF |
| --- | --- | --- | --- | --- |
| POST | `/api/checkout/iniciar` | Iniciar proceso de checkout | Si (propietario) | RF-026 |
| POST | `/api/checkout/pago` | Confirmar metodo de pago | Si (propietario) | RF-027 |
| POST | `/api/checkout/confirmar` | Confirmar compra y generar factura | Si (propietario) | RF-028 |
| POST | `/api/checkout/cancelar` | Cancelar checkout | Si (propietario) | RF-029 |

#### 6.2.11 Perfil (`/api/perfil`) -- Pendiente de implementacion

| Metodo | Ruta | Descripcion | Autenticacion | RF |
| --- | --- | --- | --- | --- |
| GET | `/api/perfil` | Consultar perfil del usuario autenticado | Si | RF-046 |
| PUT | `/api/perfil` | Actualizar perfil propio | Si | RF-047 |
| PUT | `/api/perfil/contrasena` | Cambiar contrasena | Si | RF-048 |

#### 6.2.12 Reportes (`/api/reportes`) -- Pendiente de implementacion

| Metodo | Ruta | Descripcion | Autenticacion | RF |
| --- | --- | --- | --- | --- |
| GET | `/api/reportes/ventas` | Reporte de ventas por periodo | ADMINISTRADOR | RF-049 |
| GET | `/api/reportes/productos-top` | Productos mas vendidos | ADMINISTRADOR | RF-050 |
| GET | `/api/reportes/inventario` | Resumen de inventario | ADMINISTRADOR | RF-051 |
| GET | `/api/reportes/ventas-categoria` | Ventas por categoria | ADMINISTRADOR | RF-052 |
| GET | `/api/reportes/ventas-empleado` | Ventas por empleado | ADMINISTRADOR | RF-053 |
| GET | `/api/reportes/dashboard` | Dashboard de KPIs | ADMINISTRADOR | RF-054 |

---

## 7. Cobertura de modulos del SRS

La siguiente tabla mapea cada modulo del SRS con su cobertura en este SDD, asegurando trazabilidad completa entre los 72 requerimientos funcionales y el diseno.

| Modulo SRS | Requerimientos | Secciones SDD |
| --- | --- | --- |
| Autenticacion y autorizacion | RF-001 a RF-007 | 5.1.1, 5.2.1, 5.2.2, 6.2.1 |
| Gestion de productos | RF-008 a RF-013 | 4.3.4, 5.1.3, 5.2.3, 6.2.5 |
| Catalogo y busqueda | RF-014 a RF-017 | 5.1.2, 5.2.3, 6.2.5 |
| Carrito de compras | RF-018 a RF-025 | 4.3.6, 4.3.7, 5.1.2, 5.2.4, 5.3.1, 6.2.7, 6.2.8 |
| Checkout y facturacion | RF-026 a RF-033 | 4.3.8, 4.3.9, 5.1.2, 5.2.5, 5.3.2, 6.2.9, 6.2.10 |
| Gestion de inventario | RF-034 a RF-038 | 4.3.5, 5.1.3, 5.2.6, 6.2.6 |
| Gestion de usuarios | RF-039 a RF-045 | 4.3.1, 4.3.2, 4.3.3, 5.1.3, 6.2.2, 6.2.3, 6.2.4 |
| Perfil de usuario | RF-046 a RF-048 | 6.2.11 |
| Reportes y estadisticas | RF-049 a RF-054 | 5.1.3, 6.2.12 |
| Validacion y errores | RF-055 a RF-059 | 3.3, 6.1 |
| Frontend | RF-060 a RF-072 | 2, 3.2 |
