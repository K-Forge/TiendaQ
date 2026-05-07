# Estado de Implementacion — TiendaQ

**Ultima actualizacion:** Mayo 2026

---

## Estado general

| Area | Estado actual | Notas |
|------|--------------|-------|
| Documentacion | Completa | 8 docs + 11 ADRs + openapi stub |
| Backlog | Actualizado | 65 USs en 8 sprints |
| Backend — CRUD base | Implementado | 8 controllers, arquitectura en capas clasica |
| Backend — Hexagonal/DDD | Pendiente | US-068 Sprint 1 |
| Backend — Seguridad (JWT) | Pendiente | US-009, US-010, US-011 Sprint 1 |
| Backend — Validacion + errores | Pendiente | Sprint 1 |
| Backend — Flyway | Pendiente | US-055 Sprint 1 |
| Backend — Wompi | Pendiente | US-046..050 Sprint 3 |
| Frontend | Scaffold vacio | No hay rutas ni componentes |
| Base de datos | Esquema base OK | Sin nuevas tablas (stock_reservation, revoked_token, etc.) |
| CI/CD | Pendiente | US-056..060 Sprint 4 |
| Tests | Solo contextLoads | Pendiente piramide completa |

---

## Backend — implementado

Estructura actual en `app/backend/tiendaq/src/main/java/com/tiendaq/api/`:

```
controller/   8 REST controllers (CRUD basico, sin validacion, sin DTOs)
service/      8 servicios (logica trivial, sin casos de uso del dominio)
repository/   8 JPA repositories (interfaces Spring Data)
model/        8 entidades JPA + 6 enums
```

**Problemas conocidos del codigo actual** (a resolver en Fase 0 / US-068):
- Entidades JPA expuestas directamente en la API (sin DTOs).
- Sin validacion de entrada (`@Valid` no configurado).
- Sin manejo global de errores (`@ControllerAdvice` ausente).
- `double` para precios — pendiente migrar a `BigDecimal`.
- `@Inheritance(JOINED)` en Cliente/Empleado — pendiente migrar a perfiles 1:1.
- `ddl-auto=update` activo — pendiente migrar a Flyway.
- Spring Security incluido como dependencia pero sin configurar.
- Sin autenticacion ni autorizacion.
- `DetalleFactura` existe en SQL pero sin entidad JPA.
- Enums sin `@JdbcTypeCode(SqlTypes.NAMED_ENUM)`.
- Sin indices en FKs.

---

## Frontend — estado

- Angular 21 scaffold con `app.ts` y `app.routes.ts` vacios.
- PrimeNG instalado, sin configurar.
- Sin componentes, sin rutas, sin servicios.

---

## Kanban de sprints

```
SPRINT 1 (En progreso)
──────────────────────
[ ] US-068 Refactor hexagonal/DDD
[ ] US-005 Modelo de datos mejorado (BigDecimal, @JdbcTypeCode, indices)
[ ] US-009 JWT access + refresh
[ ] US-010 Login / Registro con BCrypt
[ ] US-011 Logout con revoked_token
[ ] US-055 Dockerfiles + docker-compose

SPRINT 2 (Pendiente)
────────────────────
[ ] US-001 Registro de cliente
[ ] US-002 Catalogo de productos
[ ] US-003 Detalle de producto
[ ] US-004 Gestion de inventario
[ ] US-063 Rate limit Bucket4j

SPRINT 3 (Pendiente)
────────────────────
[ ] US-006 Carrito de compras
[ ] US-007 Checkout
[ ] US-046 Integracion Wompi ACL
[ ] US-047 Webhook Wompi
[ ] US-048 Reconciliacion de pagos

SPRINT 4-8 (Pendiente)
──────────────────────
Ver docs/BACKLOG.md para detalle completo
```

---

## Deuda tecnica conocida

| Item | Severidad | US que lo resuelve |
|------|-----------|-------------------|
| `double` para precios | Alta | US-005 |
| Sin DTOs en API | Alta | US-068 |
| `ddl-auto=update` | Alta | US-055 |
| Sin autenticacion | Alta | US-009..011 |
| `@Inheritance(JOINED)` en Cliente/Empleado | Media | US-068 |
| Sin `DetalleFactura` JPA | Media | US-007 |
| Sin indices en FKs | Media | US-005 |
| Sin tests funcionales | Media | Sprint 2+ |
| Frontend vacio | Baja (planificado) | US-006+ |
