# ADR-0005 — Flyway para migraciones (eliminar ddl-auto=update)

**Estado:** Aceptada
**Fecha:** Mayo 2026
**Autores:** Brian Vargas (PO)

---

## Contexto

El proyecto usa `spring.jpa.hibernate.ddl-auto=update`. Este modo tiene problemas criticos en produccion:

- Hibernate puede agregar columnas y tablas, pero **nunca elimina columnas ni cambia tipos**. Esquemas divergen silenciosamente.
- Orden de creacion de tablas depende de Hibernate, no del desarrollador. Restricciones FK pueden fallar.
- Imposible auditar cambios de esquema. No hay historial de quien cambio que y cuando.
- Incompatible con `NUMERIC(15,2)`: Hibernate no convertiria columnas `DOUBLE PRECISION` existentes.
- Inconsistencia entre ambientes: dev, staging y prod pueden tener esquemas distintos sin que nadie lo note.

---

## Decision

Migrar a **Flyway** para gestion de esquema. Eliminar `ddl-auto=update`.

**Configuracion:**

```yaml
# application.yml
spring:
  jpa:
    hibernate:
      ddl-auto: validate        # Solo valida que esquema coincida con entidades
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true   # Para BD existente sin historial Flyway
```

**Convencion de nombres:**

```
src/main/resources/db/migration/
├── V1__initial_schema.sql          # Esquema completo inicial (exportado de SQL actual)
├── V2__add_producto_imagen.sql     # Agregar campo imagen a producto
├── V3__bigdecimal_migration.sql    # double → NUMERIC(15,2) en todas las tablas
├── V4__add_refresh_token_table.sql
├── V5__add_revoked_token_table.sql
└── V6__add_stock_reservation.sql
```

Reglas de numeracion:
- `V{n}__` prefijo con doble guion bajo.
- Descripcion en snake_case, en espanol o ingles (consistente dentro de la migracion).
- Una vez aplicada, una migracion **nunca se modifica**. Si hay error, crear migracion nueva que lo corrija.

**Eliminar `SCRIPTS_POSTGRES.sql` como source of truth.** Pasa a ser documentacion de referencia historica; el esquema real lo controla Flyway.

---

## Consecuencias

**Positivas:**
- Historial completo de cambios de esquema en git (mismo PR que el codigo que los usa).
- Ambientes identicos: `flyway:migrate` en staging y prod aplica exactamente las mismas migraciones.
- `ddl-auto=validate` detecta divergencias entre entidades JPA y esquema real al arranque.
- Compatible con Testcontainers: cada test corre contra un esquema limpio y migrado.

**Negativas:**
- Sprint de migracion inicial requerido: exportar esquema actual a `V1__initial_schema.sql`.
- Los desarrolladores deben aprender la convencion Flyway (no solo editar el SQL y reiniciar).
- Migraciones erroneas en produccion son dificiles de revertir (Flyway no hace rollback automatico para SQL DDL).

**Deuda tecnica aceptada:** El archivo `app/database/SCRIPTS_POSTGRES.sql` se mantiene como documentacion historica pero deja de ser authoritative. EN-058 incluye la creacion de `V1__initial_schema.sql`.

---

## Alternativas consideradas

| Alternativa | Razon de rechazo |
|-------------|-----------------|
| `ddl-auto=update` (status quo) | No auditable, no reproducible, incompatible con cambios de tipo de columna |
| `ddl-auto=create-drop` | Solo para tests. Destruye datos en cada arranque |
| Liquibase | Mas potente (rollback, precondiciones), pero mas complejo. Overkill para este proyecto |
| Scripts SQL manuales sin herramienta | Sin control de version aplicado, facil de olvidar en un ambiente |
