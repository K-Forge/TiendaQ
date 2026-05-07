# Especificacion de Requisitos de Software (SRS) — TiendaQ

**Proyecto:** TiendaQ — Sistema de Comercio Electronico Universitario
**Organizacion:** Fundacion Universitaria Konrad Lorenz (FUKL) — Club K-Forge
**Version:** 3.0
**Fecha:** Mayo 2026
**Estado:** Aprobado

---

## 1. Introduccion

### 1.1 Proposito

Este documento define los requisitos funcionales y no funcionales de **TiendaQ**, sistema de comercio electronico para la tienda universitaria de la FUKL, desarrollado por el club K-Forge. Sirve como contrato entre el equipo de desarrollo, docentes evaluadores y stakeholders del proyecto.

### 1.2 Alcance del MVP

TiendaQ es un sistema de e-commerce academico con las siguientes capacidades:

- **Clientes**: registro, autenticacion, navegacion de catalogo, carrito, checkout con pago electronico via Wompi sandbox, consulta de historial de facturas.
- **Empleados**: gestion de catalogo (productos/categorias), control de inventario, consulta de pedidos y reportes de ventas.
- **Sistema**: reservas de stock con timeout, procesamiento asincrono de pagos via webhook, notificaciones por email, auditoria de acciones.

**Restricciones del MVP academico:**
- Solo recogida en campus. Sin logistica de envios.
- Solo pesos colombianos (COP). Sin soporte multimoneda.
- Wompi sandbox (no produccion). Sin dinero real.
- Zona horaria: America/Bogota.
- Idioma: es-CO. Sin internacionalizacion.
- Deploy: Render/Railway free tier. Sin SLA garantizado.

**Fuera del alcance:**
- Refund post-pago (Fase 2).
- Envios a domicilio.
- App movil nativa.
- Soporte multiidioma.
- Pasarela de pago diferente a Wompi.

### 1.3 Acronimos y terminos

| Termino | Definicion |
|---------|-----------|
| SRS | Especificacion de Requisitos de Software |
| FR | Requisito Funcional (Functional Requirement) |
| NFR | Requisito No Funcional |
| JWT | JSON Web Token |
| IVA | Impuesto al Valor Agregado (19% en Colombia) |
| COP | Peso Colombiano |
| DDD | Domain-Driven Design |
| BC | Bounded Context |
| ADR | Architecture Decision Record |

---

## 2. Actores del sistema

| Actor | Descripcion | Autenticado |
|-------|-------------|-------------|
| **Cliente** | Estudiante o comunidad universitaria que compra en la tienda | Si |
| **Empleado** | Personal de la tienda universitaria; gestiona catalogo e inventario | Si |
| **Sistema externo: Wompi** | Pasarela de pago que notifica el resultado de transacciones via webhook | No (verifica HMAC) |
| **Sistema externo: Gmail SMTP** | Servicio de envio de emails transaccionales | No |
| **Visitante** | Usuario no autenticado. Solo puede ver catalogo publico | No |

---

## 3. Requisitos Funcionales

Los FR estan agrupados por bounded context (ver [DOMAIN.md](DOMAIN.md)).

### BC-1: Identidad

| ID | Requisito | Prioridad |
|----|-----------|-----------|
| FR-101 | El sistema permite registrar un nuevo Usuario con email, password, nombre, apellido y tipo de documento | Must |
| FR-102 | Al registrar un Cliente, el sistema crea el perfil `cliente` en la misma transaccion | Must |
| FR-103 | El sistema autentica usuarios con email y password. Emite access token (15min) y refresh token (7d) | Must |
| FR-104 | El sistema rota el refresh token en cada uso. El token anterior queda invalido | Must |
| FR-105 | El sistema permite logout: invalida el refresh token via tabla `revoked_token` | Must |
| FR-106 | El sistema permite al Cliente actualizar sus datos de perfil (telefono, direccion) | Should |
| FR-107 | El sistema envia email de bienvenida al completar el registro | Should |

### BC-2: Catalogo

| ID | Requisito | Prioridad |
|----|-----------|-----------|
| FR-201 | El Empleado puede crear un Producto con nombre, descripcion, precio (COP), categoria e imagen opcional | Must |
| FR-202 | El Empleado puede actualizar cualquier campo de un Producto | Must |
| FR-203 | El Empleado puede desactivar un Producto (soft-delete). El producto no aparece en catalogo pero se mantiene en facturas historicas | Must |
| FR-204 | El Visitante y Cliente pueden listar productos del catalogo (solo activos, paginado) | Must |
| FR-205 | El Visitante y Cliente pueden filtrar productos por categoria y ordenar por precio | Must |
| FR-206 | El Visitante y Cliente pueden ver el detalle de un producto (nombre, descripcion, precio, stock disponible) | Must |
| FR-207 | El Empleado puede crear y gestionar categorias de productos | Must |

### BC-3: Inventario

| ID | Requisito | Prioridad |
|----|-----------|-----------|
| FR-301 | El Empleado puede registrar entradas de mercancia a un producto (StockEntry tipo ENTRADA) | Must |
| FR-302 | El Empleado puede registrar ajustes de inventario con motivo (StockEntry tipo AJUSTE) | Must |
| FR-303 | El sistema muestra el stock disponible por producto (cantidad - reservas activas) | Must |
| FR-304 | El sistema reserva stock al iniciar checkout. La reserva expira en 10 minutos si no se completa el pago | Must |
| FR-305 | Un job programado libera reservas expiradas cada 60 segundos | Must |
| FR-306 | El Empleado puede ver el historial de movimientos de stock por producto | Should |
| FR-307 | El sistema alerta al Empleado cuando el stock disponible cae por debajo de un umbral configurable | Could |

### BC-4: Carrito

| ID | Requisito | Prioridad |
|----|-----------|-----------|
| FR-401 | El Cliente puede agregar productos a su carrito (un cliente tiene maximo un carrito activo) | Must |
| FR-402 | El Cliente puede actualizar la cantidad de un item en el carrito | Must |
| FR-403 | El Cliente puede eliminar un item del carrito | Must |
| FR-404 | El Cliente puede ver su carrito con subtotal, IVA y total calculados | Must |
| FR-405 | El sistema impide agregar al carrito un producto sin stock disponible | Must |
| FR-406 | Carrito inactivo por mas de 30 dias se elimina automaticamente (hard-delete) | Should |

### BC-5: Pedidos

| ID | Requisito | Prioridad |
|----|-----------|-----------|
| FR-501 | El Cliente puede iniciar checkout desde su carrito activo | Must |
| FR-502 | El sistema crea una Factura con snapshot de precios al momento de la compra | Must |
| FR-503 | El total de la Factura incluye IVA 19% calculado con BigDecimal.HALF_UP | Must |
| FR-504 | El Cliente puede ver su historial de facturas | Must |
| FR-505 | El Cliente puede ver el detalle de una factura (DetalleFactura con productos) | Must |
| FR-506 | El Cliente puede cancelar una Factura en estado CREADA (pre-pago) | Should |
| FR-507 | El Empleado puede ver todas las facturas del sistema con filtros por fecha y estado | Should |

### BC-6: Pagos

| ID | Requisito | Prioridad |
|----|-----------|-----------|
| FR-601 | El sistema crea un IntentoPago via Wompi sandbox al iniciar checkout | Must |
| FR-602 | El sistema recibe y procesa el webhook de Wompi con verificacion de firma HMAC | Must |
| FR-603 | El procesamiento de webhook es idempotente: si el mismo `transaction_id` llega dos veces, solo se procesa una vez | Must |
| FR-604 | Pago APROBADO: confirma Factura, consume reserva de stock, decrementa stock real | Must |
| FR-605 | Pago RECHAZADO: libera reserva de stock, cancela Factura, notifica al Cliente | Must |
| FR-606 | El Cliente ve el estado de su pago en la pantalla de confirmacion | Must |

### BC-7: Reportes

| ID | Requisito | Prioridad |
|----|-----------|-----------|
| FR-701 | El Empleado puede ver un resumen de ventas del dia (total, cantidad de facturas) | Should |
| FR-702 | El Empleado puede ver un reporte de stock bajo (productos con disponible < umbral) | Should |
| FR-703 | El Empleado puede ver los productos mas vendidos por periodo | Could |

---

## 4. Requisitos No Funcionales

### NFR-1: Seguridad

| ID | Requisito |
|----|-----------|
| NFR-101 | Passwords almacenados con BCrypt (strength >= 12) |
| NFR-102 | Endpoints de administracion requieren rol EMPLEADO. Operaciones de cliente requieren rol CLIENTE |
| NFR-103 | Rate limiting: 5 intentos de login por 15min por IP (Bucket4j). 3 registros por hora por IP. 10 checkouts por hora por usuario |
| NFR-104 | CORS configurado para aceptar solo `http://localhost:4200` (dev) y el dominio de produccion |
| NFR-105 | Credenciales y secretos unicamente via variables de entorno. Nunca en codigo |
| NFR-106 | JWT firmado con HS256 y secreto de minimo 256 bits |

### NFR-2: Confiabilidad

| ID | Requisito |
|----|-----------|
| NFR-201 | Transacciones de checkout son ACID: reserva + IntentoPago son atomicos |
| NFR-202 | Webhook de Wompi tiene garantia de idempotencia via `processed_webhook` |
| NFR-203 | Optimistic locking en Producto, Carrito y StockLevel previene lost updates concurrentes |

### NFR-3: Rendimiento

| ID | Requisito |
|----|-----------|
| NFR-301 | Respuesta de endpoints de catalogo (listado) < 500ms bajo carga normal |
| NFR-302 | Indices en todas las FKs y en columnas de filtrado frecuente (`deleted_at`, `estado`) |
| NFR-303 | Paginacion obligatoria en endpoints de listado (default 20, max 100) |

### NFR-4: Mantenibilidad

| ID | Requisito |
|----|-----------|
| NFR-401 | Arquitectura Hexagonal + DDD por bounded context (ver [ADR-0001](adr/0001-arquitectura-hexagonal-ddd.md)) |
| NFR-402 | Migraciones de schema gestionadas por Flyway (ver [ADR-0005](adr/0005-flyway-vs-ddl-auto.md)) |
| NFR-403 | Errores de API en formato RFC 7807 Problem Details (ver [ADR-0008](adr/0008-error-envelope-rfc-7807-problem-details.md)) |
| NFR-404 | Cobertura de tests: >= 80% en capa domain, >= 60% en capa application (Jacoco) |

### NFR-5: Observabilidad

| ID | Requisito |
|----|-----------|
| NFR-501 | Spring Actuator expone `/actuator/health` y `/actuator/metrics` |
| NFR-502 | Logs en formato JSON (Logback) con campos: timestamp, level, service, trace_id, message |
| NFR-503 | Tabla `audit_log` registra todas las acciones criticas (creacion/modificacion de facturas, cambios de stock) |

---

## 5. Casos de uso principales

### CU-01: Checkout completo

**Actor:** Cliente
**Precondicion:** Cliente autenticado con carrito activo con al menos 1 item con stock disponible.

1. Cliente confirma carrito y selecciona metodo de pago.
2. Sistema verifica stock disponible para cada item.
3. Sistema crea reservas de stock (10min timeout).
4. Sistema crea Factura con snapshot de precios e IVA 19%.
5. Sistema crea IntentoPago y llama Wompi API.
6. Sistema retorna URL de pago o token de pago al frontend.
7. Cliente completa el pago en la interfaz de Wompi.
8. Wompi envia webhook `APPROVED` al backend.
9. Sistema verifica firma HMAC del webhook.
10. Sistema verifica idempotencia (`processed_webhook`).
11. Sistema confirma Factura (estado PAGADA), consume reservas, decrementa stock real.
12. Sistema envia email de confirmacion al Cliente.
13. Frontend muestra pantalla de confirmacion con numero de factura.

**Flujo alternativo — pago rechazado (paso 8):**
- Wompi envia webhook `DECLINED`.
- Sistema libera reservas de stock.
- Sistema cancela Factura (estado CANCELADA).
- Frontend muestra mensaje de pago rechazado.

**Flujo alternativo — timeout de reserva:**
- Si el Cliente no completa el pago en 10min, el job de expiracion libera las reservas.
- La Factura queda en estado CREADA. El siguiente intento de pago falla por Factura expirada.

### CU-02: Autenticacion con refresh rotation

**Actor:** Cliente o Empleado

1. Usuario envia `POST /api/auth/login` con email y password.
2. Sistema valida credenciales con BCrypt.
3. Sistema emite access token (15min) y refresh token (7d).
4. Cliente almacena tokens (access en memoria, refresh en httpOnly cookie).
5. Al expirar el access token, cliente envia `POST /api/auth/refresh` con refresh token.
6. Sistema verifica que el `jti` no este en `revoked_token`.
7. Sistema inserta `jti` anterior en `revoked_token`.
8. Sistema emite nuevo par de tokens.

### CU-03: Gestion de catalogo por Empleado

**Actor:** Empleado
**Precondicion:** Autenticado con rol EMPLEADO.

1. Empleado crea Producto con nombre, descripcion, precio, categoria.
2. Sistema persiste Producto y crea `StockLevel` con cantidad 0.
3. Empleado registra entrada de mercancia (StockEntry tipo ENTRADA).
4. Sistema incrementa `StockLevel.cantidad`.
5. Producto aparece en catalogo con stock disponible.

---

## 6. Matriz de trazabilidad

| FR | US (BACKLOG) | ADR relacionado |
|----|-------------|----------------|
| FR-101..107 | US-001, US-009, US-010, US-011 | ADR-0002 (JWT), ADR-0006 (perfiles) |
| FR-201..207 | US-002, US-003, US-044 | ADR-0007 (soft-delete) |
| FR-301..307 | US-004, US-067, US-068 | ADR-0010 (reservas) |
| FR-401..406 | US-005, US-006 | ADR-0009 (optimistic locking) |
| FR-501..507 | US-007, US-008, US-011 | ADR-0011 (DetalleFactura) |
| FR-601..606 | US-046..050, US-061 | ADR-0003 (Wompi), ADR-0008 (errors) |
| FR-701..703 | US-012, US-013 | — |
| NFR-101..106 | US-009, US-010, US-011, US-062 | ADR-0002 (JWT) |
| NFR-401..404 | US-068 | ADR-0001 (hexagonal) |
| NFR-401 | US-044 | ADR-0004 (BigDecimal), ADR-0005 (Flyway) |
