# Estado de Implementacion — TiendaQ

**Ultima actualizacion:** Mayo 2026

---

## Estado general

| Area | Estado actual | Notas |
|------|--------------|-------|
| Documentacion | Completa | 8 docs + 11 ADRs + openapi stub |
| Backlog | Actualizado | 65 items (HU/TT/EN/MX) en épicas 1-14 |
| Backend — CRUD base | Implementado | 8 controllers, arquitectura en capas clasica |
| Backend — Hexagonal/DDD | Pendiente | TT-068 Sprint 1 |
| Backend — Seguridad (JWT) | Pendiente | TT-009, HU-010, HU-011 Sprint 1 |
| Backend — Validacion + errores | Pendiente | TT-008 Sprint 1 |
| Backend — Flyway | Pendiente | EN-058 Sprint 1 |
| Backend — Wompi | Pendiente | HU-046..050 Sprint 3 |
| Frontend | Scaffold vacio | No hay rutas ni componentes |
| Base de datos | Esquema base OK | Sin nuevas tablas (stock_reservation, revoked_token, etc.) |
| CI/CD | Pendiente | EN-059, EN-060, EN-067 Sprint 4 |
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

**Problemas conocidos del codigo actual** (a resolver en Fase 0 / TT-068):
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
SPRINT 1 (En progreso) — 4 bloqueantes absolutos
──────────────────────────────────────────────────
[ ] EN-001 Estructura de carpetas y convenciones
[ ] EN-058 Flyway + perfiles de ambiente
[ ] TT-005 Modelo de datos (BigDecimal, @JdbcTypeCode, @Version, indices)
[ ] TT-068 Refactor hexagonal/DDD por bounded context

SPRINT 2 (Pendiente)
────────────────────
[ ] TT-009 JWT access + refresh
[ ] HU-010 Login con BCrypt
[ ] HU-011 Logout con revoked_token
[ ] TT-008 DTOs + @Valid + @ControllerAdvice RFC 7807
[ ] HU-007 Registro de cliente
[ ] HU-013 Catalogo de productos
[ ] HU-014 Detalle de producto

SPRINT 3 (Pendiente)
────────────────────
[ ] HU-031 Gestion de inventario
[ ] TT-063 Rate limit Bucket4j
[ ] HU-018 Carrito de compras
[ ] HU-024 Checkout

SPRINT 4+ (Pendiente)
──────────────────────
[ ] HU-046..050 Integracion Wompi (épica 12)
Ver docs/BACKLOG.md para detalle completo (épicas 1-14)
```

---

## Deuda tecnica conocida

| Item | Severidad | Item BACKLOG que lo resuelve |
|------|-----------|------------------------------|
| `double` para precios | Alta | TT-005 |
| Sin DTOs en API | Alta | TT-068 |
| `ddl-auto=update` | Alta | EN-058 |
| Sin autenticacion | Alta | TT-009, HU-010, HU-011 |
| `@Inheritance(JOINED)` en Cliente/Empleado | Media | TT-068 |
| Sin `DetalleFactura` JPA | Media | HU-024 |
| Sin indices en FKs | Media | TT-005 |
| Sin tests funcionales | Media | Sprint 2+ |
| Frontend vacio | Baja (planificado) | HU-018+ |
