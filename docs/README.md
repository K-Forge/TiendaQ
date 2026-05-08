# Documentacion de TiendaQ

Indice de navegacion para todos los documentos tecnicos del proyecto.

---

## Documentos principales

| Documento | Descripcion |
|-----------|-------------|
| [REQUIREMENTS.md](REQUIREMENTS.md) | Especificacion de Requisitos de Software (SRS) — actores, FRs, RNFs, casos de uso, trazabilidad |
| [DOMAIN.md](DOMAIN.md) | Modelo de dominio — glosario ubicuo, bounded contexts, agregados, eventos, context map |
| [DESIGN.md](DESIGN.md) | Documento de Diseno de Software (SDD) — arquitectura Hexagonal, C4 L1-L3, secuencias, state machines |
| [DATABASE.md](DATABASE.md) | Esquema de base de datos — ER, Flyway, tipos, indices, soft-delete, auditoria |
| [FRONTEND.md](FRONTEND.md) | Arquitectura frontend — Angular 21, Atomic Design, Signals, PrimeNG, rutas, guards |
| [OPERATIONS.md](OPERATIONS.md) | Operaciones — CI/CD, ambientes, Docker, observabilidad, runbook, troubleshooting |
| [TESTING.md](TESTING.md) | Estrategia de testing — piramide, Testcontainers, Playwright, cobertura |
| [PROGRESS.md](PROGRESS.md) | Estado actual de implementacion |

---

## Architecture Decision Records (ADRs)

Los ADRs documentan el POR QUE de cada decision tecnica importante.

| ADR | Decision |
|-----|----------|
| [ADR-0001](adr/0001-arquitectura-hexagonal-ddd.md) | Arquitectura Hexagonal + DDD por bounded context |
| [ADR-0002](adr/0002-jwt-propio-con-refresh-rotation.md) | JWT propio con access 15min + refresh 7d + rotacion |
| [ADR-0003](adr/0003-pasarela-pago-wompi-sandbox.md) | Pasarela de pago Wompi sandbox |
| [ADR-0004](adr/0004-money-bigdecimal-currency-cop.md) | BigDecimal + NUMERIC(15,2) para dinero en COP |
| [ADR-0005](adr/0005-flyway-vs-ddl-auto.md) | Flyway para migraciones (eliminar ddl-auto=update) |
| [ADR-0006](adr/0006-cliente-empleado-perfiles-no-herencia.md) | Cliente/Empleado como perfiles 1:1 (sin herencia JPA) |
| [ADR-0007](adr/0007-soft-delete-por-entidad.md) | Soft-delete diferenciado por entidad |
| [ADR-0008](adr/0008-error-envelope-rfc-7807-problem-details.md) | RFC 7807 Problem Details para errores API |
| [ADR-0009](adr/0009-optimistic-locking-version-column.md) | Optimistic locking con columna @Version |
| [ADR-0010](adr/0010-stock-reservation-checkout-timeout.md) | Reservas de stock con timeout 10min |
| [ADR-0011](adr/0011-detallefactura-aggregate-de-factura.md) | DetalleFactura como entidad dentro del aggregate Factura |

---

## Contrato API

| Recurso | Descripcion |
|---------|-------------|
| [api/openapi.yaml](api/openapi.yaml) | Contrato REST source-of-truth (generado con springdoc-openapi) |

---

## Como navegar esta documentacion

- **Nuevo en el proyecto**: empieza por [DOMAIN.md](DOMAIN.md) para entender el vocabulario, luego [REQUIREMENTS.md](REQUIREMENTS.md) para el alcance.
- **Vas a desarrollar**: lee [DESIGN.md](DESIGN.md) para la arquitectura y [DATABASE.md](DATABASE.md) para el esquema.
- **Vas a hacer frontend**: lee [FRONTEND.md](FRONTEND.md) y el contrato [api/openapi.yaml](api/openapi.yaml).
- **Vas a desplegar**: lee [OPERATIONS.md](OPERATIONS.md).
- **Quieres saber por que se tomo una decision**: busca el ADR correspondiente en `adr/`.
