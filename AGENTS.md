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
| Scripting | Bun 1.3.8 (scripts), pnpm (package install) |
| Database | PostgreSQL 15+ |
| DB connection | `jdbc:postgresql://localhost:5432/tiendaq` |
| DB credentials | Env vars: `DB_USER` (default: `postgres`), `DB_PASSWORD` (default: empty) |
| Schema management | Hibernate `ddl-auto=update` |

---

## Project Structure

```text
TiendaQ/
├── app/
│   ├── backend/
│   │   ├── tiendaq/                 # Spring Boot API
│   │   │   └── src/main/java/com/tiendaq/api/
│   │   │       ├── controller/      # 8 REST controllers
│   │   │       ├── service/         # 8 services
│   │   │       ├── repository/      # 8 JPA repositories
│   │   │       └── model/           # 8 entities + enums
│   │   └── postman/                 # API collections (placeholder)
│   ├── frontend/                    # Angular app
│   │   └── src/app/                 # app.ts, routes (empty — no features yet)
│   └── database/
│       ├── SCRIPTS_POSTGRES.sql     # Active DDL — use this
│       ├── INSERTS.sql              # Seed data
│       └── SCRIPTS_MYSQL_LEGACY.sql # Historical — do not use
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

- Layered architecture: Controller → Service → Repository → JPA entity.
- All API routes under `/api/`.
- Classes: PascalCase. Methods/variables: camelCase. Packages: lowercase.
- Use env vars for DB credentials. Never hardcode.

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

- **Backend:** CRUD base implemented across 8 REST modules (product, category, order, etc.). Business logic and cross-cutting concerns not yet implemented.
- **Frontend:** Angular scaffold only. `app.routes.ts` is empty — no features implemented in UI.
- **Security:** Spring Security included as dependency but not configured. No auth/authorization active.
- **Testing:** Backend has `contextLoads` only. No functional test suite.
- **Data model gap:** `DetalleFactura` exists in SQL schema but has no JPA entity equivalent.

---

## Priority Roadmap

1. DTOs for request/response (stop exposing JPA entities directly).
2. Input validation and global error handling.
3. Domain gaps: `DetalleFactura` entity, product image field if in backlog.
4. Real security: password hashing, JWT authentication, role-based authorization.
5. Frontend routes and pages built on already-working backend endpoints.

---

## Gotchas

- `scripts/start-back.sh` and `scripts/start-front.sh` use relative `cd`. Run them from the repo root.
- Use `SCRIPTS_POSTGRES.sql` for DDL. `SCRIPTS_MYSQL_LEGACY.sql` is historical — ignore it.
- Java enums and PostgreSQL enums must match exactly in text value.
- `ddl-auto=update` is active — do not rely on implicit schema changes. Update `SCRIPTS_POSTGRES.sql` explicitly.
- Frontend starts without errors but has no functional routes. Do not assume UI features exist.

---

## AI Agent Instructions

- **Never hardcode** credentials or secrets. Always use `DB_USER` / `DB_PASSWORD` env vars.
- **Never modify** `docs/` unless explicitly requested.
- **Scope discipline:** Limit changes to the requested scope. Do not refactor unrelated code.
- **Schema changes:** Update `SCRIPTS_POSTGRES.sql` to match any model changes.
- **No emojis** in technical markdown documents.
- **No automatic commits.** Present changes for review first.
- **Documentation language:** Spanish for technical docs.
