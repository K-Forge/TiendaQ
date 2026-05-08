# Esquema de Base de Datos — TiendaQ

**Motor:** PostgreSQL 15+
**Migraciones:** Flyway (`src/main/resources/db/migration/`)
**Timezone:** America/Bogota (`SET timezone = 'America/Bogota'`)
**Moneda:** COP con `NUMERIC(15,2)` (ver [ADR-0004](adr/0004-money-bigdecimal-currency-cop.md))

---

## Enums de PostgreSQL

```sql
CREATE TYPE tipo_rol        AS ENUM ('CLIENTE', 'EMPLEADO');
CREATE TYPE tipo_documento  AS ENUM ('CC', 'TI', 'CE', 'PASAPORTE');
CREATE TYPE tipo_stock      AS ENUM ('ENTRADA', 'SALIDA', 'AJUSTE');
CREATE TYPE estado_carrito  AS ENUM ('ACTIVO', 'PROCESANDO', 'COMPLETADO', 'EXPIRADO');
CREATE TYPE estado_factura  AS ENUM ('CREADA', 'PAGADA', 'CANCELADA');
CREATE TYPE estado_pago     AS ENUM ('PENDIENTE', 'APROBADO', 'RECHAZADO', 'CANCELADO');
```

Mapeo JPA con `@JdbcTypeCode(SqlTypes.NAMED_ENUM)` para evitar conversion a `VARCHAR`:

```java
@Enumerated(EnumType.STRING)
@JdbcTypeCode(SqlTypes.NAMED_ENUM)
@Column(columnDefinition = "tipo_rol")
private TipoRol rol;
```

---

## Tablas

### Bounded Context: Identidad

#### `usuario`

```sql
CREATE TABLE usuario (
    id             BIGSERIAL    PRIMARY KEY,
    nombre         VARCHAR(100) NOT NULL,
    apellido       VARCHAR(100) NOT NULL,
    email          VARCHAR(150) UNIQUE NOT NULL,
    password_hash  VARCHAR(255) NOT NULL,
    rol            tipo_rol     NOT NULL,
    deleted_at     TIMESTAMPTZ,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_usuario_email_activo ON usuario(email) WHERE deleted_at IS NULL;
```

#### `cliente`

```sql
CREATE TABLE cliente (
    id_usuario       BIGINT        PRIMARY KEY REFERENCES usuario(id),
    telefono         VARCHAR(20),
    direccion        VARCHAR(255),
    tipo_documento   tipo_documento NOT NULL,
    numero_documento VARCHAR(20)    NOT NULL,
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);
```

#### `empleado`

```sql
CREATE TABLE empleado (
    id_usuario    BIGINT      PRIMARY KEY REFERENCES usuario(id),
    cargo         VARCHAR(100),
    fecha_ingreso DATE,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

#### `revoked_token` (soporte JWT)

```sql
CREATE TABLE revoked_token (
    jti        UUID        PRIMARY KEY,
    user_id    BIGINT      NOT NULL REFERENCES usuario(id),
    expires_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_revoked_token_expires ON revoked_token(expires_at);
```

---

### Bounded Context: Catalogo

#### `categoria`

```sql
CREATE TABLE categoria (
    id          BIGSERIAL    PRIMARY KEY,
    nombre      VARCHAR(100) UNIQUE NOT NULL,
    descripcion TEXT,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
```

#### `producto`

```sql
CREATE TABLE producto (
    id           BIGSERIAL      PRIMARY KEY,
    nombre       VARCHAR(200)   NOT NULL,
    descripcion  TEXT,
    precio       NUMERIC(15,2)  NOT NULL CHECK (precio > 0),
    currency     CHAR(3)        NOT NULL DEFAULT 'COP',
    imagen_url   VARCHAR(500),
    activo       BOOLEAN        NOT NULL DEFAULT TRUE,
    categoria_id BIGINT         NOT NULL REFERENCES categoria(id),
    version      INTEGER        NOT NULL DEFAULT 0,
    deleted_at   TIMESTAMPTZ,
    created_at   TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_producto_categoria ON producto(categoria_id);
CREATE INDEX idx_producto_activo ON producto(nombre) WHERE deleted_at IS NULL;
```

---

### Bounded Context: Inventario

#### `stock_level`

```sql
CREATE TABLE stock_level (
    id          BIGSERIAL PRIMARY KEY,
    producto_id BIGINT    UNIQUE NOT NULL REFERENCES producto(id),
    cantidad    INTEGER   NOT NULL DEFAULT 0 CHECK (cantidad >= 0),
    version     INTEGER   NOT NULL DEFAULT 0,
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_stock_level_producto ON stock_level(producto_id);
```

#### `stock_entry`

```sql
CREATE TABLE stock_entry (
    id          BIGSERIAL   PRIMARY KEY,
    producto_id BIGINT      NOT NULL REFERENCES producto(id),
    tipo        tipo_stock  NOT NULL,
    cantidad    INTEGER     NOT NULL CHECK (cantidad > 0),
    motivo      VARCHAR(255),
    empleado_id BIGINT      REFERENCES empleado(id_usuario),
    deleted_at  TIMESTAMPTZ,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_stock_entry_producto ON stock_entry(producto_id);
CREATE INDEX idx_stock_entry_fecha ON stock_entry(created_at);
```

#### `stock_reservation`

```sql
CREATE TABLE stock_reservation (
    id          BIGSERIAL   PRIMARY KEY,
    producto_id BIGINT      NOT NULL REFERENCES producto(id),
    carrito_id  BIGINT      NOT NULL,
    cantidad    INTEGER     NOT NULL CHECK (cantidad > 0),
    expires_at  TIMESTAMPTZ NOT NULL DEFAULT (NOW() + INTERVAL '10 minutes'),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_stock_reservation_expires ON stock_reservation(expires_at);
CREATE INDEX idx_stock_reservation_producto ON stock_reservation(producto_id);
```

---

### Bounded Context: Carrito

#### `carrito`

```sql
CREATE TABLE carrito (
    id         BIGSERIAL      PRIMARY KEY,
    cliente_id BIGINT         NOT NULL REFERENCES cliente(id_usuario),
    estado     estado_carrito NOT NULL DEFAULT 'ACTIVO',
    version    INTEGER        NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_carrito_cliente ON carrito(cliente_id);
CREATE INDEX idx_carrito_estado ON carrito(estado);
```

#### `item_carrito`

```sql
CREATE TABLE item_carrito (
    carrito_id         BIGINT        NOT NULL REFERENCES carrito(id) ON DELETE CASCADE,
    producto_id        BIGINT        NOT NULL REFERENCES producto(id),
    cantidad           INTEGER       NOT NULL CHECK (cantidad > 0),
    precio_unitario    NUMERIC(15,2) NOT NULL,
    PRIMARY KEY (carrito_id, producto_id)
);

CREATE INDEX idx_item_carrito_producto ON item_carrito(producto_id);
```

---

### Bounded Context: Pedidos

#### `factura`

```sql
CREATE TABLE factura (
    id         BIGSERIAL      PRIMARY KEY,
    cliente_id BIGINT         NOT NULL REFERENCES cliente(id_usuario),
    fecha      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    subtotal   NUMERIC(15,2)  NOT NULL,
    iva        NUMERIC(15,2)  NOT NULL,
    total      NUMERIC(15,2)  NOT NULL,
    currency   CHAR(3)        NOT NULL DEFAULT 'COP',
    estado     estado_factura NOT NULL DEFAULT 'CREADA',
    created_at TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_factura_cliente ON factura(cliente_id);
CREATE INDEX idx_factura_estado ON factura(estado);
CREATE INDEX idx_factura_fecha ON factura(fecha);
```

#### `detalle_factura`

```sql
CREATE TABLE detalle_factura (
    id               BIGSERIAL     PRIMARY KEY,
    factura_id       BIGINT        NOT NULL REFERENCES factura(id),
    producto_id      BIGINT        NOT NULL REFERENCES producto(id),
    nombre_producto  VARCHAR(200)  NOT NULL,  -- snapshot
    precio_unitario  NUMERIC(15,2) NOT NULL,  -- snapshot
    cantidad         INTEGER       NOT NULL CHECK (cantidad > 0),
    subtotal         NUMERIC(15,2) NOT NULL
);

CREATE INDEX idx_detalle_factura_factura ON detalle_factura(factura_id);
```

---

### Bounded Context: Pagos

#### `intento_pago`

```sql
CREATE TABLE intento_pago (
    id                   BIGSERIAL    PRIMARY KEY,
    factura_id           BIGINT       NOT NULL REFERENCES factura(id),
    wompi_transaction_id VARCHAR(100) UNIQUE,
    monto                NUMERIC(15,2) NOT NULL,
    currency             CHAR(3)      NOT NULL DEFAULT 'COP',
    estado               estado_pago  NOT NULL DEFAULT 'PENDIENTE',
    created_at           TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_intento_pago_factura ON intento_pago(factura_id);
CREATE INDEX idx_intento_pago_wompi ON intento_pago(wompi_transaction_id);
```

#### `processed_webhook` (idempotencia)

```sql
CREATE TABLE processed_webhook (
    transaction_id VARCHAR(100) PRIMARY KEY,
    processed_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
```

---

### Transversal: Auditoria

#### `audit_log`

```sql
CREATE TABLE audit_log (
    id          BIGSERIAL    PRIMARY KEY,
    usuario_id  BIGINT       REFERENCES usuario(id),
    accion      VARCHAR(100) NOT NULL,
    entidad     VARCHAR(100) NOT NULL,
    entidad_id  BIGINT,
    antes       JSONB,
    despues     JSONB,
    ip          INET,
    fecha       TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_log_entidad ON audit_log(entidad, entidad_id);
CREATE INDEX idx_audit_log_usuario ON audit_log(usuario_id);
CREATE INDEX idx_audit_log_fecha ON audit_log(fecha);
```

Tabla inmutable: nunca `UPDATE` ni `DELETE` en `audit_log`.

---

## Soft-delete por entidad

| Tabla | Estrategia | Campo |
|-------|-----------|-------|
| `usuario` | Soft | `deleted_at TIMESTAMPTZ` |
| `producto` | Soft | `deleted_at TIMESTAMPTZ` |
| `stock_entry` | Soft | `deleted_at TIMESTAMPTZ` |
| `carrito` | Hard (job 30 dias) | `updated_at` como referencia |
| `item_carrito` | Cascade con carrito | — |
| `factura` | Nunca eliminar | — |
| `intento_pago` | Nunca eliminar | — |
| `audit_log` | Nunca eliminar | — |

Ver [ADR-0007](adr/0007-soft-delete-por-entidad.md).

---

## Gestion de migraciones (Flyway)

```
src/main/resources/db/migration/
├── V1__initial_schema.sql          # Esquema completo (enums + todas las tablas)
├── V2__add_producto_imagen.sql     # Campo imagen_url en producto
├── V3__bigdecimal_migration.sql    # double → NUMERIC(15,2)
├── V4__add_refresh_token.sql       # Tabla revoked_token
├── V5__add_stock_tables.sql        # stock_reservation, stock_entry
└── V6__add_audit_log.sql           # Tabla audit_log
```

Regla: una vez aplicada, una migracion **nunca se modifica**. Correcciones = nueva migracion.

Ver [ADR-0005](adr/0005-flyway-vs-ddl-auto.md).

---

## Notas de implementacion

- **`@CreationTimestamp` / `@UpdateTimestamp`** en todas las entidades JPA con `created_at` / `updated_at`.
- **`@Version`** en `Producto`, `Carrito`, `StockLevel` (ver [ADR-0009](adr/0009-optimistic-locking-version-column.md)).
- **`@SQLDelete` + `@Where`** en entidades con soft-delete (ver [ADR-0007](adr/0007-soft-delete-por-entidad.md)).
- **`@JdbcTypeCode(SqlTypes.NAMED_ENUM)`** en todos los campos enum para mapear a tipos nativos PostgreSQL.
- Todos los indices en FK estan definidos explicitamente. Hibernate no los crea automaticamente.
