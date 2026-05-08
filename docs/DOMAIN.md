# Modelo de Dominio — TiendaQ

Glosario ubicuo, bounded contexts, agregados, eventos de dominio y context map.

---

## Glosario ubicuo

Terminos del dominio con definicion precisa. Usar estos terminos exactos en codigo, tests y conversaciones del equipo.

| Termino | Definicion |
|---------|-----------|
| **Usuario** | Persona registrada en el sistema. Tiene rol `CLIENTE` o `EMPLEADO`. Puede tener ambos perfiles simultaneamente. |
| **Cliente** | Perfil 1:1 de un Usuario con rol CLIENTE. Contiene datos personales de contacto y documento. Quien realiza compras. |
| **Empleado** | Perfil 1:1 de un Usuario con rol EMPLEADO. Quien administra catalogo, inventario y pedidos. |
| **Producto** | Articulo disponible para venta. Tiene nombre, descripcion, precio, categoria y estado. Es el aggregate root del contexto Catalogo. |
| **Categoria** | Clasificacion de productos. Un producto pertenece a una categoria. |
| **StockLevel** | Nivel de inventario actual de un producto en un momento dado. Tiene `cantidad` (total fisico) y `disponible` (total - reservas activas). |
| **StockEntry** | Movimiento individual de inventario: entrada de mercancia, ajuste o salida. Traza el historial de stock. |
| **Carrito** | Contenedor temporal de items que un Cliente desea comprar. Tiene estado (`ACTIVO`, `PROCESANDO`, `COMPLETADO`, `EXPIRADO`). |
| **ItemCarrito** | Una linea dentro del Carrito: referencia a Producto + cantidad deseada. |
| **Checkout** | Proceso de convertir un Carrito activo en una Factura pagada. Incluye reserva de stock, creacion de IntentoPago y confirmacion. |
| **Factura** | Documento comercial que registra una venta completada. Aggregate root del contexto Pedidos. Inmutable una vez creada. |
| **DetalleFactura** | Linea de una Factura: snapshot de producto (nombre, precio unitario al momento de compra) + cantidad + subtotal. |
| **IntentoPago** | Registro de un intento de pago contra Wompi. Tiene estado (`PENDIENTE`, `APROBADO`, `RECHAZADO`, `CANCELADO`). |
| **Reserva de stock** | Bloqueo temporal de unidades de un producto durante el checkout. Expira en 10 minutos si no se completa el pago. |
| **IVA** | Impuesto al Valor Agregado. En Colombia: 19% sobre el subtotal. Calculado con `BigDecimal.HALF_UP`. |
| **Total** | `subtotal + IVA`. Expresado en COP con 2 decimales. |
| **Webhook** | Notificacion HTTP enviada por Wompi al backend cuando cambia el estado de una transaccion. Debe ser idempotente. |
| **Soft-delete** | Marcar un registro como eliminado con `deleted_at = NOW()` sin removerlo fisicamente de la base de datos. |
| **Perfil** | Relacion 1:1 entre Usuario y Cliente o Empleado. Un Usuario puede tener cero, uno o ambos perfiles. |

---

## Bounded Contexts

El sistema esta dividido en 7 bounded contexts. Cada uno tiene su propio modelo, lenguaje y limites de responsabilidad.

### 1. Identidad

**Responsabilidad:** Registro, autenticacion y gestion de usuarios y perfiles.

**Aggregate roots:**
- `Usuario` — aggregate root principal
  - Atributos: id, nombre, apellido, email, password_hash, rol, deleted_at
  - Entidades miembro: ninguna
- `Cliente` — perfil 1:1 de Usuario
- `Empleado` — perfil 1:1 de Usuario

**Servicios de dominio:**
- `AutenticacionService`: valida credenciales, emite tokens JWT
- `RegistroService`: crea Usuario + perfil en transaccion atomica

**Puertos de salida:**
- `TokenPort`: emitir y verificar JWT (adaptador: `JwtTokenAdapter`)
- `NotificacionPort`: enviar email de bienvenida (adaptador: `GmailSmtpAdapter`)

---

### 2. Catalogo

**Responsabilidad:** Gestion del catalogo de productos y categorias.

**Aggregate roots:**
- `Producto` — aggregate root principal
  - Atributos: id, nombre, descripcion, precio (BigDecimal), categoria, imagen_url, activo, version, deleted_at
- `Categoria`
  - Atributos: id, nombre, descripcion

**Reglas de dominio:**
- `precio` siempre positivo.
- Producto soft-deleted no aparece en catalogo publico.
- Precio se almacena en COP con 2 decimales.

**Puertos de salida:**
- `ProductoRepositoryPort`
- `CategoriaRepositoryPort`

---

### 3. Inventario

**Responsabilidad:** Control de stock disponible y movimientos de mercancia.

**Aggregate roots:**
- `StockLevel` — nivel actual por producto
  - Atributos: id, producto_id, cantidad, version
- `StockEntry` — movimiento de inventario
  - Atributos: id, producto_id, tipo (ENTRADA/SALIDA/AJUSTE), cantidad, motivo, fecha
- `StockReservation` — reserva temporal durante checkout
  - Atributos: id, producto_id, carrito_id, cantidad, expires_at

**Reglas de dominio:**
- `disponible = cantidad - SUM(reservas_activas)`. Nunca negativo.
- Reserva expira en 10 minutos.
- Una venta completada elimina la reserva y decrementa `StockEntry`.

**Servicios de dominio:**
- `StockReservationService`: crea, libera y expira reservas

---

### 4. Carrito

**Responsabilidad:** Gestion del carrito de compras de un cliente.

**Aggregate roots:**
- `Carrito` — aggregate root
  - Atributos: id, cliente_id, estado (ACTIVO/PROCESANDO/COMPLETADO/EXPIRADO), version, updated_at
  - Entidades miembro: `ItemCarrito`
- `ItemCarrito`
  - Atributos: carrito_id, producto_id, cantidad, precio_unitario_snapshot

**Reglas de dominio:**
- Un Cliente tiene maximo un Carrito `ACTIVO` a la vez.
- Carrito pasa a `PROCESANDO` al iniciar checkout. No se puede modificar en este estado.
- Carrito `COMPLETADO` o `EXPIRADO` es inmutable.
- Items sin stock disponible no pueden agregarse.

---

### 5. Pedidos

**Responsabilidad:** Creacion y consulta de facturas. Orquesta el flujo de checkout.

**Aggregate roots:**
- `Factura` — aggregate root
  - Atributos: id, cliente_id, fecha, subtotal, iva, total, currency, estado (CREADA/PAGADA/CANCELADA)
  - Entidades miembro: `DetalleFactura`
- `DetalleFactura`
  - Atributos: id, factura_id, producto_id, nombre_producto (snapshot), precio_unitario (snapshot), cantidad, subtotal

**Reglas de dominio:**
- Factura es inmutable una vez en estado `PAGADA`.
- `total = subtotal * 1.19` (IVA 19% COP), redondeado `HALF_UP`.
- `nombre_producto` y `precio_unitario` son snapshots: no cambian aunque el producto cambie.
- Solo se puede cancelar una Factura en estado `CREADA` (pre-pago).

**Servicios de dominio:**
- `CheckoutService`: orquesta reserva stock → crear IntentoPago → crear Factura

---

### 6. Pagos

**Responsabilidad:** Integracion con Wompi, manejo de intentos de pago y webhooks.

**Aggregate roots:**
- `IntentoPago`
  - Atributos: id, factura_id, wompi_transaction_id, monto, currency, estado (PENDIENTE/APROBADO/RECHAZADO/CANCELADO), created_at

**Servicios de dominio:**
- `PagoService`: crea transaccion en Wompi, procesa webhook idempotente

**Puertos de salida:**
- `PagoPort`: iniciar transaccion de pago (adaptador: `WompiPagoAdapter`)
- `WebhookVerificacionPort`: verificar firma HMAC de eventos Wompi

**Tabla de soporte:**
- `processed_webhook(transaction_id)`: garantia de idempotencia

---

### 7. Reportes

**Responsabilidad:** Consultas de lectura agregadas para el panel de empleados. Sin aggregate propio.

**Read models:**
- `ResumenVentasDia`: total ventas, cantidad de facturas, producto mas vendido por dia.
- `StockBajoReport`: productos con `disponible < umbral`.

**Caracteristicas:**
- Solo lectura. Sin escrituras de dominio.
- Puede usar queries SQL nativas o vistas de PostgreSQL.
- No tiene repositorio de escritura; consume tablas de otros contextos directamente.

---

## Eventos de Dominio

| Evento | Publicado por | Consumido por | Descripcion |
|--------|--------------|--------------|-------------|
| `UsuarioRegistrado` | Identidad | Identidad | Dispara envio de email de bienvenida |
| `ProductoCreado` | Catalogo | Inventario | Crea `StockLevel` inicial con cantidad 0 |
| `CheckoutIniciado` | Pedidos | Inventario | Crea `StockReservation` |
| `PagoAprobado` | Pagos | Pedidos, Inventario | Confirma Factura, consume reserva, decrementa stock real |
| `PagoRechazado` | Pagos | Inventario, Pedidos | Libera reserva, cancela Factura |
| `ReservaExpirada` | Inventario (job) | Inventario | Restaura `disponible` del producto |

> Nota: En el MVP, los eventos son llamadas directas entre use cases dentro de la misma JVM, no mensajeria asincrona. La abstraccion existe para facilitar migracion futura a eventos reales.

---

## Context Map

```
┌─────────────────────────────────────────────────────────────────┐
│                        TIENDAQ SYSTEM                           │
│                                                                 │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────────┐  │
│  │  IDENTIDAD   │    │   CATALOGO   │    │    INVENTARIO    │  │
│  │              │    │              │    │                  │  │
│  │ Usuario      │    │ Producto     │    │ StockLevel       │  │
│  │ Cliente      │    │ Categoria    │    │ StockEntry       │  │
│  │ Empleado     │    │              │    │ StockReservation │  │
│  └──────┬───────┘    └──────┬───────┘    └────────┬─────────┘  │
│         │                  │                      │            │
│         │ [U] autenticacion│ [U] datos producto   │ [U] stock  │
│         └──────────────────┴──────────────────────┘            │
│                                   │                            │
│                    ┌──────────────▼──────────────┐             │
│                    │           CARRITO           │             │
│                    │                             │             │
│                    │ Carrito + ItemCarrito       │             │
│                    └──────────────┬──────────────┘             │
│                                   │ [U] checkout               │
│                    ┌──────────────▼──────────────┐             │
│                    │           PEDIDOS           │             │
│                    │                             │             │
│                    │ Factura + DetalleFactura    │             │
│                    └──────────────┬──────────────┘             │
│                                   │ [ACL] → Wompi              │
│                    ┌──────────────▼──────────────┐             │
│                    │            PAGOS            │             │
│                    │                             │             │
│                    │ IntentoPago                 │             │
│                    │ WompiPagoAdapter            │             │
│                    └──────────────┬──────────────┘             │
│                                   │                            │
│                    ┌──────────────▼──────────────┐             │
│                    │          REPORTES           │             │
│                    │   (read-model, sin writes)  │             │
│                    └─────────────────────────────┘             │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘

Leyenda:
  [U]   = Upstream/Downstream: el upstream provee, el downstream consume
  [ACL] = Anti-Corruption Layer: adaptador que aisla el dominio de la API externa
```

**Wompi** es un sistema externo. El contexto Pagos lo aisla via `PagoPort` (puerto de dominio) + `WompiPagoAdapter` (adaptador de infraestructura). El dominio nunca depende directamente de la API de Wompi.
