# TiendaQ · Agent Context

> Operational context and rules for AI agents working in this repository.

---

## K-Forge Ecosystem

K-Forge is a software development club at Fundación Universitaria Konrad Lorenz (FUKL), Bogotá, founded by Brian Vargas (@13rianVargas). The club builds real-world software products for the university and community.

| Project | Repo | Description |
|---------|------|-------------|
| K-Forge Website | `K-Forge/` | Public landing page (Angular, Vercel) |
| KApp | `KApp/` | University management platform (Spring Boot microservices) |
| **TiendaQ** | `TiendaQ/` | University e-commerce system — you are here |
| Roastory | `Roastory/` | Library-cafe management system (Node.js + MongoDB) |

---

## Project Overview

**TiendaQ** is an e-commerce system for Fundación Universitaria Konrad Lorenz, developed by the K-Forge club. It handles product catalog, orders, and sales for the university store.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 25, Spring Boot 4.0.0 |
| Backend modules | Spring Web MVC, Spring Data JPA, Spring Security, Spring Validation, Actuator |
| Build | Maven Wrapper (`./mvnw`) |
| Frontend | Angular 21.2.0, TypeScript ~5.9.2, SCSS |
| UI library | PrimeNG (tema K-Forge) |
| Scripting | Bun 1.3.8 (scripts), pnpm (package install) |
| Database | PostgreSQL 15+ |
| DB connection | `jdbc:postgresql://localhost:5432/tiendaq` |
| DB credentials | Env vars: `DB_USER` (default: `postgres`), `DB_PASSWORD` (default: empty) |
| Schema management | Flyway (`src/main/resources/db/migration/`) — `ddl-auto=validate` |
| Payments | Wompi sandbox (`sandbox.wompi.co`) |
| Auth | JWT propio: access 15min + refresh 7d con rotacion |
| Money | `BigDecimal` + `NUMERIC(15,2)` + `currency CHAR(3) DEFAULT 'COP'` |

---

## Project Structure

```text
TiendaQ/
├── app/
│   ├── backend/
│   │   ├── tiendaq/                 # Spring Boot API
│   │   │   └── src/main/java/com/tiendaq/
│   │   │       ├── <contexto>/      # 7 bounded contexts (identidad, catalogo, inventario, carrito, pedidos, pagos, reportes)
│   │   │       │   ├── domain/      # Entidades, VOs, puertos (interfaces)
│   │   │       │   ├── application/ # Use cases
│   │   │       │   └── infrastructure/ # Controllers, JPA repos, adapters externos
│   │   │       └── shared/          # Utilidades transversales (auditoria, excepciones)
│   │   └── postman/                 # API collections (placeholder)
│   ├── frontend/                    # Angular 21 app
│   │   └── src/app/
│   │       ├── core/                # Guards, interceptors, servicios singleton
│   │       ├── shared/              # Atoms + Molecules (Atomic Design)
│   │       └── features/            # Catalogo, carrito, checkout, pedidos, auth, admin
│   └── database/
│       ├── SCRIPTS_POSTGRES.sql     # Esquema historico de referencia
│       ├── INSERTS.sql              # Seed data
│       └── SCRIPTS_MYSQL_LEGACY.sql # Historico — no usar
├── docs/
│   ├── adr/                         # 11 ADRs (decisiones arquitectonicas)
│   ├── api/openapi.yaml             # Contrato REST (source of truth)
│   ├── DOMAIN.md                    # Glosario, bounded contexts, context map
│   ├── REQUIREMENTS.md              # SRS v3.0
│   ├── DESIGN.md                    # SDD con C4, diagramas de filtros, state machines
│   ├── DATABASE.md                  # Esquema, Flyway, tipos, indices
│   ├── FRONTEND.md                  # Angular, Signals, PrimeNG, Atomic Design
│   ├── OPERATIONS.md                # CI/CD, Docker, runbook
│   └── TESTING.md                   # Piramide, Testcontainers, Playwright
└── scripts/
    ├── start-back.sh                # Run from repo root
    └── start-front.sh              # Run from repo root
```

---

## Dev Commands

```bash
# One-time tooling setup
corepack enable && corepack prepare pnpm@latest --activate
curl -fsSL https://bun.sh/install | bash

# Start backend (from repo root)
./scripts/start-back.sh
# or manually:
cd app/backend/tiendaq && ./mvnw spring-boot:run   # → port 8080

# Start frontend (from repo root)
./scripts/start-front.sh
# or manually:
cd app/frontend && pnpm install && bun start        # → port 4200

# Database initialization (first time)
psql -U postgres -c 'CREATE DATABASE "tiendaq";'
psql -U postgres -d "tiendaq" -f app/database/SCRIPTS_POSTGRES.sql
psql -U postgres -d "tiendaq" -f app/database/INSERTS.sql

# Tests
cd app/backend/tiendaq && ./mvnw test
```

---

## Conventions

### Java / Backend

- Hexagonal architecture + DDD per bounded context: `domain/` → `application/` → `infrastructure/`. Domain has no Spring/JPA dependencies.
- All API routes under `/api/`.
- Classes: PascalCase. Methods/variables: camelCase. Packages: lowercase.
- Use env vars for DB credentials. Never hardcode.
- Money: `BigDecimal` + `NUMERIC(15,2)`. Never `double` for prices.
- Enums: `@JdbcTypeCode(SqlTypes.NAMED_ENUM)` for native PostgreSQL enum types.
- Schema: managed by Flyway. Never modify applied migrations.
- Errors: RFC 7807 `ProblemDetail` via `@ControllerAdvice`.
- Optimistic locking: `@Version` on `Producto`, `Carrito`, `StockLevel`.

### Angular / Frontend

- Standalone components. SCSS for styles.
- Prettier config: `printWidth=100`, single quotes.
- `.editorconfig`: 2-space indent.
- Test generation disabled in schematics by default.

### Git

- **Commits:** Conventional Commits, English, lowercase, no scope, no final period.
  ```
  feat: add product catalog endpoint
  fix: resolve order total calculation
  chore: update angular to 21.3
  ```
- **Branches:** Git Flow — `main`, `develop`, `feature/*`, `bugfix/*`, `test/*`, `hotfix/*`, `release/*`.

### Versioning

SemVer `MAJOR.MINOR.PATCH`. Release cycle: alpha → beta → stable.

---

## Current State

- **Backend:** CRUD base in layered architecture (8 controllers). No DTOs, no validation, no error handling, no security active. US-068 (refactor to hexagonal) planned Sprint 1.
- **Frontend:** Angular 21 scaffold only. `app.routes.ts` is empty — no features implemented in UI.
- **Security:** Spring Security dependency present but not configured. No auth/authorization active.
- **Testing:** Backend has `contextLoads` only. No functional test suite.
- **Schema:** `SCRIPTS_POSTGRES.sql` is historical reference. Flyway migration `V1__initial_schema.sql` pending (US-055).

---

## Documentation

All technical documentation is in `docs/`. See `docs/README.md` for navigation index.

- Architecture decisions: `docs/adr/` (11 ADRs)
- API contract: `docs/api/openapi.yaml`
- Domain model: `docs/DOMAIN.md`
- Requirements: `docs/REQUIREMENTS.md`
- Design: `docs/DESIGN.md`
- Database: `docs/DATABASE.md`
- Frontend: `docs/FRONTEND.md`
- Operations: `docs/OPERATIONS.md`
- Testing: `docs/TESTING.md`

---

## Priority Roadmap (Sprint 1)

1. US-068: Refactor to Hexagonal + DDD package structure.
2. US-005: Fix data model (BigDecimal, @JdbcTypeCode, @Version, indexes, perfiles 1:1).
3. US-055: Flyway migration `V1__initial_schema.sql`, eliminate `ddl-auto=update`.
4. US-009/010/011: JWT auth (access 15min + refresh 7d + rotation + logout).
5. US-008: DTOs, @Valid, @ControllerAdvice with RFC 7807 ProblemDetail.

---

## Gotchas

- `scripts/start-back.sh` and `scripts/start-front.sh` use relative `cd`. Run them from the repo root.
- `SCRIPTS_POSTGRES.sql` is historical reference only. Flyway manages the schema going forward.
- `SCRIPTS_MYSQL_LEGACY.sql` is historical — ignore it.
- Java enums and PostgreSQL enums must match exactly in text value. Use `@JdbcTypeCode(SqlTypes.NAMED_ENUM)`.
- `ddl-auto=validate` once Flyway is active. Until then, `ddl-auto=update` is still active — update SQL explicitly.
- Frontend starts without errors but has no functional routes. Do not assume UI features exist.
- Wompi sandbox keys are in env vars `WOMPI_PUBLIC_KEY`, `WOMPI_PRIVATE_KEY`, `WOMPI_EVENTS_SECRET`. Never hardcode.
- `revoked_token` table requires daily `@Scheduled` cleanup job to prevent unbounded growth.
- All monetary values use `BigDecimal`. Never cast to `double` for calculations.

---

## AI Agent Instructions

- **Never hardcode** credentials or secrets. Always use `DB_USER` / `DB_PASSWORD` env vars.
- **Never modify** `docs/` unless explicitly requested.
- **Scope discipline:** Limit changes to the requested scope. Do not refactor unrelated code.
- **Schema changes:** Update `SCRIPTS_POSTGRES.sql` to match any model changes.
- **No emojis** in technical markdown documents.
- **No automatic commits.** Present changes for review first.
- **Documentation language:** Spanish for technical docs.


---

## Temporary Files

- `tmp/` is gitignored. Store one-off scripts and throwaway files there.
- Delete after use. Never commit anything from `tmp/`.