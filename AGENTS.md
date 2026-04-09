# AGENTS.md - Guia operativa para agentes IA en TiendaQ

## Proposito

Este documento define el contexto minimo y vigente para contribuir en este repositorio sin inventar supuestos.

- Describe el estado real del codigo (no el deseado).
- Prioriza acciones practicas para avances seguros.
- Evita que un agente toque zonas fuera de alcance cuando la tarea no lo pide.

## Stack actual (verificado en el repo)

### Backend

- Java 25 (`app/backend/tiendaq/pom.xml`)
- Spring Boot 4.0.0 (`spring-boot-starter-parent`)
- Spring Web MVC, Spring Data JPA, Spring Security, Spring Validation, Actuator
- Maven Wrapper (`./mvnw`)

### Frontend

- Angular 21.2.0 (`@angular/core`)
- Angular CLI / build 21.2.2
- TypeScript ~5.9.2
- SCSS
- Bun 1.3.8 como runtime de scripts
- pnpm como package manager para instalacion de dependencias

### Base de datos

- PostgreSQL 15+
- JDBC local: `jdbc:postgresql://localhost:5432/tiendaq`
- Usuario/password por entorno: `DB_USER`, `DB_PASSWORD`
- Hibernate `ddl-auto=update`

## Estructura del proyecto (practica)

```text
TiendaQ/
  app/
    backend/
      tiendaq/                 # API Spring Boot
        src/main/java/com/tiendaq/api/
          controller/          # 8 controladores REST
          service/             # 8 servicios
          repository/          # 8 repositorios JPA
          model/               # 8 entidades + enums
        src/main/resources/application.properties
      postman/                 # placeholder
    frontend/                  # app Angular (scaffold base)
      src/app/                 # app.ts, rutas vacias
    database/                  # scripts SQL (Postgres + legacy MySQL)
  scripts/
    start-back.sh
    start-front.sh
  README.md
  CONTRIBUTING.md
  AGENTS.md
```

## Comandos esenciales

### Backend

```bash
./scripts/start-back.sh
```

o manual:

```bash
cd app/backend/tiendaq
./mvnw spring-boot:run
```

### Frontend

```bash
./scripts/start-front.sh
```

o manual:

```bash
cd app/frontend
pnpm install
bun start
```

### Setup de herramientas (una sola vez)

```bash
corepack enable && corepack prepare pnpm@latest --activate
curl -fsSL https://bun.sh/install | bash
```

### Base de datos (inicializacion local)

```bash
psql -U postgres -c "CREATE DATABASE \"tiendaq\";"
psql -U postgres -d "tiendaq" -f app/database/SCRIPTS_POSTGRES.sql
psql -U postgres -d "tiendaq" -f app/database/INSERTS.sql
```

## Convenciones clave

- Git: flujo tipo Git Flow (`main`, `develop`, `feature/*`, `chore/*`, `bugfix/*`, `test/*`, `hotfix/*`, `release/*`).
- Commits: Conventional Commits en ingles, minusculas, sin punto final, sin scope.
- Backend Java: clases PascalCase, metodos/variables camelCase, paquetes en minuscula.
- Frontend: Prettier (`printWidth=100`, comilla simple) y `.editorconfig` con 2 espacios.
- Angular: componentes standalone, SCSS, generacion de tests desactivada por defecto en schematics.

## Seguridad y limites operativos

- Nunca hardcodear secretos ni credenciales.
- Mantener uso de variables de entorno para DB (`DB_USER`, `DB_PASSWORD`).
- No exponer datos sensibles en respuestas o docs de PR.
- No modificar `docs/` salvo solicitud explicita.
- No cambiar arquitectura global ni convenciones de ramas/commits sin acuerdo del equipo.
- Si una tarea pide un alcance puntual, limitar cambios a ese alcance.

## Estado actual del proyecto

- Backend: hay CRUD base en 8 modulos REST; aun falta logica de negocio transversal.
- Frontend: scaffold inicial; `app.routes.ts` esta vacio y no hay features implementadas.
- Testing: backend solo tiene `contextLoads`; no hay suite funcional consolidada.
- Seguridad: dependencias incluidas, pero no hay configuracion activa de auth/autorizacion.
- Modelo: existe tabla `DetalleFactura` en SQL, pero no entidad JPA equivalente.

## Prioridades practicas para contribuir

1. Introducir DTOs de request/response para dejar de exponer entidades JPA directamente.
2. Implementar validacion de entrada y manejo global de errores.
3. Completar gaps de dominio (`DetalleFactura`, campo de imagen de producto si aplica al backlog).
4. Configurar seguridad real (hash de contrasena, autenticacion, autorizacion por roles).
5. Construir rutas/paginas frontend sobre casos reales ya soportados por backend.

## Gotchas vigentes

- `scripts/start-back.sh` y `scripts/start-front.sh` hacen `cd` relativo: ejecutarlos desde raiz del repo.
- `SCRIPTS_MYSQL_LEGACY.sql` es historico; para desarrollo actual usar `SCRIPTS_POSTGRES.sql`.
- Enums de Java y enums de PostgreSQL deben coincidir exactamente en valor textual.
- `application.properties` usa `ddl-auto=update`; cuidado al depender de cambios implicitos del esquema.
- El frontend arranca, pero sin rutas funcionales (`routes: []`), asi que no asumir features UI existentes.
