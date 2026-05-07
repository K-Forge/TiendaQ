# Operaciones — TiendaQ

Guia de despliegue, CI/CD, ambientes, observabilidad y runbook de incidentes.

---

## Ambientes

| Ambiente | Backend | Frontend | Base de datos |
|----------|---------|----------|--------------|
| **Local (dev)** | `localhost:8080` | `localhost:4200` | PostgreSQL local |
| **Staging** | Render free tier | Render static site | PostgreSQL en Render |
| **Produccion** | Render free tier | Render static site | PostgreSQL en Render |

> Render free tier duerme instancias inactivas por 15min. Cold start puede tardar 30-60 segundos. Esperado para MVP academico.

---

## Variables de entorno requeridas

### Backend (Spring Boot)

```bash
# Base de datos
DB_URL=jdbc:postgresql://localhost:5432/tiendaq
DB_USER=postgres
DB_PASSWORD=

# JWT
JWT_SECRET=<secreto-minimo-256-bits-generado-con-openssl>
JWT_ACCESS_EXPIRY_MIN=15
JWT_REFRESH_EXPIRY_DAYS=7

# Wompi
WOMPI_PUBLIC_KEY=pub_test_xxx
WOMPI_PRIVATE_KEY=prv_test_xxx
WOMPI_EVENTS_SECRET=test_events_xxx

# Email
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=kforge.dev@gmail.com
MAIL_PASSWORD=<app-password-gmail>

# Spring
SPRING_PROFILES_ACTIVE=prod
```

**Generar JWT_SECRET:**
```bash
openssl rand -base64 32
```

### Frontend (Angular)

```bash
# En Render / CI
API_URL=https://tiendaq-api.onrender.com/api
```

---

## Configuracion de perfiles Spring

```yaml
# application.yml (base)
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true
    locations: classpath:db/migration

---
# application-dev.yml
spring:
  jpa:
    show-sql: true
  flyway:
    baseline-on-migrate: true

---
# application-prod.yml
server:
  port: ${PORT:8080}
spring:
  jpa:
    show-sql: false
logging:
  level:
    root: WARN
    com.tiendaq: INFO
```

---

## Docker

### Backend

```dockerfile
# app/backend/tiendaq/Dockerfile
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY target/tiendaq-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Frontend

```dockerfile
# app/frontend/Dockerfile
FROM node:22-alpine AS build
WORKDIR /app
COPY package.json pnpm-lock.yaml ./
RUN corepack enable && pnpm install --frozen-lockfile
COPY . .
RUN pnpm run build

FROM nginx:alpine
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
```

### docker-compose (desarrollo local)

```yaml
# docker-compose.yml
services:
  db:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: tiendaq
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: ""
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data

  api:
    build: app/backend/tiendaq
    depends_on: [db]
    environment:
      DB_URL: jdbc:postgresql://db:5432/tiendaq
      DB_USER: postgres
      DB_PASSWORD: ""
      JWT_SECRET: ${JWT_SECRET}
      WOMPI_PUBLIC_KEY: ${WOMPI_PUBLIC_KEY}
      WOMPI_PRIVATE_KEY: ${WOMPI_PRIVATE_KEY}
      WOMPI_EVENTS_SECRET: ${WOMPI_EVENTS_SECRET}
    ports:
      - "8080:8080"

volumes:
  pgdata:
```

---

## CI/CD (GitHub Actions)

```yaml
# .github/workflows/ci.yml
name: CI

on:
  push:
    branches: [develop, main]
  pull_request:
    branches: [develop]

jobs:
  backend:
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgres:15-alpine
        env:
          POSTGRES_DB: tiendaq_test
          POSTGRES_USER: postgres
          POSTGRES_PASSWORD: postgres
        ports: ["5432:5432"]
        options: --health-cmd pg_isready --health-interval 10s --health-timeout 5s --health-retries 5

    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { java-version: '25', distribution: 'temurin' }
      - name: Build y test backend
        working-directory: app/backend/tiendaq
        run: ./mvnw verify
        env:
          DB_URL: jdbc:postgresql://localhost:5432/tiendaq_test
          DB_USER: postgres
          DB_PASSWORD: postgres
          JWT_SECRET: test-secret-256-bits-para-ci-pipeline
          SPRING_PROFILES_ACTIVE: test

  frontend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with: { node-version: '22' }
      - name: Instalar dependencias
        working-directory: app/frontend
        run: corepack enable && pnpm install --frozen-lockfile
      - name: Build frontend
        working-directory: app/frontend
        run: pnpm run build
      - name: Tests unitarios
        working-directory: app/frontend
        run: pnpm run test
```

---

## Observabilidad

### Health check

```
GET /actuator/health
Response: { "status": "UP", "components": { "db": { "status": "UP" } } }
```

### Metricas

```
GET /actuator/metrics
GET /actuator/metrics/http.server.requests
```

### Logs JSON (Logback)

```xml
<!-- src/main/resources/logback-spring.xml -->
<springProfile name="prod">
  <appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
    <encoder class="net.logstash.logback.encoder.LogstashEncoder">
      <customFields>{"service":"tiendaq-api","environment":"prod"}</customFields>
    </encoder>
  </appender>
  <root level="INFO">
    <appender-ref ref="JSON"/>
  </root>
</springProfile>
```

Formato de log:
```json
{
  "timestamp": "2026-05-06T14:30:00.000Z",
  "level": "INFO",
  "service": "tiendaq-api",
  "logger": "com.tiendaq.pagos.application.ProcesarWebhookUseCase",
  "message": "Webhook procesado: APPROVED transaction_id=abc123 factura_id=42"
}
```

---

## Runbook

### Backend no inicia

1. Verificar logs: `docker logs tiendaq-api` o panel Render.
2. Confirmar que PostgreSQL esta accesible: `psql $DB_URL`.
3. Verificar que Flyway no tiene migracion fallida: buscar `FlywayException` en logs.
4. Verificar variables de entorno: `JWT_SECRET`, `DB_URL`, `DB_PASSWORD`.

### Flyway falla al arrancar

```
FlywayException: Validate failed: Migration checksum mismatch for migration version 3
```
**Causa:** alguien modifico un archivo de migracion ya aplicado.
**Solucion:** no modificar migraciones aplicadas. Crear nueva migracion que corrija el estado.

### Wompi webhook no llega

1. Verificar que el endpoint `/api/pagos/webhook` es accesible desde internet (no localhost).
2. Verificar `WOMPI_EVENTS_SECRET` coincide con el configurado en el panel de Wompi.
3. Buscar en `processed_webhook` si el `transaction_id` ya fue procesado.
4. Revisar logs del `PagoController` para ver si el request llego con firma invalida.

### 409 Conflict frecuentes en checkout

Causa: alta contention en `StockLevel` o `Carrito` (optimistic locking).
Solucion: el cliente debe reintentar. Si ocurre sistemicamente, revisar si hay un loop en el frontend.

### Reservas de stock no se liberan

Sintoma: stock disponible decae aunque no haya compras.
Causa: el job `StockExpirationJob` no esta corriendo.
Verificar: `GET /actuator/metrics/scheduled.tasks`. Revisar logs del job.

---

## Despliegue en Render (manual)

1. Conectar repositorio a Render.
2. Crear servicio Web (Backend): Build Command `./mvnw package -DskipTests`, Start Command `java -jar target/tiendaq-*.jar`.
3. Crear Static Site (Frontend): Build Command `pnpm install && pnpm run build`, Publish Directory `dist/frontend/browser`.
4. Crear base de datos PostgreSQL en Render (plan free).
5. Configurar variables de entorno en el panel de Render.
6. Hacer deploy. Verificar `GET /actuator/health`.
